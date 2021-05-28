package com.yin.driver;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yin.driver.addon.LruBitmapCache;
import com.yin.driver.global.GlobalFunctions;
import com.yin.driver.global.GlobalVariables;


public class AppController extends MultiDexApplication {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    public boolean mUserFirstTimeLoggedIn = false;

    private GlobalVariables globalVariables;
    private GlobalFunctions globalFunctions;

    private static AppController mInstance;
    FirebaseAnalytics analyticsInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        /*startService(new Intent(this, GPSTracker.class));*/
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }


    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setShouldCache(false);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        req.setShouldCache(false);
        getRequestQueue().add(req);
    }
    public <T> void addToAnalyticsLog(@NonNull @Size(min = 1L,max = 40L) String eventName, Bundle req) {
        getAnalyticsInstance().logEvent(eventName, req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public GlobalFunctions getGlobalFunctions(){
        if(globalFunctions == null)globalFunctions = GlobalFunctions.getInstance(getApplicationContext());
        return globalFunctions;
    }
    public GlobalVariables getGlobalVariables(){
        if(globalVariables == null)globalVariables = GlobalVariables.getInstance();
        return globalVariables;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        if(mRequestQueue!=null){mRequestQueue.stop();mRequestQueue=null;}
        if(globalVariables!=null){globalVariables=null;}
        if(globalFunctions!=null){globalFunctions=null;}
        if(mImageLoader!=null){mImageLoader=null;}
//        appseeStop();
        super.onTerminate();
    }

    public FirebaseAnalytics getAnalyticsInstance() {
        if(analyticsInstance==null)analyticsInstance = FirebaseAnalytics.getInstance(this);
        return analyticsInstance;
    }
}
