package com.ins.kuaidi.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ins.kuaidi.R;
import com.ins.kuaidi.ui.activity.ModifyPswPayActivity;
import com.ins.kuaidi.utils.AppHelper;
import com.sobey.common.view.PswView;
import com.sobey.common.view.virtualKeyboardView.VirtualKeyboardView;


/**
 * type:0 第一次输入密码 1: 确认密码
 */
public class ModifyPswPayTwoFragment extends BaseFragment implements View.OnClickListener {

    private int position;
    private int type;
    private View rootView;
    private ViewGroup showingroup;
    private View showin;

    private TextView text_name;
    private TextView btn_go;
    private ModifyPswPayActivity activity;

    private PswView pswView;
    private VirtualKeyboardView keybord;


    public static Fragment newInstance(int position, int type) {
        ModifyPswPayTwoFragment f = new ModifyPswPayTwoFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        b.putInt("type", type);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.position = getArguments().getInt("position");
        this.type = getArguments().getInt("type");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_modifypswpaytwo, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initBase();
        initView();
        initData();
        initCtrl();
    }

    private void initBase() {
        activity = (ModifyPswPayActivity) getActivity();
    }

    private void initView() {
        showingroup = (ViewGroup) getView().findViewById(R.id.showingroup);
        btn_go = (TextView) getView().findViewById(R.id.btn_go);
        pswView = (PswView) getView().findViewById(R.id.pswView);
        keybord = (VirtualKeyboardView) getView().findViewById(R.id.keybord);
        text_name = (TextView) getView().findViewById(R.id.text_modifypswpay_name);

        if (type==0) {
            btn_go.setText("下一步");
            text_name.setText("支付密码");
        }else {
            btn_go.setText("完成修改");
            text_name.setText("确认支付密码");
        }
    }

    private void initData() {
    }

    private void initCtrl() {
        btn_go.setOnClickListener(this);

        //设置不调用系统键盘
//        KeyBoardUtil.EnableSysKeyBoard(getActivity(), pswView);
//        pswView.setEnabled(false);
        //设置点击弹出自定义虚拟键盘
        keybord.setClickShowKeybord(pswView);
        //设置虚拟键盘点击事件
        AppHelper.AttachKeybordWithPswView(keybord, pswView);
    }

    private void freshCtrl() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_go:
                activity.next();
                break;
        }
    }
}
