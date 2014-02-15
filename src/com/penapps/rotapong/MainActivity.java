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

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 */
public class MainActivity extends Activity implements ChannelListener, PeerListListener, ConnectionInfoListener {

    public static final String TAG = "MainActivity";
    private WifiP2pManager manager;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
    private WifiP2pDevice thisDevice;
    private WifiPeerListAdapter players;
    private ListView listOfPlayersView;
    
    private final WifiP2pManager.ActionListener listener = new WifiP2pManager.ActionListener() {

    	@Override
    	public void onSuccess() {
        }

        @Override
        public void onFailure(int reasonCode) {
        	manager.discoverPeers(channel, listener);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        players = new WifiPeerListAdapter(this, R.layout.row_devices);
        listOfPlayersView = (ListView)findViewById(R.id.list_of_players);
        listOfPlayersView.setAdapter(players);

        // add necessary intent values to be matched.

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

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
        manager.discoverPeers(channel, listener);
	}
	
	private void unregisterReceiver()
	{
		unregisterReceiver(receiver);
	}
	
	private void registerReceiver()
	{
		receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
	}
	
	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		players.setNotifyOnChange(false);
		players.clear();
		players.setNotifyOnChange(true);
		players.addAll(peers.getDeviceList());
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
	}
	
	public void updateThisDevice(WifiP2pDevice device) {
		thisDevice = device;
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
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_devices, null);
            }
            WifiP2pDevice device = getItem(position);
            if (device != null) {
                TextView top = (TextView) v.findViewById(R.id.device_name);
                TextView bottom = (TextView) v.findViewById(R.id.device_details);
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
}
