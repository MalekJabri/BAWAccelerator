package org.mj.process.service;

import org.openapitools.client.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProcessMiningService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) {
        ProcessMiningService processMiningService = new ProcessMiningService();
        processMiningService.connectToProcessMining();
    }

    public void connectToProcessMining() {
        logger.info("Connect to Process mining using the following user ");
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath("https://raffaello.my-invenio.com");
        

    }

}
