package com.bah.attune.data;

/***
 * The TimelineGroup class represents a row on the timeline graph.
 *
 */
public class TimelineGroup
{
    private String id;
    private String content;
    private String groupByValue;

    private boolean funded;


    public String getId()
    {
        return id;
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public String getContent()
    {
        return content;
    }


    public void setContent(String content)
    {
        this.content = content;
    }


    public String getGroupByValue()
    {
        return groupByValue;
    }


    public void setGroupByValue(String groupByValue)
    {
        this.groupByValue = groupByValue;
    }


    public boolean isFunded()
    {
        return funded;
    }


    public void setFunded(boolean isFunded)
    {
        this.funded = isFunded;
    }
}
