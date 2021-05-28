package com.yin.driver.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.yin.driver.AppController;
import com.yin.driver.MainActivity;
import com.yin.driver.R;
import com.yin.driver.global.GlobalFunctions;
import com.yin.driver.global.GlobalVariables;
import com.yin.driver.profile.profile.ProfileMainActivity;
import com.yin.driver.services.ServerResponseInterface;
import com.yin.driver.services.ServicesMethodsManager;
import com.yin.driver.services.model.CityListModel;
import com.yin.driver.services.model.CountryListModel;
import com.yin.driver.services.model.ProfileModel;
import com.yin.driver.services.model.StatusModel;
import com.yin.driver.view.AlertDialog;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Android PC on 13-11-2016.
 */

public class CitySelectingActivity extends AppCompatActivity {
    public static final String TAG = "CitySelectingActivity";

    Switch switch_notification;
    Spinner language_select;
    Context context = null;
    static Activity activity = null;
    Window window = null;
    View mainView;

    // NotificationSettingsModel notificationDetails = new NotificationSettingsModel();

    private boolean isSpinnerSelected = false;

    GlobalFunctions globalFunctions;
    GlobalVariables globalVariables;

    Toolbar toolbar;
    ActionBar actionBar;
    String mTitle;
    int mResourceID, titleResourseID;
    TextView toolbar_title;
    ImageView tool_bar_logo;


    TextView update_btn, cancel_btn;

    // ProfileModel detail = null;

    SearchableSpinner
            select_country_spinner,
            select_city_spinner;

    private CountryListModel
            countryListModel = null;

    private CityListModel
            cityListModel = null;

    private List<String>
            countryStringList = new ArrayList(),
            cityStringList = new ArrayList();

    private ArrayAdapter<String> countryAdapter, cityAdapter;

    AppController appController = null;
    public Menu menu;
    public static RelativeLayout cart_notification_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_selecting_activity);

        context = this;
        activity = this;

        window = getWindow();
        globalFunctions = AppController.getInstance().getGlobalFunctions();
        globalVariables = AppController.getInstance().getGlobalVariables();

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        tool_bar_logo = findViewById(R.id.tool_bar_logo);
        toolbar.setPadding(0, globalFunctions.getStatusBarHeight(context), 0, 0);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        // tool_bar_logo.setImageResource(R.drawable.ic_menu_back);
        toolbar.setTitle("");

        tool_bar_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //enable translucent statusbar via flags
            globalFunctions.setTranslucentStatusFlag(window, true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //we don't need the translucent flag this is handled by the theme
            globalFunctions.setTranslucentStatusFlag(window, true);
            //set the statusbarcolor transparent to remove the black shadow
            this.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }


        appController = AppController.getInstance();

        update_btn = (TextView) findViewById(R.id.update_btn);
        cancel_btn = (TextView) findViewById(R.id.cancel_btn);

        select_country_spinner = (SearchableSpinner) findViewById(R.id.select_country_spinner);
        select_city_spinner = (SearchableSpinner) findViewById(R.id.select_city_spinner);

        mainView = select_country_spinner;

        getCityList(context);

        select_country_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                cityListModel = countryListModel.getCountryList().get(position).getCityList();
                setCityList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        select_city_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProfileModel profileModel = new ProfileModel();
                profileModel = GlobalFunctions.getProfile(context);
                if (select_country_spinner.getSelectedItemPosition() < 0) {
                    GlobalFunctions.displayMessaage(activity, mainView, getString(R.string.select_your_country));
                } else if (select_city_spinner.getSelectedItemPosition() < 0) {
                    GlobalFunctions.displayMessaage(activity, mainView, getString(R.string.select_your_city));
                } else {
                    profileModel.setCityId(cityListModel.getCityList().get(select_city_spinner.getSelectedItemPosition()).getId());
                    //  profileModel.setPhone(et_Phone.getText().toString().trim());
                    updateProfile(context, profileModel);
                }
               /* Intent intent = new Intent(SettingsView.this, CitySelectingActivity.class);
                startActivity(intent);*/
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog(activity);
                alertDialog.setCancelable(false);
                alertDialog.setIcon(R.drawable.app_icon);
                alertDialog.setTitle(getString(R.string.app_name));
                alertDialog.setMessage(getString(R.string.cancel_city_updation));

                alertDialog.setPositiveButton(getString(R.string.yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        onBackPressed();
                    }

                });

                alertDialog.setNegativeButton(getString(R.string.no), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }

                });
                alertDialog.show();
            }
        });


    }

    private void updateProfile(final Context context, ProfileModel profileModel) {
        GlobalFunctions.showProgress(context, getString(R.string.updatingCity));
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.updateUser(context, profileModel, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                Log.d(TAG, "Response : " + arg0.toString());
                checkUpdateAfter(arg0);
            }

            @Override
            public void OnFailureFromServer(String msg) {
                GlobalFunctions.hideProgress();
                GlobalFunctions.displayMessaage(context, mainView, msg);
                Log.d(TAG, "Failure : " + msg);
            }

            @Override
            public void OnError(String msg) {
                GlobalFunctions.hideProgress();
                GlobalFunctions.displayMessaage(context, mainView, msg);
                Log.d(TAG, "Error : " + msg);
            }
        }, "Edit_User");
    }

    private void checkUpdateAfter(Object arg0) {
        if (arg0 instanceof StatusModel) {
            StatusModel statusModel = (StatusModel) arg0;
            GlobalFunctions.displayMessaage(context, mainView, statusModel.getMessage());
        } else {
            /*Profile Model */
            ProfileModel profileModel = (ProfileModel) arg0;
            GlobalFunctions.setProfile(context, profileModel);
            showAlertDialog(context);
        }
    }

    private void showAlertDialog(final Context context) {
        final AlertDialog alertDialog = new AlertDialog(context);
        alertDialog.setCancelable(false);
        alertDialog.setIcon(R.drawable.app_icon);
        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage(getResources().getString(R.string.cityUpdatedText));
        alertDialog.setPositiveButton(getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                //detail = GlobalFunctions.getProfile(context);
                // activity.onBackPressed();
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });

        alertDialog.show();
    }

    private void setCityList() {

        if (cityListModel != null) {
            cityStringList.clear();

            for (int i = 0; i < cityListModel.getCityList().size(); i++) {
                cityStringList.add(cityListModel.getCityList().get(i).getName());
            }
            if (cityAdapter == null) {
                cityAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, cityStringList);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                select_city_spinner.setAdapter(cityAdapter);
                select_city_spinner.setTitle(getString(R.string.select_your_city));
                select_city_spinner.setPositiveButton(getString(R.string.ok));

               /* int pos=0;
                carType_sp.setSelection(pos);*/
            }
            synchronized (cityAdapter) {
                cityAdapter.notifyDataSetChanged();
            }
        }

    }


    private void getCityList(Context context) {
        GlobalFunctions.showProgress(context, getString(R.string.loading));
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getCityList(context, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                Log.d(TAG, "Response : " + arg0.toString());
                if (arg0 instanceof CountryListModel) {
                    CountryListModel countryListModel = (CountryListModel) arg0;
                    setThisPage(countryListModel);
                }
            }


            @Override
            public void OnFailureFromServer(String msg) {
                GlobalFunctions.hideProgress();
                globalFunctions.displayMessaage(activity, mainView, msg);
                Log.d(TAG, "Failure : " + msg);
            }

            @Override
            public void OnError(String msg) {
                GlobalFunctions.hideProgress();
                globalFunctions.displayMessaage(activity, mainView, msg);
                Log.d(TAG, "Error : " + msg);
            }
        }, "CountryList");
    }

    private void setThisPage(CountryListModel countryList) {
        if (countryListModel == null) {
            countryListModel = new CountryListModel();
        }
        countryListModel = countryList;
        setCountryList();
//            setCityList();

    }

    private void setCountryList() {
        if (countryListModel != null) {
            countryStringList.clear();
            countryStringList = countryListModel.getNames();

            if (countryAdapter == null) {
                countryAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, countryStringList);
                countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                select_country_spinner.setAdapter(countryAdapter);
                select_country_spinner.setTitle(getString(R.string.select_your_country));
                select_country_spinner.setPositiveButton(getString(R.string.ok));

                /*int pos=0;
                planType_sp.setSelection(pos);*/

            }
            synchronized (countryAdapter) {
                countryAdapter.notifyDataSetChanged();
            }
        }

        setCityList();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_nothing, menu);
        this.menu = menu;

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_nothing, menu);
        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;
        }
        return false;
    }


    @Override
    protected void onResume() {
        toolbar_title.setText(R.string.setting);
        super.onResume();
    }

    public static void closeThisActivity() {
        if (activity != null) {
            activity.finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (activity != null) {
            activity = null;
        }
        super.onDestroy();
    }

}
