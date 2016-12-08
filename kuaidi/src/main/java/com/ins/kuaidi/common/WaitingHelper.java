package com.ins.kuaidi.common;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/12/8.
 */

public class WaitingHelper {

    private String text = "正在为您安排车辆";
    private TextView btn_go_vali;
    private boolean isContinu = true;

    public WaitingHelper(TextView textView) {
        this.btn_go_vali = textView;
    }

    private final static int MAXTIME = 3;
    private int time = 0;
    private final static int WHAT_TIME = 0;

    private void sendTimeMessage() {
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_TIME);
            mHandler.sendEmptyMessageDelayed(WHAT_TIME, 1000);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_TIME) {
                numAdd();
                btn_go_vali.setText(getMsgStr());
                if (isContinu) sendTimeMessage();
            }
        }
    };

    public void start() {
        //开始计时
        time = 0;
        isContinu = true;
        mHandler.removeMessages(WHAT_TIME);
        sendTimeMessage();
        btn_go_vali.setText(text);
    }

    public void reset() {
        mHandler.removeMessages(WHAT_TIME);
        isContinu = false;
    }

    private String getMsgStr() {
        String str = text;
        for (int i = 0; i < time; i++) {
            str += ".";
        }
//        for (int i = 0; i < MAXTIME - time; i++) {
//            str += " ";
//        }
//        if (time == 0) {
//            str = text + "   ";
//        } else if (time == 1) {
//            str = text + ".  ";
//        } else if (time == 2) {
//            str = text + ".. ";
//        } else if (time == 3) {
//            str = text + "...";
//        }
        return str;
    }

    private void numAdd() {
        if (time < MAXTIME) {
            time++;
        } else {
            time = 0;
        }
    }
}
