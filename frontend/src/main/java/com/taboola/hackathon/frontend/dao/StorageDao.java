package com.taboola.hackathon.frontend.dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.logging.Level;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.taboola.hackathon.frontend.kafka.UserSegmentsProducer;
import com.taboola.hackathon.frontend.speech_to_text.GoogleCloudAPI;

@Component
public class StorageDao {

    Logger logger = LoggerFactory.getLogger(StorageDao.class);
    public static UserSegmentsProducer userSegmentsProducer;

    @Value("${uploads.path}")
    private String uploadsPath;

    private Path rootLocation;

    @PostConstruct
    private void init() {
        logger.info("Starting DAO with following uploads directory: " + uploadsPath);
        rootLocation = Paths.get(uploadsPath);
        logger.info("UserSegmentsProducer: creating");
      //  userSegmentsProducer = new UserSegmentsProducer();
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
            String fileAndPathOrig = uploadsPath + "/" + filename;
            String fileAndPathFlac = fileAndPathOrig + ".flac";
            Files.delete(Paths.get(fileAndPathFlac));
            String command = String.format("ffmpeg -i %s -ac 1 %s", fileAndPathOrig, fileAndPathFlac);

            Process ffmpeg = Runtime.getRuntime().exec(command);
            ffmpeg.waitFor();
            logger.info("ffmpeg conversion is accomplished: " + filename);

            GoogleCloudAPI speechToTextConverter = new GoogleCloudAPI();

            List<SpeechRecognitionResult> speechRecognitionResults = speechToTextConverter.speechToText(fileAndPathFlac);
            for (SpeechRecognitionResult result : speechRecognitionResults) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s%n", alternative.getTranscript());
                if (alternative.getTranscript().contains("sad")) {
                  //  userSegmentsProducer.sendToKafka(userId);
                }
                break;
            }
        } catch (IOException e) {
            logger.error("Exception occured while saving file: " + filename, e);
        } catch (InterruptedException e) {
            logger.error("Failed to convert file with ffmpeg: " + filename, e);
        }
    }


}
