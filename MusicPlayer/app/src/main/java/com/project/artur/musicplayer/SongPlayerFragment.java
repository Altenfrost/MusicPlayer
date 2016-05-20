package com.project.artur.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import static java.lang.Thread.sleep;

public class SongPlayerFragment extends Fragment implements View.OnClickListener {
    private OnSongActionListener onSongActionListener;
    private Song songToPlay;
    private ImageView songAlbum;
    private Button playButton, nextSongButton, previousSongButton, forwardButton, backButton;
    private SeekBar songSeekBar;
    private Thread updateSeekBar;
    private final int SKIP_VALUE = 5000;

    private static MediaPlayer songPlayer;

    public SongPlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_song_player, container, false);
        System.out.println("ON CREATE VIEW IN SONGPLAYER FRAGMENT");


        return relativeLayout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSongActionListener) {
            onSongActionListener = (OnSongActionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSongActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSongActionListener = null;
    }

    public interface OnSongActionListener {
        Song getNextSong();

        Song getPreviousSong();

        Song getActualSong();
        // TODO: Update argument type and name
    }

    public void updateSongInfo(Song newSong) {
        if (songPlayer != null && songPlayer.isPlaying()) {
            songPlayer.stop();
        }
        songToPlay = newSong;
        if (songToPlay != null)
            songPlayer = MediaPlayer.create(getContext(), songToPlay.getFileUri());

        Bitmap mainPhotoDetails = songToPlay.getAlbumPhoto();
        if (mainPhotoDetails!=null)
            this.songAlbum.setImageBitmap(songToPlay.getAlbumPhoto());
        else
            this.songAlbum.setImageResource(R.drawable.album_art_default);

        updateSeekBar = new Thread(new Runnable() {
            @Override
            public void run() {
                int totalDuration = songPlayer.getDuration();
                int currPosition = 0;
                songSeekBar.setMax(totalDuration);

                while (currPosition < totalDuration){
                    try {
                        sleep(500);
                        if (songPlayer.isPlaying()){
                            currPosition = songPlayer.getCurrentPosition();
                            songSeekBar.setProgress(currPosition);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("ON CREATE W SONGPLAYER FRAGMENT");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeControls();

    }

    private void initializeControls() {
        songAlbum = (ImageView) getView().findViewById(R.id.song_player_image);
        playButton = (Button) getView().findViewById(R.id.play_button);
        nextSongButton = (Button) getView().findViewById(R.id.next_song_button);
        previousSongButton = (Button) getView().findViewById(R.id.previous_song_button);
        forwardButton = (Button) getView().findViewById(R.id.forward_button);
        backButton = (Button) getView().findViewById(R.id.back_button);
        songSeekBar = (SeekBar) getView().findViewById(R.id.songSeekBar);

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
                    if (!updateSeekBar.isAlive())
                        updateSeekBar.start();

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
            case R.id.next_song_button:{
                updateSongInfo(onSongActionListener.getNextSong());
                playButton.setText(R.string.play);
            }
        }
    }
}
