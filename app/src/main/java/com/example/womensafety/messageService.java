package com.example.womensafety;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class messageService extends Service implements SensorEventListener {

    SensorManager sensorManager;
    SensorEventListener listener;
    Sensor accelerometer;
    String lat,lon;
    Float ax=0f,ay=0f,az=0f;
    FusedLocationProviderClient client;
    LocationCallback callback;
    LocationRequest request;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        client = LocationServices.getFusedLocationProviderClient(this);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
       // listener=this;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        //this.onSensorChanged(null);
        Log.d("test","oncreate");

        client = LocationServices.getFusedLocationProviderClient(this);



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if(location != null){
                    lat= Double.toString(location.getLatitude());
                    lon= Double.toString(location.getLongitude());
                    Log.d("test",lat);
                }

            }
        });
        request = LocationRequest.create();
        request.setInterval(3000);
        request.setFastestInterval(2000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        callback = new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                List<Location> locations = locationResult.getLocations();
                if(locations != null && locations.size() > 0){
                    Location location = locations.get(0);
                    lat= Double.toString(location.getLatitude());
                    lon= Double.toString(location.getLongitude());
                    Log.d("test","inside location result");
                    //    Toast.makeText(MainActivity.this,location.getLatitude() + " : " + location.getLongitude(),Toast.LENGTH_SHORT).show();
                }

            }
        };
        //client.requestLocationUpdates(request,callback,null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        client.removeLocationUpdates(callback);
        sensorManager.unregisterListener(this);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("test","inside sensor changed");
        ax= event.values[0];
        ay= event.values[1];
        az= event.values[2];
        if(Math.abs(ax)>5 || Math.abs(ay)>5 || Math.abs(ay)>5 ) {
            Toast.makeText(this, "yoyo", Toast.LENGTH_SHORT).show();
            Log.d("test", "sos");
            try {
                SmsManager smgr = SmsManager.getDefault();
                smgr.sendTextMessage("+919716822108", null,"http://maps.google.com/maps?saddr="+lat+","+lon+"&daddr="+lat+","+lon, null, null);
                Log.d("test","send");
            }

            catch (Exception e){
                Log.d("test","not send msg");
            }
        }
    //    axtext.setText(ax.toString());
  //      aytext.setText(ay.toString());
//        aztext.setText(az.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
