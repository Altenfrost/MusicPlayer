package com.project.artur.musicplayer;

import java.util.List;

/**
 * Created by Alten on 2016-05-21.
 */
public interface PlaylistProvider {
    Playlist getPlaylist(String playlistName);

    List<String> getPlaylistNames();

    List<Playlist> getAllPlaylists();

    int removeFromPlaylist(String playlistName, String songToRemoveTitle);

    long addPlaylist(Playlist playlistToAdd);

    long addToExistedPlaylist(String playlistName, Song songToAdd);
}
