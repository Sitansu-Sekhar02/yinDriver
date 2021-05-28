package com.yin.driver.upload.model;

import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;

public class ImageUploadingModel implements Serializable {
    private final String TAG = "ImageUploadingModel";
    private final String
            UPLOADED_NAME   = "uploadedName",
            CATEGORY        = "category",
            MEDIA_URL       = "media_url";

    File
            imageFile       = null;

    String
            categoryID      = null,
            uploadedName    = null;

    public ImageUploadingModel(){}

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public String getUploadedName() {
        return uploadedName;
    }

    public void setUploadedName(String uploadedName) {
        this.uploadedName = uploadedName;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public boolean toObject(String jsonObject){
        try{
            JSONObject json = new JSONObject(jsonObject);
            if(json.has(CATEGORY))this.categoryID = json.getString(CATEGORY);
            if(json.has(MEDIA_URL))this.imageFile = new File(json.getString(MEDIA_URL));
            if(json.has(UPLOADED_NAME))this.uploadedName = json.getString(UPLOADED_NAME);
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
            jsonMain.put(MEDIA_URL, imageFile);
            jsonMain.put(CATEGORY, categoryID);
            jsonMain.put(UPLOADED_NAME, uploadedName);
            returnString = jsonMain.toString();
        }
        catch (Exception ex){
            Log.d(TAG," To String Exception : "+ex);}
        return returnString;
    }
}
