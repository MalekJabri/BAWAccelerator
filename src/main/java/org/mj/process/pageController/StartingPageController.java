package org.mj.process.pageController;

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
        return "start";
    }
}
