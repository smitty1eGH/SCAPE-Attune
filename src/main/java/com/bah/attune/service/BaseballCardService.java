package com.bah.attune.service;

import java.util.*;
import java.util.logging.Logger;

import com.bah.attune.dao.ImportDao;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bah.attune.dao.BaseDao;
import com.bah.attune.dao.BaseballCardDao;
import com.bah.attune.data.Metadata;
import com.bah.attune.data.MetadataJson;
import com.bah.attune.data.MetadataModelJson;
import org.springframework.transaction.annotation.Transactional;

@Service public class BaseballCardService extends BaseService
{
    @Autowired private BaseballCardDao dao;

    @Override
    public BaseDao getDao()
    {
        return dao;
    }

    Logger log = Logger.getLogger(BaseballCardService.class.getName());

    public MetadataModelJson getNetworkView(String entity, String name)
    {
        MetadataModelJson json = new MetadataModelJson();

        List<MetadataJson> nodeList = new ArrayList<MetadataJson>();
        List<MetadataJson> relationshipList = new ArrayList<MetadataJson>();

        Map<String, String> map = new HashMap<String, String>();
        List<Node> list = dao.buildNetworkViewModel(entity, name);
        for (Node node : list)
        {
            if ( node.getProperty(BaseDao.NAME).equals(name) )
            {
                for (Relationship relationship : node.getRelationships(Direction.OUTGOING))
                {
                    Metadata rel = new Metadata(relationship.getType().toString(),
                            (String) relationship.getStartNode().getProperty(BaseDao.NAME),
                            (String) relationship.getEndNode().getProperty(BaseDao.NAME));

                    relationshipList.add(new MetadataJson(rel));
                    map.put((String) relationship.getStartNode().getProperty(BaseDao.NAME),
                            BaseDao.getLabels(relationship.getStartNode()));
                    map.put((String) relationship.getEndNode().getProperty(BaseDao.NAME),
                    		BaseDao.getLabels(relationship.getEndNode()));
                }

                for (Relationship relationship : node.getRelationships(Direction.INCOMING))
                {
                    Metadata rel = new Metadata(relationship.getType().toString(),
                            (String) relationship.getStartNode().getProperty(BaseDao.NAME),
                            (String) relationship.getEndNode().getProperty(BaseDao.NAME));

                    relationshipList.add(new MetadataJson(rel));
                    map.put((String) relationship.getStartNode().getProperty(BaseDao.NAME),
                    		BaseDao.getLabels(relationship.getStartNode()));
                    map.put((String) relationship.getEndNode().getProperty(BaseDao.NAME),
                    		BaseDao.getLabels(relationship.getEndNode()));
                }
            }
        }

        for (String nodeName : map.keySet())
        {
            nodeList.add(new MetadataJson(new Metadata(nodeName, map.get(nodeName))));
        }

        json.setNodeList(nodeList);
        json.setRelationshipList(relationshipList);

        return json;
    }

    @Transactional
    public void createRating(String entity, String user, String rating, String title, String comment, String objectName) {
        String name = objectName + "_" + user;
        String label = "Rating";


        log.info("get attributes map");
        Map<String, String> existingNode = dao.getAttributesMap(label, name);
        log.info(existingNode.toString());

        if(existingNode.size() == 0) {
            Collection<String> labels = new ArrayList<String>();
            labels.add(label);

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("name", name);
            properties.put("user", user);
            properties.put("rating", rating + ".0");
            properties.put("title", title);
            properties.put("comment", comment);
            properties.put("object_name", objectName);

            dao.createNode(labels, properties);
            dao.createAbstractNode(name, label);

            String relationship = entity.toLowerCase().substring(0, 1) + "_rating";

            dao.createRelationship(entity, objectName, label, name, relationship);
        } else {
            log.info("node already exists, updating");
            dao.updateRating(name, title, comment, rating);
        }

    }

}
