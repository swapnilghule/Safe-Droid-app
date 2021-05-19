package com.core.labs.callblock.Activities;

/**
 * The Onboarding Activity that is launched only at the first launch
 * Introduces the user with the app as well as asks the user for permissions
 */

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.core.labs.callblock.UserSettings;
import com.core.labs.callblock.R;
import com.hololo.tutorial.library.PermissionStep;
import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

public class OnBoardingActivity extends TutorialActivity {

    private static final String TAG = "OnBoardingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Check if Language has been previously set by user
        if(getSharedPreferences(UserSettings.PREFNAME,MODE_PRIVATE).contains("locale")) {
            LaunchActivity.setLocale(LaunchActivity.getLocale(OnBoardingActivity.this), OnBoardingActivity.this);
        }
        super.onCreate(savedInstanceState);

        //Only ask for permissions if sdk >= 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //Choose Default Language
            addFragment(new Step.Builder().setTitle(getString(R.string.slide_1_title))
                    .setContent(getString(R.string.slide_1_content))
                    .setBackgroundColor(Color.parseColor("#57606F")) // int background color
                    .setDrawable(R.drawable.locale) // int top drawable
                    .setSummary("")
                    .build());

            if(!getSharedPreferences(UserSettings.PREFNAME,MODE_PRIVATE).contains("locale")) {
                chooseLanguage();
            }


            //Calls and Messages Blocking
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //ANSWER_PHONE_CALLS is available only in > API 26
                addFragment(new PermissionStep.Builder().setTitle(getString(R.string.slide_2_title))
                        .setContent(getString(R.string.slide_2_content))
                        .setBackgroundColor(Color.parseColor("#57606F")) // int background color
                        .setDrawable(R.drawable.block_calls) // int top drawable
                        .setSummary("")
                        .setPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.CALL_PHONE,Manifest.permission.READ_CALL_LOG,Manifest.permission.ANSWER_PHONE_CALLS})
                        .build());
            } else {
                addFragment(new PermissionStep.Builder().setTitle(getString(R.string.slide_2_title))
                        .setContent(getString(R.string.slide_2_content))
                        .setBackgroundColor(Color.parseColor("#57606F")) // int background color
                        .setDrawable(R.drawable.block_calls) // int top drawable
                        .setSummary("")
                        .setPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.CALL_PHONE,Manifest.permission.READ_CALL_LOG})
                        .build());
            }


            //Auto Enable the Drive Mode
            addFragment(new PermissionStep.Builder().setTitle(getString(R.string.slide_3_title))
                    .setContent(getString(R.string.slide_3_content))
                    .setBackgroundColor(Color.parseColor("#57606F")) // int background color
                    .setDrawable(R.drawable.autoenable2) // int top drawable
                    .setSummary("")
                    .setPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION})
                    .build());

            //Notifications After the Ride
            addFragment(new PermissionStep.Builder().setTitle(getString(R.string.slide_4_title))
                    .setContent(getString(R.string.slide_4_content))
                    .setBackgroundColor(Color.parseColor("#57606F")) // int background color
                    .setDrawable(R.drawable.notifyac) // int top drawable
                    .setSummary("")
                    .setPermissions(new String[]{Manifest.permission.READ_CONTACTS})
                    .build());

            //Auto Reply Text Messages
            addFragment(new PermissionStep.Builder().setTitle(getString(R.string.slide_5_title))
                    .setContent(getString(R.string.slide_5_content))
                    .setBackgroundColor(Color.parseColor("#57606F")) // int background color
                    .setDrawable(R.drawable.autotext) // int top drawable
                    .setSummary("")
                    .setPermissions(new String[]{Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS,Manifest.permission.SEND_SMS})
                    .build());

            //Emergency Calls
            addFragment(new Step.Builder().setTitle(getString(R.string.slide_6_title))
                    .setContent(getString(R.string.slide_6_content))
                    .setBackgroundColor(Color.parseColor("#57606F")) // int background color
                    .setDrawable(R.drawable.emergency) // int top drawable
                    .setSummary("")
                    .build());

        } else {

            //Choose Default Language
            addFragment(new Step.Builder().setTitle(getString(R.string.slide_1_title))
                    .setContent(getString(R.string.slide_1_content))
                    .setBackgroundColor(Color.parseColor("#57606F")) // int background color
                    .setDrawable(R.drawable.locale) // int top drawable
                    .setSummary("")
                    .build());

            if(!getSharedPreferences(UserSettings.PREFNAME,MODE_PRIVATE).contains("locale")) {
                chooseLanguage();
            }

            //Calls and Messages Blocking
            addFragment(new Step.Builder().setTitle(getString(R.string.slide_2_title))
                    .setContent(getString(R.string.slide_2_content))
                    .setBackgroundColor(Color.parseColor("#57606F")) // int background color
                    .setDrawable(R.drawable.block_calls) // int top drawable
                    .setSummary("")
                    .build());


            //Auto Enable the Drive Mode
            addFragment(new Step.Builder().setTitle(getString(R.string.slide_3_title))
                    .setContent(getString(R.string.slide_3_content))
                    .setBackgroundColor(Color.parseColor("#57606F")) // int background color
                    .setDrawable(R.drawable.autoenable2) // int top drawable
                    .setSummary("")
                    .build());

            //Notifications After the Ride
            addFragment(new Step.Builder().setTitle(getString(R.string.slide_4_title))
                    .setContent(getString(R.string.slide_4_content))
                    .setBackgroundColor(Color.parseColor("#57606F")) // int background color
                    .setDrawable(R.drawable.notifyac) // int top drawable
                    .setSummary("")
                    .build());

            //Auto Reply Text Messages
            addFragment(new Step.Builder().setTitle(getString(R.string.slide_5_title))
                    .setContent(getString(R.string.slide_5_content))
                    .setBackgroundColor(Color.parseColor("#57606F")) // int background color
                    .setDrawable(R.drawable.autotext) // int top drawable
                    .setSummary("")
                    .build());

            //Emergency Calls
            addFragment(new Step.Builder().setTitle(getString(R.string.slide_6_title))
                    .setContent(getString(R.string.slide_6_content))
                    .setBackgroundColor(Color.parseColor("#57606F")) // int background color
                    .setDrawable(R.drawable.emergency) // int top drawable
                    .setSummary("")
                    .build());
        }

    }

    @Override
    public void finishTutorial() {
        super.finishTutorial();

        //Set the firstTimeLaunch to false
        setOnFirstTimeLaunch();

        //Go to the LaunchActivity
        startActivity(new Intent(OnBoardingActivity.this,LaunchActivity.class));
        finish();

    }

    //Method that disables this activity to be enabled in the future
    private void setOnFirstTimeLaunch() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(UserSettings.PREFNAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("FirstTimeLaunch",false);
        editor.apply();
    }

    //Method that asks user for the app's default language
    private void chooseLanguage() {
        // Setup alert dialog for choosing a new Language
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your Language");

        // add a list of languages
        String[] languages = {"English", "Hindi","Marathi"};
        builder.setItems(languages, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: LaunchActivity.setLocale(LaunchActivity.setUserLocale(getApplicationContext(),"en"),getApplicationContext());
                            Intent i = getBaseContext().getPackageManager()
                                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(i);
                            break;
                    case 1: LaunchActivity.setLocale(LaunchActivity.setUserLocale(getApplicationContext(),"hi"),getApplicationContext());
                            Intent it = getBaseContext().getPackageManager()
                                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(it);
                            break;
                    case 2: LaunchActivity.setLocale(LaunchActivity.setUserLocale(getApplicationContext(),"mr"),getApplicationContext());
                            Intent im = getBaseContext().getPackageManager()
                                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                            im.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(im);
                            break;
                }

            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false); //Cannot be cancelled on touching outside of screen
        dialog.setCancelable(false); //Cannot be cancelled
        dialog.show();
    }

}
