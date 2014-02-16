package com.penapps.rotapong.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.penapps.rotapong.R;

public class TestUDPActivity extends Activity {
	
	public static final String INFO = "com.penapps.rotapong.INFO";
	private static final int SERVER_PORT = 40001;
	private static final String CLIENT_MSG = "Hello, this is the client.";
	private static final String SERVER_MSG = "Hello, this is the server.";
	private static final int PACKET_SIZE = CLIENT_MSG.getBytes().length;
	
	private WifiP2pInfo info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_udp);
		info = getIntent().getParcelableExtra(INFO);
		
		((TextView)findViewById(R.id.is_server)).setText("I am the "
			+ (info.isGroupOwner? "server" : "client")
		);
	
		if (info.isGroupOwner)
		{
			class Result
			{
				DatagramSocket socket;
				DatagramPacket packet;
			}
			new AsyncTask<Void, Void, Result>() {

				@Override
				protected Result doInBackground(Void... params) {
					Result result = new Result();
					try {
						result.socket = new DatagramSocket(SERVER_PORT, info.groupOwnerAddress);
					} catch (SocketException e) {
						throw new RuntimeException(e);
					}
					try
					{
						byte[] bytes = new byte[PACKET_SIZE];
						result.packet = new DatagramPacket(bytes, PACKET_SIZE);
						result.socket.receive(result.packet);
						return result;
					} catch (IOException e) {
						result.socket.close();
						throw new RuntimeException(e);
					} 
				}
				
				@Override
				protected void onPostExecute(final Result result) {
					onReceived(result.packet);
					new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(Void... params) {
							try
							{
								byte[] bytes = SERVER_MSG.getBytes();
								DatagramPacket packet = new DatagramPacket(bytes, bytes.length, result.packet.getSocketAddress());
								result.socket.send(packet);
								return null;
							} catch (IOException e) {
								throw new RuntimeException(e);
							} finally {
								result.socket.close();
							}
						}
						
						@Override
						protected void onPostExecute(Void result) {
							onSent();
						}
					}.execute();
				}
				
			}.execute();
		}
		else
		{
			new AsyncTask<Void, Void, DatagramSocket>() {

				@Override
				protected DatagramSocket doInBackground(Void... params) {
					DatagramSocket socket;
					try {
						socket = new DatagramSocket();
					} catch (SocketException e) {
						throw new RuntimeException(e);
					}
					try
					{
						byte[] bytes = CLIENT_MSG.getBytes();
						DatagramPacket packet = new DatagramPacket(bytes, bytes.length, info.groupOwnerAddress, SERVER_PORT);
						socket.send(packet);
						return socket;
					} catch (IOException e) {
						socket.close();
						throw new RuntimeException(e);
					}
				}
				
				@Override
				protected void onPostExecute(final DatagramSocket result) {
					onSent();
					new AsyncTask<Void, Void, DatagramPacket>() {

						@Override
						protected DatagramPacket doInBackground(Void... params) {
							try
							{
								byte[] bytes = new byte[PACKET_SIZE];
								DatagramPacket packet = new DatagramPacket(bytes, PACKET_SIZE);
								result.receive(packet);
								return packet;
							} catch (IOException e) {
								throw new RuntimeException(e);
							} finally {
								result.close();
							}
						}
						
						@Override
						protected void onPostExecute(DatagramPacket result) {
							onReceived(result);
						}
					}.execute();
				}
				
			}.execute();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_ud, menu);
		return true;
	}
	
	private void onSent()
	{
		((TextView)findViewById(R.id.sent)).setText("Sent \"" + (info.isGroupOwner? SERVER_MSG : CLIENT_MSG) + '"');
	}
	
	private void onReceived(DatagramPacket result)
	{
		((TextView)findViewById(R.id.received)).setText("Received \"" + new String(result.getData()) + '"');
		
	}
}
