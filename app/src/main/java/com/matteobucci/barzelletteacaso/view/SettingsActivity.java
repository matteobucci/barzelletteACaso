package com.matteobucci.barzelletteacaso.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.matteobucci.barzelletteacaso.DebugActivity;
import com.matteobucci.barzelletteacaso.R;
import com.matteobucci.barzelletteacaso.view.dialog.DialogSuggerimento;
import com.matteobucci.barzelletteacaso.view.support.SliderDialog;

public class SettingsActivity extends Activity {

    public static final String adultiString="adulti_checkbox";
    public static final String lungheString="lunghe_checkbox";
    public static final String swipeString="swipe_checkbox";
    public static final String sfondoChiaro="sfondo_chiaro_checkbox";
    public static final String isChangedString ="setting_changed";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Display the fragment as the main content.
            getFragmentManager().beginTransaction().replace(android.R.id.content,
                    new PrefsFragment()).commit();

        }

        public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

            private SliderDialog _seekBarPref;
            protected Activity mActivity;

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
                       // Intent testIntent = new Intent(getActivity().getApplicationContext(), DebugActivity.class);
                        startActivity(testIntent);

                        return true;
                    }
                });

                Preference commenta_pref = findPreference("commenta_pref");

                commenta_pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        (new DialogSuggerimento()).show(getFragmentManager(), "");
                        return false;
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

                // Get widgets :
                _seekBarPref = (SliderDialog) this.findPreference("SEEKBAR_VALUE");

                // Set listener :
                getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

                // Set seekbar summary :
                int radius = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("SEEKBAR_VALUE", 10)+10;
                _seekBarPref.setSummary(this.getString(R.string.settings_summary).replace("$1", ""+radius));

            }

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                // Set seekbar summary :
                if(isAdded()) {
                    int radius = PreferenceManager.getDefaultSharedPreferences(mActivity).getInt("SEEKBAR_VALUE", 10) + 10;
                    _seekBarPref.setSummary(this.getString(R.string.settings_summary).replace("$1", "" + radius));
                }

                if(key.equals(SettingsActivity.adultiString) || key.equals(SettingsActivity.lungheString)){
                    notifyChange();
                }



            }

            @Override
            public void onAttach(Activity activity) {
                super.onAttach(activity);
                mActivity = activity;
            }


            private void notifyChange(){
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mActivity).edit();
                editor.putBoolean(SettingsActivity.isChangedString , true);
                editor.apply();
            }

        }

}
