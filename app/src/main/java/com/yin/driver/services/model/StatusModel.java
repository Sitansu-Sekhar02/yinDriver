package com.yin.driver.services.model;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;

public class StatusModel implements Serializable{
    private final String TAG = "StatusModel";

    private final String
            MESSAGE = "message",
            TITLE = "title",
            ID = "id",
            STATUS = "status",
            STATUS_CODE = "code",
            EXTRA = "extra";

    private String message = null, code = null, extra=null, id=null, title = null;
    private boolean status = true;

    public StatusModel(){
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

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

    public boolean toObject(String jsonObject){
        try{
            JSONObject json = new JSONObject(jsonObject);
            status = json.getBoolean(STATUS);
            if(json.has(ID))id = json.getString(ID);
            if(json.has(TITLE))title = json.getString(TITLE);
            if(json.has(MESSAGE))message = json.getString(MESSAGE);
            if(json.has(EXTRA))extra = json.getString(EXTRA);
            if(json.has(STATUS_CODE))code = json.getString(STATUS_CODE);
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
            jsonMain.put(ID, id);
            jsonMain.put(TITLE, title);
            jsonMain.put(MESSAGE, message);
            jsonMain.put(STATUS, status);
            jsonMain.put(EXTRA, extra);
            jsonMain.put(STATUS_CODE, code);
            returnString = jsonMain.toString();
        }
        catch (Exception ex){Log.d(TAG," To String Exception : "+ex);}
        return returnString;
    }
}
