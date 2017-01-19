package com.bah.attune.data;

public class Metadata
{
    private String name;
    private String entity;
    private String source;
    private String target;


    public Metadata(String name)
    {
        this.name = name;
    }


    public Metadata(String name, String entity)
    {
        this.name = name;
        this.entity = entity;
    }


    public Metadata(String name, String source, String target)
    {
        this.name = name;
        this.source = source;
        this.target = target;
    }


    public String getId()
    {
        if (source == null)
            return name;
        else
            return source + "_" + name + "_" + target;
    }


    public String getName()
    {
        return name;
    }


    public String getEntity()
    {
        return entity;
    }


    public String getSource()
    {
        return source;
    }


    public String getTarget()
    {
        return target;
    }
}