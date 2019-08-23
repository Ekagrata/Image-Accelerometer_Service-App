package com.example.womensafety;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    static MediaRecorder mRecorder;
    static String mfilename;
    static String CHANNEL_ID="abc";
   // static StorageReference mStorageReference;
    SensorManager sensorManager;
    Sensor accelerometer;
    Button start, stop;
    TextView axtext, aytext, aztext;
    public static SensorEventListener listener;
    String lat,lon;
    Float ax=0f,ay=0f,az=0f;
    Intent intent,intent1,intent2;

    FusedLocationProviderClient client;
    LocationCallback callback;
    LocationRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        axtext=  findViewById(R.id.ax);
        aytext= findViewById(R.id.ay);
        aztext= findViewById(R.id.az);
        start= findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        listener= this;


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        client = LocationServices.getFusedLocationProviderClient(this);
        intent= new Intent(MainActivity.this,MyService.class);
        intent1= new Intent(MainActivity.this,messageService.class);
        intent2= new Intent(MainActivity.this,Recorder.class);

        createNotificationChannel();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if(location != null){
                    lat= Double.toString(location.getLatitude());
                    lon= Double.toString(location.getLongitude());
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
                //    Toast.makeText(MainActivity.this,location.getLatitude() + " : " + location.getLongitude(),Toast.LENGTH_SHORT).show();
                }

            }
        };

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(intent);
                startService(intent1);
                startService(intent2);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(intent);
              stopService(intent1);
              stopService(intent2);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
       // client.removeLocationUpdates(callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.requestLocationUpdates(request,callback,null);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        ax= event.values[0];
        ay= event.values[1];
        az= event.values[2];
        if(Math.abs(ax)>5 || Math.abs(ay)>5 || Math.abs(ay)>5 ) {
           // Toast.makeText(this, "yoyo", Toast.LENGTH_SHORT).show();
         //  Log.d("test", "sos");
            try {
              //  SmsManager smgr = SmsManager.getDefault();
               // smgr.sendTextMessage("+919971283053",null ,"http://maps.google.com/maps?saddr="+lat+","+lon+"&daddr="+lat+","+lon, null, null);
            }
            catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        axtext.setText(ax.toString());
        aytext.setText(ay.toString());
        aztext.setText(az.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel servicechannel= new NotificationChannel(CHANNEL_ID,"abc", NotificationManager.IMPORTANCE_DEFAULT);
            Log.d("recorder1","noti channel");

            NotificationManager manager =getSystemService(NotificationManager.class);
            manager.createNotificationChannel(servicechannel);
        }
    }
}
