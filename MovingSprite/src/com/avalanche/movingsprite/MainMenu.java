package com.avalanche.movingsprite;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainMenu extends Activity {
    Button start, options, help, about; 
        TextView battleCity;
	Bitmap tank1, tank2;
	public static boolean globalSound;
	
	public SoundPool sp;
	int buttonSound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_gfx);
		super.onCreate(savedInstanceState);
		
		
		sp = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		buttonSound = sp.load(this, R.raw.button_33, 1);
		
		start = (Button) findViewById(R.id.play);
		options = (Button) findViewById(R.id.button2);
		about = (Button) findViewById(R.id.button4);
		help = (Button) findViewById(R.id.button3);
		battleCity = (TextView) findViewById(R.id.textView1);
		
		
		final Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/stencil.ttf");
		
		start.setTypeface(tf);
		options.setTypeface(tf);
		about.setTypeface(tf);
		help.setTypeface(tf);
		
		battleCity.setTypeface(tf);
		battleCity.setTextColor(Color.WHITE);
		
		start.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(globalSound){
					sp.play(buttonSound, 1, 1, 0, 0, 1);
				}
				Intent game = new Intent("android.intent.action.LEVELCHOICE");
				startActivity(game);
			}
		});
		options.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(globalSound){
					sp.play(buttonSound, 1, 1, 0, 0, 1);
				}
				Intent optionMenu = new Intent("android.intent.action.PREFERENCES");
				startActivity(optionMenu);
			}
		});
		
		about.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(globalSound){
					sp.play(buttonSound, 1, 1, 0, 0, 1);
				}
				Intent aboutScreen = new Intent("android.intent.action.ABOUTBATTLECITY");
				startActivity(aboutScreen);
			}
		});
		
		help.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(globalSound){
					sp.play(buttonSound, 1, 1, 0, 0, 1);
				}
				Intent helpScreen = new Intent("android.intent.action.HELPBATTLECITY");
				startActivity(helpScreen);
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		//SplashScreen.ourSong.release();
		

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SplashScreen.ourSong.release();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(SplashScreen.ourSong.isPlaying()){
			SplashScreen.ourSong.pause();
		}
	}
	
	@Override
	protected void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();
		
		 SharedPreferences getData = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			globalSound=getData.getBoolean("sound_control", true);
			
			
		if(!SplashScreen.ourSong.isPlaying() && MainMenu.globalSound){
			Log.d("debug","before");
			SplashScreen.ourSong.start();
			Log.d("debug","after");
		}
	}
}
