package com.penapps.rotapong;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.penapps.rotapong.shapes.Ball;
import com.penapps.rotapong.shapes.OtherPaddle;
import com.penapps.rotapong.shapes.Paddle;

public class Game {
	public final OtherPaddle otherPaddle;
	public final Paddle paddle;
	
	public final Ball ball;
	public Game(GL10 gl, Context context){
		Random random = new Random();
		if (random.nextInt(1) == 1)
			ball = new Ball(gl, context, true, 0.0f, 0.0f, -8.0f);
		else
			ball = new Ball(gl, context, false, 0.0f, 0.0f, -8.0f);
		otherPaddle = new OtherPaddle(true, 0.0f, 0.0f, 0.0f);
		paddle = new Paddle(gl, context, true, 0.0f, 0.0f, 0.0f);
	}
}
