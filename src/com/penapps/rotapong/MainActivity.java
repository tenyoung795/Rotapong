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

import com.penapps.rotapong.test.TestClientActivity;
import com.penapps.rotapong.test.TestServerActivity;

/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 */
public class MainActivity extends Activity implements ChannelListener,
		PeerListListener, ConnectionInfoListener {

	public static final String TAG = "MainActivity";
	private WifiP2pManager manager;

	private final IntentFilter intentFilter = new IntentFilter();
	private Channel channel;
	private BroadcastReceiver receiver = null;
	private WifiP2pDevice otherDevice;
	private WifiPeerListAdapter players;
	private ListView listOfPlayersView;
	private TextView statusView;
	private ProgressBar progressBar;
	private Button yesButton, noButton, cancelButton;
	private boolean waitingForReply;
	private WifiP2pConfig config;
	private final ActionListener channelListener = new ActionListener() {

		@Override
		public void onSuccess() {
		}

		@Override
		public void onFailure(int reason) {
			manager.discoverPeers(channel, channelListener);
		}
	}, inviteListener = new ActionListener() {

		@Override
		public void onFailure(int reason) {
			if (waitingForReply) {
				statusView.setText("Trying again...");
				manager.connect(channel, config, inviteListener);
			}
		}

		@Override
		public void onSuccess() {
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		players = new WifiPeerListAdapter(this, R.layout.row_devices);
		listOfPlayersView = (ListView) findViewById(R.id.list_of_players);
		listOfPlayersView.setAdapter(players);
		listOfPlayersView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				otherDevice = (WifiP2pDevice) arg0.getItemAtPosition(arg2);

				config = new WifiP2pConfig();
				config.deviceAddress = otherDevice.deviceAddress;
				config.wps.setup = WpsInfo.PBC;
				manager.connect(channel, config, inviteListener);
				waitingForReply = true;
				statusView.setText("Waiting for a reply...");
				cancelButton.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.VISIBLE);
			}
		});

		statusView = (TextView) findViewById(R.id.status);
		statusView.setText("Searching for available players...");

		progressBar = (ProgressBar) findViewById(R.id.progress_bar);
		progressBar.setVisibility(View.VISIBLE);

		yesButton = (Button) findViewById(R.id.yes);
		yesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}

		});

		noButton = (Button) findViewById(R.id.no);
		noButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				manager.cancelConnect(channel, null);
				otherDevice = null;
				yesButton.setVisibility(View.INVISIBLE);
				v.setVisibility(View.INVISIBLE);
				justListAvailablePlayers();
			}
		});

		cancelButton = (Button) findViewById(R.id.cancel);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				manager.cancelConnect(channel, null);
				otherDevice = null;
				waitingForReply = false;
				v.setVisibility(View.INVISIBLE);
				justListAvailablePlayers();
			}
		});

		waitingForReply = false;

		// add necessary intent values to be matched.

		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		initChannel();
	}

	/** register the BroadcastReceiver with the intent values to be matched */
	@Override
	public void onResume() {
		super.onResume();
		registerReceiver();
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver();
	}

	@Override
	public void onChannelDisconnected() {
		unregisterReceiver();
		initChannel();
		registerReceiver();
	}

	private void initChannel() {
		channel = manager.initialize(this, getMainLooper(), null);
		manager.discoverPeers(channel, channelListener);
	}

	private void unregisterReceiver() {
		unregisterReceiver(receiver);
	}

	private void registerReceiver() {
		receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
		registerReceiver(receiver, intentFilter);
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		players.setNotifyOnChange(false);
		players.clear();
		players.setNotifyOnChange(true);
		players.addAll(peers.getDeviceList());

		if (!waitingForReply) {
			justListAvailablePlayers();
		}
	}

	private void justListAvailablePlayers() {
		statusView.setText("List of available players:");
		progressBar.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		if (!info.groupFormed)
			return;

		if (info.isGroupOwner)
			startGame(info);
		else {
			for (int i = players.getCount() - 1; i >= 0; i--) {
				otherDevice = players.getItem(i);
				if (otherDevice.status == WifiP2pDevice.CONNECTED) {
					startGame(info);
					return;
				}
			}
		}
	}

	public void updateThisDevice(WifiP2pDevice device) {
	}

	/**
	 * Array adapter for ListFragment that maintains WifiP2pDevice list.
	 */
	private class WifiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {

		public WifiPeerListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
					bottom.setText(getDeviceStatus(device.status));
				}
			}

			return v;
		}
	}

	private static String getDeviceStatus(int deviceStatus) {
		switch (deviceStatus) {
		case WifiP2pDevice.AVAILABLE:
			return "Available";
		case WifiP2pDevice.INVITED:
			return "Invited";
		case WifiP2pDevice.CONNECTED:
			return "Connected";
		case WifiP2pDevice.FAILED:
			return "Failed";
		case WifiP2pDevice.UNAVAILABLE:
			return "Unavailable";
		default:
			return "Unknown";
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
}
