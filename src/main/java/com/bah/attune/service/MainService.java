package com.bah.attune.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bah.attune.dao.BaseDao;
import com.bah.attune.dao.MainDao;
import com.bah.attune.data.Metadata;
import com.bah.attune.data.MetadataJson;
import com.bah.attune.data.MetadataModelJson;

@Service
public class MainService extends BaseService
{
    @Autowired
    private MainDao dao;


    @Override
    public BaseDao getDao()
    {
        return dao;
    }


    public MetadataModelJson getMetadata()
    {
        MetadataModelJson json = new MetadataModelJson();

        List<MetadataJson> nodeList = new ArrayList<MetadataJson>();
        List<MetadataJson> relationshipList = new ArrayList<MetadataJson>();

        Map<String, String> map = new HashMap<String, String>();
        List<Node> list = dao.buildMetadataModel();
        for (Node node : list)
        {
            for (Relationship relationship : node.getRelationships(Direction.OUTGOING))
            {
                Metadata rel = new Metadata(relationship.getType().toString(),
                        (String) relationship.getStartNode().getProperty("name"),
                        (String) relationship.getEndNode().getProperty("name"));

                relationshipList.add(new MetadataJson(rel));
                map.put((String) relationship.getStartNode().getProperty("name"), "");
                map.put((String) relationship.getEndNode().getProperty("name"), "");
            }
        }

        for (String name : map.keySet())
            nodeList.add(new MetadataJson(new Metadata(name)));

        json.setNodeList(nodeList);
        json.setRelationshipList(relationshipList);

        return json;
    }


    @Transactional
    public void saveDashboard(String[] list)
    {
        dao.saveDashboard(list);
    }

}
