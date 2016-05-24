package com.project.artur.musicplayer;


import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class MusicGroupActivity extends AppCompatActivity implements MusicGroupFragment.OnMusicGroupActionListener, SongPlayerFragment.OnSongActionListener {
    private final FragmentManager fm = getSupportFragmentManager();
    private FragmentTransaction ft;
    public static boolean IS_LAND = false;

    private final String PLAYER_KEY = "songPlayer";
    private final String MGROUP_KEY = "musicgroup";
    private MusicGroupFragment musicGroupFragment;
    private SongPlayerFragment songPlayerFragment;
    private Song actualSong;
    private Handler handler = new Handler();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // w tym momencie te fragmenty jeszcze zawierają dane, lecz gdy odwołam się do nich w onCreate
        // to owszem, widać, że się zapisały, ale są całkowicie puste
        MusicGroupFragment musicGroupFragment = (MusicGroupFragment) fm.findFragmentByTag(MGROUP_KEY);
        fm.putFragment(outState,MGROUP_KEY,musicGroupFragment);
        if (this.songPlayerFragment!=null){
            SongPlayerFragment songPlayerFragment = (SongPlayerFragment) fm.findFragmentByTag(PLAYER_KEY);
            fm.putFragment(outState,PLAYER_KEY,songPlayerFragment);
            outState.putParcelable("actualSong",this.songPlayerFragment.getSongToPlay());
        }

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
        IS_LAND = getResources().getBoolean(R.bool.isLand);
        if (findViewById(R.id.music_group_container)!=null){
            setMusicListFragment();


        } else if (IS_LAND){
            // te if-y powinny działac, gdyby nie fakt, że gdy wywołuje getFragment to otrzymuje null reference
            if (savedInstanceState!=null && savedInstanceState.containsKey(MGROUP_KEY)){
                this.musicGroupFragment = (MusicGroupFragment) fm.getFragment(savedInstanceState,MGROUP_KEY);
                if (savedInstanceState.containsKey(PLAYER_KEY)){
                    //poniższa linijka jest tylko po to, by ominąć problem pustych referencji
                    Song song = savedInstanceState.getParcelable("actualSong");
                    this.songPlayerFragment = (SongPlayerFragment) fm.getFragment(savedInstanceState,PLAYER_KEY);

                    this.songPlayerFragment = new SongPlayerFragment();
                    this.songPlayerFragment.setSongToPlay(song);
                }else {
                    this.songPlayerFragment = new SongPlayerFragment();
                }
            } else {
                this.musicGroupFragment = new MusicGroupFragment();
                this.songPlayerFragment = new SongPlayerFragment();
            }
            this.musicGroupFragment = new MusicGroupFragment();// to jest tutaj tylko po to, żeby na tą chwilę działało, bo tak naprawdę nie potrzebuje
            // pobierać starego fragmentu, potrzebuję tylko zaktualizowanej instancji obiektu AllSongsList, którą cały czas mam


            ft = fm.beginTransaction();
            ft.replace(R.id.music_group_fragment, musicGroupFragment);
            ft.replace(R.id.song_player_fragment, songPlayerFragment);
            ft.commit();


        }

    }


    private void setMusicListFragment() {
        ft = this.fm.beginTransaction();
        this.musicGroupFragment = new MusicGroupFragment();

        ft.replace(R.id.music_group_container, this.musicGroupFragment, MGROUP_KEY);
        ft.commit();
    }

    private void setMusicPlayerFragment() {
        ft = this.fm.beginTransaction();
        this.songPlayerFragment = new SongPlayerFragment();
        ft.replace(R.id.music_group_container, this.songPlayerFragment,PLAYER_KEY);

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

        if (!IS_LAND)
            setMusicPlayerFragment();
        this.songPlayerFragment.updateSongInfo(actualSong);
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

        actualSong =this.musicGroupFragment.getPreviousSongInList();
        return actualSong;
    }

    @Override
    public Song getActualSong() {
        return actualSong;
    }


}
