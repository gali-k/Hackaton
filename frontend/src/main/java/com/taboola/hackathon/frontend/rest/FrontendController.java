package com.taboola.hackathon.frontend.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.taboola.hackathon.frontend.api.RecognizedText;
import com.taboola.hackathon.frontend.api.Recommendation;
import com.taboola.hackathon.frontend.dao.StorageDao;

@RestController
@RequestMapping("/users/{userId}")
public class FrontendController {

    @Autowired
    private StorageDao storageDao;

    @RequestMapping("/upload")
    public @ResponseBody RecognizedText retrieveText(@PathVariable String userId, @RequestBody RecognizedText recognizedText) {
        return recognizedText;
    }

    @RequestMapping(path="/audio", method = RequestMethod.POST)
    public void uploadRecording(@PathVariable String userId, @RequestParam("file") MultipartFile file) {
        storageDao.store(file, userId);
    }

    @RequestMapping("/recommend")
    public @ResponseBody Recommendation retrieveRecommendations(@PathVariable String userId) {
        Recommendation result = new Recommendation();
        result.setUserId(userId);
        result.setRecommendation("Here will be recommendations for the user");
        return result;
    }

}
