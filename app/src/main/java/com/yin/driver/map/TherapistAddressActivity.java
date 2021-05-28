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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.yin.driver.MainActivity;
import com.yin.driver.R;
import com.yin.driver.global.GlobalFunctions;
import com.yin.driver.global.GlobalVariables;
import com.yin.driver.services.model.AddressModel;
import com.yin.driver.util.PermissionUtils;
import com.yin.driver.widget.TouchableWrapper;

import java.util.ArrayList;
import java.util.List;


public class TherapistAddressActivity extends AppCompatActivity implements MapWrapperLayout.OnDragListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.OnConnectionFailedListener,
        TouchableWrapper.UpdateMapAfterUserInterection{

    public static final String
            TAG                                                     = "SrcPlaceOnMapActivity",
            BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_ADDRESS_MODEL       = "BundleSearchPlaceOnMapActivityAddressModel",
            BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_LOCATION_TYPE       = "BundleSearchPlaceOnMapActivityLocationType";

    private static final String GOOGLE_API_KEY = "AIzaSyAa4xTodj9XpdDy_7Te8evAkijmpMhlYV8";

    private static final int CAMERA_ZOOM_LEVEL_FOR_MAP = 16;

    private static final int
            LOCATION_PERMISSION_REQUEST_CODE    = 132,
            PERMISSION_ACCESS_COARSE_LOCATION   = 156;

    static Activity activity;

    Context context;
    View mainView;

    static TextView toolbar_title;
    static ImageView toolbar_logo;

    Toolbar toolbar;
    static ActionBar actionBar;

    //This is for restriciting search only to india, for other countries you can specify lat lng by changing the below values.
    private static final LatLngBounds BOUNDS_GREATER_INDIA = new LatLngBounds(
            new LatLng(8.062148, 68.212642), new LatLng(37.372499, 96.513423));

    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;

    private GlobalVariables.LOCATION_TYPE locationType = GlobalVariables.LOCATION_TYPE.NONE;

    private AddressModel mAddress;
    boolean miSConnectedDone = false;

    private View mMarkerParentView;
    private ImageView mMarkerImageView;

    private PlacesClient placesClient;

    private int
            imageParentWidth    = -1,
            imageParentHeight   = -1,
            imageHeight         = -1,
            centerX             = -1,
            centerY             = -1;

    private CustomMapFragment
            mCustomMapFragment;

    private GoogleMap
            mMap;

    private Button
            confirmButton;

    private GoogleApiClient
            googleApiClient;

    private GlobalFunctions
            globalFunctions;

    private OnSuccessListener fetchPlaceResponseHandler = new OnSuccessListener() {
        @Override
        public void onSuccess(Object object) {
            if(object instanceof FetchPlaceResponse) {
                FetchPlaceResponse fetchPlaceResponse = (FetchPlaceResponse) object;
                if (fetchPlaceResponse != null) {
                    fetchPlaceResponse.getPlace();
                    hideKeyboard();
                    Place place = fetchPlaceResponse.getPlace();
                    String address = place.getAddress();
                    if (mAddress == null) {
                        mAddress = new AddressModel();
                    }
                    mAddress.setAddress(address);
                    mAddress.setLatitude(place.getLatLng().latitude);
                    mAddress.setLongitude(place.getLatLng().longitude);

                    LatLng newLatLngTemp = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                    updateLocation(newLatLngTemp, true);

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
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);


            final List<Place.Field> fieldList = new ArrayList<>();
            fieldList.add(Place.Field.NAME);
            fieldList.add(Place.Field.ADDRESS);
            fieldList.add(Place.Field.LAT_LNG);

            FetchPlaceRequest fetchPlaceRequest =
                    FetchPlaceRequest.builder(placeId, fieldList)
                            .build();


            Task<FetchPlaceResponse> fetchPlaceRequestTask = placesClient.fetchPlace(fetchPlaceRequest);

            fetchPlaceRequestTask.addOnSuccessListener(fetchPlaceResponseHandler);

        }
    };


    public static Intent newInstance(Context context, AddressModel addressModel, GlobalVariables.LOCATION_TYPE locationType){
        Intent intent = new Intent(context, TherapistAddressActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_ADDRESS_MODEL, addressModel);
        bundle.putSerializable(BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_LOCATION_TYPE, locationType);
        intent.putExtras(bundle);
        return intent;
    }

    public static Intent newInstance(Context context, AddressModel addressModel){
        Intent intent = new Intent(context, TherapistAddressActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_ADDRESS_MODEL, addressModel);
        bundle.putSerializable(BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_LOCATION_TYPE, GlobalVariables.LOCATION_TYPE.NONE);
        intent.putExtras(bundle);
        return intent;
    }

    public static Intent newInstance(Context context){
        Intent intent = new Intent(context, TherapistAddressActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_ADDRESS_MODEL, null);
        bundle.putSerializable(BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_LOCATION_TYPE, GlobalVariables.LOCATION_TYPE.NONE);
        intent.putExtras(bundle);
        return intent;
    }

    public static Intent newInstance(Context context, GlobalVariables.LOCATION_TYPE locationType){
        Intent intent = new Intent(context, TherapistAddressActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_ADDRESS_MODEL, null);
        bundle.putSerializable(BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_LOCATION_TYPE, locationType);
        intent.putExtras(bundle);
        return intent;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.therapist_address_activity);
        activity = this;
        context = this;

        Places.initialize(AppController.getInstance().getApplicationContext(), GOOGLE_API_KEY);

        initilizeMap();

        //  globalFunctions = AppController.getInstance().getGlobalFunctions();

        mAutocompleteView = (AutoCompleteTextView) findViewById(R.id.googleplacesearch);
        mMarkerParentView = findViewById(R.id.marker_view_incl);
        mMarkerImageView = (ImageView) findViewById(R.id.marker_icon_view);
        confirmButton = (Button) findViewById(R.id.bt_confirm);

        //mAutocompleteView.setPadding(0, GlobalFunctions.getStatusBarHeight(context), 0, 0);

        mainView = mAutocompleteView;

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        //toolbar.setPadding(0, GlobalFunctions.getStatusBarHeight(mainContext), 0, 0);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbar_logo = (ImageView) toolbar.findViewById(R.id.tool_bar_logo);

        mainView = toolbar;

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
       // setOptionsMenuVisiblity(false);

        placesClient = Places.createClient(context);

        mAdapter = new PlaceAutocompleteAdapter(this, R.layout.google_places_search_items, placesClient, null);


        mAutocompleteView.setAdapter(mAdapter);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        mAutocompleteView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mAutocompleteView.getRight() - mAutocompleteView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        mAutocompleteView.setText("");
                        return true;
                    }
                }
                if (event.getRawX() <= (mAutocompleteView.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                    setResult(false);
                    return true;
                }
                return false;
            }
        });

        try {
            Intent intent = getIntent();
            mAddress = (AddressModel) intent.getExtras().getSerializable(BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_ADDRESS_MODEL);
            if(mAddress!=null) {
                if (mAddress.getLatitude() == 0 || mAddress.getLongitude() == 0) {
                    GetLatLon();
                } else {
                    LatLng newLatLngTemp = new LatLng(mAddress.getLatitude(), mAddress.getLongitude());
                    updateLocation(newLatLngTemp, true);
                }
            }else{
                GetLatLon();
            }
        }
        catch (Exception e) { }

       /* mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 *//* clientId *//*, this)
                .addApi(Places.GEO_DATA_API)
                .build();*/

        // mAdapter = new PlaceAutocompleteAdapter(this, R.layout.google_places_search_items, mGoogleApiClient, null, null);

        //TODO:In order to Restrict search to India uncomment this and comment the above line
        /*
        mAdapter = new PlaceAutocompleteAdapter(this, R.layout.google_places_search_items,
                mGoogleApiClient, BOUNDS_GREATER_INDIA, null);
         */

       /* mAutocompleteView.setAdapter(mAdapter);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        mAutocompleteView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mAutocompleteView.getRight() - mAutocompleteView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        mAutocompleteView.setText("");
                        return true;
                    }
                }
                if (event.getRawX() <= (mAutocompleteView.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                    setResult(false);
                    return true;
                }
                return false;
            }
        });

        try {
            Intent intent = getIntent();
            locationType = (GlobalVariables.LOCATION_TYPE) intent.getSerializableExtra(BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_LOCATION_TYPE);
            mAddress = (AddressModel) intent.getExtras().getSerializable(BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_ADDRESS_MODEL);

            if(mAddress!=null) {
                if (mAddress.getLatitude() == 0 || mAddress.getLongitude() == 0) {
                    GetLatLon();
                } else {
                    LatLng newLatLngTemp = new LatLng(mAddress.getLatitude(), mAddress.getLongitude());
                    updateLocation(newLatLngTemp, true);
                }
            }else{
                GetLatLon();
            }
        }
        catch (Exception e) { }
*/
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // setResult(true);
                closeThisActivity();
                Intent intent = new Intent(TherapistAddressActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

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
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

        }
    }

    private void initilizeMap() {
        if (mMap == null) {
            mCustomMapFragment = ((CustomMapFragment) getFragmentManager().findFragmentById(R.id.map));
            mCustomMapFragment.setOnDragListener(this);
            mCustomMapFragment.getMapAsync(this);
            if (mMap == null) {
                Log.d(TAG,"Sorry! unable to create maps");
            }
        }
    }
    public void setLatLng(double latitude, double longitude){
        if(mAddress == null){mAddress = new AddressModel();}
        mAddress.setLatitude(latitude);
        mAddress.setLongitude(longitude);
    }

    public LatLng getLatLng(){
        if(mAddress!=null){
            return new LatLng(mAddress.getLatitude(), mAddress.getLongitude());
        }
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        updateLocation(getLatLng(), true);
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        setLatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude);
        updateLocation(getLatLng(), true);
        return false;
    }

    @Override
    protected void onResume() {
        int titleResource = R.string.select_location;
        if(locationType== GlobalVariables.LOCATION_TYPE.PICKUP){titleResource = R.string.pickup_location;}
        else if(locationType== GlobalVariables.LOCATION_TYPE.DROP){titleResource = R.string.drop_location;}
        setTitle(titleResource);
        super.onResume();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }


    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(!miSConnectedDone) {
            miSConnectedDone = true;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                try {
                    @SuppressLint("MissingPermission") Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    double lat = lastLocation.getLatitude(), lon = lastLocation.getLongitude();
                    setLatLng(lat,lon);
                    updateLocation(getLatLng(),true);
                } catch(Exception e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void GetLatLon() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }else {
            try{
                @SuppressLint("MissingPermission") Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                double lat = lastLocation.getLatitude(), lon = lastLocation.getLongitude();
                setLatLng(lat,lon);
            } catch(Exception e) { }

            if (sLocationEnabled(this)) {
                googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();
                googleApiClient.connect();
            }else {
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

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TherapistAddressActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                TherapistAddressActivity.this.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    public boolean sLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private void setResult(boolean isSuccess){

        Intent intent = new Intent();
        intent.putExtra(BUNDLE_SEARCH_PLACE_ON_MAP_ACTIVITY_ADDRESS_MODEL,mAddress);

        if(isSuccess) setResult(RESULT_OK, intent);
        else setResult(RESULT_CANCELED, intent);

        closeThisActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_nothing, menu);
        return true;
    }

    @Override
    public void onDrag(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            Projection projection = (mMap != null && mMap.getProjection() != null) ? mMap.getProjection() : null;
            if (projection != null) {
                LatLng centerLatLng = projection.fromScreenLocation(new Point(
                        centerX, centerY));
                updateLocation(centerLatLng,false);
            }
        }
    }

    private void updateLocation(LatLng centerLatLng, boolean isCameraZoom) {
        if (centerLatLng != null) {
            setLatLng(centerLatLng.latitude, centerLatLng.longitude);

            if(isCameraZoom) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(centerLatLng, CAMERA_ZOOM_LEVEL_FOR_MAP);
                if (mMap != null)
                    mMap.animateCamera(cameraUpdate);
                /*All Zooming camer is an showcase so we dont want to show the suggestion again to stop the loop of Functionality.*/
                //mAutocompleteView.setOnItemClickListener(null);
            }

            Address address = globalFunctions.getAddressfromLatLng(activity, centerLatLng.latitude, centerLatLng.longitude);

            if(address!=null) {
                mAddress = globalFunctions.getAddressModelFromAdderess(address);
            }
            String completeAddress = null;
            if(mAddress.getAddress()!=null){completeAddress = mAddress.getAddress();}
            else{ completeAddress = globalFunctions.getAddressTextFromModelExcludingNameAndAddresswithComma(context, mAddress);}

            if (completeAddress != null) {
                //mAutocompleteView.setOnItemClickListener(null);
                mAutocompleteView.setText(completeAddress);
                mAutocompleteView.dismissDropDown();
                //mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(false);
    }

    public static void closeThisActivity(){
        if(activity!=null){activity.finish();}
    }

    @Override
    public void onUpdateMapAfterUserInterection() {

    }
}
