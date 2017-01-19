package com.bah.attune.data;

import java.util.ArrayList;
import java.util.List;

public class LinkNode
{
    private String id;
    private String name;
    private String type;

    private List<NameValuePair> data;
    private List<LinkNode> children;

    public LinkNode()
    {
    }

    public LinkNode(String id, String name)
    {
        super();
        this.id = id;
        this.name = name;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<NameValuePair> getData()
    {
        return data;
    }

    public void setData(List<NameValuePair> data)
    {
        this.data = data;
    }

    public void addData(NameValuePair data)
    {
        if ( this.data != null )
            this.data.add(data);
        else
        {
            this.data = new ArrayList<NameValuePair>();
            this.data.add(data);
        }
    }

    public String getDataValueByName(String name)
    {
        for (NameValuePair pair : this.data)
            if ( pair.getName().equals(name) )
                return pair.getValue();

        return null;
    }

    public List<LinkNode> getChildren()
    {
        return children;
    }

    public void setChildren(List<LinkNode> children)
    {
        this.children = children;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
