package com.project.artur.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import static java.lang.Thread.sleep;

public class SongPlayerFragment extends Fragment implements View.OnClickListener {
    private OnSongActionListener onSongActionListener;
    private final String SONG_KEY = "song_key";
    private Song songToPlay;
    private ImageView songAlbum;
    private TextView songDetailsTitle, songDetailsAuthor, songDetailsAlbumTitle, songDetailsBitrate;
    private Button playButton, nextSongButton, previousSongButton, forwardButton, backButton;
    private SeekBar songSeekBar;
    private Thread updateSeekBar;
    private final int SKIP_VALUE = 5000;

    private static MediaPlayer songPlayer;

    public SongPlayerFragment() {

        // Required empty public constructor
    }

    public Song getSongToPlay() {
        return songToPlay;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("ON CREATE VIEW IN SONGPLAYER FRAGMENT1");
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_song_player, container, false);
        System.out.println("ON CREATE VIEW IN SONGPLAYER FRAGMENT2");
        if (Looper.myLooper() == Looper.getMainLooper())
            System.out.println("WATEK GLOWNY");
        initializeControls(relativeLayout);
        System.out.println("PRZED POBRANIEM PIOSENKI");
        if (savedInstanceState != null) {
            songToPlay = savedInstanceState.getParcelable(SONG_KEY);
            if (songToPlay != null) {
                System.out.println("HHEHEHEHEHEHEH");
                System.out.println("PARAMETRY NASZEJ PIOSENKI:" + songToPlay.getTitle() + "  " + songToPlay.getAlbumName() + " " + songToPlay.getAuthor());
                updateSongInfo(songToPlay);
            }

        }
        System.out.println("KONIEC");


        return relativeLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SONG_KEY, songToPlay);
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
    public void onDetach() {
        super.onDetach();
        System.out.println("ON DETACH IN SONGPLAYERFRAGMENT");
        onSongActionListener = null;
    }

    public interface OnSongActionListener {
        Song getNextSong();

        Song getPreviousSong();

        Song getActualSong();
        // TODO: Update argument type and name
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("STWORZONO NA NOWO");

    }

    public void updateSongInfo(Song newSong) {
        System.out.println("XDDDD");
        if (songPlayer != null && songPlayer.isPlaying()) {
            songPlayer.stop();
        }
        songToPlay = newSong;
        System.out.println("PARAMETRY NASZEJ PIOSENKI2:" + songToPlay.getTitle() + "  " + songToPlay.getAlbumName() + " " + songToPlay.getAuthor());
        if (newSong != null) {
            System.out.println("NASZA PIOSENKA NIE JEST PUSTA");
            songPlayer = MediaPlayer.create(getContext(), newSong.getFileUri());
            songPlayer.setLooping(true);
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

                while (currPosition < totalDuration) {
                    try {
                        sleep(500);
                        if (songPlayer.isPlaying()) {
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

    private void setSongDetails() {

        Bitmap mainPhotoDetails = songToPlay.getAlbumPhoto();
        if (mainPhotoDetails != null)
            this.songAlbum.setImageBitmap(songToPlay.getAlbumPhoto());
        else
            this.songAlbum.setImageResource(R.drawable.album_art_default);

        System.out.println("USTAWIAM:" + songToPlay.getTitle() + " " + getResources().getConfiguration().orientation);
        this.songDetailsAuthor.setText(songToPlay.getAuthor());
        this.songDetailsTitle.setText(songToPlay.getTitle());
        this.songDetailsAlbumTitle.setText(songToPlay.getAlbumName());
        if (songDetailsBitrate != null) {
            songToPlay.getBitRate();
            this.songDetailsBitrate.setText(String.valueOf(songToPlay.getBitRate()));
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("ON CREATE IN SONGPLAYER FRAGMENT");
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
        songSeekBar = (SeekBar) view.findViewById(R.id.songSeekBar);

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
                        if (updateSeekBar != null && !updateSeekBar.isAlive() )
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
}
