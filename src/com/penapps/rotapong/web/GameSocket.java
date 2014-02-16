package com.penapps.rotapong.web;

import java.io.Closeable;
import java.io.IOException;

import com.penapps.rotapong.shapes.Ball;
import com.penapps.rotapong.util.FloatPair;

public interface GameSocket extends Closeable {
	
	public static final int PACKET_SIZE_BYTES = 16;
	public static final long BALL_Z_DIR_BITMASK = 0x4000000000000000l,
			BALL_Y_DIR_BITMASK = 0x2000000000000000l,
			BALL_X_DIR_BITMASK = 0x1000000000000000l;
	
	void send(float x, float y) throws IOException;	
	FloatPair recv() throws IOException;
	
	void initBall(Ball b) throws IOException;
	FloatPair roundTrip(float x, float y) throws IOException;

}