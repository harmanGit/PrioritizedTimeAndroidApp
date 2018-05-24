package com.cs345.harmanjeetdhillon.prioritizedtime;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class is linked to the app_selecter view. This class allows users to select what apps they want
 * to whitelist which allows the timer to run while these apps are running.
 *
 * @author harmanjeet dhillon
 */

public class AppSelecter extends AppCompatActivity {
    private final String TAG = "APPSELECTER";
    //private MediaPlayer mp; //button Tap Sound media player
    private LinearLayout linearLayout; // contents the apps the user can select
    // all apps the user whitelisted, used to auto toggle already whitelisted apps
    // when the switch is created
    private Set<String> whitelistedApps;//list of whitelisted apps from the database
    private int totalNumberOfApps; //total app count of apps installed on the device
    DatabaseAid myDB; //the database that stores all app info

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_selecter);
        whitelistedApps = new TreeSet<String>();//list of whitelisted apps from the database
        //mp = MediaPlayer.create(this, R.raw.button_tap);//adding the button tap sound
        myDB = new DatabaseAid(this); //obj of the database
        linearLayout = (LinearLayout) findViewById(R.id.appListLinearLayout);
        totalNumberOfApps = 0;
        findAllApps(); //running the method to find all installed apps
    }

    /**
     * This method will go through the phone and find all installed apps, by checking to see if
     * the installed apps are launchable or not
     */
    private void findAllApps() {
        int count = 0;

        //packagemanager manages what apps are installed, removed and updated
        PackageManager packageManager = getPackageManager();
        //creating a list of all apps on the phone
        List<ApplicationInfo> apps = packageManager.getInstalledApplications
                (PackageManager.GET_META_DATA);

        for (ApplicationInfo app : apps) {
            //getting the app name
            String appName = (String) packageManager.getApplicationLabel(app);
            //checking to see if the user can launch the app
            //Danny K idea borrowed to check and see if apps are launchable
            if (packageManager.getLaunchIntentForPackage(app.packageName) != null &&
                    !appName.contains("com.android.")) {
                createSwitch(appName, count); //creating the switch

                Log.d(TAG, "AppsOnDevice: " + appName);
                count++;
            }
        }
        totalNumberOfApps = count;
        Log.d(TAG, "totalApps : " + count);

        //Creating the list of apps that already whitelisted in the database
        whitelistedApps = myDB.isAppWhitelist();
    }

    /**
     * Method is used to check and see if the app is already whitelisted.
     *
     * @param appName string representing the app name
     * @return boolean if the that app is already in the app store, else false
     */
    private boolean isAppWhitelisted(String appName) {
        for (String name : whitelistedApps) {
            Log.d(TAG, "added APPS : " + whitelistedApps.size());
            //comparing all the app name to whitelistapps set which contains all whitelisted apps
            if (name.equals(appName))
                return true;
        }
        return false;
    }

    /**
     * Method takes a name and id to create a switch will represent all the apps on the phone.
     * The method also contents the listener
     *
     * THIS METHOD MAKES CALLS TO THE DATABASE AND I SUSPECT IT ALSO BREAKS THE APP
     *
     * @param nameToSet string app name use to set the switch text
     * @param id        int used to set the unique switch id
     */
    private void createSwitch(final String nameToSet, int id) {
        Switch appSwitch = new Switch(this); //creating the switch
        appSwitch.setId(id); // setting the id
        appSwitch.setText(nameToSet); //setting the name

        //Listener for the switch
        appSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //mp.start();//playing button tap sound
                Context context = getApplicationContext();
                CharSequence text = "This feature will be coming soon!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();



                 /**

                  THIS CODE IS BLOCKED OFF AS IT SEEMS TO BREAK THE APP RANDOMLY.

                  I SUSPECT THE MAIN ISSUE LIES HERE...

                //checking to see if the switch was already toggled
                if (isAppWhitelisted(nameToSet)) { //checking to see if the app is already whitelisted
                    //if the switch was toggled that means the user already had whitelisted the app

                    //THIS FEATURE IS CAUSES THE SYSTEM TO CRASH, ISSUE UNKNOWN
                    //myDB.deleteApp(nameToSet); //deleting app from the database if pressed
                    Log.d(TAG, "Not Added To DATABASE: " + nameToSet);
                } else {
                    //if the app was not already toggle that means the app does not exist in the
                    //whitelist database
                    //THIS FEATURE IS CAUSES THE SYSTEM TO CRASH, ISSUE UNKNOWN
                    myDB.insertApp(nameToSet); //adding the app to the database
                    Log.d(TAG, "Added To DATABASE: " + nameToSet);
                }
                  */

            }
        });
        //adding the add to linearLayout
        appSwitch.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(appSwitch);
    }

    /**
     * Method is used as a onClick for the save button which just moves the view to the
     * main activity
     *
     * @param view this view
     */
    public void savePressed(View view) {
        //mp.start();//playing button tap sound
        //going to the main view with a intent
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

}
