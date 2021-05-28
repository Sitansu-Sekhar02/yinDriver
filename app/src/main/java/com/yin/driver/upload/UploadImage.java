package com.yin.driver.upload;

import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.yin.driver.R;
import com.yin.driver.global.GlobalFunctions;

import java.io.File;


public class UploadImage implements ProgressListener {
    Context context;
    AmazonS3Client s3Client;
    File fileToUpload;
    String uploadFileName;
    UploadListener listener;
    UploadTask uploadTask;

    public UploadImage(Context context){
        this.context = context;
        uploadTask = new UploadTask();
        if(s3Client == null){initializeS3();}
    }

    private void initializeS3(){
        s3Client= new AmazonS3Client( new BasicAWSCredentials( context.getString(R.string.AWSAccessKeyId), context.getString(R.string.AWSSecretKey) ) );
        //s3Client.createBucket( context.getString(R.string.AWSBucket) );
    }

    public void startUploading(File filetoUpload){
        this.fileToUpload = filetoUpload;
        uploadTask.execute();
    }

    public void startUploading(File filetoUpload,UploadListener listener){
        this.fileToUpload = filetoUpload;
        this.listener = listener;
        uploadTask.execute();
    }

    public void startUploading(String filetoUpload){
        this.fileToUpload = new File(filetoUpload);
        uploadTask.execute();
    }

    public void startUploading(String filetoUpload,UploadListener listener){
        this.fileToUpload = new File(filetoUpload);
        this.listener = listener;
        uploadTask.execute();
    }

    private void upload(){
        uploadFileName = context.getString(R.string.user)+"_"+ GlobalFunctions.getCurrentTimestamp(context)+"_"+fileToUpload.getName();
        PutObjectRequest por = new PutObjectRequest( context.getString(R.string.AWSBucket), uploadFileName, fileToUpload );
        por.setGeneralProgressListener(this);
        por.withCannedAcl(CannedAccessControlList.PublicRead);
        s3Client.putObject( por );
    }

    private void setListener(boolean isSuccess, String filePath){
        if(listener!=null){
            if(isSuccess){listener.OnSuccess(filePath);}
            else{listener.OnFailure();}
        }
    }

    /*public void cancelUpload(){

    }*/

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        if(progressEvent.getEventCode() == ProgressEvent.FAILED_EVENT_CODE || progressEvent.getEventCode() == ProgressEvent.CANCELED_EVENT_CODE){
            setListener(false, null);
        }
        else if(progressEvent.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE){
            String finalURL = s3Client.getResourceUrl(context.getString(R.string.AWSBucket), uploadFileName);
            setListener(true, finalURL);
        }
    }

    class UploadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            upload();
            return null;
        }

        protected void onPostExecute(Void params) {
            // TODO: check this.exception
            // TODO: do something with the feed

        }
    }
}
