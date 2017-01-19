package com.bah.attune.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bah.attune.data.AttuneException;
import com.bah.attune.data.PortfolioBean;
import com.bah.attune.service.MainService;
import com.bah.attune.service.PortfolioService;

@SuppressWarnings("unchecked") @Controller public class PortfolioController
{
    @Autowired private MainService service;

    @Autowired private PortfolioService portfolioService;


    @RequestMapping("portfolio.exec")
    public String portfolio(Model model, @RequestParam(required = false) String chiclet,
                                         @RequestParam(required = false) String grouping, 
                                         @RequestParam(required = false) String content) throws AttuneException
    {
        preparePortfolio(model, chiclet, grouping, content);

        return "portfolio";
    }


    private void preparePortfolio(Model model, String selectedEntity, String groupingEntity, String contentEntity)
            throws AttuneException
    {
        String relationship = "";

        // List of available entities that the user can select from a dropdown
        List<String> entityList = (List<String>) portfolioService.invokeDao("getPortfolioEntityList");

        // Set the defaults if there is no selected entity
        if ( StringUtils.isEmpty(selectedEntity) )
        {
            // Use the last entity (could be any of them), and set the default
            // content
            selectedEntity = entityList.get(2);
            List<String> children = (List<String>) portfolioService.invokeDao("getChildEntitiesList", selectedEntity);
            if ( !children.isEmpty() )
                contentEntity = children.get(0);
            else
                contentEntity = "";
        }

        // Set a grouping if there was none specified
        if ( StringUtils.isEmpty(groupingEntity) )
            groupingEntity = (String) portfolioService.invokeDao("getDefaultGroupingByEntity", selectedEntity);

        relationship = (String) portfolioService.invokeDao("getRelationship", groupingEntity, selectedEntity);

        // List of portfolio beans to hold the main panel/chiclet information
        List<PortfolioBean> portfolioBeanList = (List<PortfolioBean>) portfolioService.invokeDao("getPortfolioBeanList",
                selectedEntity, groupingEntity, relationship, contentEntity);

        HashMap<String, Integer> countMap = new HashMap<String, Integer>();

        for (PortfolioBean bean : portfolioBeanList)
        {
            if ( bean.getChildren() == null || bean.getChildren().isEmpty() )
            {
                if ( bean.getIsGap() )
                    IncreaseCount(countMap, bean);
            }
            else
            {
                for (PortfolioBean child : bean.getChildren())
                {
                    if ( child.getChildren() == null || child.getChildren().isEmpty() )
                    {
                        if ( child.getIsGap() )
                            IncreaseCount(countMap, bean);
                    }
                    else
                    {
                        for (PortfolioBean grandChild : child.getChildren())
                        {
                            if ( grandChild.getIsGap() )
                                IncreaseCount(countMap, bean);
                        }
                    }
                }
            }
        }

        if ( countMap.size() > 0 )
        {
            int gapCount = 0;
            for (String name : countMap.keySet())
                gapCount += countMap.get(name);

            model.addAttribute("gapCount", gapCount);
        }

        model.addAttribute("selectedEntity", selectedEntity);
        model.addAttribute("selectedEntityCount", (Integer) service.invokeDao("getEntityCount", selectedEntity));

        // Only add the grouping entity if it's different than the selected
        // entity (entity could have no parents)
        if ( !selectedEntity.equals(groupingEntity) )
        {
            model.addAttribute("groupingEntity", groupingEntity);
            model.addAttribute("groupingEntityCount", (Integer) service.invokeDao("getEntityCount", groupingEntity));
        }

        // Only add the content entity if it exists (entity could have no
        // children)
        if ( contentEntity != null )
        {
            model.addAttribute("content", contentEntity);
            model.addAttribute("contentCount", (Integer) service.invokeDao("getEntityCount", contentEntity));
        }

        model.addAttribute("entityList", entityList);
        model.addAttribute("portfolioBeanList", portfolioBeanList);
        model.addAttribute("chicletRelation", relationship);

    }


    private void IncreaseCount(HashMap<String, Integer> countMap, PortfolioBean bean)
    {
        String name = bean.getName();
        Integer count = countMap.get(name);
        if ( count == null )
            count = 0;

        countMap.put(name, ++count);

    }


    @RequestMapping("portfolioCompare.exec")
    public String portfolioCompare(@RequestParam String entityOne,
                                                @RequestParam String nameOne, 
                                                @RequestParam String entityTwo, 
                                                @RequestParam String nameTwo, Model model) throws AttuneException
    {
        Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();

        Map<String, String> attributesMapOne = (Map<String, String>) portfolioService.invokeDao("getAttributesMap",
                entityOne, nameOne);
        Map<String, String> attributesMapTwo = (Map<String, String>) portfolioService.invokeDao("getAttributesMap",
                entityTwo, nameTwo);

        attributesMapOne.put("name", nameOne);
        attributesMapTwo.put("name", nameTwo);

        data.put("one", attributesMapOne);
        data.put("two", attributesMapTwo);
        
        model.addAttribute("data", data);

        return "comparison";
    }


    @RequestMapping("getPortfolioAttributesMap.exec")
    @ResponseBody
    public Map<String, String> getAttributesMap(@RequestParam String entity, @RequestParam String name)  throws AttuneException
    {
        return (Map<String, String>) service.invokeDao("getAttributesMap", entity, name);
    }


    @RequestMapping("getSubGroupingList.exec")
    @ResponseBody
    public List<String> getSubGroupingList(@RequestParam String entity) throws AttuneException
    {
        return (List<String>) portfolioService.invokeDao("getSubGroupingList", entity);
    }


    @RequestMapping("getChildList.exec")
    @ResponseBody
    public List<String> getChildList(@RequestParam String entity) throws AttuneException
    {
        return (List<String>) portfolioService.invokeDao("getChildList", entity);
    }
}
