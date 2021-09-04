package com.example.wootlabwhatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.wootlabwhatsapp.view.StartUpActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

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