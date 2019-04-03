package com.taboola.hackathon.frontend.speech_to_text;// Imports the Google Cloud client library
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GoogleCloudAPI {
    private SpeechClient speechClient;
    public GoogleCloudAPI(){
      try {
        this.speechClient = SpeechClient.create();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    public List<SpeechRecognitionResult> speechToText(String fileName) throws IOException {
      // Reads the audio file into memory
      Path path = Paths.get(fileName);
      byte[] data = Files.readAllBytes(path);
      ByteString audioBytes = ByteString.copyFrom(data);

      // Builds the sync recognize request
      RecognitionConfig config = RecognitionConfig.newBuilder()
              .setEncoding(AudioEncoding.LINEAR16)
              .setSampleRateHertz(48000)
              .setLanguageCode("en-US")
              .build();
      RecognitionAudio audio = RecognitionAudio.newBuilder()
              .setContent(audioBytes)
              .build();

      // Performs speech recognition on the audio file
      RecognizeResponse response = speechClient.recognize(config, audio);
      List<SpeechRecognitionResult> results = response.getResultsList();
      return results;
    }

  public static void main(String... args) throws Exception {
//      System.setProperty("GOOGLE_APPLICATION_CREDENTIALS","/Users/gali.k/Downloads/taboola-data-kafka-a5eed47f8a3e.json");
      GoogleCloudAPI speechToTextConverter = new GoogleCloudAPI();
      List<SpeechRecognitionResult> speechRecognitionResults = speechToTextConverter.speechToText("/Users/gali.k/IdeaProjects/2019/Hackaton/frontend/src/main/resources/test2_mono.wav");
      for (SpeechRecognitionResult result : speechRecognitionResults) {
        // There can be several alternative transcripts for a given chunk of speech. Just use the
        // first (most likely) one here.
        SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
        System.out.printf("Transcription: %s%n", alternative.getTranscript());
      }
  }
}