package com.project.artur.musicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alten on 2016-05-07.
 */
public class Song implements Parcelable {
    private SongDuration songDuration;
    private String title;
    private String author;
    private String albumName;
    private Uri fileUri;
    private String filePath;
    private int bitRate;
    private boolean detailedVersion;
    private Bitmap albumPhoto;


    public Song(String songDuration, String title, String author, String albumName, Uri fileUri, String path, boolean detailedVersion) {
        this.filePath = path;
        this.songDuration = new SongDuration(songDuration);
        this.title = title;
        this.author = author;
        this.albumName = albumName;
        this.fileUri = fileUri;
        this.detailedVersion = detailedVersion;

        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(path);
        this.bitRate = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
        if (detailedVersion == true) {
            createBitmap(metaRetriever);
        }

        metaRetriever.release();

    }

    private void createBitmap(MediaMetadataRetriever metaRetriever) {
        byte[] albumPhotoBytes = metaRetriever.getEmbeddedPicture();
        if (albumPhotoBytes != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inDither = true;

            this.albumPhoto = BitmapFactory.decodeByteArray(albumPhotoBytes, 0, albumPhotoBytes.length, options);
            detailedVersion = true;
        }
    }


    protected Song(Parcel in) {
        songDuration = in.readParcelable(SongDuration.class.getClassLoader());
        title = in.readString();
        author = in.readString();
        albumName = in.readString();
        fileUri = in.readParcelable(Uri.class.getClassLoader());
        filePath = in.readString();
        bitRate = in.readInt();
        detailedVersion = in.readByte() != 0;
        albumPhoto = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getFilePath() {
        return filePath;
    }


    public void setAlbumPhoto(Bitmap albumPhoto) {
        this.albumPhoto = albumPhoto;
    }

    public String getDuration() {
        return songDuration.toString();
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getAlbumName() {
        return albumName;
    }

    public SongDuration getSongDuration() {
        return songDuration;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public int getBitRate() {
        return bitRate;
    }

    public Bitmap getAlbumPhoto() {
        if (detailedVersion == true) {
            return albumPhoto;
        } else {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(this.filePath);
            createBitmap(mediaMetadataRetriever);
            return albumPhoto;
        }

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(songDuration, flags);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(albumName);
        dest.writeParcelable(fileUri, flags);
        dest.writeString(filePath);
        dest.writeInt(bitRate);
        dest.writeByte((byte) (detailedVersion ? 1 : 0));
        dest.writeParcelable(albumPhoto, flags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Song song = (Song) o;

        if (bitRate != song.bitRate) return false;
        if (title != null ? !title.equals(song.title) : song.title != null) return false;
        if (author != null ? !author.equals(song.author) : song.author != null) return false;
        if (albumName != null ? !albumName.equals(song.albumName) : song.albumName != null)
            return false;
        if (fileUri != null ? !fileUri.equals(song.fileUri) : song.fileUri != null) return false;
        return filePath != null ? filePath.equals(song.filePath) : song.filePath == null;

    }

}
