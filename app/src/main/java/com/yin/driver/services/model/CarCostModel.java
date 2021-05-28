package com.yin.driver.services.model;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;

public class CarCostModel implements Serializable {
    private final String TAG = "CarCostModel";
    private final String
            ID = "id",
            NAME = "name",
            PRICE = "price",
            DESCRIPTION = "description";

    String id = null, name = null, price = null,description=null;

    public CarCostModel() {
    }

    public CarCostModel(String id, String name, @Nullable String price,String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean toObject(String jsonObject) {
        try {
            JSONObject json = new JSONObject(jsonObject);
            this.id = json.getString(ID);
            this.name = json.getString(NAME);
            if (json.has(PRICE)) price = json.getString(PRICE);
            if (json.has(DESCRIPTION)) description = json.getString(DESCRIPTION);
            return true;
        } catch (Exception ex) {
            Log.d(TAG, "Json Exception : " + ex);
            return false;
        }
    }

    @Override
    public String toString() {
        String returnString = null;
        try {
            JSONObject jsonMain = new JSONObject();
            jsonMain.put(ID, this.getId());
            jsonMain.put(NAME, this.getName());
            jsonMain.put(PRICE, this.getPrice());
            jsonMain.put(DESCRIPTION, this.getDescription());
            returnString = jsonMain.toString();
        } catch (Exception ex) {
            Log.d(TAG, " To String Exception : " + ex);
        }
        return returnString;
    }
}