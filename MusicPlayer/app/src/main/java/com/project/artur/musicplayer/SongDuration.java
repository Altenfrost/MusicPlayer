package com.project.artur.musicplayer;

import java.io.Serializable;

/**
 * Created by Alten on 2016-05-07.
 */
public class SongDuration implements Serializable {
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

    @Override
    public String toString() {
        return minutes + ":" + seconds;
    }
}
