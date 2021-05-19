package com.core.labs.callblock.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.core.labs.callblock.Blocker;
import com.core.labs.callblock.Activities.DriveModeEnabledActivity;
import com.core.labs.callblock.R;

/**
 * A foreground service to notify the user that the DriveMode is currently enabled
 */

public class DriveModeEnabledService extends Service {

    //Declare all your class variables here
    private static final int FOREGROUND_ID = 1070;
    private static final String TAG = "DriveModeEnabledService";
    MediaPlayer player;
    private Context context;

    public DriveModeEnabledService() {
        //Default Empty Constructor
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        //Set the Device to do not disturb mode
        setRingerMode(context,AudioManager.RINGER_MODE_SILENT);

        //Voice telling that Drive Mode has been enabled
        player = MediaPlayer.create(this, R.raw.enabled);
        player.setLooping(false);
        player.setVolume(1.0f,1.0f);
        player.start();

        //Start Listening for Incoming Calls
        Blocker.startListening(this);

        //Start a Notification stating that Drive Mode has been enabled!
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final String channelId = "DriveModeEnabled";
        final String channelName = "Drive Mode Enabled";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId,channelName,importance);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //On Clicking the notification take the user to DriveModeEnabled Activity
        Intent intent = new Intent(this,DriveModeEnabledActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        //Notification for all SDK Versions
        Notification notification = new NotificationCompat.Builder(this,channelId)
                .setContentTitle("Drive Mode Enabled")
                .setContentText("Touch to Disable the Drive Mode")
                .setSmallIcon(R.drawable.car)
                .setContentIntent(pendingIntent)
                .build();

        //Start the foreground Service
        startForeground(FOREGROUND_ID,notification);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Stop foreground Service
        stopForeground(true);
        //Stop Listening for incoming calls
        Blocker.stopListening(this);
        Log.d(TAG,"Service and Notifications cleared successfully");
        //Voice for disabling Drive Mode
        if (!player.isPlaying()) {
            player.stop();
            player.reset();
            player.release();
            player = MediaPlayer.create(this,R.raw.disabled);
            player.start();
        }

        //Set to ringer mode from do not disturb mode
        setRingerMode(context,AudioManager.RINGER_MODE_NORMAL);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Method to set the Ringer Mode to Normal or Silent
    public static void setRingerMode(Context context, int mode) {

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Check for DND permissions for API 24+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !nm.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);
            audioManager.setRingerMode(mode);
        }
    }
}
