/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.penapps.rotapong;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.penapps.rotapong.util.WifiP2pUtils;

/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 */
public class MainActivity extends Activity implements PeerListListener, ConnectionInfoListener {

	public static final String TAG = MainActivity.class.getSimpleName();

	private static final IntentFilter INTENT_FILTER = new IntentFilter();
	static {
		INTENT_FILTER.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		INTENT_FILTER.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		INTENT_FILTER.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		INTENT_FILTER.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	}

	private RotapongApplication mApp;
	private BroadcastReceiver mReceiver;
	private WifiP2pDevice mOtherDevice;
	private WifiPeerListAdapter mPlayers;
	private ListView mListofPlayersView;
	private TextView mStatusView;
	private ProgressBar mProgressBar;
	private Button mComputerButton, mYesButton, mNoButton, mCancelButton;
	private boolean mWaitingForReply;
	private final WifiP2pConfig mConfig = new WifiP2pConfig();
	private final ActionListener mInviteListener = new ActionListener() {
	
		@Override
		public void onSuccess() {
		}

		@Override
		public void onFailure(int reason) {
			if (mWaitingForReply) {
				mStatusView.setText("Trying again...");
				mApp.connect(mConfig, mInviteListener);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApp = (RotapongApplication)getApplicationContext();
		setContentView(R.layout.main);

		mPlayers = new WifiPeerListAdapter(this, R.layout.row_devices);
		mListofPlayersView = (ListView) findViewById(R.id.list_of_players);
		mListofPlayersView.setAdapter(mPlayers);
		mListofPlayersView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mOtherDevice = (WifiP2pDevice) arg0.getItemAtPosition(arg2);

				mConfig.deviceAddress = mOtherDevice.deviceAddress;
				mConfig.wps.setup = WpsInfo.PBC;
				mApp.connect(mConfig, mInviteListener);
				mWaitingForReply = true;
				mStatusView.setText("Waiting for a reply...");
				mCancelButton.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.VISIBLE);
			}
		});

		mStatusView = (TextView) findViewById(R.id.status);
		mStatusView.setText("Searching for available players...");

		mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
		mProgressBar.setVisibility(View.VISIBLE);

		mComputerButton = (Button) findViewById(R.id.play_computer);
		mComputerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startGame();
			}

		});

		mYesButton = (Button) findViewById(R.id.yes);
		mYesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}

		});

		mNoButton = (Button) findViewById(R.id.no);
		mNoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.cancelConnect();
				mOtherDevice = null;
				mYesButton.setVisibility(View.INVISIBLE);
				v.setVisibility(View.INVISIBLE);
				justListAvailablePlayers();
			}
		});

		mCancelButton = (Button) findViewById(R.id.cancel);
		mCancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.cancelConnect();
				mOtherDevice = null;
				mWaitingForReply = false;
				v.setVisibility(View.INVISIBLE);
				justListAvailablePlayers();
			}
		});

		mWaitingForReply = false;
	}

	/** register the BroadcastReceiver with the intent values to be matched */
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver();
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver();
	}

	private void unregisterReceiver() {
		unregisterReceiver(mReceiver);
	}

	private void registerReceiver() {
		mReceiver = new MenuBroadcastReceiver();
		registerReceiver(mReceiver, INTENT_FILTER);
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		mPlayers.setNotifyOnChange(false);
		mPlayers.clear();
		mPlayers.setNotifyOnChange(true);
		mPlayers.addAll(peers.getDeviceList());

		if (!mWaitingForReply) {
			justListAvailablePlayers();
		}
	}

	private void justListAvailablePlayers() {
		mStatusView.setText("List of available players:");
		mProgressBar.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		if (!info.groupFormed)
			return;

		if (info.isGroupOwner)
			startGame(info);
		else {
			for (int i = mPlayers.getCount() - 1; i >= 0; i--) {
				mOtherDevice = mPlayers.getItem(i);
				if (mOtherDevice.status == WifiP2pDevice.CONNECTED) {
					startGame(info);
					return;
				}
			}
		}
	}	

	private void startGame(WifiP2pInfo info) {
		Log.d(TAG, "Started game!");
		startActivity(
			new Intent(this, GameActivity.class)
				.putExtra(GameActivity.SERVER, info.groupOwnerAddress)
				.putExtra(GameActivity.IS_SERVER, info.isGroupOwner)
		);
	}

	private void startGame() {
		Log.d(TAG, "Started game!");
		startActivity(new Intent(this, GameActivity.class));
	}

	private static class MenuBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			MainActivity activity = (MainActivity)context;
			String action = intent.getAction();
			if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
				int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
				Log.d(MainActivity.TAG, "P2P state changed - "
						+ (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED));
				// switch in and out the proper fragments on change
			} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
				activity.mApp.requestPeers(activity);
				Log.d(MainActivity.TAG, "P2P peers changed");
			} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION
					.equals(action)) {
				NetworkInfo networkInfo = (NetworkInfo) intent
						.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
				if (networkInfo.isConnected()) {
					activity.mApp.requestConnectionInfo(activity);
				}
			} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
					.equals(action)) {
				WifiP2pDevice device = (WifiP2pDevice) intent
						.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
				/* what to do? */
			}
		}

	}

	/**
	 * Array adapter for ListFragment that maintains WifiP2pDevice list.
	 */
	private static class WifiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {

		public WifiPeerListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row_devices, null);
			}
			WifiP2pDevice device = getItem(position);
			if (device != null) {
				TextView top = (TextView) v.findViewById(R.id.device_name);
				TextView bottom = (TextView) v
						.findViewById(R.id.device_details);
				if (top != null) {
					top.setText(device.deviceName);
				}
				if (bottom != null) {
					bottom.setText(WifiP2pUtils.getDeviceStatus(device.status));
				}
			}

			return v;
		}
	}

}
