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
public class DialogSex extends Dialog {
    private TextView text_man, text_woman;
    private Context context;

    public DialogSex(Context context) {
        super(context, R.style.PopupDialog);
        this.context = context;
        setMsgDialog();
    }

    private void setMsgDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_sex, null);
        text_man = (TextView) mView.findViewById(R.id.text_dialogsex_man);
        text_woman = (TextView) mView.findViewById(R.id.text_dialogsex_woman);
        text_man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSexClickListener != null) onSexClickListener.onSexClick(0);
                dismiss();
            }
        });
        text_woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSexClickListener != null) onSexClickListener.onSexClick(1);
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

    public interface OnSexClickListener {
        void onSexClick(int sex);
    }

    private OnSexClickListener onSexClickListener;

    public void setOnSexClickListener(OnSexClickListener onSexClickListener) {
        this.onSexClickListener = onSexClickListener;
    }

}
