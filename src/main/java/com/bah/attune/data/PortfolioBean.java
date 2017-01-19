package com.bah.attune.data;

import java.util.List;

public class PortfolioBean
{
    private String name;
    private String label;
    private boolean isGap;

    private List<PortfolioBean> children;
    private List<NameValuePair> counts;


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public String getLabel()
    {
        return label;
    }


    public void setLabel(String label)
    {
        this.label = label;
    }


    public boolean getIsGap()
    {
        return isGap;
    }


    public void setIsGap(boolean isGap)
    {
        this.isGap = isGap;
    }


    public List<PortfolioBean> getChildren()
    {
        return children;
    }


    public void setChildren(List<PortfolioBean> children)
    {
        this.children = children;
    }


    public List<NameValuePair> getCounts()
    {
        return counts;
    }


    public void setCounts(List<NameValuePair> counts)
    {
        this.counts = counts;
    }
}
