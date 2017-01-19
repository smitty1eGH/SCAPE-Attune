package com.bah.attune.data;

import java.util.List;

public class MetadataModelJson
{
    List<MetadataJson> nodeList;
    List<MetadataJson> relationshipList;


    public List<MetadataJson> getNodeList()
    {
        return nodeList;
    }


    public List<MetadataJson> getRelationshipList()
    {
        return relationshipList;
    }


    public void setNodeList(List<MetadataJson> nodeList)
    {
        this.nodeList = nodeList;
    }


    public void setRelationshipList(List<MetadataJson> relationshipList)
    {
        this.relationshipList = relationshipList;
    }

}
