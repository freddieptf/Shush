package com.freddieptf.shush.calendar.Utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.Window;

import com.freddieptf.shush.R;

/**
 * Created by fred on 12/13/15.
 */
public class ColorUtils {

    Context context;
    public ColorUtils(Context context){
        this.context = context;
    }

    public int getPrimaryColor(){
        return ContextCompat.getColor(context, R.color.colorPrimary);
    }

    public int getAccentColorLight(){
        return ContextCompat.getColor(context, R.color.colorAccentLig);
    }

    public int getAccentColor(){
        return ContextCompat.getColor(context, R.color.colorAccent);
    }

    public int darken(int color, int defaultDarkColor){
        if(color == getPrimaryColor()) return defaultDarkColor;
        float hsv[] =  new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2]*10/13;
        return Color.HSVToColor(hsv);
    }

    @TargetApi(21)
    public static void setStatusBarColor(Window window, int color){
        window.setStatusBarColor(color);
    }
}
