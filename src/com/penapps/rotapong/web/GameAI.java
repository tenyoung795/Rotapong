package com.penapps.rotapong.web;

import java.io.IOException;

import com.penapps.rotapong.shapes.Ball;
import com.penapps.rotapong.util.FloatPair;

public class GameAI implements GameSocket {

	@Override
	public void close() throws IOException {
	}

	@Override
	public void send(float x, float y) throws IOException {
	}

	@Override
	public FloatPair recv() throws IOException {
		return new FloatPair();
	}

	@Override
	public void initBall(Ball b) throws IOException {
	}

	@Override
	public FloatPair roundTrip(float x, float y) throws IOException {
		return recv();
	}

}
