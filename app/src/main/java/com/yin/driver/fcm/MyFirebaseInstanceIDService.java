package com.yin.driver.fcm;


import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.yin.driver.services.ServerResponseInterface;
import com.yin.driver.services.ServicesMethodsManager;
import com.yin.driver.services.model.PushNotificationModel;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseInstIDService";
View mainView;


    //this method will be called
    //when the token is generated
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        //now we will have the token
        String token = FirebaseInstanceId.getInstance().getToken();
        PushNotificationModel model = new PushNotificationModel();
        model.setRegistration_id(token);
        sendPushNotificationID(getApplicationContext(), model);
      //  SendFcmTokenToserver(token);
      //for now we are displaying the token in the log
        //copy it as this method is called only when the new token is generated
        //and usually new token is only generated when the app is reinstalled or the data is cleared
        Log.d("MyRefreshedToken", token);
    }

    private void sendPushNotificationID(final Context context, PushNotificationModel pushNotificationModel){
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.sendPushNotificationID(context, pushNotificationModel, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                Log.d(TAG, "Response : "+ arg0.toString());
            }

            @Override
            public void OnFailureFromServer(String msg) {
                Log.d(TAG, "Failure : "+ msg);
            }

            @Override
            public void OnError(String msg) {
                Log.d(TAG, "Error : "+ msg);
            }
        }, "Send_Push_Notification_ID");
    }

}