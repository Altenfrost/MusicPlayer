package com.project.artur.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Thread.sleep;

public class SongPlayerFragment extends Fragment implements View.OnClickListener {
    private OnSongActionListener onSongActionListener;
    public static final String SONG_KEY = "song_key";
    private Song songToPlay;
    private ImageView songAlbum;
    private TextView songDetailsTitle, songDetailsAuthor, songDetailsAlbumTitle, songDetailsBitrate;
    private Button playButton, nextSongButton, previousSongButton, forwardButton, backButton;
    private SeekBar songSeekBar;
    private Thread updateSeekBar;
    private final int SKIP_VALUE = 5000;
    private static MediaPlayer songPlayer;


    public interface OnSongActionListener {
        Song getNextSong();

        Song getPreviousSong();

        Song getActualSong();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    public SongPlayerFragment() {
        // Required empty public constructor
    }

    public Song getSongToPlay() {
        return songToPlay;
    }

    public void setSongToPlay(Song songToPlay) {
        this.songToPlay = songToPlay;
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        System.out.println("ON ATTACH IN SONGPLAYER FRAGMENT");
        if (context instanceof OnSongActionListener) {
            onSongActionListener = (OnSongActionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSongActionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //setRetainInstance(true);
        // gdy go dodam to zwraca: java.lang.IllegalArgumentException: No view found for id 0x7f0c0072 (com.project.artur.musicplayer:id/music_group_container) for fragment SongPlayerFragment{a31205c #1 id=0x7f0c0072 songPlayer}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_song_player, container, false);


        initializeControls(relativeLayout);
        if (songToPlay!=null)
            updateSongInfo(songToPlay);
        return relativeLayout;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    private void initializeControls(View view) {
        songDetailsTitle = (TextView) view.findViewById(R.id.song_details_title);
        songDetailsAuthor = (TextView) view.findViewById(R.id.song_details_author);
        songDetailsAlbumTitle = (TextView) view.findViewById(R.id.song_details_album_title);
        songDetailsBitrate = (TextView) view.findViewById(R.id.song_details_bitrate);

        songAlbum = (ImageView) view.findViewById(R.id.song_player_image);
        playButton = (Button) view.findViewById(R.id.play_button);
        nextSongButton = (Button) view.findViewById(R.id.next_song_button);
        previousSongButton = (Button) view.findViewById(R.id.previous_song_button);
        forwardButton = (Button) view.findViewById(R.id.forward_button);
        backButton = (Button) view.findViewById(R.id.back_button);
        songSeekBar = (SeekBar) view.findViewById(R.id.song_SeekBar);

        playButton.setOnClickListener(this);
        nextSongButton.setOnClickListener(this);
        previousSongButton.setOnClickListener(this);
        forwardButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                songPlayer.seekTo(seekBar.getProgress());
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.song_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.assign_song_item: {
                if (songToPlay == null) {
                    Toast.makeText(getContext(), "Nie wybrałeś żadnej pisoenki!", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    if (songPlayer.isPlaying())
                        songPlayer.stop();


                    Intent intent = new Intent(this.getContext(), AddSongToPlaylistActivity.class);
                    Bitmap temp = songToPlay.getAlbumPhoto();
                    songToPlay.setAlbumPhoto(null);
                    intent.putExtra(SONG_KEY, songToPlay);
                    startActivity(intent);
                    songToPlay.setAlbumPhoto(temp);
                }


                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }

    }


    public void updateSongInfo(Song newSong) {
        if (songPlayer != null && songPlayer.isPlaying()) {
            songPlayer.stop();
            this.songSeekBar.setProgress(0);
        }
        songToPlay = newSong;
        if (newSong != null) {
            songPlayer = MediaPlayer.create(getContext(), newSong.getFileUri());
            playButton.setText(R.string.play);


            setSongDetails();

            createPlayBarTask();
        }

    }

    private void createPlayBarTask() {

        updateSeekBar = new Thread(new Runnable() {
            @Override
            public void run() {
                int totalDuration = songPlayer.getDuration();
                int currPosition = 0;
                songSeekBar.setMax(totalDuration);
                while (currPosition < totalDuration ) {
                    try {
                        sleep(500);

                        currPosition = songPlayer.getCurrentPosition();
                        songSeekBar.setProgress(currPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setSongDetails() {

        Bitmap mainPhotoDetails = songToPlay.getAlbumPhoto();
        if (mainPhotoDetails != null)
            this.songAlbum.setImageBitmap(songToPlay.getAlbumPhoto());
        else
            this.songAlbum.setImageResource(R.drawable.album_art_default);

        this.songDetailsAuthor.setText(getResources().getString(R.string.song_author_prefix) + songToPlay.getAuthor());
        this.songDetailsTitle.setText(getResources().getString(R.string.song_title_prefix) + songToPlay.getTitle());
        this.songDetailsAlbumTitle.setText(getResources().getString(R.string.song_album_prefix) + songToPlay.getAlbumName());

        if (songDetailsBitrate != null) {
            this.songDetailsBitrate.setText(getResources().getString(R.string.song_bitrate_prefix) + String.valueOf(songToPlay.getBitRate()));
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (songPlayer != null) {
            switch (id) {
                case R.id.play_button: {
                    if (songPlayer.isPlaying()) {
                        songPlayer.pause();
                        playButton.setText(R.string.play);
                    } else {
                        songPlayer.start();
                        if (updateSeekBar != null && !updateSeekBar.isAlive())
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
                case R.id.next_song_button: {
                    updateSongInfo(onSongActionListener.getNextSong());
                    playButton.setText(R.string.play);
                    break;
                }
                case R.id.previous_song_button: {
                    updateSongInfo(onSongActionListener.getPreviousSong());
                    playButton.setText(R.string.play);
                    break;
                }

            }
        }

    }



    @Override
    public void onDetach() {
        super.onDetach();
        onSongActionListener = null;
    }
}
