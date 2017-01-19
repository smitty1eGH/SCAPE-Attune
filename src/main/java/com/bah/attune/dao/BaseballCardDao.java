package com.bah.attune.dao;

import java.util.*;
import java.util.logging.Logger;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.TransactionFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository public class BaseballCardDao extends BaseDao
{

    Logger log = Logger.getLogger(BaseballCardDao.class.getName());

    public Map<String, String> getAttributesMap(String entity, String name)
    {
        Map<String, String> attributesMap = new HashMap<String, String>();
        String query = "MATCH (n:`" + entity + "` {name:'" + name + "'}) return n";

        Map<String, Object> map = null;
        try {
             map = runQuery(query).single();
        } catch(NoSuchElementException e) {
            log.info("getAttributesMap failed to find " + name);
            return attributesMap;
        }

        Node node = (Node) map.get("n");
        for (String property : node.getPropertyKeys()) {
            if (property == NAME || property == "Icon")
                continue;

            attributesMap.put(property, (String) node.getProperty(property));
        }

        return attributesMap;
    }

    public Map<String, Map<String, String>> getRatings(String objectName)
    {
        Map<String, Map<String, String>> nodeMap = new HashMap<String, Map<String, String>>();
        String query = "MATCH (n:Rating {object_name:'" + objectName + "'}) return n";

        for (Map<String, Object> map : runQuery(query)) {
            Node node = (Node) map.get("n");

            Map<String, String> attributesMap = new HashMap<String, String>();
            for (String property : node.getPropertyKeys()) {
                attributesMap.put(property, (String) node.getProperty(property));
            }

            nodeMap.put(node.getProperty("user").toString(), attributesMap);
        }

        return nodeMap;
    }


    public Map<String, List<String>> getParentMap(String entity, String name)
    {
        String query = "MATCH (n:`" + entity + "` {name:'" + name + "'}) <-[r]-(n2) return labels(n2), (n2.name)";
        return getRelationMap(query);
    }


    public Map<String, List<String>> getChildrenMap(String entity, String name) {
        String query = "MATCH (n:`" + entity + "` {name:'" + name + "'}) -[r]->(n2) return labels(n2), (n2.name)";
        return getRelationMap(query);
    }

    public Map<String, List<String>> getRelationMap(String query) {
        Map<String, List<String>> resultMap = new HashMap<String, List<String>>();
        List<String> valuesList = new ArrayList<String>();
        for (Map row : runQuery(query))
        {
            String labels = row.get("labels(n2)").toString();
            if ( labels.contains("Timeline Milestone") )
            {
                labels = labels.replaceAll("(, Timeline Milestone)|(Timeline Milestone, )", "");
            }
            if ( labels.contains("AbstractNodeEntity") )
            {
                labels = labels.replaceAll("(, AbstractNodeEntity)|(AbstractNodeEntity, )", "");
            }
            if ( labels.contains("_") )
            {
            	// Replace labels that start with "_".
            	// This is not the same label as the entity, so we have to replace _.*
                labels = labels.replaceAll("((?<=,) _.+?,)|(^_.+?, )|(, _[^,]+?$)", "");
            }
            String key = labels.replaceAll("\\[", "").replaceAll("\\]", "");
            if ( !resultMap.containsKey(key) )
            {
                valuesList = new ArrayList<String>();
                valuesList.add(row.get("(n2.name)").toString());
            }
            else
            {
                valuesList.add(row.get("(n2.name)").toString());
            }
            resultMap.put(labels.replaceAll("\\[", "").replaceAll("\\]", ""), valuesList);
        }

        return resultMap;
    }

    public List<Node> buildNetworkViewModel(String entity, String name)
    {
        List<Node> nodeList = new ArrayList<Node>();

        String query = "MATCH (n:`" + entity + "` {name:'" + name + "'}) -[r]-(n2) return n, n2";

        for (Map row : runQuery(query))
        {
            if ( !nodeList.contains((Node) row.get("n")) )
            {
                nodeList.add((Node) row.get("n"));
            }
            nodeList.add((Node) row.get("n2"));
        }

        return nodeList;
    }

    @Transactional
    public void updateRating(String name, String title, String comment, String score) {
        String strScore = score + ".0";
        String stmnt = String.format("MATCH (n:Rating {name:'%s'}) set n.comment = '%s', n.title='%s', n.rating='%s'",
                                        name, comment, title, strScore);
        runQuery(stmnt);
    }
}
