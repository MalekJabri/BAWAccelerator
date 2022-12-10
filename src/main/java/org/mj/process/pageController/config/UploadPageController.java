package org.mj.process.pageController.config;

import org.mj.process.model.ConnectionRequest;
import org.mj.process.model.DataMining;
import org.mj.process.model.DocumentRequest;
import org.mj.process.service.ContentService;
import org.mj.process.service.DataProcessingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

@Controller
public class UploadPageController {
    private static Logger logger = Logger.getLogger(UploadPageController.class.getName());
    @Value("${spring.servlet.multipart.location}")
    private String path;
    @Value("${process-mining.default-value}")
    private boolean initValue;

    public static ConnectionRequest getConnectionRequest() {
        ConnectionRequest connectionRequest;
        connectionRequest = new ConnectionRequest();
        connectionRequest.getFileNetServerRequest().setServer("baw.malek-jabri-s-account.cloud");
        connectionRequest.getFileNetServerRequest().setPassword("!Filenet1!");
        connectionRequest.getFileNetServerRequest().setPort("9080");
        connectionRequest.getFileNetServerRequest().setUser("dadmin");
        connectionRequest.getFileNetServerRequest().setRepository("TOS");
        connectionRequest.getProcessMiningServerRequest().setUrl("https://raffaello.my-invenio.com");
        connectionRequest.getProcessMiningServerRequest().setUserID("malek.jabri");
        connectionRequest.getProcessMiningServerRequest().setApikey("fct2ul8nt4otv1kg");
        return connectionRequest;
    }
    
    @GetMapping("/uploadFile")
    public String getUpload(HttpServletRequest request, HttpServletResponse response, Model model) {
        initPage(model);
        return "uploadDocument";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(HttpSession session, Model model, DocumentRequest documentRequest, @RequestParam("document") MultipartFile document) {
        logger.info("Document will be uploaded");
        logger.info("Information : " + documentRequest);
        if (document != null && document.getSize() > 0) {
            logger.info("Download the file and save it to a path: " + path);
            documentRequest.setFilePath(ContentService.storeDocument("test", document, path, "csv"));
        } else if (documentRequest.getFilePath() == null || documentRequest.getFilePath().isEmpty()) {
            model.addAttribute("csvImport", new DocumentRequest());
            model.addAttribute("message", "Please complete the path or add a file");
            model.addAttribute("classAlert", "alert alert-warning");
            model.addAttribute("displayMessage", "");
            return "uploadDocument";
        }

        try {
            DataProcessingService dataProcessingService = new DataProcessingService();
            DataMining dataMining = dataProcessingService.GetContentProcess(documentRequest.getFilePath(), documentRequest.getDelimiter());
            dataMining = null;
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("csvImport", new DocumentRequest());
            model.addAttribute("message", "Issue with the file! please check the logs");
            model.addAttribute("classAlert", "alert alert-warning");
            model.addAttribute("displayMessage", "");
            return "uploadDocument";
        }
        // Complete the variables
        ConnectionRequest connectionRequest;
        if (initValue) {
            connectionRequest = getConnectionRequest();
        } else connectionRequest = new ConnectionRequest();
        session.setAttribute("documentRequest", documentRequest);
        model.addAttribute("connectionRequest", connectionRequest);
        model.addAttribute("message", "Upload has been done correctly");
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "display:none");
        return "connectToServer";
    }

    private Model initPage(Model model) {
        DocumentRequest documentRequest = new DocumentRequest();
        if (initValue) {
            documentRequest.setFilePath("/Users/jabrimalek/Project/lalux/History.csv");
            documentRequest.setDelimiter(";");
            documentRequest.setCleanDate(true);
            documentRequest.setCleanIDAttribute(true);
        }
        model.addAttribute("csvImport", documentRequest);
        model.addAttribute("message", "");
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "display:none");
        model.addAttribute("position", 1);
        return model;
    }
}
