package com.yin.driver.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.yin.driver.AppController;
import com.yin.driver.MainActivity;
import com.yin.driver.R;
import com.yin.driver.global.GlobalFunctions;
import com.yin.driver.global.GlobalVariables;
import com.yin.driver.services.ServerResponseInterface;
import com.yin.driver.services.ServicesMethodsManager;


public class TermsAndConditionActivity extends AppCompatActivity {

    public static final String TAG = "TAndCActivity";

    Context context;
    static Activity activity;
    View mainView;

    Toolbar toolbar;
    ActionBar actionBar;
    CheckBox tC_cb;
    TextView enter_tv, activity_home_tv;

    GlobalVariables globalVariables;
    GlobalFunctions globalFunctions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_terms_and_condition );

        context = this;
        activity = this;

       /* globalFunctions = AppController.getInstance().getGlobalFunctions();
        globalVariables = AppController.getInstance().getGlobalVariables();*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags( WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS );
            window.clearFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
//            window.setStatusBarColor(ContextCompat.getColor(this, R.color.ColorStatusBar));
        }
       /* toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setPadding(0, GlobalFunctions.getStatusBarHeight(context), 0, 0);
        toolbar.setNavigationIcon(R.drawable.ic_back_dark);
        toolbar.setContentInsetsAbsolute(0,0);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();*/
        /*Text Views*/
        tC_cb = ( CheckBox ) findViewById( R.id.activity_terms_and_condition_tC_cb );
        enter_tv = ( TextView ) findViewById( R.id.activity_terms_and_condition_enter_tv );
        activity_home_tv = ( TextView ) findViewById( R.id.activity_home_tv );

        mainView = tC_cb;

        enter_tv.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();
            }
        } );

        if (globalFunctions.getSharedPreferenceString( globalVariables.SHARED_PREFERENCE_COOKIE ) == null) {
            getGuestUserCreation( context );
        }


     /*   activity_home_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tC_cb != null ) {
                    if (!tC_cb.isChecked()) {
                        GlobalFunctions.displayMessaage(context, mainView, getString(R.string.check_terms_and_condition));
                    } else {
                        Intent intent = new Intent(TermsAndConditionActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                }
            }
        });*/
    }

    private void getGuestUserCreation(final Context context) {
        this.context = context;
        if (globalFunctions.getSharedPreferenceString( globalVariables.SHARED_PREFERENCE_COOKIE ) == null) {
            ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
            servicesMethodsManager.getGuestUserCreation( context, new ServerResponseInterface() {
                @Override
                public void OnSuccessFromServer(Object arg0) {
                    Log.d( TAG, "Response getIndex : " + arg0.toString() );
                    //globalFunctions.setCartCount(context, );
                    //successmessage(arg0);
                }

                @Override
                public void OnFailureFromServer(String msg) {
                    globalFunctions.displayMessaage( activity, mainView, msg );
                    Log.d( TAG, "Failure : " + msg );
                }

                @Override
                public void OnError(String msg) {
                    globalFunctions.displayMessaage( activity, mainView, msg );
                    Log.d( TAG, "Error : " + msg );
                }
            }, "get_Index" );
        } else {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeThisActivity();
                /*if(!globalFunctions.isLoggedin(mainContext)){
                Call Login Activity
                    Intent intent = new Intent(mainContext, LoginMainActivity.class);
                    startActivityForResult(intent,globalVariables.REQUEST_FOR_LOGIN);
                }else {
                    Fragment fragment = new PostAdMainFragment();
                    mainActivityFM.beginTransaction().replace(R.id.main_fragment_container, fragment, "PostAds").addToBackStack(null).commit();
                    return true;
                }*/
        }
        return false;
    }

    private void validateInput() {
        if (tC_cb != null) {

            if (!tC_cb.isChecked()) {
                GlobalFunctions.displayMessaage( context, mainView, getString( R.string.check_terms_and_condition ) );
            } else {
                GlobalFunctions.setSharedPreferenceString( context, GlobalVariables.SHARED_PREFERENCE_TERMS_AND_CONDITIONS_SELECTED, "checked" );
                startActivity( new Intent( this, LoginActivity.class ) );
            }

        }
    }

    @Override
    public void onBackPressed() {
        closeThisActivity();
        super.onBackPressed();
    }

    public static void closeThisActivity() {
        if (activity != null) {
            activity.finish();
            //activity.overridePendingTransition(R.anim.zoom_in,R.anim.zoom_out);
        }
    }

}
