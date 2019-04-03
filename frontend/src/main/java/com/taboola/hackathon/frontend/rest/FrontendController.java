package com.taboola.hackathon.frontend.rest;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.taboola.hackathon.frontend.api.RecognizedText;
import com.taboola.hackathon.frontend.api.Recommendation;

@RestController
@RequestMapping("/users/{userId}")
public class FrontendController {

    @RequestMapping("/upload")
    public String retrieveText(@PathVariable String userId, @RequestBody RecognizedText recognizedText) {
        return "Uploading text for user: " + userId + ", " + recognizedText;
    }

    @RequestMapping("/recommend")
    public @ResponseBody Recommendation retrieveRecommendations(@PathVariable String userId) {
        Recommendation result = new Recommendation();
        result.setUserId(userId);
        result.setRecommendation("Here will be recommendations for the user");
        return result;
    }

}
