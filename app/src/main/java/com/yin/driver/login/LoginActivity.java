package com.yin.driver.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.yin.driver.AppController;
import com.yin.driver.MainActivity;
import com.yin.driver.R;
import com.yin.driver.global.GlobalFunctions;
import com.yin.driver.global.GlobalVariables;
import com.yin.driver.services.ServerResponseInterface;
import com.yin.driver.services.ServicesMethodsManager;
import com.yin.driver.services.model.CountryListModel;
import com.yin.driver.services.model.LoginModel;
import com.yin.driver.services.model.ProfileModel;
import com.yin.driver.services.model.RegisterModel;
import com.yin.driver.services.model.StatusModel;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    Context context;
    static Activity activity;
    View mainView;

    EditText mobileNumber_etv, password_etv;
    TextView sendActivation_tv;

    CountryCodePicker countryCode_ccsp;

    SearchableSpinner
            select_country_code_spinner;

    private CountryListModel
            countryListModel = null;

    String selectedCountryCodeLength = null;

    int selectCountryCodeLength = 0;

    private ArrayAdapter <String> countryAdapter;

    private List <String>
            countryStringList = new ArrayList();

    GlobalVariables globalVariables;
    GlobalFunctions globalFunctions;
    RegisterModel registerModel;
    LoginModel loginModel;
    String selected_country_code = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );

        setContentView( R.layout.login_activity );

        context = this;
        activity = this;
        globalFunctions = AppController.getInstance().getGlobalFunctions();
        globalVariables = AppController.getInstance().getGlobalVariables();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags( WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS );
            window.clearFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
//            window.setStatusBarColor(ContextCompat.getColor(this, R.color.ColorStatusBar));
        }
        countryCode_ccsp = ( CountryCodePicker ) findViewById( R.id.login_activity_countryCode_sp );

        mobileNumber_etv = ( EditText ) findViewById( R.id.login_activity_mobNo_ev );
        password_etv = ( EditText ) findViewById( R.id.login_activity_password_ev );
        sendActivation_tv = ( TextView ) findViewById( R.id.registration_activity_sendActivation_tv );

        mainView = mobileNumber_etv;


        mobileNumber_etv.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String digits = mobileNumber_etv.getText().toString().trim();

                if (digits.length() >= 9 && digits.length() >= 10) {
                    GlobalFunctions.closeKeyboard( activity );
                }
            }
        } );

        countryCode_ccsp.setOnCountryChangeListener( new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                selected_country_code = countryCode_ccsp.getSelectedCountryCodeWithPlus();
                mobileNumber_etv.setText( "" );
            }
        } );

        selected_country_code = countryCode_ccsp.getSelectedCountryCodeWithPlus();

        countryCode_ccsp.registerCarrierNumberEditText( mobileNumber_etv );

        countryCode_ccsp.setPhoneNumberValidityChangeListener( new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
            }
        } );


        sendActivation_tv.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();
            }
        } );

    }

    private void validateInput() {
        if (mobileNumber_etv != null) {
            String
                    mobileNo = mobileNumber_etv.getText().toString().trim(),
                    password = password_etv.getText().toString().trim();

            if (mobileNo.isEmpty()) {
                mobileNumber_etv.setError( getString( R.string.pleaseFillmobileNo ) );
                mobileNumber_etv.setFocusableInTouchMode( true );
                mobileNumber_etv.requestFocus();
            } else if (password.isEmpty()) {
                password_etv.setError( getString( R.string.pleaseFillpassword ) );
                password_etv.setFocusableInTouchMode( true );
                password_etv.requestFocus();
            } else if (!GlobalFunctions.isPhoneNumberValid( mobileNo )) {
//                mobNo_ev.setText( "" );
                mobileNumber_etv.setError( getString( R.string.mobileNoNotValid ) );
                mobileNumber_etv.setFocusableInTouchMode( true );
                mobileNumber_etv.requestFocus();
                // GlobalFunctions.displayMessaage(context, mainView, getString(R.string.mobileNumberNotValid));
            } else if (selected_country_code.isEmpty()) {
                globalFunctions.displayMessaage( activity, mainView, getString( R.string.countryCodeNONotValid ) );
            } else {
                LoginModel loginModel = new LoginModel();
                loginModel.setUserName( mobileNo );
                loginModel.setCountryCode( selected_country_code );
                loginModel.setPassword( GlobalFunctions.md5( password ) );

                loginUser( context, loginModel );

            }
        }
    }

    private void loginUser(final Context context, final LoginModel model) {
        GlobalFunctions.showProgress( context, "Logging..." );
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.loginUser( context, model, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
//                hideLoading();
                GlobalFunctions.hideProgress();
                Log.d( TAG, "Response : " + arg0.toString() );
                validateOutput( arg0 );
            }

            @Override
            public void OnFailureFromServer(String msg) {
//                hideLoading();
                GlobalFunctions.hideProgress();
                GlobalFunctions.displayMessaage( context, mainView, msg );
                Log.d( TAG, "Failure : " + msg );
            }

            @Override
            public void OnError(String msg) {
//                hideLoading();
                GlobalFunctions.hideProgress();
                GlobalFunctions.displayMessaage( context, mainView, msg );
                Log.d( TAG, "Error : " + msg );
            }
        }, "Login_User" );
    }

    private void validateOutput(Object model) {
        if (model instanceof StatusModel) {
            StatusModel statusModel = ( StatusModel ) model;
         /*   if (statusModel.getExtra().equalsIgnoreCase( "1" )) {
                final AlertDialog alertDialog = new AlertDialog( activity );
                alertDialog.setCancelable( false );
                alertDialog.setIcon( R.drawable.ic_app );
                alertDialog.setTitle( getString( R.string.otp ) );
                alertDialog.setMessage( statusModel.getMessage() );

                alertDialog.setPositiveButton( getString( R.string.ok ), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
//                        GlobalFunctions.closeAllActivities();
                        OtpModel otpModel = new OtpModel();
                        otpModel.setPhone( loginModel.getUserName() );
                        otpModel.setCountryCode( loginModel.getCountryCode() );
                        Intent intent = RegisterActivity.newInstance( context, otpModel );
                        startActivity( intent );
                    }

                } );
                alertDialog.show();

            } else {*/
            GlobalFunctions.displayErrorDialog( context, getString( R.string.sorry_u_have_not_register_with_us ) );

        } else if (model instanceof ProfileModel) {
            GlobalFunctions.closeAllActivities();
            ProfileModel profileModel = ( ProfileModel ) model;
            GlobalFunctions.setProfile( context, profileModel );
            Intent intent = new Intent( context, MainActivity.class );
            startActivity( intent );
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeThisActivity();
        }
        return false;
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
