package com.bah.attune.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.atteo.evo.inflector.English;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.stereotype.Repository;

import com.bah.attune.data.NameValuePair;
import com.bah.attune.data.PortfolioBean;

@Repository public class PortfolioDao extends BaseDao
{
    public List<NameValuePair> getChildLabelsAndCounts(List<String> labels, String startEntity, String entityName)
    {
        List<NameValuePair> portfolioNameValuePairList = new ArrayList<NameValuePair>();

        for (String label : labels)
        {
            String query = "match (n1:`" + startEntity + "` { name: '" + entityName + "'})-[*]->(n2:`" + label
                    + "`) return count(n2)";
            for (Map<String, Object> row : runQuery(query))
            {
                String count = row.get("count(n2)").toString();

                NameValuePair portfolioNameValuePair = new NameValuePair(label, count);

                portfolioNameValuePairList.add(portfolioNameValuePair);
            }
        }

        return portfolioNameValuePairList;
    }


    public List<String> getPortfolioEntityList()
    {
        List<String> list = new ArrayList<String>();

        String query = "match (n:`Metadata`) return distinct n.name order by n.name";

        for (Map<String, Object> row : runQuery(query))
        {
            if ( !row.get("n.name").toString().contains("Capability Analysis") )
                list.add((String) row.get("n.name"));
        }

        return list;
    }


    public List<String> getChildEntitiesList(String entity)
    {
        List<String> list = new ArrayList<String>();

        String query = "match (n1:Metadata { name: '" + entity + "' })-[*]->(n2:Metadata) return distinct n2.name";

        for (Map<String, Object> row : runQuery(query))
        {
            String label = row.get("n2.name").toString();

            list.add(label);
        }

        return list;
    }


    public Map<String, String> getAttributesMap(String entity, String name)
    {
        Map<String, String> attributesMap = new HashMap<String, String>();
        String query = "MATCH (n:" + entity + " {name:'" + name + "'}) return n";
        Map<String, Object> map = runQuery(query).single();

        Node node = (Node) map.get("n");
        for (String property : node.getPropertyKeys())
        {
            if ( property == NAME || property == "Icon" )
                continue;

            attributesMap.put(property, (String) node.getProperty(property));
        }

        return attributesMap;
    }


    public String getIcon(String entity, String name)
    {
        String url = "/attune/images/generic.png";

        String query = "MATCH (n:" + entity + " {name:'" + name + "'}) return n";

        Map<String, Object> map = runQuery(query).single();

        Node node = (Node) map.get("n");

        if ( node.hasProperty("Icon") )
        {
            url = "/attune/icons/" + (String) node.getProperty("Icon");
        }

        return url;

    }


    public List<String> getSubGroupingList(String entity)
    {
        List<String> list = new ArrayList<String>();

        String query = "match n-->(m:Metadata {name:'" + entity + "'}) return n.name";
        Result<Map<String, Object>> result = runQuery(query);

        while (result.iterator().hasNext())
        {
            String name = (String) result.iterator().next().get("n.name");

            list.add(name);

            query = "match n-->(m:Metadata {name:'" + name + "'}) return n.name";
            result = runQuery(query);
        }

        return list;

    }


    public List<String> getChildList(String entity)
    {
        List<String> list = new ArrayList<String>();
        addChild(entity, list);
        return list;
    }


    private void addChild(String entity, List<String> list)
    {
        String query = "match (m:Metadata {name:'" + entity + "'})-->n return n.name";
        Result<Map<String, Object>> result = runQuery(query);

        for (Map<String, Object> row : result)
        {
            String child = (String) row.get("n.name");
            list.add(child);

            addChild(child, list);
        }
    }


    /***
     * This function is used to determine what grouping the entity should fall
     * under. This picks the first level ancestor of the node if it has it, and
     * returns itself if it has none (therefore it will be grouped by itself).
     * 
     * @param entity
     * @return
     */
    public String getDefaultGroupingByEntity(String entity)
    {
        // The grouping will be itself if it has no parents
        String grouping = entity;

        String query = "match (n1:Metadata {name: '" + entity + "'})<--(n2:Metadata) return distinct n2.name";

        Result<Map<String, Object>> results = runQuery(query);

        // Select one parent and use that
        for (Map<String, Object> row : results)
        {
            grouping = row.get("n2.name").toString();
            break;
        }

        return grouping;
    }


    /***
     * Returns the relationship between the first and second entities. Three
     * possible outcomes: 1. Orphan (the two entities are the same 2. Parent
     * (entity1 is the parent of the entity2) 3. Grandparent (entity1 is some
     * distant ancestor of entity2)
     * 
     * 
     * @param entity1
     * @param entity2
     * @return
     */
    public String getRelationship(String entity1, String entity2)
    {
        if ( entity1.equals(entity2) )
            return "isOrphan";
        else
        {
            String parentQuery = "match (n1:Metadata {name:'" + entity1 + "'})-->(n2:Metadata {name: '" + entity2
                    + "'}) return n2";

            if ( runQuery(parentQuery).iterator().hasNext() )
                return "isParent";
            else
                return "isGrandparent";
        }
    }


    /***
     * This returns the list of name value pairs of each type of children (of
     * any distance separated), along with the number of children of that type
     * it has. <br>
     * For example, a 'Ship' could have a child 'Event' and 'Event' could have a
     * child 'Location'. The list of name value pairs would contain 'Event' and
     * 'Location', and how many of those types are below the 'Ship'.
     * 
     * @param entity
     * @param label
     * @return
     */
    public List<NameValuePair> getChildLabelsAndCounts(String entity, String label, String content)
    {
        List<NameValuePair> counts = new ArrayList<NameValuePair>();

        String query = "match (n1:`" + label + "` {name: '" + entity + "'})-[*]->(n2) return distinct n2";

        for (Map<String, Object> row : runQuery(query))
        {
            Node n = (Node) row.get("n2");

            for (Iterator<Label> iter = n.getLabels().iterator(); iter.hasNext();)
            {
                String l = iter.next().name();

                // Skip SDN labels
                if(isSDNLabel(l)) {
                	continue;
                }
                
                String countQuery = "match (n1:" + label + " {name:'" + entity + "'})-[*]->(n2:`" + l + "`) " + 
                                    "return count(distinct n2) as count";
                
                Map<String, Object> map = (runQuery(countQuery)).singleOrNull();
                
                if ( map != null )
                {
                    NameValuePair pair = new NameValuePair(l, map.get("count") + "");

                    if ( !counts.contains(pair) && pair.getName().equals(content) )
                        counts.add(pair);
                }
            }
        }

        return counts;
    }


    public List<PortfolioBean> getPortfolioBeanList(String chiclet, String grouping, String relationship, String content)
    {
        List<PortfolioBean> beans = new ArrayList<PortfolioBean>();

        switch (relationship)
        {
            case "isOrphan": handleOrphan(beans, chiclet, content); break;
          
            case "isParent": handleParent(beans, grouping, chiclet, content); break;
           
            case "isGrandparent": handleGrandparent(beans, grouping, chiclet, content); break;
            
            default: break;
        }

        return beans;
    }


    private void handleGrandparent(List<PortfolioBean> beans, String grouping, String chiclet, String content)
    {
        for (Node group : getEntitiesByLabel(grouping))
        {
            PortfolioBean groupBean = new PortfolioBean();

            groupBean.setLabel(grouping);
            groupBean.setName(group.getProperty(NAME).toString());

            List<PortfolioBean> subgroupBeans = new ArrayList<PortfolioBean>();
            String subgroupQuery = "match (n1:`" + grouping + "` {name:'" + groupBean.getName() + "'})" + 
                                          "-->(n2)-[*]->(n3:`" + chiclet + "`) return distinct n2";
            
            for (Map<String, Object> row1 : runQuery(subgroupQuery))
            {
                Node n = (Node) row1.get("n2");

                PortfolioBean subgroupBean = new PortfolioBean();

                String subgroupName = n.getProperty(NAME).toString();
                String subgroupLabel = getLabels(n);

                subgroupBean.setName(subgroupName);
                subgroupBean.setLabel(subgroupLabel);

                List<PortfolioBean> childBeans = new ArrayList<PortfolioBean>();
                String childQuery = "match (n1:`" + subgroupLabel + "` {name:'" + subgroupName + "'})" + 
                                            "-[*]->" + "(n2:`" + chiclet + "`) return distinct n2";
                
                for (Map<String, Object> row2 : runQuery(childQuery))
                {
                    Node child = (Node) row2.get("n2");

                    PortfolioBean childBean = new PortfolioBean();

                    String name = child.getProperty(NAME).toString();
                    String label = getLabels(child);

                    childBean.setName(name);
                    childBean.setLabel(label);
                    childBean.setCounts(getChildLabelsAndCounts(name, label, content));
                    childBean.setIsGap(checkGap(name, label));
                    
                    childBeans.add(childBean);
                }
                subgroupBean.setChildren(childBeans);
                subgroupBeans.add(subgroupBean);
            }

            groupBean.setChildren(subgroupBeans);
            beans.add(groupBean);
        }

    }


    private void handleParent(List<PortfolioBean> beans, String grouping, String chiclet, String content)
    {
        for (Node group : getEntitiesByLabel(grouping))
        {
            PortfolioBean groupBean = new PortfolioBean();

            groupBean.setLabel(grouping);
            groupBean.setName(group.getProperty(NAME).toString());

            List<PortfolioBean> children = new ArrayList<PortfolioBean>();
            String childQuery = "match (n1:`" + grouping + "` {name:'" + groupBean.getName() + "'})" + 
                                   "-->(n2:`" + chiclet + "`) return distinct n2";
            
            for (Map<String, Object> row : runQuery(childQuery))
            {
                Node n = (Node) row.get("n2");
                PortfolioBean child = new PortfolioBean();

                String label = getLabels(n);
                String name = n.getProperty(NAME).toString();

                child.setName(name);
                child.setLabel(label);
                child.setCounts(getChildLabelsAndCounts(name, label, content));
                child.setIsGap(checkGap(name, label));
                
                children.add(child);
            }

            groupBean.setChildren(children);
            beans.add(groupBean);
        }
        
    }


    private void handleOrphan(List<PortfolioBean> beans, String chiclet, String content)
    {
        PortfolioBean bean = new PortfolioBean();
        List<PortfolioBean> children = new ArrayList<PortfolioBean>();

        bean.setLabel(chiclet);
        bean.setName("All " + English.plural(chiclet));

        for (Node n : getEntitiesByLabel(chiclet))
        {
            PortfolioBean child = new PortfolioBean();

            String label = getLabels(n);
            String name = n.getProperty(NAME).toString();
            
            child.setName(name);
            child.setLabel(label);
            child.setCounts(getChildLabelsAndCounts(chiclet, n.getProperty(NAME).toString(), content));
            child.setIsGap(checkGap(name, label));
            
            children.add(child);
        }

        bean.setChildren(children);
        beans.add(bean);
    }


    private boolean checkGap(String name, String label)
    {
        //match (n:Metadata{name:'Capability'})-[r]-(m) return r, m
        String metaQuery = "match (n:`Metadata`{ name: '" + label + "'})-[r]-(m) return r.required, m.name";
        List<String> metaRelationshipList = new ArrayList<String>();
        
        for (Map row: runQuery(metaQuery))
        {
            if ( (boolean) row.get("r.required") )
                metaRelationshipList.add((String)row.get("m.name"));
        }
              
        if ( ! metaRelationshipList.isEmpty() )
        {
            //match (n:Capability{name:'Data Storage'})-[r]-(m) return distinct labels(m)
            List<String> nodeRelationshipList = new ArrayList<String>();
            
            String nodeQuery = "match (n:`" + label + "`{ name: '" + name + "'})-[r]-(m) return distinct labels(m)";  
            for (Map row: runQuery(nodeQuery))
            {
                List<String> list = (List<String>)row.get("labels(m)");
                if ( ! list.isEmpty() )
                	for(String l : list) {
                		if(!isSDNLabel(l)){
                			nodeRelationshipList.add(l);
                		}
                	}
            }
            
            boolean isGap = true;
            for (String metaRel: metaRelationshipList)
            {
                for (String nodeRel: nodeRelationshipList)
                {
                    if ( nodeRel.equals(metaRel))
                    {
                        isGap = false;
                        break;
                    }
                }
            }

            return isGap;
        }
        else
            return false;
    }

}
