package com.yin.driver.services.model;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;

public class ApartmentModel implements Serializable {
    private final String TAG = "CityModel";

    private final String
            ID                  = "id",
            TITLE               = "title",
            PINCODE             = "pincode",
            PHONE               = "phone",
            LOCATION            = "location",
            ADDRESS             = "address",
            CITY                = "city",
            CITY_NAME           = "city_name";

    String
            id                  = null,
            title               = null,
            pincode             = null,
            phone               = null,
            address             = null,
            city                = null,
            cityName            = null;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public boolean toObject(String jsonObject){
        try{
            JSONObject json = new JSONObject(jsonObject);
            this.id = json.getString(ID);

            if(json.has(TITLE)){this.title = json.getString(TITLE);}
            if(json.has(PINCODE)){this.pincode = json.getString(PINCODE);}
            if(json.has(PHONE)){this.phone = json.getString(PHONE);}
            if(json.has(ADDRESS)){this.address = json.getString(ADDRESS);}
            if(json.has(CITY)){this.city = json.getString(CITY);}
            if(json.has(CITY_NAME)){this.cityName = json.getString(CITY_NAME);}

            return true;
        }catch(Exception ex){
            Log.d(TAG, "Json Exception : " + ex);
            return false;}

    }

    @Override
    public String toString(){
        String returnString = null;
        try{
            JSONObject jsonMain = new JSONObject();
            jsonMain.put(ID, this.id);
            jsonMain.put(TITLE, this.title);
            jsonMain.put(PHONE, this.phone);
            jsonMain.put(PINCODE, this.pincode);
            jsonMain.put(ADDRESS, this.address);
            jsonMain.put(CITY, this.city);
            jsonMain.put(CITY_NAME, this.cityName);

            returnString = jsonMain.toString();
        }
        catch (Exception ex){Log.d(TAG," To String Exception : "+ex);}
        return returnString;
    }
}
