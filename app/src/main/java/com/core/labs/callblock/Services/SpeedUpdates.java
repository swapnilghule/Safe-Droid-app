package com.core.labs.callblock.Services;

/**
 * Service Class to Monitor the speed and if speed > 20 KM/HR then auto enable the Drive Mode
 * A Foreground Service
 */

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.core.labs.callblock.Activities.DriveModeEnabledActivity;
import com.core.labs.callblock.Activities.LaunchActivity;
import com.core.labs.callblock.R;

public class SpeedUpdates extends Service {

    //Declare all the class variables here
    private static final int FOREGROUND_ID = 1337;
    private static final double EARTH_RADIUS = 6371000f;
    private static final String TAG = "SpeedUpdates";
    private static final int speedThreshold = 20;
    double curTime = 0;
    double oldLat = 0.0;
    double oldLon = 0.0;
    LocationManager locationManager;
    LocationListener locationListener;

    public SpeedUpdates() {
    }

    public void onCreate() {
        super.onCreate();

        //Create a Notification
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final String channelId = "SpeedUpdates";
        CharSequence channelName = "Speed Updates";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        //Only if SDK > API 26 then create a NOTIFICATION CHANNEL
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //On clicking the "Auto Enable Drive Mode" notification take to the DriveModeFragment
        Intent resultIntent = new Intent(this, LaunchActivity.class);
        PendingIntent mResultIntent = PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        //Notification for all SDK versions
        Notification notification = new NotificationCompat.Builder(this,channelId)
                .setContentTitle("Auto-Enable Drive Mode")
                .setContentText("Drive Mode Will Be Enabled Automatically")
                .setSmallIcon(R.drawable.car)
                .setContentIntent(mResultIntent)
                .build();

        //Start the Foreground Notification
        startForeground(FOREGROUND_ID,notification);

        //Set up Location Updates

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(getSpeed(location) > speedThreshold) {
                    Intent intent = new Intent(SpeedUpdates.this,DriveModeEnabledActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Log.d(TAG,"Auto-Enabled the Drive Mode as speed greater than 20KM/HR");
                    Toast.makeText(SpeedUpdates.this,"Auto Enabled Drive Mode",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        //Check if permission granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //Start Listening to Location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    //Method to getSpeed in KM/HR
    private int getSpeed(Location location){
        double newTime= System.currentTimeMillis();
        double newLat = location.getLatitude();
        double newLon = location.getLongitude();
        double distance = calculationByDistance(newLat,newLon,oldLat,oldLon);
        Log.d(TAG,"Distance is " + distance + " mtrs");
        double timeDifferent = newTime - curTime;
        Log.d(TAG,"Time difference is " + timeDifferent + " ms");
        double speed = distance/timeDifferent;
        curTime = newTime;
        oldLat = newLat;
        oldLon = newLon;
        int speedInKm = (int) (speed * 1000 * 3.6f);
        Log.d(TAG,"Speed is : "  + speedInKm + " Km/hr");
        return speedInKm;
    }

    //Method to calculate Distance from LAT And LONG (Check the Haversine Formula)
    public double calculationByDistance(double lat1, double lon1, double lat2, double lon2){
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return EARTH_RADIUS * c;
    }

    @Override
    public void onDestroy() {
        //Destroy the Foreground Notification
        stopForeground(true);
        //Stop the location updates
        locationManager.removeUpdates(locationListener);
        Log.d(TAG,"Service and Notifications cleared successfully");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
