package com.project.artur.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by Alten on 2016-05-07.
 */
public class SongAdapter extends BaseAdapter {
    private Context context;
    private static List<Song> songList;

    public SongAdapter(Context context, List<Song> songList) {

        this.context = context;
        this.songList = songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    public List<Song> getSongList() {
        return songList;
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Song getItem(int position) {
        return songList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.song_row_layout, parent, false);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }
        bindSongToView(songList.get(position), convertView, viewHolder);


        return convertView;
    }

    private void bindSongToView(Song song, View songView, ViewHolder viewHolder) {
        viewHolder.songPhoto = (ImageView) songView.findViewById(R.id.song_photo);
        Bitmap mainPhoto = song.getAlbumPhoto();
        if (mainPhoto == null) {
            Picasso.with(context).load(R.drawable.album_art_default).into(viewHolder.songPhoto);
            //viewHolder.songPhoto.setImageResource(R.drawable.album_art_default);
        } else {
            //Picasso.with(context).load(song.getFileUri()).into(viewHolder.songPhoto); nie działa niestety
            viewHolder.songPhoto.setImageBitmap(mainPhoto);
        }

        viewHolder.songTitle = (TextView) songView.findViewById(R.id.song_title);
        viewHolder.songTitle.setText(song.getTitle());

        viewHolder.songDuration = (TextView) songView.findViewById(R.id.song_duration);
        viewHolder.songDuration.setText(song.getDuration().toString());
    }

    private class ViewHolder {
        private TextView songTitle;
        private ImageView songPhoto;
        private TextView songDuration;

    }


}
