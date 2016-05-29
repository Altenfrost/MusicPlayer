package com.project.artur.musicplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity {

    public static final String CHOOSEN_PLAYLIST = "choosenPlaylist";
    private ListView playlistView;
    private PlaylistAdapter playlistAdapter;
    private PlaylistProvider playlistProvider;

    @Override
    protected void onResume() {
        super.onResume();

        List<String> actualPlaylistNames = playlistProvider.getPlaylistNames();
        playlistAdapter.setPlaylistNames(actualPlaylistNames);
        playlistAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        playlistView = (ListView) findViewById(R.id.playlist_list);

        playlistProvider = new PlaylistDatabase(this);

        playlistAdapter = new PlaylistAdapter(this, playlistProvider.getPlaylistNames());

        playlistView.setAdapter(playlistAdapter);

        playlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlaylist = playlistAdapter.getItem(position);

                Intent intent = new Intent(getApplicationContext(), MusicGroupActivity.class);
                Playlist playlistChoosen = playlistProvider.getPlaylist(selectedPlaylist);

                intent.putExtra(CHOOSEN_PLAYLIST, playlistChoosen);
                startActivity(intent);
                clearSongPlayer();
            }

        });


    }

    private void clearSongPlayer() {
        if (SongPlayerFragment.getSongPlayer() != null)
            SongPlayerFragment.getSongPlayer().reset();
    }


}
