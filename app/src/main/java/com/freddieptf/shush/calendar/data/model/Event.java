package com.freddieptf.shush.calendar.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by freddieptf on 19/10/16.
 */

public class Event implements Parcelable{

    private final long id;
    private final String name;
    private final long startTime;
    private final long endTime;
    private ShushProfile shushProfile;

    public Event(long id, String name, long startTime, long endTime){
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private Event(Parcel in) {
        id = in.readLong();
        name = in.readString();
        startTime = in.readLong();
        endTime = in.readLong();
        shushProfile = in.readParcelable(ShushProfile.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
        dest.writeParcelable(shushProfile, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public void addShushProfile(ShushProfile shushProfile){
        this.shushProfile = shushProfile;
    }

    public ShushProfile getShushProfile() {
        return shushProfile;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "name: " + name + "\n" + "id: " + id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Event && ((Event)obj).getId() == getId();
    }
}
