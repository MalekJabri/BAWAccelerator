package org.mj.process.service;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ContentService {

    private static final Logger logger = LoggerFactory.getLogger(ContentService.class);

    public static String getFullPath(String path) {
        LocalDate localDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        StringBuilder buffer = new StringBuilder();
        buffer.append(path).append("/").append(localDate.getYear()).append("/").append(localDate.getMonthValue()).append("/").append(localDate.getDayOfMonth()).append("/");
        logger.info(buffer.toString());
        return buffer.toString();
    }

    public static String storeDocument(String id, MultipartFile file, String path, String format) {
        String result = "";
        try {
            if (!file.isEmpty()) {
                File storagePath = new File(getFullPath(path));
                storagePath.mkdirs();
                File docu = new File(storagePath.getAbsolutePath() + "/" + id + "." + format);
                file.transferTo(docu);
                result = docu.getAbsolutePath();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    public static String store64bitContent(String id, String base64, String path, String format) {
        String result = "";
        try {
            if (base64 != null && !base64.isEmpty()) {
                File storagePath = new File(getFullPath(path));
                storagePath.mkdirs();
                byte[] data = Base64.decodeBase64(base64);
                logger.info("Path to save the document " + storagePath.getAbsolutePath() + "/" + id + "." + format);
                FileOutputStream stream = new FileOutputStream(storagePath.getAbsolutePath() + "/" + id + "." + format);
                stream.write(data);
                result = storagePath.getAbsolutePath() + "/" + id + "." + format;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }


}
