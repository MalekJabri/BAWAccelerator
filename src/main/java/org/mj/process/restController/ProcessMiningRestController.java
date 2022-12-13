package org.mj.process.restController;

import com.ibm.mj.ApiClient;
import com.ibm.mj.processmining.model.ProcessInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mj.process.model.Attribute;
import org.mj.process.model.ConnectionRequest;
import org.mj.process.service.ProcessMiningService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "/api/core")
@Tag(name = "Process Mining controller", description = "the CaseController API")
public class ProcessMiningRestController {
    private static Logger logger = Logger.getLogger(ProcessMiningRestController.class.getName());

    private static void extracted(String orgID, List<Attribute> projects, List<ProcessInfo> projectsInfo) {
        projectsInfo.forEach((project) -> {
            if (project.getOrganization().equals(orgID)) {
                projects.add(new Attribute(project.getProjectName(), project.getProjectTitle()));
            }
        });
    }

    @GetMapping(path = "/getProjects")
    public List<Attribute> listProjectsForOrg(HttpSession session, @RequestParam(name = "solution", required = true) String orgID) {
        logger.info("The list of project  for an organisation  :" + orgID);
        List<Attribute> projects = new ArrayList<>();
        List<ProcessInfo> projectsInfo;
        ConnectionRequest connectionRequest = (ConnectionRequest) session.getAttribute("connectionRequest");
        if (connectionRequest == null) {
            logger.warning("The connection request is empty");
        } else {
            projectsInfo = (List<ProcessInfo>) session.getAttribute("projectsInfo");
            if (projectsInfo != null) {
                extracted(orgID, projects, projectsInfo);
            } else {
                logger.info("The connection request has been found");
                ProcessMiningService processMiningService = new ProcessMiningService();
                ApiClient apiClient = processMiningService.getDefaultClient(connectionRequest.getProcessMiningServerRequest());
                projectsInfo = processMiningService.listProjects(apiClient);
                session.setAttribute("projectsInfo", projectsInfo);
                extracted(orgID, projects, projectsInfo);
                logger.info("the projects are " + projects);
            }
        }
        return projects;
    }
}
