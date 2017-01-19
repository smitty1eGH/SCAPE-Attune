package com.bah.attune.data;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class NameValuePair
{
    private String name;
    private String value;

    public NameValuePair()
    {
    }

    public NameValuePair(String name, String value)
    {
        super();
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public String getValue()
    {
        return value;
    }


    public void setValue(String value)
    {
        this.value = value;
    }
    
    @Override
    public boolean equals(Object o)
    {
    	if (o instanceof NameValuePair)
    	{
    		NameValuePair comparing = (NameValuePair) o;
    		return comparing.getName().equals(this.getName()) && 
    		       comparing.getValue().equals(this.getValue());
    	}
    	
    	return false;
    }
    
    @Override
    public int hashCode()
    {
    	return new HashCodeBuilder(31, 37).append(this.getName()).append(this.getValue()).toHashCode();
    }

}
