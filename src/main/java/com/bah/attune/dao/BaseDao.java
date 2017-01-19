package com.bah.attune.dao;

import java.util.*;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.transaction.annotation.Transactional;

public class BaseDao
{
	public static final String NAME = "name";
	
    @Autowired
    private Neo4jTemplate template;

    public Neo4jTemplate neo4j()
    {
        return template;
    }

    public List<Map<String, Object>> loadAll(String label)
    {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String query = "match (n:`" + label + "`) return n order by n.name";

        for (Map<String, Object> row : runQuery(query))
        {
            Map<String, Object> map = new HashMap<String, Object>();

            collectProperties(map, (Node) row.get("n"));

            list.add(map);
        }

        return list;

    }
    
    public List<Node> getEntitiesByLabel(String entityLabel)
    {
    	List<Node> nodes = new ArrayList<Node>();
    	String query = "match (n:`" + entityLabel + "`) return n";
    	
    	for (Map<String, Object> row: runQuery(query))
    		nodes.add((Node) row.get("n"));
    	
    	return nodes;
    }



    public Result<Map<String, Object>> runQuery(String query)
    {
        return neo4j().query(query, null);
    }
    
    @Transactional
    public Node createNode(Collection<String> labels,
                           Map<String, Object> properties) {
        Node node = neo4j().createNode(properties, labels);

        return node;
    }

    @Transactional
    public void createAbstractNode(String entity, String label) {
        String abstractNodeStmt = String.format("MATCH (n:%s {name:\"%s\"}) SET n:_%s:`AbstractNodeEntity`", label, entity, label);
        runQuery(abstractNodeStmt);
    }

    @Transactional
    public void createRelationship(String entity1, String name1, String entity2, String name2, String relationship) {
        String createStmt = "MATCH (n1:`" + entity1 + "` { name: '" + name1
                + "'}), (n2:`" + entity2 + "` { name: '" + name2
                + "'}) CREATE UNIQUE (n1)-[r:`" + relationship + "`]->(n2)";

        runQuery(createStmt);
    }
    
    public List<Map<String, Object>> loadEntity(String label)
    {
        // find required relationship
        List<String> relationshipList = new ArrayList<String>();
        
        String relationshipQuery = "match (n:`Metadata`{ name: '" + label + "'})<-[r]-(m) return r.required, m.name";
        for (Map row : runQuery(relationshipQuery))
        {
            if ( (boolean) row.get("r.required") )
                relationshipList.add((String) row.get("m.name") );
        }

        String query = "match (n:`" + label + "`) return n";
        if ( ! relationshipList.isEmpty() )
            query = "match (n:`" + label + "`)<-[]-(m:`" + relationshipList.get(0) + "`) return n, m.name"; 
        
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> row : runQuery(query))
        {
            Map<String, Object> map = new HashMap<String, Object>();

            collectProperties(map, (Node) row.get("n"));
            if ( ! relationshipList.isEmpty() )
                map.put(relationshipList.get(0),(String) row.get("m.name"));
            
            list.add(map);
        }
        
        
        return list;

    }
    
    protected void collectProperties(Map<String, Object> map, PropertyContainer container)
    {
        for (String key : container.getPropertyKeys())
            map.put(key, container.getProperty(key));
    }
    
    // Remove SDN labels from the label list
    public static String getLabels(Node node) {
    	StringJoiner labelList = new StringJoiner(", ");
    	String label;
    	
    	Iterator<Label> labels = node.getLabels().iterator();
    	while(labels.hasNext()) {
    		label = labels.next().name();
    		if(!isSDNLabel(label)) {
    			labelList.add(label);
    		}
    	}
    	
    	return labelList.toString();
    }
    
    public static boolean isSDNLabel(String label) {
    	return label.startsWith("_") || "AbstractNodeEntity".equals(label);
    }
}
