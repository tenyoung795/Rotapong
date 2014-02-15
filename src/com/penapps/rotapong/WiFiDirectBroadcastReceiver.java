package com.penapps.rotapong;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver{
	private WifiP2pManager mManager;
    private Channel mChannel;
    private MainActivity mActivity;
    PeerListListener peerListListener;
    
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
            MainActivity activity, PeerListListener peerListListener) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        this.peerListListener = peerListListener;
    }
    
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.d("wtf", "Wifi P2P is enabled");
            } else {
            	Log.d("wtf", "Wifi P2P is NOT enabled");
            }
        	
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
        	// request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
                mManager.requestPeers(mChannel, peerListListener);
            }
        	
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
        	if (mManager == null) {
                return;
            }
        	
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        	
        }
	}

}
