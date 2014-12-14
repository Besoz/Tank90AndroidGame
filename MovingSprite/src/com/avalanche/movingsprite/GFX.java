package com.avalanche.movingsprite;
 
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
 
public class GFX extends Activity implements OnTouchListener {
 
	GFXsurface mySurface; // object from the class GFXsurface (down)
	float touchedX;
	float touchedY;
	SparseArray<PointF> mActivePointers;
	int levelNumber;
	public static String fileName = "myData"; // file to put data
	SharedPreferences toSave,toSave2,toSave3;
	boolean canIncrementLevel;
	boolean canPassNextLevel;
	public static boolean  globalSound;
	int levelAchieved;
	
	 protected PowerManager.WakeLock mWakeLock;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		levelNumber = getIntent().getIntExtra("levelNum", 1);
		mySurface = new GFXsurface(this, levelNumber);
		mySurface.setOnTouchListener(this);
		mActivePointers = new SparseArray<PointF>();	
		canIncrementLevel = true;
		canPassNextLevel = false;
		SharedPreferences getData = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		globalSound = getData.getBoolean("sound_control", true);
		toSave3 = getSharedPreferences(fileName, 0);
		levelAchieved = toSave3.getInt("levelNumber", 1);
		
		setContentView(mySurface);
		
		
	}
 
	@Override
	public boolean onTouch(View v, MotionEvent event) {
 
		// get pointer index from the event object
		int pointerIndex = event.getActionIndex();
 
		// get pointer ID
		int pointerId = event.getPointerId(pointerIndex);
 
		// get masked (not specific to a pointer) action
		int maskedAction = event.getActionMasked();
 
		switch (maskedAction) {
 
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN: {
			// We have a new pointer. Lets add it to the list of pointers
			PointF f = new PointF();
			f.x = event.getX(pointerIndex);
			f.y = event.getY(pointerIndex);
			
			if (canPassNextLevel) {
				openOptionsMenu();
			}
 
			mActivePointers.put(pointerId, f);
			break;
		}
 
		case MotionEvent.ACTION_MOVE: { // a pointer was moved
			for (int size = event.getPointerCount(), i = 0; i < size; i++) {
				PointF point = mActivePointers.get(event.getPointerId(i));
				if (point != null) {
					point.x = event.getX(i);
					point.y = event.getY(i);
				}
			}
			break;
		}
 
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_CANCEL: {
			mActivePointers.remove(pointerId);
			break;
		}
		}
 
		return true;
	}
 
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		SplashScreen.ourSong.setVolume((float)0.2,(float) 0.2);
		
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();
        
		Log.d("debug", "onResume called");
		 SharedPreferences getData = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			globalSound=getData.getBoolean("sound_control", true);
		if(!SplashScreen.ourSong.isPlaying() && globalSound){
			SplashScreen.ourSong.start();
		}
		mySurface.resume();
	}
 
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SplashScreen.ourSong.setVolume((float)0.5,(float) 0.5);
		this.mWakeLock.release();
		Log.d("debug", "onPause called");
		if(SplashScreen.ourSong.isPlaying()){
			SplashScreen.ourSong.pause();
		}
		mySurface.pause();
		
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("debug", "onStop called");
		//onPause();
	}
 
	// ///////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////
	public class GFXsurface extends SurfaceView implements Runnable {
		SurfaceHolder holder;
		Thread thread = null;
		boolean isRunning = false;
		Bitmap right, left, up, down, u, d, l, r, fire, bLeft, enemyCounter;
		Bitmap bRight;
		Bitmap bUp;
		Bitmap bDown, smallExplode, mediumExplode, largeExplode, missionFailed;
		Bitmap arrows;
		Bitmap[] levelVictory;
		Bitmap targetAlive, targetDestroyed;
		Bitmap[] bonus;
		tankSprite mytank, enemyTank;
		Paint paint, textPaint, healthPaint, horizontalLine, whiteText,shieldPaint;
		int tileDimensions;
		boolean initialize;
		int lives;
		Map myMap;
		Random randomMoves;
		ArrayList<tankSprite> tanksArray;
		int tankInMap, levelTanks, level, targetDimension;
		int numWave1, numWave2, numWave3;
		float healthFactor;
		public SoundPool sp;
		int shoot, winSound, loseSound, explodeSound,planeSound , bombFallSound , PlaneExplodeSound;
		boolean winBool, loseBool;
		boolean playing;
		Bitmap missionAcomplished;
		int[][] levelBonus;
		int bonusIndex;
		int shiledPower;
		public MediaPlayer tankMoveSound;
		SharedPreferences savedSound;
		public boolean gameOver;
		int destroyedTanks,planeUpMotion,bombScale;
		long startTime;
		Bitmap planeStrike , missile;
		int[][] explosionPositions;
		boolean firstBonus, secondBonus, thirdBonus, buildShield , airStrike;
 
		public GFXsurface(Context context, int lvlNum) {
			super(context);
			holder = getHolder();
			sp = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
			shoot = sp.load(context, R.raw.shot, 1);
			loseSound = sp.load(context, R.raw.cod4_lose, 1);
			explodeSound = sp.load(context, R.raw.explosion_04, 1);
			winSound = sp.load(context, R.raw.win_cod4, 1);
			planeSound = sp.load(context, R.raw.airplane_sound, 1);
			bombFallSound = sp.load(context, R.raw.bomb_fall, 1);
			PlaneExplodeSound = sp.load(context, R.raw.plane_bomb, 1);
 
			levelBonus = new int[3][4];
 
			savedSound = getSharedPreferences("game_sound", 0);
			tankMoveSound = MediaPlayer.create(context, R.raw.tank_move);
			savedSound = getSharedPreferences("game_sound", 0);
 
			if (savedSound.getBoolean("sound", true) == true) {
				tankMoveSound.start();
				tankMoveSound.pause();
			}
 
			tankMoveSound.setLooping(true);
 
			tankInMap = 0;
			right = BitmapFactory.decodeResource(getResources(),R.drawable.tank_right);
			down = BitmapFactory.decodeResource(getResources(),R.drawable.tank_down);
			left = BitmapFactory.decodeResource(getResources(),R.drawable.tank_left);
			up = BitmapFactory.decodeResource(getResources(),R.drawable.tank_up);
			level = lvlNum;
			l = BitmapFactory.decodeResource(getResources(), R.drawable.l);
			arrows = BitmapFactory.decodeResource(getResources(),R.drawable.arrows);
 
			bLeft = BitmapFactory.decodeResource(getResources(),R.drawable.bullet_l);
			bRight = BitmapFactory.decodeResource(getResources(),R.drawable.bullet_r);
			bUp = BitmapFactory.decodeResource(getResources(),R.drawable.bullet_u);
			bDown = BitmapFactory.decodeResource(getResources(),R.drawable.bullet_d);
			enemyCounter = BitmapFactory.decodeResource(getResources(),R.drawable.enemy_counter);
			missionAcomplished = BitmapFactory.decodeResource(getResources(),R.drawable.misssion_accomp);
 
			fire = BitmapFactory.decodeResource(getResources(), R.drawable.fire);
 
			smallExplode = BitmapFactory.decodeResource(getResources(),R.drawable.small_explode);
			mediumExplode = BitmapFactory.decodeResource(getResources(),R.drawable.medium_explode);
			largeExplode = BitmapFactory.decodeResource(getResources(),R.drawable.large_explode);
 
			bonus = new Bitmap[5];
			// buildShield
			bonus[0] = BitmapFactory.decodeResource(getResources(),R.drawable.build_shield);
			// extraLive
			bonus[1] = BitmapFactory.decodeResource(getResources(),R.drawable.extra_live);
			// extraShoot
			bonus[2] = BitmapFactory.decodeResource(getResources(),R.drawable.extra_shoot);
			// shield
			bonus[3] = BitmapFactory.decodeResource(getResources(),R.drawable.shield);
			// air strike
			bonus[4] = BitmapFactory.decodeResource(getResources(),R.drawable.air_strike);
 
			//victory image
			levelVictory = new Bitmap[10];
			levelVictory[0] = BitmapFactory.decodeResource(getResources(),R.drawable.commander_wife);
			levelVictory[1] = BitmapFactory.decodeResource(getResources(),R.drawable.commander);
			levelVictory[2] = BitmapFactory.decodeResource(getResources(),R.drawable.base);
			levelVictory[3] = BitmapFactory.decodeResource(getResources(),R.drawable.plane);
			planeStrike = levelVictory[3];
			levelVictory[4] = BitmapFactory.decodeResource(getResources(),R.drawable.ship);
			levelVictory[5] = BitmapFactory.decodeResource(getResources(),R.drawable.communication_tower);
			levelVictory[6] = BitmapFactory.decodeResource(getResources(),R.drawable.bridge);
			levelVictory[7] = BitmapFactory.decodeResource(getResources(),R.drawable.humvi);
			levelVictory[8] = BitmapFactory.decodeResource(getResources(),R.drawable.gf_win);
			levelVictory[9] = BitmapFactory.decodeResource(getResources(),R.drawable.eagle);
 
			missile = BitmapFactory.decodeResource(getResources(),R.drawable.missile);
 
			targetAlive = BitmapFactory.decodeResource(getResources(),R.drawable.target_alive);
			targetDestroyed = BitmapFactory.decodeResource(getResources(),R.drawable.target_destroyed);
 
			missionFailed = BitmapFactory.decodeResource(getResources(),R.drawable.failed);
			paint = new Paint();
			paint.setColor(Color.GRAY);
			paint.setStrokeWidth(3);
 
			healthPaint = new Paint();
			healthPaint.setARGB(100, 34, 139, 34);
			healthPaint.setStrokeWidth(8);
 
			shieldPaint = new Paint();
			shieldPaint.setARGB(100, 74, 112, 139);
			shieldPaint.setStrokeWidth(8);
 
			whiteText = new Paint();
			whiteText.setColor(Color.WHITE);
			whiteText.setTextSize(20);
			whiteText.setTypeface(Typeface.DEFAULT_BOLD);
 
			horizontalLine = new Paint();
			horizontalLine.setColor(Color.BLACK);
			horizontalLine.setStrokeWidth(2);
 
			textPaint = new Paint();
			textPaint.setColor(Color.BLACK);
			textPaint.setTypeface(Typeface.DEFAULT_BOLD);
 
			initialize = false;
			randomMoves = new Random();
			tanksArray = new ArrayList<tankSprite>();
		}
 
		public void initialize() {
			playing = true;
			winBool = true;
			loseBool = true;
			gameOver = false;
 
			firstBonus = true;
			secondBonus = true;
			thirdBonus = false;
 
			buildShield = false;
			airStrike = false;
 
			tileDimensions = this.getHeight() / 26;
			bonusIndex = -1;
 
			numWave3 = 20;
			numWave1 = (20 - 2 * level) / 2;
			numWave2 = 20 - 2 * level;
 
			lives = 2; // maximum 6
			levelTanks = 0;//0; // to be reviewed
			destroyedTanks = 5;
			startTime = 0;
 
			l = Bitmap.createScaledBitmap(l, tileDimensions * 4,tileDimensions * 4, false); // use 4* tileDimensions
 
			enemyCounter = Bitmap.createScaledBitmap(enemyCounter,tileDimensions, tileDimensions, false);
 
			for (int i = 0; i < 5; i++) 
			{
				bonus[i] = Bitmap.createScaledBitmap(bonus[i],tileDimensions * 2, tileDimensions * 2, false);
			}
 
			targetDimension = targetAlive.getWidth();
			mytank = new tankSprite(GFXsurface.this, right, down, left, up, 0,tileDimensions, 19 * tileDimensions, 24 * tileDimensions,3, true, tanksArray.size()); // not
			tanksArray.add(mytank);
			// sure
			bLeft = Bitmap.createScaledBitmap(bLeft, (tileDimensions * 2) / 6,(tileDimensions * 2) / 6, false);
			bRight = Bitmap.createScaledBitmap(bRight,(tileDimensions * 2) / 6, (tileDimensions * 2) / 6, false);
			bUp = Bitmap.createScaledBitmap(bUp, (tileDimensions * 2) / 6,(tileDimensions * 2) / 6, false);
			bDown = Bitmap.createScaledBitmap(bDown, (tileDimensions * 2) / 6,(tileDimensions * 2) / 6, false); // must=
			
			planeStrike =  Bitmap.createScaledBitmap(planeStrike, 10*tileDimensions,16*tileDimensions, false);
			
			textPaint.setTextSize(this.getHeight() - 26 * tileDimensions- tileDimensions / 4); // reviewd
 
			smallExplode = Bitmap.createScaledBitmap(smallExplode,tileDimensions / 2, tileDimensions / 2, false);
			mediumExplode = Bitmap.createScaledBitmap(mediumExplode,tileDimensions, tileDimensions, false);
			largeExplode = Bitmap.createScaledBitmap(largeExplode,2 * tileDimensions, 2 * tileDimensions, false);
 
			try {
				myMap = new Map(level, tileDimensions, GFX.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
 
			levelBonus[0][3] = -2;
			levelBonus[1][3] = -2;
			levelBonus[2][3] = -2;
 
			// health factor indecate the number of health squares
			healthFactor = ((l.getWidth() * 3 - tileDimensions) - tileDimensions)/ mytank.health;
 
			toSave = getSharedPreferences(fileName, 0);
			toSave2 = getSharedPreferences(fileName, 0);
			initialize = true;
		}
 
		public void createWave() {
			int xTank = 12 * tileDimensions + randomMoves.nextInt(3) * 12* tileDimensions;// x
			if (tankInMap < 5 && levelTanks < 20) 
			{
				if (levelTanks < numWave1) 
				{
					// create easy tank red 1 yellow 2
					enemyTank = new tankSprite(GFXsurface.this, right, down,left, up, randomMoves.nextInt(2) + 1,tileDimensions, xTank, 0, 3, false,tanksArray.size());tanksArray.add(enemyTank);
					tankInMap++;
					levelTanks++;
					destroyedTanks--;
				}
				else if (levelTanks < numWave2) 
				{
					// create medium tank purple 4 cyan 5
					enemyTank = new tankSprite(GFXsurface.this, right, down,
							left, up, randomMoves.nextInt(2) + 4,
							tileDimensions, xTank, 0, 3, false,
							tanksArray.size());
					tanksArray.add(enemyTank);
					tankInMap++;
					levelTanks++;
					destroyedTanks--;
				}
				else if (levelTanks < numWave3) 
				{
					// create hard tank orange 6 pink 7
					enemyTank = new tankSprite(GFXsurface.this, right, down,left, up, randomMoves.nextInt(2) + 6,tileDimensions, xTank, 0, 3, false,tanksArray.size());
					tanksArray.add(enemyTank);
					tankInMap++;
					levelTanks++;
					destroyedTanks--;
				}
			} 
			else if (tankInMap == 0 && levelTanks == 20) {
				enemyTank = new tankSprite(GFXsurface.this, right, down, left,up, 3, tileDimensions, 24 * tileDimensions, 0, 3,false, tanksArray.size());
				tanksArray.add(enemyTank);
				tankInMap++;
				levelTanks++;
				thirdBonus = true;
				destroyedTanks = 0;
			}
 
		}
 
		@Override
		public void run() {
			while (!holder.getSurface().isValid());
 
			if (!initialize)	initialize();
 
			while (isRunning) {
				if (!holder.getSurface().isValid())
					continue;
 
				Canvas canvas = holder.lockCanvas();
 
				draw(canvas);
				holder.unlockCanvasAndPost(canvas);
 
				try {
					Thread.sleep(50);
				} // slow the tank
				catch (InterruptedException e) {
					e.printStackTrace();
				}
 
			}
		}
 
		public boolean isTouched(int button, Canvas canvas) {
 
			// up >>3 left >>2 down >> 1 right >>0 fire >> 4
 
			switch (button) {
 
			case 4: // fire
				for (int size = mActivePointers.size(), i = 0; i < size; i++) {
					PointF point = mActivePointers.valueAt(i);
					if (point != null)
						if ((point.x > (38 * tileDimensions)+ ((canvas.getWidth() - (38 * tileDimensions)) / 2)- (fire.getWidth() / 2)&& point.x < (38 * tileDimensions)+ ((canvas.getWidth() - (38 * tileDimensions)) / 2)+ (fire.getWidth() / 2)&& point.y > canvas.getHeight() - 2* l.getHeight() && point.y < canvas.getHeight()- 2* l.getHeight()+ fire.getHeight())) {
							return true;
						}
				}
				return false;
			case 3: // up
				for (int size = mActivePointers.size(), i = 0; i < size; i++) 
				{
					PointF point = mActivePointers.valueAt(i);
					if (point != null)
						if (((point.x > l.getWidth() && point.x < l.getWidth()+ l.getWidth()) && (point.y > canvas.getHeight() - 3 * l.getHeight() && point.y < canvas.getHeight() - 2 * l.getHeight()))) 
						{
							return true;
						}
				}
				return false;
			case 2: // left
				for (int size = mActivePointers.size(), i = 0; i < size; i++) {
					PointF point = mActivePointers.valueAt(i);
					if (point != null)
						if (((point.x > 0 && point.x < 0 + l.getWidth()) && (point.y > canvas.getHeight() - 2 * l.getHeight() && point.y < canvas.getHeight() - 1 * l.getHeight()))) 
						{
							return true;
						}
				}
				return false;
 
			case 1: // down
				for (int size = mActivePointers.size(), i = 0; i < size; i++) {
					PointF point = mActivePointers.valueAt(i);
					if (point != null)
						if (((point.x > l.getWidth() && point.x < l.getWidth()+ l.getWidth()) && (point.y > canvas.getHeight() - 1 * l.getHeight() && point.y < canvas.getHeight()))) 
						{
							return true;
						}
				}
				return false;
 
			case 0: // right
				for (int size = mActivePointers.size(), i = 0; i < size; i++) {
					PointF point = mActivePointers.valueAt(i);
					if (point != null)
						if (((point.x > 2 * l.getWidth() && point.x < 2* l.getWidth() + l.getWidth()) && (point.y > canvas.getHeight() - 2 * l.getHeight() && point.y < canvas.getHeight() - 1 * l.getHeight()))) 
						{
							return true;
						}
				}
				return false;
			default:
				break;
			}
			return false;
		}
 
		public void draw(Canvas canvas) {
			// super.draw(canvas);
 
			canvas.drawColor(Color.BLACK);
 
			canvas.drawText("Level " + level, tileDimensions,2 * tileDimensions, whiteText);
 
			canvas.drawText("Enemy Left: " + (21 - levelTanks + tankInMap),tileDimensions, 4 * tileDimensions, whiteText);
			int separationTanks = 0;
			int col = 1;
			int row = 1;
			for (int i = 1; i <= (21 - levelTanks + tankInMap); i++, row++) 
			{
				if (row * tileDimensions + separationTanks >= 3 * 4* tileDimensions - tileDimensions) 
				{
					col++;
					row = 1;
					separationTanks = 0;
				}
				canvas.drawBitmap(enemyCounter, row * tileDimensions + separationTanks, 4 * tileDimensions + col* tileDimensions, healthPaint);
				separationTanks += tileDimensions / 4;
			}
 
			canvas.drawText("Lives", tileDimensions,canvas.getHeight() - 3 * l.getHeight() - 2 * l.getHeight()/ 4 - 3 * tileDimensions, whiteText);
			// lives
			int separation = 0;
			for (int i = 1; i <= lives; i++) 
			{
				Rect src = new Rect(0, 0, up.getWidth() / 8, up.getWidth() / 8);
				Rect dst = new Rect(i * tileDimensions + (i - 1) * separation,canvas.getHeight() - 3 * l.getHeight() - 2* l.getHeight() / 4 - 2 * (up.getWidth() / 8),(i * tileDimensions + up.getWidth() / 8) + (i - 1)* separation, canvas.getHeight() - 3* l.getHeight() - 2 * l.getHeight() / 4- up.getWidth() / 8);
				canvas.drawBitmap(up, src, dst, null);
				separation = 10;
			}
 
			// shield bar
			if (buildShield) 
			{
				for (int i = 1; i <= shiledPower; i++)
				canvas.drawLine(5 * tileDimensions,canvas.getHeight() - 3 * l.getHeight()- l.getHeight() / 4 - tileDimensions,5 * tileDimensions + healthFactor * i,canvas.getHeight() - 3 * l.getHeight()- l.getHeight() / 4 - tileDimensions,shieldPaint);
 
				for (int i = 1; i < shiledPower; i++)
					canvas.drawLine(5 * tileDimensions + healthFactor * i,canvas.getHeight() - 3 * l.getHeight()- l.getHeight() / 4 - tileDimensions - 4,5 * tileDimensions + healthFactor * i,canvas.getHeight() - 3 * l.getHeight()- l.getHeight() / 4 - tileDimensions + 4,horizontalLine);
 
				if (shiledPower <= 0)
					buildShield = false;
			}
 
			canvas.drawText("Health", tileDimensions, canvas.getHeight() - 3* l.getHeight() - 2 * l.getHeight() / 4, whiteText);
			// health bar
			if (lives >= 0) 
			{
				for (int i = 1; i <= tanksArray.get(0).health; i++)
					canvas.drawLine(tileDimensions,canvas.getHeight() - 3 * l.getHeight()- l.getHeight() / 4, tileDimensions+ healthFactor * i, canvas.getHeight() - 3* l.getHeight() - l.getHeight() / 4,healthPaint);
				
				for (int i = 1; i < tanksArray.get(0).health; i++)
					canvas.drawLine(tileDimensions + healthFactor * i,canvas.getHeight() - 3 * l.getHeight()- l.getHeight() / 4 - 4, tileDimensions+ healthFactor * i, canvas.getHeight() - 3* l.getHeight() - l.getHeight() / 4 + 4,horizontalLine);
			} 
			else 
			{
				// draw mission failed
				gameOver = true;
			}
 
			// motion arrows
			canvas.drawBitmap(arrows, 0,canvas.getHeight() - 3 * l.getHeight(), null);
 
			// separation line
			canvas.drawLine(3 * l.getWidth(), 0, 3 * l.getWidth(),26 * tileDimensions, paint);
			canvas.drawLine((3 * l.getWidth()) + 26 * tileDimensions, 0,(3 * l.getWidth()) + 26 * tileDimensions,26 * tileDimensions, paint);
 
			// down line
			canvas.drawRect(3 * 4 * tileDimensions - 1, 26 * tileDimensions, 3* 4 * tileDimensions + 26 * tileDimensions + 2,canvas.getHeight(), paint);
 
			canvas.drawBitmap(fire,(38 * tileDimensions)+ ((canvas.getWidth() - (38 * tileDimensions)) / 2)- (fire.getWidth() / 2),canvas.getHeight() - 2 * l.getHeight(), null);
			// x is the center between 2 sepration line and end of the screen
 
			myMap.draw(canvas, true);
 
			if (lives >= 0) 
			{
				// up >> 3
				if (!gameOver && isTouched(3, canvas) && playing && !airStrike) {
					tanksArray.get(0).draw(canvas, 3);
					if (!tankMoveSound.isPlaying() && globalSound) {
						tankMoveSound.start();
					}
				}
				// left >> 2
				else if (!gameOver && isTouched(2, canvas) && playing && !airStrike) {
					tanksArray.get(0).draw(canvas, 2);
					if (!tankMoveSound.isPlaying() && globalSound) {
						tankMoveSound.start();
					}
					// down >> 1
				} else if (!gameOver && isTouched(1, canvas) && playing && !airStrike) {
					tanksArray.get(0).draw(canvas, 1);
					if (!tankMoveSound.isPlaying() && globalSound) {
						tankMoveSound.start();
					}
					// right >> 0
				} else if (!gameOver && isTouched(0, canvas) && playing && !airStrike) {
					tanksArray.get(0).draw(canvas, 0);
					if (!tankMoveSound.isPlaying() && globalSound) {
						tankMoveSound.start();
					}
				} else { // stand still >> -1
					tanksArray.get(0).draw(canvas, -1);
					if (tankMoveSound.isPlaying() && globalSound) {
						tankMoveSound.pause();
					}
				}
 
				if (!gameOver && !airStrike && isTouched(4, canvas)  && playing && (System.currentTimeMillis() - tanksArray.get(0).shootTime)>=300) 
				{
					if (tanksArray.get(0).numOfBullets < tanksArray.get(0).maxBullets && globalSound) 
					{
						sp.play(shoot, 1, 1, 0, 0, 1);
					}
					tanksArray.get(0).shoot();
				}
			}
 
 
			if(!airStrike) // draw dynamic tanks
			{
				for (int i = 1; i < tanksArray.size(); i++) 
				{
					if (tanksArray.get(i).isColliding)	tanksArray.get(i).draw(canvas, randomMoves.nextInt(4));
					else	tanksArray.get(i).draw(canvas, tanksArray.get(i).direction);
				}
				
				for (int i = 1; i < tanksArray.size(); i++) 
				{
						int randomShoot = randomMoves.nextInt(10);
						if ( randomShoot == 0)
							tanksArray.get(i).shoot();
				}
			}
			else // draw static tanks with no bullets shoots
			{
				for (int i = 1; i < tanksArray.size(); i++) 
				{
					tanksArray.get(i).draw(canvas, -1);
				}
			}

 
			if((System.currentTimeMillis() - startTime  >= 1000) && destroyedTanks !=0 && !airStrike)
			createWave();
 
			myMap.draw(canvas, false);
 
			// level text
			canvas.drawText("L E V E L " + level, 3 * 4 * tileDimensions + 13* tileDimensions, 26 * tileDimensions + tileDimensions / 2,textPaint);
 
			
			// bonus part
			
			// 1st bonus after wave 1
			if (levelTanks - tankInMap >= numWave1 && levelTanks - tankInMap < numWave2 && firstBonus)
			{
				levelBonus[0][0] = randomMoves.nextInt(5); // bonus type
				if(level<=4 && levelBonus[0][0]==4)  levelBonus[0][0] -= randomMoves.nextInt(4)+1;
				levelBonus[0][1] = randomMoves.nextInt(24) + 1; // x index , min 1 , max 24
				levelBonus[0][2] = randomMoves.nextInt(24) + 1; // y index
				levelBonus[0][3] = -1; // exist in map , initialy -2
				firstBonus = false;
			} 
			else if (levelTanks - tankInMap >= numWave2 && levelTanks - tankInMap < numWave3 && secondBonus) 
			{
				levelBonus[1][0] = randomMoves.nextInt(5); // bonus type
				
				if(level<=4 && levelBonus[1][0]==4)  levelBonus[1][0] -= randomMoves.nextInt(4)+1;
				
				// make the new bonus new
				while (levelBonus[1][0] == levelBonus[0][0])
				{
					levelBonus[1][0] = randomMoves.nextInt(5);
					if(level<=4 && levelBonus[1][0]==4)  levelBonus[1][0] -= randomMoves.nextInt(4)+1;
				}
				
				
 
				levelBonus[1][1] = randomMoves.nextInt(24) + 1; // x
				levelBonus[1][2] = randomMoves.nextInt(24) + 1; // y
				levelBonus[1][3] = -1; // exist in map , initialy -2
				secondBonus = false;
			} 
			else if (thirdBonus) 
			{
				levelBonus[2][0] =randomMoves.nextInt(5);// bonus type
 
				if(level<=4 && levelBonus[2][0]==4)  levelBonus[2][0] -= randomMoves.nextInt(4)+1;
				
				// make the new bonus new
				while (levelBonus[2][0] == levelBonus[0][0]|| levelBonus[2][0] == levelBonus[1][0])
				{
					levelBonus[2][0] = randomMoves.nextInt(5);
					if(level<=4 && levelBonus[2][0]==4)  levelBonus[2][0] -= randomMoves.nextInt(4)+1;
				}
				
				levelBonus[2][1] = randomMoves.nextInt(24) + 1;// x
				levelBonus[2][2] = randomMoves.nextInt(24) + 1;// y
				levelBonus[2][3] = -1; // exist in map , initially -2
				thirdBonus = false;
			}
 
			for (int i = 0; i < 3; i++) 
			{
				if (levelBonus[i][3] == -1) // exist in map
				{
					canvas.drawBitmap(bonus[levelBonus[i][0]], 12* tileDimensions + tileDimensions* levelBonus[i][2], tileDimensions* levelBonus[i][1], null);
				}
				else if (levelBonus[i][3] == -3) // Collided with it
				{
					// buildShield , extra Live , extraShoot , shield , air strike
					bonusIndex++;
					levelBonus[i][3] = bonusIndex;

					switch (levelBonus[i][0]) 
					{
						case 0:
							for (int k = 11; k <= 15; k++) Map.mapArray[22][k] = 2;
							for (int k = 11; k <= 15; k++) Map.mapArray[23][k] = 2;
							Map.mapArray[25][10] = Map.mapArray[24][11]= Map.mapArray[24][14]= Map.mapArray[24][15]=2;
							break;
						case 1:
							lives++;
							break;
						case 2:
							tanksArray.get(0).maxBullets = 4;
							tanksArray.get(0).numOfBullets = 0;
							break;
						case 3:
							shiledPower = 2;
							buildShield = true;
							break;
						case 4:
							//soot el tayara
							if (globalSound) 
							{
								sp.play(planeSound, 1, 1, 0, 0, 1);
								
							}
							
							explosionPositions = new int[tankInMap][3];
							for(int l = 1 ; l < tankInMap+1;l++)
							{
								explosionPositions[l-1][0] = tanksArray.get(l).x;
								explosionPositions[l-1][1] = tanksArray.get(l).y;
								explosionPositions[l-1][2] = 3;
							}
							bombScale = 10;
							planeUpMotion = 22;
							airStrike = true;
							break;
					}
				} 
				else if (levelBonus[i][3] >= 0) // exist in right (already taken)
				{
					canvas.drawBitmap(bonus[levelBonus[i][0]],40 * tileDimensions, tileDimensions* (2 * levelBonus[i][3]) + tileDimensions,null);
				}
			}
			
 
			if (!gameOver)// target alive
			{
				// tile 13,24 .. 14,24 .. 13,25 .. 14,25
				Rect src = new Rect(0, (level - 1) * targetDimension,targetDimension, level * targetDimension);
				Rect dst = new Rect(24 * tileDimensions, 24 * tileDimensions,26 * tileDimensions, 26 * tileDimensions);
				canvas.drawBitmap(targetAlive, src, dst, null);
			} 
			else 
			{
				Rect src = new Rect(0, (level - 1) * targetDimension,targetDimension, level * targetDimension);
				Rect dst = new Rect(24 * tileDimensions, 24 * tileDimensions,26 * tileDimensions, 26 * tileDimensions);
				canvas.drawBitmap(targetDestroyed, src, dst, null);
 
				Rect src2 = new Rect(0, 0, missionFailed.getWidth(),missionFailed.getHeight());
				Rect dst2 = new Rect(10, (26 * tileDimensions) / 3,canvas.getWidth() - 10, canvas.getHeight()- ((26 * tileDimensions) / 3));
				canvas.drawBitmap(missionFailed, src2, dst2, null);
				if (loseBool && globalSound) 
				{
					sp.play(loseSound, 1, 1, 0, 0, 1);
					loseBool = false;
				}
			}
 
			// you win
			if ((21 - levelTanks + tankInMap) == 0) 
			{
//				nextLevelItem=menu.findItem(R.id.next_game);
//				nextLevelItem.setIcon(R.drawable.ic_menu_forward);
				
				Rect src2 = new Rect(0, 0, levelVictory[level-1].getWidth(), levelVictory[level-1].getHeight());
				Rect dst2 = new Rect(canvas.getWidth()/2-levelVictory[level-1].getWidth()/2,2*tileDimensions,canvas.getWidth()/2+levelVictory[level-1].getWidth()/2,2*tileDimensions+levelVictory[level-1].getHeight());
				canvas.drawBitmap(levelVictory[level-1], src2, dst2, null);
 
				Rect src3 = new Rect(0, 0, missionAcomplished.getWidth(),missionAcomplished.getHeight());
				Rect dst3 = new Rect(10, 2*tileDimensions+levelVictory[level-1].getHeight(),canvas.getWidth() - 10, 6*tileDimensions+levelVictory[level-1].getHeight());
				canvas.drawBitmap(missionAcomplished, src3, dst3, null);
				playing = false;
 
				canPassNextLevel = true;
 
				if (winBool && globalSound) 
				{
					sp.play(winSound, 1, 1, 0, 0, 1);
					winBool = false;
				}
 
				SharedPreferences.Editor editor = toSave.edit();
				if (canIncrementLevel) {
					toSave2 = getSharedPreferences(fileName, 0);
					int x = toSave2.getInt("levelNumber", 5);
					if (x < levelNumber + 1) {
						editor.putInt("levelNumber", ++levelNumber);
						editor.commit();
						canIncrementLevel = false;
					}
				}
			}
			
			if(airStrike)
			{
				
				//missile effect
				if(planeUpMotion < 2)
				{
					if(bombScale>0) // missile drop
					{
						//soot el w2oo3 10
						if(bombScale==10){
							if (globalSound) 
							{
								sp.play(bombFallSound, 1, 1, 0, 0, 1);
							}
						}
						missile =  Bitmap.createScaledBitmap(missile, bombScale*tileDimensions/2,bombScale*tileDimensions/2, false);
						canvas.drawBitmap(missile,26*tileDimensions-(bombScale*tileDimensions/2)/2,13*tileDimensions-(bombScale*tileDimensions/2)/2,null);
						bombScale--;
					}
					else // tanks explosion
					{
						
						// nowa2af w2oo3 el kombela 0
						// ebda2 soot el enfgaar 0
						
						if(bombScale==0){
							if (globalSound) 
							{
								sp.stop(bombFallSound);
								sp.play(PlaneExplodeSound, 1, 1, 0, 0, 1);
							}
						}
						
						for(int i = tanksArray.size()-1 ; i > 0 && bombScale==0; i--)
						{
							tanksArray.remove(1);
							destroyedTanks++;
						}
						
						if(bombScale==0)
					      {
					       bombScale--;
					      }
						for(int i = 0 ; i < tankInMap ; i++)
						{
							switch(explosionPositions[i][2])
							{
								case 3:
									canvas.drawBitmap(smallExplode, explosionPositions[i][0]+tileDimensions-tileDimensions/4, explosionPositions[i][1]+tileDimensions/2 - tileDimensions/4, null);
									break;
								case 2:
									canvas.drawBitmap(mediumExplode, explosionPositions[i][0]+tileDimensions-tileDimensions/2, explosionPositions[i][1]+tileDimensions/2 - tileDimensions/2, null);
									break;
								case 1:
									canvas.drawBitmap(largeExplode, explosionPositions[i][0]+tileDimensions-2*tileDimensions/2, explosionPositions[i][1]+tileDimensions/2 - 2*tileDimensions/2, null);
									break;
								case 0:
									canvas.drawBitmap(mediumExplode, explosionPositions[i][0]+tileDimensions-tileDimensions/2, explosionPositions[i][1]+tileDimensions/2 - tileDimensions/2, null);
									break;
								case -1:
									canvas.drawBitmap(smallExplode, explosionPositions[i][0]+tileDimensions-tileDimensions/4, explosionPositions[i][1]+tileDimensions/2 - tileDimensions/4, null);
									break;
							}
							explosionPositions[i][2]-=1;
						}
									
					}
				}
				
				// plane motion
				if(planeUpMotion > -17 )
				{
					canvas.drawBitmap(planeStrike,21*tileDimensions,planeUpMotion*tileDimensions,null);
					planeUpMotion--;
					
				}
				else // air strike ended
				{
					//7awa2af soot el tayara
						airStrike = false;
						startTime = System.currentTimeMillis();
						tankInMap = 0;
				}
			}
		}
 
		public void pause() {
			isRunning = false;
			
				try {
					thread.join();
				} catch (InterruptedException e) {
 
					e.printStackTrace();
				}
				
 
			thread = null;
		}
 
		public void resume() {
			isRunning = true;
			thread = new Thread(this);
			thread.start();
		}
 
	}
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if ( ( levelNumber==10 && !canPassNextLevel) || (levelNumber==11)){
			getMenuInflater().inflate(R.menu.gfx_menu_2, menu);

		}else{
			getMenuInflater().inflate(R.menu.gfx, menu);
		}
		return true;
	}
 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
 
		switch (item.getItemId()) {
		case R.id.new_game:
			int x = levelNumber;
			if (canIncrementLevel == false) {
				x = levelNumber - 1;
			}
			Intent myIntent = new Intent("android.intent.action.GAME");
			myIntent.putExtra("levelNum", x);
			startActivity(myIntent);
			finish();
			break;
		case R.id.exit_game:
			finish();
			break;
 
		case R.id.next_game:
 
			if ((canPassNextLevel&& levelNumber <=10) || levelNumber < levelAchieved ) {
				if (canIncrementLevel == false) {
					Intent intent = new Intent("android.intent.action.STORY");
					intent.putExtra("levelNum", levelNumber);
					startActivity(intent);
					finish();
				} else {
					Intent intent = new Intent("android.intent.action.STORY");
					intent.putExtra("levelNum", levelNumber + 1);
					startActivity(intent);
					finish();
				}
 
				break;
			}
 
		}
		return true;
	}
 
}