package com.yin.driver.services.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddCarOptionsListModel implements Serializable {

    private final String
            TAG             = "AddCarOptionsListModel";

    private final String
            PLAN_TYPE       = "plan_type",
            CAR_TYPE        = "car_type",
            CAR_COLOR       = "colours";


    KeyValueListModel
            planTypeList    = null,
            carTypeList     = null,
            carColourList   = null;

    public AddCarOptionsListModel(){}

    public KeyValueListModel getPlanTypeList() {
        return planTypeList;
    }

    public void setPlanTypeList(KeyValueListModel planTypeList) {
        this.planTypeList = planTypeList;
    }

    public KeyValueListModel getCarTypeList() {
        return carTypeList;
    }

    public void setCarTypeList(KeyValueListModel carTypeList) {
        this.carTypeList = carTypeList;
    }

    public KeyValueListModel getCarColourList() {
        return carColourList;
    }

    public void setCarColourList(KeyValueListModel carColourList) {
        this.carColourList = carColourList;
    }

    public boolean toObject(String jsonObjectString){
        try{
            JSONObject json = new JSONObject(jsonObjectString);
            List<KeyValueModel> list = new ArrayList<KeyValueModel>();

            if(json.has(PLAN_TYPE)){
                KeyValueListModel modelLocal = new KeyValueListModel();
                if(modelLocal.toObject(json.getJSONArray(PLAN_TYPE))){
                    this.planTypeList = modelLocal;
                }
            }
            if(json.has(CAR_TYPE)){
                KeyValueListModel modelLocal = new KeyValueListModel();
                if(modelLocal.toObject(json.getJSONArray(CAR_TYPE))){
                    this.carTypeList = modelLocal;
                }
            }
            if(json.has(CAR_COLOR)){
                KeyValueListModel modelLocal = new KeyValueListModel();
                if(modelLocal.toObject(json.getJSONArray(CAR_COLOR))){
                    this.carColourList = modelLocal;
                }
            }
            return true;
        }catch(Exception ex){
            Log.d(TAG, "Json Exception : " + ex);
            return false;}

    }

    @Override
    public String toString(){
        String returnString = null;
        try{
            JSONObject json = new JSONObject();

            json.put(PLAN_TYPE, this.planTypeList != null? new JSONArray(this.planTypeList.toString(true)) : new JSONArray());
            json.put(CAR_TYPE, this.carTypeList != null? new JSONArray(this.carTypeList.toString(true)) : new JSONArray());
            json.put(CAR_COLOR, this.carColourList != null? new JSONArray(this.carColourList.toString(true)) : new JSONArray());

            returnString = json.toString();
        }
        catch (Exception ex){Log.d(TAG," To String Exception : "+ex);}
        return returnString;
    }
}
