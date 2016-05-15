package com.project.artur.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
    private List<Song> songList;
    private SongAdapter songAdapter;
    private OnSelectedSongListener onSelectedSongListener;
    protected SongsFinderService songsFinderService;
    protected Boolean songsFinderServiceBound = false;

    private Handler handler = new Handler();
    private ServiceConnection songsFinderConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            SongsFinderService.LocalSongBinder binder = (SongsFinderService.LocalSongBinder) service;
            songsFinderService = binder.getService();
            songsFinderServiceBound = true;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    songList = songsFinderService.getSongsList();
                    songAdapter.setSongList(songList);
                    System.out.println("ROZMIAR TABLICY"+songList.size());
                    songAdapter.notifyDataSetChanged();
                }
            };
            handler.post(runnable);


            System.out.println("USTAWILEM ADAPTER"+songList.size());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            songsFinderServiceBound = false;
        }
    };


    public interface OnSelectedSongListener {
        void showSongMenu(Song selectedSong);
    }


    public MusicGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("STWORZYLEM FRAGMENT");


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_music_group, container, false);
        System.out.println(songsFinderServiceBound+" w onCreateView");
        // Inflate the layout for this fragment
        return relativeLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        songListView = (ListView) this.getView().findViewById(R.id.song_list);
        System.out.println(songsFinderServiceBound+" w onActivityCreated");
        songList = new ArrayList<>();
        initializeList();
        Intent intent = new Intent(this.getActivity(), SongsFinderService.class);
        this.getActivity().bindService(intent , songsFinderConnection, Context.BIND_AUTO_CREATE);


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.println(songsFinderServiceBound+" w onAttach");
        if (context instanceof OnSelectedSongListener) {
            onSelectedSongListener = (OnSelectedSongListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSelectedSongListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (songsFinderServiceBound) {
            this.getActivity().unbindService(songsFinderConnection);
            songsFinderServiceBound = false;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSelectedSongListener = null;
    }



    private void initializeList() {
        System.out.println("Inicjuje liste"+songsFinderServiceBound);


        /*List<File> mySongs = songsFinder.getSongsFromSource(new File("/storage/extSdCard/Android/data/com.project.artur.musicplayer/files/Music"));*/
        /*for (File songFile : mySongs) {
            songList.add(new Song(songFile));
        }*/
        songAdapter = new SongAdapter(getView().getContext(), songList);

        songListView.setAdapter(songAdapter);
        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song selectedSong = songAdapter.getItem(position);
                onSelectedSongListener.showSongMenu(selectedSong);
            }
        });
    }

    /*private void playMusic(Song selectedSong) {
        Intent i = new Intent(getActivity(), SongPlayerActivity.class);
        i.putExtra(SongPlayerActivity.SONG_KEY, selectedSong);
        startActivity(i);
    }*/


    /*public static MusicGroupFragment newInstance(String param1, String param2) {
        MusicGroupFragment fragment = new MusicGroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
*/

}
