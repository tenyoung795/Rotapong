package com.penapps.rotapong;

import android.app.Application;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;

public class RotapongApplication extends Application implements ChannelListener {

	private WifiP2pManager mManager;
	private Channel mChannel;
	private final ActionListener mDiscoverListener = new ActionListener() {

		@Override
		public void onSuccess() {
		}

		@Override
		public void onFailure(int reason) {
			discoverPeers();
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		initChannel();
	}

	@Override
	public void onChannelDisconnected() {
		initChannel();
	}
	
	public void requestPeers(PeerListListener listener) {
		mManager.requestPeers(mChannel, listener);
	}

	public void requestConnectionInfo(ConnectionInfoListener listener) {
		mManager.requestConnectionInfo(mChannel, listener);
	}

	public void connect(WifiP2pConfig config, ActionListener listener) {
		mManager.connect(mChannel, config, listener);
	}

	public void cancelConnect() {
		mManager.cancelConnect(mChannel, null);
	}

	public void initChannel() {
		mChannel = mManager.initialize(this, getMainLooper(), this);
		discoverPeers();
	}

	private void discoverPeers() {
		mManager.discoverPeers(mChannel, mDiscoverListener);
	}

}
