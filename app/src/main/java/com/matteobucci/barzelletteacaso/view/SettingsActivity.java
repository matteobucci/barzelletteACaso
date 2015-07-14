package com.matteobucci.barzelletteacaso.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;

import com.matteobucci.barzelletteacaso.R;

import java.util.List;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends Activity {

    public static final String adultiString="adulti_checkbox";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Display the fragment as the main content.
            getFragmentManager().beginTransaction().replace(android.R.id.content,
                    new PrefsFragment()).commit();

        }

        public static class PrefsFragment extends PreferenceFragment {

            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                // Load the preferences from an XML resource
                addPreferencesFromResource(R.xml.pref_general);


                Preference infoPref = findPreference("info_pref");
                infoPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference preference) {

                        Intent testIntent = new Intent(getActivity().getApplicationContext(), AboutActivity.class);
                        startActivity(testIntent);

                        return true;
                    }
                });

                Preference valutaPref = findPreference("valuta_pref");
                valutaPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference preference) {

                        String url = getResources().getString(R.string.url_app_playstore);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        return true;
                    }
                });
            }
        }


}
