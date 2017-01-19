package com.bah.attune.data;

/***
 * The TimelineEvent class represents an individual point or range on the
 * timeline graph.
 *
 */
public class TimelineEvent
{
    private String group;
    private String id;
    private String content;
    private String type;
    private String color;
    private String start;
    private String end;
    private String cost;
    private String internalType;
    private String entityLabel;


    public String getGroup()
    {
        return group;
    }


    public void setGroup(String group)
    {
        this.group = group;
    }


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


    public String getType()
    {
        return type;
    }


    public void setType(String type)
    {
        this.type = type;
    }

    public String getColor()
    {
        return color;
    }


    public void setColor(String color)
    {
        this.color = color;
    }


    public String getStart()
    {
        return start;
    }


    public void setStart(String start)
    {
        this.start = start;
    }


    public String getEnd()
    {
        return end;
    }


    public void setEnd(String end)
    {
        this.end = end;
    }


    public String getCost()
    {
        return cost;
    }


    public void setCost(String cost)
    {
        this.cost = cost;
    }


	public String getEntityLabel() {
		return entityLabel;
	}


	public void setEntityLabel(String entityLabel) {
		this.entityLabel = entityLabel;
	}


	public String getInternalType() {
		return internalType;
	}


	public void setInternalType(String internalType) {
		this.internalType = internalType;
	}
}
