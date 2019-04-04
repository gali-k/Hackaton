package com.taboola.hackathon.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FrontendApplication {

	// do this in command line!
	// export GOOGLE_APPLICATION_CREDENTIALS=/home/ubuntu/credentials.json
	public static void main(String[] args) {
		SpringApplication.run(FrontendApplication.class, args);
	}

}
