package com.bah.attune.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.parboiled.common.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bah.attune.dao.ImportDao;
import com.bah.attune.data.AttuneException;
import com.bah.attune.data.NameValuePair;

@Service
public class ImportService extends BaseService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ImportService.class);

	@Autowired
	private ImportDao importDao;

	@Autowired
	private ValidationService validationService;

	private List<NameValuePair> entities;
	
	private Set<String> nodeTypes;

	public List<String> importDataSheet(Workbook wb) throws AttuneException {
		System.out.println("start importing...");

		long s = System.currentTimeMillis();
		long s0 = s;
		validationService.validateWorkbook(wb);
		System.out.println("validation time ="
				+ (System.currentTimeMillis() - s));

		List<String> errorMessages = validationService
				.getWorkbookValidationErrorMessages();

		if (errorMessages.isEmpty()) {
			s = System.currentTimeMillis();

			clearDB();
			System.out.println("clearDB time ="
					+ (System.currentTimeMillis() - s));
			s = System.currentTimeMillis();

			setEntities(wb);
			System.out.println("setEntities time ="
					+ (System.currentTimeMillis() - s));
			s = System.currentTimeMillis();

			createMetadataModel(wb);
			System.out.println("createMetadataModel time ="
					+ (System.currentTimeMillis() - s));
			s = System.currentTimeMillis();

			System.out.println("importing data nodes...");
			createDataNodes(wb);
			System.out.println("createDataNodes time ="
					+ (System.currentTimeMillis() - s));
			s = System.currentTimeMillis();

			createDataRelationships(wb);
			System.out.println("createDataRelationships time ="
					+ (System.currentTimeMillis() - s));
			s = System.currentTimeMillis();

			createDashboard(wb);
			System.out.println("createDashboard time ="
					+ (System.currentTimeMillis() - s));
			s = System.currentTimeMillis();

			createTimeline(wb);
			
			createAbstractNodeEntities();

			System.out.println("Total import time ="
					+ (System.currentTimeMillis() - s0));
		}

		return errorMessages;
	}

	@Transactional
	private void setEntities(Workbook wb) throws AttuneException {
		try {
			Row row;
			Sheet sheet = wb.getSheet("Entity");

			List<NameValuePair> entityValues = new ArrayList<NameValuePair>();

			int i = 1;
			while (sheet.getRow(i) != null) {
				row = sheet.getRow(i);

				if (row.getCell(0) != null
						&& StringUtils.isNotEmpty(row.getCell(0)
								.getStringCellValue())) {
					String name = row.getCell(0).getStringCellValue();
					Boolean isRoot = false;

					Cell cell = row.getCell(1);

					if (cell != null
							&& StringUtils
									.isNotEmpty(cell.getStringCellValue())
							&& "Yes".equalsIgnoreCase(cell.getStringCellValue()))
						isRoot = true;

					entityValues
							.add(new NameValuePair(name, isRoot.toString()));
				}

				i++;
			}

			this.entities = entityValues;
		} catch (Exception ex) {
			LOGGER.error("Error occured in ImportService" + ex);
			throw new AttuneException(
					"Error occured in ImportService: setEntities "
							+ ex.toString());
		}
	}

	@Transactional
	private void createMetadataModel(Workbook wb) throws AttuneException {
		createEntityNodes();
		createMetadataRelationships(wb);
	}

	@Transactional
	private void createEntityNodes() throws AttuneException {
		nodeTypes = new HashSet<String>();
		try {
			for (NameValuePair entity : entities) {
				Collection<String> labels = new ArrayList<String>();
				labels.add("Metadata");

				Map<String, Object> properties = new HashMap<String, Object>();
				properties.put("name", entity.getName());
				properties.put("isRoot",
						Boolean.parseBoolean(entity.getValue()));

				// Get a set of all the node types imported
				nodeTypes.add(entity.getName());
				
				importDao.createNode(labels, properties);
			}

		} catch (Exception ex) {
			LOGGER.error("Error occured in ImportService" + ex);

			throw new AttuneException(
					"Error occured in ImportService: createEntityNodes"
							+ ex.toString());
		}
	}

	@Transactional
	private void createMetadataRelationships(Workbook wb)
			throws AttuneException {
		try {
			Sheet sheet = wb.getSheet("Metadata");
			Row row;

			int i = 1;
			while (sheet.getRow(i) != null) {
				row = sheet.getRow(i);

				String startEntity = row.getCell(0).getStringCellValue();
				String relationship = row.getCell(1).getStringCellValue();
				String endEntity = row.getCell(2).getStringCellValue();

				boolean required = false;
				if (row.getCell(3) != null
						&& "Yes".equalsIgnoreCase(row.getCell(3)
								.getStringCellValue()))
					required = true;

				importDao.createMetadataRelationship(startEntity, endEntity,
						relationship);
				importDao.setRequired(startEntity, endEntity, required);

				i++;
			}
		} catch (Exception ex) {
			LOGGER.error("Error occured in ImportService" + ex);
			throw new AttuneException(
					"Error occured in ImportService: createMetadataRelationships"
							+ ex.toString());
		}
	}
	
	@Transactional
	private void createAbstractNodeEntities() throws AttuneException {
		try {
			for (String nodeType : nodeTypes) {
				importDao.createAbstractNodes(nodeType);
			}
		} catch (Exception ex) {
			LOGGER.error("Error occured in ImportService" + ex);

			throw new AttuneException(
					"Error occured in ImportService: createAbstractNodeEntities"
							+ ex.toString());
		}
	}

	private void createDataNodes(Workbook wb) throws AttuneException {
		try {
			for (NameValuePair entity : entities) {
				long s = System.currentTimeMillis();
				analyzeEntity(wb, entity);
				System.out.println("  import time for " + entity.getName()
						+ "=" + (System.currentTimeMillis() - s));
			}
		} catch (Exception ex) {
			LOGGER.error("Error occured in ImportService" + ex);
			throw new AttuneException(
					"Error occured in ImportService: createDataNodes"
							+ ex.toString());
		}

	}

	private void analyzeEntity(Workbook wb, NameValuePair entity)
			throws AttuneException {
		Sheet sheet;
		Row row;
		Cell cell;

		sheet = wb.getSheet(entity.getName());

		Collection<String> labels = new ArrayList<String>();
		labels.add(entity.getName());

		Short headerRowColumnCount = sheet.getRow(0).getLastCellNum();

		String fieldlist = "";

		row = sheet.getRow(0);
		for (int k = 0; k <= headerRowColumnCount; k++) {
			if (row.getCell(k) != null) {
				fieldlist += removeSpecialCharacters(row.getCell(k)
						.getStringCellValue()) + ",";
			}
		}

		fieldlist = fieldlist.substring(0, fieldlist.length() - 1);

		importDao.setFieldlist(entity.getName(), fieldlist);

		int i = 1;
		long s = System.currentTimeMillis();

		System.out.println("importing " + entity.getName());

		while (sheet.getRow(i) != null) {
			row = sheet.getRow(i);

			if (row.getCell(0) != null
					&& StringUtils.isNotEmpty(row.getCell(0)
							.getStringCellValue())) {
				Map<String, Object> properties = new HashMap<String, Object>();

				for (int j = 0; j <= headerRowColumnCount; j++) {
					if (row.getCell(j) != null) {
						cell = row.getCell(j);

						String value = getStringCellValue(cell);

						if (value != null) {
							String property = sheet.getRow(0).getCell(j)
									.getStringCellValue();

							// Call this method prior to removing
							// special characters from the property
							// name.
							value = formatValueForPropertyType(property, value);

							property = removeSpecialCharacters(property);

							properties.put(property, value);
						}
					}
				}

				importDao.createNode(labels, properties);

			}

			i++;

			if (i % 100 == 0) {
				System.out.println("  i=" + i + " create entity node time = "
						+ (System.currentTimeMillis() - s));
				s = System.currentTimeMillis();
			}
		}
	}

	private String removeSpecialCharacters(String property) {
		String newString = property;
		newString = newString.replace("!", "");
		newString = newString.replace("*", "");
		newString = newString.replace("#", "");
		newString = newString.replace("$", "");
		newString = newString.replace("/", "");

		// Protect against XSS.
		newString = newString.replace("<", "");
		newString = newString.replace(">", "");

		return newString;
	}

	private void createDataRelationships(Workbook wb) throws AttuneException {
		System.out.println("importing relationships...");
		try {
			Sheet sheet = wb.getSheet("Relationship");
			Row row;

			int i = 1;
			long s0 = System.currentTimeMillis();
			long s = s0;
			while (sheet.getRow(i) != null
					&& sheet.getRow(i).getCell(0) != null
					&& StringUtils.isNotEmpty(sheet.getRow(i).getCell(0)
							.getStringCellValue())) {
				row = sheet.getRow(i);

				processRelationshipRow(row);

				i++;

				if (i % 500 == 0) {
					System.out.println("  i=" + i + " processing time ="
							+ (System.currentTimeMillis() - s));
					s = System.currentTimeMillis();
				}
			}

		} catch (Exception ex) {
			LOGGER.error("Error occured in ImportService" + ex);

			throw new AttuneException(
					"Error occured in ImportService: createDataRelationships"
							+ ex.toString());
		}
	}

	@Transactional
	private void processRelationshipRow(Row row) throws AttuneException {
		importDao.processRelationshipRow(row);
	}

	@Transactional
	public void clearDB() {
		importDao.deleteRelationships();
		importDao.deleteNodes();
	}

	@Transactional
	private void createDashboard(Workbook wb) {
		Collection<String> labels = new ArrayList<String>();
		labels.add("Dashboard");

		Sheet sheet = wb.getSheet("Dashboard");
		for (int i = 1; i < 10; i++) {
			String alertCheck = "";
			String alertValue = "";

			Row row = sheet.getRow(i);

			if(row == null || StringUtils.isEmpty(row.getCell(0).getStringCellValue())) {
				continue;
			}

			String name = row.getCell(0).getStringCellValue();
			String entity = row.getCell(1).getStringCellValue();
			String groupBy = row.getCell(2).getStringCellValue();
			String displayList = row.getCell(3).getStringCellValue();
			if (row.getCell(4) != null) {
				alertCheck = row.getCell(4).getStringCellValue();

				Cell cell = row.getCell(5);
				if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					alertValue = cell.getNumericCellValue() + "";
					if (alertValue.endsWith(".0"))
						alertValue = alertValue.substring(0,
								alertValue.length() - 2);
				} else
					alertValue = cell.getStringCellValue();

			}
			String chartType = row.getCell(6).getStringCellValue();

			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("name", name);
			properties.put("entity", entity);
			properties.put("groupBy", groupBy);
			properties.put("displayList", displayList);
			properties.put("alertCheck", alertCheck);
			properties.put("alertValue", alertValue);
			properties.put("chartType", chartType);

			importDao.createNode(labels, properties);
		}
	}

	@Transactional
	private void createTimeline(Workbook wb) {
		Sheet sheet = wb.getSheet("Timeline");
		if (sheet == null)
			return;

		Collection<String> labels = new ArrayList<String>();
		labels.add("Timeline");

		String entity = sheet.getRow(0).getCell(1).getStringCellValue();
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("entity", entity);

		if (sheet.getRow(1) != null)
			properties.put("order by", sheet.getRow(1).getCell(1)
					.getStringCellValue());

		importDao.createNode(labels, properties);
	}

	private String formatValueForPropertyType(String property, String value) {
		String newValue = value;
		if (property.contains("/")) {
			// Handle Excel's formatting of dates
			DateTime beginningOfTime = new DateTime(1900, 1, 1, 0, 0);
			DateTime correctedTime = beginningOfTime.plusDays((int) Double
					.parseDouble(newValue) - 2);
			newValue = correctedTime.toString("MM/dd/yyyy");
		} else if (property.contains("$")) {
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			newValue = nf.format(new BigDecimal(newValue));
		} else if (property.contains("#") && newValue.endsWith(".0")) {
			newValue = newValue.substring(0, newValue.length() - 2);
		}

		return newValue;
	}

	private String getStringCellValue(Cell cell) throws AttuneException {
		String value;

		if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
			value = cell.getStringCellValue();
		} else if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			return null;
		} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			Boolean b = cell.getBooleanCellValue();
			value = b.toString();
		} else if (cell.getCellType() == Cell.CELL_TYPE_ERROR) {
			return null;
		} else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
			return null;
		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			Double d = cell.getNumericCellValue();
			value = d.toString();
		} else {
			return null;
		}

		// Protect against XSS.
		value = value.replace("<", "");
		value = value.replace(">", "");

		return value;
	}
}
