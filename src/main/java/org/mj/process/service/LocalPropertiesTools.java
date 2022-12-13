package org.mj.process.service;

import org.mj.process.model.servers.ConnectionRequest;
import org.mj.process.model.servers.ProcessMiningServer;
import org.mj.process.model.servers.BAWContentServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Properties;
import java.util.logging.Logger;

@Service
public class LocalPropertiesTools {

    private static Logger logger = Logger.getLogger(LocalPropertiesTools.class.getName());
    @Value("${process-mining.config.file}")
    private String configPath;

    public static void main(String[] args) throws IOException {
        LocalPropertiesTools localPropertiesTools = new LocalPropertiesTools();
        String path = "config.properties";
        String path1 = "config1.properties";
        BAWContentServer bawServer = localPropertiesTools.getBAWServerAccess();
        ProcessMiningServer processMiningServer = localPropertiesTools.getProcessMiningServer();
        localPropertiesTools.saveBawConfig(bawServer);
        localPropertiesTools.saveProcessMining(processMiningServer);
    }

    public boolean checkFile() {
        File document = new File(configPath);
        return document.exists();
    }

    public ConnectionRequest getConnectionRequest() {
        ConnectionRequest connectionRequest = new ConnectionRequest();
        if (checkFile()) {
            connectionRequest.setBAWContentServer(getBAWServerAccess());
            connectionRequest.setProcessMiningServer(getProcessMiningServer());
        }
        return connectionRequest;
    }


    public BAWContentServer getBAWServerAccess() {
        BAWContentServer bawServer = new BAWContentServer();
        if (checkFile()) {
            try {
                InputStream input = new FileInputStream(configPath);
                Properties prop = new Properties();
                prop.load(input);
                bawServer.setServer(prop.getProperty("baw.content.server"));
                bawServer.setUser(prop.getProperty("baw.content.userName"));
                bawServer.setPassword(prop.getProperty("baw.content.password"));
                bawServer.setRepository(prop.getProperty("baw.content.os"));
                bawServer.setPort(prop.getProperty("baw.content.port"));
                logger.info("Filenet Configuration " + bawServer);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return bawServer;
    }

    public ProcessMiningServer getProcessMiningServer() {
        ProcessMiningServer processMiningServer = new ProcessMiningServer();
        if (checkFile()) {
            try {
                InputStream input = new FileInputStream(configPath);
                Properties prop = new Properties();
                prop.load(input);
                processMiningServer.setUrl(prop.getProperty("pm.url"));
                processMiningServer.setUserID(prop.getProperty("pm.userID"));
                processMiningServer.setApikey(prop.getProperty("pm.apiKey"));
                logger.info("Process Mining configuration" + processMiningServer);
                input.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return processMiningServer;
    }

    public boolean saveBawConfig(BAWContentServer bawServer) throws IOException {
        Properties prop = new Properties();
        if (checkFile()) {
            logger.info("File found retrieve data");
            prop.load(new FileInputStream(configPath));
        }
        try {
            OutputStream output = new FileOutputStream(configPath);
            // set the properties value
            prop.setProperty("baw.content.server", bawServer.getServer());
            prop.setProperty("baw.content.userName", bawServer.getUser());
            prop.setProperty("baw.content.password", bawServer.getPassword());
            prop.setProperty("baw.content.os", bawServer.getRepository());
            prop.setProperty("baw.content.port", bawServer.getPort());
            // save properties to project root folder
            prop.store(output, null);
            logger.info("Save the bawserver confir in the following path " + configPath);
        } catch (IOException io) {
            io.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean saveProcessMining(ProcessMiningServer processMiningServer) throws IOException {
        Properties prop = new Properties();
        if (checkFile()) {
            logger.info("File found retrieve data");
            prop.load(new FileInputStream(configPath));
        }
        try {
            OutputStream output = new FileOutputStream(configPath);
            // set the properties value
            prop.setProperty("pm.apiKey", processMiningServer.getApikey());
            prop.setProperty("pm.userID", processMiningServer.getUserID());
            prop.setProperty("pm.url", processMiningServer.getUrl());
            // save properties to project root folder
            prop.store(output, null);
            logger.info("Save the process Mining Server config in the following path " + configPath);
        } catch (IOException io) {
            io.printStackTrace();
            return false;
        }
        return true;
    }
}
