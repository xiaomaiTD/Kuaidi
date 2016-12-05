package com.ins.middle.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by Administrator on 2016/12/5.
 */

public class PhoneStateBroadCastReceiver extends BroadcastReceiver {
    private static final String TAG = "PhoneReceiver";
    private static boolean mIncomingFlag = false;
    private static String mIncomingNumber = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果是拨打电话
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            mIncomingFlag = false;
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.e(TAG, "call OUT:" + phoneNumber);
        }
//        else {
//            // 如果是来电
//            TelephonyManager tManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
//            switch (tManager.getCallState()) {
//                case TelephonyManager.CALL_STATE_RINGING:
//                    mIncomingNumber = intent.getStringExtra("incoming_number");
//                    Log.e(TAG, "RINGING :" + mIncomingNumber);
//                    break;
//                case TelephonyManager.CALL_STATE_OFFHOOK:
//                    if (mIncomingFlag) {
//                        Log.e(TAG, "incoming ACCEPT :" + mIncomingNumber);
//                    }
//                    break;
//                case TelephonyManager.CALL_STATE_IDLE:
//                    if (mIncomingFlag) {
//                        Log.e(TAG, "incoming IDLE");
//                    }
//                    break;
//            }
//        }
    }
}

//
//<!-- 拨打电话监听-->
//<receiver android:name="com.ins.middle.receiver.PhoneStateBroadCastReceiver">
//<intent-filter>
//<action android:name="android.intent.action.PHONE_STATE"/>
//<action android:name="android.intent.action.NEW_OUTGOING_CALL" />
//</intent-filter>
//</receiver>
//
//<!--监听拨打电话-->
//<uses-permission android:name="android.permission.READ_PHONE_STATE" />
//<uses-permission android:name="android.permission.PROCESS_OUTGOING_CALLS" />
