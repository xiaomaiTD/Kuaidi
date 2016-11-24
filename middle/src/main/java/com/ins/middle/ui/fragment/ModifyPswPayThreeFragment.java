package com.ins.middle.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ins.middle.R;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.common.AppVali;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.User;
import com.ins.middle.ui.activity.ModifyPswPayActivity;
import com.ins.middle.utils.AppHelper;
import com.sobey.common.utils.MD5Util;
import com.sobey.common.view.PswView;
import com.sobey.common.view.virtualKeyboardView.VirtualKeyboardView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.http.RequestParams;

/**
 * type:0 第一次输入密码 1: 确认密码
 */
public class ModifyPswPayThreeFragment extends BaseFragment implements View.OnClickListener {

    private int position;
    private View rootView;
    private ViewGroup showingroup;
    private View showin;

    private TextView text_name;
    private TextView btn_go;
    private ModifyPswPayActivity activity;

    private PswView pswView;
    private VirtualKeyboardView keybord;

    private String psw;

    public static Fragment newInstance(int position) {
        ModifyPswPayThreeFragment f = new ModifyPswPayThreeFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        f.setArguments(b);
        return f;
    }

    @Subscribe
    public void onEventMainThread(String flagSpc) {
        if (AppConstant.EVENT_MODIFYPAYPSW.equals(AppConstant.getFlag(flagSpc))) {
            psw = AppConstant.getStr(flagSpc);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.position = getArguments().getInt("position");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

        btn_go.setText("完成修改");
        text_name.setText("确认提现密码");
    }

    private void initData() {
    }

    private void initCtrl() {
        btn_go.setOnClickListener(this);

        //设置不调用系统键盘
//        KeyBoardUtil.EnableSysKeyBoard(getActivity(), pswView);
//        pswView.setEnabled(false);
        //设置点击弹出自定义虚拟键盘
        keybord.setVisibility(View.GONE);
        keybord.setClickShowKeybord(pswView);
        //设置虚拟键盘点击事件
        AppHelper.AttachKeybordWithPswView(keybord, pswView);
    }

    private void freshCtrl() {
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_go) {
            String repetpsw = pswView.getPsw();
            String msg = AppVali.payPsw(psw, repetpsw);
            if (msg != null) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            } else {
                netCommit(psw);
            }
        }
    }

    public void netCommit(String psw) {
        RequestParams params = new RequestParams(AppData.Url.updateUser);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("payPassword", MD5Util.md5(psw));
        CommonNet.samplepost(params, User.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                User user = (User) pojo;
                AppData.App.saveUser(user);
                activity.finish();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netStart(int status) {
                btn_go.setEnabled(false);
            }

            @Override
            public void netEnd(int status) {
                btn_go.setEnabled(true);
            }
        });
    }
}
