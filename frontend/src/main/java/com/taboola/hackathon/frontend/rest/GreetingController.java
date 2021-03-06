package com.taboola.hackathon.frontend.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    public final String greeting = "Up and running: ";

    @RequestMapping("/status")
    public long greeting(){
        return System.currentTimeMillis();
    }

}
