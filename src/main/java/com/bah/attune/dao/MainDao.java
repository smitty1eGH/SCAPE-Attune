package com.bah.attune.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.bah.attune.data.NameValuePair;

@SuppressWarnings({ "rawtypes" })
@Repository
public class MainDao extends BaseDao
{
    public static final String ICON = "Icon";
    public static final String ATTACHMENTS = "Attachments";

    public List<Node> buildMetadataModel()
    {
        List<Node> nodeList = new ArrayList<Node>();

        String query = "match (n:`Metadata`) return n";

        for (Map row : runQuery(query))
        {
            nodeList.add((Node) row.get("n"));
        }

        return nodeList;
    }

    
    public List<NameValuePair> getEntityList()
    {
        List<NameValuePair> list = new ArrayList<NameValuePair>();

        String query = "match (n:`Metadata`) return n.name order by n.name";

        for (Map row : runQuery(query))
        {
            list.add(new NameValuePair((String) row.get("n.name"), ""));
        }

        return list;
    }

    
    public Integer getEntityCount(String entity)
    {
        String query = "match (n:`" + entity + "`) return count(n) as count";

        Integer count = 0;

        for (Map row : runQuery(query))
        {
            count = (Integer) row.get("count");
            break;
        }

        return count;
    }
    
    
    public String[] getFieldList(String entity)
    {
        List<String> returnList = new ArrayList<String>();

        String query = "match (n: `Metadata` {name:'" + entity + "'}) return n";
        Map<String, Object> map = runQuery(query).single();

        Node node = (Node) map.get("n");
        String fieldList = (String) node.getProperty("fieldList");

        for (String field : StringUtils.tokenizeToStringArray(fieldList, ","))
        {
            if (!field.endsWith(ICON) && !field.equals(ATTACHMENTS))
                returnList.add(field);
        }

        // add the required relationship
        String relationshipQuery = "match (n:`Metadata`{ name: '" + entity + "'})<-[r]-(m) return r.required, m.name";
        for (Map row : runQuery(relationshipQuery))
        {
            if ( (boolean) row.get("r.required") )
                returnList.add((String) row.get("m.name") );
        }
                
        return returnList.toArray(new String[0]);
    }

    
    public String[] getDisplayList(String entity)
    {
        String query = "match (n:Dashboard{entity:'" + entity + "'}) return n";
        Map<String, Object> map = runQuery(query).single();

        Node node = (Node) map.get("n");
        String displayList = (String) node.getProperty("displayList");
        if ("*".equals(displayList))
            return getFieldList(entity);
        else
            return StringUtils.tokenizeToStringArray(displayList, ",");
    }

    
    public List<NameValuePair> getGroupByList(String entity, String groupBy)
    {
        if ( isField(entity, groupBy))
            return  getGroupByFieldList(entity, groupBy);
        else
            return getGroupByEntityList(entity, groupBy);
    }
    
    
    public List<String> getValueList(String entity, String fieldName)
    {
    	if ( isField(entity, fieldName))
    		return getFieldValueList(entity, fieldName);
    	else
    		return getEntityValueList(fieldName);
    }
    
    
    public List<String> getSearchResults(String name) {
    	name = name.toLowerCase();
    	List<String> results = new ArrayList<String>(); 
    	
    	String query = "match (n) where has(n.name) return n.name";
    	
    	for (Map<String, Object> row: runQuery(query)) {
    		String result = ((String) row.get("n.name"));
    		
    		// Give the results that start with the name precedence over 
    		// those that just contain it
    		if (result.toLowerCase().startsWith(name) && !results.contains(result))
    			results.add(0, result);
    		else if (result.toLowerCase().contains(name) && !results.contains(result))
    			results.add(results.size(), result);
    	}
    	
    	return results;
    }
    

    private List<NameValuePair> getGroupByFieldList(String entity, String groupBy)
    {
        List<NameValuePair> list = new ArrayList<NameValuePair>();

        String query = "match (n:`" + entity + "`) return n.`" + groupBy + "`, count(n.`" + groupBy + "`) order by n.`"
                + groupBy + "`";

        for (Map row : runQuery(query))
        {
            list.add(new NameValuePair((String) row.get("n.`" + groupBy + "`"),
                    (Integer) row.get("count(n.`" + groupBy + "`)") + ""));
        }

        return list;
    }
    
    
    private List<String> getFieldValueList(String entity, String field)
    {
        List<String> valueList = new ArrayList<String>();
        String query = "match (n:`" + entity + "`) return distinct n.`" + field + "` as value order by value";

        for (Map row : runQuery(query))
        {
        	String value = (String) row.get("value");
        	
        	if (value != null && !value.isEmpty())
        		valueList.add((String) row.get("value"));
        }

        return valueList;
    }
    
    
    private List<NameValuePair> getGroupByEntityList(String entity, String groupBy)
    {
        List<NameValuePair> list = new ArrayList<NameValuePair>();

        String query = "match (n:`" + entity + "`)-[]-(m:`" + groupBy + "`) return m.name, count(n) order by m.name";

        for (Map row : runQuery(query))
        {
            list.add(new NameValuePair((String) row.get("m.name"), (Integer) row.get("count(n)") + ""));
        }

        return list;
    }
    
    
    private List<String> getEntityValueList(String entity)
    {
        List<String> valueList = new ArrayList<String>();
        String query = "match (n:`" + entity + "`) return distinct n.name order by n.name";

        for (Map row : runQuery(query))
        {
            valueList.add((String) row.get("n.name"));
        }

        return valueList;
    }
    
    
    private boolean isField(String entity, String fieldName)
    {
        String query = "match (n:Metadata {name:'" + entity + "'}) return n";
        Map<String, Object> map = runQuery(query).single();

        Node node = (Node) map.get("n");
        String fieldList = (String) node.getProperty("fieldList");

        for (String field : StringUtils.tokenizeToStringArray(fieldList, ","))
        {
            if ( field.equals(fieldName))
                return true;
        }
        
        return false;
    }


    public void saveDashboard(String[] list)
    {
        for (int i = 1; i <= 3; i++)
        {
            String query = "match (n:Dashboard{name: 'entity" + i + "'}) " + "set n.entity='" + list[i * 6].trim() + "'"
                    + ",   n.chartType='" + list[i * 6 + 1].trim() + "'" + ",   n.groupBy='" + list[i * 6 + 2].trim() + "'"
                    + ",   n.alertCheck='" + list[i * 6 + 3].trim() + "'" + ",   n.alertValue='" + list[i * 6 + 4].trim() + "'"
                    + ",   n.displayList='" + list[i * 6 + 5].trim() + "'";

            runQuery(query);
        }
        
    }
    
    public boolean timelineCreated()
    {
        String query = "match (n:Timeline) return n";
        
        if ( runQuery(query).singleOrNull() != null )
            return true;
        else
            return false;   
    }
}