package com.bah.attune.data;

import java.util.List;

public class Entity
{
    private String label;
    private List<NameValuePair> properties;


    public String getLabel()
    {
        return label;
    }


    public void setLabel(String label)
    {
        this.label = label;
    }


    public List<NameValuePair> getProperties()
    {
        return properties;
    }


    public void setProperties(List<NameValuePair> properties)
    {
        this.properties = properties;
    }


    public String getPropertyValue(String property)
    {
        for (NameValuePair pair : properties)
        {
            if ( property.equals(pair.getName()) )
                return pair.getValue();
        }

        return "";
    }
}
