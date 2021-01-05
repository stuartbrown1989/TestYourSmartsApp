package com.example.testyoursmarts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {
private TextView appName;
private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        appName = findViewById(R.id.text_appname);
       Animation anim = AnimationUtils.loadAnimation(this, R.anim.myanim);
       appName.setAnimation(anim);
        mAuth = FirebaseAuth.getInstance();

        // Access a Cloud Firestore instance from your Activity
      dbQuery.g_firestore = FirebaseFirestore.getInstance();


       new Thread()
       {
           @Override
           public void run()
           {
               try {
                   sleep(3000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }

               if(mAuth.getCurrentUser() != null)
               {
                   Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                   startActivity(intent);
                   SplashActivity.this.finish();
               }
               else
               {
                   Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                   startActivity(intent);
                   SplashActivity.this.finish();
               }


           }
       }.start();
    }
}
