package com.example.gioti.temperaturemonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

// welcome screen
public class splash extends AppCompatActivity {
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        iv = findViewById(R.id.ivTemp);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.mytransition);
        iv.startAnimation(animation);
        final Intent i = new Intent(this, MapsActivity.class);  //dhmioyrgei ena antikeimeno intent to opoio xrhsimopoioume gia na metaferthoume sthn antistoixh klash
        Thread timer = new Thread() {   //  dhmiourgoume ena thread to opoio trexei parallhla me to activity sto opoio ekteleitai h leitourgia xronokathusterhshs
            public void run() {
                try {
                    sleep(2000);    //stamataei to thread sto shmeio auto gia 2"
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(i);   // ksekinaei h klash MapsActivity
                    finish();
                }
            }

        };
        timer.start();  // ksekinaei to thread

    }
}
