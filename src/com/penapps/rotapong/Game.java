package com.penapps.rotapong;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.penapps.rotapong.shapes.Ball;
import com.penapps.rotapong.shapes.Paddle;
import com.penapps.rotapong.shapes.Paddle;

public class Game {
	public final Paddle paddle, otherPaddle;
	//public final Paddle paddle;
	
	public final Ball ball;
	public int bounceCount = 0;
	public Game(GL10 gl, Context context){
		ball = new Ball(gl, context, 0.0f, 0.0f, -8.0f);
		otherPaddle = new Paddle(true, 0.0f, 0.0f, 0.0f);
		paddle = new Paddle(true, 0.0f, 0.0f, 0.0f);
	}
}
