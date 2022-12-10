package org.mj.process.pageController;

import org.mj.process.model.DocumentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class StartingPageController {

    @Value("${process-mining.default-value}")
    private boolean initValue;

    @GetMapping("/")
    public String startingPage(HttpServletRequest request, HttpServletResponse response, Model model) {
        initPage(model);
        return "choice";
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
