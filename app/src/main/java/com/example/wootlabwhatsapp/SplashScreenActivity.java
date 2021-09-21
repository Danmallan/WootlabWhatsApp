package com.example.wootlabwhatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.wootlabwhatsapp.view.StartUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if ( firebaseUser != null){
            Thread thread = new Thread(){
                public void run(){
                    try{
                        sleep(4000);
                    }catch (InterruptedException e){
                        Log.e("Splash Screen", e.getMessage());
                        e.printStackTrace();
                    }finally {
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    }
                }
            };

            thread.start();
        }else {
            Thread thread = new Thread(){
                public void run(){
                    try{
                        sleep(4000);
                    }catch (InterruptedException e){
                        Log.e("Splash Screen", e.getMessage());
                        e.printStackTrace();
                    }finally {
                        startActivity(new Intent(SplashScreenActivity.this, StartUpActivity.class));
                    }
                }
            };

            thread.start();
        }
    }

}