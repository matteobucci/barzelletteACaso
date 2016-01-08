package com.matteobucci.barzelletteacaso.view;

import com.matteobucci.barzelletteacaso.R;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;

/**
 * Created by Matti on 20/09/2015.
 */
public class Application  extends android.app.Application {

    // Debugging switch
    public static final boolean APPDEBUG = false;

    // Debugging tag for the application
    public static final String APPTAG = "BarzelletteACaso";

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(getApplicationContext(), getString(R.string.parse_code1), getString(R.string.parse_code2));
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }


}