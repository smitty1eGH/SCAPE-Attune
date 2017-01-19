package com.bah.attune.web;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bah.attune.data.AttuneException;
import com.bah.attune.data.NameValuePair;
import com.bah.attune.data.TimelineData;
import com.bah.attune.data.TimelineModel;
import com.bah.attune.service.MainService;
import com.bah.attune.service.TimelineService;

@Controller
@SuppressWarnings("unchecked") 
public class TimelineController 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TimelineController.class);
    
	@Autowired
	private TimelineService timelineService;
	
	@Autowired
	private MainService mainService;

	@RequestMapping("timeline.exec")
	public String timeline(Model model, @RequestParam String entity) throws AttuneException
	{
		prepareTimeline(model, entity);

		return "timeline";
	}

	private void prepareTimeline(Model model, String selectedEntity) throws AttuneException
	{
		String entity = (String) timelineService.invokeDao("getSelectedEntity");
		
		model.addAttribute("selectedEntity", entity);
		
		model.addAttribute("parents", timelineService.invokeDao("getParents", entity));
		model.addAttribute("children", timelineService.invokeDao("getChildren", entity));
		model.addAttribute("selectedEntityCount", timelineService.invokeDao("getEntityCount", entity));
		
		model.addAttribute("orderedList", timelineService.invokeDao("getOrderedList"));
	}
	
	/****** Ajax ******/
	
	@RequestMapping("getTimelineModel.exec")
	public @ResponseBody TimelineModel getTimelineModel() throws AttuneException
	{
		TimelineModel model = new TimelineModel();
		
		model.setSelectedEntity((NameValuePair) timelineService.invokeDao("getSelectedEntityAndCount")); 
		model.setEventCount((int) mainService.invokeDao("getEntityCount", "TimelineEvent"));
		model.setTotalBudget((int) timelineService.invokeDao("getTotalBudget"));
		
		TimelineData timelineData = (TimelineData) timelineService.invokeDao("getTimelineData");
		
		model.setTimelineData(timelineData);
		model.setBudgetData((List<NameValuePair>) timelineService.invokeDao("getBudgetsByFiscalYear"));
		
		return model;
	}
	
	
	@RequestMapping("getTimelinePDF.exec")
	public void getTimelinePDF(@RequestParam String html, HttpServletResponse response) throws AttuneException
	{
	    try
	    {
    		byte[] content = timelineService.createPDFinMemory(html);
    		InputStream pdf = new ByteArrayInputStream(content);
    		
    		String fileName = "timeline-" + System.nanoTime() + ".pdf";
    		
    		response.setContentType("application/pdf");
    		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
    
    		ServletOutputStream output = response.getOutputStream();
    		IOUtils.copy(pdf, output);
    		response.flushBuffer();
    		output.close();	
	    }
	    catch(Exception e)
	    {
            LOGGER.error("Error occured in getTimelinePDF ", e);
            throw new AttuneException("Error occured in getTimelinePDF, Parameters: " + html + ", Error:" + e.toString());
	    }
	}
	
	
	@RequestMapping(value="saveTimeline.exec", method=RequestMethod.POST)
	public @ResponseBody boolean saveTimeline(@RequestBody TimelineData timelineData) throws AttuneException
	{
		return timelineService.saveTimelineData(timelineData);
	}
	
}
