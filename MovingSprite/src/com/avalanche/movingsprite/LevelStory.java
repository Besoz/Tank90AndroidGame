package com.avalanche.movingsprite;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class LevelStory extends Activity{

    int levelNum;
    Bitmap[] levelPic;
    Button startLevel;
	public SoundPool sp;
	int buttonSound;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.story);
		
		sp = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		buttonSound = sp.load(this, R.raw.button_33, 1);
		
		startLevel= (Button) findViewById(R.id.StartGame);
		ImageButton next = (ImageButton) findViewById(R.id.nextlevel);
		TextView story = (TextView) findViewById(R.id.textstory);
		
		levelPic = new Bitmap[10];
		
		levelPic[0] = BitmapFactory.decodeResource(getResources(),
				R.drawable.commander_wife);
		levelPic[1] = BitmapFactory.decodeResource(getResources(),
				R.drawable.commander);
		levelPic[2] = BitmapFactory.decodeResource(getResources(),
				R.drawable.base);
		levelPic[3] = BitmapFactory.decodeResource(getResources(),
				R.drawable.plane);
		levelPic[4] = BitmapFactory.decodeResource(getResources(),
				R.drawable.ship);
		levelPic[5] = BitmapFactory.decodeResource(getResources(),
				R.drawable.communication_tower);
		levelPic[6] = BitmapFactory.decodeResource(getResources(),
				R.drawable.bridge);
		levelPic[7] = BitmapFactory.decodeResource(getResources(),
				R.drawable.humvi);
		levelPic[8] = BitmapFactory.decodeResource(getResources(),
				R.drawable.gf_win);
		levelPic[9] = BitmapFactory.decodeResource(getResources(),
				R.drawable.eagle);
		
		levelNum = getIntent().getIntExtra("levelNum", 1);
//		levelPic[7] = Bitmap.createScaledBitmap(levelPic[7], 325 ,180, false);
		
		switch(levelNum-1)
		{
			case 0:
				next.setImageBitmap(levelPic[levelNum-1]);
				story.setText("\nSir our situation in this war is getting worse. Enemy made several attacks in many important areas of our army and took control over them. We need your help Sir! Yesterday at night the enemy attacked us and Kidnapped the Commander's Wife.They are keeping here in the low lands.You must save her Sir.\n");
				break;
			case 1:
				next.setImageBitmap(levelPic[levelNum-1]);
				story.setText("\nGreetings Sir for saving the Commander's Wife, you made a great work but it seems that it's not enough. Our Commander went to the low lands to save his wife thinking you were dead. According to our scouts the enemy caputred him and they are going to execute him by tomorrow. You must save him Sir, you are our last hope.\n");
				break;
			case 2:
				next.setImageBitmap(levelPic[levelNum-1]);
				story.setText("\nWell done Sir for saving our Commander. The War is not over yet.  We got news that the enemy is going to attack our base. We must stand together to defeat those bastards. Our Commander has ordered you to defend the Area 3, Be Careful Sir !\n");
				break;
			case 3:
				next.setImageBitmap(levelPic[levelNum-1]);
				story.setText("\nOur base is now safe. But there are many areas still under the control of our enemy. We must recapture them all. Start with the airport as it will reinforce our army greatly. Good luck Sir.\n");
				break;
			case 4:
				next.setImageBitmap(levelPic[levelNum-1]);
				story.setText("\nWelcome back Sir. you have done a great effort recapturing our airport, That will allow you to call airstrikes to help you destroy the nearby enemy tanks. Now we will strike them where it hurts most by capturing the harbour and cutting their supply lines. Best of luck Sir. \n");
				break;
			case 5:
				next.setImageBitmap(levelPic[levelNum-1]);
				story.setText("\nBad news Sir! while we were busy attacking their harbour they managed to capture out communication tower. We must retrieve that tower at any cost. You are the only one qualified for this mission Sir. Let's make them suffer!! \n");
				break;
			case 6:
				next.setImageBitmap(levelPic[levelNum-1]);
				story.setText("\nGreat work Sir our communications are back. We received a message from squad 4 on the other side of the bridge. They are being pinned down and need your support. You will have to cross the bridge to help them. Becarful the enemy overheared their meassage and are expecting you.\n");
				break;
			case 7:
				next.setImageBitmap(levelPic[levelNum-1]);
				story.setText("\nMayday....Mayday....we are squad 4. We are being hammered by enemy troops. Mayday....Mayday....anyone.... please save us!!!\n");
				break;
			case 8:
				next.setImageBitmap(levelPic[levelNum-1]);
				story.setText("\nWe salute you Sir, you have managed to save many lives that day. From now on you are a Battle Hero. Unfortunately the enemy want to put you down. they have kidnapped your girl friend!! Now is the time save her and make those bastards pay.\n");
				break;
			case 9:
				next.setImageBitmap(levelPic[levelNum-1]);
				story.setText("\nFinally we have gained the upper hand over this war. The only easy day was yesterday. We have discoverd the main base of our enemy. It's time to put an end to this long struggle. May the God be with us.\n");
				break;
		}
		
		next.setOnClickListener(new View.OnClickListener() {
    		
			@Override
			public void onClick(View v) 
            {
				if(MainMenu.globalSound){
					sp.play(buttonSound, 1, 1, 0, 0, 1);
				}
				Intent myIntent = new Intent("android.intent.action.GAME");
				myIntent.putExtra("levelNum",levelNum);
			    startActivity(myIntent);
                finish();
			}
		});
		
		startLevel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(MainMenu.globalSound){
					sp.play(buttonSound, 1, 1, 0, 0, 1);
				}
				Intent myIntent = new Intent("android.intent.action.GAME");
				myIntent.putExtra("levelNum",levelNum);
			    startActivity(myIntent);
                finish();
			}
		});

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
