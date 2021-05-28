package com.yin.driver.services.model;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;

public class CarCostListModel implements Serializable {
    private final String TAG = "CarCostListModel";

    private final String
            CAR_COST = "package";

    CarCostModel packageName=null;

    public CarCostModel getPackageName() {
        return packageName;
    }

    public void setPackageName(CarCostModel packageName) {
        this.packageName = packageName;
    }

    public boolean toObject(String jsonObject) {
        try {
            JSONObject json = new JSONObject(jsonObject);
            if (json.has(CAR_COST)) {
                JSONObject localObject = json.getJSONObject(CAR_COST);
                CarCostModel modalLocal = new CarCostModel();
                if (modalLocal.toObject(localObject.toString())) {
                    this.packageName = modalLocal;
                }
            }

            return true;
        } catch (Exception ex) {
            Log.d(TAG, "Json Exception : " + ex);
        }
        return false;
    }

    @Override
    public String toString() {
        String returnString = null;
        try {
            JSONObject jsonMain = new JSONObject();

            jsonMain.put(CAR_COST, packageName != null ? new JSONObject(packageName.toString()) : new JSONObject());
            returnString = jsonMain.toString();
        } catch (Exception ex) {
            Log.d(TAG, " To String Exception : " + ex);
        }
        return returnString;
    }
}