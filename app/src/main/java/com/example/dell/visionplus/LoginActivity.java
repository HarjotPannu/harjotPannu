package com.example.dell.visionplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextToSpeech tto_s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        TextToSpeech.OnInitListener listener = new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status)
            {

            }
        };

        // creating object of TextToSpeech
        tto_s = new TextToSpeech(LoginActivity.this , listener);
        tto_s.setSpeechRate(0);

        Handler h = new Handler();

        Runnable r = new Runnable() {
            @Override
            public void run() {

                tto_s.speak("Press volume down key to skip login process" , TextToSpeech.QUEUE_ADD , null);

                Toast.makeText(LoginActivity.this,"Press volume down key to skip Login process",Toast.LENGTH_SHORT).show();

            }
        };

        h.postDelayed(r,2000);


    }



    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {


            Intent i = new Intent(LoginActivity.this,ModuleActivity.class);
            startActivity(i);
            finish();
            new Handler().post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {

                    tto_s.speak("volume down key pressed" , TextToSpeech.QUEUE_ADD , null);

                    tto_s.playSilentUtterance(1000, TextToSpeech.QUEUE_ADD, null);

                    Toast.makeText(LoginActivity.this, "Volume Down Pressed", Toast.LENGTH_SHORT)
                            .show();


                }
            });

            return true;
        }

        else {
            return super.onKeyDown(keyCode, event);
        }
    }


    public void loginFun(View view) {

        EditText email_et = findViewById(R.id.email_et);

        EditText password_et = findViewById(R.id.password_et);

        String email = email_et.getText().toString();

        String password = password_et.getText().toString();

        final ProgressDialog progress_bar = new ProgressDialog(LoginActivity.this);


        progress_bar.setTitle("Please Wait");

        progress_bar.setMessage("Logging In...");

        progress_bar.show();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progress_bar.hide();

                if(task.isSuccessful())
                {
                    Intent i = new Intent(LoginActivity.this , ModuleActivity.class);

                    startActivity(i);

                    finish();

                    new Handler().post(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {

                            tto_s.speak("Login Successfully" , TextToSpeech.QUEUE_ADD , null);

                            tto_s.playSilentUtterance(1000, TextToSpeech.QUEUE_ADD, null);

                            Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT)
                                    .show();


                        }
                    });

                }

                else {
                    tto_s.speak("Invalid Login" , TextToSpeech.QUEUE_ADD , null);

                    Toast.makeText(LoginActivity.this , "Invalid Login" , Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void signUp(View view) {

        Intent i = new Intent(LoginActivity.this,SignUpActivity.class);
        startActivity(i);

    }

    public void skip_1(View view) {

        Intent i = new Intent(LoginActivity.this,ModuleActivity.class);
        startActivity(i);

    }

    @Override
    protected void onPause() {
        super.onPause();

        tto_s.stop();
    }
}
