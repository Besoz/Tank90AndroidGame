package com.avalanche.movingsprite;
 
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.avalanche.movingsprite.GFX.GFXsurface;
 
public class tankSprite {
 
    int x, y, direction, srcX, srcY, dimsOfTile, kindForX, kindForY, kind,destTile;
    int xSpeed, ySpeed,maxBullets,numOfBullets;
    boolean isColliding,ally,colloided;
    int xBulletSpeed, yBulletSpeed;
    ArrayList<Bullet> bullets;
    int tankID,xDestoryedTank,yDestoryedTank,drawExplosion,health;
	double healthUnit;
    long shootTime;
    Paint greenLine,redLine;
 
    Bitmap[] spriteSheets = new Bitmap[4]; // pics of motions
 
    GFXsurface ourSurface;
 
    public tankSprite(GFXsurface gfXsurface, Bitmap right, Bitmap down, Bitmap left, Bitmap up, int type,int tileWidth , int startX , int startY , int directory,boolean friend ,int ID) 
	{
		ourSurface = gfXsurface;
		shootTime = 0;
		drawExplosion = -2;
 
		spriteSheets[0] = right;
		spriteSheets[1] = down;
		spriteSheets[2] = left;
		spriteSheets[3] = up;
 
		destTile = tileWidth;
		bullets = new ArrayList();
		colloided = false;
 
		ally = friend;
 
		maxBullets = 3;
 
		numOfBullets = 0;
 
		kind = type;
		kindForX = kind;
		kindForY = 1;
 
		dimsOfTile = right.getHeight() / 8;// = 24 width and height of a single							// square
 
		x=startX;
		y=startY;
		srcX = srcY = 1;
 
		xSpeed = 0;
		ySpeed = 0;
		direction = directory;
		isColliding=false;
 
		tankID=ID;
 
		switch(kind)
		{
			case 0: // green
                    health = 4;
					break;
			case 1: // red
                    health =1;
				    break;
			case 2: // yellow
                    health =1;
				    break;
			case 3: // blue
                    health =12;
				    break;
    		case 4: //Purple
                    health =2;
				    break;
            case 5: // cyan
                    health =3;
				    break;
            case 6: // orange
                    health =4;
			    	break;
            case 7: // Pink
                    health =4;
                    break;
		}
		greenLine = new Paint();
		greenLine.setColor(Color.GREEN);
		greenLine.setStrokeWidth(4);
		
		redLine = new Paint();
		redLine.setColor(Color.RED);
		redLine.setStrokeWidth(4);
		
		healthUnit=1.5*dimsOfTile/health;
	}
 
	public void shoot()
	{
		if(numOfBullets<maxBullets  && (System.currentTimeMillis() - shootTime)>=300)
		{
			shootTime = System.currentTimeMillis();
			Bullet myBullet = new Bullet(x,y,direction,destTile,ally,ourSurface,tankID); 
			bullets.add(myBullet);
			numOfBullets++;
		}
	}
 
    public void resetIndices()
    {
        for(int i = 1 ; i < ourSurface.tanksArray.size() ;i++) // tanks loop
        {
            ourSurface.tanksArray.get(i).tankID = i;
            for(int j = 0 ; j < ourSurface.tanksArray.get(i).bullets.size() ; j++) // bullets loop
                ourSurface.tanksArray.get(i).bullets.get(j).sourceTankIndex = i;
        }
    }
 
 
	public void draw(Canvas canvas, int dir) 
	{
		//tank motion
		update(dir);
		Rect src = new Rect(kindForX * (srcX * dimsOfTile), kindForY * (srcY * dimsOfTile), (kindForX * (srcX * dimsOfTile)) + dimsOfTile, (kindForY * (srcY * dimsOfTile)) + dimsOfTile);
		Rect dst = new Rect(x, y, x + 2 * destTile, y + 2 * destTile);
		canvas.drawBitmap(spriteSheets[direction], src, dst, null);
		
		// health line for enemy tanks
		
		if ( y - dimsOfTile/4 >=0 && tankID !=0  ){
			canvas.drawLine(x, y-dimsOfTile/4, (float) (x+1.5*dimsOfTile),y-dimsOfTile/4,greenLine);
			canvas.drawLine((float) (x+(health)*healthUnit), y-dimsOfTile/4, (float) (x+1.5*dimsOfTile),y-dimsOfTile/4,redLine);
		}
 
		for(int i = 0 ; i < bullets.size() ; i++)
		{
			if(bullets.get(i).isMoving)	bullets.get(i).draw(canvas);
			else
			{
				if(bullets.get(i).destroyedTankIndex == -1) // collision to wall
				{
					// make small explosion
					canvas.drawBitmap(ourSurface.smallExplode, bullets.get(i).xBullet, bullets.get(i).yBullet, null);
				}
				else // collision to tank
				{
                    canvas.drawBitmap(ourSurface.smallExplode, bullets.get(i).xBullet, bullets.get(i).yBullet, null);
					// x and y tank
                    if(bullets.get(i).destroyedTankIndex >= 0 && bullets.get(i).destroyedTankIndex < ourSurface.tanksArray.size())
                    {
                        if(i >=0 && i < bullets.size())
                        {
					        xDestoryedTank = ourSurface.tanksArray.get(bullets.get(i).destroyedTankIndex).x;
					        yDestoryedTank = ourSurface.tanksArray.get(bullets.get(i).destroyedTankIndex).y;
                        }
                        else continue;
                    }
                    else continue;
 
                    //ne2alel el health 
                    if(bullets.get(i).destroyedTankIndex == 0 && ourSurface.buildShield)
                    {
                    	ourSurface.shiledPower--;
                    }
                    else
                    {
                    	ourSurface.tanksArray.get(bullets.get(i).destroyedTankIndex).health--;
                    }
 
                    //decrement lives
                    if(bullets.get(i).destroyedTankIndex == 0 && ourSurface.tanksArray.get(bullets.get(i).destroyedTankIndex).health<=0)
                    ourSurface.lives--;
 
                    //enemy tank destroyed
                    if(bullets.get(i).destroyedTankIndex != 0 && ourSurface.tanksArray.get(bullets.get(i).destroyedTankIndex).health<=0) // remove enemy tank
                    {
                    	if(GFX.globalSound) ourSurface.sp.play(ourSurface.explodeSound, 1, 1, 0, 0, 1);
					    ourSurface.tanksArray.remove(bullets.get(i).destroyedTankIndex);
                        ourSurface.tankInMap--;
                        drawExplosion = 2; //make explosion
                        ourSurface.destroyedTanks++;
                        ourSurface.startTime = System.currentTimeMillis();
                        resetIndices();
                    }
 
                    //player tank destroy , have lives or no
                    else if(ourSurface.lives>=0 && bullets.get(i).destroyedTankIndex == 0 && ourSurface.tanksArray.get(bullets.get(i).destroyedTankIndex).health<=0)
                    {
                        //Restet Place
                    	if(GFX.globalSound) ourSurface.sp.play(ourSurface.explodeSound, 1, 1, 0, 0, 1);
                        ourSurface.tanksArray.get(0).x = 19 *destTile;
                        ourSurface.tanksArray.get(0).y = destTile * 24;
                        ourSurface.tanksArray.get(0).direction = 3;
                        ourSurface.tanksArray.get(0).kindForX = 0;
                        ourSurface.tanksArray.get(0).kindForY = 1;
                        ourSurface.tanksArray.get(0).health = 4;
                        drawExplosion = 2;//make explosion
                    }
                    else if(ourSurface.lives<0 && bullets.get(i).destroyedTankIndex == 0 && ourSurface.tanksArray.get(bullets.get(i).destroyedTankIndex).health<=0){
                        //game over
                    	if(GFX.globalSound) ourSurface.sp.play(ourSurface.explodeSound, 1, 1, 0, 0, 1);
                    	ourSurface.tanksArray.get(0).x = 0;
                        ourSurface.tanksArray.get(0).y = 0;
                        drawExplosion = 2;//make explosion
                    }
 
				}
				bullets.remove(i);	
				numOfBullets--;
			}
		}
 
		if(drawExplosion != -2)
		{
			switch(drawExplosion)
			{
				case 2:
					canvas.drawBitmap(ourSurface.mediumExplode, xDestoryedTank+destTile-destTile/2, yDestoryedTank+destTile/2 - destTile/2, null);
					break;
				case 1:
					canvas.drawBitmap(ourSurface.largeExplode, xDestoryedTank+destTile-2*destTile/2, yDestoryedTank+destTile/2 - 2*destTile/2, null);
					break;
				case 0:
					canvas.drawBitmap(ourSurface.mediumExplode, xDestoryedTank+destTile-destTile/2, yDestoryedTank+destTile/2 - destTile/2, null);
					break;
				case -1:
					canvas.drawBitmap(ourSurface.smallExplode, xDestoryedTank+destTile-destTile/4, yDestoryedTank+destTile/2 - destTile/4, null);
					break;
			}
			drawExplosion--;
		}
 
	}
 
	public void update(int dir) {
 
 
		if(!colloided && !(dir == direction) || !(dir == -1))	colloided = false;
 
		if(!colloided)
		{
			isColliding=false;
			// do nothing
			if (dir == -1) {
				xSpeed = 0;
				ySpeed = 0;
			}
			// moving right
			else if (dir == 0) {
				xSpeed = destTile/4;
				ySpeed = 0;
				direction = 0;
 
				kindForY = kind;
				kindForX = 1;
 
				// right wall
				// destTile * 2 >> 3ard al dababa
				if (x + destTile * 2 + xSpeed > (3 * 4*destTile)+26*destTile) {
					x= ((3 * 4*destTile)+26*destTile)-(2*destTile);
					xSpeed = 0;
					ySpeed = 0;
					isColliding=true;
				}
 
			}
 
			// moving down
			else if (dir == 1) {
 
				xSpeed = 0;
				ySpeed = destTile/4;
				direction = 1;
 
				kindForY = 1;
				kindForX = kind; // because x is constant when moving down
 
				// down wall
				if (y + destTile * 2 + ySpeed > 26*destTile) {
					y=26*destTile-(2*destTile);
					xSpeed = 0;
					ySpeed = 0;
					isColliding=true;
				}
 
			}
 
			// moving left
			else if (dir == 2) {
				xSpeed = -(destTile/4);
				ySpeed = 0;
				direction = 2;
 
				kindForY = kind;
				kindForX = 1;
 
				// left wall
				if (x + xSpeed < 3 * destTile*4) {
					x=3*destTile*4;
					xSpeed = 0;
					ySpeed = 0;
					isColliding=true;
				}
 
			}
 
			// moving up
			else if (dir == 3) {
				xSpeed = 0;
				ySpeed = -(destTile/4);
				direction = 3;
 
				kindForY = 1;
				kindForX = kind; // because x is constant when moving up
 
				// up wall
				if (y + ySpeed <= 0) {
					y=0;
					xSpeed = 0;
					ySpeed = 0;
					isColliding=true;
				}
 
			}
 
 
 
			// collision
			int typeOfCollision = collision(dir);
 
			if(typeOfCollision == 1)
			{
				xSpeed =0;
				ySpeed = 0;
				isColliding=true;
			}
			else if(typeOfCollision == 2)// snow then increase speed
			{
				if(xSpeed != 0) xSpeed+= (2*xSpeed);
				else if(ySpeed != 0) ySpeed += (2*ySpeed);
			}
		}
 
		if (dir == 0 || dir == 2) // right or left
		{
			srcX = (srcX + 1) % 8;
			srcY = 1;
		} 
		else if (dir == 1 || dir == 3) // up or down
		{
			srcX = 1;
			srcY = (srcY + 1) % 8;
		}
 
		x += xSpeed;
		y += ySpeed;
	}
 
    public int collision(int dir)
    {
        if(dir == -1) return 0;
 
        if(dir==0) // right
        {
            int row = (y+destTile)/destTile;     // y of center of the tank / tank height = row index of down tile if opposed to 2 tiles , and middle tile if opposed to 3 tiles
            int prevRow = row-1;        // prev row index (always exist)
            int nextRow = row+1;    // next row
            if(nextRow == Map.mapArray.length) nextRow--;
 
            int nextCol = ((x- 3*4*destTile)+2*destTile +xSpeed)/destTile; // +1
            if(nextCol >= Map.mapArray[0].length) return 0; // hit the right wall
 
            //hit target
            if(nextCol==12 && row ==24 || nextCol==13 && row ==24 || nextCol==12 && row ==25 || nextCol==13 && row ==25 )
            {
            	return 1;
            }
            else if(nextCol==12 && prevRow ==24 || nextCol==13 && prevRow ==24 || nextCol==12 && prevRow ==25 || nextCol==13 && prevRow ==25 )
            {
            	return 1;
            }
            else if(nextCol==12 && nextRow ==24 || nextCol==13 && nextRow ==24 || nextCol==12 && nextRow ==25 || nextCol==13 && nextRow ==25 )
            {
            	return 1;
            }
 
            int tileX = nextCol * destTile + (3*4*destTile);
 
            if(x + destTile * 2 + xSpeed  >= tileX) // touched
            {
                int tileYStart = prevRow * destTile;
                int tileYEnd = nextRow * destTile + destTile;
 
                int tankYStart = y;
                int tankYMiddle = y+destTile;
                int tankYEnd = y+2*destTile;
 
                int returnValue = 0;
                for(int i = tileYStart ; i< tileYEnd ; i+=destTile) // check the 3 tills
                {
                    if(tankYStart >= i && tankYStart <= i+destTile) //check up block
                    {
                        returnValue = detectCollisionType(prevRow,nextCol);
                        if(returnValue!=0)
                        {
                        	x= tileX - 2*destTile;
                        	colloided = true;
                        	return returnValue;
                        }
                    }
                    else if(tankYMiddle >= i && tankYMiddle <= i+destTile) // check middle block
                    {
                        returnValue = detectCollisionType(row,nextCol);
                        if(returnValue!=0) 
                        {
                        	x= tileX - 2*destTile;
                        	colloided = true;
                        	return returnValue;
                        }
                    }
                    else if(tankYEnd >= i && tankYEnd <= i+destTile) // check bottom block
                    {
                        returnValue = detectCollisionType(nextRow,nextCol);
                        if(returnValue!=0  && tankYStart>=prevRow*destTile+destTile/3)
                        {
                        	x= tileX - 2*destTile;
                        	colloided = true;
                        	return returnValue;
                        }
                    }
                }
            }
 
 
 
 
        }
        else if(dir==2) // left
        {
            int row = (y+destTile)/destTile;     // y of center of the tank / tank height = row index of down tile
            int prevRow = row-1;        // prev row index (always exist)
            int nextRow = row+1;    // next row
            if(nextRow == Map.mapArray.length) nextRow--;
 
            int nextCol = (x- 3*4*destTile +xSpeed)/destTile; // -1
            if(nextCol < 0) return 0;
 
            //hit target
            if(nextCol==12 && row ==24 || nextCol==13 && row ==24 || nextCol==12 && row ==25 || nextCol==13 && row ==25 )
            {
            	return 1;
            }
            else if(nextCol==12 && prevRow ==24 || nextCol==13 && prevRow ==24 || nextCol==12 && prevRow ==25 || nextCol==13 && prevRow ==25 )
            {
            	return 1;
            }
            else if(nextCol==12 && nextRow ==24 || nextCol==13 && nextRow ==24 || nextCol==12 && nextRow ==25 || nextCol==13 && nextRow ==25 )
            {
            	return 1;
            }
 
            int tileX = nextCol * destTile + (3*4*destTile) + destTile;
 
            if(x + xSpeed <= tileX) //touched
            {
                int tileYStart = prevRow * destTile;
                int tileYEnd = nextRow * destTile + destTile;
 
                int tankYStart = y;
                int tankYMiddle = y+destTile;
                int tankYEnd = y+2*destTile;
 
                int returnValue = 0;
                for(int i = tileYStart ; i< tileYEnd ; i+=destTile) // check the 3 tills
                {
                    if(tankYStart >= i && tankYStart <= i+destTile) //check up block
                    {
                        returnValue= detectCollisionType(prevRow,nextCol);
                        if(returnValue!=0)
                        {
                        	x= tileX;
                        	colloided = true;
                        	return returnValue;
                        }
                    }
                    else if(tankYMiddle >= i && tankYMiddle <= i+destTile) // check middle block
                    {
                        returnValue = detectCollisionType(row,nextCol);
                        if(returnValue!=0)
                        {
                        	x= tileX;
                        	colloided = true;
                        	return returnValue;
                        }
                    }
                    else if(tankYEnd >= i && tankYEnd <= i+destTile) // check bottom block
                    {
                        returnValue = detectCollisionType(nextRow,nextCol);
                        if(returnValue!=0 && tankYStart>=prevRow*destTile+destTile/3)
                        {
                        	x= tileX;
                        	colloided = true;
                        	return returnValue;
                        }
                    }
                }
            }
 
        }
        else if(dir==3) // up
        {
            int upRow = (y + ySpeed)/destTile;    // -1 y of front of the tank / tank height = row index of down tile
 
            int leftCol = (x- 3*4*destTile)/destTile; // most left col
            int middleCol = leftCol+1;
            int rightCol = middleCol+1;
            if(rightCol == Map.mapArray[0].length) rightCol--;
 
            //hit target
            if(leftCol==12 && upRow ==24 || leftCol==13 && upRow ==24 || leftCol==12 && upRow ==25 || leftCol==13 && upRow ==25 )
            {
            	return 1;
            }
            else if(middleCol==12 && upRow ==24 || middleCol==13 && upRow ==24 || middleCol==13 && upRow ==25 || middleCol==13 && upRow ==25 )
            {
            	return 1;
            }
            else if(rightCol==12 && upRow ==24 || rightCol==13 && upRow ==24 || rightCol==12 && upRow ==25 || rightCol==13 && upRow ==25 )
            {
            	return 1;
            }
 
            int tillY = upRow*destTile + destTile;
 
            if(y + ySpeed <= tillY) // touched
            {
                int tileXStart = leftCol*destTile + (3*4*destTile);
                int tileXEnd = rightCol*destTile + destTile +(3*4*destTile) ;
 
                int tankXStart = x;
                int tankXMiddle = x+destTile;
                int tankXEnd = x+2*destTile;
 
                int returnValue = 0;
                for(int i = tileXStart ; i< tileXEnd ; i+=destTile) // check the 3 tills
                {
                    if(tankXStart >= i && tankXStart <= i+destTile) //check up block
                    {
                        returnValue= detectCollisionType(upRow,leftCol);
                        if(returnValue!=0)
                        {
                        	y = tillY;
                        	colloided = true;
                        	return returnValue;
                        }
                    }
                    else if(tankXMiddle >= i && tankXMiddle <= i+destTile) // check middle block
                    {
                        returnValue = detectCollisionType(upRow,middleCol);
                        if(returnValue!=0)
                        {
                        	y = tillY;
                        	colloided = true;
                        	return returnValue;
                        }
                    }
                    else if(tankXEnd >= i && tankXEnd <= i+destTile) // check bottom block
                    {
                        returnValue = detectCollisionType(upRow,rightCol);
                        if(returnValue!=0 && tankXStart >= leftCol*destTile+ 3*4*destTile +destTile/3)
                        {
                        	y = tillY;
                        	colloided = true;
                        	return returnValue;
                        }
                    }
                }
            }
 
        }
        else if (dir == 1) // down dir = 1
        {
            int downRow = (y+ySpeed)/destTile +2; //+1;
            if(downRow==Map.mapArray.length) return 0; // hit the wall
 
            int leftCol = (x- 3*4*destTile)/destTile;
            int middleCol = leftCol+1;
            int rightCol = middleCol+1;
            if(rightCol == Map.mapArray[0].length) rightCol--;
 
            //hit target
            if(leftCol==12 && downRow ==24 || leftCol==13 && downRow ==24 || leftCol==12 && downRow ==25 || leftCol==13 && downRow ==25 )
            {
            	return 1;
            }
            else if(middleCol==12 && downRow ==24 || middleCol==13 && downRow ==24 || middleCol==12 && downRow ==25 || middleCol==13 && downRow ==25 )
            {
            	return 1;
            }
            else if(rightCol==12 && downRow ==24 || rightCol==13 && downRow ==24 || rightCol==12 && downRow ==25 || rightCol==13 && downRow ==25 )
            {
            	return 1;
            }
 
            int tillY = downRow*destTile;
 
            if(y + 2* destTile+ ySpeed >= tillY) // touched
            {
                int tileXStart = leftCol*destTile + (3*4*destTile);
                int tileXEnd = rightCol*destTile + destTile +(3*4*destTile) ;
 
                int tankXStart = x;
                int tankXMiddle = x+destTile;
                int tankXEnd = x+2*destTile;
 
                int returnValue = 0;
                for(int i = tileXStart ; i< tileXEnd ; i+=destTile) // check the 3 tills
                {
                    if(tankXStart >= i && tankXStart <= i+destTile) //check up block
                    {
                        returnValue= detectCollisionType(downRow,leftCol);
                        if(returnValue!=0)
                        {
                        	y = tillY-2*destTile;
                        	colloided = true;
                        	return returnValue;
                        }
                    }
                    else if(tankXMiddle >= i && tankXMiddle <= i+destTile) // check middle block
                    {
                        returnValue = detectCollisionType(downRow,middleCol);
                        if(returnValue!=0)
                        {
                        	y = tillY-2*destTile;
                        	colloided = true;
                        	return returnValue;
                        }
                    }
                    else if(tankXEnd >= i && tankXEnd <= i+destTile) // check bottom block
                    {
                        returnValue = detectCollisionType(downRow,rightCol);
                        if(returnValue!=0 && tankXStart >= leftCol*destTile+ 3*4*destTile +destTile/3)
                        {
                        	y = tillY-2*destTile;
                        	colloided = true;
                        	return returnValue;
                        }
                    }
                }
            }
        }
 
 
//      tank collide with a tank 
        int leftX,topY,rightX,downY; // Foreign tank square
        int frontUp,frontDown,frontLeft,frontRight; // determine the front coordinates of the tank according to the direction
        for (int i  =0 ; i < ourSurface.tanksArray.size() ; i ++)
        {
            if (i != tankID)
            {
                leftX=ourSurface.tanksArray.get(i).x;
                rightX = leftX + 2*destTile;
 
                topY=ourSurface.tanksArray.get(i).y;
                downY = topY+2*destTile;
 
 
 
                if(direction== 0 || direction ==2) // Horizontal Collision
                {
                    frontUp = y;
                    frontDown = y+2*destTile;
                    if( (topY >= frontUp && topY <= frontDown)|| (downY>=frontUp && downY <= frontDown) ) // tank in the range to colloide
                    {
                        if(direction == 0) // right
                        {
                            if(x+2*destTile + xSpeed >= leftX  && x+xSpeed <= leftX) // touched
                            {
                                return 1;
                            }
                        }
                        else  // left
                        {
                            if(x+xSpeed <=rightX && x+xSpeed+2*destTile >= rightX) // touched
                            {
                                return 1;    
                            }
                        }
                    }
 
                }
                else if (direction == 1 || direction == 3) // Vertical Collision
                {
                    frontLeft = x;
                    frontRight = x+2*destTile;
 
                    if( (leftX>= frontLeft && leftX <= frontRight)|| (rightX>=frontLeft && rightX <= frontRight) ) // tank in the range to colloide
                    {
                        if(direction == 1) // down
                        {
                            if(y+2*destTile + ySpeed >= topY  && y+2*destTile + ySpeed <= downY) // touched
                            {
                                return 1;
                            }
                        }
                        else  // up
                        {
                            if(y+ySpeed <=downY && y+ySpeed+2*destTile >= downY) // touched
                            {
                                return 1;    
                            }
                        }
                    }
 
                }
 
 
 
            }
        }
 
        return 0;
    }
 
	public int detectCollisionType(int row , int col)
	{
		// hit a brick or a wall
		if (row < 0 || col < 0 ) return 0;
		if(Map.mapArray[row][col]== 1 || Map.mapArray[row][col]== 2 || Map.mapArray[row][col]== 3)	return 1;
		// on snow
		else if(Map.mapArray[row][col]== 5) return 2;
 
		if(tankID == 0)
		{
			for(int i = 0 ; i < 3 ; i++)
			{
				// collided
				if( ourSurface.levelBonus[i][3] == -1 && row == ourSurface.levelBonus[i][1] && col == ourSurface.levelBonus[i][2])
				{
					ourSurface.levelBonus[i][3] = -3;
					break;
				}
				else if(ourSurface.levelBonus[i][3] == -1 &&  row == ourSurface.levelBonus[i][1] && col == ourSurface.levelBonus[i][2]+1) 
				{
					ourSurface.levelBonus[i][3] = -3;
					break;
				}
				else if(ourSurface.levelBonus[i][3] == -1 &&  row == ourSurface.levelBonus[i][1]+1 && col == ourSurface.levelBonus[i][2]) 
				{
					ourSurface.levelBonus[i][3] = -3;
					break;
				}
				else if(ourSurface.levelBonus[i][3] == -1 &&  row == ourSurface.levelBonus[i][1]+1 && col == ourSurface.levelBonus[i][2]+1) 
				{
					ourSurface.levelBonus[i][3] = -3;
					break;
				}
 
			}
		}
 
		return 0;
	}
 
 
 
}
 