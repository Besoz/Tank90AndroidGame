package com.avalanche.movingsprite;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class Transient extends Activity  {
	TextView t;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.firing);
		t = (TextView) findViewById(R.id.textView1);
		Typeface tf=Typeface.createFromAsset(getAssets(), "fonts/stencil.ttf");
		t.setTypeface(tf);
		final Intent game = new Intent("android.intent.action.GAME");
		Thread timer = new Thread(){
			public void run()
			{
				try{
					sleep(1000);
				}
				catch(InterruptedException e){
					e.printStackTrace();
				}
				finally{
					startActivity(game);
				}
			}
		};
		
		timer.start();
		
	}

	

}
