package com.project.artur.musicplayer;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        playlistView = (ListView) findViewById(R.id.playlist_list);

        final PlaylistProvider playlistProvider = new PlaylistDatabase(this);

        playlistAdapter = new PlaylistAdapter(this, playlistProvider.getPlaylistNames());

        playlistView.setAdapter(playlistAdapter);

        playlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlaylist = playlistAdapter.getItem(position);

                Intent intent = new Intent(getApplicationContext(), MusicGroupActivity.class);
                Playlist playlistChoosen = playlistProvider.getPlaylist(selectedPlaylist);
                /*List<Song> songsInChoosenPlaylist = new ArrayList<Song>();
                for (Song songInPlaylist: playlistChoosen.getSongsInPlaylist()){
                    songsInChoosenPlaylist.
                }*/

                intent.putExtra(CHOOSEN_PLAYLIST, playlistChoosen);
                startActivity(intent);
                /*playlistToChangeName = selectedPlaylist;

                summary.setText(infoAboutSong + " \n Wybrana playlista:" + playlistToChangeName);*/
            }

        });
        List<Playlist> playlists = playlistProvider.getAllPlaylists();


    }
}
