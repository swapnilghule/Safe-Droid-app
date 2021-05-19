package com.core.labs.callblock.BroadcastRecievers;

/**
 * BroadCast Receiver Class to Receive notifications about incoming calls and then Block Them.
 */

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.telecom.TelecomManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.core.labs.callblock.DB_Helpers.CallLogsDBHelper;
import com.core.labs.callblock.UserSettings;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class IncomingCall extends BroadcastReceiver {

    //Declare all the class variables here
    private static final String TAG = "IncomingCall";
    private TelecomManager telecomManager;

    //State What to do onReceiving the notification about an Incoming Call
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            Log.d(TAG, "State : " + state);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {  //Check if there is an incoming call
                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);//get the incoming number
                if (number == null)
                    return; // For Android P and above sometimes it sends 2 or more broadcasts and one of the broadcast sends a null Incoming Number
                Log.d(TAG, "incoming number : " + number);
                declinePhone(context); //Actual Implementation of Blocking Calls
                Log.d(TAG, "Call Cut Ringing");
                //Get the contact details from Contacts if present else return "Unknown number"
                String name = getContactDetails(number, context);
                Log.d(TAG, name);
                //Save the log to SQLite
                saveCallLogs(name, number, context);
                //Send a msg to the caller if AutoReply is Enabled
                if (UserSettings.isAutoReplyEnabled(context)) {
                    sendSMS(number, context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Actual Method to end the incoming calls
    private void declinePhone(Context context) {
        try {
            //Check if Android API < 28
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                //This code runs for all android versions below API 28 up to API 21 except for few versions of API 26 and 27
                String serviceManagerName = "android.os.ServiceManager";
                String serviceManagerNativeName = "android.os.ServiceManagerNative";
                String telephonyName = "com.android.internal.telephony.ITelephony";
                Class<?> telephonyClass;
                Class<?> telephonyStubClass;
                Class<?> serviceManagerClass;
                Class<?> serviceManagerNativeClass;
                Method telephonyEndCall;
                Object telephonyObject;
                Object serviceManagerObject;
                telephonyClass = Class.forName(telephonyName);
                telephonyStubClass = telephonyClass.getClasses()[0];
                serviceManagerClass = Class.forName(serviceManagerName);
                serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
                Method getService = // getDefaults[29];
                        serviceManagerClass.getMethod("getService", String.class);
                Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
                Binder tmpBinder = new Binder();
                tmpBinder.attachInterface(null, "fake");
                serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
                IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
                Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
                telephonyObject = serviceMethod.invoke(null, retbinder);
                telephonyEndCall = telephonyClass.getMethod("endCall");
                telephonyEndCall.invoke(telephonyObject);
            } else {
                //This code runs for android versions above API 28
                telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                //Check for Permission
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (telecomManager.isInCall()) {
                    telecomManager.endCall();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Cant disconnect call....");
        }
    }

    //Method to return the Caller Id.
    private String getContactDetails(String number, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "";

        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactsLookup = contentResolver.query(uri, null, null, null, null);

        try {
            // if number found in the contact lists return the name
            if (contactsLookup != null && contactsLookup.getCount() > 0) {
                contactsLookup.moveToNext();
                name = contactsLookup.getString(contactsLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            } else {
                //If number not found in the contacts list then return "Unknown Number"
                name = "Unknown Number";
            }
        } finally {
            if (contactsLookup != null) {
                contactsLookup.close();
            }
        }
        return name;
    }

    //Method to save the call log along with the time and date of the call to the SQLite Database
    private void saveCallLogs(String name, String number, Context context) {
        CallLogsDBHelper callLogsDBHelper = new CallLogsDBHelper(context);
        SQLiteDatabase sqLiteDatabase = callLogsDBHelper.getWritableDatabase();

        //Get current time when call came in
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        callLogsDBHelper.addCallLog(name, number, date, sqLiteDatabase);
        callLogsDBHelper.close();
    }

    //Method that sends an SMS to the caller
    protected static void sendSMS(String phoneNo, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(UserSettings.PREFNAME, Context.MODE_PRIVATE);
        String msg = sharedPreferences.getString("autoReplyMsg", UserSettings.DEFAULT_AUTO_REPLY)
                + " Reply with '"+ EmergencySMS.EMERGENCY_KEYWORD + "' for call back"
                + System.getProperty("line.separator")
                + "Sent using SafeDriveDroid";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Log.d(TAG, "Message sent to the caller");
        } catch (Exception ex) {
            Log.d(TAG, "Error : Message not sent to the caller");
            ex.printStackTrace();
        }
    }

}
