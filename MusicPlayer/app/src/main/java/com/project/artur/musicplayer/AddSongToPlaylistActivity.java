package com.project.artur.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class AddSongToPlaylistActivity extends Activity {

    private ListView availablePlaylistsView;
    private EditText newPlaylistName;
    private TextView summary;
    private Button confirmPlaylistNameButton, savePlaylistChooseButton;
    private Song songToAdd;
    private String infoAboutSong;
    private String playlistToChangeName;
    private PlaylistAdapter playlistAdapter;
    private PlaylistProvider playlistProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song_to_playlist);

        Intent intent = getIntent();
        this.songToAdd = intent.getParcelableExtra(SongPlayerFragment.SONG_KEY);
        if (this.songToAdd == null)
            throw new IllegalArgumentException("No song was selected");

        playlistProvider  = new PlaylistDatabase(this);

        playlistAdapter = new PlaylistAdapter(getApplicationContext(), playlistProvider.getPlaylistNames());

        initializeControls();

        initializeListView();

        setListenersOnButtons();
    }

    private void initializeControls() {
        this.newPlaylistName = (EditText) findViewById(R.id.new_playlist_edit_text);
        this.summary = (TextView) findViewById(R.id.playlist_operation_summary);
        this.availablePlaylistsView = (ListView) findViewById(R.id.available_playlists_list);
        this.confirmPlaylistNameButton = (Button) findViewById(R.id.accept_new_playlist_button);
        this.savePlaylistChooseButton = (Button) findViewById(R.id.assign_playlist_button);

        this.infoAboutSong = "Musisz wybrać playlistę dla: " + songToAdd.getTitle();
        this.summary.setText(infoAboutSong);
    }

    private void initializeListView() {
        availablePlaylistsView.setAdapter(playlistAdapter);
        availablePlaylistsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlaylist = playlistAdapter.getItem(position);
                playlistToChangeName = selectedPlaylist;

                summary.setText(infoAboutSong + " \n Wybrana playlista:" + playlistToChangeName);
            }

        });
    }

    private void setListenersOnButtons() {
        this.confirmPlaylistNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistToChangeName = newPlaylistName.getText().toString();
                summary.setText(infoAboutSong + " \n Wybrana playlista:" + playlistToChangeName);
            }
        });

        this.savePlaylistChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playlistToChangeName != null && playlistToChangeName.length() != 0) {
                    saveSongToPlaylist();
                }


            }
        });
    }

    private void saveSongToPlaylist() {

        Playlist playlist = playlistProvider.getPlaylist(playlistToChangeName);
        if (playlist != null) {
            if (checkIfSongBelongToPlaylist(playlist))
                return;


            playlistProvider.addToExistedPlaylist(playlistToChangeName, songToAdd);
            Toast.makeText(this, "Dodano do istniejącej playlisty", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Playlist newPlaylist = createNewPlaylist();
            playlistProvider.addPlaylist(newPlaylist);
            Toast.makeText(this, "Utworzono nową playlistę", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    @NonNull
    private Playlist createNewPlaylist() {
        List<Song> songsInPlaylist = new ArrayList<Song>();
        songsInPlaylist.add(songToAdd);
        return new Playlist(songsInPlaylist, playlistToChangeName);
    }

    private boolean checkIfSongBelongToPlaylist(Playlist playlist) {
        for (Song songInPlaylist : playlist.getSongsInPlaylist()) {
            if (songInPlaylist.getTitle().equals(songToAdd.getTitle())) {
                Toast.makeText(this, "Ta piosenka już jest w tej playliście!", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }
}
