package com.taboola.voicerecommendation;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.taboola.voicerecommendation.model.RecognizedText;
import com.taboola.voicerecommendation.service.TService;

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
    public static final String UPLOAD_URL = "UPLOAD_URL";
    private String URL;

    private RequestQueue queue;
    private String audioFilePath;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity.this.audioFilePath = intent.getStringExtra("audioFile");
            Log.d(MainActivity.class.getSimpleName(), audioFilePath);
            Toast.makeText(MainActivity.this, "Recording Ready", Toast.LENGTH_LONG).show();
            btnSendRecording.setEnabled(true);
            btnPlayRecording.setEnabled(true);
        }
    };
    private Button btnSendRecording;
    private Button btnPlayRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);
        btnSendRecording = findViewById(R.id.btnSendRecording);
        btnPlayRecording = findViewById(R.id.btnPlayRecording);
        handlePermissions();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("recording-finished"));
        startService(new Intent(this, TService.class).putExtra(UPLOAD_URL, URL));
        btnSendRecording.setOnClickListener(v -> {
            SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, URL,
                    response -> Log.d(CLASS_SIMPLE_NAME, response),
                    error -> Log.e(CLASS_SIMPLE_NAME, "Send failed", error));
            smr.addFile("recording", audioFilePath);
            queue.add(smr);
        });
        btnPlayRecording.setOnClickListener(v -> {
            MediaPlayer mp = new MediaPlayer();
            try {
                mp.setDataSource(this, Uri.parse(audioFilePath));
            } catch (Exception e) {
                Log.e(CLASS_SIMPLE_NAME, "Can't play", e);
            }
            try {
                mp.prepare();
            } catch (Exception e) {
                Log.e(CLASS_SIMPLE_NAME, "can't prepare", e);
            }
            mp.start();
        });
    }

    private void handlePermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_GRANTED_CODE);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_GRANTED_CODE);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_GRANTED_CODE);
        }
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.PROCESS_OUTGOING_CALLS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS}, PERMISSIONS_GRANTED_CODE);
        }
        URL = String.format("http://mxprocess.net:8080/users/%s/upload",
                ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getImei());
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
//            txtRecognizedSpeech.setText(result.get(0));
        }
    }

}
