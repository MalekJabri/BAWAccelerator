package org.mj.process.pageController.config;

import org.mj.process.model.CaseHistory;
import org.mj.process.model.ConfigurationRequest;
import org.mj.process.model.DataMining;
import org.mj.process.model.DocumentRequest;
import org.mj.process.model.generic.Attribute;
import org.mj.process.model.servers.ConnectionRequest;
import org.mj.process.pageController.config.baw.CaseServerPageController;
import org.mj.process.service.ContentService;
import org.mj.process.service.DataProcessingService;
import org.mj.process.service.LocalPropertiesTools;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static org.mj.process.pageController.config.baw.CaseServerPageController.getEventsType;

@Controller
public class UploadPageController {
    private static Logger logger = Logger.getLogger(UploadPageController.class.getName());
    @Value("${spring.servlet.multipart.location}")
    private String path;
    @Value("${process-mining.default-value}")
    private boolean initValue;

    @Autowired
    private LocalPropertiesTools propertiesTools;


    @GetMapping("/uploadFile")
    public String getUpload(HttpServletRequest request, HttpServletResponse response, Model model) {
        DocumentRequest documentRequest = getDocumentRequest();
        model.addAttribute("csvImport", documentRequest);
        model.addAttribute("Levels", getEventsType());
        model.addAttribute("message", "");
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "display:none");
        model.addAttribute("position", 1);
        return "uploadDocument";
    }

    private DocumentRequest getDocumentRequest() {
        DocumentRequest documentRequest = new DocumentRequest();
        if (initValue) {
            documentRequest.setFilePath("/Users/jabrimalek/Project/lalux/BAW_DATA/CH_CASEHIST.csv");
            documentRequest.setDateFormat("yyyy-MM-dd-hh.mm.ss");
            documentRequest.setDelimiter(",");
            documentRequest.setCleanDate(true);
            documentRequest.setCleanIDAttribute(true);
            documentRequest.setAddInformation(false);
            documentRequest.setEncodedFormat("BASE64");
        }
        return documentRequest;
    }

    @PostMapping("/uploadFile")
    public String uploadFile(HttpSession session, Model model, DocumentRequest documentRequest, @RequestParam("document") MultipartFile document) {
        logger.info("Document will be uploaded");
        logger.info("Information : " + documentRequest);
        List<Attribute> caseTypes = new ArrayList<>();
        if (document != null && document.getSize() > 0) {
            logger.info("Download the file and save it to a path: " + path);
            documentRequest.setFilePath(ContentService.storeDocument("test", document, path, "csv"));
        } else if (documentRequest.getFilePath() == null || documentRequest.getFilePath().isEmpty()) {
            model.addAttribute("csvImport", documentRequest);
            model.addAttribute("Levels", getEventsType());
            model.addAttribute("message", "Please complete the path or add a file");
            model.addAttribute("classAlert", "alert alert-warning");
            model.addAttribute("displayMessage", "");
            return "uploadDocument";
        }
        try {
            DataProcessingService dataProcessingService = new DataProcessingService();
            DataMining dataMining = dataProcessingService.GetContentProcess(documentRequest.getFilePath(), documentRequest.getDelimiter());
            CaseHistory caseHistory = new CaseHistory();
            System.out.println(Arrays.toString(dataMining.getHeaders()));
            caseHistory.analyse(dataMining, documentRequest);
            caseTypes = caseHistory.getCaseAttribute();
            dataMining = null;
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("csvImport", documentRequest);
            model.addAttribute("Levels", getEventsType());
            model.addAttribute("message", "Issue with the CSV! please check the logs : " + e.getLocalizedMessage());
            model.addAttribute("classAlert", "alert alert-warning");
            model.addAttribute("displayMessage", "");
            return "uploadDocument";
        }
        // Complete the variables
        session.setAttribute("documentRequest", documentRequest);
        if (documentRequest.isAddInformation()) {
            ConnectionRequest connectionRequest = new ConnectionRequest();
            if (initValue) {
                connectionRequest = propertiesTools.getConnectionRequest();
                CaseServerPageController caseServerPageController = new CaseServerPageController();
                if (connectionRequest.getBawContentServer() != null)
                    return caseServerPageController.testConnection(session, model, connectionRequest);
            }
            model.addAttribute("connectionRequest", connectionRequest);
            model.addAttribute("message", "Upload has been done correctly");
            model.addAttribute("classAlert", "alert alert-success");
            model.addAttribute("displayMessage", "display:none");
            return "bawCase/connectToServer";
        } else {
            model.addAttribute("configurationRequest", new ConfigurationRequest());
            model.addAttribute("Levels", getEventsType());
            model.addAttribute("CaseTypes", caseTypes);
            model.addAttribute("message", "Upload has been done correctly");
            model.addAttribute("classAlert", "alert alert-success");
            model.addAttribute("displayMessage", "display:none");
            return "configuration";
        }
    }


}
