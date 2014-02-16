package com.penapps.rotapong;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.os.SystemClock;

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
	
	public Game(GL10 gl, Context context, InetAddress server, boolean isServer){
		ball = new Ball(gl, context, 0.0f, 0.0f, -8.0f);
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
			FloatPair otherPair = mSocket.roundTrip(paddle.x, paddle.y);
			otherPaddle.x = -otherPair.first;
			otherPaddle.y = -otherPair.second;
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
}
