package com.yin.driver.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.yin.driver.AppController;
import com.yin.driver.R;
import com.yin.driver.global.GlobalFunctions;
import com.yin.driver.global.GlobalVariables;
import com.yin.driver.services.ServerResponseInterface;
import com.yin.driver.services.ServicesMethodsManager;
import com.yin.driver.services.model.AddressModel;
import com.yin.driver.services.model.DriverTaskModel;
import com.yin.driver.services.model.DriverTaskUpdateMethodModel;
import com.yin.driver.util.PermissionUtils;
import com.yin.driver.widget.TouchableWrapper;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class SearchPlaceOnMapActivity extends AppCompatActivity implements MapWrapperLayout.OnDragListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.OnConnectionFailedListener,
        TouchableWrapper.UpdateMapAfterUserInterection {

    public static final String
            TAG = "SrcPlaceOnMapActivity",
            BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_ADDRESS_MODEL = "BundleSearchPlaceOnMapActivityAddressModel",
            BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_DRIVER_TASK_MODEL = "BundleSearchPlaceOnMapActivityDriverTaskModel",
            BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_LOCATION_TYPE = "BundleSearchPlaceOnMapActivityLocationType";


    private static final int CAMERA_ZOOM_LEVEL_FOR_MAP = 16;

    private static final int
            LOCATION_PERMISSION_REQUEST_CODE = 132,
            PERMISSION_ACCESS_COARSE_LOCATION = 156;

    static Activity activity;

    Context context;
    View mainView;

    //This is for restriciting search only to india, for other countries you can specify lat lng by changing the below values.
    private static final LatLngBounds BOUNDS_GREATER_INDIA = new LatLngBounds(
            new LatLng( 8.062148, 68.212642 ), new LatLng( 37.372499, 96.513423 ) );

    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;
    String s;
    private GlobalVariables.LOCATION_TYPE locationType = GlobalVariables.LOCATION_TYPE.NONE;

    private AddressModel mAddress;
    boolean miSConnectedDone = false;

    private View mMarkerParentView;
    private ImageView mMarkerImageView;

    Toolbar toolbar;
    ActionBar actionBar;
    String mTitle;
    int mResourceID, titleResourseID;
    TextView toolbar_title;
    ImageView tool_bar_back_icon, tool_bar_icon;

    private PlacesClient placesClient;

    private int
            imageParentWidth = -1,
            imageParentHeight = -1,
            imageHeight = -1,
            centerX = -1,
            centerY = -1;

    private CustomMapFragment
            mCustomMapFragment;

    private GoogleMap
            mMap;

    private static Button
            confirmButton;

    DriverTaskUpdateMethodModel driverTaskUpdateMethodModel;

    private GoogleApiClient
            googleApiClient;

    DriverTaskModel driverTaskModel = null;

    private GlobalFunctions
            globalFunctions;

    CircleImageView list_driver_iv, list_user_iv, theapist_iv;
    CircleImageView therapist_location_car_iv, therapist_pickup_car_iv, customer_location_car_iv;
    TextView therapist_location_car_tv, therapist_pickup_car_tv, customer_location_car_tv;
    TextView driver_name_tv, row_list_location_tv, user_namev, user_no_tv,
            user_time_tv, therapists_name_tv, therapists_no_tv, therapists_time_tv;

    private OnSuccessListener fetchPlaceResponseHandler = new OnSuccessListener() {
        @Override
        public void onSuccess(Object object) {
            if (object instanceof FetchPlaceResponse) {
                FetchPlaceResponse fetchPlaceResponse = ( FetchPlaceResponse ) object;
                if (fetchPlaceResponse != null) {
                    fetchPlaceResponse.getPlace();
                    hideKeyboard();
                    com.google.android.libraries.places.api.model.Place place = fetchPlaceResponse.getPlace();
                    String address = place.getAddress();
                    if (mAddress == null) {
                        mAddress = new AddressModel();
                    }
                    mAddress.setAddress( address );
                    mAddress.setLatitude( place.getLatLng().latitude );
                    mAddress.setLongitude( place.getLatLng().longitude );

                    LatLng newLatLngTemp = new LatLng( place.getLatLng().latitude, place.getLatLng().longitude );
                    updateLocation( newLatLngTemp, true );

                }
            }
        }
    };

/*    private ResultCallback<PlaceBuffer>
            mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(PlaceBuffer places) {
                    if (!places.getStatus().isSuccess()) {
                        places.release();
                        return;
                    }
                    final Place place = places.get(0);
                    hideKeyboard();
                    String address = place.getAddress().toString();
                    if(mAddress == null){mAddress = new AddressModel();}
                    mAddress.setAddress(address);
                    mAddress.setLatitude(place.getLatLng().latitude);
                    mAddress.setLongitude(place.getLatLng().longitude);

                    LatLng newLatLngTemp = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                    updateLocation(newLatLngTemp, true);
                }
            };*/

    /*  private AdapterView.OnItemClickListener
              mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                      final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                      final String placeId = String.valueOf(item.placeId);

                      PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                              .getPlaceById(mGoogleApiClient, placeId);
                      placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                  }
              };
  */
    private AdapterView.OnItemClickListener
            mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView <?> parent, View view, int position, long id) {

            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem( position );
            final String placeId = String.valueOf( item.placeId );


            final List <Place.Field> fieldList = new ArrayList <>();
            fieldList.add( com.google.android.libraries.places.api.model.Place.Field.NAME );
            fieldList.add( com.google.android.libraries.places.api.model.Place.Field.ADDRESS );
            fieldList.add( com.google.android.libraries.places.api.model.Place.Field.LAT_LNG );

            FetchPlaceRequest fetchPlaceRequest =
                    FetchPlaceRequest.builder( placeId, fieldList )
                            .build();


            Task <FetchPlaceResponse> fetchPlaceRequestTask = placesClient.fetchPlace( fetchPlaceRequest );

            fetchPlaceRequestTask.addOnSuccessListener( fetchPlaceResponseHandler );

        }
    };


    public static Intent newInstance(Context context, AddressModel addressModel, GlobalVariables.LOCATION_TYPE locationType) {
        Intent intent = new Intent( context, SearchPlaceOnMapActivity.class );
        Bundle bundle = new Bundle();
        bundle.putSerializable( BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_ADDRESS_MODEL, addressModel );
        bundle.putSerializable( BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_LOCATION_TYPE, locationType );
        intent.putExtras( bundle );
        return intent;
    }

    public static Intent newInstance(Context context, DriverTaskModel driverTaskModel) {
        Intent intent = new Intent( context, SearchPlaceOnMapActivity.class );
        Bundle bundle = new Bundle();
        bundle.putSerializable( BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_DRIVER_TASK_MODEL, driverTaskModel );
        intent.putExtras( bundle );
        return intent;
    }

    public static Intent newInstance(Context context, AddressModel addressModel) {
        Intent intent = new Intent( context, SearchPlaceOnMapActivity.class );
        Bundle bundle = new Bundle();
        bundle.putSerializable( BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_ADDRESS_MODEL, addressModel );
        bundle.putSerializable( BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_LOCATION_TYPE, GlobalVariables.LOCATION_TYPE.NONE );
        intent.putExtras( bundle );
        return intent;
    }

    public static Intent newInstance(Context context) {
        Intent intent = new Intent( context, SearchPlaceOnMapActivity.class );
        Bundle bundle = new Bundle();
        bundle.putString( BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_ADDRESS_MODEL, null );
        bundle.putSerializable( BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_LOCATION_TYPE, GlobalVariables.LOCATION_TYPE.NONE );
        intent.putExtras( bundle );
        return intent;
    }

    public static Intent newInstance(Context context, GlobalVariables.LOCATION_TYPE locationType) {
        Intent intent = new Intent( context, SearchPlaceOnMapActivity.class );
        Bundle bundle = new Bundle();
        bundle.putString( BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_ADDRESS_MODEL, null );
        bundle.putSerializable( BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_LOCATION_TYPE, locationType );
        intent.putExtras( bundle );
        return intent;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_search_place_on_map );
        activity = this;
        context = this;

        Places.initialize( AppController.getInstance().getApplicationContext(), activity.getString( R.string.GoogleAPIKey ) );

        initilizeMap();


        toolbar = findViewById( R.id.tool_bar ); // Attaching the layout to the toolbar object
        tool_bar_back_icon = findViewById( R.id.tool_bar_back_icon );
//        toolbar.setPadding(0, globalFunctions.getStatusBarHeight(context), 0, 0);
//        toolbar.setNavigationIcon(R.drawable.ic_back);
//        toolbar.setContentInsetsAbsolute(0, 0);
//        toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        // tool_bar_logo.setImageResource(R.drawable.ic_menu_back);
//        toolbar.setTitle("");

        tool_bar_back_icon.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        } );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags( WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS );
            window.clearFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            window.setStatusBarColor( ContextCompat.getColor( this, R.color.sends ) );
        }

        //  globalFunctions = AppController.getInstance().getGlobalFunctions();

        mAutocompleteView = ( AutoCompleteTextView ) findViewById( R.id.googleplacesearch );
        mMarkerParentView = findViewById( R.id.marker_view_incl );
        mMarkerImageView = ( ImageView ) findViewById( R.id.marker_icon_view );
        confirmButton = ( Button ) findViewById( R.id.bt_confirm );


        //mAutocompleteView.setPadding(0, GlobalFunctions.getStatusBarHeight(context), 0, 0);

        mainView = mAutocompleteView;

        placesClient = com.google.android.libraries.places.api.Places.createClient( context );

        mAdapter = new PlaceAutocompleteAdapter( this, R.layout.google_places_search_items, placesClient, null );


        mAutocompleteView.setAdapter( mAdapter );
        mAutocompleteView.setOnItemClickListener( mAutocompleteClickListener );

        mAutocompleteView.setOnTouchListener( new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mAutocompleteView.getRight() - mAutocompleteView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        mAutocompleteView.setText( "" );
                        return true;
                    }
                }
                if (event.getRawX() <= (mAutocompleteView.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                    setResult( false );
                    return true;
                }
                return false;
            }
        } );

        try {
            Intent intent = getIntent();
            mAddress = ( AddressModel ) intent.getExtras().getSerializable( BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_ADDRESS_MODEL );
            if (mAddress != null) {
                if (mAddress.getLatitude() == 0 || mAddress.getLongitude() == 0) {
                    GetLatLon();
                } else {
                    LatLng newLatLngTemp = new LatLng( mAddress.getLatitude(), mAddress.getLongitude() );
                    updateLocation( newLatLngTemp, true );
                }
            } else {
                GetLatLon();
            }
        } catch (Exception e) {
        }


        list_driver_iv = findViewById( R.id.row_list_news_list_driver_iv );
        list_user_iv = findViewById( R.id.row_list_news_list_user_iv );
        theapist_iv = findViewById( R.id.row_list_news_list_theapist_iv );
        therapist_location_car_iv = findViewById( R.id.row_list_therapist_location_car_iv );
        therapist_pickup_car_iv = findViewById( R.id.row_list_therapist_pickup_car_iv );
        customer_location_car_iv = findViewById( R.id.row_list_customer_location_car_iv );
        driver_name_tv = findViewById( R.id.row_list_news_list_driver_name_tv );
        row_list_location_tv = findViewById( R.id.row_list_location_tv );
        user_namev = findViewById( R.id.row_list_news_list_user_namev );
        user_no_tv = findViewById( R.id.row_list_user_no_tv );
        user_time_tv = findViewById( R.id.row_list_user_time_tv );
        therapists_name_tv = findViewById( R.id.row_list_news_list_therapists_name_tv );
        therapists_no_tv = findViewById( R.id.row_list_therapists_no_tv );
        therapists_time_tv = findViewById( R.id.row_list_therapists_time_tv );
        therapist_location_car_tv = findViewById( R.id.row_list_therapist_location_car_tv );
        therapist_pickup_car_tv = findViewById( R.id.row_list_therapist_pickup_car_tv );
        customer_location_car_tv = findViewById( R.id.row_list_customer_location_car_tv );


        if (getIntent() != null) {
            driverTaskModel = ( DriverTaskModel ) getIntent().getSerializableExtra( BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_DRIVER_TASK_MODEL );
        }
        Log.d( "cuehvj", "" + driverTaskModel );

        if (driverTaskModel != null) {
            setThisPage( driverTaskModel );
        }

        confirmButton.setFocusableInTouchMode(false);



        confirmButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!driverTaskModel.getStatus().equalsIgnoreCase( GlobalVariables.DRIVER_TASK_WAY_To_COMPLETE )) {
                    validateInput();
                }
            }
        } );

        confirmButton.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction())
                {
                    view.performClick();
                    return true;
                }
                return false;
            }
        });
    }



    private void setThisPage(DriverTaskModel driverTaskModel) {

        if (driverTaskModel != null) {

            if (driverTaskModel.getDriver_first_name() != null && driverTaskModel.getDriver_last_name() != null) {
                driver_name_tv.setText( driverTaskModel.getDriver_first_name() + " " + driverTaskModel.getDriver_last_name() );
            }

            if (driverTaskModel.getAddress() != null) {
                row_list_location_tv.setText( driverTaskModel.getAddress() );
            }
            if (driverTaskModel.getT_therapists_name() != null) {
                therapists_name_tv.setText( driverTaskModel.getT_therapists_name() );
            }
            if (driverTaskModel.getT_therapists_name() != null) {
                user_no_tv.setText( driverTaskModel.getTherapist_id() );
            }
            if (driverTaskModel.getT_mobile_number() != null) {
                therapists_no_tv.setText( driverTaskModel.getT_mobile_number() );
            }
            if (driverTaskModel.getBooking_time() != null) {
//                user_time_tv.setText( driverTaskModel.getBooking_time() );
                user_time_tv.setText( GlobalFunctions.getTimeFromDate( driverTaskModel.getBooking_time() ) );
            }
            if (driverTaskModel.getCustomer_name() != null) {
                user_namev.setText( driverTaskModel.getCustomer_name() );
            }
            if (driverTaskModel.getTherapist_pickup_time() != null) {
                therapists_time_tv.setText( GlobalFunctions.getTimeFromDate( driverTaskModel.getTherapist_pickup_time() ) );
//                therapists_time_tv.setText( driverTaskModel.getTherapist_pickup_time() );
            }

            if (driverTaskModel.getStatus() != null) {
                if (driverTaskModel.getStatus().equalsIgnoreCase( GlobalVariables.DRIVER_TASK_UPDATE_WAITING )) {
                    confirmButton.setText( activity.getString( R.string.start_ride ) );
                    setEnableButton( true );
                } else if (driverTaskModel.getStatus().equalsIgnoreCase( GlobalVariables.DRIVER_TASK_UPDATE_START_RIDE )) {
                    confirmButton.setText( activity.getString( R.string.on_the_way ) );
                    setEnableButton( true );
                } else if (driverTaskModel.getStatus().equalsIgnoreCase( GlobalVariables.DRIVER_TASK_UPDATE_ON_THE_WAY )) {
                    confirmButton.setText( activity.getString( R.string.reached_therapist ) );
                    setEnableButton( true );
                } else if (driverTaskModel.getStatus().equalsIgnoreCase( GlobalVariables.DRIVER_TASK_UPDATE_REACHED )) {
                    confirmButton.setText( activity.getString( R.string.on_the_way_to_customer ) );
                    setEnableButton( true );
                } else if (driverTaskModel.getStatus().equalsIgnoreCase( GlobalVariables.DRIVER_TASK_WAY_To_CUSTOMER_LOCATION )) {
                    confirmButton.setText( activity.getString( R.string.complete_task ) );
                    setEnableButton( true );
                } else if (driverTaskModel.getStatus().equalsIgnoreCase( GlobalVariables.DRIVER_TASK_WAY_To_COMPLETE )) {
                    confirmButton.setText( activity.getString( R.string.completed ) );
                    setEnableButton( false );

                }

            }

            if (driverTaskModel.getStatus() != null) {
                String status = driverTaskModel.getStatus();
                if (status.equalsIgnoreCase( GlobalVariables.DRIVER_TASK_UPDATE_ON_THE_WAY )) {
                    therapist_location_car_iv.setImageDrawable( getResources().getDrawable( R.drawable.ic_blue_theripast_loc ) );
                    therapist_pickup_car_iv.setImageDrawable( getResources().getDrawable( R.drawable.ic_white_theripast_pickup ) );
                    customer_location_car_iv.setImageDrawable( getResources().getDrawable( R.drawable.ic_white_customer_loc ) );

                } else if (status.equalsIgnoreCase( GlobalVariables.DRIVER_TASK_UPDATE_REACHED )) {
                    therapist_location_car_iv.setImageDrawable( getResources().getDrawable( R.drawable.ic_blue_theripast_loc ) );
                    therapist_pickup_car_iv.setImageDrawable( getResources().getDrawable( R.drawable.ic_blue_therapist_pickup ) );
                    customer_location_car_iv.setImageDrawable( getResources().getDrawable( R.drawable.ic_white_customer_loc ) );

                } else if (status.equalsIgnoreCase( GlobalVariables.DRIVER_TASK_WAY_To_CUSTOMER_LOCATION )) {
                    therapist_location_car_iv.setImageDrawable( getResources().getDrawable( R.drawable.ic_blue_theripast_loc ) );
                    therapist_pickup_car_iv.setImageDrawable( getResources().getDrawable( R.drawable.ic_blue_therapist_pickup ) );
                    customer_location_car_iv.setImageDrawable( getResources().getDrawable( R.drawable.ic_blue_customer_loc ) );

                } else if (status.equalsIgnoreCase( GlobalVariables.DRIVER_TASK_WAY_To_COMPLETE )) {
                    therapist_location_car_iv.setImageDrawable( getResources().getDrawable( R.drawable.ic_blue_theripast_loc ) );
                    therapist_pickup_car_iv.setImageDrawable( getResources().getDrawable( R.drawable.ic_blue_therapist_pickup ) );
                    customer_location_car_iv.setImageDrawable( getResources().getDrawable( R.drawable.ic_blue_customer_loc ) );
                }

            }
        }
    }

    private void setEnableButton(boolean isEnabled) {
        if (isEnabled) {
            confirmButton.setClickable( true );
            confirmButton.setFocusable( true );
            confirmButton.setFocusableInTouchMode( true );
        } else {
            confirmButton.setClickable( false );
            confirmButton.setFocusable( false );
            confirmButton.setFocusableInTouchMode( false );
        }
    }

    public void validateInput() {

        if (driverTaskUpdateMethodModel == null) {
            driverTaskUpdateMethodModel = new DriverTaskUpdateMethodModel();
        }
        if (driverTaskModel.getId() != null) {
            driverTaskUpdateMethodModel.setId( driverTaskModel.getId() );
        }
 /*       if (driverTaskModel.getStatus() != null) {
            driverTaskUpdateMethodModel.setStatus( driverTaskModel.getStatus() );
        }*/
        if (driverTaskModel.getCurrent_latitude() != null) {
//            driverTaskUpdateMethodModel.setCurrent_latitude(driverTaskModel.getCurrent_latitude());

            driverTaskUpdateMethodModel.setCurrent_latitude( "12.87" );
        }
        if (driverTaskModel.getCurrent_longitude() != null) {
//            driverTaskUpdateMethodModel.setCurrent_longitude(driverTaskModel.getCurrent_longitude());

            driverTaskUpdateMethodModel.setCurrent_longitude( "10.76" );
        }
        if (driverTaskModel.getStatus() != null) {
            if (driverTaskModel.getStatus().equalsIgnoreCase( GlobalVariables.DRIVER_TASK_UPDATE_WAITING )) {
                driverTaskUpdateMethodModel.setStatus( GlobalVariables.DRIVER_TASK_UPDATE_START_RIDE );
            } else if (driverTaskModel.getStatus().equalsIgnoreCase( GlobalVariables.DRIVER_TASK_UPDATE_START_RIDE )) {
                driverTaskUpdateMethodModel.setStatus( GlobalVariables.DRIVER_TASK_UPDATE_ON_THE_WAY );
            } else if (driverTaskModel.getStatus().equalsIgnoreCase( GlobalVariables.DRIVER_TASK_UPDATE_ON_THE_WAY )) {
                driverTaskUpdateMethodModel.setStatus( GlobalVariables.DRIVER_TASK_UPDATE_REACHED );
            } else if (driverTaskModel.getStatus().equalsIgnoreCase( GlobalVariables.DRIVER_TASK_UPDATE_REACHED )) {
                driverTaskUpdateMethodModel.setStatus( GlobalVariables.DRIVER_TASK_WAY_To_CUSTOMER_LOCATION );
            } else if (driverTaskModel.getStatus().equalsIgnoreCase( GlobalVariables.DRIVER_TASK_WAY_To_CUSTOMER_LOCATION )) {
                driverTaskUpdateMethodModel.setStatus( GlobalVariables.DRIVER_TASK_WAY_To_COMPLETE );
            }
        }
        driverTaskUpdate( context, driverTaskUpdateMethodModel );
    }

    private void driverTaskUpdate(final Context context, DriverTaskUpdateMethodModel model) {
        GlobalFunctions.showProgress( context, getString( R.string.loading ) );
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.driverUpdateTask( context, model, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
//                hideLoading();
                Log.d( TAG, "Response : " + arg0.toString() );
                GlobalFunctions.hideProgress();
                if (arg0 instanceof DriverTaskModel) {
                    driverTaskModel = ( DriverTaskModel ) arg0;
                    setThisPage( driverTaskModel );
                }
            }

            @Override
            public void OnFailureFromServer(String msg) {
//                hideLoading();
                GlobalFunctions.hideProgress();
                Toast.makeText( context, msg, Toast.LENGTH_SHORT ).show();
                Log.d( TAG, "Failure : " + msg );
            }

            @Override
            public void OnError(String msg) {
//                hideLoading();
                GlobalFunctions.hideProgress();
                Toast.makeText( context, msg, Toast.LENGTH_SHORT ).show();
                Log.d( TAG, "Error : " + msg );
            }
        }, "Register_User" );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( displaymetrics );

        imageParentWidth = mMarkerParentView.getWidth();
        imageParentHeight = mMarkerParentView.getHeight();
        imageHeight = mMarkerImageView.getHeight();

        centerX = imageParentWidth / 2;
        centerY = (imageParentHeight / 2) + (imageHeight / 2);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent( intent );
        handleIntent( intent );
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals( intent.getAction() )) {
            String query = intent.getStringExtra( SearchManager.QUERY );

        }
    }

    private void initilizeMap() {
        if (mMap == null) {
            mCustomMapFragment = (( CustomMapFragment ) getFragmentManager().findFragmentById( R.id.map ));
            mCustomMapFragment.setOnDragListener( this );
            mCustomMapFragment.getMapAsync( this );
            if (mMap == null) {
                Log.d( TAG, "Sorry! unable to create maps" );
            }
        }
    }

    public void setLatLng(double latitude, double longitude) {
        if (mAddress == null) {
            mAddress = new AddressModel();
        }
        mAddress.setLatitude( latitude );
        mAddress.setLongitude( longitude );
    }

    public LatLng getLatLng() {
        if (mAddress != null) {
            return new LatLng( mAddress.getLatitude(), mAddress.getLongitude() );
        }
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener( this );
        enableMyLocation();
        updateLocation( getLatLng(), true );
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission( this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true );
        } else if (mMap != null) {
            mMap.setMyLocationEnabled( true );
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        setLatLng( mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude );
        updateLocation( getLatLng(), true );
        return false;
    }

    @Override
    protected void onResume() {
        int titleResource = R.string.select_location;
        if (locationType == GlobalVariables.LOCATION_TYPE.PICKUP) {
            titleResource = R.string.pickup_location;
        } else if (locationType == GlobalVariables.LOCATION_TYPE.DROP) {
            titleResource = R.string.drop_location;
        }
        setTitle( titleResource );
        super.onResume();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }


    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = ( InputMethodManager ) this.getSystemService( Context.INPUT_METHOD_SERVICE );
            inputManager.hideSoftInputFromWindow( view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS );
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (!miSConnectedDone) {
            miSConnectedDone = true;
            if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION )
                    == PackageManager.PERMISSION_GRANTED) {
                try {
                    @SuppressLint("MissingPermission") Location lastLocation = LocationServices.FusedLocationApi.getLastLocation( googleApiClient );
                    double lat = lastLocation.getLatitude(), lon = lastLocation.getLongitude();
                    setLatLng( lat, lon );
                    updateLocation( getLatLng(), true );
                } catch (Exception e) {
                    Log.d( TAG, e.getMessage() );
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void GetLatLon() {
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION )
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION );
        } else {
            try {
                @SuppressLint("MissingPermission") Location lastLocation = LocationServices.FusedLocationApi.getLastLocation( googleApiClient );
                double lat = lastLocation.getLatitude(), lon = lastLocation.getLongitude();
                setLatLng( lat, lon );
            } catch (Exception e) {
            }

            if (sLocationEnabled( this )) {
                googleApiClient = new GoogleApiClient.Builder( this, this, this ).addApi( LocationServices.API ).build();
                googleApiClient.connect();
            } else {
                showSettingsAlert();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder( SearchPlaceOnMapActivity.this );

        // Setting Dialog Title
        alertDialog.setTitle( "GPS settings" );

        // Setting Dialog Message
        alertDialog.setMessage( "GPS is not enabled. Do you want to go to settings menu?" );

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton( "Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                Intent intent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                SearchPlaceOnMapActivity.this.startActivity( intent );
            }
        } );

        // on pressing cancel button
        alertDialog.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        } );

        // Showing Alert Message
        alertDialog.show();
    }

    public boolean sLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt( context.getContentResolver(), Settings.Secure.LOCATION_MODE );
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString( context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED );
            return !TextUtils.isEmpty( locationProviders );
        }
    }

    private void setResult(boolean isSuccess) {

        Intent intent = new Intent();
        intent.putExtra( BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_ADDRESS_MODEL, mAddress );

        if (isSuccess) setResult( RESULT_OK, intent );
        else setResult( RESULT_CANCELED, intent );

        closeThisActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.menu_address, menu );
        return true;
    }

    @Override
    public void onDrag(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            Projection projection = (mMap != null && mMap.getProjection() != null) ? mMap.getProjection() : null;
            if (projection != null) {
                LatLng centerLatLng = projection.fromScreenLocation( new Point(
                        centerX, centerY ) );
                updateLocation( centerLatLng, false );
            }
        }
    }

    private void updateLocation(LatLng centerLatLng, boolean isCameraZoom) {
        if (centerLatLng != null) {
            setLatLng( centerLatLng.latitude, centerLatLng.longitude );

            if (isCameraZoom) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( centerLatLng, CAMERA_ZOOM_LEVEL_FOR_MAP );
                if (mMap != null)
                    mMap.animateCamera( cameraUpdate );
                /*All Zooming camer is an showcase so we dont want to show the suggestion again to stop the loop of Functionality.*/
                //mAutocompleteView.setOnItemClickListener(null);
            }

            Address address = globalFunctions.getAddressfromLatLng( activity, centerLatLng.latitude, centerLatLng.longitude );

            if (address != null) {
                mAddress = globalFunctions.getAddressModelFromAdderess( address );
            }
            String completeAddress = null;
            if (mAddress.getAddress() != null) {
                completeAddress = mAddress.getAddress();
            } else {
                completeAddress = globalFunctions.getAddressTextFromModelExcludingNameAndAddresswithComma( context, mAddress );
            }

            if (completeAddress != null) {
                //mAutocompleteView.setOnItemClickListener(null);
                mAutocompleteView.setText( completeAddress );
                mAutocompleteView.dismissDropDown();
                //mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult( false );
        closeThisActivity();
    }

    public static void closeThisActivity() {
        if (activity != null) {
            activity.finish();
        }
    }

    @Override
    public void onUpdateMapAfterUserInterection() {

    }
}
