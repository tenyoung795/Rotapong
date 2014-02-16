package com.penapps.rotapong.web;

import java.io.Closeable;
import java.io.IOException;

import com.penapps.rotapong.util.FloatPair;

public interface GameSocket extends Closeable {
	
	public static final int PACKET_SIZE_BYTES = 16;
	
	void send(float x, float y) throws IOException;	
	FloatPair recv() throws IOException;

}