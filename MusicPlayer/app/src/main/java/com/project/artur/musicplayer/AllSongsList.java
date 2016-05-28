package com.project.artur.musicplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alten on 2016-05-23.
 */
public class AllSongsList {
    private static AllSongsList allSongsListInstance = null;

    private List<Song> allSongs;

    private AllSongsList() {
    }

    public static AllSongsList getInstance(){
        if(allSongsListInstance == null)
        {
            allSongsListInstance = new AllSongsList();
            allSongsListInstance.setAllSongs(new ArrayList<Song>());
        }
        return allSongsListInstance;
    }

    public List<Song> getAllSongs() {
        return allSongs;
    }

    public void setAllSongs(List<Song> allSongs) {
        this.allSongs = allSongs;
    }
}
