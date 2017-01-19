package com.bah.attune.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bah.attune.data.AttuneException;
import com.bah.attune.data.LinkNode;
import com.bah.attune.service.MainService;
import com.bah.attune.service.TraceabilityService;

@Controller public class TraceabilityController
{
    @Autowired private TraceabilityService service;

    @Autowired private MainService mainService;


    @RequestMapping("traceability.exec")
    public String traceability() throws AttuneException
    {
        return "traceability";
    }


    @RequestMapping("linkNode.exec")
    public String linkNode() throws AttuneException
    {
        return "linkNode";
    }


    @RequestMapping("getLinkNodeData.exec")
    public @ResponseBody LinkNode getLinkNodeData(@RequestParam String id, @RequestParam String label,
            @RequestParam String name, @RequestParam int levels) throws AttuneException
    {
        return (LinkNode) service.invokeDao("getLinkNodeData", id, label, name, levels);
    }


    @RequestMapping("getLinkNodeGaps.exec")
    public @ResponseBody LinkNode getLinkNodeGaps() throws AttuneException
    {
        return (LinkNode) service.invokeDao("getLinkNodeGaps");
    }


    @RequestMapping("getSearchSuggestions.exec")
    public @ResponseBody List<String> getSearchSuggestions(@RequestParam String search) throws AttuneException
    {
        return (List<String>) mainService.invokeDao("getSearchResults", search);
    }


    @RequestMapping("getNodePath.exec")
    public @ResponseBody LinkNode getNodePath(@RequestParam String nodeName) throws AttuneException
    {
        return (LinkNode) service.invokeDao("getNodePath", nodeName);
    }
}
