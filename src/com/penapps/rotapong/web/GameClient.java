package com.penapps.rotapong.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.penapps.rotapong.shapes.Ball;
import com.penapps.rotapong.util.FloatPair;

public class GameClient implements GameSocket {
	
	private DatagramSocket mSocket;
	private long mLastTimeMillis;
	private InetAddress mServer;
	
	public GameClient(InetAddress server) throws SocketException {
		mSocket = new DatagramSocket();
		mLastTimeMillis = 0l;
		mServer = server;
	}

	@Override
	public void close() throws IOException {
		mSocket.close();
	}
	
	@Override
	public void initBall(Ball b) throws IOException {
		// serve the ball
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(bytes);
		try
		{
			stream.writeLong(0x8000000000000000l
				| (b.zDir? BALL_Z_DIR_BITMASK : 0)
				| (b.yDir? BALL_Y_DIR_BITMASK : 0)
				| (b.xDir? BALL_X_DIR_BITMASK : 0)
			);
			stream.writeFloat(b.xSpeed);
			stream.writeFloat(b.ySpeed);
			mSocket.send(new DatagramPacket(bytes.toByteArray(), bytes.size(), mServer, GameServer.PORT));
		} finally
		{
			stream.close();
		}
		
	}

	@Override
	public void send(float x, float y) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(bytes);
		try
		{
			stream.writeLong(System.currentTimeMillis());
			stream.writeFloat(x);
			stream.writeFloat(y);
			mSocket.send(new DatagramPacket(bytes.toByteArray(), bytes.size(), mServer, GameServer.PORT));
		} finally
		{
			stream.close();
		}
	}

	@Override
	public FloatPair recv() throws IOException {
		byte[] bytes = new byte[PACKET_SIZE_BYTES];
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
		long timestamp = 0l;
		DataInputStream stream;
		do {
			mSocket.receive(packet);
			stream = new DataInputStream(new ByteArrayInputStream(packet.getData()));
			timestamp = stream.readLong();
		} while (timestamp < mLastTimeMillis);
		mLastTimeMillis = timestamp;
		FloatPair opponentPair = new FloatPair(stream.readFloat(), stream.readFloat());
		return opponentPair;
	}

	@Override
	public FloatPair roundTrip(float x, float y) throws IOException {
		FloatPair fp = recv();
		send(x, y);
		return fp;
	}
	
	

}
