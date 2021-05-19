package com.core.labs.callblock.DB_Helpers;

/**
 * Class for defining the Database Schema
 */

public class CallLogsContract {

    //Create private constructor so that no instance of this class is made
    private CallLogsContract() {}

    //Define the Schema of the Table
    public class CallLogsEntry {
        public static final String TABLE_NAME = "call_logs";
        public static final String CALL_ID = "_id";
        public static final String CONTACT_NAME = "contact_name";
        public static final String CONTACT_NUMBER = "contact_number";
        public static final String DATE = "call_date";
    }
}
