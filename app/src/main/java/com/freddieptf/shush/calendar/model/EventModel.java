package com.freddieptf.shush.calendar.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fred on 12/8/15.
 */
public class EventModel implements Parcelable {
    String title;
    String end;
    String start;
    String id;
    String description;
    SettingsModel settingsModel;
    String trigger;
    String wifiSSID;

    public EventModel(){
        super();
    }

    protected EventModel(Parcel in) {
        title = in.readString();
        end = in.readString();
        start = in.readString();
        id = in.readString();
        description = in.readString();
        wifiSSID = in.readString();
        trigger = in.readString();
        settingsModel = in.readParcelable(SettingsModel.class.getClassLoader());
    }

    public static final Creator<EventModel> CREATOR = new Creator<EventModel>() {
        @Override
        public EventModel createFromParcel(Parcel in) {
            return new EventModel(in);
        }

        @Override
        public EventModel[] newArray(int size) {
            return new EventModel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(end);
        parcel.writeString(start);
        parcel.writeString(id);
        parcel.writeString(description);
        parcel.writeString(wifiSSID);
        parcel.writeString(trigger);
        parcel.writeParcelable(settingsModel, i);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public SettingsModel getSettings() {
        return settingsModel;
    }

    public EventModel setSettings(SettingsModel settings) {
        settingsModel = settings;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public EventModel setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getEnd() {
        return end;
    }

    public EventModel setEnd(String end) {
        this.end = end;
        return this;
    }

    public String getStart() {
        return start;
    }

    public EventModel setStart(String start) {
        this.start = start;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public EventModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getId() {
        return id;
    }

    public EventModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getTrigger(){
        return trigger;
    }

    public EventModel setTrigger(String trigger){
        this.trigger = trigger;
        return this;
    }

    public String getWifiSSID(){
        return wifiSSID;
    }

    public EventModel setWifiSSID(String wifiSSID){
        this.wifiSSID = wifiSSID;
        return this;
    }

}
