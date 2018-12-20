package com.kandi.systemui.service;

import java.lang.reflect.Method;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiController extends BroadcastReceiver {

//    ImageView mImageView;
//    boolean mBluetoothEnabled;
    KandiSystemUiService mService;
    boolean mWifiEnabled, mWifiConnected;
    Context mContext;
    final WifiManager mWifiManager;
    int mWifiRssi, mWifiLevel = 0;
    String mWifiSsid;
    private static int mTotalLevel = 5;
    
    public WifiController(KandiSystemUiService service){
        mService = service;
        mContext = service;
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        addReceiver();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.d("WIFI","wifi:"+intent.getAction());
        final String action = intent.getAction();
        if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            mWifiEnabled = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN) == WifiManager.WIFI_STATE_ENABLED;

        } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            final NetworkInfo networkInfo = (NetworkInfo)
                    intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            boolean wasConnected = mWifiConnected;
            mWifiConnected = networkInfo != null && networkInfo.isConnected();
            // If we just connected, grab the inintial signal strength and ssid
            if (mWifiConnected && !wasConnected) {
                // try getting it out of the intent first
                WifiInfo info = (WifiInfo) intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                if (info == null) {
                    info = mWifiManager.getConnectionInfo();
                }
                if (info != null) {
                    mWifiSsid = huntForSsid(info);
                } else {
                    mWifiSsid = null;
                }
            } else if (!mWifiConnected) {
                mWifiSsid = null;
            }
            // Apparently the wifi level is not stable at this point even if we've just connected to
            // the network; we need to wait for an RSSI_CHANGED_ACTION for that. So let's just set
            // it to 0 for now
            //mWifiLevel = 0;
            //mWifiRssi = -200;
            // support systemui single card
            if (mWifiConnected) {
                WifiInfo wifiInfo = ((WifiManager) mContext.getSystemService(Context.WIFI_SERVICE))
                        .getConnectionInfo();
                if (wifiInfo != null) {
                    int newRssi = wifiInfo.getRssi();
                    int newSignalLevel = WifiManager.calculateSignalLevel(newRssi,
                    		mTotalLevel);
                    // XLog.d(TAG, " RSSI updateWifiState: mWifiLevel = " +
                    // mWifiLevel
                    // + "	newRssi=" + newRssi + " newSignalLevel = "
                    // + newSignalLevel);
                    if (newSignalLevel != mWifiLevel) {
                        mWifiLevel = newSignalLevel;
                    }
                }
            }
        } else if (action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
            if (mWifiConnected) {
                mWifiRssi = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, -200);
                mWifiLevel = WifiManager.calculateSignalLevel(
                        mWifiRssi, mTotalLevel);
            }
        }
        
        
        ////////////set the icon;
        mService.setWifiLevel(mWifiLevel);   
    }
    
    public void update(Context context)
    {
        try {
            WifiManager wifiMg = (WifiManager)context.getSystemService(context.WIFI_SERVICE);
             List<ScanResult> list = wifiMg.getScanResults();
             if(isWifiApEnabled(wifiMg)){
//                 Log.e("WIFI AP"," level is: "+"100");
                 ((KandiSystemUiService)context).setWifiLevel(-10);
            	 return;
             }
            if (list != null && mWifiConnected) {
//                if (list != null) {
//                    for (ScanResult scanResult : list) {
//                        int nSigLevel = WifiManager.calculateSignalLevel(
//                                scanResult.level, mTotalLevel);
//                    }
//                }
                
                WifiInfo wifiInfo = wifiMg.getConnectionInfo();
                int nWSig = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), mTotalLevel);
//                Log.e("WIFI"," level is: "+nWSig);
                ((KandiSystemUiService)context).setWifiLevel(nWSig);
                
            }else
            {
            	 ((KandiSystemUiService)context).setWifiLevel(0);
//                Log.d("WIFI", "DisConnect!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    private String huntForSsid(WifiInfo info) {
        String ssid = info.getSSID();
        if (ssid != null) {
            return ssid;
        }
        // OK, it's not in the connectionInfo; we have to go hunting for it
        List<WifiConfiguration> networks = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration net : networks) {
            if (net.networkId == info.getNetworkId()) {
                return net.SSID;
            }
        }
        return null;
    }
    
    
    public void addReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mService.registerReceiver(this, filter);
    }
    public void removeReceiver() {
        mService.unregisterReceiver(this);
    }
    
	public boolean isWifiApEnabled(WifiManager mWifiManager) {
		try {
			Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
			method.setAccessible(true);
			return (Boolean) method.invoke(mWifiManager);

		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
