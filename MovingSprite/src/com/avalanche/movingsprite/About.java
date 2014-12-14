package com.avalanche.movingsprite;

import android.app.Activity;
import android.os.Bundle;

public class About  extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(!SplashScreen.ourSong.isPlaying() && MainMenu.globalSound){
			SplashScreen.ourSong.start();
		}
	}
}
