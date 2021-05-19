package com.core.labs.callblock.Services;

/**
 * Class where all the background voice over music wil be played
 * 1) For notifying Emergency calls
 */
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.core.labs.callblock.BroadcastRecievers.EmergencySMS;
import com.core.labs.callblock.R;

public class VoiceOverService extends Service {

    //Declare all your class variables here
    private static final String TAG = "VoiceOverService";
    MediaPlayer player;
    Bundle bundle;
    String voiceOver;
    int count;

    public VoiceOverService() {
        //Default Empty Constructor
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        bundle = intent.getExtras();
        if(bundle != null) {
            voiceOver = bundle.getString("voiceOver");

            switch (voiceOver) {
                case EmergencySMS.EMERGENCY_KEYWORD :
                    //Ask media player to play Emergency Voice over
                    playEmergency();
                    break;
                default :
                    Log.d(TAG,"Error : Not received any of the voice overs");
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    //Plays the voice over for 5 times and then destroys the service
    private synchronized void playEmergency() {
        Log.d(TAG,"Playing Emergency Voice Over");
        player = MediaPlayer.create(this, R.raw.emergency_call);
        count = 0;
        player.start();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            int maxCount = 4;
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(count < maxCount) {
                    count++;
                    player.seekTo(0);
                    player.start();
                }else {
                    stopSelf();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Release the player
        if(player != null) {
            player.stop();
            player.reset();
            player.release();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
