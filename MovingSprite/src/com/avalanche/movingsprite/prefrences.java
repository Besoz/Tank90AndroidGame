package com.avalanche.movingsprite;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;

public class prefrences extends PreferenceActivity {

    CheckBoxPreference sound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);
		setContentView(R.layout.pref_layout);
		

		sound = (CheckBoxPreference) findPreference("sound_control");

		if (sound == null)
			Log.d("debug", "sound be null");
		sound.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				// TODO Auto-generated method stub

				if (sound.isChecked()) {
					if (!SplashScreen.ourSong.isPlaying()) {
						SplashScreen.ourSong.start();
					}
				} else {
					if (SplashScreen.ourSong.isPlaying()) {
						SplashScreen.ourSong.pause();
					}
				}
				return false;
			}
		});

	}

}
