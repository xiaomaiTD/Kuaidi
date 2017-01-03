package com.ins.middle.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/29.
 */

public class SMSBroadcastReceiver extends BroadcastReceiver {


    /**
     * 收到短信Action
     **/
    String ACTION_SMS_RECIVER = "android.provider.Telephony.SMS_RECEIVED";

    /**
     * 注册广播接受者监听短信验证码自动回写 可在onCreate()中进行注册;
     */
    public void registSmsReciver(Activity activity) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SMS_RECIVER);
        // 设置优先级 不然监听不到短信
        filter.setPriority(1000);
        activity.registerReceiver(this, filter);
    }

    public void unRegistSmsReciver(Activity activity) {
        // 取消短信广播注册
        activity.unregisterReceiver(this);
    }

    private String analysisVerify(String message) {
        char[] msgs = message.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < msgs.length; i++) {
            if ('0' <= msgs[i] && msgs[i] <= '9') {
                sb.append(msgs[i]);
            }
        }
        return sb.toString();
    }

    ///////////////////////////////////////////
    ///////////////////////////////////////////
    ///////////////////////////////////////////

    private static MessageListener mMessageListener;

    public SMSBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        for (Object pdu : pdus) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
            String sender = smsMessage.getDisplayOriginatingAddress();
            String content = smsMessage.getMessageBody();
            long date = smsMessage.getTimestampMillis();
            Date timeDate = new Date(date);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = simpleDateFormat.format(timeDate);

            Log.e("msg", "短信来自:" + sender);
            Log.e("msg", "短信内容:" + content);
            Log.e("msg", "短信时间:" + time);

            mMessageListener.OnReceived(content);

            //如果短信来自5556,不再往下传递
            if ("5556".equals(sender)) {
                System.out.println(" abort ");
                abortBroadcast();
            }

        }
    }

    // 回调接口
    public interface MessageListener {
        public void OnReceived(String message);
    }

    public void setOnReceivedMessageListener(MessageListener messageListener) {
        this.mMessageListener = messageListener;
    }
}
