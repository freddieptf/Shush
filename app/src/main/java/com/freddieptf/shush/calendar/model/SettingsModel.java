package com.freddieptf.shush.calendar.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fred on 12/25/15.
 */
public class SettingsModel implements Parcelable{

    public SettingsModel(){
        super();
    }

    protected SettingsModel(Parcel p){
        ringerMode = p.readInt();
        brightness = p.readInt();
        wifiDisabled = p.readInt() == 1 ? true : false;
        mobileDataDisabled = p.readInt() == 1 ? true : false;
    }

    public int getRingerMode() {
        return ringerMode;
    }

    public SettingsModel setRingerMode(int ringerMode) {
        this.ringerMode = ringerMode;
        return this;
    }

    public int getBrightness() {
        return brightness;
    }

    public SettingsModel setBrightness(int brightness) {
        this.brightness = brightness;
        return this;
    }

    public boolean isWifiDisabled() {
        return wifiDisabled;
    }

    public SettingsModel setWifiDisabled(boolean wifiDisabled) {
        this.wifiDisabled = wifiDisabled;
        return this;
    }

    public boolean isMobileDataDisabled() {
        return mobileDataDisabled;
    }

    public SettingsModel setMobileDataDisabled(boolean mobileDataDisabled) {
        this.mobileDataDisabled = mobileDataDisabled;
        return this;
    }

    public int ringerMode = -1;
    public int brightness;
    public boolean wifiDisabled;
    public boolean mobileDataDisabled;


    public static Creator<SettingsModel> CREATOR = new Creator<SettingsModel>() {
        @Override
        public SettingsModel createFromParcel(Parcel source) {
            return new SettingsModel(source);
        }

        @Override
        public SettingsModel[] newArray(int size) {
            return new SettingsModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ringerMode);
        dest.writeInt(brightness);
        dest.writeInt(wifiDisabled ? 1 : 0);
        dest.writeInt(mobileDataDisabled ? 1 : 0);
    }
}
