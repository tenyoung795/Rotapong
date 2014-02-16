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
	public void send(float x, float y) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(new ByteArrayOutputStream());
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
		ByteArrayInputStream byteInput = new ByteArrayInputStream(bytes);
        DataInputStream stream = new DataInputStream(byteInput);
        try
        {
        	do {
        		mSocket.receive(packet);
        		byteInput.reset();
        		timestamp = stream.readLong();
        	} while (timestamp < mLastTimeMillis);
        	timestamp = mLastTimeMillis;
        	return new FloatPair(stream.readFloat(), stream.readFloat());
        } finally {
        	stream.close();
        }
	}

}
