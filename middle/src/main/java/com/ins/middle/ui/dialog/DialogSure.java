package com.ins.middle.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ins.middle.R;
import com.sobey.common.utils.StrUtils;


/**
 * @author Tom.Cai
 * @Function: 自定义对话框
 * @Date: 2013-10-28
 * @Time: 下午12:37:43
 */
public class DialogSure extends Dialog {

    private Context context;
    private TextView text_dialog_sure;
    private TextView text_dialog_title;
    private TextView text_cancle;
    private TextView text_ok;

    private String msg;
    private String title;
    private String cancelStr;
    private String sureStr;

    public DialogSure(Context context) {
        this(context, "", "确定？", "取消", "确定");
    }

    public DialogSure(Context context, String msg) {
        this(context, "", msg, "取消", "确定");
    }

    public DialogSure(Context context, String title, String msg) {
        this(context, title, msg, "取消", "确定");
    }

    public DialogSure(Context context, String title, String msg, String cancelStr, String sureStr) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.title = title;
        this.msg = msg;
        this.cancelStr = cancelStr;
        this.sureStr = sureStr;
        setLoadingDialog();
    }

    private void setLoadingDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.dialog_sure, null);// 得到加载view

        text_dialog_sure = (TextView) v.findViewById(R.id.text_dialog_sure);
        text_dialog_title = (TextView) v.findViewById(R.id.text_dialog_title);
        text_cancle = (TextView) v.findViewById(R.id.dialog_cancel);
        text_ok = (TextView) v.findViewById(R.id.dialog_ok);


        text_cancle.setOnClickListener(listener);
        text_ok.setOnClickListener(listener);

        setData();

        this.setCanceledOnTouchOutside(true);
        super.setContentView(v);
    }

    public void setTitle(String title){
        this.title = title;
        setData();
    }

    public void setMsg(String msg){
        this.msg = msg;
        setData();
    }

    private void setData(){
        text_dialog_title.setText(title);
        text_dialog_sure.setText(msg);
        text_cancle.setText(cancelStr);
        text_ok.setText(sureStr);
        if (StrUtils.isEmpty(title)){
            text_dialog_title.setVisibility(View.GONE);
        }else {
            text_dialog_title.setVisibility(View.VISIBLE);
        }
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
        lp.width = (int) (screenWidth * 0.85); // 宽度
//        lp.height = (int) (lp.width*0.65); // 高度
        dialogWindow.setAttributes(lp);
    }

    public void setOnCancleListener(View.OnClickListener listener) {
        text_cancle.setOnClickListener(listener);
    }

    public void setOnOkListener(View.OnClickListener listener) {
        text_ok.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };
}
