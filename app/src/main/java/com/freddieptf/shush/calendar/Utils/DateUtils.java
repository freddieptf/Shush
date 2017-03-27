package com.freddieptf.shush.calendar.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by fred on 3/26/17.
 */

public class DateUtils {

    public static String formatDate(long date){
        String simpleTime = "h:mm a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(simpleTime);
        Date d = new Date(date);
        return simpleDateFormat.format(d);
    }

    public static String getFormattedDate(long time, String format){
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        return simpleDateFormat.format(date);
    }
}
