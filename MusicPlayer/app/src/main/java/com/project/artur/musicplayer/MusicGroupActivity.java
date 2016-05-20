package com.project.artur.musicplayer;


import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MusicGroupActivity extends AppCompatActivity implements MusicGroupFragment.OnMusicGroupActionListener, SongPlayerFragment.OnSongActionListener {
    private final FragmentManager fm = getSupportFragmentManager();
    private FragmentTransaction ft;
    private boolean isLand = false;
    private Fragment currentFragment = null;
    private MusicGroupFragment musicGroupFragment;
    private SongPlayerFragment songPlayerFragment;
    private Song actualSong;
    private Handler handler = new Handler();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("ON SAVE");
        //outState.putParcelableArrayList("songlist", (ArrayList<? extends Parcelable>) musicGroupFragment.getSongAdapter().getSongList());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("NISZCZE MUSIC GROUP");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("ON CREATE W ACTIVITY");
        setContentView(R.layout.activity_music_group);


        isLand = getResources().getBoolean(R.bool.isLand);
        if (!isLand) {
            if (this.currentFragment == null)
                setMusicListFragment();
            else
                setMusicPlayerFragment();
        } else {
            this.musicGroupFragment = (MusicGroupFragment) fm.findFragmentById(R.id.music_group_fragment);
            this.songPlayerFragment = (SongPlayerFragment) fm.findFragmentById(R.id.song_player_fragment);
            /*ft = this.fm.beginTransaction();

            System.out.println("SPRAWDZENIE, CZY FT JEST PUSTY:"+ft.isEmpty());
            if (this.musicGroupFragment == null){
                System.out.println("MUSIC GROUP FRAGMENT JEST NULLEM WIEC TWORZE NOWY");
                this.musicGroupFragment = new MusicGroupFragment();
            }
            if (this.songPlayerFragment == null){
                System.out.println("SONG PLAYER FRAGMENT JEST NULLEM WIEC TWORZE NOWY");
                this.songPlayerFragment = new SongPlayerFragment();
            }

            ft.add( R.id.music_group_fragment, this.musicGroupFragment);
            ft.add(R.id.song_player_fragment, this.songPlayerFragment);
            ft.commit()*/;
        }

    }

    private void setMusicListFragment() {
        ft = this.fm.beginTransaction();
        this.musicGroupFragment = new MusicGroupFragment();
        this.currentFragment = this.musicGroupFragment;
        ft.replace(R.id.music_group_container, this.currentFragment);
        ft.commit();
    }

    private void setMusicPlayerFragment() {
        ft = this.fm.beginTransaction();
        this.songPlayerFragment = new SongPlayerFragment();
        this.currentFragment = songPlayerFragment;
        ft.replace(R.id.music_group_container, this.currentFragment);

        // dodajemy transakcję na stos
        // dzięki temu możemy wrócić przyciskiem BACK
        ft.addToBackStack(null);
        // zatwierdzamy transakcję

        ft.commit();
        this.fm.executePendingTransactions();
    }


    @Override
    public void showSongMenu(Song selectedSong) {
        actualSong = selectedSong;

        if (!isLand)
            setMusicPlayerFragment();
        this.songPlayerFragment.updateSongInfo(actualSong);
    }

    @Override
    public void refreshMusicList(final List<Song> actualList) {
        System.out.println("ODŚWIEŻAM LISTĘ UTWORÓW");

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (musicGroupFragment != null) {
                    musicGroupFragment.getSongAdapter().setSongList(actualList);
                    musicGroupFragment.getSongAdapter().notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public Song getNextSong() {

        actualSong = this.musicGroupFragment.getNextSongPos();


        return actualSong;
    }

    @Override
    public Song getPreviousSong() {
        return null;
    }

    @Override
    public Song getActualSong() {
        return actualSong;
    }


}
