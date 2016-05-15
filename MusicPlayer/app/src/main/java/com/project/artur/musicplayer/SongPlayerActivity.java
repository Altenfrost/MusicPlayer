package com.project.artur.musicplayer;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.IOException;

public class SongPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String SONG_KEY = "song";
    private Song songToPlay;
    private Button playButton, nextSongButton, previousSongButton, forwardButton, backButton;
    private SeekBar songSeekBar;
    private final int SKIP_VALUE = 5000;

    private MediaPlayer songPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Oncreate INIT");
        setContentView(R.layout.activity_song_player);


        InitializeControls();
        Intent actualIntent = getIntent();

        songToPlay = (Song) actualIntent.getExtras().getParcelable(SONG_KEY);
        Uri songUri = songToPlay.getFileUri();


        songPlayer = MediaPlayer.create(getApplicationContext(), songUri);



    }

    private void InitializeControls() {
        playButton = (Button) findViewById(R.id.play_button);
        nextSongButton = (Button) findViewById(R.id.next_song_button);
        previousSongButton = (Button) findViewById(R.id.previous_song_button);
        forwardButton = (Button) findViewById(R.id.forward_button);
        backButton = (Button) findViewById(R.id.back_button);
        songSeekBar = (SeekBar) findViewById(R.id.songSeekBar);

        playButton.setOnClickListener(this);
        nextSongButton.setOnClickListener(this);
        previousSongButton.setOnClickListener(this);
        forwardButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.play_button: {
                if (songPlayer.isPlaying()) {
                    songPlayer.pause();
                    playButton.setText(R.string.play);
                } else {
                    songPlayer.start();

                    playButton.setText(R.string.pause);
                }
                break;
            }
            case R.id.forward_button:
                songPlayer.seekTo(songPlayer.getCurrentPosition() + SKIP_VALUE);
                break;
            case R.id.back_button:
                songPlayer.seekTo(songPlayer.getCurrentPosition() - SKIP_VALUE);
                break;
        }
    }
}
