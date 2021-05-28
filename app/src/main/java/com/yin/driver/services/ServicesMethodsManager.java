package com.yin.driver.services;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yin.driver.AppController;
import com.yin.driver.R;
import com.yin.driver.global.GlobalFunctions;
import com.yin.driver.global.GlobalVariables;
import com.yin.driver.services.model.AddCarOptionsListModel;
import com.yin.driver.services.model.AddressListModel;
import com.yin.driver.services.model.AddressModel;
import com.yin.driver.services.model.ApartmentListModel;
import com.yin.driver.services.model.AppointmentListModel;
import com.yin.driver.services.model.CarCostListModel;
import com.yin.driver.services.model.CategoryListModel;
import com.yin.driver.services.model.ChangePasswordModel;
import com.yin.driver.services.model.CityApartmentListModel;
import com.yin.driver.services.model.CountryListModel;
import com.yin.driver.services.model.CreatePlanModel;
import com.yin.driver.services.model.DocumentUploadModel;
import com.yin.driver.services.model.DriverTaskListListModel;
import com.yin.driver.services.model.DriverTaskModel;
import com.yin.driver.services.model.DriverTaskUpdateMethodModel;
import com.yin.driver.services.model.GenderModel;
import com.yin.driver.services.model.HomeIndexModel;
import com.yin.driver.services.model.IndexModel;
import com.yin.driver.services.model.KeyValueListModel;
import com.yin.driver.services.model.LoginModel;
import com.yin.driver.services.model.NewsListModel;
import com.yin.driver.services.model.NotificationListModel;
import com.yin.driver.services.model.OrderSubmitModel;
import com.yin.driver.services.model.PlanListModel;
import com.yin.driver.services.model.PostModel;
import com.yin.driver.services.model.ProfileModel;
import com.yin.driver.services.model.PushNotificationModel;
import com.yin.driver.services.model.RegisterModel;
import com.yin.driver.services.model.SessionTypeTimingsListModel;
import com.yin.driver.services.model.StatusModel;
import com.yin.driver.services.model.UploadImageModel;
import com.yin.driver.services.model.registration.OtpModel;

import org.json.JSONObject;


public class ServicesMethodsManager {

    public static final String TAG = "ServicesMethodsMgr";
    private ServerResponseInterface mUiCallBack;

    GlobalFunctions globalFunctions;
    GlobalVariables globalVariables;

    public ServicesMethodsManager() {
        globalFunctions = AppController.getInstance().getGlobalFunctions();
        globalVariables = AppController.getInstance().getGlobalVariables();
    }

    private void setCallbacks(ServerResponseInterface mCallBack) {
        mUiCallBack = mCallBack;
    }

    private void postData(final Context context, final Object obj, String URL, String query, String TAG, final boolean isCookieSave) {
        /*if(obj instanceof LoginModel){
            String token =  GlobalFunctions.getSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_TOKEN);
            if(token!=null){URL += "?"+"token="+ token;}
        }*/

        final VolleyServices request = new VolleyServices(context);
        request.setCallbacks(new VolleyServices.ResposeCallBack() {
            @Override
            public void OnSuccess(JSONObject arg0) {
                if (isCookieSave) {
                    GlobalFunctions.setSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_COOKIE, request.getCookie());
                }
                parseResponse(context, obj, arg0);
            }

            @Override
            public void OnFailure(int cause) {
                mUiCallBack.OnFailureFromServer(context.getString(cause));
            }

            @Override
            public void OnFailure(String cause) {
                mUiCallBack.OnFailureFromServer(cause);
            }
        });
        request.setBody(obj.toString());
        request.makeJsonPostRequest(context, URL, query, TAG);
    }

    private void postData(final Context context, final Object obj, String URL, String query, String TAG) {
        /*if(obj instanceof LoginModel){
            String token =  GlobalFunctions.getSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_TOKEN);
            if(token!=null){URL += "?"+"token="+ token;}
        }*/

        final VolleyServices request = new VolleyServices(context);
        request.setCallbacks(new VolleyServices.ResposeCallBack() {
            @Override
            public void OnSuccess(JSONObject arg0) {
                if (obj instanceof HomeIndexModel) {
                    GlobalFunctions.setSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_COOKIE, request.getCookie());
                }
                parseResponse(context, obj, arg0);
            }

            @Override
            public void OnFailure(int cause) {
                mUiCallBack.OnFailureFromServer(context.getString(cause));
            }

            @Override
            public void OnFailure(String cause) {
                mUiCallBack.OnFailureFromServer(cause);
            }
        });
        request.setBody(obj.toString());
        request.makeJsonPostRequest(context, URL, query, TAG);
    }

    private void postData(final Context context, final Object obj, String URL, String TAG) {
        final VolleyServices request = new VolleyServices(context);
        request.setCallbacks(new VolleyServices.ResposeCallBack() {
            @Override
            public void OnSuccess(JSONObject arg0) {
               /* if(obj instanceof LoginModel){
                    GlobalFunctions.setSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_COOKIE,request.getCookie());
                }*/
                if (obj instanceof HomeIndexModel) {
                    globalFunctions.setSharedPreferenceString(context, globalVariables.SHARED_PREFERENCE_COOKIE, request.getCookie());
                }
                parseResponse(context, obj, arg0);
            }

            @Override
            public void OnFailure(int cause) {
                mUiCallBack.OnFailureFromServer(context.getString(cause));
            }

            @Override
            public void OnFailure(String cause) {
                mUiCallBack.OnFailureFromServer(cause);
            }
        });
        request.setBody(obj.toString());
        request.makeJsonPostRequest(context, URL, null, TAG);
    }

    public void getNewsList(Context context, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
//        query = query!=null? query+"&phone="+model.getPhone() : "phone="+model.getPhone();
//        query = query!=null? query+"&otp="+OTP : "otp="+OTP;
        String URL = ServerConstants.URL_GetNewsList;
        getData(context, new NewsListModel(), URL, query, TAG);
    }

    public void getPlanList(Context context, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
//        query = query!=null? query+"&phone="+model.getPhone() : "phone="+model.getPhone();
//        query = query!=null? query+"&otp="+OTP : "otp="+OTP;
        String URL = ServerConstants.URL_GetMyCarsList;
        getData(context, new PlanListModel(), URL, query, TAG);
    }

    private void getData(@NonNull final Context context, @NonNull final Object obj, @NonNull String URL, @Nullable String query, @Nullable String TAG) {
        //if(query!=null){if(!query.equalsIgnoreCase("")){URL += "?"+query;}}
        final VolleyServices request = new VolleyServices(context);
        request.setCallbacks(new VolleyServices.ResposeCallBack() {
            @Override
            public void OnSuccess(JSONObject arg0) {
                Log.d("Response", arg0.toString());
                parseResponse(context, obj, arg0);
            }

            @Override
            public void OnFailure(int cause) {
                mUiCallBack.OnFailureFromServer(context.getString(cause));
            }

            @Override
            public void OnFailure(String cause) {
                mUiCallBack.OnFailureFromServer(cause);
            }
        });
        request.setBody(obj.toString());
        request.makeJsonGETRequest(context, URL, query, TAG);
    }

    private void parseResponse(Context context, Object obj, JSONObject resp) {
        Log.d(TAG, resp.toString());
        if (obj instanceof IndexModel) {
            IndexModel model = new IndexModel();
            if (model.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(model);
            } else {
                mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
            }
        } else if (obj instanceof NotificationListModel) {
            NotificationListModel model = new NotificationListModel();
            if (model.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(model);
            } else {
                mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
            }
        } else if (obj instanceof OtpModel) {
            StatusModel model = new StatusModel();
            if (model.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(model);
            } else {
                ProfileModel profileModel = new ProfileModel();
                if (profileModel.toObject(resp.toString())) {
                    mUiCallBack.OnSuccessFromServer(profileModel);
                } else {
                    mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
                }
            }
        } else if (obj instanceof KeyValueListModel) {
            KeyValueListModel model = new KeyValueListModel();
            if (model.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(model);
            } else {
                mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
            }
        } else if (obj instanceof LoginModel || obj instanceof ProfileModel) {
            StatusModel model = new StatusModel();
            if (model.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(model);
            } else {
                ProfileModel profileModel = new ProfileModel();
                if (profileModel.toObject(resp.toString())) {
                    mUiCallBack.OnSuccessFromServer(profileModel);
                } else {
                    mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
                }
            }
        } else if ( obj instanceof DriverTaskUpdateMethodModel) {
                DriverTaskModel driverTaskModel = new DriverTaskModel();
                if (driverTaskModel.toObject(resp.toString())) {
                    mUiCallBack.OnSuccessFromServer(driverTaskModel);
                } else {
                    mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
            }
        } else if (obj instanceof RegisterModel || obj instanceof StatusModel || obj instanceof ChangePasswordModel || obj instanceof PushNotificationModel) {
            StatusModel model = new StatusModel();
            if (model.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(model);
            } else {
                ProfileModel profileModel = new ProfileModel();
                if (profileModel.toObject(resp.toString())) {
                    mUiCallBack.OnSuccessFromServer(profileModel);
                } else {
                    mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
                }
            }
        } else if (obj instanceof CountryListModel) {
            CountryListModel model = new CountryListModel();
            if (model.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(model);
            } else {
                mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
            }
        } else if (obj instanceof AppointmentListModel) {
            AppointmentListModel model = new AppointmentListModel();
            if (model.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(model);
            } else {
                mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
            }
        } else if (obj instanceof GenderModel) {
            CategoryListModel listModel = new CategoryListModel();
            if (listModel.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(listModel);
            } else {
                StatusModel statusModel = new StatusModel();
                if (statusModel.toObject(resp.toString())) {
                    mUiCallBack.OnSuccessFromServer(statusModel);
                } else {
                    mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
                }
            }
        } else if (obj instanceof AddressModel) {
            AddressListModel listModel = new AddressListModel();
            if (listModel.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(listModel);
            } else {
                AddressModel model = new AddressModel();
                if (model.toObject(resp.toString())) {
                    mUiCallBack.OnSuccessFromServer(model);
                } else {
                    StatusModel statusModel = new StatusModel();
                    if (statusModel.toObject(resp.toString())) {
                        mUiCallBack.OnSuccessFromServer(statusModel);
                    } else {
                        mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
                    }
                }

            }
        } else if (obj instanceof AddCarOptionsListModel) {
            AddCarOptionsListModel listModel = new AddCarOptionsListModel();
            if (listModel.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(listModel);
            } else {
                StatusModel statusModel = new StatusModel();
                if (statusModel.toObject(resp.toString())) {
                    mUiCallBack.OnSuccessFromServer(statusModel);
                } else {
                    mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
                }
            }
        } else if (obj instanceof SessionTypeTimingsListModel) {
            SessionTypeTimingsListModel listModel = new SessionTypeTimingsListModel();
            if (listModel.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(listModel);
            } else {
                StatusModel statusModel = new StatusModel();
                if (statusModel.toObject(resp.toString())) {
                    mUiCallBack.OnSuccessFromServer(statusModel);
                } else {
                    mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
                }
            }
        } else if (obj instanceof AddressListModel) {
            AddressListModel listModel = new AddressListModel();
            if (listModel.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(listModel);
            } else {
                mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
            }
        } else if (obj instanceof CreatePlanModel) {
            CreatePlanModel listModel = new CreatePlanModel();
            if (listModel.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(listModel);
            } else {
                mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
            }
        } else if (obj instanceof CityApartmentListModel) {
            CityApartmentListModel listModel = new CityApartmentListModel();
            if (listModel.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(listModel);
            } else {
                mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
            }
        } else if (obj instanceof ApartmentListModel) {
            ApartmentListModel listModel = new ApartmentListModel();
            if (listModel.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(listModel);
            } else {
                mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
            }
        }else if (obj instanceof DriverTaskListListModel) {
            DriverTaskListListModel listModel = new DriverTaskListListModel();
            if (listModel.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(listModel);
            } else {
                mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
            }
        } else if (obj instanceof CarCostListModel) {
            CarCostListModel listModel = new CarCostListModel();
            if (listModel.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(listModel);
            } else {
                mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
            }
        } else if (obj instanceof OrderSubmitModel) {
            StatusModel model = new StatusModel();
            if (model.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(model);
            } else {
                mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
            }
        } else if (obj instanceof NewsListModel) {
            NewsListModel model = new NewsListModel();
            if (model.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(model);
            } else {
                mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
            }
        } else if (obj instanceof PlanListModel) {
            PlanListModel model = new PlanListModel();
            if (model.toObject(resp.toString())) {
                mUiCallBack.OnSuccessFromServer(model);
            } else {
                mUiCallBack.OnError(context.getString(R.string.ErrorResponseData));
            }
        }
    }

    public void getIndex(Context context, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        getData(context, new IndexModel(), ServerConstants.URL_GetIndex, null, TAG);
    }

    public void addToAddress(Context context, @NonNull AddressModel addressModel, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        postData(context, addressModel, ServerConstants.URL_Address, TAG);
    }

    public void updateAddress(Context context, @NonNull AddressModel addressModel, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        postData(context, addressModel, ServerConstants.URL_Address, TAG);
    }

    public void deleteAddress(Context context, @NonNull AddressModel addressModel, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        postData(context, addressModel, ServerConstants.URL_DeleteAddress, TAG);
    }

    public void getAddress(Context context, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        getData(context, new AddressListModel(), ServerConstants.URL_GetAddress, null, TAG);
    }

    public void loginUser(Context context, LoginModel loginModel, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
//        loginModel.setUuid(GlobalFunctions.getUniqueID(context));
//        loginModel.setSystemInfo(GlobalFunctions.getDevice());
//        loginModel.setDeviceType(GlobalVariables.DEVICE_TYPE);
        postData(context, loginModel, ServerConstants.URL_LoginUser, TAG);
    }

    public void registerUser(Context context, RegisterModel registerModel, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        // registerModel.setUuid(GlobalFunctions.getUniqueID(context));
        //registerModel.setSystemInfo(GlobalFunctions.getDevice());
        // registerModel.setSystemInfo(GlobalFunctions.getDevice());
        registerModel.setGender(GlobalVariables.GENDER.FEMALE);
        postData(context, registerModel, ServerConstants.URL_RegisterUser, TAG);
    }

    public void getCategoryList(Context context, @NonNull int index, @NonNull int size, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
        query = query != null ? query + "&index=" + index : "index=" + index;
        query = query != null ? query + "&size=" + size : "size=" + size;
        GenderModel genderModel = new GenderModel();
        genderModel.setGender("2");
        String URL = ServerConstants.URL_GetCategoryList;
       /* if(globalFunctions.getUserType().equals(GlobalVariables.USER_TYPE.VENDOR)){
            URL = ServerConstants.URL_GetVendorOrdersList;
        }*/
        // postData(context, new CategoryListModel(), URL, query, TAG);
        postData(context, genderModel, URL, query, TAG);
    }

    public void getMyAppointments(Context context, @NonNull int index, @NonNull int size, @NonNull int status, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
        query = query != null ? query + "&size=" + size : "size=" + size;
        query = query != null ? query + "&index=" + index : "index=" + index;
        query = query != null ? query + "&status=" + status : "status=" + status;
        //GenderModel genderModel = new GenderModel();
        //genderModel.setGender("2");
        String URL = ServerConstants.URL_GetConfirmedAppointmentsList;


        getData(context, new AppointmentListModel(), URL, query, TAG);
    }

    public void bookingStatusUpdate(Context context, @NonNull int status, @NonNull String booking_id, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
        query = query != null ? query + "&booking_id=" + booking_id : "booking_id=" + booking_id;
        query = query != null ? query + "&status=" + status : "status=" + status;
        String URL = ServerConstants.URL_BookingStatusUpdate;
        getData(context, new StatusModel(), URL, query, TAG);
    }


    public void logout(Context context, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        postData(context, new StatusModel(), ServerConstants.URL_LogoutUser, null, TAG);
    }

    public void updateUser(Context context, ProfileModel profileModel, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String url = ServerConstants.URL_GetProfile;
        postData(context, profileModel, url, TAG);
    }
    public void driverUpdateTask(Context context, DriverTaskUpdateMethodModel driverTaskUpdateMethodModel, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String url = ServerConstants.URL_DriverTaskUpdate;
        postData(context, driverTaskUpdateMethodModel, url, TAG);
    }

    public void getProfile(Context context, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
        String url = ServerConstants.URL_GetProfile;
        getData(context, new ProfileModel(), url, query, TAG);
    }

    public void changePassword(Context context, ChangePasswordModel changePasswordModel, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        postData(context, changePasswordModel, ServerConstants.URL_ChangePassword, TAG);
    }

    public void changePassword(Context context, ChangePasswordModel changePasswordModel, StatusModel model, GlobalVariables.USER_TYPE userType, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
        query = query != null ? query + "&id=" + model.getExtra() : "id=" + model.getExtra();
        query = query != null ? query + "&user_type=" + userType.toInt() : "user_type=" + userType.toInt();
        postData(context, changePasswordModel, ServerConstants.URL_ChangePassword, query, TAG);
    }

    public void sendPushNotificationID(Context context, PushNotificationModel pushNotificationModel, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
        pushNotificationModel.setDeviceType(GlobalVariables.DEVICE_TYPE);
     /*   query = query!=null? query+"&id="+model.getExtra() : "id="+model.getExtra();
        query = query!=null? query+"&user_type="+userType.toInt() : "user_type="+userType.toInt();*/
        postData(context, pushNotificationModel, ServerConstants.URL_PushNotification, query, TAG);
    }

    public void getNotification(Context context, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
        query = query != null ? query + "&user_type=" + GlobalFunctions.getUserType(context).toInt() : "user_type=" + GlobalFunctions.getUserType(context).toInt();
        String URL = ServerConstants.URL_GetNotifications;
        getData(context, new NotificationListModel(), URL, query, TAG);
    }

    public void getGuestUserCreation(Context context, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        HomeIndexModel model = new HomeIndexModel();
        model.setUid(GlobalFunctions.getUniqueID(context));
        model.setSystem_info(GlobalFunctions.getDevice());
        postData(context, model, ServerConstants.URL_GuestUserCreation, null, TAG, true);
    }

    public void sendOtp(Context context, RegisterModel registerModel, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
//        query = query!=null? query+"&phone="+mobileNumber : "phone="+mobileNumber;
        String URL = ServerConstants.URL_SendOTP;
        postData(context, registerModel, URL, query, TAG, false);
    }

    public void verifyOTP(Context context, OtpModel otpModel, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
//        query = query!=null? query+"&phone="+model.getPhone() : "phone="+model.getPhone();
//        query = query!=null? query+"&otp="+OTP : "otp="+OTP;
        String URL = ServerConstants.URL_VerifyOTP;
        postData(context, otpModel, URL, query, TAG);
    }

    public void forgotPassword(Context context, LoginModel loginModel, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
        query = query != null ? query + "&user_type=" + loginModel.getUserType() : "user_type=" + loginModel.getUserType();
        GlobalFunctions.removeSharedPreferenceKey(context, GlobalVariables.SHARED_PREFERENCE_COOKIE);
        String URL = ServerConstants.URL_ForgotPassword;
        postData(context, loginModel, URL, query, TAG);
    }

    public void sendSelectedAddress(Context context, AddressModel loginModel, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
//        query = query!=null? query+"&user_type="+loginModel.getUserType() : "user_type="+loginModel.getUserType();
        GlobalFunctions.removeSharedPreferenceKey(context, GlobalVariables.SHARED_PREFERENCE_COOKIE);
        String URL = ServerConstants.URL_ForgotPassword;
        postData(context, loginModel, URL, query, TAG);
    }

    public void getCarOptionsList(Context context, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
//        query = query!=null? query+"&phone="+model.getPhone() : "phone="+model.getPhone();
//        query = query!=null? query+"&otp="+OTP : "otp="+OTP;
        String URL = ServerConstants.URL_CreatePlan;
        getData(context, new AddCarOptionsListModel(), URL, query, TAG);
    }

    public void getCarCostList(Context context, String planTypeId, String carTypeId, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
        query = query != null ? query + "&plan_type=" + planTypeId : "plan_type=" + planTypeId;
        query = query != null ? query + "&car_type=" + carTypeId : "car_type=" + carTypeId;
        String URL = ServerConstants.URL_GetPackage;
        getData(context, new CarCostListModel(), URL, query, TAG);
    }

    public void getSessionTypeTimingsList(Context context, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
//        query = query!=null? query+"&phone="+model.getPhone() : "phone="+model.getPhone();
//        query = query!=null? query+"&otp="+OTP : "otp="+OTP;
        String URL = ServerConstants.URL_Session_Type_Timings;
        getData(context, new SessionTypeTimingsListModel(), URL, query, TAG);
    }

    public void getCityDetails(Context context, String cityID, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
        query = query != null ? query + "/" + cityID : cityID;
        String url = ServerConstants.URL_CityDetails;
        getData(context, new CityApartmentListModel(), url, query, TAG);
    }

    public void getApartmentDetails(Context context, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
//        query = query!=null? query+"/"+cityID : cityID;
        String url = ServerConstants.URL_CityDetails;
        getData(context, new ApartmentListModel(), url, query, TAG);
    }
    public void getDriverTaskList(Context context, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
//        query = query!=null? query+"/"+cityID : cityID;
        String url = ServerConstants.URL_DriverTaskList;
        getData(context, new DriverTaskListListModel(), url, query, TAG);
    }

    public void submitOrder(Context context, OrderSubmitModel orderSubmitModel, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
//        query = query!=null? query+"/"+cityID : cityID;
        String url = ServerConstants.URL_SubmitOrder;
        postData(context, orderSubmitModel, url, TAG);
    }

    public void submitOrderRequest(Context context, OrderSubmitModel orderSubmitModel, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
//        query = query!=null? query+"/"+cityID : cityID;
        String url = ServerConstants.URL_Request;
        postData(context, orderSubmitModel, url, TAG);
    }

    public void getMyPlansList(Context context, @NonNull int index, @NonNull int size, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;
        PostModel postModel = new PostModel();
        postModel.setIndex(index);
        postModel.setCount(size);
        /*query = query!=null? query+"&type/"+type : "type/"+type;
        query = query!=null? query+"&index/"+index : "index/"+index;
        query = query!=null? query+"&size/"+size : "size/"+size;*/
        postData(context, postModel, ServerConstants.URL_GetMyPlansList, query, TAG);
    }

    public void profileImageUpdate(Context context, DocumentUploadModel documentUploadModel, ServerResponseInterface mCallInterface, String TAG) {
//        setCallbacks(mCallInterface);
//        postData(context, documentUploadModel, ServerConstants.URL_UploadImageProfile, TAG);
    }

    public void uploadImage(Context context, UploadImageModel uploadImageModel, GlobalVariables.IMAGE_UPLOAD_TYPE type, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        String query = null;//"type=1";
//        String url = ServerConstants.URL_UploadImage;
//        if (type == GlobalVariables.IMAGE_UPLOAD_TYPE.PROFILE) {
//            url = ServerConstants.URL_UploadImageProfile;
//        }
//        postData(context, uploadImageModel, url, query, TAG);
    }


    public void getCityList(Context context, ServerResponseInterface mCallInterface, String TAG) {
        setCallbacks(mCallInterface);
        //http://imcrinox.com/APP/api/get_wishlist.php?category_id=3&index=0&size=10&branch_id=12

        //   String query = "type=1&top=1";
        String query = null;
        //int id = 9;
        //query = query != null ? query + "&id=" + id : "id=" + id;
        getData(context, new CountryListModel(), ServerConstants.URL_GetCityList, null, TAG);
    }
}
