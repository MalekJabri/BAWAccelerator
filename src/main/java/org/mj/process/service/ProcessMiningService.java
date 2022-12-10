package org.mj.process.service;


import com.ibm.mj.ApiClient;
import com.ibm.mj.processmining.AuthorizationApi;
import com.ibm.mj.processmining.ProcessInquiryManagementApi;
import com.ibm.mj.processmining.model.*;
import org.mj.process.model.Attribute;
import org.mj.process.model.ProcessMiningServerRequest;
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
        ProcessMiningServerRequest processMiningServerRequest = new ProcessMiningServerRequest();
        processMiningServerRequest.setUrl("https://raffaello.my-invenio.com");
        processMiningServerRequest.setUserID("malek.jabri");
        processMiningServerRequest.setApikey("fct2ul8nt4otv1kg");
        ApiClient apiClient = processMiningService.getDefaultClient(processMiningServerRequest);
        List<Attribute> orgs = processMiningService.getOrg(apiClient);
        logger.info("The number of organisation are " + orgs.size());
        processMiningService.createNewProject("MalSecond6", "465d35ee", apiClient);
        List<ProcessInfo> infos = processMiningService.listProjects(apiClient);
    }

    public ApiClient getDefaultClient(ProcessMiningServerRequest processMiningServerRequest) {
        ApiClient defaultClient = new ApiClient();
        defaultClient.setBasePath(processMiningServerRequest.getUrl());
        AuthorizationApi apiInstance = new AuthorizationApi(defaultClient);
        TokenRequest token = new TokenRequest();
        token.setApiKey(processMiningServerRequest.getApikey());
        token.setUid(processMiningServerRequest.getUserID());
        SignResponse result = apiInstance.createToken(token);
        logger.info("Token  " + result.getSign());
        logger.info("Authentication to the server for " + processMiningServerRequest.getUserID() + " is " + result.getSuccess());
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

    public List<Attribute> getOrg(ApiClient apiClient) {
        List<Attribute> attributes = new ArrayList<>();
        Set<String> sets = new HashSet<>();
        for (ProcessInfo processInfo : listProjects(apiClient)) {
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
        logger.info("Project information :" + project);
        return project.getSuccess();
    }

    /*
     Upload the data for a project using a path
     */
    public boolean uploadData(ApiClient apiClient, String projectID, String OrgID, String caseType, String path) {
        StringResponse result = new StringResponse();
        result.setSuccess(false);
        ProcessInquiryManagementApi processInquiryManagementApi = new ProcessInquiryManagementApi(apiClient);
        String projectKey = projectID;
        String org = OrgID;
        Boolean forUpdate = true;
        Boolean dataSetOverride = false;
        String description = "Process flow the case type " + caseType;
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
