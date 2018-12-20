package com.kandi.systemui.service;

import java.math.BigDecimal;
import java.util.Locale;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kandi.systemui.R;
import com.kandi.systemui.bean.SystemInfo;
import com.kandi.systemui.driver.DriverServiceManger;
import com.kandi.systemui.driver.KdPhoneServiceManger;
import com.kandi.widget.PassThroughButton;

public class KandiSystemUiService extends Service{

	final boolean FloatOrSystemUI = false;	//false琛ㄧず宸ヤ綔鍦╯ystemui妯″紡锛宼rue琛ㄧず宸ヤ綔鍦ㄦ诞绐楄皟璇曟ā寮�
    FrameLayout mFloatLayout;
    FrameLayout mBottomFloatLayout;
    WindowManager.LayoutParams wmParams;
    WindowManager mWindowManager;
    BluetoothController mBluetoothController;
    BatteryAndTimeController mBatteryAndTimeController;
    WifiController mWifiController;
    TelephonySignalController mTelephonySignalController;

    private ImageButton topInternetBtn;
    private ImageButton topPowerBtn;
    private ImageButton topCameraBtn;
    private ImageButton topPhoneBtn;

    private PassThroughButton top_opertate_loaction_btn;
    private PassThroughButton top_opertate_music_btn;
    private PassThroughButton top_opertate_middle_image;

    private TextView status_bar_time_textview;
    private ImageView status_bar_battery_imageView;
    private ImageButton status_bar_3g_type_btn;
    private ImageButton status_bar_3g_level_btn;
    private ImageButton status_bar_wifi_btn;
    private ImageButton status_bar_wifiap_btn;
    private TextView status_bar_remain_textview;
    private ImageView status_bar_bluetooth_image;
    //private ImageView status_bar_bluetooth_phone_image;
    private RelativeLayout topbluebg;
    //private TelephonyManager Tel;
    //private MyPhoneStateListener MyListener;
    private ImageView imageButton_service;
    private ImageView imageButton_voice;
    private ImageView imageButton_set;
    private ImageView imageButton_air;
//    private ImageView imageButton_navigator;
//    private FrameLayout centerview;
    Handler mHandler;
    private TranslateAnimation downAnimation = new TranslateAnimation(-0, 0, 0, 60);
    private TranslateAnimation upAnimation = new TranslateAnimation(0, 0, 60, 0);
    private boolean isPhoneCalling = false;
    private final double MILEUTIL = 1.609344; // 鑻遍噷-鍏噷鎹㈢畻鍗曚綅
    
    public void TopMenuServiceHideEvent() {
    }
    
    private boolean isBluetoothEnableFlag = false;
    public void setBluetoothState(boolean isBluetoothEnable) {
        if (isBluetoothEnable) {
            status_bar_bluetooth_image.setImageResource(R.drawable.status_bar_bluetooth_normal);
        } else {
            status_bar_bluetooth_image.setImageResource(R.drawable.status_bar_bluetooth_unuse);
            topbluebg.setBackgroundResource(R.drawable.home_top_menu_normal);
        }
        isBluetoothEnableFlag = isBluetoothEnable;
    }

    public void setBluetoothPhoneState(boolean isBluetoothPhoneEnable) {
        isPhoneCalling = isBluetoothPhoneEnable;
        if (isPhoneCalling) {
            topbluebg.setBackgroundResource(R.drawable.home_top_menu_normal_calling);
        } else {
            topbluebg.setBackgroundResource(R.drawable.home_top_menu_normal);
        }
    }

    public void TopRefreshNetworkEvent(int asu, boolean state) {
    	if(state){
    		if (asu == 0) {
    			// level = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
    			status_bar_3g_level_btn.setImageResource(R.drawable.home_top_btn6_01); // 鏂綉
    		} else if (asu == 4) {
    			// level = SIGNAL_STRENGTH_GREAT;
    			status_bar_3g_level_btn.setImageResource(R.drawable.home_top_btn6_04);
    		} else if (asu == 3) {
    			// level = SIGNAL_STRENGTH_GOOD;
    			status_bar_3g_level_btn.setImageResource(R.drawable.home_top_btn6_03);
    		} else if (asu == 2) {
    			// level = SIGNAL_STRENGTH_MODERATE;
    			status_bar_3g_level_btn.setImageResource(R.drawable.home_top_btn6_02);
    		} else {
    			// level = SIGNAL_STRENGTH_POOR;
    			status_bar_3g_level_btn.setImageResource(R.drawable.home_top_btn6_01);
    		}
    	}else{
    		status_bar_3g_level_btn.setImageResource(R.drawable.home_top_btn6_01);
    	}
    }
    
    public void TopRefreshNetworkLogoEvent(int type, boolean state) {
        if(state){
        	switch (type) {
        	case TelephonyManager.NETWORK_TYPE_GPRS:
        	case TelephonyManager.NETWORK_TYPE_EDGE:
        	case TelephonyManager.NETWORK_TYPE_CDMA:
        	case TelephonyManager.NETWORK_TYPE_1xRTT:
        	case TelephonyManager.NETWORK_TYPE_IDEN:
        		status_bar_3g_type_btn.setImageResource(R.drawable.home_top_gsm_e2icon);
        		break;
        	case TelephonyManager.NETWORK_TYPE_UMTS:
        	case TelephonyManager.NETWORK_TYPE_EVDO_0:
        	case TelephonyManager.NETWORK_TYPE_EVDO_A:
        	case TelephonyManager.NETWORK_TYPE_HSDPA:
        	case TelephonyManager.NETWORK_TYPE_HSUPA:
        	case TelephonyManager.NETWORK_TYPE_HSPA:
        	case TelephonyManager.NETWORK_TYPE_EVDO_B:
        	case TelephonyManager.NETWORK_TYPE_EHRPD:
        	case TelephonyManager.NETWORK_TYPE_HSPAP:
        		status_bar_3g_type_btn.setImageResource(R.drawable.home_top_gsm_g3icon);
        		break;
        	case TelephonyManager.NETWORK_TYPE_LTE:
        		status_bar_3g_type_btn.setImageResource(R.drawable.home_top_gsm_g4icon);
        		break;
        	default:
        		status_bar_3g_type_btn.setImageResource(R.drawable.home_top_gsm_e2icon);
        	}
        }else{
        	status_bar_3g_type_btn.setImageResource(R.drawable.home_top_gsm_xicon);
        }
    }

    public void setBetteryLevel(int level) {
//        if (level >= 90) {
//            status_bar_battery_imageView.setImageResource(R.drawable.home_top_btn1_06);
//        } else if (level >= 70) {
//            status_bar_battery_imageView.setImageResource(R.drawable.home_top_btn1_05);
//        } else if (level >= 50) {
//            status_bar_battery_imageView.setImageResource(R.drawable.home_top_btn1_04);
//        } else if (level >= 30) {
//            status_bar_battery_imageView.setImageResource(R.drawable.home_top_btn1_03);
//        } else if (level >= 10) {
//            status_bar_battery_imageView.setImageResource(R.drawable.home_top_btn1_02);
//        } else if (level >= 0) {
//            status_bar_battery_imageView.setImageResource(R.drawable.home_top_btn1_01);
//        } else {
//            status_bar_battery_imageView.setImageResource(R.drawable.home_top_btn1_00); // 鍏呯數涓�
//        }
        
    	if(level==-1){//鍏呯數鐘舵��
    		status_bar_battery_imageView.setImageResource(R.drawable.home_top_btn1_00);
    		return;
    	}
    	
		if(level<10){
			status_bar_battery_imageView.setImageResource(R.drawable.home_top_btn1_01);
		}else if(level>=10&&level<25){
			status_bar_battery_imageView.setImageResource(R.drawable.home_top_btn1_02);
		}else if(level>=25&&level<50){
			status_bar_battery_imageView.setImageResource(R.drawable.home_top_btn1_03);
		}else if(level>=50&&level<75){
			status_bar_battery_imageView.setImageResource(R.drawable.home_top_btn1_04);
		}else if(level>=75&&level<90){
			status_bar_battery_imageView.setImageResource(R.drawable.home_top_btn1_05);
		}else{
			status_bar_battery_imageView.setImageResource(R.drawable.home_top_btn1_06);
		}
    }

    public void setRemainMileage(int m) {
    	// 鑾峰彇绯荤粺璇█锛屽鏋滄槸鑻辨枃鍒欐崲绠楁垚鑻遍噷锛�1鑻遍噷=1.609344鍏噷
		if(Locale.getDefault().getLanguage().equals("en")) { // 濡傛灉鏄嫳鏂囷紝鍒欏洓鑸嶄簲鍏ユ崲绠�
			double mi = m/MILEUTIL;
			m = new BigDecimal(mi).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		}
        status_bar_remain_textview.setText(m + getResources().getString(R.string.main_status_mile));
    }

    public void setCurrentTime(String string) {
        status_bar_time_textview.setText(string);
    }
    //濡傛灉淇″彿绛夌骇绛変簬-10锛岃〃绀哄伐浣滃湪AP妯″紡涓�
    public void setWifiLevel(int level) {
    	final int AP_Level = -10;
    	if(AP_Level==level){
    		status_bar_wifi_btn.setImageResource(R.drawable.home_top_btn5_05);
    		status_bar_wifiap_btn.setVisibility(View.VISIBLE);
    		return;
    	}else{
    		status_bar_wifiap_btn.setVisibility(View.GONE);
    	}
        switch (level) {
        // 濡傛灉鏀跺埌姝ｇ‘鐨勬秷鎭氨鑾峰彇WifiInfo锛屾敼鍙樺浘鐗囧苟鏄剧ず淇″彿寮哄害
        case 4:
            status_bar_wifi_btn.setImageResource(R.drawable.home_top_btn5_05);
            // Toast.makeText(MainActivity.this,
            // "淇″彿寮哄害锛�" + level + " 淇″彿鏈�濂�", Toast.LENGTH_SHORT)
            // .show();
            break;
        case 3:
            status_bar_wifi_btn.setImageResource(R.drawable.home_top_btn5_04);
            // Toast.makeText(MainActivity.this,
            // "淇″彿寮哄害锛�" + level + " 淇″彿杈冨ソ", Toast.LENGTH_SHORT)
            // .show();
            break;
        case 2:
            status_bar_wifi_btn.setImageResource(R.drawable.home_top_btn5_03);
            // Toast.makeText(MainActivity.this,
            // "淇″彿寮哄害锛�" + level + " 淇″彿涓�鑸�", Toast.LENGTH_SHORT)
            // .show();
            break;
        case 1:
            status_bar_wifi_btn.setImageResource(R.drawable.home_top_btn5_02);
            // Toast.makeText(MainActivity.this,
            // "淇″彿寮哄害锛�" + level + " 淇″彿杈冨樊", Toast.LENGTH_SHORT)
            // .show();
            break;
        case 0:
            status_bar_wifi_btn.setImageResource(R.drawable.home_top_btn5_06);
            // Toast.makeText(MainActivity.this,
            // "淇″彿寮哄害锛�" + level + " 鏃犱俊鍙�", Toast.LENGTH_SHORT)
            // .show();
            break;
        default:
            // 浠ラ槻涓囦竴
            status_bar_wifi_btn.setImageResource(R.drawable.home_top_btn5_06);
            // Toast.makeText(MainActivity.this, "鏃犱俊鍙�",
            // Toast.LENGTH_SHORT).show();
        }

    }

    class StatusEvent {
        int soc;
        int mileage;
        boolean bluetooth;
        int wifiLevel;
        String sTime;
        String networkType;
    }

    public void TopStatusBarEvent(StatusEvent event) {

        // battery
        setBetteryLevel(event.soc);

        // mileage
        int mile = event.mileage;
        // 鑾峰彇绯荤粺璇█锛屽鏋滄槸鑻辨枃鍒欐崲绠楁垚鑻遍噷锛�1鑻遍噷=1.609344鍏噷
 		if(Locale.getDefault().getLanguage().equals("en")) { // 濡傛灉鏄嫳鏂囷紝鍒欏洓鑸嶄簲鍏ユ崲绠�
 			double mi = mile/MILEUTIL;
 			mile = new BigDecimal(mi).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
 		}
        status_bar_remain_textview.setText(Integer.toString(mile) + getResources().getString(R.string.main_status_mile));

        // bluetooth
        setBluetoothState(event.bluetooth);

        // wifi
        setWifiLevel(event.wifiLevel);

        // time
        status_bar_time_textview.setText(event.sTime);
    }

    private BaseReceiver baseReceiver;
    private Locale mLocale;
    
    @Override
    public void onCreate() {
        super.onCreate();
        /*
         * TODO rwei
         * EventBus.getDefault().register(this,"TopMenuServiceHideEvent"
         * ,TopMenuServiceHideEvent.class);
         */
        mLocale = getApplication().getResources().getConfiguration().locale;
        DriverServiceManger.getInstance().startService(this);
        KdPhoneServiceManger.getInstance().startKdPhone(this);
        createFloatView();
        createBottomFloatViewUnder();
        createBottomFloatView();
        initHeader();
        
		// USB State Events
        baseReceiver = new BaseReceiver();
		IntentFilter storageIntentFilter = new IntentFilter();
		storageIntentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
		storageIntentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		storageIntentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		storageIntentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		storageIntentFilter.addDataScheme("file");

		registerReceiver(baseReceiver, storageIntentFilter);
		mHandler.sendEmptyMessageDelayed(1000, 3000);
    }
    
    /**
	 * 鍩虹被骞挎挱鎺ユ敹
	 * */
	private class BaseReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// U鐩樻彃鍏ヤ簨浠�
			if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)
					|| intent.getAction().equals(Intent.ACTION_MEDIA_CHECKING)) {
				// USB_STATE_ON;
				Toast.makeText(context, getString(R.string.usb_in),
						Toast.LENGTH_SHORT).show();
			}
			// U鐩樻嫈鍑轰簨浠�
			else if (intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)
					|| intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED)) {
				// USB_STATE_OFF;
				Toast.makeText(context, getString(R.string.usb_out),
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        /*LayoutParams.TYPE_PHONE 鐢ㄤ簬娴獥鏂瑰紡璋冭瘯锛孡ayoutParams.TYPE_STATUS_BAR鐢ㄤ簬绯荤粺UI鏂瑰紡鍙戝竷*/
        if(FloatOrSystemUI){
        	wmParams.type = LayoutParams.TYPE_PHONE;
        }else{
        	wmParams.type = LayoutParams.TYPE_STATUS_BAR;
        }
        
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;

        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(this);
        mFloatLayout = (FrameLayout) inflater.inflate(R.layout.home_top_layout, null);
        mWindowManager.addView(mFloatLayout, wmParams);

        topInternetBtn = (ImageButton) mFloatLayout.findViewById(R.id.second_internet_btn);
        topPowerBtn = (ImageButton) mFloatLayout.findViewById(R.id.second_power_btn);
        topCameraBtn = (ImageButton) mFloatLayout.findViewById(R.id.second_canmara_btn);
        topPhoneBtn = (ImageButton) mFloatLayout.findViewById(R.id.second_phone_btn);

        status_bar_time_textview = (TextView) mFloatLayout.findViewById(R.id.status_bar_time_textview);

        top_opertate_loaction_btn = (PassThroughButton) mFloatLayout.findViewById(R.id.top_opertate_loaction_btn);
        top_opertate_music_btn = (PassThroughButton) mFloatLayout.findViewById(R.id.top_opertate_music_btn);
        top_opertate_middle_image = (PassThroughButton) mFloatLayout.findViewById(R.id.top_opertate_middle_image);
        //美版屏蔽
//        if((SystemInfo.getInstance().getProperty("ro.product.model")).endsWith("K17")){
//        	top_opertate_middle_image.setBackgroundResource(R.drawable.top_operate_middle_icon_selector);
//        }else{
//        	top_opertate_middle_image.setBackgroundResource(R.drawable.top_operate_middle_icon_global_selector);
//        }
        
        status_bar_battery_imageView = (ImageView) mFloatLayout.findViewById(R.id.status_bar_battery_image);
        status_bar_3g_type_btn = (ImageButton) mFloatLayout.findViewById(R.id.status_bar_3g_type_btn);
        status_bar_3g_level_btn = (ImageButton) mFloatLayout.findViewById(R.id.status_bar_3g_level_btn);
        status_bar_wifi_btn = (ImageButton) mFloatLayout.findViewById(R.id.status_bar_wifi_btn);
        status_bar_wifiap_btn = (ImageButton) mFloatLayout.findViewById(R.id.status_bar_wifiap_btn);
        status_bar_remain_textview = (TextView) mFloatLayout.findViewById(R.id.status_bar_Remain_textview);
        status_bar_bluetooth_image = (ImageView) mFloatLayout.findViewById(R.id.status_bar_bluetooth_image);

        topbluebg = (RelativeLayout) mFloatLayout.findViewById(R.id.topbluebg);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        topInternetBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent me) {
                if (me.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isPhoneCalling) {
                        topbluebg.setBackgroundResource(R.drawable.home_top_menu_web_calling);
                    } else
                        topbluebg.setBackgroundResource(R.drawable.home_top_menu_web);
                } else if (me.getAction() == MotionEvent.ACTION_UP) {
                    if (isPhoneCalling) {
                        topbluebg.setBackgroundResource(R.drawable.home_top_menu_normal_calling);
                    } else
                        topbluebg.setBackgroundResource(R.drawable.home_top_menu_normal);

                    if (isActivityAtTop("WebActivity")) {
                        gotoHome();
                        return true;
                    }
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.kandi.home", "com.kandi.view.WebActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        startActivity(intent);
                        SetBackGround(false);
                    } catch (Exception e) {
                        Log.e("KandiSystemUiService", e.toString());
                    }
                }
                return true;
            }
        });

        topPowerBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent me) {
                if (me.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isPhoneCalling) {
                        topbluebg.setBackgroundResource(R.drawable.home_top_menu_energy_calling);
                    } else
                        topbluebg.setBackgroundResource(R.drawable.home_top_menu_energy);
                } else if (me.getAction() == MotionEvent.ACTION_UP) {
                    if (isPhoneCalling) {
                        topbluebg.setBackgroundResource(R.drawable.home_top_menu_normal_calling);
                    } else 
                        topbluebg.setBackgroundResource(R.drawable.home_top_menu_normal);
                    if (isActivityAtTop("PowerDetailActivity")) {
                        gotoHome();
                        return true;
                    }
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.kandi.home", "com.kandi.view.PowerDetailActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("nBattIndex",0);
                    try {
                        startActivity(intent);
                        SetBackGround(false);
                    } catch (Exception e) {
                        Log.e("KandiSystemUiService", e.toString());
                    }
                }
                return true;
            }
        });

        // topPowerBtn.setOnClickListener(new View.OnClickListener() {
        // @Override
        // public void onClick(View arg0) {
        // showTopBlueBg(1);
        // Toast.makeText(MainTopMenuService.this, "power",
        // Toast.LENGTH_SHORT).show();
        // EventBus.getDefault().postSticky(new TopMenuPowerClickEvent());
        // }
        // });

        topCameraBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent me) {
                if (me.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isPhoneCalling) {
                        topbluebg.setBackgroundResource(R.drawable.home_top_menu_camera_calling);
                    } else
                        topbluebg.setBackgroundResource(R.drawable.home_top_menu_camera);
                } else if (me.getAction() == MotionEvent.ACTION_UP) {
                    if (isPhoneCalling) {
                        topbluebg.setBackgroundResource(R.drawable.home_top_menu_normal_calling);
                    } else 
                        topbluebg.setBackgroundResource(R.drawable.home_top_menu_normal);
                    if (isActivityAtTop("EntertainmentFragmentVideoActivity")) {
//                    if (isActivityAtTop("CameraActivity")) {
                        gotoHome();
                        return true;
                    }
                    Intent intent = new Intent();
//                    intent.setComponent(new ComponentName("com.kandi.home", "com.kandi.view.CameraActivity"));
                    intent.setComponent(new ComponentName("com.kandi.home", "com.kandi.view.EntertainmentFragmentVideoActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        startActivity(intent);
                        SetBackGround(false);
                    } catch (Exception e) {
                        Log.e("KandiSystemUiService", e.toString());
                    }
                }
                return true;
            }
        });

        topPhoneBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent me) {
                if (me.getAction() == MotionEvent.ACTION_DOWN) {
                    topbluebg.setBackgroundResource(R.drawable.home_top_menu_phone);
                } else if (me.getAction() == MotionEvent.ACTION_UP) {
                    if (isPhoneCalling) {
                        topbluebg.setBackgroundResource(R.drawable.home_top_menu_normal_calling);
                    } else 
                        topbluebg.setBackgroundResource(R.drawable.home_top_menu_normal);
                    if (isActivityAtTop("DialActivity")) {
                        gotoHome();
                        return true;
                    }
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.kandi.home", "com.kandi.view.DialActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        startActivity(intent);
                        SetBackGround(false);
                    } catch (Exception e) {
                        Log.e("KandiSystemUiService", e.toString());
                    }
                }
                return true;
            }
        });
        top_opertate_loaction_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isActivityAtTop("RadioActivity")) {
                    gotoHome();
                    return;
                }
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.kandi.home", "com.kandi.view.RadioActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(intent);
                    SetBackGround(false);
                } catch (Exception e) {
                    Log.e("KandiSystemUiService", e.toString());
                }

            }
        });

        top_opertate_music_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isActivityAtTop("EntertainmentFragmentActivity")) {
                    gotoHome();
                    return;
                }
                Intent intent = new Intent();
                intent.setComponent(
                        new ComponentName("com.kandi.home", "com.kandi.view.EntertainmentFragmentActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(intent);
                    SetBackGround(false);
                } catch (Exception e) {
                    Log.e("KandiSystemUiService", e.toString());
                }
            }
        });

        top_opertate_middle_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.kandi.home", "com.kandi.view.MainActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(intent);
                    SetBackGround(false);
                } catch (Exception e) {
                    Log.e("KandiSystemUiService", e.toString());
                }
            }
        });
        
        if (isBluetoothEnableFlag) {
            status_bar_bluetooth_image.setImageResource(R.drawable.status_bar_bluetooth_normal);
        } else {
            status_bar_bluetooth_image.setImageResource(R.drawable.status_bar_bluetooth_unuse);
            topbluebg.setBackgroundResource(R.drawable.home_top_menu_normal);
        }
    }

    private void SetBackGround(boolean enable){
    	if(enable){
    		mFloatLayout.setBackgroundResource(R.drawable.system_top_bg);
    		mBottomFloatLayout.setBackgroundResource(R.drawable.system_bottom_bg);	
    	}else{
    		mFloatLayout.setBackground(null);
    		mBottomFloatLayout.setBackground(null);	
    	}
    }
    
    @Override
    public void onDestroy() {
    	unregisterReceiver(baseReceiver);
        super.onDestroy();
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
        if (mBottomFloatLayout != null) {
            mWindowManager.removeView(mBottomFloatLayout);
        }
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        /* 浠庡緱鍒扮殑淇″彿寮哄害,姣忎釜tiome渚涘簲鍟嗘湁鏇存柊 */
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
//            Log.d("3G level", String.valueOf(signalStrength.getGsmSignalStrength()) );
        }
    };

    public boolean restartKdService() {
        if (!DriverServiceManger.getInstance().isServiceRunning()) {
            DriverServiceManger.getInstance().startService(this);
            return false;
        } else {
            return true;
        }
    }
    //閲嶅惎KDPhone鏈嶅姟
    public boolean restartKdPhone(){
        if (!KdPhoneServiceManger.getInstance().isKdPhoneRunning()) {
        	KdPhoneServiceManger.getInstance().startKdPhone(this);
            Log.e("KdPhone", "KdPhone is not run,so restart it!");
            return false;
        } else {
            return true;
        }
    }

    // 鍒濆鍖栭《閮ㄧ殑涓滆タ
    protected void initHeader() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	String btconnected = SystemInfo.getInstance().getProperty("sys.kd.btconnected");
        		if(!"".equals(btconnected)){
        			if("yes".equals(btconnected)){
        				setBluetoothState(true);
        			}else if("no".equals(btconnected)){
        				setBluetoothState(false);
        				mHandler.sendEmptyMessageDelayed(1000, 3000);
        			}
        		}
            }
        };
        
        mBluetoothController = new BluetoothController(this);
        mBatteryAndTimeController = new BatteryAndTimeController(this);
        mWifiController = new WifiController(this);
        mTelephonySignalController = new TelephonySignalController(this);

        //MyListener = new MyPhoneStateListener();
        //Tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Tel.listen(MyListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    private void createBottomFloatViewUnder() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        /*LayoutParams.TYPE_PHONE 鐢ㄤ簬娴獥鏂瑰紡璋冭瘯锛�2019鐢ㄤ簬绯荤粺UI鏂瑰紡鍙戝竷*/
        if(FloatOrSystemUI){
        	layoutParams.type = LayoutParams.TYPE_PHONE;
        }else{
        	layoutParams.type = 2024;
            
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;


        DisplayMetrics dm = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        if(FloatOrSystemUI){
        }else{
        	layoutParams.x = 0;
        	layoutParams.y = height;
        }
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(this);
        final View navbarView = (FrameLayout) inflater.inflate(R.layout.bottom_float_layout_under, null, false);
        navbarView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight,
                    int oldBottom) {
                Log.d("kandiSystemUI", "navbarView height = " + navbarView.getHeight());
            }
        });
        mWindowManager.addView(navbarView, layoutParams);
    }
    
    private void createBottomFloatView() {
        Log.d("kandiSystemUI", "createBottomFloatView ========= ");
        
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        /*LayoutParams.TYPE_PHONE 鐢ㄤ簬娴獥鏂瑰紡璋冭瘯锛�2024鐢ㄤ簬绯荤粺UI鏂瑰紡鍙戝竷*/
        if(FloatOrSystemUI){
        	wmParams.type = LayoutParams.TYPE_PHONE;
        }else{
        	wmParams.type = 2019;  	
        }
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;

        DisplayMetrics dm = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        if(FloatOrSystemUI){
        }else{
        	wmParams.x = 0;
        	wmParams.y = height;
        }
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(this);
        mBottomFloatLayout = (FrameLayout) inflater.inflate(R.layout.home_bottom_nav_layout, null);
        mWindowManager.addView(mBottomFloatLayout, wmParams);

        imageButton_service = (ImageView) mBottomFloatLayout.findViewById(R.id.imageButton_service);
        imageButton_voice = (ImageView) mBottomFloatLayout.findViewById(R.id.imageButton_voice);
        imageButton_set = (ImageView) mBottomFloatLayout.findViewById(R.id.imageButton_set);
        imageButton_air = (ImageView) mBottomFloatLayout.findViewById(R.id.imageButton_air);
//        imageButton_navigator = (ImageView) mBottomFloatLayout.findViewById(R.id.imageButton_bg);

        mBottomFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        imageButton_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	Intent intent = new Intent();
                intent.setComponent(
                        new ComponentName("com.kandi.settings", "com.kandi.settings.activitys.WindowActivity"));
                intent.putExtra("intent_key_tab", 1);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(intent);
                    SetBackGround(false);
                } catch (Exception e) {
                    Log.e("KandiSystemUiService", e.toString());
                }
            }
        });
        imageButton_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	Intent intent = new Intent();
                intent.setComponent(
                        new ComponentName("com.kandi.settings", "com.kandi.settings.activitys.WindowActivity"));
                intent.putExtra("intent_key_tab", 4);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(intent);
                    SetBackGround(false);
                } catch (Exception e) {
                    Log.e("KandiSystemUiService", e.toString());
                }
            }
        });

        imageButton_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent();
                intent.setComponent(
                        new ComponentName("com.kandi.settings", "com.kandi.settings.activitys.WindowActivity"));
                intent.putExtra("intent_key_tab", 2);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(intent);
                    SetBackGround(false);
                } catch (Exception e) {
                    Log.e("KandiSystemUiService", e.toString());
                }
            }
        });
        imageButton_air.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setComponent(
                        new ComponentName("com.kandi.settings", "com.kandi.settings.activitys.WindowActivity"));
                intent.putExtra("intent_key_tab", 3);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(intent);
                    SetBackGround(false);
                } catch (Exception e) {
                    Log.e("KandiSystemUiService", e.toString());
                }
            }
        });

//        centerview = (FrameLayout) mBottomFloatLayout.findViewById(R.id.centerview);
//        centerview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                Intent intent = new Intent();
//                try {
//                    PackageManager packageManager = KandiSystemUiService.this.getPackageManager();
//                    intent = packageManager.getLaunchIntentForPackage("com.autonavi.amapautolite");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
//                            | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                    SetBackGround(true);
//                } catch (Exception e) {
//                    Log.e("Map", e.toString());
//                }finally{
//                	SetBackGround(false);
//                	Log.d("MAP","not found!");
//                }
//            }
//        });

//        imageButton_navigator.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                if (isPackageAtTop("com.autonavi.amapautolite")) {
//                    gotoHome();
//                    return ;
//                }
//                Intent intent = new Intent();
//                try {
//                    PackageManager packageManager = KandiSystemUiService.this.getPackageManager();
//                    intent = packageManager.getLaunchIntentForPackage("com.autonavi.amapautolite");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
//                            | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                    SetBackGround(true);
//                } catch (Exception e) {
//                	 SetBackGround(false);
//                    Log.e("KandiSystemUiService", e.toString());
//                }
//
//            }
//        });
    }

    public void BottomMenuServiceHideEvent() {
        mFloatLayout.setVisibility(View.GONE);

        downAnimation.setDuration(500);
        downAnimation.setRepeatCount(0);
        downAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFloatLayout.setVisibility(View.GONE);
                        System.out.println("set it gone");
                    }
                }, 500);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {

            }
        });
        mBottomFloatLayout.startAnimation(downAnimation);
    }

    public void BottomMenuServiceShowEvent() {
        mBottomFloatLayout.setVisibility(View.VISIBLE);

        upAnimation.setDuration(500);
        upAnimation.setRepeatCount(0);
        upAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {

            }
        });
        mBottomFloatLayout.startAnimation(upAnimation);
    }

    /**
     * 涓嫳鏂囧垏鎹㈠悗閲嶆柊鍔犺浇view
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	
    	Locale locale = getApplication().getResources().getConfiguration().locale;
        if (!locale.equals(mLocale)) {
        	mLocale = locale;
            if(mWindowManager == null){
            	mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
            }
            //鍒犻櫎椤堕儴娴獥
            if(mFloatLayout!=null){
            	mWindowManager.removeViewImmediate(mFloatLayout);
            }
            //鍒犻櫎搴曢儴瀵艰埅鏍�
            if(mBottomFloatLayout!=null){
            	mWindowManager.removeViewImmediate(mBottomFloatLayout);
            }
            //閲嶆柊鍒濆鍖栫晫闈�,mFloatLayout涓巑BottomFloatLayout鐨勯噸鏂板姞杞藉湪鍚勮嚜鐨刟ddOnAttachStateChangeListener涓�
            try {
            	//鐩墠娌℃湁濂界殑鍔炴硶 鍙兘sleep涓�涓嬪啀create
            	Thread.sleep(1500);
            	createFloatView();
            	createBottomFloatView();
            	initHeader();
            } catch (InterruptedException e) {
            	// TODO Auto-generated catch block
            	e.printStackTrace();
            }
        }
    }

    private boolean isActivityAtTop(String activityName) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        if (cn.getClassName().contains(activityName)) {
            return true;
        }
        return false;
    }
    private boolean isPackageAtTop(String packageName) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        if (cn.getPackageName().equalsIgnoreCase(packageName)) {
            return true;
        }
        return false;
    }
    void gotoHome() {
        Intent homeIntent = new Intent();
        homeIntent = new Intent();
        homeIntent.setComponent(new ComponentName("com.kandi.home", "com.kandi.view.MainActivity"));
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(homeIntent);
            SetBackGround(false);
        } catch (Exception e) {
            Log.e("KandiSystemUiService", e.toString());
        }
    }
    
    void test(){
    	this.stopSelf();
    	this.startService(null);
    }
}
