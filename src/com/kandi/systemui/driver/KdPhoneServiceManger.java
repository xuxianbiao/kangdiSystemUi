package com.kandi.systemui.driver;


import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;

/**
 * @author ivan.lv
 *
 */
public class KdPhoneServiceManger {
	
	private static final String ACTION_PHONE = "com.kd.kdphone.LocalService";

	private static KdPhoneServiceManger instance;
	public static KdPhoneServiceManger getInstance() {
		if (instance == null) {
			instance = new KdPhoneServiceManger();
		}
		return instance;
	}
	private Context context;
	
	public void startKdPhone(Context context) {
		this.context = context;
		if(isKdPhoneRunning()){return;}
		try{
			this.context.startService(new Intent(ACTION_PHONE));
		}catch(Exception e){
			
		}
	}
	
	public boolean isKdPhoneRunning() {
		ActivityManager manager = (ActivityManager) this.context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(30)) {
			String str = service.service.getClassName();
			if ("com.kandi.phone.LocalService".equals(str)) {
				return true;
			}
		}
		return false;
	}
}
