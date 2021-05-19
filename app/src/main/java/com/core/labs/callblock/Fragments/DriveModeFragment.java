package com.core.labs.callblock.Fragments;

/**
 * Fragment to enable / disable the Drive Mode
 * Auto Enable drive mode
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.core.labs.callblock.Activities.DriveModeEnabledActivity;
import com.core.labs.callblock.UserSettings;
import com.core.labs.callblock.R;
import com.core.labs.callblock.Services.SpeedUpdates;

public class DriveModeFragment extends Fragment {

    //Declare all your class variables here
    View view;
    Button enableDriveMode;
    CheckBox autoEnable;
    Context context;

    public DriveModeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_drive_mode, container, false);

        //Get the context
        context = getContext();

        //Find the views
        enableDriveMode = view.findViewById(R.id.enableDriveMode);
        autoEnable = view.findViewById(R.id.autoEnable);

        //Set the state of Auto Enabled to true if already present
        autoEnable.setChecked(UserSettings.isAutoEnabled(context));

        //Set the function of enableDriveMode button on clicking it
        enableDriveMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextScreen = new Intent(context,DriveModeEnabledActivity.class);
                startActivity(nextScreen);
                getActivity().finish();
            }
        });

        //If Drive Mode is already Enabled then don't arrive at this Activity
        if(UserSettings.isEnabled(getContext())){
            Intent nextActivity = new Intent(context,DriveModeEnabledActivity.class);
            startActivity(nextActivity);
            getActivity().finish();
        }

        //Set the function of auto Enabling the drive mode
        //i.e. Keep the Speed Updates Service started
        autoEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isAutoEnabled = ((CheckBox) view).isChecked();

                //Store the value in sharedPrefs to access them later
                SharedPreferences sharedPreferences = context.getSharedPreferences(UserSettings.PREFNAME,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isAutoEnabled",isAutoEnabled);
                editor.apply();

                //Start/Stop Speed Updates Service
                if(UserSettings.isAutoEnabled(context)) {
                    context.startService(new Intent(context,SpeedUpdates.class));
                }else {
                    context.stopService(new Intent(context,SpeedUpdates.class));
                }
            }
        });

        return view;
    }

}
