package org.mj.process.service;


import com.ibm.mj.ApiClient;
import com.ibm.mj.processmining.AuthorizationApi;
import com.ibm.mj.processmining.ProcessInquiryManagementApi;
import com.ibm.mj.processmining.model.*;
import org.mj.process.model.generic.Attribute;
import org.mj.process.model.servers.ProcessMiningServer;
import org.mj.process.tools.JSONTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProcessMiningService {
    private static final Logger logger = LoggerFactory.getLogger(ProcessMiningService.class);

    public static void main(String[] args) {
        ProcessMiningService processMiningService = new ProcessMiningService();
        ProcessMiningServer processMiningServer = new ProcessMiningServer();
        processMiningServer.setUrl("https://raffaello.my-invenio.com");
        processMiningServer.setUserID("malek.jabri");
        processMiningServer.setApikey("fct2ul8nt4otv1kg");
        ApiClient apiClient = processMiningService.getDefaultClient(processMiningServer);
        processMiningService.uploadData(apiClient, "pqmalek", "465d35ee", "documents-compiled.csv", false);
    }

    public ApiClient getDefaultClient(ProcessMiningServer processMiningServer) {
        ApiClient defaultClient = new ApiClient();
        defaultClient.setBasePath(processMiningServer.getUrl());
        AuthorizationApi apiInstance = new AuthorizationApi(defaultClient);
        TokenRequest token = new TokenRequest();
        token.setApiKey(processMiningServer.getApikey());
        token.setUid(processMiningServer.getUserID());
        SignResponse result = apiInstance.createToken(token);
        logger.info("Token  " + result.getSign());
        logger.info("Authentication to the server for " + processMiningServer.getUserID() + " is " + result.getSuccess());
        defaultClient.setBearerToken(result.getSign());
        logger.info("Assigned bearer token to api client ");
        return defaultClient;
    }

    public List<ProcessInfo> listProjects(ApiClient apiClient) {
        logger.info("Connect to Process mining using the following user ");
        ProcessInquiryManagementApi processInquiryManagementApi = new ProcessInquiryManagementApi(apiClient);
        ProcessesResponse process = processInquiryManagementApi.getProcessList("", "", "");
        return process.getData();
    }

    public ProcessInfo getProject(ApiClient apiClient, String projectTitle) {
        ProcessInfo processInfo = null;
        logger.info("Connect to Process mining using the following user ");
        ProcessInquiryManagementApi processInquiryManagementApi = new ProcessInquiryManagementApi(apiClient);
        ProcessesResponse process = processInquiryManagementApi.getProcessList("", "", "");
        for (ProcessInfo info : process.getData()) if (info.getProjectTitle().equals(projectTitle)) return info;
        return null;
    }

    public List<Attribute> getOrg(List<ProcessInfo> processInfos) {
        List<Attribute> attributes = new ArrayList<>();
        Set<String> sets = new HashSet<>();
        for (ProcessInfo processInfo : processInfos) {
            if (sets.add(processInfo.getOrganization())) {
                Attribute attribute = new Attribute(processInfo.getOrganizationTitle(), processInfo.getOrganization());
                attributes.add(attribute);
                logger.info("Info: " + attribute);
            }
        }
        return attributes;
    }

    public boolean createNewProject(String projectName, String OrgID, ApiClient apiClient) {
        ProcessInquiryManagementApi processInquiryManagementApi = new ProcessInquiryManagementApi(apiClient);
        ProjectRefRequest projectRequest = new ProjectRefRequest();
        projectRequest.setTitle(projectName);
        projectRequest.setOrg(OrgID);
        ProcessResponse project = processInquiryManagementApi.createNewProject("", "", "", projectRequest);
        List<ProcessInfo> projects = processInquiryManagementApi.getProcessList(null, null, null).getData();
        projects.forEach(processInfo -> {
            logger.info(processInfo.getProjectName() + "-- " + processInfo.getProjectTitle());
        });
        logger.info("Project information :" + project);
        return project.getSuccess();
    }

    public void createLog(String projectKey, String org, ApiClient apiClient, String[] headers, boolean augmented, String dateFormat) {
        ProcessInquiryManagementApi processInquiryManagementApi = new ProcessInquiryManagementApi(apiClient);
        Boolean forUpdate = true;
        String mapping = JSONTool.getMappingInfo(headers, augmented, dateFormat);
        logger.info("Mapping details ");
        logger.info(mapping);
        StringResponse result = processInquiryManagementApi.performProcessMining(projectKey, org, null, null, null, forUpdate, mapping);
        logger.info("Result for the mapping " + result);
    }


    /*
     Upload the data for a project using a path
     */
    public boolean uploadData(ApiClient apiClient, String projectID, String OrgID, String path, boolean append) {
        StringResponse result = new StringResponse();
        result.setSuccess(false);
        ProcessInquiryManagementApi processInquiryManagementApi = new ProcessInquiryManagementApi(apiClient);
        String projectKey = projectID;
        String org = OrgID;
        Boolean forUpdate = true;
        Boolean dataSetOverride = !append;
        String description = "Process flow the case type ";
        File _file = new File(path); // File |
        try {
            result = processInquiryManagementApi.uploadDataSet(projectKey, org, null, null, null, forUpdate, dataSetOverride, description, _file);
        } catch (RestClientException e) {
            logger.error("Exception when calling ProcessInquiryManagementApi#uploadDataSet");
            logger.error("Status code: " + e.getMessage());
            logger.error("Reason: " + e.getCause());
            logger.error("Response headers: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return result.getSuccess();
    }
}
