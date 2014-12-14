package com.avalanche.movingsprite;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LevelChoice extends Activity {

        Button[] buttonsArray;
    String[] buttonsTexts;
	public static String fileName = "myData"; // file to put data
	SharedPreferences toSave;
	int levelAchieved;
	TextView text;

	public SoundPool sp;
	int buttonSound;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_level_choice);

		sp = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		buttonSound = sp.load(this, R.raw.button_33, 1);
		
		// startGame =(Button) findViewById(R.id.play);.
		buttonsArray = new Button[10];
		buttonsTexts = new String[10];
		Button button1 = (Button) findViewById(R.id.level1);
		Button button2 = (Button) findViewById(R.id.level2);
		Button button3 = (Button) findViewById(R.id.level3);
		Button button4 = (Button) findViewById(R.id.level4);
		Button button5 = (Button) findViewById(R.id.level5);
		Button button6 = (Button) findViewById(R.id.level6);
		Button button7 = (Button) findViewById(R.id.level7);
		Button button8 = (Button) findViewById(R.id.level8);
		Button button9 = (Button) findViewById(R.id.level9);
		Button button10 = (Button) findViewById(R.id.level10);
		buttonsArray[0] = button1;
		buttonsArray[1] = button2;
		buttonsArray[2] = button3;
		buttonsArray[3] = button4;
		buttonsArray[4] = button5;
		buttonsArray[5] = button6;
		buttonsArray[6] = button7;
		buttonsArray[7] = button8;
		buttonsArray[8] = button9;
		buttonsArray[9] = button10;

		buttonsTexts[0] = "1-Rescue Commander's wife";
		buttonsTexts[1] = ("2-Commander revenge");
		buttonsTexts[2] = ("3-Defending the base");
		buttonsTexts[3] = ("4-Securing the airport");
		buttonsTexts[4] = ("5-Capturing enemy harbour");
		buttonsTexts[5] = ("6-Saving communication tower");
		buttonsTexts[6] = ("7-Crossing the bridge");
		buttonsTexts[7] = ("8-Defending friendly troops");
		buttonsTexts[8] = ("9-Oh! my Girl Friend bastards");
		buttonsTexts[9] = ("10-Taking revenge");
		
		final Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/stencil.ttf");
//		text=(TextView) findViewById(R.id.choose);
//		text.setTypeface(tf);

		toSave = getSharedPreferences(fileName, 0);
		// ////////////////////////////////////////////
	
		SharedPreferences.Editor editor = toSave.edit();
		editor.putInt("levelNumber", 9);
		editor.commit();
		levelAchieved = 9;
		
		// ///////////////////////////////////////////////

		// ////////////////////////////////

		for (int i = 0; i < buttonsArray.length; i++) {

			final Button theButton = buttonsArray[i];
			final int itr = i;
			theButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (levelAchieved >= itr + 1) {
						if(MainMenu.globalSound){
							sp.play(buttonSound, 1, 1, 0, 0, 1);
						}
						Intent myIntent = new Intent("android.intent.action.STORY");
						myIntent.putExtra("levelNum", itr + 1);
						startActivity(myIntent);
					}
				}
			});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(!SplashScreen.ourSong.isPlaying() && MainMenu.globalSound){
			SplashScreen.ourSong.start();
		}
		toSave = getSharedPreferences(fileName, 0);
		levelAchieved = toSave.getInt("levelNumber", 1);
		//Log.d("debug", "levelAchieved onResume " + levelAchieved);

		for (int i = 0; i < buttonsArray.length; i++) {

			if (levelAchieved >= i+1) {
				buttonsArray[i].setClickable(true);
				buttonsArray[i].setText(buttonsTexts[i]);
			} else {
				buttonsArray[i].setClickable(false);
				buttonsArray[i].setText("Locked");
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		SharedPreferences.Editor editor = toSave.edit();
		editor.putInt("levelNumber", levelAchieved);
		editor.commit();
		if(SplashScreen.ourSong.isPlaying()){
			SplashScreen.ourSong.pause();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.level_choice, menu);
		return true;
	}

}

/*
 * Button startGame, button1, button2, button3, button4, button5, button6,
 * button7, button8, button9, button10;
 */
/*
 * button1.setOnClickListener(new View.OnClickListener() {
 * 
 * @Override public void onClick(View v) { // TODO Auto-generated method
 * stub Intent myIntent = new Intent("android.intent.action.GAME"); if
 * (levelAchieved >= 1) { button1.setClickable(true);
 * myIntent.putExtra("levelNum", 1); startActivity(myIntent); } else {
 * button1.setClickable(false); } } }); button2.setOnClickListener(new
 * View.OnClickListener() {
 * 
 * @Override public void onClick(View v) { // TODO Auto-generated method
 * stub Intent myIntent = new Intent("android.intent.action.GAME"); if
 * (levelAchieved >= 2) { button2.setClickable(true);
 * myIntent.putExtra("levelNum", 2); startActivity(myIntent); } } });
 * 
 * button3.setOnClickListener(new View.OnClickListener() {
 * 
 * @Override public void onClick(View v) { // TODO Auto-generated method
 * stub Log.d("debug", "levelAchieved" + levelAchieved); Intent myIntent
 * = new Intent("android.intent.action.GAME"); if (levelAchieved >= 3) {
 * myIntent.putExtra("levelNum", 3); startActivity(myIntent); } } });
 * button4.setOnClickListener(new View.OnClickListener() {
 * 
 * @Override public void onClick(View v) { // TODO Auto-generated method
 * stub Intent myIntent = new Intent("android.intent.action.GAME"); if
 * (levelAchieved >= 4) { myIntent.putExtra("levelNum", 4);
 * startActivity(myIntent); } } }); button5.setOnClickListener(new
 * View.OnClickListener() {
 * 
 * @Override public void onClick(View v) { // TODO Auto-generated method
 * stub if (levelAchieved >= 5) { Intent myIntent = new
 * Intent("android.intent.action.GAME"); myIntent.putExtra("levelNum",
 * 5); startActivity(myIntent); } } }); button6.setOnClickListener(new
 * View.OnClickListener() {
 * 
 * @Override public void onClick(View v) { // TODO Auto-generated method
 * stub if (levelAchieved >= 6) { Intent myIntent = new
 * Intent("android.intent.action.GAME"); myIntent.putExtra("levelNum",
 * 6); startActivity(myIntent); } } }); button7.setOnClickListener(new
 * View.OnClickListener() {
 * 
 * @Override public void onClick(View v) { // TODO Auto-generated method
 * stub if (levelAchieved >= 7) { Intent myIntent = new
 * Intent("android.intent.action.GAME"); myIntent.putExtra("levelNum",
 * 7); startActivity(myIntent); } } }); button8.setOnClickListener(new
 * View.OnClickListener() {
 * 
 * @Override public void onClick(View v) { // TODO Auto-generated method
 * stub if (levelAchieved >= 8) { Intent myIntent = new
 * Intent("android.intent.action.GAME"); myIntent.putExtra("levelNum",
 * 8); startActivity(myIntent); } } }); button9.setOnClickListener(new
 * View.OnClickListener() {
 * 
 * @Override public void onClick(View v) { // TODO Auto-generated method
 * stub if (levelAchieved >= 9) { Intent myIntent = new
 * Intent("android.intent.action.GAME"); myIntent.putExtra("levelNum",
 * 9); startActivity(myIntent); } } }); button10.setOnClickListener(new
 * View.OnClickListener() {
 * 
 * @Override public void onClick(View v) { // TODO Auto-generated method
 * stub if (levelAchieved >= 10) { Intent myIntent = new
 * Intent("android.intent.action.GAME"); myIntent.putExtra("levelNum",
 * 10); startActivity(myIntent); } } });
 */


/*
if (levelAchieved >= 1) {
	button1.setClickable(true);
} else {
	button1.setClickable(false);
	button1.setText("Locked");
}

if (levelAchieved >= 2) {
	button2.setClickable(true);
	button2.setText("2-Commander revenge");
} else {
	button2.setClickable(false);
	button2.setText("Locked");
}

if (levelAchieved >= 3) {
	button3.setClickable(true);
	button3.setText("3-Defending the base");
} else {
	button3.setClickable(false);
	button3.setText("Locked");
}

if (levelAchieved >= 4) {
	button4.setClickable(true);
	button4.setText("4-Securing the airport");
} else {
	button4.setClickable(false);
	button4.setText("Locked");
}

if (levelAchieved >= 5) {
	button5.setClickable(true);
	button5.setText("5-Recapture the harbour");
} else {
	button5.setClickable(false);
	button5.setText("Locked");
}

if (levelAchieved >= 6) {
	button6.setClickable(true);
	button6.setText("6- Saving communication tower");
} else {
	button6.setClickable(false);
	button6.setText("Locked");
}

if (levelAchieved >= 7) {
	button7.setClickable(true);
	button7.setText("7-Crosing the bridge");
} else {
	button7.setClickable(false);
	button7.setText("Locked");
}

if (levelAchieved >= 8) {
	button8.setClickable(true);
	button8.setText("8-Defending friendly troops");
} else {
	button8.setClickable(false);
	button8.setText("Locked");
}

if (levelAchieved >= 9) {
	button9.setClickable(true);
	button9.setText("9-oohh my Girl Friend bastardssss");
} else {
	button9.setClickable(false);
	button9.setText("Locked");
}
if (levelAchieved >= 10) {
	button10.setClickable(true);
	button10.setText("10-Now let&apos;s renvenge");
} else {
	button10.setClickable(false);
	button10.setText("Locked");
}
}
*/