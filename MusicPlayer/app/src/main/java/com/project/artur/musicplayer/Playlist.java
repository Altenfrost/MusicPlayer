package com.project.artur.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Alten on 2016-05-21.
 */
public class Playlist implements Parcelable {
    private List<Song> songsInPlaylist;
    private String playlistTitle;

    public Playlist(List<Song> songsInPlaylist, String playlistTitle) {
        this.songsInPlaylist = songsInPlaylist;
        this.playlistTitle = playlistTitle;
    }

    public List<Song> getSongsInPlaylist() {
        return songsInPlaylist;
    }

    public void setSongsInPlaylist(List<Song> songsInPlaylist) {
        this.songsInPlaylist = songsInPlaylist;
    }

    public String getPlaylistTitle() {
        return playlistTitle;
    }

    public void setPlaylistTitle(String playlistTitle) {
        this.playlistTitle = playlistTitle;
    }

    protected Playlist(Parcel in) {
        songsInPlaylist = in.createTypedArrayList(Song.CREATOR);
        playlistTitle = in.readString();
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(songsInPlaylist);
        dest.writeString(playlistTitle);
    }
}
