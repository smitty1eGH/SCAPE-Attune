package com.bah.attune.data;

import java.util.List;
import java.util.Map;

public class DashboardEntity
{
    private String entity;
    private String icon;
    private String groupBy;
    private String alertCheck;
    private String alertValue;
    private String chartType;
    private String chartTypeValue;

    private List<Map<String, Object>> elementList;
    private List<NameValuePair> groupByList;
    private String[] displayList;
    private String[] fieldList;
    private String[] nonDisplayList;
    private List<String> fieldValueList;

    public String getEntity()
    {
        return entity;
    }

    public void setEntity(String entity)
    {
        this.entity = entity;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public String getGroupBy()
    {
        return groupBy;
    }

    public void setGroupBy(String groupBy)
    {
        this.groupBy = groupBy;
    }

    public String getAlertCheck()
    {
        return alertCheck;
    }

    public void setAlertCheck(String alertCheck)
    {
        this.alertCheck = alertCheck;
    }

    public String getAlertValue()
    {
        return alertValue;
    }

    public void setAlertValue(String alertValue)
    {
        this.alertValue = alertValue;
    }

    public String getChartType()
    {
        return chartType;
    }

    public void setChartType(String chartType)
    {
        this.chartType = chartType;
    }

    public String getChartTypeValue()
    {
        return chartTypeValue;
    }

    public void setChartTypeValue(String chartTypeValue)
    {
        this.chartTypeValue = chartTypeValue;
    }

    public List<Map<String, Object>> getElementList()
    {
        return elementList;
    }

    public void setElementList(List<Map<String, Object>> elementList)
    {
        this.elementList = elementList;
    }

    public List<NameValuePair> getGroupByList()
    {
        return groupByList;
    }

    public void setGroupByList(List<NameValuePair> groupByList)
    {
        this.groupByList = groupByList;
    }

    public String[] getDisplayList()
    {
        return displayList;
    }

    public void setDisplayList(String[] displayList)
    {
        this.displayList = displayList;
    }

    public String[] getFieldList()
    {
        return fieldList;
    }

    public void setFieldList(String[] fieldList)
    {
        this.fieldList = fieldList;
    }

    public List<String> getFieldValueList()
    {
        return fieldValueList;
    }

    public String[] getNonDisplayList()
    {
        return nonDisplayList;
    }

    public void setFieldValueList(List<String> fieldValueList)
    {
        this.fieldValueList = fieldValueList;
    }

    public void setNonDisplayList(String[] strings)
    {
        this.nonDisplayList = strings;
    }
}
