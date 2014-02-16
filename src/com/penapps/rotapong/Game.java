package com.penapps.rotapong;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.penapps.rotapong.shapes.Ball;
import com.penapps.rotapong.shapes.Paddle;
import com.penapps.rotapong.util.FloatPair;
import com.penapps.rotapong.web.GameClient;
import com.penapps.rotapong.web.GameServer;
import com.penapps.rotapong.web.GameSocket;

public class Game {
	private static final long FRAME_MILLIS = 17;
	public final Paddle paddle, otherPaddle;
	//public final Paddle paddle;
	
	public final Ball ball;
	public int bounceCount = 0;
	private long prevMillis;
	private GameSocket mSocket;
	
	public Game(InetAddress server, boolean isServer){
		ball = new Ball(0.0f, 0.0f, -8.0f);
		otherPaddle = new Paddle(true, 0.0f, 0.0f, 0.0f);
		paddle = new Paddle(true, 0.0f, 0.0f, 0.0f);
		try {
			mSocket = isServer? new GameServer(server) : new GameClient(server);
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		prevMillis = SystemClock.elapsedRealtime();
	}
	
	public void step()
	{
		try {
			moveBall();
			FloatPair otherPair = mSocket.roundTrip(paddle.x, paddle.y);
			otherPaddle.x = -otherPair.first;
			otherPaddle.y = otherPair.second;
			long nowMillis = SystemClock.elapsedRealtime();
			long diff = nowMillis - prevMillis;
			if (diff < FRAME_MILLIS)
			{
				SystemClock.sleep(FRAME_MILLIS - diff);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void createResources(GL10 gl, Context context)
	{
		ball.createResources(gl, context);
	}
	
	private void moveBall() {
		if (ball.zDir) {
			ball.z += ball.zSpeed;
			if (ball.z >= -6f) {
				checkLose();
				bounceCount++;
				ball.zDir = false;
			}
		} else {
			ball.z -= ball.zSpeed;
			if (ball.z <= -10.0f) {
				bounceCount++;
				ball.zDir = true;
			}
		}
		if (ball.xDir){
			ball.x += ball.xSpeed;
			if (ball.x >= 1.75f)
				ball.xDir = false;
		} else {
			ball.x -= ball.xSpeed;
			if (ball.x <= -1.75f)
				ball.xDir = true;
		}
		if (ball.yDir){
			ball.y += ball.ySpeed;
			if (ball.y >= 1.75f)
				ball.yDir = false;
		} else {
			ball.y -= ball.ySpeed;
			if (ball.y <= -1.75f)
				ball.yDir = true;
		}
		
		if (bounceCount % 5 == 0 && bounceCount != 0){
			//game.ball.xSpeed += (float) Math.pow(2, ((int)(Math.random() * ((8 - 5) + 1) + 5)) * -1);
			//game.ball.ySpeed += (float) Math.pow(2, ((int)(Math.random() * ((8 - 5) + 1) + 5)) * -1);
			//Log.i("TAG", game.ball.xSpeed + " " + game.ball.ySpeed);
		}
		
	}
	
	private void checkLose() {
		if (ball.zDir) {
			if (ball.x + .5f < paddle.x){
				Log.d("TAG", ball.x + ", " + ball.y + " " + paddle.x + ", " + paddle.y);
			}
			else if (ball.x > paddle.x + 1.0f){
				Log.d("TAG", ball.x + ", " + ball.y + " " + paddle.x + ", " + paddle.y);
			}
			else if (ball.y > paddle.y + 1.5f){
				Log.d("TAG", ball.x + ", " + ball.y + " " + paddle.x + ", " + paddle.y);
			}
			else if (ball.y + .1f < paddle.y){
				Log.d("TAG", ball.x + ", " + ball.y + " " + paddle.x + ", " + paddle.y);
			}
			// tell other device if this guy loses.
		}
		return;
	}
	
	public void updatePaddleZ(int newZ) {
		paddle.x = ((int) (newZ / 5)) * 0.1f;
	}

	public void updatePaddleY(int newY) {
		paddle.y = (float) ((newY / 4) * 0.1f);
	}

}
