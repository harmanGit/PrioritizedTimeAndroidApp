package com.cs345.harmanjeetdhillon.prioritizedtime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The class is used to view and wipe the user history in the app database. This method is linked
 * up with the history view.
 *
 * @author harmanjeet dhillon
 */

public class History extends AppCompatActivity {
    private DatabaseAid myDatabase; //database with all app and score info
    //private MediaPlayer mp; //button Tap Sound media player
    //this layout contents all textviews (textviews represent the user history)
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        myDatabase = new DatabaseAid(this);
        //mp = MediaPlayer.create(this, R.raw.button_tap);//adding the button tap sound
        //this layout contents all textviews (textviews represent the user history)
        linearLayout = (LinearLayout) findViewById(R.id.histroyLinearLayout);
        showHistory();//calling the method to display the history at page launch up
    }

    /**
     * Method is used as a onClick for the wipe history button. This method will also wipe the
     * user history in the database, and the running score saved in the sharedPref. After the
     * data is wiped this method will also change the view to the main activity.
     * @param view this
     */
    public void wipeHistoryPressed(View view) {
        //mp.start();//playing button tap sound
        myDatabase.wipeHistory();//calling the method to wipe stored user data from the database
        //clearing the score from the sharedPref
        SharedPreferences sharedPref = getSharedPreferences("hostAppUser", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("currentTotalScore", 0);
        editor.commit();
        //Moving back to the main activity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    /**
     * This method shows the user history stored in the database.
     */
    private void showHistory() {
        //getting the cursor to show history
        Cursor cursor = myDatabase.dataDumb();
        if (cursor.getCount() == 0) {
            System.out.println("NO DATA FOUND");
            return;
        }
        //going through the database and displaying all user history
        while (cursor.moveToNext()) {
            //if (cursor.getString(0) != null)
            //    createTextView("APPS : " + cursor.getString(1));
            if (cursor.getInt(1) != 0) {
                createTextView("Score Added : " + cursor.getInt(1));
            }
            if (cursor.getInt(2) != 0 ) {
                createTextView("Score Subbed : " + cursor.getInt(2));
            }

        }
    }

    /**
     * This method is used to create textviews which represent the user history
     * @param textToSet string text, the text that will placed on the textview when created
     */
    private void createTextView(String textToSet) {
        //creating a text view
        TextView scoreHistory = new TextView(this);
        scoreHistory.setText(textToSet);//setting score as the text
        scoreHistory.setTextSize(25);//setting size
        //setting themed color
        scoreHistory.setTextColor(getResources().getColor(R.color.colorAccent));
        scoreHistory.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //adding the text to the view
        linearLayout.addView(scoreHistory);
    }
}