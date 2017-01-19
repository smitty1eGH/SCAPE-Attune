package com.bah.attune.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bah.attune.data.AttuneException;
import com.bah.attune.data.DashboardEntity;
import com.bah.attune.data.NameValuePair;
import com.bah.attune.service.MainService;

@SuppressWarnings("unchecked")
@Controller
public class DashboardController
{
    public static final Map<String, String> chartTypeMap = new HashMap<String, String>();

    static
    {
        chartTypeMap.put("Pie Chart", "pie");
        chartTypeMap.put("Donut Chart", "donut");
        chartTypeMap.put("Bar Chart", "bar");
        chartTypeMap.put("Horizontal Bar Chart", "bar");
        chartTypeMap.put("Line Chart", "line");
    }

    @Autowired
    private MainService service;

    @RequestMapping("dashboard.exec")
    public String dashboard(Model model) throws AttuneException
    {
        prepareDashboard(model);

        return "dashboard";
    }
    
    
    @RequestMapping("configDashboard.exec")
    public String configDashboard(Model model) throws AttuneException
    {
        prepareDashboard(model);
        model.addAttribute("allEntityList", service.invokeDao("getEntityList"));

        String[] allChartTypeList = new String[]
        { "Pie Chart", "Donut Chart", "Bar Chart", "Horizontal Bar Chart", "Line Chart" };
        model.addAttribute("allChartTypeList", allChartTypeList);

        return "configDashboard";
    }
    
    
    @RequestMapping(value = "saveDashboard.exec", method = RequestMethod.POST)
    @ResponseBody 
    public Integer saveDashboard(@RequestBody String[] list)
    {
        service.saveDashboard(list);

        return 0;
    }

    // --------------------------------------- ajax -------------------------------------------------------------
    @RequestMapping("getFieldList.exec")
    @ResponseBody 
    public String[] getFieldList(@RequestParam String entity) throws AttuneException
    {
        return (String[]) service.invokeDao("getFieldList", entity);
    }
    

    @RequestMapping("getValueList.exec")
    @ResponseBody 
    public List<String> getValueList(@RequestParam String entity, @RequestParam String field) throws AttuneException
    {
        return (List<String>) service.invokeDao("getValueList", entity, field);
    }

    private void prepareDashboard(Model model) throws AttuneException
    {
        List<DashboardEntity> dashboardEntityList = new ArrayList<DashboardEntity>();
        List<Map<String, Object>> dashboardList = (List<Map<String, Object>>) service.invokeDao("loadAll", "Dashboard");
        for (Map<String, Object> map : dashboardList)
        {
            DashboardEntity dashboardEntity = new DashboardEntity();

            String entity = (String) map.get("entity");
            String groupBy = (String) map.get("groupBy");
            dashboardEntity.setEntity(entity);
            dashboardEntity.setGroupBy(groupBy);
            dashboardEntity.setAlertCheck((String) map.get("alertCheck"));
            dashboardEntity.setAlertValue((String) map.get("alertValue"));
            dashboardEntity.setChartType((String) map.get("chartType"));
            dashboardEntity.setChartTypeValue(chartTypeMap.get((String) map.get("chartType")));

            dashboardEntity.setElementList((List<Map<String, Object>>) service.invokeDao("loadEntity", entity));
            dashboardEntity.setGroupByList((List<NameValuePair>) service.invokeDao("getGroupByList", entity, groupBy));

            String[] fieldList = (String[]) service.invokeDao("getFieldList", entity);
            String[] displayList = (String[]) service.invokeDao("getDisplayList", entity);
            Collection<String> nonDisplayList = CollectionUtils.disjunction(Arrays.asList(fieldList),
                    Arrays.asList(displayList));
            dashboardEntity.setFieldList(fieldList);
            dashboardEntity.setDisplayList(displayList);
            dashboardEntity.setNonDisplayList(nonDisplayList.toArray(new String[nonDisplayList.size()]));
            dashboardEntity.setFieldValueList((List<String>) service.invokeDao("getValueList", entity, groupBy));
            dashboardEntityList.add(dashboardEntity);
        }
        model.addAttribute("dashboardEntityList", dashboardEntityList);
    }
}
