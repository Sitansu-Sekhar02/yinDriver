package com.yin.driver.upload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.yin.driver.R;
import com.yin.driver.global.GlobalVariables;
import com.yin.driver.services.ServerResponseInterface;
import com.yin.driver.services.ServicesMethodsManager;
import com.yin.driver.services.model.UploadImageModel;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class UploadImageToServer {

    public static final String TAG = "UploadImageToServer";

    Context context;
    File filesToUpload;
    UploadToServerListener listener;
    UploadTask uploadTask = new UploadTask();

    private GlobalVariables.IMAGE_UPLOAD_TYPE imageType = GlobalVariables.IMAGE_UPLOAD_TYPE.PROFILE;

    public UploadImageToServer(Context context){
        this.context = context;
    }

    public void startUploading(File filetoUpload){
        this.filesToUpload = filetoUpload;
        uploadTask.execute();
    }

    public void startUploading(File filetoUpload, UploadToServerListener listener){
        this.filesToUpload = filetoUpload;
        this.listener = listener;
        uploadTask.execute();
    }

    public void startUploading(File filetoUpload, GlobalVariables.IMAGE_UPLOAD_TYPE type, UploadToServerListener listener){
        this.filesToUpload = filetoUpload;
        this.listener = listener;
        this.imageType = type;
        uploadTask.execute();
    }

    public UploadToServerListener getListener() {
        return listener;
    }

    public void setListener(UploadToServerListener listener) {
        this.listener = listener;
    }

    public GlobalVariables.IMAGE_UPLOAD_TYPE getImageType() {
        return imageType;
    }

    public void setImageType(GlobalVariables.IMAGE_UPLOAD_TYPE imageType) {
        this.imageType = imageType;
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }

    private String getBase64(File mPath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(mPath.toString(), options);
        String myBase64Image = "data:image/jpeg;base64,"+encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 70);
        return myBase64Image;
    }

    private void upload(String base64){
        if(base64!=null){
            UpdateImagesToServer(base64);
        }else{
            setResult(true, null, context.getString(R.string.image_convertion_error));
        }
    }


    private void UpdateImagesToServer(String base64){
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        UploadImageModel uploadImageModel = new UploadImageModel();
        uploadImageModel.setFile(base64);
        servicesMethodsManager.uploadImage(context, uploadImageModel,  imageType, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                Log.d(TAG, "Response : "+ arg0.toString());
                setResult(true, null, arg0);
            }

            @Override
            public void OnFailureFromServer(String msg) {
                setResult(false, msg, null);
            }

            @Override
            public void OnError(String msg) {
                setResult(false, msg, null);
            }
        }, "UploadImageToServer");
    }


    class UploadTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

                String base64 = getBase64(filesToUpload);
                return base64;
        }

        protected void onPostExecute(String base64) {
            // TODO: check this.exception
            // TODO: do something with the feed
            upload(base64);
        }
    }

    private void setResult(boolean isSuccess, String message, Object obj){
        if(listener!=null){
            if(isSuccess){listener.OnSuccess(obj);}
            else{listener.OnFailure(message);}
        }
    }


}
