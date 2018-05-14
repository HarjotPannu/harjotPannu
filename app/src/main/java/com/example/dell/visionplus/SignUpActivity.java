package com.example.dell.visionplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class SignUpActivity extends AppCompatActivity {

    TextToSpeech tto_s;
    EditText firstname , lastname , email , password , confirm_password ;
    TextView show_password , confirm_show;
    String fname , lname , mail , passw , confirm_pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextToSpeech.OnInitListener listener = new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status)
            {

            }
        };

        // creating object of TextToSpeech
        tto_s = new TextToSpeech(SignUpActivity.this , listener);
        tto_s.setSpeechRate(0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);


        password = findViewById(R.id.password);

        show_password = findViewById(R.id.show_password);
        show_password.setVisibility(View.GONE);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(password.getText().length()>0) {

                    show_password.setVisibility(View.VISIBLE);
                }
                else{
                    show_password.setVisibility((View.GONE));
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

         show_password.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(show_password.getText() == "Show"){
                     show_password.setText("Hide");
                     password.setTransformationMethod(null);

                     password.setSelection(password.length());
                 }
                 else{
                     show_password.setText("Show");
                     password.setTransformationMethod(new PasswordTransformationMethod());
                     password.setSelection(password.length());
                 }
             }
         });

         confirm_show = findViewById(R.id.confirm_show);
         confirm_show.setVisibility(View.GONE);
        confirm_password = findViewById(R.id.confirm_password);


        confirm_password.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(confirm_password.getText().length()>0) {

                    confirm_show.setVisibility(View.VISIBLE);
                }
                else{
                    confirm_show.setVisibility((View.GONE));
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        confirm_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(confirm_show.getText() == "Show"){
                    confirm_show.setText("Hide");
                    confirm_password.setTransformationMethod(null);

                    confirm_password.setSelection(confirm_password.length());
                }
                else{
                    confirm_show.setText("Show");
                    confirm_password.setTransformationMethod(new PasswordTransformationMethod());
                    confirm_password.setSelection(confirm_password.length());
                }
            }
        });


    }

    public void submit(View view) {

        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);


        fname = firstname.getText().toString();

        if(fname.length() <= 3 )
        {
            firstname.setError("firstname  must be of minimum 3 characters");
            Toast.makeText(SignUpActivity.this,"firstname  must be of minimum 3 characters",Toast.LENGTH_SHORT).show();

            return;
        }

        lname = lastname.getText().toString();

        if(lname.length() < 4 )
        {
            lastname.setError("lastname must be of minimum 4 characters");
            Toast.makeText(SignUpActivity.this,"lastname must be of minimum 4 characters",Toast.LENGTH_SHORT).show();

            return;
        }

        mail = email.getText().toString();

        if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches() )
        {
            email.setError( "please enter valid email" );
            Toast.makeText(SignUpActivity.this,"Please enter valid email",Toast.LENGTH_SHORT).show();

            return;
        }

        passw = password.getText().toString();

        if(passw.length() < 8)
        {
            password.setError( "password should contain atleast 8 characters" );
            Toast.makeText(SignUpActivity.this,"password should contain atleast 8 characters",Toast.LENGTH_SHORT).show();

            return;
        }

        confirm_pass = confirm_password.getText().toString();

        if( ! passw.equals(confirm_pass))

        {
            confirm_password.setError( "password and confirm password do  not match" );
            Toast.makeText(SignUpActivity.this,"password and confirm password do  not match",Toast.LENGTH_SHORT).show();

            return;
        }

        final ProgressDialog progress_bar = new ProgressDialog(SignUpActivity.this);


        progress_bar.setTitle("Please Wait");

        progress_bar.setMessage("Creating account...");

        progress_bar.show();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        OnCompleteListener<AuthResult> listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progress_bar.hide();

                System.out.println(String.valueOf(task.getException()));
                if(task.isSuccessful()) {

                    ProfileData data = new ProfileData(fname, lname, mail);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    database.getReference().child("user").child(mail.replace(".", "")).setValue(data);
                    tto_s.speak("Account Created" , TextToSpeech.QUEUE_FLUSH , null);


                    Toast.makeText(SignUpActivity.this,"Account Created",Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(SignUpActivity.this ,  ModuleActivity.class);
                    startActivity(i);
                    finish();

                }

                     else
                    {
                        tto_s.speak("Email already exist" , TextToSpeech.QUEUE_ADD , null);

                        Toast.makeText(SignUpActivity.this , "Email already exist" , Toast.LENGTH_SHORT).show();
                        return;

                    }
                }

            };

        auth.createUserWithEmailAndPassword(mail , passw).addOnCompleteListener(listener);


    }
}
