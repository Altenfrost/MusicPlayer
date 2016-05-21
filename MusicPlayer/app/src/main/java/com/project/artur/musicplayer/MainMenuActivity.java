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

        Button button = (Button) findViewById(R.id.music_button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo(MusicGroupActivity.class);
            }
        });
        /*Button exitButton = (Button) findViewById(R.id.exit_button);
        exitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
*/

    }



    private void goTo(Class destinationClass){
        Intent i = new Intent(this, destinationClass);
        startActivity(i);
    }


}
