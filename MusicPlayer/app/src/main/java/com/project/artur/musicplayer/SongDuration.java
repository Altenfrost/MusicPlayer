package com.project.artur.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Alten on 2016-05-07.
 */
public class SongDuration implements Parcelable {
    private String rawSongDuration;
    private String minutes;
    private String seconds;

    public SongDuration(String rawSongDuration) {
        this.rawSongDuration = rawSongDuration;
        long dur = Long.parseLong(this.rawSongDuration);
        seconds = String.valueOf((dur % 60000) / 1000);
        if (seconds.length() == 1)
            seconds += "0";
        minutes = String.valueOf(dur / 60000);
    }

    protected SongDuration(Parcel in) {
        rawSongDuration = in.readString();
        minutes = in.readString();
        seconds = in.readString();
    }

    public static final Creator<SongDuration> CREATOR = new Creator<SongDuration>() {
        @Override
        public SongDuration createFromParcel(Parcel in) {
            return new SongDuration(in);
        }

        @Override
        public SongDuration[] newArray(int size) {
            return new SongDuration[size];
        }
    };

    public String getRawSongDuration() {
        return rawSongDuration;
    }

    @Override
    public String toString() {
        return minutes + ":" + seconds;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(rawSongDuration);
        dest.writeString(minutes);
        dest.writeString(seconds);
    }
}
