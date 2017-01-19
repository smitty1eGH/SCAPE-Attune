package com.bah.attune.data;

import java.util.List;

public class TimelineModel
{
    private NameValuePair selectedEntity;
    private int eventCount;
    private String totalBudget;

    private TimelineData timelineData;

    private List<NameValuePair> budgetData;


    public NameValuePair getSelectedEntity()
    {
        return selectedEntity;
    }


    public void setSelectedEntity(NameValuePair selectedEntity)
    {
        this.selectedEntity = selectedEntity;
    }


    public int getEventCount()
    {
        return eventCount;
    }


    public void setEventCount(int eventCount)
    {
        this.eventCount = eventCount;
    }


    public String getTotalBudget()
    {
        return totalBudget;
    }


    public void setTotalBudget(String totalBudget)
    {
        this.totalBudget = totalBudget;
    }


    public void setTotalBudget(int totalBudget)
    {
        if ( totalBudget >= 1000 && totalBudget < 1000000 )
            this.totalBudget = "$" + (totalBudget / 1000) + "k";
        else if ( totalBudget >= 1000000 && totalBudget < 1000000000 )
            this.totalBudget = "$" + (totalBudget / 1000000) + "M";
        else if ( totalBudget >= 1000000000 )
            this.totalBudget = "$" + (totalBudget / 1000000000) + "B";
        else
            this.totalBudget = "$" + totalBudget;
    }


    public List<NameValuePair> getBudgetData()
    {
        return budgetData;
    }


    public void setBudgetData(List<NameValuePair> budgetData)
    {
        this.budgetData = budgetData;
    }


    public TimelineData getTimelineData()
    {
        return timelineData;
    }


    public void setTimelineData(TimelineData timelineData)
    {
        this.timelineData = timelineData;
    }
}
