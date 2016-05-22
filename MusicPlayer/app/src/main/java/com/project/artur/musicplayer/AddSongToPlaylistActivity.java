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
    private List<String> availablePlaylistsNames;
    private String infoAboutSong;
    private String playlistToChangeName;
    private PlaylistAdapter playlistAdapter;
    private PlaylistDatabase playlistDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song_to_playlist);

        Intent intent = getIntent();
        this.songToAdd = intent.getParcelableExtra(SongPlayerFragment.SONG_KEY);

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

        this.infoAboutSong = "Musisz wybrać playlistę dla: "+songToAdd.getTitle();
        this.summary.setText(infoAboutSong);
    }

    private void initializeListView() {
        availablePlaylistsNames = new ArrayList<>();

        playlistAdapter = new PlaylistAdapter(getApplicationContext(),availablePlaylistsNames);

        availablePlaylistsView.setAdapter(playlistAdapter);
        availablePlaylistsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlaylist = playlistAdapter.getItem(position);
                playlistToChangeName = selectedPlaylist;

                summary.setText(infoAboutSong+ " \n Wybrana playlista:"+playlistToChangeName);
            }

        });
    }

    private void setListenersOnButtons() {
        this.confirmPlaylistNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistToChangeName = newPlaylistName.getText().toString();
                summary.setText(infoAboutSong+ " \n Wybrana playlista:"+playlistToChangeName);
            }
        });

        this.savePlaylistChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playlistToChangeName!=null && playlistToChangeName.length()!=0){
                    saveSongToPlaylist();
                }


            }
        });
    }

    private void saveSongToPlaylist() {
        PlaylistProvider playlistProvider = new PlaylistDatabase(this);
        Playlist playlist = playlistProvider.getPlaylist(playlistToChangeName);
        if (playlist!=null){
            if (checkIfSongBelongToPlaylist(playlist))
                return;


            playlistProvider.addToExistedPlaylist(playlistToChangeName, songToAdd);
            Toast.makeText(this,"Dodano do istniejącej playlisty",Toast.LENGTH_SHORT).show();
            return;
        } else {
            Playlist newPlaylist = createNewPlaylist();
            playlistProvider.addPlaylist(newPlaylist);
            Toast.makeText(this,"Utworzono nową playlistę",Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    @NonNull
    private Playlist createNewPlaylist() {
        List<Song> songsInPlaylist = new ArrayList<Song>();
        songsInPlaylist.add(songToAdd);
        return new Playlist(songsInPlaylist,playlistToChangeName);
    }

    private boolean checkIfSongBelongToPlaylist(Playlist playlist) {
        for (Song songInPlaylist: playlist.getSongsInPlaylist()){
            if (songInPlaylist.getTitle().equals(songToAdd.getTitle())){
                Toast.makeText(this,"Ta piosenka już jest w tej playliście!",Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }
    /*private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    *//**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     *//*
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    *//**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     *//*
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }*/
}
