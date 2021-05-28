package com.yin.driver.contactus;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.vlonjatg.progressactivity.ProgressConstraintLayout;
import com.yin.driver.R;
import com.yin.driver.global.GlobalFunctions;
import com.yin.driver.global.GlobalVariables;


public class ContactUsActivity extends AppCompatActivity {

    public static final String TAG = "ReviewActivity";

    public static final String
            BUNDLE_KEY_OTP_ACTIVITY_REGISTER_MODEL = "BundleKeyOTPActivityRegisterModel";

    Context context;
    static Window window = null;

    private TextView
            support_tv,
            email_tv;

    private ImageView
            facebook_iv,
            twitter_iv,
            instagram_iv;

    android.support.v7.widget.Toolbar toolbar;
    static ImageView tool_bar_back_icon;
    ActionBar actionBar;

    private static Activity activity;
    View mainView;

    ProgressConstraintLayout progressActivity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_contact_us );
//        setContentView(R.layout.dialog_services_info);
//        setContentView(R.layout.package_info);
        activity = this;
        context = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags( WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS );
            window.clearFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
//            window.setStatusBarColor(ContextCompat.getColor(this, R.color.ColorStatusBar));
        }

        toolbar = ( Toolbar ) findViewById( R.id.tool_bar ); // Attaching the layout to the toolbar object
//        toolbar.setPadding(0, GlobalFunctions.getStatusBarHeight(context), 0, 0);
//        toolbar.setNavigationIcon(R.drawable.ic_back_dark);
//        toolbar.setContentInsetsAbsolute(0,0);
        TextView title = toolbar.findViewById( R.id.toolbar_title );
        ImageView tool_bar_logo = toolbar.findViewById( R.id.tool_bar_logo );
        tool_bar_back_icon = ( ImageView ) toolbar.findViewById( R.id.tool_bar_back_icon );

        tool_bar_back_icon.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        } );
        setSupportActionBar( toolbar );
        actionBar = getSupportActionBar();

//        title.setText("Contact Us" );
        title.setVisibility( View.GONE );
//        tool_bar_icon.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags( WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS );
            window.clearFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            window.setStatusBarColor( ContextCompat.getColor( this, R.color.sends ) );
        }


        support_tv = ( TextView ) findViewById( R.id.activity_contact_us_support_tv );
        email_tv = ( TextView ) findViewById( R.id.activity_contact_us_email_tv );
        facebook_iv = ( ImageView ) findViewById( R.id.activity_contact_us_facebook_iv );
        twitter_iv = ( ImageView ) findViewById( R.id.activity_contact_us_twitter_iv );
        instagram_iv = ( ImageView ) findViewById( R.id.activity_contact_us_instagram_iv );

        mainView = toolbar;

        support_tv.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalFunctions.callPhone( activity, GlobalVariables.SUPPORT_DEFAULT_NUMBER );
            }
        } );

        email_tv.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalFunctions.sendMail( activity, GlobalVariables.SUPPORT_DEFAULT_EMAIl, GlobalVariables.SUPPORT_DEFAULT_SUBJECT, GlobalVariables.SUPPORT_DEFAULT_DESCRIPTION );
            }
        } );


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