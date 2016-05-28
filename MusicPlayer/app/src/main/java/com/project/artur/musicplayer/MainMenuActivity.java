package com.project.artur.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeControls();

    }

    private void initializeControls() {
        Button musicGroupButton = (Button) findViewById(R.id.music_button);
        musicGroupButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo(MusicGroupActivity.class);
            }
        });

        Button playListButton = (Button) findViewById(R.id.playlist_button);
        playListButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo(PlaylistActivity.class);
            }
        });
    }


    private void goTo(Class destinationClass) {
        Intent i = new Intent(this, destinationClass);
        startActivity(i);
    }


}
