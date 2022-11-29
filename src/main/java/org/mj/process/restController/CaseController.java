package org.mj.process.restController;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mj.process.model.ServerECM;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/core")
@Tag(name = "CaseController", description = "the CaseController API")
public class CaseController {

    @PostMapping(path = "/listSolutions")
    public List<String> listSolutions(HttpServletResponse response, @Parameter(description = "Message details", required = true) @RequestBody ServerECM serverECM) {
        List<String> solutions = new ArrayList<>();
        return solutions;
    }


}
