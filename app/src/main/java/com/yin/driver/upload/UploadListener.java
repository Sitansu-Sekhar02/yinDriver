package com.yin.driver.upload;

public interface UploadListener {
    public void OnSuccess(String fileName);
    public void OnFailure();
}
