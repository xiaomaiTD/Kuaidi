package com.ins.middle.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.ins.middle.R;
import com.ins.middle.common.AppConstant;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.ui.activity.LoginActivity;

import org.greenrobot.eventbus.EventBus;
import org.xutils.http.RequestParams;

import com.ins.middle.utils.AppHelper;
import com.ins.middle.common.AppData;
import com.ins.middle.common.AppVali;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.User;
import com.ins.middle.utils.PackageUtil;

/**
 * Created by Administrator on 2016/6/2 0002.
 */
public class LoginPhoneFragment extends BaseFragment implements View.OnClickListener {

    private int position;
    private View rootView;
    private ViewGroup showingroup;
    private View showin;

    private CircularProgressButton btn_go;
    private EditText edit_login_phone;
    private LoginActivity activity;

    public static Fragment newInstance(int position) {
        LoginPhoneFragment f = new LoginPhoneFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.position = getArguments().getInt("position");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_loginphone, container, false);
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
        activity = (LoginActivity) getActivity();
    }

    private void initView() {
        showingroup = (ViewGroup) getView().findViewById(R.id.showingroup);
        btn_go = (CircularProgressButton) getView().findViewById(R.id.btn_go);
        edit_login_phone = (EditText) getView().findViewById(R.id.edit_login_phone);
        btn_go.setIndeterminateProgressMode(true);
    }

    private void initData() {
    }

    private void initCtrl() {
        btn_go.setOnClickListener(this);
    }

    private void freshCtrl() {
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_go) {
            btn_go.setClickable(false);

            String phone = edit_login_phone.getText().toString();
            String msg = AppVali.phone(phone);
            if (msg != null) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                btn_go.setClickable(true);
            } else {
                netCheckMobile(phone);
            }
        }
    }

    private void netCheckMobile(final String phone) {
        RequestParams params = new RequestParams(AppData.Url.checkMobile);
        params.addBodyParameter("mobile", phone);
        params.addBodyParameter("flag", PackageUtil.isClient() ? "0" : "1");
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                final CommonEntity com = (CommonEntity) pojo;
                AppHelper.progOk2dle(btn_go, new AppHelper.ProgressCallback() {
                    @Override
                    public void callback() {
                        if (code == 200) {
                            //第一次登陆，调整验证码页面
                            btn_go.setClickable(true);
                            String phone_vali = phone + "|" + com.getValicode();
                            EventBus.getDefault().post(AppConstant.makeFlagStr(AppConstant.EVENT_DIALOGLOGON_VALI, phone_vali));
                            activity.next();
                        } else if (code == 1010) {
                            //已注册，跳转登陆
                            String phone_type = phone + "|" + "0";
                            EventBus.getDefault().post(AppConstant.makeFlagStr(AppConstant.EVENT_DIALOGLOGON_PHONE, phone_type));
                            activity.goPosition(2);
                            //已注册（没有密码），跳转设置密码（2016.12.9 改为调整验证码页面）
                        } else if (code == 1012) {
//                            String phone_type = phone + "|" + "2";
//                            EventBus.getDefault().post(AppConstant.makeFlagStr(AppConstant.EVENT_DIALOGLOGON_PHONE, phone_type));
//                            activity.goPosition(2);
                            String phone_type = phone + "|" + com.getValicode() + "|" + "2";
                            EventBus.getDefault().post(AppConstant.makeFlagStr(AppConstant.EVENT_DIALOGLOGON_VALI, phone_type));
                            activity.next();
                        }
                    }
                });
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                AppHelper.progError2dle(btn_go);
                btn_go.setClickable(true);
            }

            @Override
            public void netStart(int code) {
                btn_go.setProgress(50);
            }
        });
    }
}
