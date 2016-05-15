package com.project.artur.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class SongsFinderService extends Service {
    final String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
    final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    final String[] projection = { MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION};

    private final IBinder songsBinder = new LocalSongBinder();

    public class LocalSongBinder extends Binder {
        SongsFinderService getService() {

            //zwracamy instancje serwisu, przez nią odwołamy się następnie do metod.
            return SongsFinderService.this;
        }
    }


    public SongsFinderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("ZWRACAM BIND");
        return songsBinder;
    }

    public List<Song> getSongsList() {
        List<Song> songsFound = new ArrayList<>();
        System.out.println("ZACZYNAMY SZUKAC W SERWISIE");
                Cursor cursor = getContentResolver().query(uri,
                        projection, selection, null, null);

                if (cursor.getCount() == 0) {
                    System.out.println("Nie znaleziono żadnej piosenki");

                } else {
                    int i = 1;
                    System.out.println("ILOSC UTWOROW:"+cursor.getCount());
                    cursor.moveToFirst();
                    do {
                        System.out.println(i);
                        i++;

                        Uri playableUri
                                = Uri.withAppendedPath(uri,
                                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));

                        songsFound.add(new Song(
                                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                                playableUri,
                                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))

                        ));
                    } while (cursor.moveToNext());
                    cursor.close();
                }





        return songsFound;
    }
    /*public List<Song> getSongsList() {
        final String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        int columnIndex;
        final String[] projection = { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM_ID };

        handler.post(new Runnable() {
            public void run() {
                Cursor cursor = getContentResolver().query(uri,
                        projection, selection, null, null);

                if (cursor.getCount() == 0) {
                    System.out.println("Nie znaleziono żadnej piosenki");

                } else {
                    cursor.moveToFirst();
                    do {

                        System.out.println("Nazwa piosenki: "+cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                        *//*names.add(cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));*//*

                    } while (cursor.moveToNext());


            }
        });
    }
}*/
}
