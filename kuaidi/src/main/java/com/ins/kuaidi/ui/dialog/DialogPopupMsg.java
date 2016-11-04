package com.ins.kuaidi.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ins.kuaidi.R;


/**
 * @author Tom.Cai
 * @Function: 自定义对话框
 * @Date: 2013-10-28
 * @Time: 下午12:37:43
 */
public class DialogPopupMsg extends Dialog {
    private TextView btn_go;
    private EditText edit_detail;
    private Context context;

    public DialogPopupMsg(Context context) {
        super(context, R.style.PopupDialog);
        this.context = context;
        setMsgDialog();
    }

    private void setMsgDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_popupmsg, null);
        edit_detail = (EditText) mView.findViewById(R.id.edit_popupmsg_detail);
        btn_go = (TextView) mView.findViewById(R.id.btn_go);
        btn_go.setOnClickListener(listener);

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

    public void setOnSendListener(View.OnClickListener listener) {
        btn_go.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DialogPopupMsg.this.dismiss();
        }
    };
}
