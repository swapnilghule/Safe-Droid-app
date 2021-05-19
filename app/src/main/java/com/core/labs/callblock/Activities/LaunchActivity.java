package com.core.labs.callblock.Activities;

/**
 * The Launcher Activity
 * Holds the PageAdapter and different fragments
 */

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.core.labs.callblock.UserSettings;
import com.core.labs.callblock.Adapters.PageAdapter;
import com.core.labs.callblock.R;

import java.util.Locale;

public class LaunchActivity extends AppCompatActivity {

    //Define your class variables here
    private static final String TAG = "LaunchActivity";
    TabLayout tabLayout;
    ViewPager viewPager;
    PageAdapter pageAdapter;
    Bundle bundle;
    boolean showCallLogs = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Check if its first time launch
        checkFirstTimeLaunch();

        //Check the locale user and set the locale
        setLocale(getLocale(this),this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        //Get the toolbar to create options menu
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.appBarLaunchToolbar);
        toolbar.inflateMenu(R.menu.menu);

        //Set the onClickListener of menu
        toolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.english :
                        setLocale(setUserLocale(getApplicationContext(),"en"),getApplicationContext());
                        recreate();
                        return true;
                    case R.id.hindi :
                        setLocale(setUserLocale(getApplicationContext(),"hi"),getApplicationContext());
                        recreate();
                        return true;
                    case R.id.marathi :
                        setLocale(setUserLocale(getApplicationContext(),"mr"),getApplicationContext());
                        recreate();
                    default:
                            return false;
                }
            }
        });

        //Check if we clicked on notification that says "Check if someone tried to call you"
        bundle = getIntent().getExtras();
        if(bundle != null) {
            showCallLogs = bundle.getBoolean("showCallLogs");
        }

        //Get all the views
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        //Set the page adapter
        pageAdapter = new PageAdapter(getSupportFragmentManager(),3,this);
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        //if we are coming from the notification then go to the Calls Logs Fragment
        if(showCallLogs) {
            viewPager.setCurrentItem(2,true);
        }

    }

    //Method to change the Locale
    public static void setLocale(String lang,Context context) {
        Locale myLocale = new Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    //Method that returns the current selected locale. Returns "en" by default
    public static String getLocale(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(UserSettings.PREFNAME,MODE_PRIVATE);
        return sharedPreferences.getString("locale","en");
    }

    //Method used to store and set the user selected Locale and return the selected locale
    protected static String setUserLocale(Context context,String locale) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(UserSettings.PREFNAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("locale",locale);
        editor.apply();
        return locale;
    }

    //Method to check if the app is launched for the first time0
    private void checkFirstTimeLaunch() {
        SharedPreferences sharedPreferences = getSharedPreferences(UserSettings.PREFNAME,MODE_PRIVATE);
        if(sharedPreferences.getBoolean("FirstTimeLaunch",true)){
            startActivity(new Intent(LaunchActivity.this,OnBoardingActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check for DND permissions for API 24+
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !nm.isNotificationPolicyAccessGranted()) {
            Toast.makeText(this,"Safe Drive Droid requires 'Do Not Disturb Access'",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            //Warn user if Android API version 26 and 27 i.e Android Oreo for broken call blocking app
            if(Build.VERSION.SDK_INT == 26 || Build.VERSION.SDK_INT == 27) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("WARNING!");
                builder.setMessage("You seem to be using Android Oreo. Automatic call ending may or may not work!");
                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
                Log.d(TAG,"Found Oreo");
            }
        }
    }
}
