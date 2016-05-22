package com.project.artur.musicplayer;

import java.util.List;

/**
 * Created by Alten on 2016-05-21.
 */
public interface PlaylistProvider {
    Playlist getPlaylist(String playlistName);

    List<String> getPlaylistNames();

    void removePlaylist(String playlistName);

    void addPlaylist(Playlist playlistToAdd);

    void addToExistedPlaylist(String playlistName, Song songToAdd);
}
