package com.ins.middle.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ins.middle.R;


/**
 * @author Tom.Cai
 * @Function: 自定义对话框
 * @Date: 2013-10-28
 * @Time: 下午12:37:43
 */
public class DialogAge extends Dialog {
    private TextView text_90, text_80, text_70, text_60, text_50;
    private Context context;

    public DialogAge(Context context) {
        super(context, R.style.PopupDialog);
        this.context = context;
        setMsgDialog();
    }

    private void setMsgDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_age, null);
        text_90 = (TextView) mView.findViewById(R.id.text_dialogage_90);
        text_80 = (TextView) mView.findViewById(R.id.text_dialogage_80);
        text_70 = (TextView) mView.findViewById(R.id.text_dialogage_70);
        text_60 = (TextView) mView.findViewById(R.id.text_dialogage_60);
        text_50 = (TextView) mView.findViewById(R.id.text_dialogage_50);

        text_90.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAgeClickListener != null) onAgeClickListener.onAgeClick(90);
                dismiss();
            }
        });
        text_80.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAgeClickListener != null) onAgeClickListener.onAgeClick(80);
                dismiss();
            }
        });
        text_70.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAgeClickListener != null) onAgeClickListener.onAgeClick(70);
                dismiss();
            }
        });
        text_60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAgeClickListener != null) onAgeClickListener.onAgeClick(60);
                dismiss();
            }
        });
        text_50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAgeClickListener != null) onAgeClickListener.onAgeClick(50);
                dismiss();
            }
        });

        this.setCanceledOnTouchOutside(true);    //点击外部关闭

        Window win = this.getWindow();
        win.setGravity(Gravity.BOTTOM);    //从下方弹出
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);

        super.setContentView(mView);
    }

    @Override
    public void show() {
        super.show();
    }

    public interface OnAgeClickListener {
        void onAgeClick(int age);
    }

    private OnAgeClickListener onAgeClickListener;

    public void setOnAgeClickListener(OnAgeClickListener onAgeClickListener) {
        this.onAgeClickListener = onAgeClickListener;
    }
}
