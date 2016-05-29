package com.project.artur.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Alten on 2016-05-20.
 */
public class PlaylistAdapter extends BaseAdapter {
    private Context context;
    private List<String> playlistNames;

    public PlaylistAdapter(Context context, List<String> playlistsNames) {
        this.context = context;
        this.playlistNames = playlistsNames;
    }

    public void setPlaylistNames(List<String> playlistNames) {
        this.playlistNames = playlistNames;
    }

    @Override
    public int getCount() {
        return this.playlistNames.size();
    }

    @Override
    public String getItem(int position) {
        return this.playlistNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        View playlistView;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            playlistView = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.playlist_row_layout, parent, false);
            playlistView.setTag(viewHolder);
        } else {
            playlistView = convertView;
            viewHolder = (ViewHolder) playlistView.getTag();

        }
        bindPlaylistToView(position, viewHolder, playlistView);

        return playlistView;

    }

    private void bindPlaylistToView(int position, ViewHolder viewHolder, View playlistView) {
        viewHolder.playlistTitle = (TextView) playlistView.findViewById(R.id.playlist_title);
        viewHolder.playlistTitle.setText(this.playlistNames.get(position));
    }


    private class ViewHolder {
        private TextView playlistTitle;
    }

}
