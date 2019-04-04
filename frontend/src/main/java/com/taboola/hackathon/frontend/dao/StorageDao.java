package com.taboola.hackathon.frontend.dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
public class StorageDao {

    Logger logger = LoggerFactory.getLogger(StorageDao.class);


    @Value("${uploads.path}")
    private String uploadsPath;

    private Path rootLocation;

    @PostConstruct
    private void init() {
        logger.info("Starting DAO with following uploads directory: " + uploadsPath);
        rootLocation = Paths.get(uploadsPath);
    }

    public void store(MultipartFile file, String userId) {
        String filename = StringUtils.cleanPath(userId + "_" + file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                logger.error("Do not to store empty file");
                return;
            }
            if (filename.contains("..")) {
                logger.error("Someone tries to upload file in a wrong-wrong-wrong place");
                return;
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(
                    inputStream,
                    this.rootLocation.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING
                );
                logger.info("Upload complete: " + filename);
            }
            String command = String.format("ffmpeg -i %s/%s -ac 1 %s/%s.flac", uploadsPath, filename, uploadsPath, filename);
            Process ffmpeg = Runtime.getRuntime().exec(command);
            ffmpeg.waitFor();
        } catch (IOException e) {
            logger.error("Exception occured while saving file: " + filename, e);
        } catch (InterruptedException e) {
            logger.error("Failed to convert file with ffmpeg: " + filename, e);
        }
    }


}
