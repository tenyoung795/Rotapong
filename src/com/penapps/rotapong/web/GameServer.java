package com.penapps.rotapong.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;

import android.util.Log;

import com.penapps.rotapong.util.FloatPair;

public class GameServer implements GameSocket {
	
	public static final int PORT = 40001;
	private static final String TAG = GameServer.class.getSimpleName();
	private DatagramSocket mSocket;
	private long mLastTimeMillis;
	private SocketAddress mClient;
	
	public GameServer(InetAddress address) throws SocketException
	{
		mSocket = new DatagramSocket(PORT, address);
		mLastTimeMillis = 0l;
		mClient = null;
	}

	@Override
	public void send(float x, float y) throws IOException {
		if (mClient == null)
			throw new IllegalStateException("Server has not started communicating with client yet");
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(bytesOut);
		try
		{
			stream.writeLong(System.currentTimeMillis());
			stream.writeFloat(x);
			stream.writeFloat(y);
			stream.flush();
			byte[] bytes = bytesOut.toByteArray();
			mSocket.send(new DatagramPacket(bytes, bytes.length, mClient));
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
		if (mClient == null)
		{
			mClient = packet.getSocketAddress();
		}
		FloatPair opponentPair = new FloatPair(stream.readFloat(), stream.readFloat());
		return opponentPair;
	}

	@Override
	public void close() throws IOException {
		mSocket.close();
	}
	
}
