package com.yin.driver.upload;

public interface UploadToServerListener {
    public void OnSuccess(Object arg0);
    public void OnFailure(String message);
}
