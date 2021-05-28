package com.yin.driver;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.squareup.picasso.Picasso;
import com.yin.driver.contactus.ContactUsActivity;
import com.yin.driver.fcm.analytics.AnalyticsReport;
import com.yin.driver.global.GlobalFunctions;
import com.yin.driver.global.GlobalVariables;
import com.yin.driver.home.HomeFragment;
import com.yin.driver.location.LocationServices;
import com.yin.driver.profile.profile.ProfileMainActivity;
import com.yin.driver.services.ServerResponseInterface;
import com.yin.driver.services.ServicesMethodsManager;
import com.yin.driver.services.model.CountryModel;
import com.yin.driver.services.model.KeyValueModel;
import com.yin.driver.services.model.NotificationModel;
import com.yin.driver.services.model.ProfileModel;
import com.yin.driver.services.model.PushNotificationModel;
import com.yin.driver.services.model.StatusModel;
import com.yin.driver.view.AlertDialog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String BUNDLE_DEEPLINK_URL = "BundleDeepLinkURL";
    public static final String BUNDLE_MAIN_NOTIFICATION_MODEL = "BundleMainModelNotificationModel";
    private LayoutInflater layoutInflater;
    public static String TAG = "MainActivity";
    public static Context mainContext = null;
    public static RelativeLayout cart_notification_layout, notification_layout;
    public static LocationServices locServices = null;
    static Activity activity = null;
    static Toolbar toolbar;
    static ActionBar actionBar;
    static String mTitle;
    static int mResourceID;
    static int titleResourseID;
    static GlobalFunctions globalFunctions;
    static TextView toolbar_title;
    static ImageView toolbar_logo;
    static Intent locationintent;
    public Menu menu;
    FragmentManager mainActivityFM = null;
    Window mainWindow = null;
    KeyValueModel keyValueModel;
    View mainView;
    GlobalVariables globalVariables;
    android.support.v7.app.AlertDialog stateAlert;
    NavigationView navigationView;
    View navigationHeaderView;
    TextView navigationVersion_tv;
    ViewPager viewPager;
    SmartTabLayout viewPagerTab;
    FragmentPagerItem
            upComingFragment = null,
            completedFragment = null;
    int gravity = 0;
    private NotificationModel notificationModel = null;
    int mToolbarHeight;

    // final int gravity = globalFunctions.getLanguage() == GlobalVariables.LANGUAGE.ARABIC ? GravityCompat.END : GravityCompat.START;
    ValueAnimator mVaActionBar;
    AnalyticsReport analyticsReport;
    String selected_state_id;
    int city_selection = -1;
    CountryModel countryModel;
    private List <CountryModel> countryList = new ArrayList();
    private List <String> countryStringList = new ArrayList();
    private String[] countryArr;
    private Context context;

    public static Intent newInstance(Context context, String url) {
        Intent intent = new Intent( context, MainActivity.class );
        intent.putExtra( BUNDLE_DEEPLINK_URL, url );
        return intent;
    }

    public static Intent newInstance(Context context, NotificationModel notificationModel) {
        Intent intent = new Intent( context, MainActivity.class );
        intent.putExtra( BUNDLE_MAIN_NOTIFICATION_MODEL, notificationModel );
        return intent;
    }

    public static void setTitle(String title, int titleImageID, int backgroundResourceID) {
        mTitle = title;
        if (backgroundResourceID != 0) {
            mResourceID = backgroundResourceID;
        } else {
            mResourceID = 0;
        }
        if (titleImageID != 0) {
            titleResourseID = titleImageID;
        } else {
            titleResourseID = 0;
        }
        restoreToolbar();
    }

/*    public static void setItemCount(int count) {
        setUpTabs();

    }*/

    public static void setMyTitle(String title, int titleImageID, int backgroundResourceID) {
        mTitle = title;
        if (backgroundResourceID != 0) {
            mResourceID = backgroundResourceID;
        } else {
            mResourceID = 0;
        }
        if (titleImageID != 0) {
            titleResourseID = titleImageID;
        } else {
            titleResourseID = 0;
        }
        restoreToolbar();
    }

    public static void startLocationService() {
        activity.startService( locationintent );
    }

    public static void stopLocationService() {
        activity.stopService( locationintent );
    }

    public static void setNotification(boolean isNotify) {
        if (notification_layout != null) {
            /*final ImageView tv = (ImageView) notification_layout.findViewById(R.id.actionbar_badge_notification_textview);
            if(!isNotify){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setVisibility(View.GONE);
                    }
                });

            }else{
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setVisibility(View.VISIBLE);
                    }
                });
            }*/
        }
    }

    private static void restoreToolbar() {
        //toolbar = (Toolbar) findViewById(R.id.tool_bar);
        Log.d( TAG, "Restore Tool Bar" );
        if (actionBar != null) {
            Log.d( TAG, "Restore Action Bar not Null" );
            Log.d( TAG, "Title : " + mTitle );
            if (titleResourseID != 0) {
                toolbar_logo.setVisibility( View.VISIBLE );
                toolbar_title.setVisibility( View.GONE );
                toolbar_logo.setImageResource( titleResourseID );
            } else {
                toolbar_logo.setVisibility( View.GONE );
                toolbar_title.setVisibility( View.VISIBLE );
                toolbar_title.setText( mTitle );
            }

            if (mResourceID != 0) toolbar.setBackgroundResource( mResourceID );
            //actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled( true );
        }

    }

    public static void closeThisActivity() {
        if (activity != null) {
            activity.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        activity = this;
        mainContext = this;
        mainWindow = getWindow();

        mainActivityFM = getSupportFragmentManager();
        layoutInflater = activity.getLayoutInflater();
        globalFunctions = AppController.getInstance().getGlobalFunctions();
        globalVariables = AppController.getInstance().getGlobalVariables();

        analyticsReport = new AnalyticsReport();

        Drawable navIconDrawable = ResourcesCompat.getDrawable( getResources(), R.drawable.ic_menu_header, getTheme() );

        toolbar = ( Toolbar ) findViewById( R.id.tool_bar ); // Attaching the layout to the toolbar object
        toolbar.setPadding(0, GlobalFunctions.getStatusBarHeight(mainContext), 0, 0);
        toolbar.setContentInsetsAbsolute( 0, 0 );
        toolbar.setNavigationIcon( navIconDrawable );
        toolbar_title = ( TextView ) toolbar.findViewById( R.id.toolbar_title );
        toolbar_logo = ( ImageView ) toolbar.findViewById( R.id.tool_bar_logo );
        toolbar_title.setVisibility( View.GONE );
        toolbar_logo.setVisibility( View.VISIBLE );

        mainView = toolbar;

        setSupportActionBar( toolbar );
        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator( navIconDrawable );
        setOptionsMenuVisiblity( false );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags( WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS );
            window.clearFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            window.setStatusBarColor( ContextCompat.getColor( this, R.color.sends ) );
        }

        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {
            PushNotificationModel pushNotificationModel = new PushNotificationModel();
            pushNotificationModel.setRegistration_id( token );
            sendPushNotificationID( mainContext, pushNotificationModel );
        }
        if (getIntent().hasExtra( BUNDLE_MAIN_NOTIFICATION_MODEL )) {
            notificationModel = ( NotificationModel ) getIntent().getSerializableExtra( BUNDLE_MAIN_NOTIFICATION_MODEL );
        } else {
            notificationModel = null;
        }
        accessPermissions( this );
/*
        viewPager = ( ViewPager ) findViewById( R.id.viewpager );
        viewPagerTab = ( SmartTabLayout ) findViewById( R.id.viewpager_tab );
        viewPager.setOffscreenPageLimit( 0 );*/


        gravity = globalFunctions.getLanguage( context ) == GlobalVariables.LANGUAGE.ARABIC ? GravityCompat.START : GravityCompat.START;
        final DrawerLayout drawer = ( DrawerLayout ) findViewById( R.id.drawer_layout );
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        toggle.setDrawerIndicatorEnabled( false );
        toggle.setHomeAsUpIndicator( navIconDrawable );
        toggle.setToolbarNavigationClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible( gravity )) {
                    drawer.closeDrawer( gravity );
                } else {
                    drawer.openDrawer( gravity );
                }
            }
        } );
        drawer.addDrawerListener( toggle );
        toggle.syncState();

        navigationView = ( NavigationView ) findViewById( R.id.nav_view );
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            navigationView.setPointerIcon(PointerIcon.load(getResources(), R.drawable.ic_menu));
        }*/
        navigationView.setNavigationItemSelectedListener( this );
        navigationHeaderView = navigationView.getHeaderView( 0 );

        mainActivityFM.addOnBackStackChangedListener( new FragmentManager.OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {
                if (mainActivityFM != null) {
                    Fragment currentFragment = mainActivityFM.findFragmentById( R.id.container );
                    if (currentFragment != null) {
                        currentFragment.onResume();
                    }
                }
            }
        } );


        Fragment homeFragment = new HomeFragment();
            replaceFragment( homeFragment, HomeFragment.TAG, getString( R.string.app_name ), 0, 0 );


    }

    public void showCitiesPopup() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder( MainActivity.this );
        builder.setTitle( getString( R.string.select_your_city ) );
        final ArrayAdapter <String> arrayAdapter = new ArrayAdapter <>( MainActivity.this, android.R.layout.select_dialog_singlechoice );

        arrayAdapter.addAll( countryStringList );

      /*  if(globalFunctions.getCityDetail(MainActivity.this)!= null) {
            city_selection = globalFunctions.getCityDetail(MainActivity.this).getIndex();
*/
        for (int i = 0; i < countryStringList.size(); i++) {
            if (globalFunctions.getCountryDetail() != null) {
               /* if(countryStringList != null && countryStringList.get(i).contains(globalFunctions.getCityDetail().getName())){
                    city_selection  = globalFunctions.getCityDetail().getIndex();
                }*/
            }
        }
/*
        builder.setSingleChoiceItems( arrayAdapter, city_selection, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                city_selection = which;
                String strName = arrayAdapter.getItem( which );
                selected_state_id = countryList.get( which ).getId();
                countryModel = new CountryModel();
                // countryModel.setIndex(which);
                countryModel.setId( selected_state_id );
                countryModel.setName( strName );
                globalFunctions.setCountryDetail( countryModel );
                //Log.d(TAG, "country : "+   globalFunctions.getCountryDetail().getName());

                analyticsReport.selectCountry( countryModel );

                setUpTabs();

              *//*  if(mainActivityFM!=null){
                    mainActivityFM.findFragmentById(R.id.container).onResume();
                }else{
                    Fragment homeFragment = new HomeFragment();
                    replaceFragment(homeFragment, HomeFragment.TAG, getString(R.string.app_name), 0, 0);
                }*//*
                dialog.cancel();

            }
        } );*/

        if (stateAlert != null) {
            if (!stateAlert.isShowing()) {
                stateAlert = builder.create();
                stateAlert.show();
            }
        } else {
            stateAlert = builder.create();
            stateAlert.show();
        }
    }

    /*private void setUpTabs() {
        if (viewPager.getChildCount() > 0) {
            viewPager.removeAllViews();
        }
        FragmentPagerItems fragmentPagerItems = new FragmentPagerItems( activity );

        upComingFragment = FragmentPagerItem.of( getString( R.string.new_title ), MyAppointmentsNewListFragment.class, MyAppointmentsNewListFragment.getBundle( null, GlobalVariables.LIST_TYPE.CURRENT ) );
        fragmentPagerItems.add( upComingFragment );

        completedFragment = FragmentPagerItem.of( getString( R.string.confirmed ), MyAppointmentsConfirmedListFragment.class, MyAppointmentsConfirmedListFragment.getBundle( null, GlobalVariables.LIST_TYPE.HISTORY ) );
        fragmentPagerItems.add( completedFragment );

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), fragmentPagerItems );

        if (viewPager != null && adapter != null) {
            viewPager.setAdapter( adapter );
            // if(viewPagerTab!=null)viewPagerTab.setViewPager(viewPager);

            if (viewPagerTab != null) {
                viewPagerTab.setViewPager( viewPager );
            }

            // viewPagerTab = (SmartTabLayout) LayoutInflater.from(getApplicationContext().getApplicationContext()).inflate(R.layout.actionbar_badge_notification_layout, null);

        }

    }*/

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo( getPackageName(), PackageManager.GET_SIGNATURES );
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance( "SHA" );
                md.update( signature.toByteArray() );
                String hashKey = new String( Base64.encode( md.digest(), 0 ) );
                Log.i( TAG, "printHashKey() Hash Key: " + hashKey );
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e( TAG, "printHashKey()", e );
        } catch (Exception e) {
            Log.e( TAG, "printHashKey()", e );
        }
    }

    @Override
    protected void onStart() {
      /*  if (upComingFragment != null) {
            Fragment fragment = upComingFragment.instantiate( activity, 0 );
            (( MyAppointmentsNewListFragment ) fragment).refreshList();
        }
        if (completedFragment != null) {
            Fragment fragment = completedFragment.instantiate( activity, 0 );
            (( MyAppointmentsConfirmedListFragment ) fragment).refreshList();
        }*/
        super.onStart();
    }

    @Override
    protected void onResume() {
        setNavigationHeaders();
        super.onResume();

    }

    public void setOptionsMenuVisiblity(boolean showMenu) {
        if (menu == null)
            return;
        //menu.setGroupVisible(R.id.menu, showMenu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       /* if(resultCode == globalVariables.REQUEST_SEARCH_ACTIVITY){
                if (data != null) {
                    KeyValueModel model = (KeyValueModel) data.getSerializableExtra(SearchActivity.BUNDLE_KEYVALUE_MODEL);
                    Fragment fragment = ProductListFragment.newInstance(model);
                    replaceFragment(fragment, ProductListFragment.TAG, getString(R.string.search_results), 0, 0);
            }
        }else {*/
        super.onActivityResult( requestCode, resultCode, data );
        /*}*/
    }

    public int getTitleResourseID() {
        return titleResourseID;
    }

    public void setTitleResourseID(int titleResourseID) {
        this.titleResourseID = titleResourseID;
        restoreToolbar();
    }

    public void accessPermissions(Activity activity) {
        int permissionCheck_getAccounts = ContextCompat.checkSelfPermission( activity, android.Manifest.permission.GET_ACCOUNTS );
        int permissionCheck_callPhone = ContextCompat.checkSelfPermission( activity, android.Manifest.permission.CALL_PHONE );
        int permissionCheck_lockwake = ContextCompat.checkSelfPermission( activity, android.Manifest.permission.WAKE_LOCK );
        int permissionCheck_internet = ContextCompat.checkSelfPermission( activity, android.Manifest.permission.INTERNET );
        int permissionCheck_Access_internet = ContextCompat.checkSelfPermission( activity, android.Manifest.permission.ACCESS_NETWORK_STATE );
        int permissionCheck_Access_wifi = ContextCompat.checkSelfPermission( activity, android.Manifest.permission.ACCESS_WIFI_STATE );
        // int permissionCheck_External_storage = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // int permissionCheck_cam = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA);

        if (permissionCheck_internet != PackageManager.PERMISSION_GRANTED || permissionCheck_Access_internet != PackageManager.PERMISSION_GRANTED || permissionCheck_callPhone != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale( activity, android.Manifest.permission.INTERNET ) && ActivityCompat.shouldShowRequestPermissionRationale( activity, android.Manifest.permission.ACCESS_NETWORK_STATE ) && ActivityCompat.shouldShowRequestPermissionRationale( activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE ) && ActivityCompat.shouldShowRequestPermissionRationale( activity, android.Manifest.permission.CAMERA ) || ActivityCompat.shouldShowRequestPermissionRationale( activity, android.Manifest.permission.CALL_PHONE )) {
                Fragment homeFragment = null;
                homeFragment = new HomeFragment();
                mainActivityFM.beginTransaction().replace( R.id.container, homeFragment, "" ).commitAllowingStateLoss();
            } else {
                ActivityCompat.requestPermissions( activity, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CALL_PHONE}, globalVariables.PERMISSIONS_REQUEST_PRIMARY );
            }
        }
        /*gpsTracker.getLocation();*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == globalVariables.PERMISSIONS_REQUEST_PRIMARY) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Fragment homeFragment = new HomeFragment();
                replaceFragment( homeFragment, HomeFragment.TAG, getString( R.string.app_name ), 0, 0 );
            }
        }
    }

    private void sendPushNotificationID(final Context context, PushNotificationModel pushNotificationModel) {
        if (globalFunctions.getSharedPreferenceString( globalVariables.SHARED_PREFERENCE_COOKIE ) != null) {
            ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
            servicesMethodsManager.sendPushNotificationID( context, pushNotificationModel, new ServerResponseInterface() {
                @Override
                public void OnSuccessFromServer(Object arg0) {
                    Log.d( TAG, "Response : " + arg0.toString() );
                }

                @Override
                public void OnFailureFromServer(String msg) {
                    Log.d( TAG, "Failure : " + msg );
                }

                @Override
                public void OnError(String msg) {
                    Log.d( TAG, "Error : " + msg );
                }
            }, "Send_Push_Notification_ID" );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.menu_nothing, menu );
        this.menu = menu;
        setOptionsMenuVisiblity( false );

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.menu_nothing, menu );
        this.menu = menu;
        setOptionsMenuVisiblity( false );

        return true;
    }

    public void setNavigationHeaders() {
        if (navigationHeaderView != null && mainContext != null) {
            TextView
                    header_name_tv = ( TextView ) navigationHeaderView.findViewById( R.id.navigation_header_name ),
                    header_address_tv = ( TextView ) navigationHeaderView.findViewById( R.id.navigation_header_address_tv );

            ImageView
                    // header_close_iv = (ImageView) navigationHeaderView.findViewById(R.id.navigation_header_close_iv),
                    header_app_iv = ( ImageView ) navigationHeaderView.findViewById( R.id.navigation_header_profile_round_view );


            ProfileModel profileModel = globalFunctions.getProfile( context );
            if (profileModel != null && mainContext != null) {
                try {

                    String fullName = profileModel.getFirstName() + " " + profileModel.getLastName();
                    header_name_tv.setText( fullName != null ? fullName : getString( R.string.guest ) );
                    header_address_tv.setText( profileModel.getCityName() != null ? profileModel.getCityName() : getString( R.string.select_city ) );


                    try {
                        if (profileModel.getProfileImg() != null || !profileModel.getProfileImg().equals( "null" ) || !profileModel.getProfileImg().equalsIgnoreCase( "" )) {
                            Picasso.with( mainContext ).load(profileModel.getProfileImg() ).placeholder( R.drawable.ic_boys_icon ).into( header_app_iv );
                        }
                    } catch (Exception e) {
                    }

             /*       if (profileModel.getImage() != null ) {
                        Picasso.with( layoutInflater.getContext() ).load( profileModel.getImage() ).fit().centerInside()
                                .placeholder( R.drawable.ic_boys_icon )
                                .error( R.drawable.ic_boys_icon )
                                .into( header_app_iv );
                    }*/

                } catch (Exception exxx) {
                    Log.e( TAG, exxx.getMessage() );
                }

            } else {
                if (mainContext != null) {
                    try {
                        header_address_tv.setVisibility( View.GONE );
                        header_name_tv.setText( mainContext.getString( R.string.guest ) );
                    } catch (Exception exxx) {
                        Log.e( TAG, exxx.getMessage() );
                    }
                }
            }

            final DrawerLayout drawer = ( DrawerLayout ) findViewById( R.id.drawer_layout );

            header_app_iv.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent( mainContext, ProfileMainActivity.class );
                    startActivity( intent );
                    drawer.closeDrawer( gravity );
                  /*  Fragment homeFragment = new HomeFragment();
                    replaceFragment(homeFragment, HomeFragment.TAG, getString(R.string.app_name), 0, 0);
                    drawer.closeDrawer(gravity);*/
                }
            } );

            header_name_tv.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent( mainContext, ProfileMainActivity.class );
                    startActivity( intent );
                    drawer.closeDrawer( gravity );
                }
            } );

            header_address_tv.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent( mainContext, ProfileMainActivity.class );
                    startActivity( intent );
                    drawer.closeDrawer( gravity );
                }
            } );

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return false;
    }

    public void replaceFragment(@NonNull Fragment fragment, @NonNull String tag, @NonNull String title, int titleImageID, @NonNull int bgResID) {
        if (fragment != null && mainActivityFM != null) {
            Fragment tempFrag = mainActivityFM.findFragmentByTag( tag );
            if (tempFrag != null) {
//                mainActivityFM.beginTransaction().remove(tempFrag).commitAllowingStateLoss();
                mainActivityFM.beginTransaction().remove( tempFrag ).commit();
            }
            setTitle( title, titleImageID, bgResID );
            FragmentTransaction ft = mainActivityFM.beginTransaction();
            //ft.setCustomAnimations(R.anim.slide_out_left, R.anim.slide_out_right);
            ft.add( R.id.container, fragment, tag ).addToBackStack( tag ).commitAllowingStateLoss();
        }
    }

    public void addFragment(@NonNull Fragment fragment, @NonNull String tag, @NonNull String title, int titleImageID, @NonNull int bgResID) {
        if (fragment != null && mainActivityFM != null) {
            Fragment tempFrag = mainActivityFM.findFragmentByTag( tag );
            if (tempFrag != null) {
                fragment = tempFrag;
            }
            setTitle( title, titleImageID, bgResID );
            FragmentTransaction ft = mainActivityFM.beginTransaction();
            //ft.setCustomAnimations(R.anim.slide_out_left, R.anim.slide_out_right);
            ft.replace( R.id.container, fragment ).addToBackStack( tag ).commitAllowingStateLoss();
        }
    }

    public void overlayFragment(@NonNull Fragment fragment, @NonNull String tag, @NonNull String title, int titleImageID, @NonNull int bgResID) {
        if (fragment != null && mainActivityFM != null) {
            Fragment tempFrag = mainActivityFM.findFragmentByTag( tag );
            if (tempFrag != null) {
                fragment = tempFrag;
            }
            setTitle( title, titleImageID, bgResID );
            FragmentTransaction ft = mainActivityFM.beginTransaction();
            //ft.setCustomAnimations(R.anim.slide_in_left, 0);
            ft.add( R.id.container, fragment ).addToBackStack( null ).commitAllowingStateLoss();
        }
    }

    public void LanguageChange(Context context) {
        ShowPopUpLanguage( context );
    }

    public void ShowPopUpLanguage(final Context context) {
        /*android.app.AlertDialog.Builder builder;
        builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(getApplicationContext().getString(R.string.lang_change));
        builder.setCancelable(true);
        builder.setPositiveButton(getApplicationContext().getResources()
                .getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                RestartEntireApp(context);
            }
        });
        builder.show();*/
    }

    public void RestartEntireApp(Context context, boolean isLanguageChange) {
        if (isLanguageChange) {
            SharedPreferences shared_preference = PreferenceManager.getDefaultSharedPreferences( this
                    .getApplicationContext() );

            String mCustomerLanguage = shared_preference.getString(
                    globalVariables.SHARED_PREFERENCE_SELECTED_LANGUAGE, "null" );
            String mCurrentlanguage;
            if ((mCustomerLanguage.equalsIgnoreCase( "en" ))) {
                globalFunctions.setLanguage( context, GlobalVariables.LANGUAGE.ARABIC );

                mCurrentlanguage = "ar";
            } else {
                mCurrentlanguage = "en";
                globalFunctions.setLanguage( context, GlobalVariables.LANGUAGE.ENGLISH );

            }
            SharedPreferences.Editor editor = shared_preference.edit();
            editor.putString( globalVariables.SHARED_PREFERENCE_SELECTED_LANGUAGE, mCurrentlanguage );
            editor.commit();
        }
        globalFunctions.closeAllActivities();
        Intent i = new Intent( this, SplashScreen.class );
        i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity( i );
        System.exit( 0 );
    }

    public void stimulateOnResumeFunction() {
        mainActivityFM.findFragmentById( R.id.container ).onResume();
    }

    public void releseFragments() {
        for (int i = 0; i < mainActivityFM.getBackStackEntryCount(); ++i) {
            mainActivityFM.popBackStack();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState( outState );
    }

    @Override
    public void onBackPressed() {
        if (mainActivityFM != null) {
            String currentFragment = mainActivityFM.findFragmentById( R.id.container ).getClass().getName();
            String homeFragmentName = HomeFragment.class.getName();
            DrawerLayout drawer = ( DrawerLayout ) findViewById( R.id.drawer_layout );
            if (drawer.isDrawerOpen( GravityCompat.START )) {
                drawer.closeDrawers();
            } else if (!currentFragment.equalsIgnoreCase( homeFragmentName )) {
                Fragment homeFragment = homeFragment = new HomeFragment();
                replaceFragment( homeFragment, HomeFragment.TAG, getString( R.string.home ), 0, 0 );
            } else if (currentFragment.equalsIgnoreCase( homeFragmentName )) {
                /*Exit Alert Box*/
                final AlertDialog alertDialog = new AlertDialog( mainContext );
                alertDialog.setCancelable( false );
                alertDialog.setIcon( R.drawable.ic_app_icon );
                alertDialog.setTitle( getString( R.string.app_name ) );
                alertDialog.setMessage( getResources().getString( R.string.appExitText ) );
                alertDialog.setPositiveButton( getString( R.string.yes ), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        GlobalFunctions.closeAllActivities();
                        finishAffinity();
                        System.exit( 0 );
                    }
                } );
                alertDialog.setNegativeButton( getString( R.string.cancel ), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                } );

                alertDialog.show();

            } else {
                super.onBackPressed();
                stimulateOnResumeFunction();
            }
        } else {
            super.onBackPressed();
            stimulateOnResumeFunction();

        }
        //stimulateOnResumeFunction();


    }

    private void getGuestUserCreation(final Context context) {
        this.context = context;
        if (globalFunctions.getSharedPreferenceString( globalVariables.SHARED_PREFERENCE_COOKIE ) == null) {
            ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
            servicesMethodsManager.getGuestUserCreation( context, new ServerResponseInterface() {
                @Override
                public void OnSuccessFromServer(Object arg0) {
                    Log.d( TAG, "Response getIndex : " + arg0.toString() );
                    Fragment homeFragment = new HomeFragment();
                    replaceFragment( homeFragment, HomeFragment.TAG, getString( R.string.app_name ), 0, 0 );
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
            Log.d( TAG, "Error : " + "error" );
        }
    }


    @Override
    protected void onDestroy() {
        //deRegisterBroadcast();
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
       /* if (id == R.id.nav_language) {
           // startActivity(new Intent(this, ReviewActivity.class));
           *//* Fragment homeFragment = new HomeFragment();
            replaceFragment(homeFragment, HomeFragment.TAG, getString(R.string.app_name), 0, 0);*//*
        } *//*else if (id == R.id.nav_appointments) {
            startActivity(new Intent(this, SelectTherapistActivity.class));

           *//* Intent intent = new Intent(mainContext, ServicesListActivity.class);
            startActivity(intent);*//*
        }*/ /*else if (id == R.id.nav_payments) {
            startActivity(new Intent(this, ThankYouActivity.class));
          *//*  Intent intent = new Intent(mainContext, DoctorsListActivity.class);
            startActivity(intent);*//*
        }*/ /*else if (id == R.id.nav_address) {

            startActivity(new Intent(this, ServiceListActivity.class));
           *//* if (globalFunctions.isLoggedIn(context)) {
                Intent intent = new Intent(activity, ConsultedDoctorsListActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
            }*//*
        }*/
      /*  if (id == R.id.nav_home) {
            Intent intent = new Intent( mainContext, MainActivity.class );
            startActivity( intent );
        } else if (id == R.id.nav_completed_bookings) {
            Intent intent = new Intent( mainContext, CompletedBookingsListActivity.class );
            startActivity( intent );
        } else*/
        if (id == R.id.nav_account) {
            Intent intent = new Intent( mainContext, ProfileMainActivity.class );
            startActivity( intent );
        } else if (id == R.id.nav_support) {
            startActivity( new Intent( this, ContactUsActivity.class ) );
        } else if (id == R.id.nav_logout) {

            final AlertDialog alertDialog = new AlertDialog( activity );
            alertDialog.setCancelable( false );
            alertDialog.setIcon( R.drawable.ic_app );
            alertDialog.setTitle( activity.getString( R.string.app_name ) );
            alertDialog.setMessage( activity.getResources().getString( R.string.appLogoutText ) );
            alertDialog.setPositiveButton( activity.getString( R.string.yes ), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    //Logout the Application
                    logoutUser( mainContext );
                }
            } );
            alertDialog.setNegativeButton( activity.getString( R.string.cancel ), new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alertDialog.dismiss();
                }
            } );

            alertDialog.show();

        }/*else if (id == R.id.nav_support) {
            startActivity(new Intent(this, SelectTherapistListActivity.class));

           *//* Intent intent = new Intent(mainContext, LearningVideosListActivity.class);
            startActivity(intent);*//*
        } *//*else if (id == R.id.nav_about_yin) {
            startActivity(new Intent(this, AboutYinActivity.class));
           *//* Intent intent = new Intent(mainContext, SpecialOffersListActivity.class);
            startActivity(intent);*//*
        }*/

          /*  if (globalFunctions.isLoggedIn(context)) {
                Intent intent = new Intent(mainContext, ContactUsActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
            }*//*
        } *//*else if (id == R.id.nav_customer_service) {
            startActivity(new Intent(this, ContactUsActivity.class));
          *//*  Intent intent = new Intent(mainContext, SettingsView.class);
            startActivity(intent);*//*
        }*/ /*else if (id == R.id.nav_make_an_appointment) {
            if (globalFunctions.isLoggedIn(context)) {
                Intent intent = new Intent(mainContext, MakeAnAppointmentActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
            }
        } else if (id == R.id.nav_help) {
            if (globalFunctions.isLoggedIn(context)) {
                Intent intent = new Intent(mainContext, HelpActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
            }
        } else if (id == R.id.nav_about_us) {
            Intent intent = new Intent(mainContext, AboutUsListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_360_degree_view) {
            Intent intent = new Intent(mainContext, WebviewActivity.class);
            startActivity(intent);
        }
*/
        DrawerLayout drawer = ( DrawerLayout ) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( gravity );
        return false;
    }

    private void logoutUser(final Context context) {
        GlobalFunctions.showProgress( context, getString( R.string.logingout ) );
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.logout( context, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                Log.d( TAG, "Response : " + arg0.toString() );
                validateOutput( arg0 );
            }

            @Override
            public void OnFailureFromServer(String msg) {
                GlobalFunctions.hideProgress();
                Toast.makeText( context, msg, Toast.LENGTH_SHORT ).show();
                //GlobalFunctions.displayMessaage(context, mainView, msg);
                Log.d( TAG, "Failure : " + msg );
            }

            @Override
            public void OnError(String msg) {
                GlobalFunctions.hideProgress();
                Toast.makeText( context, msg, Toast.LENGTH_SHORT ).show();
                //GlobalFunctions.displayMessaage(context, mainView, msg);
                Log.d( TAG, "Error : " + msg );
            }
        }, "Logout_User" );
    }


    private void validateOutput(Object model) {
        if (model instanceof StatusModel) {
            StatusModel statusModel = ( StatusModel ) model;
            //globalFunctions.displayMessaage(activity, mainView, statusModel.getMessage());
            if (statusModel.isStatus()) {
                /*Logout success, Clear all cache and reload the home page*/

            }
//            analyticsReport.logout(detail);
            globalFunctions.logoutApplication( mainContext );
            /*Intent intent = new Intent(activity, MainActivity.class);
            startActivity(intent);*/
            (( MainActivity ) activity).RestartEntireApp( mainContext, false );
        }

    }

}
