package com.core.labs.callblock;

/**
 * Class to simplify the work of starting and stopping listening to
 * all the incoming calls and messages.
 */

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.core.labs.callblock.BroadcastRecievers.EmergencySMS;
import com.core.labs.callblock.BroadcastRecievers.IncomingCall;

public class Blocker {

    //Declare all the class variables here
    private static final String TAG = "Blocker";
    public static final String PREFNAME = "Blocker";

    //The Default Constructor
    private Blocker() {

    }

    //Start Listening to Incoming calls and messages
    public static void startListening(Context context) {

        //First Check if the Component is already enabled or not
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFNAME,Context.MODE_PRIVATE);
        boolean isEnabled = sharedPreferences.getBoolean("isEnabled",false);

        if(!isEnabled) {
            //For Calls Blocking
            ComponentName receiver = new ComponentName(context, IncomingCall.class);
            PackageManager pm = context.getPackageManager();
            pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            Toast.makeText(context,"Enabled DriveMode",Toast.LENGTH_LONG).show();
            Log.d(TAG,"Started Listening for Incoming calls");

            //For EmergencySMS Listening
            receiver = new ComponentName(context, EmergencySMS.class);
            pm = context.getPackageManager();
            pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            Log.d(TAG,"Started Listening for Emergency SMS");

            //Set the isEnabled to true
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isEnabled",true);
            editor.apply();
        }else {
            Log.d(TAG,"Error: Already Started Listening to incoming calls");
        }

    }

    //Stop Listening to Incoming calls and messages
    public static void stopListening(Context context) {
        //First Check if the Component is already enabled or not
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFNAME,Context.MODE_PRIVATE);
        boolean isEnabled = sharedPreferences.getBoolean("isEnabled",false);

        if(isEnabled) {
            //For Calls Blocking
            ComponentName receiver = new ComponentName(context, IncomingCall.class);
            PackageManager pm = context.getPackageManager();
            pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            Toast.makeText(context,"Disabled DriveMode",Toast.LENGTH_LONG).show();
            Log.d(TAG,"Stopped Listening for Incoming calls");

            //For EmergencySMS Listening
            receiver = new ComponentName(context, EmergencySMS.class);
            pm = context.getPackageManager();
            pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            Log.d(TAG,"Stopped Listening for Emergency SMS");

            //Set the isEnabled to false
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isEnabled",false);
            editor.apply();
        } else {
            Log.d(TAG,"Error: Already Stopped Listening to incoming calls");
        }

    }

}
