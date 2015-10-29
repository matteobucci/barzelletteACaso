package com.matteobucci.barzelletteacaso.view;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;

/**
 * Created by Matti on 20/09/2015.
 */
public class Application  extends android.app.Application {
    // Debugging switch
    public static final boolean APPDEBUG = true;

    // Debugging tag for the application
    public static final String APPTAG = "BarzelletteACaso";

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
     //   Parse.enableLocalDatastore(this);

        Parse.initialize(getApplicationContext(), "bOaWgp9vs9LqO1EieOBHwo6wKSHKO01WNUvgLsRW", "1AahIK8ikqCBmfQvouxyfzQrOeU05mu4eGWfkI0M");



        ParseInstallation.getCurrentInstallation().saveInBackground();



    }


}