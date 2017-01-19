package com.bah.attune.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.Node;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bah.attune.data.NameValuePair;
import com.bah.attune.data.TimelineData;
import com.bah.attune.data.TimelineEvent;
import com.bah.attune.data.TimelineGroup;

@Repository
public class TimelineDao extends BaseDao 
{
	public String getSelectedEntity()
	{
		Map<String, Object> result = runQuery("match (n:Timeline) return n").single();
		
		if (result != null)
		{
			Node node = (Node) result.get("n");
			return node.getProperty("entity").toString();
		}
		
		return null;
	}
	
	
	public NameValuePair getSelectedEntityAndCount()
	{
		String entity = getSelectedEntity();
		
		String query = "match (n:`" + entity + "`) return count(distinct n)";
		String count = runQuery(query).single().get("count(distinct n)").toString();
		
		return new NameValuePair(entity, count);
	}
	
	
	public NameValuePair getParentEntityAndCount()
	{
		String selectedEntity = getSelectedEntity();
		
		// If a selected entity has multiple parent types, just use one
		String query1 = "match (n)-->(m:`" + selectedEntity + "`) return distinct n";
		Result<Map<String, Object>> result = runQuery(query1);
		
		if ( result.iterator().hasNext()  )
		{
		    Node node = (Node) result.iterator().next().get("n");
		
    		String label = getLabels(node);
    		
    		String query2 = "match (n:`" + label + "`)-->(m:`" + selectedEntity + "`) return count(distinct n)";
    		String count = runQuery(query2).single().get("count(distinct n)").toString();
    		
    		return new NameValuePair(label, count);
		}
		else
		{
		    return new NameValuePair("", "");
		}
	}
	
	
	public NameValuePair getChildEntityAndCount()
	{
		String selectedEntity = getSelectedEntity();
		
		// If a selected entity has multiple children types, just use one
		String query1 = "match (n:`" + selectedEntity + "`)-->(m) return m";
		Node node = (Node) runQuery(query1).iterator().next().get("m");
		String label = getLabels(node);
		
		String query2 = "match (n:`" + selectedEntity + "`)-->(m:`" + label + "`) return count(distinct m)";
		String count = runQuery(query2).single().get("count(distinct m)").toString();
		
		return new NameValuePair(label, count);
	}
	
	
	public List<NameValuePair> getSumOnPropertiesAndCounts()
	{
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		
		String selectedEntity = getSelectedEntity();
		String query1 = "match (n:Timeline) return n.sumOn";
		List<String> properties = (List<String>) runQuery(query1).single().get("n.sumOn");
		
		for (String property: properties)
		{
			String query2 = "match (n:`" + selectedEntity + "`) return n.`" + property + "`";
			int count = 0;
			
			for (Map<String, Object> row: runQuery(query2))
			{
				int value = Integer.parseInt(row.get("n.`" + property + "`").toString());
				count += value;
			}
			
			pairs.add(new NameValuePair(property, String.valueOf(count)));
		}
		
		return pairs;
	}
	
	
	public TimelineData getTimelineData() 
	{
		TimelineData data = new TimelineData();

		List<String> types = new ArrayList<String>();
		
		String selectedEntity = getSelectedEntity();
		String query = "match (n:`" + selectedEntity + "`)-->(m:TimelineEvent) optional match (m)-->(o:Milestone) return n, m, o ";
		String orderBy = "order by ";
		String orderByProperty = "";
		
        Map<String, Object> result = runQuery("match (n:Timeline) return n").singleOrNull();
        
        if (result != null)
        {
            Node node = (Node) result.get("n");
            
            if (node.hasProperty("order by") && node.getProperty("order by") != null) {
            	orderByProperty = node.getProperty("order by").toString();
            	orderBy += "n.`" + node.getProperty("order by").toString() + "`, ";
            }
        }
        
        orderBy +="n.name";
        
        for (Map<String, Object> row: runQuery(query + orderBy)) 
		{
			Node parent = (Node) row.get("n");
			String groupId = Long.toString(parent.getId());
			
			if (!data.hasGroup(groupId))
			{
				TimelineGroup group = new TimelineGroup();
				group.setId(groupId);
				group.setContent(parent.getProperty(NAME).toString());
				
				if (StringUtils.isNotEmpty(orderByProperty) && parent.hasProperty(orderByProperty))
					group.setGroupByValue(parent.getProperty(orderByProperty).toString());
				
				// Set the funding status. By default, we want the entities to be 'Funded' if there's
				// no Funding Status property
				if (parent.hasProperty("Funding Status"))
					group.setFunded(parent.getProperty("Funding Status").toString().equals("Funded"));
				else
					group.setFunded(true);
				
				data.addGroup(group);
			}
			
			// Add the TimelineEvent node
			Node child = (Node) row.get("m");
			String childId = Long.toString(child.getId());
			
			if (!data.hasEvent(childId))
			{
				TimelineEvent event = new TimelineEvent();
				event.setId(childId);
				event.setContent(child.getProperty(NAME).toString());
				event.setGroup(groupId);
				event.setStart(getStartDate(child));
				event.setEntityLabel(getLabels(child));
				
				if (!getStartDate(child).equals(getEndDate(child)) && StringUtils.isNotBlank(getEndDate(child)))
				{
					event.setType("range");
					event.setEnd(getEndDate(child));
				}
				else
					event.setType("point");
				
				if (child.hasProperty("Cost"))
					event.setCost(child.getProperty("Cost").toString());
				
				if (child.hasProperty("Type")) 
				{
					event.setInternalType(child.getProperty("Type").toString());
					
					if (!types.contains(child.getProperty("Type").toString()))
						types.add(child.getProperty("Type").toString());
				}
				
				data.addEvent(event);
			}
			
			// Add the Milestone node
			Node milestoneChild = (Node) row.get("o");
			
			if (milestoneChild != null)
			{
				String milestoneId = Long.toString(milestoneChild.getId());
				
				if (!data.hasEvent(milestoneId))
				{
					TimelineEvent milestoneEvent = new TimelineEvent();
					milestoneEvent.setId(milestoneId);
					milestoneEvent.setContent(milestoneChild.getProperty(NAME).toString());
					milestoneEvent.setGroup(groupId);
					milestoneEvent.setStart(getStartDate(milestoneChild));
					milestoneEvent.setEntityLabel(getLabels(milestoneChild));
					
					if (!getStartDate(milestoneChild).equals(getEndDate(milestoneChild)) && StringUtils.isNotBlank(getEndDate(milestoneChild)))
					{
						milestoneEvent.setType("range");
						milestoneEvent.setEnd(getEndDate(milestoneChild));
					}
					else
						milestoneEvent.setType("point");
					
					if (milestoneChild.hasProperty("Cost"))
						milestoneEvent.setCost(milestoneChild.getProperty("Cost").toString());
					
					if (milestoneChild.hasProperty("Type"))
					{
						milestoneEvent.setInternalType(milestoneChild.getProperty("Type").toString());
						
						if (!types.contains(milestoneChild.getProperty("Type").toString()))
							types.add(milestoneChild.getProperty("Type").toString());
					}
					
					data.addEvent(milestoneEvent);
				}
			}
		}
        
        data.setTypes(types);
		
		return data;
	}
	
	
	public int getTotalBudget() 
	{
		int total = 0;
		
		String query = "match (n:TimelineBudget) return n";
		
		for (Map<String, Object> row: runQuery(query)) {
			Node n = (Node) row.get("n");
			
			String amount = n.hasProperty("Amount") ? n.getProperty("Amount").toString() : "0";
			
			amount = amount.replaceAll("\\$", "").replaceAll(",", "");
			
			// Parse as a double to handle the possible decimal
			int cost = (int) Double.parseDouble(amount);
			
			total += cost;
		}
		
		return total;
	}
	
	
	public List<NameValuePair> getBudgetsByFiscalYear() {
		List<NameValuePair> budgets = new ArrayList<NameValuePair>();
		
		String query = "match (n:TimelineBudget) return n order by n.FY";
		
		for (Map<String, Object> row: runQuery(query)) {
			Node node = (Node) row.get("n");
			String fiscalYear = node.getProperty("FY").toString();
			String amount = node.getProperty("Amount").toString();
			NameValuePair budgetPair = new NameValuePair(fiscalYear, amount);
			
			budgets.add(budgetPair);
		}
		
		return budgets;
	}
	
	
	public List<NameValuePair> getParents(String selectedEntity) {
		List<NameValuePair> labelsAndCounts = new ArrayList<NameValuePair>();
		
		String query = "match (n:Metadata)-[]->(m:Metadata {name: '" + selectedEntity + "'}) return n";
		
		for (Map<String, Object> row: runQuery(query)) 
		{
			Node node = (Node) row.get("n");
			String label = node.getProperty("name").toString();
			String query2 = "match (n:`" + label + "`)-[]->(m:`" + selectedEntity + "`) return count(distinct n)";
			String count = runQuery(query2).single().get("count(distinct n)").toString();
			labelsAndCounts.add(new NameValuePair(label, count));
		}
		
		return labelsAndCounts;
	}
	
	
	public List<NameValuePair> getChildren(String selectedEntity) {
		List<NameValuePair> labelsAndCounts = new ArrayList<NameValuePair>();
		
		String query = "match (n:Metadata {name: '" + selectedEntity + "'})-[]->(m:Metadata) return m";
		
		for (Map<String, Object> row: runQuery(query)) {
			Node node = (Node) row.get("m");
			String label = node.getProperty("name").toString();
			String query2 = "match (n:`" + selectedEntity + "`)-[]->(m:`" + label + "`) return count(distinct m)";
			String count = runQuery(query2).single().get("count(distinct m)").toString();
			labelsAndCounts.add(new NameValuePair(label, count));
		}
		
		return labelsAndCounts;
	}
	
	
	private String getStartDate(Node node)
	{
		Iterable<String> keys = node.getPropertyKeys();
		
		for (String key: keys)
			if (key.contains("Start Date"))
					return node.getProperty(key).toString();
		
		return "";
	}
	
	
	private String getEndDate(Node node)
	{
		Iterable<String> keys = node.getPropertyKeys();
		
		for (String key: keys)
			if (key.contains("End Date"))
					return node.getProperty(key).toString();
		
		return "";
	}
	
	
	public List<NameValuePair> getOrderedList()
	{
	    List<NameValuePair> list = new ArrayList<NameValuePair>();
        
        Node timelineNode  = (Node) runQuery("match (n:Timeline) return n").single().get("n");
        
        String orderCause = "order by ";
        String orderBy = "";
        
        if (timelineNode.hasProperty("order by") && timelineNode.getProperty("order by") != null) {
        	orderBy = (String) timelineNode.getProperty("order by");
        	orderCause += "n.`" + orderBy + "`, ";
        }

        orderCause +="n.name";
        
        String entity = timelineNode.getProperty("entity").toString();
        
        String query = "match (n:`" + entity + "`) return n ";
        
        for (Map<String, Object> row: runQuery(query + orderCause)) 
        {
            Node node = (Node) row.get("n");
            NameValuePair pair = new NameValuePair();
            pair.setName((String) node.getProperty(NAME));
            
            if (StringUtils.isNotBlank(orderBy))
            	pair.setValue((String) node.getProperty(orderBy));
            else
            	pair.setValue((String) node.getProperty(NAME));
            
            list.add(pair);
        }
        
        return list;        
	}
	
	
	@Transactional
	public void saveTimelineEvent(TimelineEvent event)
	{
		String entityLabel = event.getEntityLabel();
		String entityName = event.getContent();
		String startDate = event.getStart();
		String endDate = event.getEnd();
		
		Node node = (Node) runQuery("match (n:`" + entityLabel + "` {name:'" + entityName + "'}) return n").single().get("n");
		
		for (String property: node.getPropertyKeys())
		{
			if (property.contains("Start Date") && StringUtils.isNotBlank(startDate))
				node.setProperty(property, startDate);
			if (property.contains("End Date") && StringUtils.isNotBlank(endDate))
				node.setProperty(property, endDate);
		}
	}
}
