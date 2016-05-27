package com.project.artur.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class MusicGroupFragment extends Fragment {
    private ListView songListView;
    private boolean isServiceBound = false;
    private int lastSongPos = 0;
    private SongAdapter songAdapter;
    private OnMusicGroupActionListener onMusicGroupActionListener;
    protected SongsFinderService songsFinderService;

    public int getLastSongPos() {
        return lastSongPos;
    }

    public void setLastSongPos(int lastSongPos) {
        this.lastSongPos = lastSongPos;
    }

    private ServiceConnection songsFinderConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            isServiceBound = true;

            SongsFinderService.LocalSongBinder binder = (SongsFinderService.LocalSongBinder) service;
            songsFinderService = binder.getService();
            if (AllSongsList.getInstance().getAllSongs().size() == 0) {
                songsFinderService.findSongsList(onMusicGroupActionListener);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isServiceBound = false;
        }
    };

    public Song getNextSongInList() {
        lastSongPos = (lastSongPos + 1 == AllSongsList.getInstance().getAllSongs().size()) ? 0 : lastSongPos + 1;
        return songAdapter.getItem(lastSongPos);
    }

    public Song getPreviousSongInList() {
        lastSongPos = (lastSongPos - 1 < 0) ? AllSongsList.getInstance().getAllSongs().size() - 1 : lastSongPos - 1;
        return songAdapter.getItem(lastSongPos);

    }


    public interface OnMusicGroupActionListener {
        void showSong(Song selectedSong);

        void refreshMusicList();
    }


    public MusicGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AllSongsList.getInstance().getAllSongs()==null){
            AllSongsList.getInstance().setAllSongs(new ArrayList<Song>());
        }
        songAdapter = new SongAdapter(getContext(), AllSongsList.getInstance().getAllSongs());

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
        songListView = (ListView) this.getView().findViewById(R.id.song_list);

        initializeList();
        if (AllSongsList.getInstance().getAllSongs().size() == 0) {
            Intent intent = new Intent(this.getActivity(), SongsFinderService.class);
            this.getActivity().bindService(intent, songsFinderConnection, Context.BIND_AUTO_CREATE);
        }


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

        if (isServiceBound)
            this.getActivity().unbindService(songsFinderConnection);
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
                onMusicGroupActionListener.showSong(selectedSong);
            }

        });

    }

}
