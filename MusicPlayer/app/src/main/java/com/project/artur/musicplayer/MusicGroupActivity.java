package com.project.artur.musicplayer;


import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MusicGroupActivity  extends AppCompatActivity implements MusicGroupFragment.OnMusicGroupActionListener, SongPlayerFragment.OnSongActionListener{
    private final FragmentManager fm = getSupportFragmentManager();
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
        setContentView(R.layout.activity_music_group);

        boolean isLand = getResources().getBoolean(R.bool.isLand);
        System.out.println("ISLAND "+isLand);
        if (!isLand){
            setMusicListFragment();
        }

    }
    private void setMusicListFragment() {
        FragmentTransaction ft = this.fm.beginTransaction();
        this.musicGroupFragment = new MusicGroupFragment();
        this.currentFragment = this.musicGroupFragment;
        ft.replace(R.id.music_group_container, this.currentFragment);
        ft.commit();
    }
    private void setMusicPlayerFragment() {
        FragmentTransaction ft = this.fm.beginTransaction();
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
        setMusicPlayerFragment();
    }

    @Override
    public void refreshMusicList(final List<Song> actualList) {
        System.out.println("Odświeżam");
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (musicGroupFragment!=null){
                    musicGroupFragment.getSongAdapter().setSongList(actualList);
                    musicGroupFragment.getSongAdapter().notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public Song getNextSong() {
        return null;
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
