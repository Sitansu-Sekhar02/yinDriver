package com.yin.driver.profile.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vlonjatg.progressactivity.ProgressRelativeLayout;
import com.yin.driver.R;
import com.yin.driver.global.GlobalFunctions;
import com.yin.driver.services.ServerResponseInterface;
import com.yin.driver.services.ServicesMethodsManager;
import com.yin.driver.services.model.ChangePasswordModel;
import com.yin.driver.services.model.ProfileCategoryListModel;
import com.yin.driver.services.model.ProfileCategoryModel;
import com.yin.driver.services.model.ProfileModel;
import com.yin.driver.services.model.ProfileServicesModel;
import com.yin.driver.services.model.StatusModel;
import com.yin.driver.view.AlertDialog;
import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    public static final String BUNDLE_KEY_CITY_HOUSE_TYPE_FRAGMENT_ORDER_SUBMIT_MODEL = "BundleKeyCreateRideFragmentOrderSubmit";


    Activity activity;
    Context context;
    View mainView, changePasswordView;

    RecyclerView recyclerView;

    ProfileCategoryListModel details = new ProfileCategoryListModel();

    List <ProfileCategoryModel> list = new ArrayList <ProfileCategoryModel>();
    List <ProfileServicesModel> servicesList;

    LinearLayoutManager layoutManager;

//    ProfileCategoryListAdapter adapter;

    ProgressRelativeLayout progressActivity;
    DilatingDotsProgressBar progressBar;

    int index = 0;
    int preLast = 0;
    boolean dataEnd = false;
    boolean loadingList = false;


    TextView name_tv, therapist_tv, update_button, email_ev;
    EditText first_name_etv, last_name_etv, mobile_etv, email_etv, password_etv, confirm_password_etv;
    CircleImageView profile_image;

    LinearLayout detailLayout, progressLayout, profile_image_ll;

    static boolean isEditing = false;

    ProfileModel detail = null;

    public ProfileFragment() {
        setHasOptionsMenu( true );
    }

    public static Fragment newInstance() {
        Fragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString( BUNDLE_KEY_CITY_HOUSE_TYPE_FRAGMENT_ORDER_SUBMIT_MODEL, null );
        fragment.setArguments( bundle );
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu( true );
        super.onCreate( savedInstanceState );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView( inflater, container, savedInstanceState );
        View view = inflater.inflate( R.layout.profile_fragment, container, false );

        activity = getActivity();
        context = getActivity();

        name_tv = ( TextView ) view.findViewById( R.id.name_tv );

        first_name_etv = ( EditText ) view.findViewById( R.id.first_name_etv );
        last_name_etv = ( EditText ) view.findViewById( R.id.last_name_etv );
        mobile_etv = ( EditText ) view.findViewById( R.id.mobile_etv );
        email_etv = ( EditText ) view.findViewById( R.id.email_etv );
        email_ev = view.findViewById( R.id.email_ev );
     /*   password_etv = ( EditText ) view.findViewById( R.id.password_etv );
        confirm_password_etv = ( EditText ) view.findViewById( R.id.confirm_password_etv );*/

        profile_image = ( CircleImageView ) view.findViewById( R.id.profile_fragment_profile_image );

        detailLayout = ( LinearLayout ) view.findViewById( R.id.detailLayout );
        progressLayout = ( LinearLayout ) view.findViewById( R.id.progressBarLayout );
        profile_image_ll = ( LinearLayout ) view.findViewById( R.id.profile_image_ll );

        profile_image_ll.setBackground( getResources().getDrawable( R.drawable.bg_gradient ) );

        recyclerView = ( RecyclerView ) view.findViewById( R.id.category_list_activity_recyclerView );
        progressActivity = view.findViewById( R.id.details_progressActivity );
        progressBar = ( DilatingDotsProgressBar ) view.findViewById( R.id.extraProgressBar );

//        update_button = ( TextView ) view.findViewById( R.id.update_button );

        mainView = name_tv;


        getProfile();

        return view;
    }

    private void setDetails() {
        if (context != null && isAdded()) {
            detail = GlobalFunctions.getProfile( context );
            setThisPage();
        }
    }

    private void setDetails(ProfileModel profile) {
        GlobalFunctions.setProfile( context, profile );
        if (context != null && isAdded()) {
            detail = GlobalFunctions.getProfile( context );
            setThisPage();
        }
        ProfileModel model = ( ProfileModel ) profile;
    }

    private void showContent() {
        if (progressActivity != null) {
            progressActivity.showContent();
        }
    }

    private void showEmptyPage() {
        if (progressActivity != null) {
            progressActivity.showEmpty( getResources().getDrawable( R.drawable.app_icon ), getString( R.string.empty ),
                    getString( R.string.no_list_to_display ) );
        }
    }

    private void getProfile() {
        GlobalFunctions.showProgress( context, getString( R.string.getting_profile ) );
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getProfile( context, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                Log.d( TAG, "Response : " + arg0.toString() );
                setDetails( ( ProfileModel ) arg0 );
            }

            @Override
            public void OnFailureFromServer(String msg) {
                GlobalFunctions.hideProgress();
                GlobalFunctions.displayMessaage( context, mainView, msg );
                Log.d( TAG, "Failure : " + msg );
                setDetails();
            }

            @Override
            public void OnError(String msg) {
                GlobalFunctions.hideProgress();
                GlobalFunctions.displayMessaage( context, mainView, msg );
                Log.d( TAG, "Error : " + msg );
                setDetails();
            }
        }, "Get Profile" );
    }


    @Override
    public void onResume() {
        /* getProfile();*/
        // MainActivity.setTitleResourseID(0);
        (( ProfileMainActivity ) activity).setTitle( getString( R.string.app_name ), R.drawable.ic_app_menu, 0 );
        super.onResume();
    }

    private void checkChangePasswordAfter(Object model) {
        if (model instanceof StatusModel) {
            StatusModel statusModel = ( StatusModel ) model;
//            GlobalFunctions.displayMessaage(context, mainView, statusModel.getMessage());
            if (statusModel.isStatus()) {
                showAlertDialog( context, statusModel.getMessage() );
            }
        }
    }

    private void showAlertDialog(final Context context, final String message) {
        final AlertDialog alertDialog = new AlertDialog( context );
        alertDialog.setCancelable( false );
        alertDialog.setIcon( R.drawable.app_icon );
        alertDialog.setTitle( getString( R.string.app_name ) );
        alertDialog.setMessage( message );
        alertDialog.setPositiveButton( getString( R.string.ok ), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                logoutUser( context );
            }
        } );

        alertDialog.show();
    }

    private void setThisPage() {
        if (detail != null) {
            if (isAdded()) {
                //  setEditPage(false);

                Log.d( "heckDetail",""+ detail );

                String fullName = detail.getFirstName() + " " + detail.getLastName();
                name_tv.setText( fullName );
                first_name_etv.setText( detail.getFirstName() );
                last_name_etv.setText( detail.getLastName() );
                mobile_etv.setText( detail.getPhone() );
                email_etv.setText( detail.getEmail() );
//                email_ev.setText( detail.getEmail() );

                try {
                    if (detail.getProfileImg() != null || !detail.getProfileImg().equals( "null" ) || !detail.getProfileImg().equalsIgnoreCase( "" )) {
                        Picasso.with( context ).load( detail.getProfileImg() ).placeholder( R.drawable.ic_boys_icon ).into( profile_image );
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    private void validateOutput(Object model) {
        if (model instanceof StatusModel) {
            StatusModel statusModel = ( StatusModel ) model;
            GlobalFunctions.displayMessaage( context, mainView, statusModel.getMessage() );
            if (statusModel.isStatus()) {
                /*Logout success, Clear all cache and reload the home page*/

            }
            GlobalFunctions.logoutApplication( context );
            // MainActivity.RestartEntireApp(context);
        }

    }


    public static boolean isEditing() {
        return isEditing;
    }

    public static void setIsEditing(boolean isEditing) {
        ProfileFragment.isEditing = isEditing;
    }


    private void updatePassword(final Context context, ChangePasswordModel changePasswordModel) {
        GlobalFunctions.showProgress( context, getString( R.string.updatingPassword ) );
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.changePassword( context, changePasswordModel, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                Log.d( TAG, "Response : " + arg0.toString() );
                checkChangePasswordAfter( arg0 );
            }

            @Override
            public void OnFailureFromServer(String msg) {
                GlobalFunctions.hideProgress();
                GlobalFunctions.displayMessaage( context, mainView, msg );
                Log.d( TAG, "Failure : " + msg );
            }

            @Override
            public void OnError(String msg) {
                GlobalFunctions.hideProgress();
                GlobalFunctions.displayMessaage( context, mainView, msg );
                Log.d( TAG, "Error : " + msg );
            }
        }, "Change_password" );
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
}
