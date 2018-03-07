package com.example.gioti.temperaturemonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class splash extends AppCompatActivity {
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //iv = findViewById(R.id.iv);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.mytransition);
        //iv.startAnimation(animation);
        final Intent i = new Intent(this, MapsActivity.class);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(i);
                    finish();
                }
            }

        };
        timer.start();

    }
}
