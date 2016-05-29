package com.project.artur.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
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

import java.io.IOException;

import static java.lang.Thread.sleep;

public class SongPlayerFragment extends Fragment implements View.OnClickListener {
    public static final String LAST_PLAYED_SONG = "lastPlayedSong";
    public static final String SONG_CURRENT_POSITION = "songCurrentPosition";
    private OnSongActionListener onSongActionListener;
    public static final String SONG_KEY = "song_key";
    private Song songToPlay;
    private String playlistTitle;
    private ImageView songAlbum;
    private TextView songDetailsTitle, songDetailsAuthor, songDetailsAlbumTitle, songDetailsBitrate;
    private Button playButton, nextSongButton, previousSongButton, forwardButton, backButton;
    private SeekBar songSeekBar;
    private Thread updateSeekBarTask;
    private boolean isPlaylist = false;
    private final int SKIP_VALUE = 5000;
    private static MediaPlayer songPlayer;

    public static MediaPlayer getSongPlayer() {
        return songPlayer;
    }


    public interface OnSongActionListener {
        Song getNextSong();

        Song getPreviousSong();

        void deleteSong(Song songToDelete);
    }


    public void setPlaylistTitle(String playlistTitle) {
        this.playlistTitle = playlistTitle;
        isPlaylist = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (songPlayer != null) {
            outState.putInt(SONG_CURRENT_POSITION, songPlayer.getCurrentPosition());
            outState.putParcelable(LAST_PLAYED_SONG, songToPlay);
        }


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

        if (songToPlay != null)
            setSongDetails();
        return relativeLayout;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(LAST_PLAYED_SONG) && savedInstanceState.containsKey(SONG_CURRENT_POSITION)) {
            Song savedSong = savedInstanceState.getParcelable(LAST_PLAYED_SONG);
            int currentPosition = savedInstanceState.getInt(SONG_CURRENT_POSITION);
            updateSongInfo(savedSong, currentPosition);
        }

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
                if (songPlayer != null)
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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem deleteMenuItem = menu.findItem(R.id.delete_song_item);
        if (deleteMenuItem != null) {
            if (isPlaylist == true) {
                deleteMenuItem.setVisible(true);
            } else {
                deleteMenuItem.setVisible(false);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (songToPlay == null) {
            Toast.makeText(getContext(), R.string.no_song_choose_info, Toast.LENGTH_SHORT).show();
            return false;
        }
        switch (item.getItemId()) {
            case R.id.assign_song_item: {

                if (songPlayer.isPlaying())
                    songPlayer.stop();


                Intent intent = new Intent(this.getContext(), AddSongToPlaylistActivity.class);
                Bitmap temp = optimizeSong();
                intent.putExtra(SONG_KEY, songToPlay);
                startActivity(intent);
                returnToDefaultSongDataState(temp);


                break;
            }
            case R.id.delete_song_item: {
                if (songPlayer!=null && songPlayer.isPlaying())
                    songPlayer.stop();

                PlaylistProvider playlistProvider = new PlaylistDatabase(this.getContext());
                int result = playlistProvider.removeFromPlaylist(playlistTitle, songToPlay.getTitle());
                if (result == 0)
                    Toast.makeText(this.getContext(), R.string.delete_song_operation_failed, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(this.getContext(), R.string.delete_song_operation_success, Toast.LENGTH_LONG).show();

                onSongActionListener.deleteSong(songToPlay);
                break;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
        return true;

    }

    private void returnToDefaultSongDataState(Bitmap temp) {
        songToPlay.setAlbumPhoto(temp);
    }

    private Bitmap optimizeSong() {
        Bitmap temp = songToPlay.getAlbumPhoto();
        songToPlay.setAlbumPhoto(null);
        return temp;
    }


    public void updateSongInfo(Song newSong, int currentPositionInSong) {
        if (newSong == null)
            return;
        songToPlay = newSong;
        if (songPlayer != null) {
            if (songPlayer.isPlaying())
                songPlayer.stop();
            songPlayer.reset();
            songPlayer.setLooping(true);
            try {
                songPlayer.setDataSource(getContext(), songToPlay.getFileUri());

                songPlayer.prepare();
                songPlayer.seekTo(currentPositionInSong);
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            songPlayer = MediaPlayer.create(getContext(), newSong.getFileUri());
            songPlayer.setLooping(true);
        }
        this.songSeekBar.setMax(songPlayer.getDuration());
        this.songSeekBar.setProgress(currentPositionInSong);
        playButton.setText(R.string.play);

        setSongDetails();

        createPlayBarTask();


    }

    private void createPlayBarTask() {

        updateSeekBarTask = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    int totalDuration = songPlayer.getDuration();
                    int currPosition = songPlayer.getCurrentPosition();
                    songSeekBar.setMax(totalDuration);
                    while (currPosition < totalDuration && songPlayer.isPlaying()) {
                        try {

                            sleep(500);

                            currPosition = songPlayer.getCurrentPosition();
                            if (currPosition >= 0)
                                songSeekBar.setProgress(currPosition);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    synchronized (songPlayer) {
                        try {
                            songPlayer.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } while (songPlayer.isPlaying());


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
                        if (wasThreadAndSeekBarInitialized()) {
                            updateSeekBarTask.start();
                        }


                        playButton.setText(R.string.pause);
                        synchronized (songPlayer) {
                            songPlayer.notifyAll();
                        }
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
                    updateSongInfo(onSongActionListener.getNextSong(), 0);
                    playButton.setText(R.string.play);
                    break;
                }
                case R.id.previous_song_button: {
                    updateSongInfo(onSongActionListener.getPreviousSong(), 0);
                    playButton.setText(R.string.play);
                    break;
                }

            }
        }

    }

    private boolean wasThreadAndSeekBarInitialized() {
        return updateSeekBarTask != null && !updateSeekBarTask.isAlive() && updateSeekBarTask.getState() == Thread.State.NEW;
    }

    @Override
    public void onResume() {
        super.onResume();

        playButton.setText(R.string.play);
        if (songPlayer != null && songToPlay != null) {
            updateSongInfo(songToPlay, songPlayer.getCurrentPosition());
            synchronized (songPlayer) {
                songPlayer.notifyAll();
            }

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSongActionListener = null;
    }
}
