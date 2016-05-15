package com.project.artur.musicplayer;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MusicGroupActivity  extends AppCompatActivity implements MusicGroupFragment.OnSelectedSongListener, SongPlayerFragment.OnSongActionListener{
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment currentFragment = null;
    private Song actualSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_group);

        boolean isLand = getResources().getBoolean(R.bool.isLand);
        System.out.println("ISLAND "+isLand);
        if (!isLand){
            System.out.println("USTAWIAM FRAGMENT");
            setOverviewFragment();
        }

    }
    private void setOverviewFragment() {
        System.out.println("DZIALA");
        FragmentTransaction ft = this.fm.beginTransaction();
        this.currentFragment = new MusicGroupFragment();
        ft.replace(R.id.music_group_container, this.currentFragment);
        ft.commit();
    }
    private void setMusicPlayerFragment() {
        FragmentTransaction ft = this.fm.beginTransaction();
        SongPlayerFragment songPlayerFragment = new SongPlayerFragment();
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
