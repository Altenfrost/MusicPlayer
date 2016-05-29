package com.project.artur.musicplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static com.project.artur.musicplayer.DbConstants.*;

/**
 * Created by Alten on 2016-05-21.
 */
public class PlaylistDatabase implements PlaylistProvider {
    private PlaylistDbHelper playlistDbHelper;
    private Context context;

    public PlaylistDatabase(Context context) {
        this.context = context;
        this.playlistDbHelper = new PlaylistDbHelper(context);
    }

    @Override
    public Playlist getPlaylist(String playlistName) {
        SQLiteDatabase db = playlistDbHelper.getReadableDatabase();

        String[] projection = {
                COLUMN_PLAYLIST_TITLE, COLUMN_SONG_TITLE, COLUMN_SONG_AUTHOR, COLUMN_SONG_ALBUM_NAME, COLUMN_SONG_DURATION, COLUMN_SONG_FILE_URI, COLUMN_SONG_PATH, COLUMN_SONG_BITRATE
        };
        Cursor cursor = db.query(
                TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                COLUMN_PLAYLIST_TITLE + "=?",                                // The columns for the WHERE clause
                new String[]{playlistName},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        cursor.moveToFirst();
        if (cursor.getCount() == 0)
            return null;
        List<Song> songsInPlaylist = getAllResultsFromCursor(cursor);
        cursor.close();

        return new Playlist(songsInPlaylist, playlistName);
    }

    @NonNull
    private List<Song> getAllResultsFromCursor(Cursor cursor) {
        List<Song> songsInPlaylist = new ArrayList<>();
        do {
            fetchDataAboutSong(cursor, songsInPlaylist);
        } while (cursor.moveToNext());
        return songsInPlaylist;
    }

    private void fetchDataAboutSong(Cursor cursor, List<Song> songsInPlaylist) {
        Uri convertedUri = Uri.parse(cursor.getString(cursor.getColumnIndex(COLUMN_SONG_FILE_URI)));

        songsInPlaylist.add(new Song(cursor.getString(cursor.getColumnIndex(COLUMN_SONG_DURATION)),
                cursor.getString(cursor.getColumnIndex(COLUMN_SONG_TITLE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_SONG_AUTHOR)),
                cursor.getString(cursor.getColumnIndex(COLUMN_SONG_ALBUM_NAME)),
                convertedUri,
                cursor.getString(cursor.getColumnIndex(COLUMN_SONG_PATH)), false));
    }

    @Override
    public List<String> getPlaylistNames() {
        List<String> allPlaylistsNames = new ArrayList<>();
        SQLiteDatabase db = playlistDbHelper.getReadableDatabase();

        String[] projection = {
                COLUMN_PLAYLIST_TITLE
        };

        Cursor cursor = db.query(
                TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                COLUMN_PLAYLIST_TITLE,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            do {
                allPlaylistsNames.add(cursor.getString(cursor.getColumnIndex(COLUMN_PLAYLIST_TITLE)));
            } while (cursor.moveToNext());
            cursor.close();

        }

        return allPlaylistsNames;

    }


    @Override
    public int removeFromPlaylist(String playlistName, String songToRemoveTitle) {
        SQLiteDatabase db = playlistDbHelper.getWritableDatabase();
        String whereClause = COLUMN_PLAYLIST_TITLE + "=?  AND " + COLUMN_SONG_TITLE + "=?";
        int result = db.delete(TABLE_NAME, whereClause, new String[]{playlistName, songToRemoveTitle});

        return result;
    }


    @Override
    public long addPlaylist(Playlist playlistToAdd) {
        SQLiteDatabase db = playlistDbHelper.getWritableDatabase();

        Song songToAdd = playlistToAdd.getSongsInPlaylist().get(0);

        ContentValues contentValues = fillValuesToInsert(playlistToAdd, songToAdd);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result;
    }

    @NonNull
    private ContentValues fillValuesToInsert(Playlist playlistToAdd, Song songToAdd) {
        ContentValues contentValues = fillValuesToInsert(playlistToAdd.getPlaylistTitle(), songToAdd);
        return contentValues;
    }

    @Override
    public long addToExistedPlaylist(String playlistName, Song songToAdd) {
        SQLiteDatabase db = playlistDbHelper.getWritableDatabase();

        ContentValues contentValues = fillValuesToInsert(playlistName, songToAdd);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result;
    }

    @NonNull
    private ContentValues fillValuesToInsert(String playlistName, Song songToAdd) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_PLAYLIST_TITLE, playlistName);
        contentValues.put(COLUMN_SONG_TITLE, songToAdd.getTitle());
        contentValues.put(COLUMN_SONG_AUTHOR, songToAdd.getAuthor());
        contentValues.put(COLUMN_SONG_ALBUM_NAME, songToAdd.getAlbumName());
        contentValues.put(COLUMN_SONG_DURATION, songToAdd.getSongDuration().getRawSongDuration());
        contentValues.put(COLUMN_SONG_FILE_URI, songToAdd.getFileUri().toString());
        contentValues.put(COLUMN_SONG_PATH, songToAdd.getFilePath());
        contentValues.put(COLUMN_SONG_BITRATE, songToAdd.getBitRate());
        return contentValues;
    }


}
