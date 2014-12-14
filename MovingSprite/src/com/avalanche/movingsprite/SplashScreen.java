package com.avalanche.movingsprite;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class SplashScreen extends Activity {
	static MediaPlayer ourSong;
	SharedPreferences saving;
	public static boolean globalSound;
	boolean intentCalled;
	Thread timer ;
	@Override
	protected void onCreate(Bundle makingBackgroud) {
		// TODO Auto-generated method stub
		super.onCreate(makingBackgroud);

		intentCalled = false;

		SharedPreferences getData = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		globalSound = getData.getBoolean("sound_control", true);

		setContentView(R.layout.splash_layout);

		saving = getSharedPreferences("splash_sound", 0);
		ourSong = MediaPlayer.create(SplashScreen.this, R.raw.remember);
		saving = getSharedPreferences("splash_sound", 0);

		if (saving.getBoolean("sound", true) == true) {
			if (globalSound)
				ourSong.start();
			else {
				ourSong.start();
				ourSong.pause();
			}
		}

		ourSong.setLooping(true);
		timer = new Thread() {
			public void run() {
				try {
					sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					if (!SplashScreen.this.isFinishing()) {
						intentCalled = true;
						Intent openMainActivity = new Intent("com.amr.movingsprite.MAINMENU");
						startActivity(openMainActivity);

					}
				}
			}
		};
		timer.start();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("debug", "on pause called in splash");

		if (intentCalled) {
			
			finish();
		} else {
			if (ourSong.isPlaying()) {
				intentCalled=false; // just being sure
				finish();
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("debug", "on resume called in splash");
		
		if (globalSound) {
			if (!ourSong.isPlaying()) {
				ourSong.start();
			}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		//ourSong.release();
		finish();

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("debug", "on destroy called in splash");
		if(!intentCalled){
			ourSong.release();
		}
		
	}
}
