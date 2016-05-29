package com.project.artur.musicplayer;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;


public class MusicGroupActivity extends AppCompatActivity implements MusicGroupFragment.OnMusicGroupActionListener, SongPlayerFragment.OnSongActionListener {
    private final FragmentManager fm = getSupportFragmentManager();
    private FragmentTransaction ft;
    public static boolean IS_LAND = false;
    private Playlist deliveredPlaylist = null;
    private final String PLAYER_KEY = "songPlayer";
    private final String MGROUP_KEY = "musicGroup";
    private final String PLAYLIST_KEY = "actualPlaylist";
    private MusicGroupFragment musicGroupFragment;
    private SongPlayerFragment songPlayerFragment;
    private Song actualSong;
    private Handler handler = new Handler();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        fm.putFragment(outState, MGROUP_KEY, musicGroupFragment);
        fm.putFragment(outState, PLAYER_KEY, songPlayerFragment);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDeliveredPlaylist();

        setContentView(R.layout.activity_music_group);
        IS_LAND = getResources().getBoolean(R.bool.isLand);

        if (savedInstanceState != null) {
            songPlayerFragment = (SongPlayerFragment) fm.getFragment(savedInstanceState, PLAYER_KEY);
            musicGroupFragment = (MusicGroupFragment) fm.getFragment(savedInstanceState, MGROUP_KEY);

        } else {
            songPlayerFragment = new SongPlayerFragment();
            musicGroupFragment = new MusicGroupFragment();

            ft = fm.beginTransaction();

            ft.add(R.id.music_group_fragment, musicGroupFragment, MGROUP_KEY);
            ft.add(R.id.song_player_fragment, songPlayerFragment, PLAYER_KEY);

            ft.commit();
        }
        if (deliveredPlaylist != null) {
            this.musicGroupFragment.setSongsList(deliveredPlaylist.getSongsInPlaylist());
            this.songPlayerFragment.setPlaylistTitle(deliveredPlaylist.getPlaylistTitle());
        }
        setFragmentVisibility();
    }

    private void setDeliveredPlaylist() {
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            deliveredPlaylist = intent.getExtras().getParcelable(PlaylistActivity.CHOOSEN_PLAYLIST);
        }
    }

    private boolean isPlaylistPutInInstance(Bundle savedInstanceState) {
        return savedInstanceState != null && savedInstanceState.containsKey(PLAYLIST_KEY);
    }

    private void setFragmentVisibility() {
        ft = fm.beginTransaction();

        if (IS_LAND) {
            int backstackValue = fm.getBackStackEntryCount();
            if (backstackValue > 0) {
                fm.popBackStack();
            }
            ft.show(musicGroupFragment);
            ft.show(songPlayerFragment);
        } else {
            ft.show(musicGroupFragment);
            ft.hide(songPlayerFragment);

        }
        ft.commit();
    }


    @Override
    public void showSong(Song selectedSong) {
        actualSong = selectedSong;

        if (!IS_LAND) {
            fm.beginTransaction()
                    .show(songPlayerFragment)
                    .hide(musicGroupFragment)
                    .addToBackStack(null)
                    .commit();
        }
        this.songPlayerFragment.updateSongInfo(actualSong, 0);
    }

    @Override
    public void refreshMusicList() {

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (musicGroupFragment != null) {
                    musicGroupFragment.getSongAdapter().setSongList(AllSongsList.getInstance().getAllSongs());
                    musicGroupFragment.getSongAdapter().notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public Song getNextSong() {
        actualSong = this.musicGroupFragment.getNextSongInList();
        return actualSong;
    }

    @Override
    public Song getPreviousSong() {
        actualSong = this.musicGroupFragment.getPreviousSongInList();
        return actualSong;
    }

    @Override
    public void deleteSong(Song songToDelete) {
        updateDataAfterDelete(songToDelete);
        this.musicGroupFragment.getSongAdapter().notifyDataSetChanged();
        if (this.musicGroupFragment.getSongAdapter().getSongList().size() == 0)
            finish();
        else {

            this.songPlayerFragment.updateSongInfo(getNextSong(), 0);
        }


    }

    private void updateDataAfterDelete(Song songToDelete) {
        this.musicGroupFragment.getSongAdapter().getSongList().remove(songToDelete);
        deliveredPlaylist.getSongsInPlaylist().remove(songToDelete);
        Intent intent = getIntent();
        intent.putExtra(PlaylistActivity.CHOOSEN_PLAYLIST, deliveredPlaylist);
    }


}
