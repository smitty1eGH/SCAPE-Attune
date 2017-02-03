package com.bah.attune.web;

import java.io.File;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bah.attune.data.AttuneException;
import com.bah.attune.data.MetadataModelJson;
import com.bah.attune.service.MainService;
import com.bah.attune.service.SecurityService;

@Controller public class MainController
{
    @Autowired private MainService service;


    @RequestMapping("login.exec")
    public String login()
    {
        return "login";
    }


    @RequestMapping("main.exec")
    public String main(Model model) throws AttuneException
    {
        model.addAttribute("username", SecurityService.getUserName());
        model.addAttribute("timelineCreated",  (boolean)service.invokeDao("timelineCreated"));

        File logo = new File(System.getProperty("attune.root") + "/icons/logo.png");

        if ( logo.exists() )
            model.addAttribute("logo", true);

        return "main";
    }


    @RequestMapping("metadata.exec")
    public String metadata()
    {
        return "metadata";
    }


    @RequestMapping("getMetadata.exec")
    @ResponseBody
    public MetadataModelJson getMetadata()
    {
        return service.getMetadata();
    }

    @RequestMapping("getTimelineCreated.exec")
    @ResponseBody
    public boolean getTimelineCreated() throws AttuneException
    {
        return (boolean)service.invokeDao("timelineCreated");
    }

    @RequestMapping("latestUpdates.exec")
    public String latestUpdates()
    {
        return "latestUpdates";
    }

  /*  @RequestMapping("latestUpdates.exec")
    public String latestUpdates()
    {
        return "latestUpdates";
    } */
}
