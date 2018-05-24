package com.cs345.harmanjeetdhillon.prioritizedtime;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * This class is used for the settings view. This settings view contents a button to play
 * background music, a button that links to the allow apps page, and a button
 * that has not yet been implemented called the progress icon changer. Thats coming soon.
 *
 * @author harmanjeet dhillon
 */

public class Settings extends AppCompatActivity {
    private final String TAG = "SETTINGS";
    private MediaPlayer mp;//media player for music
    //private MediaPlayer mpTap; //button Tap

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        //creating a media player to play the background music
        mp = MediaPlayer.create(this, R.raw.beethoven);
       // mpTap = MediaPlayer.create(this, R.raw.button_tap);
        mp.setLooping(true);//making the music loop
        //mp.start();//starting the music thread
        //mp.pause();
    }

    /**
     * Method is an onClick for the changeProgressIcon button, which has not yet been implemented.
     * @param view this
     */
    public void changeProgressIcon(View view){
       // mpTap.start();//playing button tap sound
        Context context = getApplicationContext();
        CharSequence text = "This feature will be coming soon!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * Method is used as a onClick to for the allow apps button. Method also moves views to the
     * app selector view
     * @param view this
     */
    public void allowAppsPressed(View view) {
        //mpTap.start();//playing button tap sound
        //using a intent to move the view
        Intent intent = new Intent(getApplicationContext(), AppSelecter.class);
        startActivity(intent);
    }

    /**
     * Method is used as a onClick for the background music button. Method plays and pauses
     * background music. Method also displays a toast telling the user if music was paused or
     * not.
     * @param view this
     */
    public void toggleMusicPressed(View view) {
        //mpTap.start();//playing button tap sound
        Context context = getApplicationContext();
        CharSequence text = "";
        int duration = Toast.LENGTH_SHORT;

        //if the sound is playing then pause, if its paused then play
        if(mp.isPlaying()){
            text = "Paused!";
            mp.pause();
        } else {
            text = "Started!";
            mp.start();
        }

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}
