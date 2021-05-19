package com.core.labs.callblock.Fragments;
/**
 * Fragment class that displays the call logs that the user missed when
 * the Drive Mode was enabled and the calls were automatically rejected
 * Doesn't display other call logs (when drive mode was not enabled)
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.core.labs.callblock.DB_Helpers.CallLogsContract;
import com.core.labs.callblock.DB_Helpers.CallLogsDBHelper;
import com.core.labs.callblock.R;

public class CallLogsFragment extends Fragment {

    //Declare all your class variables here
    View view;
    Context context;
    private CallLogsDBHelper callLogsDBHelper;
    private ListView listView;

    public CallLogsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_call_logs, container, false);
        context = getContext();

        //Find all the views
        listView = view.findViewById(R.id.callLogsListView);

        callLogsDBHelper = new CallLogsDBHelper(context);

        populateListView();

        return view;
    }

    //Populate and show all the Call Logs that the user missed when the Drive Mode was enabled
    private void populateListView() {
        SQLiteDatabase sqLiteDatabase = callLogsDBHelper.getReadableDatabase();

        Cursor data = callLogsDBHelper.showCallLogs(sqLiteDatabase);

        if(data != null && data.getCount() > 0) {
            String columns[] = new String[] {
                    CallLogsContract.CallLogsEntry.CONTACT_NAME,
                    CallLogsContract.CallLogsEntry.CONTACT_NUMBER,
                    CallLogsContract.CallLogsEntry.DATE
            };

            int boundTo[] = new int[] {
                    R.id.callLogName,
                    R.id.callLogNumber,
                    R.id.callLogDate
            };

            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(context,R.layout.call_log_item,data,columns,boundTo,0);
            listView.setAdapter(simpleCursorAdapter);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        callLogsDBHelper.close();
    }
}
