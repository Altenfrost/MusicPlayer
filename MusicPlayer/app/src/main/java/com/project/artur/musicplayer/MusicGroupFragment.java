package com.project.artur.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.Nullable;
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
    private static List<Song> songList;
    private SongAdapter songAdapter;
    private OnMusicGroupActionListener onMusicGroupActionListener;
    protected SongsFinderService songsFinderService;

    private ServiceConnection songsFinderConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            SongsFinderService.LocalSongBinder binder = (SongsFinderService.LocalSongBinder) service;
            songsFinderService = binder.getService();
            if (songList == null || songList.size()==0){
                songsFinderService.setSongsFound(songList);
                songsFinderService.getSongsList(onMusicGroupActionListener);
            }

            /*Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    songList = songsFinderService.getSongsList(onMusicGroupActionListener);
                    songAdapter.setSongList(songList);
                    System.out.println("ROZMIAR TABLICY"+songList.size());
                    songAdapter.notifyDataSetChanged();
                }
            };
            handler.post(runnable);*/


            System.out.println("USTAWILEM ADAPTER" + songList.size());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("songlist", (ArrayList<? extends Parcelable>) songList);
        System.out.println("On Save");
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
        songList = new ArrayList<>();
        songAdapter = new SongAdapter(getContext(), songList);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState!=null)
            System.out.println("HURRRA");
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_music_group, container, false);
        // Inflate the layout for this fragment
        System.out.println("OnCreateView");
        return relativeLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("OnActivityCreated");
        songListView = (ListView) this.getView().findViewById(R.id.song_list);
        if (savedInstanceState!=null){
            System.out.println("Nie jest nullem");
            songList = savedInstanceState.getParcelableArrayList("songlist");
        }
        songList = songAdapter.getSongList();
        initializeList();
        Intent intent = new Intent(this.getActivity(), SongsFinderService.class);
        this.getActivity().bindService(intent, songsFinderConnection, Context.BIND_AUTO_CREATE);


    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.println("OnAttach");
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
        System.out.println("OnDestroy");
        this.getActivity().unbindService(songsFinderConnection);
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("On Resume"+songList.size());
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("On Pause"+songList.size());
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("On Stop"+songList.size());
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
                onMusicGroupActionListener.showSongMenu(selectedSong);
            }
        });
    }

}
