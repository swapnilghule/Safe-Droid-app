package com.core.labs.callblock.BroadcastRecievers;

/**
 * BroadCast Receiver class to listen for Incoming SMS and check if there is an emergency caller
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.core.labs.callblock.Services.VoiceOverService;

public class EmergencySMS extends BroadcastReceiver {

    //Declare all the class variables here
    public static final String EMERGENCY_KEYWORD = "1";
    private static final String TAG = "EmergencySMS";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"Got a message");
       final Bundle bundle = intent.getExtras();

       try {
           if(bundle != null){
               final Object[] pdusObj = (Object[]) bundle.get("pdus");
               for(int i = 0; i < pdusObj.length; i++) {
                   SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                   String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                   String message = currentMessage.getDisplayMessageBody();
                   try {
                       //Check if the incoming SMS contains the EMERGENCY_KEYWORD
                       if(message.matches("(?i)" + EMERGENCY_KEYWORD + ".*")) {
                           //If we found an incoming message starting with keyword EMERGENCY_KEYWORD then notify
                           //the driver using voice
                           Intent emergency = new Intent(context,VoiceOverService.class);
                           emergency.putExtra("voiceOver",EMERGENCY_KEYWORD);
                           context.startService(emergency);
                           Log.d(TAG,"Received Emergency message from " + phoneNumber);
                       }else {
                           Log.d(TAG,"Could'nt get Emergency keyword");
                       }
                   }catch(Exception e) {
                       e.printStackTrace();
                   }
               }
           }

       }catch(Exception e) {
           e.printStackTrace();
        }
    }
}
