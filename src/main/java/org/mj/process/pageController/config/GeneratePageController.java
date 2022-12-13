package org.mj.process.pageController.config;

import org.mj.process.pageController.config.baw.CaseServerPageController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/download")
    public ResponseEntity downloadFileFromLocal(HttpSession session) throws FileNotFoundException, MalformedURLException {
        String finalDoc = (String) session.getAttribute("finalDoc");
        Path path = Paths.get(finalDoc);
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
