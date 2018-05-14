package com.example.dell.visionplus;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.ArrayList;

public class ModuleActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {
    GestureLibrary mLibrary;

    TextToSpeech tto_s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_activity);

        // creating OnOnitListener interface
        TextToSpeech.OnInitListener listener = new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status)
            {

            }
        };

        // creating object of TextToSpeech
        tto_s = new TextToSpeech(ModuleActivity.this , listener);

        tto_s.setSpeechRate(0);

        Handler h = new Handler();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                message();

            }
        };

        h.postDelayed(r,2000);



        mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!mLibrary.load()) {
            finish();
        }

        GestureOverlayView gestures =  findViewById(R.id.gestures);
        gestures.addOnGesturePerformedListener(this);
    }

    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = mLibrary.recognize(gesture);

        System.out.println("prediction scrore is   "+predictions.get(0).score +"   prediction name is    "+predictions.get(0).name);

        if (predictions.size() > 0 && predictions.get(0).score > 2.0) {

            String result = predictions.get(0).name;
            if(result.equalsIgnoreCase("FACE")) {

                Intent i = new Intent(ModuleActivity.this, FaceTrackerActivity.class);
                startActivity(i);

                new Handler().post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {

                        tto_s.speak("Face Detection Module Opened" , TextToSpeech.QUEUE_ADD , null);

                        tto_s.playSilentUtterance(1000, TextToSpeech.QUEUE_ADD, null);

                        Toast.makeText(ModuleActivity.this, "Face Detection Module Opened", Toast.LENGTH_SHORT)
                                .show();


                    }
                });


            }
            else if(result.equalsIgnoreCase("GPS")){
                Intent i = new Intent(ModuleActivity.this,GpsActivity.class);
                startActivity(i);
                new Handler().post(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {

                        tto_s.speak("GPS Opened", TextToSpeech.QUEUE_ADD, null);

                        tto_s.playSilentUtterance(1000, TextToSpeech.QUEUE_ADD, null);

                        Toast.makeText(ModuleActivity.this,"GPS Opened",Toast.LENGTH_SHORT).show();
                    }
                });


            }

            else{
                tto_s.speak("Invalid Gesture", TextToSpeech.QUEUE_ADD, null);

                Toast.makeText(this, "Invalid Gesture", Toast.LENGTH_LONG).show();
            }

        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

            Toast.makeText(this, "Volume Down Pressed", Toast.LENGTH_SHORT)
                    .show();

            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

            Toast.makeText(this, "Volume Up Pressed", Toast.LENGTH_SHORT)
                    .show();
            message();

            return true;
        }

        else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private  void message()
    {

        String speakData = "draw cee for detection of objects , draw zeddd for face detection , draw ess for gps , press volume up button for repetition";

        final String[] splitData = speakData.split(" , ");


        new Handler().post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {

                for (int i = 0; i < splitData.length; i++) {


                    tto_s.speak(splitData[i], TextToSpeech.QUEUE_ADD, null);

                    tto_s.playSilentUtterance(1000, TextToSpeech.QUEUE_ADD, null);

                }
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();

        tto_s.stop();
    }

    protected void onResume(){
        super.onResume();
        message();
    }
}
