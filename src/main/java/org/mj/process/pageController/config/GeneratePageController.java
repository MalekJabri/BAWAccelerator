package org.mj.process.pageController.config;

import org.mj.process.model.ConfigurationRequest;
import org.mj.process.model.DocumentRequest;
import org.mj.process.model.FileNetServerRequest;
import org.mj.process.pageController.config.baw.CaseServerPageController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

@Controller
public class GeneratePageController {

    private static Logger logger = Logger.getLogger(CaseServerPageController.class.getName());
    @Value("${spring.servlet.multipart.location}")
    private String pathDocument;
    @Value("${process-mining.default-value}")
    private boolean initValue;

    @PostMapping("/generateFile")
    public String generateFile(HttpServletRequest request, HttpServletResponse response, Model model, FileNetServerRequest filenetServerRequest) {
        logger.info("Filenet " + filenetServerRequest);
        model.addAttribute("message", "Connection to server successful");
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "");
        model.addAttribute("position", 3);
        return "final";
    }

    @GetMapping("/download")
    public ResponseEntity downloadFileFromLocal(HttpSession session) throws FileNotFoundException, MalformedURLException {
        DocumentRequest documentRequest = (DocumentRequest) session.getAttribute("documentRequest");
        ConfigurationRequest configurationRequest = (ConfigurationRequest) session.getAttribute("ConfigurationRequest");
        Path path = Paths.get(documentRequest.getFilePath());
        Resource resource = null;
        File file = new File(path.toUri());
        resource = new UrlResource(path.toUri());
        final InputStream inputStream = new FileInputStream(file);
        final InputStreamResource resourcef = new InputStreamResource(inputStream);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentLength((int) file.length())
                .body(resourcef);

    }
}
