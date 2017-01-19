package com.bah.attune.dao;

import java.util.Collection;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bah.attune.data.AttuneException;

@Repository
public class ImportDao extends BaseDao {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ImportDao.class);
	
	@Transactional
	public void createMetadataRelationship(String startEntity,
			String endEntity, String relationship) {
		String createRelationship = "MATCH (n1:`Metadata` { name: '"
				+ startEntity + "'}), " + "(n2:`Metadata` { name: '"
				+ endEntity + "'})  " + "CREATE UNIQUE (n1)-[:`" + relationship
				+ "`]->(n2)";

		runQuery(createRelationship);
	}

	@Transactional
	public void setRequired(String startEntity, String endEntity,
			Boolean required) {
		String setRequired = "MATCH (n1:`Metadata` { name: '" + startEntity
				+ "'})" + "-[r]-" + "(n2:`Metadata` { name: '" + endEntity
				+ "'}) " + "SET r.required = " + required;

		runQuery(setRequired);
	}


	public void processRelationshipRow(Row row) throws AttuneException {
		createRelationship(getStringCellValue(row.getCell(0)), getStringCellValue(row.getCell(1)),
				getStringCellValue(row.getCell(3)), getStringCellValue(row.getCell(4)), getStringCellValue(row.getCell(2)));
	}

	@Transactional
	public void setFieldlist(String entity, String fieldlist) {
		String setFieldlist = "match (n:Metadata { name:'" + entity + "'}) "
				+ " set n.fieldList = '" + fieldlist + "'";

		runQuery(setFieldlist);
	}

	@Transactional
	public void deleteNode(String label) {
		String deleteStmt = "match (n:" + label + ") delete n";

		runQuery(deleteStmt);
	}

	public void deleteNodes() {
		String deleteRelationshipsStmt = "match (n) delete n";
		runQuery(deleteRelationshipsStmt);
	}

	public void deleteRelationships() {
		String deleteRelationshipsStmt = "match ()-[r]-() delete r";
		runQuery(deleteRelationshipsStmt);
	}
	
	@Transactional
	public void createAbstractNodes(String label) {
		String abstractNodeStmt = String.format("MATCH (n:%s) SET n:_%s:`AbstractNodeEntity`", label, label);
		runQuery(abstractNodeStmt);
	}

	private String getStringCellValue(Cell cell) throws AttuneException {
		String value;

		if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
			value = cell.getStringCellValue();
		} else if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			value = null;
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
