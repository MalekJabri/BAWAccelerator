package org.mj.process.pageController.query;

import org.mj.process.model.ConfigurationRequest;
import org.mj.process.model.ConnectionRequest;
import org.mj.process.service.CaseTypeService;
import org.mj.process.service.ServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

import static org.mj.process.pageController.config.UploadPageController.getConnectionRequest;
import static org.mj.process.pageController.config.baw.CaseServerPageController.getEventsType;

@Controller
public class CreateQueryPageController {

    private static Logger logger = Logger.getLogger(CreateQueryPageController.class.getName());
    @Value("${process-mining.default-value}")
    private boolean initValue;

    @GetMapping("/connectToServer")
    public String prepareConfServer(HttpSession session, HttpServletResponse response, Model model) {

        ConnectionRequest connectionRequest;
        if (session.getAttribute("connectionRequest") == null)
            if (initValue) {
                connectionRequest = getConnectionRequest();
            } else connectionRequest = new ConnectionRequest();
        else connectionRequest = (ConnectionRequest) session.getAttribute("connectionRequest");
        model.addAttribute("message", "");
        model.addAttribute("connectionRequest", connectionRequest);
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "display:none;");
        return "/query/connecToFilenet";
    }

    @PostMapping("/createQuery")
    public String connectToServer(HttpSession session, HttpServletResponse response, Model model, ConnectionRequest connectionRequest) {
        ServerConfig serverConfig;
        try {
            serverConfig = new ServerConfig(connectionRequest.getFileNetServerRequest());
            logger.info("The object store is  " + serverConfig.getOs().get_Name());
        } catch (Exception e) {
            model.addAttribute("message", "Connection to server failed, please verify parameters");
            model.addAttribute("connectionRequest", connectionRequest);
            model.addAttribute("classAlert", "alert alert-warning");
            model.addAttribute("displayMessage", "");
            return "/query/connecToFilenet";
        }
        try {
            CaseTypeService caseTypeService = new CaseTypeService(serverConfig);
            model.addAttribute("Solutions", caseTypeService.getAttributesSolutions());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Connection to server failed, please verify parameters");
            model.addAttribute("connectionRequest", connectionRequest);
            model.addAttribute("classAlert", "alert alert-warning");
            model.addAttribute("displayMessage", "");
            return "/query/connecToFilenet";
        }
        session.setAttribute("connectionRequest", connectionRequest);
        model.addAttribute("message", "Successful connection to Filenet / Process mining");
        model.addAttribute("configurationRequest", new ConfigurationRequest());
        model.addAttribute("Levels", getEventsType());
        model.addAttribute("classAlert", "alert alert-success");
        model.addAttribute("displayMessage", "");
        model.addAttribute("position", 2);
        model.addAttribute("displayProperty", "display:none");
        return "/query/configQuery";
    }

}
