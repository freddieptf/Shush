package com.freddieptf.shush.calendar.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by freddieptf on 19/10/16.
 */

public class ShushProfile implements Parcelable {

    private final int ringerMode;
    private final int brightness;
    private final boolean wifiDisabled;
    private final boolean mobileDataDisbaled;

    private ShushProfile(Builder builder){
        this.ringerMode = builder.ringerMode;
        this.brightness = builder.brightness;
        this.wifiDisabled = builder.wifiDisabled;
        this.mobileDataDisbaled = builder.mobileDataDisbaled;
    }

    public static class Builder{
        private int ringerMode = -1;
        private int brightness = -1;
        private boolean wifiDisabled = false;
        private boolean mobileDataDisbaled = false;

        public Builder(){}

        public Builder setRingerMode(int ringerMode) {
            this.ringerMode = ringerMode;
            return this;
        }

        public Builder setBrightness(int brightness) {
            this.brightness = brightness;
            return this;
        }

        public Builder setWifiDisabled(boolean wifiDisabled) {
            this.wifiDisabled = wifiDisabled;
            return this;
        }

        public Builder setMobileDataDisbaled(boolean mobileDataDisbaled) {
            this.mobileDataDisbaled = mobileDataDisbaled;
            return this;
        }

        public ShushProfile build(){
            return new ShushProfile(this);
        }
    }

    private ShushProfile(Parcel in) {
        ringerMode = in.readInt();
        brightness = in.readInt();
        wifiDisabled = in.readByte() != 0;
        mobileDataDisbaled = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ringerMode);
        dest.writeInt(brightness);
        dest.writeByte((byte) (wifiDisabled ? 1 : 0));
        dest.writeByte((byte) (mobileDataDisbaled ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShushProfile> CREATOR = new Creator<ShushProfile>() {
        @Override
        public ShushProfile createFromParcel(Parcel in) {
            return new ShushProfile(in);
        }

        @Override
        public ShushProfile[] newArray(int size) {
            return new ShushProfile[size];
        }
    };

    @Override
    public String toString() {
        return String.format("ringer: %d \n brightness: %d \n wifiDisabled: %s", ringerMode, brightness, wifiDisabled);
    }

    public int getRingerMode() {
        return ringerMode;
    }

    public int getBrightness() {
        return brightness;
    }

    public boolean isWifiDisabled() {
        return wifiDisabled;
    }

    public boolean isMobileDataDisbaled() {
        return mobileDataDisbaled;
    }

}
