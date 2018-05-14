package com.example.dell.visionplus;

import android.content.Intent;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {

    TextToSpeech tto_s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextToSpeech.OnInitListener listener = new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status)
            {

            }
        };

        // creating object of TextToSpeech
        tto_s = new TextToSpeech(MainActivity.this , listener);

        Handler h = new Handler();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                tto_s.speak("Welcome to Vision Plus" , TextToSpeech.QUEUE_FLUSH , null);

            }
        };

        h.postDelayed(r,2000);

        Runnable r1 = new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(MainActivity.this , LoginActivity.class);

                startActivity(i);

                finish();

            }
        };

        h.postDelayed(r1,4000);
    }

}