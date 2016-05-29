package com.project.artur.musicplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.project.artur.musicplayer.DbConstants.*;

/**
 * Created by Alten on 2016-05-21.
 */
public class PlaylistDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_PLAYLIST_TITLE + " TEXT, " +
                    COLUMN_SONG_TITLE + " TEXT, " +
                    COLUMN_SONG_AUTHOR + " TEXT, " +
                    COLUMN_SONG_ALBUM_NAME + " TEXT, " +
                    COLUMN_SONG_DURATION + " TEXT, " +
                    COLUMN_SONG_FILE_URI + " TEXT, " +
                    COLUMN_SONG_PATH + " TEXT, " +
                    COLUMN_SONG_BITRATE + " INTEGER);";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;


    public PlaylistDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
    }

}
