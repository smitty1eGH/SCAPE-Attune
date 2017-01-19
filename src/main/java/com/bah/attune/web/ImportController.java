package com.bah.attune.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.bah.attune.data.AttuneException;
import com.bah.attune.service.ImportService;

@Controller public class ImportController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportController.class);

    @Autowired private ImportService importService;


    @RequestMapping("dataImport.exec")
    public String dataImport()
    {
        return "dataImport";
    }


    @RequestMapping(value = "upload.exec", method = RequestMethod.POST)
    public @ResponseBody List<String> upload(@RequestParam("file[]") MultipartFile file) throws AttuneException
    {
        try
        {
            if ( file.getOriginalFilename().endsWith(".xls") )
            {
                Workbook wb = WorkbookFactory.create(file.getInputStream());
                return importService.importDataSheet(wb);
            }
            else
            {
                List<String> errorMessages = new ArrayList<String>();
                errorMessages.add(
                        "Due to security risks, only the '.xls' file extension is supported. Please save the workbook as an '.xls' file and re-import.");
                return errorMessages;
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Error occured in upload", e);
            throw new AttuneException("Error occured in upload, Error:" + e.toString());
        }
    }


    private void handleResourceUpload(MultipartFile zipFile) throws IOException
    {
        int index = zipFile.getOriginalFilename().indexOf(".");
        String resource = zipFile.getOriginalFilename().substring(0, index);

        // remove the last digit
        if ( resource.matches("^.+?\\d$") )
            resource = resource.substring(0, resource.length() - 1);

        File rootDir = new File(System.getProperty("attune.root") + "/" + resource);
        if ( !rootDir.exists() )
            rootDir.mkdir();

        ZipEntry entry;
        ZipInputStream zip = new ZipInputStream(zipFile.getInputStream());

        while ((entry = zip.getNextEntry()) != null)
        {
            String fileName = entry.getName();
            byte[] fileContent = IOUtils.toByteArray(zip);

            File file = new File(rootDir + "/" + fileName);

            if ( file.exists() )
                file.delete();

            if ( file.createNewFile() )
            {
                FileOutputStream fout = new FileOutputStream(file);
                fout.write(fileContent);
            }
        }

        IOUtils.closeQuietly(zip);
    }

}
