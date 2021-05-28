package com.yin.driver.services.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CategoryListModel implements Serializable {

    private final String TAG = "CategoryListModel";

    private final String RESPONSE = "response";

    List<CategoryModel> categoryListModels = new ArrayList<CategoryModel>();

    public CategoryListModel(){}


    public List<CategoryModel> getCategoryListModels() {
        return categoryListModels;
    }

    public void setCategoryListModels(List<CategoryModel> categoryListModels) {
        this.categoryListModels = categoryListModels;
    }

    public boolean toObject(String jsonObjectString){
        try{
            JSONObject json = new JSONObject(jsonObjectString);
            JSONArray array = json.getJSONArray(RESPONSE);
            List<CategoryModel> list = new ArrayList<CategoryModel>();
            for (int i=0;i<array.length();i++){
                JSONObject jsonObject = array.getJSONObject(i);
                CategoryModel keyValueModel = new CategoryModel();
                keyValueModel.toObject(jsonObject.toString());
                list.add(keyValueModel);
            }
            this.categoryListModels = list;
            return true;
        }catch(Exception ex){
            Log.d(TAG, "Json Exception : " + ex);}
        return false;
    }

    public boolean toObject(JSONArray jsonArray){
        try{
            List<CategoryModel> list = new ArrayList<CategoryModel>();
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CategoryModel keyValueModel = new CategoryModel();
                keyValueModel.toObject(jsonObject.toString());
                list.add(keyValueModel);
            }
            this.categoryListModels = list;
            return true;
        }catch(Exception ex){
            Log.d(TAG, "Json Exception : " + ex);}
        return false;
    }

    @Override
    public String toString(){
        String returnString = null;
        try{
            JSONObject jsonMain = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            List<CategoryModel> list = this.categoryListModels;
            for(int i=0;i<list.size();i++){
                jsonArray.put(new JSONObject(list.get(i).toString()));
            }
            jsonMain.put(RESPONSE, jsonArray);
            returnString = jsonMain.toString();
        }
        catch (Exception ex){Log.d(TAG," To String Exception : "+ex);}
        return returnString;
    }

    public String toString(boolean isArray){
        String returnString = null;
        try{
            JSONObject jsonMain = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            List<CategoryModel> list = this.categoryListModels;
            for(int i=0;i<list.size();i++){
                jsonArray.put(new JSONObject(list.get(i).toString()));
            }
            if(!isArray){
                jsonMain.put(RESPONSE, jsonArray);
                returnString = jsonMain.toString();
            }else{
                returnString = jsonArray.toString();
            }

        }
        catch (Exception ex){Log.d(TAG," To String Exception : "+ex);}
        return returnString;
    }
}
