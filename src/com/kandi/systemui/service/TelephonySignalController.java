package com.kandi.systemui.service;

import java.util.List;

import android.content.BroadcastReceiver;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

public class TelephonySignalController {


    KandiSystemUiService mService;
    Context mContext;
    private TelephonyManager mPhone;
    
    public TelephonySignalController(KandiSystemUiService service){
        mService = service;
        mContext = service;
        String TAG = "TelephonySignalController";
        PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            	int level = signalStrength.getLevel();
//            	Log.d("MOBLIE SIGNAL", "telephone level:" + level);
            	if(mPhone.getSimState() == TelephonyManager.SIM_STATE_ABSENT  
                        || mPhone.getSimState() == TelephonyManager.SIM_STATE_UNKNOWN){
            		mService.TopRefreshNetworkEvent(level, false);
            	}else{
            		mService.TopRefreshNetworkEvent(level, true);
            	}
            }

			@Override
			public void onDataConnectionStateChanged(int state, int networkType) {
				super.onDataConnectionStateChanged(state, networkType);
				if(mPhone.getSimState() == TelephonyManager.SIM_STATE_ABSENT  
                        || mPhone.getSimState() == TelephonyManager.SIM_STATE_UNKNOWN){
					mService.TopRefreshNetworkLogoEvent(networkType, false);
				}else{
					mService.TopRefreshNetworkLogoEvent(networkType, true);
				}
			}
        };
        if (mPhone == null) { 
            try {
                mPhone = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);;
            } catch (Exception ex) {
                Log.e(TAG, ex.toString());
                return;
            }
            mPhone.listen(mPhoneStateListener,
                    PhoneStateListener.LISTEN_SERVICE_STATE
                  | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                  | PhoneStateListener.LISTEN_CALL_STATE
                  | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                  | PhoneStateListener.LISTEN_DATA_ACTIVITY);
        }
        
    }
}
