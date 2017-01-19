package com.bah.attune.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bah.attune.data.AnalysisParameters;
import com.bah.attune.data.AnalysisResult;
import com.bah.attune.data.AttuneException;
import com.bah.attune.data.NameValuePair;
import com.bah.attune.service.DataAnalaysisService;
import com.bah.attune.service.MainService;

@Controller
public class DataAnalysisController 
{
	@Autowired
	private DataAnalaysisService service;
	
	@Autowired
	private MainService mainService;
	
	@RequestMapping("dataAnalysis.exec")
	public String dataAnalysis(Model model) throws AttuneException 
	{
	    model.addAttribute("entities", (List<NameValuePair>) mainService.invokeDao("getEntityList"));
		
		return "dataAnalysis";
	}

	
	@RequestMapping(value="runAnalysis.exec", method=RequestMethod.POST) 
	public String runAnalysis(@RequestBody AnalysisParameters analysisParameters, Model model) throws AttuneException
	{
		model.addAttribute("analysisResult", (AnalysisResult) service.invokeDao("getAnalysisResults", analysisParameters));
		return "dataAnalysisResults";
	}
}