package com.trivedi.birthdayreminder.dummy;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.trivedi.birthdayreminder.ItemListActivity;
import com.trivedi.birthdayreminder.R;

public class SplashScreen extends Activity {
    ImageView imgView;
    private static final int SPLASH_DISPLAY_TIME = 2000;
    static boolean flag_logout;

    //  SharedPreferences pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Handler().postDelayed(new Runnable() {
            public void run() {
              //  Intent mainIntent;

                    try {
                        Intent mainIntent = new Intent(SplashScreen.this,
                                ItemListActivity.class);
                        SplashScreen.this.startActivity(mainIntent);
                        SplashScreen.this.finish();
                    } catch (ActivityNotFoundException e) {
                        Log.e("Error...", e.getMessage() + "");

                    }

                overridePendingTransition(R.anim.mainfadein,
                        R.anim.splashfadeout);
            }
        }, SPLASH_DISPLAY_TIME);
    }
}
