package com.bah.attune.data;

import java.util.List;

public class AnalysisParameters
{
    private String entityType;
    private List<Parameter> additionalParameters;


    public List<Parameter> getAdditionalParameters()
    {
        return additionalParameters;
    }


    public void setAdditionalParameters(List<Parameter> additionalParameters)
    {
        this.additionalParameters = additionalParameters;
    }


    public String getEntityType()
    {
        return entityType;
    }


    public void setEntityType(String entityType)
    {
        this.entityType = entityType;
    }
}
