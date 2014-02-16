package com.penapps.rotapong.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.penapps.rotapong.R;
import com.penapps.rotapong.util.FloatPair;
import com.penapps.rotapong.web.GameServer;

public class TestServerActivity extends Activity {
	
	private static final String TAG = TestServerActivity.class.getSimpleName();
	public static final String SERVER = "com.penapps.rotapong.SERVER";
	private static final long FRAME_MILLIS = 17;
	private GameServer mServer;
	private Handler mHandler;
	private TextView mSendingView, mReceivingView;
	private PlayerThread mPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_udp);
		mSendingView = (TextView)findViewById(R.id.sending);
		mReceivingView = (TextView)findViewById(R.id.receiving);
		mHandler = new Handler();
		try {
			mServer = new GameServer((InetAddress)getIntent().getSerializableExtra(SERVER));
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		mPlayer = new PlayerThread();
		mPlayer.start();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		mPlayer.kill();
		try {
			mServer.close();
		} catch (IOException e) {
			Log.e(TAG, "Couldn't close server", e);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_server, menu);
		return true;
	}
	
	private class PlayerThread extends Thread {

		boolean isRunning = true;
                
		@Override
		public void run()
		{
			Random random = new Random();
			long prevMillis = SystemClock.elapsedRealtime();
			while (isRunning)
			{
				try {
					final FloatPair opponentPair = mServer.recv();
					Log.d(TAG, "opponentPair: " + opponentPair);
					final float x = random.nextFloat(), y = random.nextFloat();
					mServer.send(x, y);
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							mSendingView.setText("Sending " + new FloatPair(x, y));
							mReceivingView.setText("Receiving " + opponentPair);
						}
					});
					long nowMillis = SystemClock.elapsedRealtime();
					long diff = nowMillis - prevMillis;
					if (diff < FRAME_MILLIS)
					{
						SystemClock.sleep(FRAME_MILLIS - diff);
					}
					prevMillis = nowMillis;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

		public void kill()
		{
			isRunning = false;
		}
	};

}
