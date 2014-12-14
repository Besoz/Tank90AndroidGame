package com.avalanche.movingsprite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Map {
    public static int[][] mapArray = new int[26][26];
	int myTile, waterMove;
	Bitmap wall, water, brick, snow, tree, black, water2;

	// 0 black // 1 brick // 2 wall // 3 water // 4 tree // 5 snow
	public Map(int numOfLevel, int tile, Context c) throws IOException {
		myTile = tile;
		waterMove = 0;
		wall = BitmapFactory.decodeResource(c.getResources(), R.drawable.solid);
		water = BitmapFactory.decodeResource(c.getResources(), R.drawable.water);
		water2 = BitmapFactory.decodeResource(c.getResources(), R.drawable.water2);
		brick = BitmapFactory.decodeResource(c.getResources(), R.drawable.brick);
		snow = BitmapFactory.decodeResource(c.getResources(), R.drawable.snow);
		tree = BitmapFactory.decodeResource(c.getResources(), R.drawable.tree);
		black = BitmapFactory.decodeResource(c.getResources(), R.drawable.black);

		wall = Bitmap.createScaledBitmap(wall, myTile, myTile, false);
		water = Bitmap.createScaledBitmap(water, myTile, myTile, false);
		water2 = Bitmap.createScaledBitmap(water2, myTile, myTile, false);
		brick = Bitmap.createScaledBitmap(brick, myTile, myTile, false);
		snow = Bitmap.createScaledBitmap(snow, myTile, myTile, false);
		tree = Bitmap.createScaledBitmap(tree, myTile, myTile, false);
		black = Bitmap.createScaledBitmap(black, myTile, myTile, false);
//		numOfLevel=1;
		BufferedReader br = new BufferedReader(new InputStreamReader(c.getAssets().open("level" + numOfLevel + ".txt")));
		try {

			char myCharacter;

			for (int x = 0; x < 26; x++) {
			    for (int y = 0; y < 26; y++) {
			     myCharacter = (char) br.read();
			     mapArray[x][y] = myCharacter-'0';
			     if(y==25){
			      myCharacter = (char) br.read();
			      myCharacter = (char) br.read();
			      
			     }
			    }
			   }
		} finally {
			br.close();
		}
	}

	public void draw(Canvas canvas, boolean under) {
		int Xzero = myTile * 3 * 4; // start after the separation line
		int i = 0;
		int j = 0;
		for (int y = 0; y < 26 * myTile; y += myTile , i++) {
			j = 0;
			for (int x = Xzero; x < Xzero + (26 * myTile); x += myTile , j++) {
				if (under == true) // print blocks under tank
				{
					if (mapArray[i][j] == 0) 
					{
						canvas.drawBitmap(black, x, y, null);
					}
					else if ((mapArray[i][j] == 1)) 
					{
						canvas.drawBitmap(brick, x, y, null);
					}
					else if ((mapArray[i][j] == 2)) 
					{
						canvas.drawBitmap(wall, x, y, null);
					}
					else if ((mapArray[i][j] == 3)) 
					{
						if (waterMove == 0) 
						{
							canvas.drawBitmap(water, x, y, null);
						}
						else 
						{
							canvas.drawBitmap(water2, x, y, null);
						}
					} 
					else if ((mapArray[i][j] == 4)) 
					{
						canvas.drawBitmap(black, x, y, null);
					}
					else if ((mapArray[i][j] == 5)) 
					{
						canvas.drawBitmap(snow, x, y, null);
					}

				} 
				else // print blocks on tanks
				{
					if (mapArray[i][j] == 4) {
						canvas.drawBitmap(tree, x, y, null);
					}
				}
			}
		}
		
		
		if (under == true)
			waterMove = (waterMove + 1) % 2;
	}

}
