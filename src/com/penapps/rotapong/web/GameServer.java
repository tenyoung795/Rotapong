package com.penapps.rotapong.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

import com.penapps.rotapong.util.FloatPair;

public class GameServer implements GameSocket {
	
	public static final int PORT = 40001;
	private DatagramSocket mSocket;
	private long mLastTimeMillis;
	private SocketAddress mClient;
	
	public GameServer() throws SocketException
	{
		mSocket = new DatagramSocket(PORT);
		mLastTimeMillis = 0l;
		mClient = null;
	}

	@Override
	public void send(float x, float y) throws IOException {
		if (mClient == null)
			throw new IllegalStateException("Server has not started communicating with client yet");
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(new ByteArrayOutputStream());
		try
		{
			stream.writeLong(System.currentTimeMillis());
			stream.writeFloat(x);
			stream.writeFloat(y);
			mSocket.send(new DatagramPacket(bytes.toByteArray(), bytes.size(), mClient));
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
        	if (mClient == null)
        	{
        		mClient = packet.getSocketAddress();
        	}
        	return new FloatPair(stream.readFloat(), stream.readFloat());
        } finally {
        	stream.close();
        }
	}

	@Override
	public void close() throws IOException {
		mSocket.close();
	}
	
}
