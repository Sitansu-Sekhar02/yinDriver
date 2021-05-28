package com.yin.driver.services.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApartmentListModel implements Serializable {
    private final String
            TAG                = "ApartmentListModel";

    private final String
            CITY       = "city",
            APARTMENT          = "appartments";


    KeyValueListModel
            apartmentTypeList     = null;

    List<ApartmentModel> apartmentList = new ArrayList<ApartmentModel>();

    public ApartmentListModel(){}

    public KeyValueListModel getApartmentTypeList() {
        return apartmentTypeList;
    }

    public void setApartmentTypeList(KeyValueListModel apartmentTypeList) {
        this.apartmentTypeList = apartmentTypeList;
    }

    public List<ApartmentModel> getApartmentList() {
        return apartmentList;
    }

    public void setApartmentList(List<ApartmentModel> apartmentList) {
        this.apartmentList = apartmentList;
    }

    public boolean toObject(String jsonObjectString){
        try{
            JSONObject json = new JSONObject(jsonObjectString);
            JSONArray array = json.getJSONArray(CITY);
            List<ApartmentModel> list = new ArrayList<ApartmentModel>();

            if(json.has(CITY)){
                KeyValueListModel modelLocal = new KeyValueListModel();
                if(modelLocal.toObject(json.getJSONArray(CITY))){
                    this.apartmentTypeList = modelLocal;
                }
            }
            if (json.has(CITY)) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObj = array.getJSONObject(i);
                    JSONArray ja = jsonObj.getJSONArray(APARTMENT);
                    int len = ja.length();
                    for (int j = 0; j < len; j++) {
                        JSONObject jsonObjLocal = ja.getJSONObject(j);
                        ApartmentModel keyValueModel = new ApartmentModel();
                        if (keyValueModel.toObject(jsonObjLocal.toString())) {
                            list.add(keyValueModel);
                        }
                    }
                }
                this.apartmentList = list;
            }
            return true;
        }catch(Exception ex){
            Log.d(TAG, "Json Exception : " + ex);
            return false;}

    }

    public boolean toObject(JSONArray jsonArray){
        try{
            List<ApartmentModel> list = new ArrayList<ApartmentModel>();
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ApartmentModel keyValueModel = new ApartmentModel();
                keyValueModel.toObject(jsonObject.toString());
                list.add(keyValueModel);
            }
            this.apartmentList = list;
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
            JSONArray jsonArray = new JSONArray();
            List<ApartmentModel> list = this.apartmentList;

            json.put(CITY, this.apartmentTypeList != null? new JSONArray(this.apartmentTypeList.toString(true)) : new JSONArray());

            for(int i=0;i<list.size();i++){
                jsonArray.put(new JSONObject(list.get(i).toString()));
            }
            json.put(APARTMENT, jsonArray);
            returnString = json.toString();
        }
        catch (Exception ex){Log.d(TAG," To String Exception : "+ex);}
        return returnString;
    }

    public String toString(boolean isArray){
        String returnString = null;
        try{
            JSONObject jsonMain = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            List<ApartmentModel> list = this.apartmentList;
            for(int i=0;i<list.size();i++){
                jsonArray.put(new JSONObject(list.get(i).toString()));
            }
            if(!isArray){
                jsonMain.put(CITY, jsonArray);
                returnString = jsonMain.toString();
            }else{
                returnString = jsonArray.toString();
            }

        }
        catch (Exception ex){Log.d(TAG," To String Exception : "+ex);}
        return returnString;
    }
}
