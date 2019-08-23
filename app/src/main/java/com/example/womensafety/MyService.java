package com.example.womensafety;

import android.app.Service;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {


    Timer t;
    @Override
    public void onCreate() {
        super.onCreate();
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

                                  @Override
                                  public void run() {
                                      //Called each time when 1000 milliseconds (1 second) (the period parameter)
                                     CapturePhoto();
                                  }

                              }, 0, 2000);
       // CapturePhoto();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        t.cancel();
    }

    private void CapturePhoto() {

        Log.d("kkkk","Preparing to take photo");
        Camera camera = null;

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

        int frontCamera = 1;
        //int backCamera=0;

        Camera.getCameraInfo(frontCamera, cameraInfo);

        try {
            camera = Camera.open(frontCamera);
        } catch (RuntimeException e) {
            Log.d("kkkk","Camera not available: " + 1);
            camera = null;
            //e.printStackTrace();
        }
        try {
            if (null == camera) {
                Log.d("kkkk","Could not get camera instance");
            } else {
                Log.d("kkkk","Got the camera, creating the dummy surface texture");
                try {
                    camera.setPreviewTexture(new SurfaceTexture(0));
                    camera.startPreview();
                } catch (Exception e) {
                    Log.d("kkkk","Could not set the surface preview texture");
                    e.printStackTrace();
                }
                camera.takePicture(null, null, new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        File pictureFileDir=new File(Environment.getExternalStorageDirectory().getAbsolutePath());

//                        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
//                            pictureFileDir.mkdirs();
//                        }
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
                        String date = dateFormat.format(new Date());
                        String photoFile = "ServiceClickedPic_" + "_" + date + ".jpg";
                        String filename = pictureFileDir.getPath() + File.separator + photoFile;
                        File mainPicture = new File(filename);

                        try {
                            FileOutputStream fos = new FileOutputStream(mainPicture);
                            fos.write(data);
                            fos.close();
                            Log.d("kkkk","image saved");
                        } catch (Exception error) {
                            Log.d("kkkk","Image could not be saved");
                        }
                        camera.release();
                    }
                });
            }
        } catch (Exception e) {
            camera.release();
        }
    }
}