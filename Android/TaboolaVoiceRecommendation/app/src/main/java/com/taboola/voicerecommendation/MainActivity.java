package com.taboola.voicerecommendation;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.taboola.voicerecommendation.model.RecognizedText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private static final Gson gson = new Gson();
    private static final int PERMISSIONS_GRANTED_CODE = 101;
    public static final String CLASS_SIMPLE_NAME = MainActivity.class.getSimpleName();
    private String URL;

    private Button btnSendText;
    private Button btnStartRecording;
    private TextView txtRecognizedSpeech;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSendText = findViewById(R.id.btnSendText);
        btnSendText.setOnClickListener((v) -> sendText());
        btnStartRecording = findViewById(R.id.btnStartRecording);
        btnStartRecording.setOnClickListener((v) -> startVoiceInput());
        txtRecognizedSpeech = findViewById(R.id.txtRecognizedSpeech);
        queue = Volley.newRequestQueue(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_GRANTED_CODE);
        }
        URL = String.format("http://mxprocess.net:8080/users/%s/upload", ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getImei());
    }

    private void sendText() {
        try {
            queue.add(createJsonRequest());
        } catch (JSONException e) {
            Log.e(MainActivity.class.getSimpleName(), "Json error occurred", e);
        }
    }

    private JsonObjectRequest createJsonRequest() throws JSONException {
        RecognizedText recognizedText = new RecognizedText(System.currentTimeMillis(),
                txtRecognizedSpeech.getText().toString());
        JSONObject jsonRequest = new JSONObject(gson.toJson(recognizedText));
        return new JsonObjectRequest(Request.Method.POST, URL, jsonRequest,
                response -> txtRecognizedSpeech.setText(response.toString()),
                error -> Log.e(CLASS_SIMPLE_NAME, "Sending failed", error));
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Log.e(CLASS_SIMPLE_NAME, "Can't find activity", a);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode != RESULT_OK || data == null) {
                Toast.makeText(getBaseContext(), "Bad recognition result", Toast.LENGTH_LONG).show();
                return;
            }

            List<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            txtRecognizedSpeech.setText(result.get(0));
        }
    }
}