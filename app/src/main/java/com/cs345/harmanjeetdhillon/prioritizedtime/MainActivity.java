package com.cs345.harmanjeetdhillon.prioritizedtime;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Button;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationChannel;
import android.widget.Toast;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * This is the main class and first view the user sees also this class
 * does the core work for the app. This also has the timer and the
 * notifications features.
 *
 * @author harmanjeet dhillon
 */
public class MainActivity extends AppCompatActivity {
    private final String TAG = "MAINACTIVITY";
    private DatabaseAid myDataBase; //database with all app and score info
    private static NotificationCompat.Builder notifi; //notification builder
    private static final int uniqueID = 759456;//notification id
    private SharedPreferences sharedPref;//high score storage
    //private MediaPlayer mp; //button Tap Sound media player
    private SharedPreferences.Editor editor;
    private NumberPicker timePicker;//number picker in the view
    private LinearLayout timerLayout;//timer that counts down
    private TextView countDownMinsTextView;//countdown mins in the view
    private TextView countDownSecsTextView;//countdown secs in the view
    private TextView currentScoreTextView;//high score in the main view
    private Set<String> runningApps;//set of all running apps
    //set of all apps the user whitelisted to use while the focus timer is running
    private Set<String> whitelistedApps;
    private Button startButton;//start button in the main
    private Button historyButton;//history button in the main view
    private Boolean isTimerRunning;//remaining time used to penalize the user
    private Boolean penalize;//bool representing if the user should be penalize
    private Boolean isDistracting; //bool used to study users from getting distrated
    private int countDownTimeInMins;//user enter time to countdown from
    private int timeRemaining;//remaining time used to penalize the user
    private int currentScore;//current score
    private MyTimer timer;//timer obj

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDataBase = new DatabaseAid(this); //data with all app and score info
        timePicker = findViewById(R.id.timePicker);//number picker in the view
        timePicker.setMinValue(25);//min time to pick from
        timePicker.setMaxValue(60);//mac time to pick from
        timePicker.setValue(25);//default time
        //mp = MediaPlayer.create(this, R.raw.button_tap);//adding the button tap sound
        timerLayout = findViewById(R.id.countDownLayout);//layout for the entire main view
        countDownMinsTextView = findViewById(R.id.countDownMins);//countdown mins in the view
        countDownSecsTextView = findViewById(R.id.countDownSecs);//countdown secs in the view
        currentScoreTextView = findViewById(R.id.currentScore);//high score in the main view
        historyButton = findViewById(R.id.scoreHistory);//history button in the main view
        startButton = findViewById(R.id.startButton);//start button in the main
        //Saving data for later use.
        sharedPref = getSharedPreferences("hostAppUser", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        //displaying the user's score
        currentScoreTextView.setText(Integer.toString(sharedPref
                .getInt("currentTotalScore", 0)));
        countDownTimeInMins = 00;//user enter time to countdown from
        isTimerRunning = false;//bool used to check if the timer is running
        timer = new MyTimer(countDownTimeInMins);//timer obj
        //starting the creation of the notification
        notifi = new NotificationCompat.Builder(getApplicationContext(), "timerCompleted");
        timeRemaining = 0;//remaining time used to penalize the user
        currentScore = 0;//current score
        runningApps = new TreeSet<String>(); //set of all running apps
        //set of all apps the user whitelisted to use while the focus timer is running
        whitelistedApps = new TreeSet<String>();
        penalize = false;//bool representing if the user should be penalize
        isDistracting = true;//bool used to study users from getting distrated
    }

    @Override
    protected void onResume() {
        super.onResume();
        //removing the pop up notification after the user opens up the again
        notifi.setAutoCancel(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //TEMP CODE TILL THE DATABASE WORKS
        /**
        if(isTimerRunning){
            timer.cancel();//stopping the timer
            scoreManager(false, timeRemaining);//penalizing the user
            isTimerRunning = false;
        }
         */


        //This method uses the database to check with apps to see if they were whitelisted
        //but this method is current broken to a unknown flaw
        //shouldPenalize();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //TEMP CODE TILL THE DATABASE WORKS
        if(isTimerRunning){
            timer.cancel(); //stopping the timer
            //removing the timer for the number picker
            viewSwitcher(false, timerLayout, timePicker);//removing
            //penalizing the user for quiting early
            scoreManager(false, timeRemaining);
            isTimerRunning = false; //resetting the timer var
            countDownMinsTextView.setText("00");//resetting the timer
            countDownSecsTextView.setText("00");//resetting the timer
            startButton.setText("Begin!"); //changing the text to begin after timer is stopped
        }
    }

    /**
     * Method is used to check if the user should be penalized. The user can be penalized if they
     * run a blacklisted app and or they end the timer early.
     *
     * METHOD IS NOT USED BECAUSE MAKES CALLS TO THE DATABASE WHICH I SUSPECT ALSO BREAKS THE APP
     */
    public void shouldPenalize() {
        whitelistedApps = myDataBase.isAppWhitelist();//getting all the whitelisted apps form the database
        ActivityManager activityManager =
                (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        //BUG : This is know BUG. For some reason one one app running is found.
        List<ActivityManager.RunningAppProcessInfo> runningAppsList =
                activityManager.getRunningAppProcesses();
        Log.d(TAG, "appFinder : 1 Running apps: " + runningAppsList.size());
        //going through all the running apps to see if any blacklisted apps are running
        for (ActivityManager.RunningAppProcessInfo runningApp : runningAppsList) {
            Log.d(TAG, "appFinder : 2 " + runningApp.processName);
            for (String whitelistedApp : whitelistedApps) {
                //checking to see if a blacklisted app is being used
                if (!runningApp.processName.contains(whitelistedApp.toLowerCase())) {
                   //Log.d(TAG, "appFinder : reset score");
                    penalize = true;
                    break;
                }
            }
        }

        //penalizing the user and then resetting the var
        if (penalize) {
            timer.cancel();//stopping the timer
            scoreManager(false, timeRemaining);//penalizing the user
            penalize = false;//resetting the reset var
        }
    }

    /**
     * This method is the onClick for teh begin button which turns into the cancel button when
     * pressed. This method starts and stops the timer, and add and removes points from the user.
     * @param view
     */
    public void beginButtonPressed(View view) {
        //mp.start();//playing button tap sound
        //changing the begin button into the cancel button
        startButton.setText("Cancel!");

        //checking to see if the timer is running
        if (!isTimerRunning) {
            //if the timer is not running the, the timer will be started
            countDownTimeInMins = timePicker.getValue();//getting the user selected timer from the user
            int startTimer = countDownTimeInMins * 60;
            isTimerRunning = true;//resetting the timer
            //removing the number picker for the timer
            viewSwitcher(true, timerLayout, timePicker);
            timer = new MyTimer(startTimer);//creating the new timer
            timer.start();//starting the timer

        } else {
            timer.cancel(); //stopping the timer
            //removing the timer for the number picker
            viewSwitcher(false, timerLayout, timePicker);//removing
            //penalizing the user for quiting early
            scoreManager(false, timeRemaining);
            isTimerRunning = false; //resetting the timer var
            countDownMinsTextView.setText("00");//resetting the timer
            countDownSecsTextView.setText("00");//resetting the timer
            startButton.setText("Begin!"); //changing the text to begin after timer is stopped
        }
    }

    /**
     * This method is used as a onClick for the history button on the home page. All this method
     * does is change the view to the history page
     * @param view
     */
    public void historyButtonPressed(View view) {
        //mp.start();//playing button tap sound
        Context context = getApplicationContext();
        CharSequence text = "Study session will stop if you open this, you are getting distracted!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        //intent to move
        Intent intent = new Intent(getApplicationContext(), History.class);
        if(isTimerRunning){
            if(!isDistracting){
                //moving to the history page
                startActivity(intent);
                isDistracting = true;
            } else {
                toast.show();
                isDistracting = false;
            }
        } else {
            //moving to the history page
            startActivity(intent);
        }



    }

    /**
     * This method is used as a onClick for the settings button on the home page. All this method
     * does is change the view to the settings page
     * @param view
     */
    public void settingsPressed(View view) {
        //mp.start();//playing button tap sound
        Context context = getApplicationContext();
        CharSequence text = "Study session will stop if you open this, you are getting distracted!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        //intent to move
        Intent intent = new Intent(getApplicationContext(), Settings.class);
        if(isTimerRunning){
            if(!isDistracting){
                //moving to the history page
                startActivity(intent);
                isDistracting = true;
            } else {
                toast.show();
                isDistracting = false;
            }
        } else {
            //moving to the history page
            startActivity(intent);
        }

    }

    /**
     * Method will update current high score. Method also does the calculations for score. Method
     * requires a boolean representing if the user gets points added or a deduction. Lastly a the
     * time is required to calculate the score to be added or deducted.
     *
     * @param status boolean representing if the user gets points or a deduction
     * @param time   the time the user has completed
     */
    public void scoreManager(boolean status, int time) {
        currentScore = 0;//sharedPref.getInt("currentTotalScore", 0);
        boolean isInserted = false;
        if (status) {
            currentScore = (time * 2) + sharedPref.getInt("currentTotalScore", 0);
            //event Name appended with event Count because of over write issues
            editor.putInt("currentTotalScore", currentScore);
            Log.d(TAG, "CurrentTotalScore: " + currentScore);
            isInserted = myDataBase.insertScore(true, currentScore);

        } else {
            currentScore = sharedPref.getInt("currentTotalScore", 0) - time;
            //event Name appended with event Count because of over write issues
            editor.putInt("currentTotalScore", currentScore);
            Log.d(TAG, "CurrentTotalScore: " + currentScore);
            isInserted = myDataBase.insertScore(false, currentScore);
        }

        Log.d(TAG, "InsertedIntoDatabase: " + isInserted);
        currentScoreTextView.setText(Integer.toString(currentScore));
        editor.commit();
        currentScore = 0;
    }

    /**
     * Method is used to flip the view after the begin or cancelled button is pressed. This method
     * will flip to a timer if the begin button is click and hide the number picker. But if the
     * cancel button is clicked the the timer will be replaced by a number picker.
     * @param flip bool representing if the flip, true to hide the number picker
     *             false to hide the timer
     * @param lL linerlayout representing the timer layout
     * @param nP number picker representing the time picker
     */
    private void viewSwitcher(boolean flip, LinearLayout lL, NumberPicker nP) {
        if (flip) {
            nP.setEnabled(false);//disabling the number picker
            nP.setVisibility(View.GONE);//hiding the number picker
            lL.setVisibility(View.VISIBLE);//showing the timer
        } else {
            nP.setEnabled(true);//enabling the number picker
            nP.setVisibility(View.VISIBLE);//showing the number picker
            lL.setVisibility(View.GONE);//hiding the linear layout
        }
    }

    /**
     * This class runs the entire logical and view for the timer. This class also pushes out
     * notification when the timer is completed and will also add points to the users high score.
     *
     */
    class MyTimer extends CountDownTimer {

        public MyTimer(long secsRemaining) {
            super(secsRemaining * 1000 + 1,
                    1000);
        }

        int secsRemaining = 0;
        int minsRemaining = countDownTimeInMins;

        //This method called on onTick controls the countdown logic and updates the timer view
        @Override
        public void onTick(long millisUntilFinished) {
            secsRemaining--;
            //counting down
            if (secsRemaining < 0) {
                secsRemaining = 59;
                minsRemaining--;
            }
            timeRemaining = minsRemaining;
            //Log.d(TAG, "Time Remaining : " + timeRemaining);//used for testing

            //if statement allowing the textview to show single digit seconds with zero in front for
            //aesthetic purposes
            if(secsRemaining < 10){
                countDownSecsTextView.setText("0" + Integer.toString(secsRemaining));
            } else {
                //setting the textview with the updated time, this updates every second the
                //timer is running
                countDownMinsTextView.setText(Integer.toString(minsRemaining));
                countDownSecsTextView.setText(Integer.toString(secsRemaining));
            }
        }

        //The onFinish is called when the timer is completed and is done running. This method also
        //resets the timer view and it also sends out a push notification telling the user that they
        //have completed a study session. This method will also add points to the users high score
        @Override
        public void onFinish() {
            //finish();  // in CountdownActivity // worked with this

            countDownMinsTextView.setText("00");//resetting the timer
            countDownSecsTextView.setText("00");//resetting the timer

            //Making the notification
            notifi.setSmallIcon(R.mipmap.ic_mini_icon); //LOGO
            //notifi.setLargeIcon(R.mipmap.ic_launcher);
            notifi.setTicker("Session Completed!");
            notifi.setWhen(System.currentTimeMillis());
            notifi.setContentTitle("Session Completed!");
            notifi.setContentText(randomQuotes());
            notifi.setChannelId("timerCompleted");
            notifi.setSound(null); //Set Sound
            //when the notification is pressed, move to the main view
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            //Allows a foreign app broadcasts an Intent
            PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(),
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notifi.setContentIntent(pIntent);

            //Building the notification and sending it out
            NotificationManager notifiManager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel mChannel = new NotificationChannel("timerCompleted",
                    "channelNote", NotificationManager.IMPORTANCE_DEFAULT);

            notifiManager.createNotificationChannel(mChannel);
            notifiManager.notify(uniqueID, notifi.build());

            //Adding the score
            scoreManager(true, countDownTimeInMins);
        }
    }

    /**
     * Method is used to randomly display a quote after the user finishes the study session. This
     * method returns a string randomly represent a motivation quote
     * @return string represent a motivation quote
     */
    private String randomQuotes() {
        String quotes[] = new String[5];
        quotes[0] = "Yes! You did it, now try doubling your focus time!";
        quotes[1] = "Small goals do add up, you will complete your main goal!";
        quotes[2] = "Try doing one more session!";
        quotes[3] = "Never give up, you can do it!";
        quotes[4] = "The time you waste sitting around someone else is working to take ur job away.";
        Random rand = new Random();
        int  n = rand.nextInt(4);

        return quotes[n]; //returning a quote
    }
}
