package com.bah.attune.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.bah.attune.data.AnalysisParameters;
import com.bah.attune.data.AnalysisResult;
import com.bah.attune.data.Entity;
import com.bah.attune.data.NameValuePair;
import com.bah.attune.data.Parameter;

@Repository public class DataAnalysisDao extends BaseDao
{
    public AnalysisResult getAnalysisResults(AnalysisParameters parameters)
    {
        AnalysisResult result = new AnalysisResult();

        result.setCriteria(parameters.getAdditionalParameters());

        String query = "match (n:`" + parameters.getEntityType() + "` {";

        int parameterCount = 0;
        Parameter relationshipParam = null;
        for (Parameter p : parameters.getAdditionalParameters())
        {
            // If this parameter is not a property of the entity, then it must
            // be a relationship
            // Additionally, only look at parameters whose values aren't 'Any',
            // meaning the user is
            // trying to filter by that parameter
            if ( !isField(parameters.getEntityType(), p.getProperty()) && !"Any".equals(p.getValue()) )
            {
                relationshipParam = p;
            }
            else if ( !"Any".equals(p.getValue()) )
            {
                String property = p.getProperty();
                String value = p.getValue();

                query += "`" + property + "`: '" + value + "', ";

                parameterCount++;
            }
        }

        // Remove the last comma if there were any parameters, otherwise remove
        // the open bracket
        if ( parameterCount > 0 )
            query = query.substring(0, query.lastIndexOf(',')) + "}";
        else
            query = query.substring(0, query.lastIndexOf('{'));

        query += ")";

        // Insert the relationsip query if there is a relationship parameter
        if ( relationshipParam != null )
            query += "<--(m:`" + relationshipParam.getProperty() + "` {name:'" + relationshipParam.getValue() + "'})";

        // Add the return statement
        query += " return n";

        List<Entity> entities = new ArrayList<Entity>();
        for (Map<String, Object> row : runQuery(query))
        {
            Node n = (Node) row.get("n");
            Entity entity = new Entity();
            entity.setLabel(getLabels(n));

            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            for (Parameter p : parameters.getAdditionalParameters())
            {
                String property = p.getProperty();
                String value = "";

                if ( n.hasProperty(property) )
                    value = n.getProperty(property).toString();
                else if ( !isField(getLabels(n), property) )
                    value = getRelationshipNodeName(getLabels(n),
                            n.getProperty(NAME).toString(), property);

                NameValuePair pair = new NameValuePair(property, value);

                pairs.add(pair);
            }

            entity.setProperties(pairs);
            entities.add(entity);
        }

        result.setEntities(entities);

        return result;
    }


    private String getRelationshipNodeName(String entity1, String name, String entity2)
    {
        String query = "match (n:`" + entity1 + "` {name:'" + name + "'})<--(m:`" + entity2 + "`) return m";
        String result = "";

        Iterator<Map<String, Object>> iter = runQuery(query).iterator();
        Node theParent = null;

        if ( iter.hasNext() )
            theParent = (Node) iter.next().get("m");

        if ( theParent != null && theParent.hasProperty(NAME) )
            result = theParent.getProperty(NAME).toString();

        return result;
    }


    private boolean isField(String entity, String fieldName)
    {
        String query = "match (n: `Metadata` {name:'" + entity + "'}) return n";
        Map<String, Object> map = runQuery(query).single();

        Node node = (Node) map.get("n");
        String fieldList = (String) node.getProperty("fieldList");

        for (String field : StringUtils.tokenizeToStringArray(fieldList, ","))
        {
            if ( field.equals(fieldName) )
                return true;
        }

        return false;
    }
}
