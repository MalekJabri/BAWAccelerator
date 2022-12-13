package org.mj.process.service;

import com.filenet.api.core.ObjectStore;
import com.ibm.mj.core.p8Connection.ConnectionTool;
import com.ibm.mj.core.properties.MappingFieldsTool;
import lombok.Data;
import org.mj.process.model.servers.BAWContentServer;

import java.util.logging.Logger;

@Data
public class ServerConfig {

    public static Logger logger = Logger.getLogger(ServerConfig.class.getName());
    private ObjectStore os;
    private ConnectionTool connectionTool;


    public ServerConfig(String objecStore, int config) {
        getconnection(objecStore, config);
    }

    public ServerConfig(BAWContentServer config) {
        getconnection(config.getRepository(), config);
    }

    public static void main(String[] args) {
        ServerConfig serverConfig = new ServerConfig("TOS", 3);
        System.out.println(serverConfig.getOs().toString());
    }

    public void getconnection(String to, int config) {
        logger.info("Start Connection based on the configuration " + config);
        MappingFieldsTool info = new MappingFieldsTool("connection.properties");
        connectionTool = new ConnectionTool();
        connectionTool.establishConnection(info.getMappingValue(config + "_user"), info.getMappingValue(config + "_password"), "FileNetP8WSI", "https://" + info.getMappingValue(config + "_host") + ":9443/wsi/FNCEWS40MTOM");
        os = connectionTool.fetchOS(to);
        logger.info("Start Connection based on the configuration " + info.getMappingValue(config + "_user") + " hosts " + info.getMappingValue(config + "_host"));
    }

    public void getconnection(String to, BAWContentServer config) {
        logger.info("Start Connection based on the configuration " + config);
        connectionTool = new ConnectionTool();
        connectionTool.establishConnection(config.getUser(), config.getPassword(), "FileNetP8WSI", "http://" + config.getServer() + ":9080/wsi/FNCEWS40MTOM");
        os = connectionTool.fetchOS(to);
        logger.info("Start Connection based on the configuration " + config.getUser() + " host: " + config.getServer());
    }
}
