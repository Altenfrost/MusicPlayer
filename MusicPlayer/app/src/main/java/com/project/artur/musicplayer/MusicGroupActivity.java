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
    private Fragment currentFragment = null;
    private final String PLAYER_TAG = "player_tag";
    private final String GROUP_TAG = "group_tag";
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

        IS_LAND = getResources().getBoolean(R.bool.isLand);
        System.out.println("POOOOOOOOOOOO");
        if (findViewById(R.id.music_group_container)!=null){
            setMusicListFragment();



        /*if (!IS_LAND) {
            if (this.currentFragment == null)
                setMusicListFragment();
            else{
                System.out.println("WYWOłanie setMUsicPLayerFragment");
                setMusicPlayerFragment();
            }
*/
        } else {
            System.out.println("TERAZ TWORZE");
            this.songPlayerFragment = new SongPlayerFragment();
            System.out.println("STWORZYLEM");
            this.musicGroupFragment = new MusicGroupFragment();
            if (this.songPlayerFragment!=null && this.musicGroupFragment!=null)
                System.out.println("ZNALEZIONO");
            System.out.println("ILOSC FRAGMENTOW"+fm.getFragments().size());

            /*this.songPlayerFragment = (SongPlayerFragment) fm.findFragmentByTag(PLAYER_TAG);
            this.musicGroupFragment = (MusicGroupFragment) fm.findFragmentByTag(GROUP_TAG);
*/


            /*if (fm.findFragmentById(R.id.music_group_container)!=null){
                this.songPlayerFragment = (SongPlayerFragment) fm.findFragmentById(R.id.music_group_container);
                System.out.println("FUCK YEAH");

            }*/

            ft = fm.beginTransaction();

            ft.replace(R.id.music_group_fragment, musicGroupFragment);
            ft.replace(R.id.song_player_fragment, songPlayerFragment);
            ft.commit();
            System.out.println("ILOSC FRAGMENTOW"+fm.getFragments().size());

            /*ft = fm.beginTransaction();
            System.out.println("RAZ");

            System.out.println("DWA");
            this.songPlayerFragment = (SongPlayerFragment) fm.findFragmentById(R.id.song_player_fragment);
            System.out.println("TRZY");
            ft.replace(R.id.music_group_fragment,this.musicGroupFragment);
            ft.replace(R.id.song_player_fragment, this.songPlayerFragment);
            ft.commit();
            fm.executePendingTransactions();*/
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
        System.out.println("SET MUSIC LIST FRAGMENT");
        ft = this.fm.beginTransaction();
        this.musicGroupFragment = new MusicGroupFragment();
        this.currentFragment = this.musicGroupFragment;
        ft.replace(R.id.music_group_container, this.musicGroupFragment, GROUP_TAG);
        ft.commit();
    }

    private void setMusicPlayerFragment() {
        System.out.println("SET MUSIC PLAYER FRAGMENT");
        ft = this.fm.beginTransaction();
        this.songPlayerFragment = new SongPlayerFragment();
        this.currentFragment = songPlayerFragment;
        ft.replace(R.id.music_group_container, this.songPlayerFragment,PLAYER_TAG);

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
