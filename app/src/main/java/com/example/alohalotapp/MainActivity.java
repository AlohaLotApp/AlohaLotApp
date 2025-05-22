package com.example.alohalotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Duration (in milliseconds) that the splash screen is shown before moving to the main screen
    private static int SPLASH_SCREEN = 3000;

    //variables
    Animation topAnim, bottomAnim;
    ImageView logo;
    TextView welcome, slogan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //Hooks
        logo = findViewById(R.id.logoImgView);
        welcome = findViewById(R.id.welcomeTxt);
        slogan = findViewById(R.id.quoteTxt);

        logo.setAnimation(topAnim);
        welcome.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        // Delays the execution of the code inside 'run()' for the duration of SPLASH_SCREEN (e.g., 5 seconds)
        new Handler().postDelayed((new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, Login.class);

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair <View, String>(logo, "logo_image");
                pairs[1] = new Pair <View, String>(welcome, "logo_text");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        }),SPLASH_SCREEN);
    }
}