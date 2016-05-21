package com.project.artur.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;


public class MusicGroupFragment extends Fragment {
    private ListView songListView;
    private boolean isServiceBound = false;
    private int lastSongPos = 0;
    private static List<Song> songList;
    private SongAdapter songAdapter;
    private OnMusicGroupActionListener onMusicGroupActionListener;
    protected SongsFinderService songsFinderService;

    private ServiceConnection songsFinderConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            //System.out.println("WYWOŁANIE ON SERVICE CONNECTED IN FRAGMENT");
            isServiceBound = true;

            SongsFinderService.LocalSongBinder binder = (SongsFinderService.LocalSongBinder) service;
            songsFinderService = binder.getService();
            if (songList == null || songList.size() == 0) {
                songsFinderService.setSongsFound(songList);
                songsFinderService.findSongsList(onMusicGroupActionListener);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isServiceBound = false;
        }
    };

    public Song getNextSongInList() {
        lastSongPos = (lastSongPos + 1 == songList.size()) ? 0 : lastSongPos + 1;
        return songAdapter.getItem(lastSongPos);
    }

    public Song getPreviousSongInList() {
        lastSongPos = (lastSongPos - 1 < 0) ? songList.size() - 1 : lastSongPos - 1;
        return songAdapter.getItem(lastSongPos);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("songlist", (ArrayList<? extends Parcelable>) songAdapter.getSongList());
        //System.out.println("On Save INSTANCE IN FRAGMENT");
    }

    public interface OnMusicGroupActionListener {
        void showSongMenu(Song selectedSong);

        void refreshMusicList(List<Song> actualList);
    }


    public MusicGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null){
            //System.out.println("HEH");
            songList = savedInstanceState.getParcelableArrayList("songlist");
            //System.out.println("ROZMIAR"+songList.size());
        }else if (songList == null){
            songList = new ArrayList<>();
        }
        //System.out.println("ON CREATE IN FRAGMENT");
        songAdapter = new SongAdapter(getContext(), songList);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_music_group, container, false);
        // Inflate the layout for this fragment
        return relativeLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("OnActivityCreated IN MUSICGROUP FRAGMENT");
        songListView = (ListView) this.getView().findViewById(R.id.song_list);
        if (savedInstanceState != null) {
            System.out.println("Nie jest nullem");
            songList = savedInstanceState.getParcelableArrayList("songlist");
        }
        //System.out.println("ROZMIAR LISTY SONGLIST:" + songList.size() + " ROZMIARY SONGLIST ADAPTERA:" + songAdapter.getSongList().size());
        //songList = songAdapter.getSongList();//wtf
        initializeList();
        if (songList.size() == 0) {
            Intent intent = new Intent(this.getActivity(), SongsFinderService.class);
            this.getActivity().bindService(intent, songsFinderConnection, Context.BIND_AUTO_CREATE);
        }


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.println("ON ATTACH IN MUSICGROUP FRAGMENT");
        if (context instanceof OnMusicGroupActionListener) {
            onMusicGroupActionListener = (OnMusicGroupActionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMusicGroupActionListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //System.out.println("OnDestroy IN FRAGMENT" + songList.size() + " I " + songAdapter.getSongList().size());
        songList = songAdapter.getSongList();

        if (isServiceBound)
            this.getActivity().unbindService(songsFinderConnection);
    }

    @Override
    public void onResume() {
        super.onResume();
        //System.out.println("On Resume" + songList.size() + "IN FRAGMENT");
    }

    @Override
    public void onPause() {
        super.onPause();
        //System.out.println("On Pause" + songList.size() + "IN FRAGMENT");
    }

    @Override
    public void onStop() {
        super.onStop();
        //System.out.println("On Stop" + songList.size() + "IN FRAGMENT");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //System.out.println("ON DETACH IN FRAGMENT");
        onMusicGroupActionListener = null;
    }

    public SongAdapter getSongAdapter() {
        return songAdapter;
    }

    private void initializeList() {
        songListView.setAdapter(songAdapter);
        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song selectedSong = songAdapter.getItem(position);
                lastSongPos = position;
                onMusicGroupActionListener.showSongMenu(selectedSong);
            }

        });

    }

}
