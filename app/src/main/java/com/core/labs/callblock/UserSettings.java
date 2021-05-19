package com.core.labs.callblock;

/**
 * Class that contains various static methods used by the app to modify user settings
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class UserSettings extends AppCompatActivity {

    //Declare all the class variables here
    public static final String PREFNAME = "Settings";
    public static final String DEFAULT_AUTO_REPLY = "I'm currently driving. Please try calling again later.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Method to check if the Drive Mode is enabled
    public static boolean isEnabled(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Blocker.PREFNAME,MODE_PRIVATE);
        return sharedPreferences.getBoolean("isEnabled",false);
    }

    //Method to check if the Auto Enable Drive mode is enabled
    public static boolean isAutoEnabled(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFNAME,MODE_PRIVATE);
        return sharedPreferences.getBoolean("isAutoEnabled",false);
    }

    //Method to store the "Auto Reply Text Message"
    public static void saveAutoReplyMsg(EditText autoReplyMsg, Context context) {
        String msg = autoReplyMsg.getText().toString();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFNAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("autoReplyMsg",msg);
        editor.apply();
        Toast.makeText(context,"Auto Reply Text Message Changed!",Toast.LENGTH_LONG).show();
    }

    //Method that returns the "Auto Reply Text Message"
    public static String getAutoReplyMsg(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFNAME,MODE_PRIVATE);
        return sharedPreferences.getString("autoReplyMsg",DEFAULT_AUTO_REPLY);
    }

    //Method to check if the "Default Auto Reply Text Message" has been enabled
    public static boolean isDefaultAutoReplyEnabled(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFNAME,MODE_PRIVATE);
        return sharedPreferences.getBoolean("isDefaultAutoReplyText",true);
    }

    //Method to check whether "Auto Reply Text Message" has been enabled
    public static boolean isAutoReplyEnabled(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFNAME,MODE_PRIVATE);
        return sharedPreferences.getBoolean("isAutoReplyEnabled",true);
    }
}
