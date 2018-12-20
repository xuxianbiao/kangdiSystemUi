package com.kandi.systemui.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.ContentResolver;
import android.os.Handler;
import android.os.RemoteException;

import com.kandi.systemui.driver.DriverServiceManger;
import com.kandi.systemui.driver.EcocEnergyInfoDriver;

public class BatteryAndTimeController {
    KandiSystemUiService mService;
    Timer mTime;
    TimerTask mTimerTask;
    final int Restart_time=5;//如果检查到服务程序崩溃，5秒后将重启服务
    int TimeOut_Count=0;
    int TimeOut_KdPhone_Count=0;	//音量调节服务运行监测周期
    public BatteryAndTimeController(KandiSystemUiService service) {
        mService = service;
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }

    private void refreshPannel() {
        EcocEnergyInfoDriver model = DriverServiceManger.getInstance().getEcocEnergyInfoDriver();
        //EnergyInfoDriver model = DriverServiceManger.getInstance().getEnergyInfoDriver();

        if(model != null) {
            model.retreveGeneralInfo();
            if (model.getCargingState() == 1) {
                mService.setBetteryLevel(-1);
            }else {
                mService.setBetteryLevel((int)model.getSOC());
            }
            mService.setRemainMileage(model.getRemainMileage());
//            Log.d("BAT", "CargingState:" + model.getCargingState() + ":Bettery:" + model.getSOC() + ":RemainMileage:" + model.getRemainMileage());
        }else {
        	TimeOut_Count++;
        	if(TimeOut_Count>=Restart_time){
	        	mService.setBetteryLevel(0);
	            mService.restartKdService();
//	            Log.e("BAT", "Vwcs is not run!");
	            TimeOut_Count = 0;
        	}
        }
//        TimeOut_KdPhone_Count++;
//        if(TimeOut_KdPhone_Count>=Restart_time){
//            mService.restartKdPhone();
//        	TimeOut_KdPhone_Count = 0;
//        }

    }

    private void refreshTime() {
        try{
        	ContentResolver cv = this.mService.getApplication().getContentResolver();
            String strTimeFormat = android.provider.Settings.System.getString(cv,
                                               android.provider.Settings.System.TIME_12_24);
            if(strTimeFormat != null || !"".equals(strTimeFormat)) {
            	if("24".equals(strTimeFormat)){
            		Date date = new Date();
            		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            		String dateStr = sdf.format(date);
            		mService.setCurrentTime(dateStr);
//            		Log.i("SystemUI time","24_Time"+":"+dateStr);
            	}else{
            		Date date = new Date();
            		SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm:ss");
            		String dateStr = sdf.format(date);
            		mService.setCurrentTime(dateStr);
//            		Log.i("SystemUI time","12_Time"+":"+dateStr);
            	}
            }else{
            	android.provider.Settings.System.putString(cv,android.provider.Settings.System.TIME_12_24,"12");
            }
        }catch(Exception e){
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String dateStr = sdf.format(date);
            mService.setCurrentTime(dateStr);
//            Log.i("SystemUI time","24_Time"+":"+dateStr);
        }
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }

    Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                refreshPannel();
                refreshTime();
                mService.mWifiController.update(mService);
            }
        };
    };
    
    public void removeBatteryAndTimeObserve(){
        mTime.cancel();
    }
}
