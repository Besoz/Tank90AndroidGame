
package com.avalanche.movingsprite;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.avalanche.movingsprite.GFX.GFXsurface;

public class Bullet {
    int xBullet, yBullet;
    int xSpeed, ySpeed, direction;
    boolean ally, isMoving;
    int  destTile;
	Bitmap bullet;
	GFXsurface ourSurface;
	int bulletDim, destroyedTankIndex, sourceTankIndex;

	public Bullet(int xTank, int yTank, int direct, int tileWidth, boolean kind, GFXsurface gfXsurface, int sourceTank) {
		
		destroyedTankIndex = -1;
		direction = direct;
		destTile = tileWidth;
		isMoving = true;
		ourSurface = gfXsurface;
		bulletDim = destTile / 3;
		sourceTankIndex = sourceTank;
		switch (direction) {
		case 0: // right
			xBullet = xTank + 2 * destTile - destTile;
			yBullet = yTank + destTile - destTile / 6; // y al center bata3at al
														// dababa - nos 3arf al
														// bullet
			bullet = gfXsurface.bRight;
			xSpeed = destTile;
			ySpeed = 0;
			break;
		case 1: // down
			xBullet = xTank + destTile - destTile / 6;
			yBullet = yTank + 2 * destTile - destTile;
			bullet = gfXsurface.bDown;
			xSpeed = 0;
			ySpeed = destTile;
			break;
		case 2: // left
			xBullet = xTank - (destTile * 2) / 6 + destTile;
			yBullet = yTank + destTile - destTile / 6;
			bullet = gfXsurface.bLeft;
			xSpeed = -destTile;
			ySpeed = 0;
			break;
		case 3: // up
			xBullet = xTank + destTile - destTile / 6;
			yBullet = yTank - (destTile * 2) / 6 + destTile;
			bullet = gfXsurface.bUp;
			xSpeed = 0;
			ySpeed = -destTile;
			break;
		}

	}

	public void draw(Canvas canvas) {
		if (isMoving) {
			update(direction);
			canvas.drawBitmap(bullet, xBullet, yBullet, null);
		}
	}

	public void update(int dir) {

		
		if (dir == 0)// right
		{
			// check collision
			if (xBullet + bullet.getWidth() + xSpeed > (3 * 4 * destTile) + 26 * destTile) {
				xBullet = (3 * 4 * destTile) + 26 * destTile - bulletDim;
				xSpeed = 0;
				ySpeed = 0;
				isMoving = false;
			}
		} else if (dir == 1)// down
		{
			// check collision
			if (yBullet + bullet.getHeight() + ySpeed > 26 * destTile) {

				xSpeed = 0;
				ySpeed = 0;
				yBullet = 26 * destTile - bulletDim;
				isMoving = false;
			}
		} else if (dir == 2)// left
		{
			// check collision
			if (xBullet + xSpeed < 3 * destTile * 4) {

				xSpeed = 0;
				ySpeed = 0;
				xBullet = 3 * destTile * 4;
				isMoving = false;
			}

		} else if (dir == 3)// up
		{
			// check collision
			if (yBullet + ySpeed <= 0) {

				xSpeed = 0;
				ySpeed = 0;
				yBullet = 0;
				isMoving = false;
			}
		}

		collision();

		xBullet += xSpeed;
		yBullet += ySpeed;
	}

	public void collision() {

		float xFront = 0, yFront = 0;
		float positionStart, positionMiddle, positionEnd;
		int determineCol,determineRow ;
		  
		switch (direction) {
		
		case 0: // right
			xFront = xBullet + bulletDim +xSpeed/2;
			yFront = yBullet + bulletDim / 2;
													
			determineCol=  (int) ((xFront-(12*destTile))/destTile);
			if(determineCol>=26) determineCol =25;
			
			for (int i = 0; i < 26; i++) {
				if (Map.mapArray[i][determineCol] == 1 || Map.mapArray[i][determineCol] == 2) {
					positionStart = i * destTile;
					positionMiddle = positionStart + (destTile / 2);
					positionEnd = positionStart + destTile;
					// on up of brick
					if (yFront >= positionStart && yFront < positionMiddle) 
                    {
						// first stop bullet
						xSpeed = 0;
						ySpeed = 0;
						isMoving = false;
						// check brick type
						if (Map.mapArray[i][determineCol] == 1) {
							Map.mapArray[i][determineCol] = 0;
						}
						// check prev brick type
						if (i - 1 >= 0) {
							if (Map.mapArray[i-1][determineCol] == 1) {
								Map.mapArray[i-1][determineCol] = 0;
							}
						}
                        
                        break;
					}
					// on down of brick
					else if (yFront >= positionMiddle && yFront < positionEnd) 
                    {
						// first stop bullet
						xSpeed = 0;
						ySpeed = 0;
						isMoving = false;
						// check brick type
						if (Map.mapArray[i][determineCol] == 1) {
							Map.mapArray[i][determineCol] = 0;
						}
						// check next brick type
						if (i + 1 < 26) {
							if (Map.mapArray[i+1][determineCol] == 1) {
								Map.mapArray[i+1][determineCol] = 0;
							}
						}
                        
                        break;
					}

				}

			}
			break;
		case 1: // down
			xFront = xBullet + bulletDim / 2;
			yFront = yBullet + bulletDim + ySpeed/2;

			determineRow = (int) (yFront / destTile);
			 if(determineRow>=26) determineRow =25;
			for (int i = 0; i < 26; i++) {
				if (Map.mapArray[determineRow][i] == 1 || Map.mapArray[determineRow][i] == 2) {
					positionStart = i * destTile+12*destTile;
					positionMiddle = positionStart + (destTile / 2);
					positionEnd = positionStart + destTile;
					// on left of brick
					if (xFront >= positionStart && xFront < positionMiddle) 
                    {
						// first stop bullet
						xSpeed = 0;
						ySpeed = 0;
						isMoving = false;
						// check brick type
						if (Map.mapArray[determineRow][i] == 1) {
							Map.mapArray[determineRow][i] = 0;
						}
						// check prev brick type
						if (i - 1 >= 0) {
							if (Map.mapArray[determineRow][i-1] == 1) {
								Map.mapArray[determineRow][i-1] = 0;
							}
						}
                        break;
					}
					// on right of brick
					else if (xFront >= positionMiddle && xFront < positionEnd) 
                    {
						// first stop bullet
						xSpeed = 0;
						ySpeed = 0;
						isMoving = false;
						// check brick type
						if (Map.mapArray[determineRow][i] == 1) {
							Map.mapArray[determineRow][i] = 0;
						}
						// check next brick type
						if (i + 1 < 26) {
							if (Map.mapArray[determineRow][i+1] == 1) {
								Map.mapArray[determineRow][i+1] = 0;
							}
						}
                        break;
					}

				}

			}
			break;
		case 2: // left
			xFront = xBullet+xSpeed/2 ;
			yFront = yBullet + bulletDim / 2;
			
			determineCol=  (int) ((xFront-(12*destTile))/destTile);
			if(determineCol>=26) determineCol =25;
			
			for (int i = 0; i < 26; i++) {
				if (Map.mapArray[i][determineCol] == 1 || Map.mapArray[i][determineCol] == 2) {
					positionStart = i * destTile;
					positionMiddle = positionStart + (destTile / 2);
					positionEnd = positionStart + destTile;
					// on up of brick
					if (yFront >= positionStart && yFront < positionMiddle) 
                    {
						// first stop bullet
						xSpeed = 0;
						ySpeed = 0;
						isMoving = false;
						// check brick type
						if (Map.mapArray[i][determineCol] == 1) {
							Map.mapArray[i][determineCol] = 0;
						}
						// check prev brick type
						if (i - 1 >= 0) {
							if (Map.mapArray[i-1][determineCol] == 1) {
								Map.mapArray[i-1][determineCol] = 0;
							}
						}
                        break;
					}
					// on down of brick
					else if (yFront >= positionMiddle && yFront < positionEnd) 
                    {
						// first stop bullet
						xSpeed = 0;
						ySpeed = 0;
						isMoving = false;
						// check brick type
						if (Map.mapArray[i][determineCol] == 1) {
							Map.mapArray[i][determineCol] = 0;
						}
						// check next brick type
						if (i + 1 < 26) {
							if (Map.mapArray[i+1][determineCol] == 1) {
								Map.mapArray[i+1][determineCol] = 0;
							}
						}
                        break;
					}

				}

			}
			break;
		case 3: // up
			xFront = xBullet + bulletDim / 2;
			yFront = yBullet + ySpeed/2;

			 determineRow = (int) (yFront / destTile);
			 if(determineRow>=26) determineRow =25;
			
			for (int i = 0; i < 26; i++) {
				if (Map.mapArray[determineRow][i] == 1 || Map.mapArray[determineRow][i] == 2) {
					positionStart = i * destTile +12*destTile;
					positionMiddle = positionStart + (destTile / 2);
					positionEnd = positionStart + destTile;
					// on left of brick
					if (xFront >= positionStart && xFront < positionMiddle) 
                    {
						// first stop bullet
						xSpeed = 0;
						ySpeed = 0;
						isMoving = false;
						// check brick type
						if (Map.mapArray[determineRow][i] == 1) {
							Map.mapArray[determineRow][i] = 0;
						}
						// check prev brick type
						if (i - 1 >= 0) {
							if (Map.mapArray[determineRow][i-1] == 1) {
								Map.mapArray[determineRow][i-1] = 0;
							}
						}
                        break;
					}
					// on right of brick
					else if (xFront >= positionMiddle && xFront < positionEnd) 
                    {
						// first stop bullet
						xSpeed = 0;
						ySpeed = 0;
						isMoving = false;
						// check brick type
						if (Map.mapArray[determineRow][i] == 1) {
							Map.mapArray[determineRow][i] = 0;
						}
						// check next brick type
						if (i + 1 < 26) {
							if (Map.mapArray[determineRow][i+1] == 1) {
								Map.mapArray[determineRow][i+1] = 0;
							}
						}
                        break;
					}

				}

			}
			break;
		}
		
		
 	    //	collision with target
	    if(xFront >= 24*destTile && xFront <=26*destTile && yFront<=26*destTile && yFront>= 24*destTile )
		{
		     ourSurface.gameOver = true;
	    	 xSpeed = 0;
		     ySpeed = 0;
		     isMoving = false;
		     return;
		}

		// collision with tanks
		int topY, downY, leftX, rightX;

		for (int i = 0; i < ourSurface.tanksArray.size(); i++) {
			if (i == sourceTankIndex)
				continue;

			topY = ourSurface.tanksArray.get(i).y;
			downY = topY + 2 * destTile;

			leftX = ourSurface.tanksArray.get(i).x;
			rightX = leftX + 2 * destTile;

			if ((xBullet + xSpeed >= leftX && xBullet + xSpeed <= rightX) && (yBullet + ySpeed >= topY && yBullet + ySpeed <= downY)) { // colloided
				if (ourSurface.tanksArray.get(i).ally == ourSurface.tanksArray.get(sourceTankIndex).ally)
					return; // tanks of the same type
				xSpeed = 0;
				ySpeed = 0;
				isMoving = false;
				destroyedTankIndex = i;
				return;
			}
		}

	}
}