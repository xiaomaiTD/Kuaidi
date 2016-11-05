package com.ins.driver.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ins.driver.R;


/**
 * @author Tom.Cai
 * @Function: 自定义对话框
 * @Date: 2013-10-28
 * @Time: 下午12:37:43
 */
public class DialogPayStatus extends Dialog {

    private Context context;
    private TextView text_msg;
    private TextView text_price;
    private String msg;
    private String priceStr;

    public DialogPayStatus(Context context, String priceStr) {
        this(context, "已收到定金", priceStr);
    }

    public DialogPayStatus(Context context, String msg, String priceStr) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.msg = msg;
        this.priceStr = priceStr;
        setLoadingDialog();
    }

    private void setLoadingDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.dialog_paystatus, null);// 得到加载view

        text_msg = (TextView) v.findViewById(R.id.text_msg);
        text_price = (TextView) v.findViewById(R.id.text_paystatus_price);

        text_msg.setText(msg);
        text_price.setText(priceStr);

        this.setCanceledOnTouchOutside(true);
        super.setContentView(v);
    }


    @Override
    public void show() {
        super.show();
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        /////////获取屏幕宽度
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        ;
        wm.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        /////////设置高宽
        lp.width = (int) (screenWidth * 0.7); // 宽度
//        lp.height = (int) (lp.width*0.65); // 高度
        dialogWindow.setAttributes(lp);
    }

    public void setMsg(String msg) {
        text_msg.setText(msg);
    }
}
