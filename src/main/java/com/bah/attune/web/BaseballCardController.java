package com.bah.attune.web;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bah.attune.data.AttuneException;
import com.bah.attune.data.MetadataModelJson;
import com.bah.attune.service.BaseballCardService;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("unchecked") @Controller public class BaseballCardController
{
    @Autowired private BaseballCardService service;

    Logger log = Logger.getLogger(BaseballCardController.class.getName());

    @RequestMapping("baseballCard.exec")
    public String baseballCard(Model model, @RequestParam String entity, @RequestParam String name) throws AttuneException
    {
        model.addAttribute("entity", entity);
        model.addAttribute("name", name);

        return "baseballCard";
    }

    @RequestMapping("getAttributesMap.exec")
    @ResponseBody
    public Map<String, String> getAttributesMap(@RequestParam String entity, @RequestParam String name)
            throws AttuneException
    {
        return (Map<String, String>) service.invokeDao("getAttributesMap", entity, name);
    }


    @RequestMapping("getParentMap.exec")
    @ResponseBody
    public Map<String, String> getParentMap(@RequestParam String entity, @RequestParam String name)
            throws AttuneException
    {
        return (Map<String, String>) service.invokeDao("getParentMap", entity, name);
    }


    @RequestMapping("getChildrenMap.exec")
    @ResponseBody
    public Map<String, List<String>> getChildrenMap(@RequestParam String entity, @RequestParam String name)
            throws AttuneException
    {
        return (Map<String, List<String>>) service.invokeDao("getChildrenMap", entity, name);
    }


    @RequestMapping("getNetworkView.exec")
    @ResponseBody
    public MetadataModelJson getNetworkView(@RequestParam String entity, @RequestParam String name)
            throws AttuneException
    {
        return service.getNetworkView(entity, name);
    }

    @RequestMapping("getRatings.exec")
    @ResponseBody
    public Map<String, Map<String, String>> getRatings(@RequestParam String objectName)
            throws AttuneException
    {
        log.info("getRatings: " + objectName);
        return (Map<String, Map<String, String>>) service.invokeDao("getRatings", objectName);
    }

    @RequestMapping(value = "createRating.exec", method = RequestMethod.POST)
    @ResponseBody
    public String createRating(HttpServletRequest request) throws AttuneException
    {
        String entity = request.getParameter("entity");
        String user = request.getParameter("user");
        String rating = request.getParameter("rating");
        String title = request.getParameter("title");
        String comment = request.getParameter("comment");
        String objectName = request.getParameter("objectName");
        service.createRating(entity, user, rating, title, comment, objectName);

        return "created";
    }
}
