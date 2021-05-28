package com.yin.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.yin.driver.global.GlobalFunctions;
import com.yin.driver.global.GlobalVariables;
import com.yin.driver.login.LoginActivity;
import com.yin.driver.login.TermsAndConditionActivity;



/**
 * Created by Sivasabharish Chinnaswamy on 04-12-2015.
 */


public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    GlobalVariables globalVariables;
    GlobalFunctions globalFunctions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        globalFunctions = AppController.getInstance().getGlobalFunctions();
        globalVariables = AppController.getInstance().getGlobalVariables();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.ColorStatusBar));
        }

        final ImageView logo = (ImageView) findViewById(R.id.splash_logo);

        GlobalVariables.LANGUAGE mCurrentLocale = GlobalFunctions.getLanguage(this);

        Log.d("Splash Screen", "Language Selected (SplashScreen) : "+mCurrentLocale);

        new Handler().postDelayed(new Runnable() {

         /*
         * Showing splash screen with a timer. This will be useful when you
         * want to show case your app logo / company
         */


            @Override
            public void run() {
                Intent i = null;
                i = new Intent(SplashScreen.this, MainActivity.class);
                if(GlobalFunctions.isLoggedIn(SplashScreen.this))
                {
                    i = new Intent(SplashScreen.this, MainActivity.class);}
//                else {i = new Intent(SplashScreen.this, LoginActivity.class);}
                else if(globalFunctions.getSharedPreferenceString(globalVariables.SHARED_PREFERENCE_TERMS_AND_CONDITIONS_SELECTED) != null){

                    if(globalFunctions.getSharedPreferenceString(globalVariables.SHARED_PREFERENCE_TERMS_AND_CONDITIONS_SELECTED).equalsIgnoreCase("checked") ){
                        i = new Intent(SplashScreen.this, TermsAndConditionActivity.class);
                    }else{
                        i = new Intent(SplashScreen.this, TermsAndConditionActivity.class);
                    }
                }else{
                    i = new Intent(SplashScreen.this, TermsAndConditionActivity.class);
                }

                startActivity(i);

                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
