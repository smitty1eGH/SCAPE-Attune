package com.bah.attune.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.bah.attune.dao.BaseDao;
import com.bah.attune.dao.TimelineDao;
import com.bah.attune.data.AttuneException;
import com.bah.attune.data.TimelineData;
import com.bah.attune.data.TimelineEvent;

@Service public class TimelineService extends BaseService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TimelineService.class);

    @Autowired private TimelineDao dao;


    @Override
    public BaseDao getDao()
    {
        return dao;
    }


    public byte[] createPDFinMemory(String htmlContent) throws AttuneException
    {
        try
        {
            // Build the html
            StringBuilder htmlBuilder = buildHtml(htmlContent);

            // Create the pdf document, and save it to the images directory
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(new StringBufferInputStream(htmlBuilder.toString()));

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocument(document, "");

            OutputStream output = new ByteArrayOutputStream();
            renderer.layout();
            renderer.createPDF(output);
            output.close();

            return ((ByteArrayOutputStream) output).toByteArray();
        }
        catch (Exception e)
        {
            LOGGER.error("Error occured in createPDFinMemory ", e);
            throw new AttuneException(
                    "Error occured in createPDFinMemory, Parameters:" + htmlContent + " , Error:" + e.toString());
        }
    }


    private StringBuilder buildHtml(String htmlContent) throws IOException
    {
        StringBuilder htmlBuilder = new StringBuilder();
        String root = System.getProperty("attune.root");

        htmlBuilder.append("<html>\n");
        htmlBuilder.append("	<head>\n");
        htmlBuilder.append("		<title>Timeline</title>\n");
        htmlBuilder.append("		<style>\n");

        for (String line : Files.readAllLines(Paths.get(root + "/css/vis.css"), StandardCharsets.UTF_8))
            htmlBuilder.append(line + "\n");

        for (String line : Files.readAllLines(Paths.get(root + "/css/timeline.css"), StandardCharsets.UTF_8))
            htmlBuilder.append(line + "\n");

        htmlBuilder.append("		</style>\n");
        htmlBuilder.append("	</head>\n");
        htmlBuilder.append("	<body>\n");
        htmlBuilder.append(htmlContent.replaceAll("&nbsp", " "));
        htmlBuilder.append("	</body>\n");
        htmlBuilder.append("</html>");

        return htmlBuilder;
    }
    
    
    public boolean saveTimelineData(TimelineData timelineData)
    {
    	try
		{
    		for (TimelineEvent event: timelineData.getData())
        	{
        		dao.saveTimelineEvent(event);
        	}
		}
    	catch (Exception e)
    	{
    		System.out.println("Error");
    		return false;
    	}
    	
    	return true;
    }

}
