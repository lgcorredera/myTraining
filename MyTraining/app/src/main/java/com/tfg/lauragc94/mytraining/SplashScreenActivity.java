package com.tfg.lauragc94.mytraining;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;

// Splash Screen

public class SplashScreenActivity extends Activity {

    //Duración del Splash Screen
    private static final long SPLASH_SCREEN_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Orientation of the screen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Hide 'title bar'
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash_screen);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //Start the next Activity
                Intent loginIntent = new Intent().setClass(SplashScreenActivity.this, LoginActivity.class);
                startActivity(loginIntent);

                //Close the activity and can not come back
                finish();
            }
        };

        //Time that the screen will be displayed
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }
}
