package com.bah.attune.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bah.attune.dao.BaseDao;
import com.bah.attune.data.NameValuePair;

@Service
public class ValidationService {

	// the required tabs
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ValidationService.class);
	private static final List<String> requiredTabs = Arrays.asList("Metadata",
			"Dashboard", "Entity", "Relationship");

	private static final String ERROR_WORKBOOK_MISSING_TABS = "Workbook does not contain the required tab: %1.";
	private static final String ERROR_REQUIRED_TAB_EMPTY = "Required tab %1 can not be empty.";
	private static final String ERROR_ENTITY_TAB_NOT_FOUND = "Required tab for entity %1 in %2 tab not found.";
	private static final String ERROR_ENTITY_NOT_FOUND = "%1 %2 from %3 tab at row %4 not found in Entity tab.";
	private static final String ERROR_ENTITY_IS_ROOT_NOT_VALID = "Cell at row %1, column %2 in sheet %3 is not a valid value. The value must be 'Yes', 'No', or left blank.";
	private static final String ERROR_VALUE_NOT_UNIQUE = "%1 %2 from %3 tab at row %4 is not unique.";
	private static final String ERROR_COLUMN_NOT_FOUND = "%1 value %2 from %3 tab at row %4 is not a column in corresponding Entity tab.";
	private static final String ERROR_CANNOT_BE_NULL = "Cell at row %1, column %2 in sheet %3 cannot be null.";
	private static final String ERROR_EXPECTED_NUMBER = "Cell at row %1, column %2 in sheet %3 expected to be a number.";
	private static final String ERROR_EXPECTED_CURRENCY = "Cell at row %1, column %2 in sheet %3 expected to be currency.";
	private static final String ERROR_EXPECTED_DATE = "Cell at row %1, column %2 in sheet %3 expected to be a date value (Mm/dd/yyyy).";
	private static final String ERROR_RELATIONSHIP_NOT_FOUND = "%1 %2 at row %3 in Relationship tab not found in corresponding %4 tab.";
	private static final String ERROR_RELATIONSHIP_NOT_VALID = "Relationship at row %1 in Relationship tab is not a valid metadata relationship.";
	private static final String ERROR_RELATIONSHIP_REQUIRED_NOT_VALID = "Cell at row %1, column %2 in sheet %3 is not a valid value. The value must be 'Yes', 'No', or left blank.";
	private static final String ERROR_MILESTONE_ENDS_WITH = "%1 column in sheet %2 needs to end with %3 to indicate %4";
	private static final String ERROR_CAPABILITY_RELATIONSHIP = "%1 %2 in Capability Analysis tab found at row %3, column %4 in Metadata tab. "
			+ "%1 of Capability Analysis is not allowed any relationships.";
	private static final String ERROR_CAPABILITY_VALUE = "%1 %2 in tab %3 at row %4 not found in corresponding %1 tab.";

	private List<String> workbookTabsList;
	private List<String> errorMessages;
	private Map<String, List<String>> entitiesMap;
	private Map<String, List<String>> entitiesColumnsMap;
	private Map<String, List<String>> capabilityAnalysisMap;
	private Map<NameValuePair, String> metadataMap;

	private String customize(String base, String value1, String value2,
			String value3, String value4) {
		return base.replace("%1", value1).replace("%2", value2)
				.replace("%3", value3).replace("%4", value4);
	}

	public void validateEntity(Workbook wb) {
		Row row;
		Sheet entitiesSheet = wb.getSheet("Entity");
		entitiesMap = new HashMap<String, List<String>>();

		int i = 1;
		while (entitiesSheet.getRow(i) != null) {
			// make sure no duplicate entries, and add to map
			row = entitiesSheet.getRow(i);
			if (!entitiesMap.containsKey(row.getCell(0).getStringCellValue())) {
				entitiesMap.put(row.getCell(0).getStringCellValue(), null);
			} else {
				errorMessages.add(customize(ERROR_VALUE_NOT_UNIQUE, "Entity",
						row.getCell(0).getStringCellValue(), "Entity",
						String.valueOf(i + 1)));
			}

			// Check if the 'isRoot' column contains a valid value.
			if (row.getCell(1) != null
					&& row.getCell(1).getCellType() != Cell.CELL_TYPE_BLANK) {
				if (row.getCell(1).getCellType() != Cell.CELL_TYPE_STRING
						|| (!StringUtils.equalsIgnoreCase(row.getCell(1)
								.getStringCellValue(), "Yes") && !StringUtils
								.equalsIgnoreCase(row.getCell(1)
										.getStringCellValue(), "No"))) {
					errorMessages.add(customize(ERROR_ENTITY_IS_ROOT_NOT_VALID,
							String.valueOf(i + 1), "2", "Entity", ""));
				}
			}

			i++;
		}
	}

	public void validateMetadata(Workbook wb) {
		Row row;
		Sheet sheet = wb.getSheet("Metadata");

		int i = 1;
		while (sheet.getRow(i) != null) {
			row = sheet.getRow(i);
			// check if each Entity in Metadata Model is listed in Entities tab
			checkMetadatEntity(sheet, row, i);

			// Check if the 'Required' column contains a valid value.
			if (row.getCell(3) != null
					&& row.getCell(3).getCellType() != Cell.CELL_TYPE_BLANK) {
				if (row.getCell(3).getCellType() != Cell.CELL_TYPE_STRING
						|| (!StringUtils.equalsIgnoreCase(row.getCell(3)
								.getStringCellValue(), "Yes") && !StringUtils
								.equalsIgnoreCase(row.getCell(3)
										.getStringCellValue(), "No"))) {
					errorMessages.add(customize(
							ERROR_RELATIONSHIP_REQUIRED_NOT_VALID,
							String.valueOf(i + 1), "4", "Metadata", ""));
				}
			}

			i++;
		}

		List<String> sheetNameList = new ArrayList<String>();

		for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++)
			sheetNameList.add(wb.getSheetName(sheetIndex));

		// remove required tabs from sheet name list
		for (String requiredTab : requiredTabs) {
			if (!("TimelineEvent").equals(requiredTab)
					&& !("TimelineBudget").equals(requiredTab))
				sheetNameList.remove(requiredTab);
		}

		// check for the tab for each corresponding Entity listed in Entities
		// tab
		for (String entity : entitiesMap.keySet()) {
			if (!sheetNameList.contains(entity))
				errorMessages.add(customize(ERROR_ENTITY_TAB_NOT_FOUND, entity,
						"Entity", "", ""));
		}
	}

	private void checkMetadatEntity(Sheet sheet, Row row, int i) {
		String startEntity = row.getCell(0).getStringCellValue();
		String relationship = row.getCell(1).getStringCellValue();
		String endEntity = row.getCell(2).getStringCellValue();

		if (!entitiesMap.containsKey(startEntity))
			errorMessages.add(customize(ERROR_ENTITY_NOT_FOUND, sheet.getRow(0)
					.getCell(0).getStringCellValue(), row.getCell(0)
					.getStringCellValue(), "Metadata", String.valueOf(i + 1)));

		if (!entitiesMap.containsKey(endEntity))
			errorMessages.add(customize(ERROR_ENTITY_NOT_FOUND, sheet.getRow(0)
					.getCell(2).getStringCellValue(), row.getCell(2)
					.getStringCellValue(), "Metadata", String.valueOf(i + 1)));

		NameValuePair entityPair = new NameValuePair();
		entityPair.setName(startEntity);
		entityPair.setValue(endEntity);

		metadataMap.put(entityPair, relationship);
	}

	public void validateDashboard(Workbook wb) {
		Row row;
		Sheet sheet = wb.getSheet("Dashboard");
		List<String> elementList = new ArrayList<String>();

		int i = 1;
		while (sheet.getRow(i) != null) {
			row = sheet.getRow(i);

			// check if every value in Element is unique
			if (!elementList.contains(row.getCell(0).getStringCellValue())) {
				elementList.add(row.getCell(0).getStringCellValue());
			} else {
				errorMessages.add(customize(ERROR_VALUE_NOT_UNIQUE, sheet
						.getRow(0).getCell(0).getStringCellValue(), row
						.getCell(0).getStringCellValue(), "Dashboard", String
						.valueOf(i + 1)));
			}

			// check if value in Entity is in Entities list
			if (!entitiesMap.containsKey(row.getCell(1).getStringCellValue())) {
				errorMessages.add(customize(ERROR_ENTITY_NOT_FOUND, sheet
						.getRow(0).getCell(1).getStringCellValue(), row
						.getCell(1).getStringCellValue(), "Dashboard", String
						.valueOf(i + 1)));
			} else {
				// check if value in Group By is a column in the corresponding
				// Entity tab
				if (entitiesColumnsMap.get(row.getCell(1).getStringCellValue()) != null
						&& !entitiesColumnsMap.get(
								row.getCell(1).getStringCellValue()).contains(
								row.getCell(2).getStringCellValue())) {
					errorMessages.add(customize(ERROR_COLUMN_NOT_FOUND, sheet
							.getRow(0).getCell(2).getStringCellValue(), row
							.getCell(2).getStringCellValue(), "Dashboard",
							String.valueOf(i + 1)));
				}

				// check if value in Display List are columns in the
				// corresponding Entity tab
				validateDisplayListValue(sheet, row, i);
			}

			i++;
		}
	}

	private void validateDisplayListValue(Sheet sheet, Row row, int i) {
		if (!("*").equals(row.getCell(3).getStringCellValue())) {
			String[] displayList = row.getCell(3).getStringCellValue()
					.split("\\s*,\\s*");
			for (String s : displayList) {
				if (!entitiesColumnsMap
						.get(row.getCell(1).getStringCellValue()).contains(s)) {
					errorMessages.add(customize(ERROR_COLUMN_NOT_FOUND, sheet
							.getRow(0).getCell(3).getStringCellValue(), s,
							"Dashboard", String.valueOf(i + 1)));
				}
			}
		}
	}

	public void validateEntityTabs(Workbook wb) {
		entitiesColumnsMap = new HashMap<String, List<String>>();

		for (String sheetName : entitiesMap.keySet()) {
			if (workbookTabsList.contains(sheetName)) {
				validateSheet(wb, sheetName);
			}
		}
	}

	public void validateSheet(Workbook wb, String sheetName) {
		Sheet sheet = wb.getSheet(sheetName);
		int i = 0;
		List<String> entityColumnsList = new ArrayList<String>();
		while (sheet.getRow(0).getCell(i) != null) {
			String columnName = sheet.getRow(0).getCell(i).getStringCellValue();

			// check columns designated for Milestones to end with
			// correct character
			// and remove Milestone tag so column name can be checked in
			// dashboard tab
			if (columnName.contains("(Milestone Start Date)")) {
				if (!columnName.endsWith("/")) {
					errorMessages.add(customize(ERROR_MILESTONE_ENDS_WITH,
							columnName, sheetName, "/",
							"(Milestone Start Date)"));
				}
				columnName = columnName.replace(" (Milestone Start Date)", "");
			} else if (columnName.contains("(Milestone End Date)")) {
				if (!columnName.endsWith("/"))
					errorMessages
							.add(customize(ERROR_MILESTONE_ENDS_WITH,
									columnName, sheetName, "/",
									"(Milestone End Date)"));
				columnName = columnName.replace(" (Milestone End Date)", "");
			} else if (columnName.contains("(Milestone FY)")) {
				if (!columnName.endsWith("#"))
					errorMessages.add(customize(ERROR_MILESTONE_ENDS_WITH,
							columnName, sheetName, "#", "(Milestone FY)"));
				columnName = columnName.replace(" (Milestone FY)", "");
			}

			columnName = columnName.replaceAll("[^a-zA-Z0-9\\s]", "");

			entityColumnsList.add(columnName);

			// put unique Names into entities map, for the first column
			// ( '!name*' column )
			if (i == 0) {
				mapUniqueNames(sheet, sheetName);
			}

			// check columns that are in capability analysis, see if each value
			// is in the corresponding filterEntity tab.
			for (String filterEntity : capabilityAnalysisMap.keySet()) {
				if (sheet.getRow(0).getCell(i).getStringCellValue()
						.contains(filterEntity)) {
					int j = 1;
					while (sheet.getRow(j) != null) {
						for (String value : sheet.getRow(j).getCell(i)
								.getStringCellValue().split("\\|")) {
							if (!capabilityAnalysisMap.get(filterEntity)
									.contains(value)) {
								errorMessages
										.add(customize(ERROR_CAPABILITY_VALUE,
												filterEntity, value, sheetName,
												String.valueOf(j + 1)));
							}
						}

						j++;
					}
				}
			}

			// validate each of the column types for corresponding data
			validateColumnTypes(sheet, columnName, i);

			i++;
		}

		entitiesColumnsMap.put(sheetName, entityColumnsList);
	}

	private void validateColumnTypes(Sheet sheet, String sheetName, int i) {
		if (sheet.getRow(0).getCell(i).getStringCellValue().endsWith("*")) {
			validateAnythingColumn(sheet, sheetName, i);
		} else if (sheet.getRow(0).getCell(i).getStringCellValue()
				.endsWith("#")) {
			validateNumericColumn(sheet, sheetName, i);
		} else if (sheet.getRow(0).getCell(i).getStringCellValue()
				.endsWith("$")) {
			validateCurrencyColumn(sheet, sheetName, i);
		} else if (sheet.getRow(0).getCell(i).getStringCellValue()
				.endsWith("/")) {
			validateDateColumn(sheet, sheetName, i);
		}
	}

	private void mapUniqueNames(Sheet sheet, String sheetName) {
		List<String> entityNamesList = new ArrayList<String>();
		int j = 1;
		while (sheet.getRow(j) != null && sheet.getRow(j).getCell(0) != null) {
			sheet.getRow(j).getCell(0).setCellType(1);
			if (StringUtils.isNotEmpty(sheet.getRow(j).getCell(0)
					.getStringCellValue())) {
				if (!entityNamesList.contains(sheet.getRow(j).getCell(0)
						.getStringCellValue()))
					entityNamesList.add(sheet.getRow(j).getCell(0)
							.getStringCellValue());
				else
					errorMessages.add(customize(ERROR_VALUE_NOT_UNIQUE, BaseDao.NAME,
							sheet.getRow(j).getCell(0).getStringCellValue(),
							sheetName, String.valueOf(j + 1)));
			}

			j++;
		}

		entitiesMap.put(sheetName, entityNamesList);
	}

	private void validateDateColumn(Sheet sheet, String sheetName, int i) {
		int j = 1;
		while (sheet.getRow(j) != null) {
			if (sheet.getRow(j).getCell(i) != null
					&& sheet.getRow(j).getCell(i).getCellType() != Cell.CELL_TYPE_BLANK) {
				formatCellToDate(sheet, sheetName, i, j);
			} else {
				if (sheet.getRow(0).getCell(i).getStringCellValue()
						.startsWith("!"))
					errorMessages.add(customize(ERROR_CANNOT_BE_NULL,
							String.valueOf(j + 1), String.valueOf(i + 1),
							sheetName, ""));
			}

			j++;
		}
	}

	private void formatCellToDate(Sheet sheet, String sheetName, int i, int j) {
		DateFormat df = new SimpleDateFormat("M/dd/yyyy");
		if (sheet.getRow(j).getCell(i).getCellType() == Cell.CELL_TYPE_NUMERIC) {
			try {
				df.format(sheet.getRow(j).getCell(i).getDateCellValue());
			} catch (Exception e) {
				errorMessages.add(customize(ERROR_EXPECTED_DATE,
						String.valueOf(j + 1), String.valueOf(i + 1),
						sheetName, ""));
				errorMessages.add(e.getMessage());
				LOGGER.error(
						"Error occured in Validation Service formatting date of Numeric type",
						e);
			}
		} else {
			errorMessages
					.add(customize(ERROR_EXPECTED_DATE, String.valueOf(j + 1),
							String.valueOf(i + 1), sheetName, ""));
		}
	}

	private void validateCurrencyColumn(Sheet sheet, String sheetName, int i) {
		int j = 1;
		while (sheet.getRow(j) != null) {
			if (sheet.getRow(j).getCell(i) != null
					&& sheet.getRow(j).getCell(i).getCellType() != Cell.CELL_TYPE_BLANK) {
				formatCellToCurrency(sheet, sheetName, i, j);
			} else {
				if (sheet.getRow(0).getCell(i).getStringCellValue()
						.startsWith("!"))
					errorMessages.add(customize(ERROR_CANNOT_BE_NULL,
							String.valueOf(j + 1), String.valueOf(i + 1),
							sheetName, ""));
			}

			j++;
		}
	}

	private void formatCellToCurrency(Sheet sheet, String sheetName, int i,
			int j) {
		NumberFormat nf = NumberFormat.getCurrencyInstance();
		if (sheet.getRow(j).getCell(i).getCellType() == Cell.CELL_TYPE_STRING) {
			try {
				nf.format(new BigDecimal(sheet.getRow(j).getCell(i)
						.getStringCellValue()));
			} catch (Exception e) {
				errorMessages.add(customize(ERROR_EXPECTED_CURRENCY,
						String.valueOf(j + 1), String.valueOf(i + 1),
						sheetName, ""));
				errorMessages.add(e.getMessage());
				LOGGER.error(
						"Error occured in Validation Service formatting currency of String type",
						e);

			}
		} else if (sheet.getRow(j).getCell(i).getCellType() == Cell.CELL_TYPE_NUMERIC) {
			try {
				nf.format(BigDecimal.valueOf(sheet.getRow(j).getCell(i)
						.getNumericCellValue()));
			} catch (Exception e) {
				errorMessages.add(customize(ERROR_EXPECTED_CURRENCY,
						String.valueOf(j + 1), String.valueOf(i + 1),
						sheetName, ""));
				errorMessages.add(e.getMessage());
				LOGGER.error(
						"Error occured in Validation Service formatting currency of Numeric type",
						e);
			}
		} else {
			errorMessages
					.add(customize(ERROR_EXPECTED_CURRENCY,
							String.valueOf(j + 1), String.valueOf(i + 1),
							sheetName, ""));
		}
	}

	private void validateNumericColumn(Sheet sheet, String sheetName, int i) {
		int j = 1;
		while (sheet.getRow(j) != null) {
			if (sheet.getRow(j).getCell(i) == null
					|| sheet.getRow(j).getCell(i).getCellType() == Cell.CELL_TYPE_BLANK) {
				if (sheet.getRow(0).getCell(i).getStringCellValue()
						.startsWith("!"))
					errorMessages.add(customize(ERROR_CANNOT_BE_NULL,
							String.valueOf(j + 1), String.valueOf(i + 1),
							sheetName, ""));
			} else {
				if (sheet.getRow(j).getCell(i).getCellType() != Cell.CELL_TYPE_NUMERIC)
					errorMessages.add(customize(ERROR_EXPECTED_NUMBER,
							String.valueOf(j + 1), String.valueOf(i + 1),
							sheetName, ""));
			}

			j++;
		}
	}

	private void validateAnythingColumn(Sheet sheet, String sheetName, int i) {
		int j = 1;
		while (sheet.getRow(j) != null) {
			if ((sheet.getRow(j).getCell(i) == null || sheet.getRow(j)
					.getCell(i).getCellType() == Cell.CELL_TYPE_BLANK)
					&& sheet.getRow(0).getCell(i).getStringCellValue()
							.startsWith("!")) {
				errorMessages.add(customize(ERROR_CANNOT_BE_NULL,
						String.valueOf(j + 1), String.valueOf(i + 1),
						sheetName, ""));
			}

			j++;
		}
	}

	public void validateRelationship(Workbook wb) {
		Sheet sheet = wb.getSheet("Relationship");

		int i = 1;
		while (sheet.getRow(i) != null && sheet.getRow(i).getCell(0) != null) {
			// validate that startEntityType exists within Entity tab,
			// and startEntity exists within corresponding startEntityType tab
			String startEntityType = sheet.getRow(i).getCell(0)
					.getStringCellValue();

			if (!entitiesMap.containsKey(startEntityType)) {
				errorMessages.add(customize(ERROR_ENTITY_NOT_FOUND, sheet
						.getRow(0).getCell(0).getStringCellValue(), sheet
						.getRow(i).getCell(0).getStringCellValue(),
						"Relationship", String.valueOf(i + 1)));
			} else {
				if (entitiesMap.get(startEntityType) != null
						&& !entitiesMap.get(startEntityType)
								.contains(
										sheet.getRow(i).getCell(1)
												.getStringCellValue())) {
					errorMessages.add(customize(ERROR_RELATIONSHIP_NOT_FOUND,
							sheet.getRow(0).getCell(1).getStringCellValue(),
							sheet.getRow(i).getCell(1).getStringCellValue(),
							String.valueOf(i + 1), sheet.getRow(0).getCell(0)
									.getStringCellValue()));
				}
			}

			// validate that endEntityType exists within Entity tab,
			// and endEntity exists within corresponding endEntityType tab
			String endEntityType = sheet.getRow(i).getCell(3)
					.getStringCellValue();
			if (!entitiesMap.containsKey(endEntityType)) {
				errorMessages.add(customize(ERROR_ENTITY_NOT_FOUND, sheet
						.getRow(0).getCell(3).getStringCellValue(), sheet
						.getRow(i).getCell(3).getStringCellValue(),
						"Relationship", String.valueOf(i + 1)));
			} else {
				if (entitiesMap.get(endEntityType) != null
						&& !entitiesMap.get(endEntityType)
								.contains(
										sheet.getRow(i).getCell(4)
												.getStringCellValue())) {
					errorMessages.add(customize(ERROR_RELATIONSHIP_NOT_FOUND,
							String.valueOf(i + 1), "", "", ""));
				}
			}

			// validate that the relationship is defined in the metadata model
			String relationship = sheet.getRow(i).getCell(2)
					.getStringCellValue();

			NameValuePair entityPair = new NameValuePair();
			entityPair.setName(startEntityType);
			entityPair.setValue(endEntityType);

			String metadataRelationship = metadataMap.get(entityPair);

			if (StringUtils.isEmpty(metadataRelationship)
					|| !StringUtils.equals(metadataRelationship, relationship)) {
				errorMessages.add(customize(ERROR_RELATIONSHIP_NOT_VALID,
						String.valueOf(i + 1), "", "", ""));
			}

			i++;
		}
	}

	public void validateTimeline(Workbook wb) {
		Sheet sheet = wb.getSheet("Timeline");

		if (sheet == null)
			return;

		// check if each Entity in Timeline is listed in Entities tab
		if (!entitiesMap.containsKey(sheet.getRow(0).getCell(1)
				.getStringCellValue()))
			errorMessages.add(customize(ERROR_ENTITY_NOT_FOUND, sheet.getRow(0)
					.getCell(0).getStringCellValue(), sheet.getRow(0)
					.getCell(1).getStringCellValue(), "Timeline",
					String.valueOf(1)));

		// Check if attribute for corresponding entity exists.
		int i = 1;
		while (sheet.getRow(i) != null) {
			if (entitiesColumnsMap.containsKey(sheet.getRow(0).getCell(1)
					.getStringCellValue())) {
				if (!entitiesColumnsMap
						.get(sheet.getRow(0).getCell(1).getStringCellValue())
						.contains(
								sheet.getRow(i).getCell(1).getStringCellValue())) {
					errorMessages.add(customize(ERROR_COLUMN_NOT_FOUND, sheet
							.getRow(i).getCell(0).getStringCellValue(), sheet
							.getRow(i).getCell(1).getStringCellValue(),
							"Timeline", String.valueOf(i + 1)));
				}
			} else {
				errorMessages.add(customize(ERROR_ENTITY_TAB_NOT_FOUND, sheet
						.getRow(0).getCell(1).getStringCellValue(), "Timeline",
						"", ""));
			}

			i++;
		}
	}

	public void validateCapabilityAnalysis(Workbook wb) {
		Sheet sheet = wb.getSheet("Capability Analysis");
		Sheet relationshipSheet = wb.getSheet("Metadata");
		capabilityAnalysisMap = new HashMap<String, List<String>>();

		if (sheet != null) {
			int i = 1;
			while (sheet.getRow(i) != null) {
				// check that filterEntity is an Entity
				if (!entitiesMap.keySet().contains(
						sheet.getRow(i).getCell(0).getStringCellValue())) {
					errorMessages.add(customize(ERROR_ENTITY_NOT_FOUND, sheet
							.getRow(0).getCell(0).getStringCellValue(), sheet
							.getRow(i).getCell(0).getStringCellValue(),
							"Capability Analysis", String.valueOf(i + 1)));
				} else // store filterEntity and filterEntityValues to be
						// checked later
				{
					Sheet filterEntitySheet = wb.getSheet(sheet.getRow(i)
							.getCell(0).getStringCellValue());
					List<String> filterEntityValues = new ArrayList<String>();

					int k = 1;
					while (filterEntitySheet.getRow(k) != null) {
						filterEntityValues.add(filterEntitySheet.getRow(k)
								.getCell(0).getStringCellValue());

						k++;
					}

					capabilityAnalysisMap.put(sheet.getRow(i).getCell(0)
							.getStringCellValue(), filterEntityValues);
				}

				// check that resultTitle is not null
				if (sheet.getRow(i).getCell(1) == null
						|| sheet.getRow(i).getCell(1).getCellType() == Cell.CELL_TYPE_BLANK) {
					errorMessages.add(customize(ERROR_CANNOT_BE_NULL,
							String.valueOf(i + 1), "2", "Capability Analysis",
							""));
				}

				// make sure no incoming and outgoing relationships to
				// filterEntity
				int j = 1;
				while (relationshipSheet.getRow(j) != null) {
					if (relationshipSheet
							.getRow(j)
							.getCell(0)
							.getStringCellValue()
							.equals(sheet.getRow(i).getCell(0)
									.getStringCellValue())) {
						errorMessages.add(customize(
								ERROR_CAPABILITY_RELATIONSHIP, sheet.getRow(0)
										.getCell(0).getStringCellValue(), sheet
										.getRow(i).getCell(0)
										.getStringCellValue(),
								String.valueOf(j + 1), "1"));
					}
					if (relationshipSheet
							.getRow(j)
							.getCell(2)
							.getStringCellValue()
							.equals(sheet.getRow(i).getCell(0)
									.getStringCellValue())) {
						errorMessages.add(customize(
								ERROR_CAPABILITY_RELATIONSHIP, sheet.getRow(0)
										.getCell(0).getStringCellValue(), sheet
										.getRow(i).getCell(0)
										.getStringCellValue(),
								String.valueOf(j + 1), "3"));
					}

					j++;
				}
				i++;
			}
		}
	}

	public void validateWorkbookTabs(Workbook wb) {
		workbookTabsList = new ArrayList<String>();

		for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++)
			workbookTabsList.add(wb.getSheetName(sheetIndex));

		// check that each required tab exists
		for (String requiredTab : requiredTabs) {
			if (!workbookTabsList.contains(requiredTab)) {
				errorMessages.add(customize(ERROR_WORKBOOK_MISSING_TABS,
						requiredTab, "", "", ""));
			} else {
				Sheet sheet = wb.getSheet(requiredTab);
				int rowNum = sheet.getLastRowNum();
				if (rowNum == 0)
					errorMessages.add(customize(ERROR_REQUIRED_TAB_EMPTY,
							requiredTab, "", "", ""));
			}
		}
	}

	public void validateWorkbook(Workbook wb) {
		errorMessages = new ArrayList<String>();
		metadataMap = new HashMap<NameValuePair, String>();

		validateWorkbookTabs(wb);

		// if all required tabs are present, proceed to check them
		if (errorMessages.isEmpty()) {
			validateEntity(wb);
			validateMetadata(wb);
			validateCapabilityAnalysis(wb);
			validateEntityTabs(wb);
			validateRelationship(wb);
			validateDashboard(wb);
			validateTimeline(wb);
		}
	}

	public List<String> getWorkbookValidationErrorMessages() {
		return errorMessages;
	}
}