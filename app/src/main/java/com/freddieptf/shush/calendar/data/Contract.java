package com.freddieptf.shush.calendar.data;

import android.provider.BaseColumns;

/**
 * Created by freddieptf on 21/10/16.
 */

public class Contract {

    public static class CalendarEventsTable implements BaseColumns{

        public static final String TABLE_NAME = "Calendar_Events";

        public static final String COLUMN_EVENT_NAME = "event_name";
        public static final String COLUMN_BEGIN = "begins";
        public static final String COLUMN_END = "ends";
        public static final String COLUMN_IS_SET = "is_set";
    }

    public static class ShushProfileTable implements BaseColumns{

        public static final String TABLE_NAME = "Shush_Profiles";

        //foreign key
        public static final String COLUMN_EVENT_KEY = "event_fk";

        public static final String COLUMN_PROFILE_NAME = "profile_name";
        public static final String COLUMN_SILENT_MODE = "sound_mode";
        public static final String COLUMN_BRIGHTNESS = "brightness";
        public static final String COLUMN_WIFI_DISABLED = "wifi_disabled";
        public static final String COLUMN_DATA_DISABLED = "data_disabled";
    }
}
