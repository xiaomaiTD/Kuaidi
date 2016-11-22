package com.ins.middle.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.ins.middle.R;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.common.AppVali;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.User;
import com.ins.middle.ui.activity.LoginActivity;
import com.ins.middle.utils.AppHelper;
import com.ins.middle.utils.PackageUtil;
import com.sobey.common.utils.MD5Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.http.RequestParams;

/**
 * type 0:登陆 1:注册 2:设置密码
 */
public class LoginPswFragment extends BaseFragment implements View.OnClickListener {

    private int position;
    private View rootView;
    private ViewGroup showingroup;
    private View showin;

    private LoginActivity activity;

    private CircularProgressButton btn_go;
    private String phone;
    private int type;
    private EditText edit_login_psw;
    private TextView text_login_title;

    public static Fragment newInstance(int position) {
        LoginPswFragment f = new LoginPswFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        this.position = getArguments().getInt("position");
    }

    private boolean back2first = false;

    @Subscribe
    public void onEventMainThread(String flagSpc) {
        if (AppConstant.EVENT_DIALOGLOGON_PHONE.equals(AppConstant.getFlag(flagSpc))) {
            String phone_type = AppConstant.getStr(flagSpc);
            String[] split = phone_type.split("\\|");
            phone = split[0];
            type = Integer.parseInt(split[1]);

            if (type == 0) {
                //登录
                text_login_title.setText("输入密码");
                edit_login_psw.setHint("输入手机密码");
                back2first = true;
            } else if (type == 1) {
                //注册
                text_login_title.setText("设置密码");
                edit_login_psw.setHint("设置手机密码");
                back2first = false;
            } else if (type == 2) {
                //设置密码
                text_login_title.setText("设置密码");
                edit_login_psw.setHint("设置手机密码");
                back2first = true;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_loginpsw, container, false);
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
        edit_login_psw = (EditText) getView().findViewById(R.id.edit_login_psw);
        text_login_title = (TextView) getView().findViewById(R.id.text_login_title);
        btn_go = (CircularProgressButton) getView().findViewById(R.id.btn_go);
        btn_go.setIndeterminateProgressMode(true);

        getView().findViewById(R.id.btn_left).setOnClickListener(this);
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

            String psw = edit_login_psw.getText().toString();
            String msg = AppVali.login_go(phone, psw);
            if (msg != null) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                btn_go.setClickable(true);
            } else {
                netLogin(phone, psw);
            }
        } else if (i == R.id.btn_left) {
            if (back2first) {
                activity.goPosition(0);
            } else {
                activity.last();
            }
        }
    }

    private void netLogin(String phone, String password) {
        RequestParams params;
        if (type == 0) {
            params = new RequestParams(AppData.Url.login);
        } else if (type == 1) {
            params = new RequestParams(AppData.Url.register);
        } else {
            params = new RequestParams(AppData.Url.setPsw);
        }
        params.addBodyParameter("mobile", phone);
        params.addBodyParameter("password", MD5Util.md5(password));
        params.addBodyParameter("flag", PackageUtil.isClient() ? "0" : "1");
        params.addBodyParameter("deviceType", 0 + "");
        params.addBodyParameter("deviceToken", AppData.App.getJpushId());
        CommonNet.samplepost(params, User.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                if (pojo == null) netSetError(code, "错误:返回数据为空");
                else {
                    final User user = (User) pojo;
                    AppHelper.progOk2dle(btn_go, new AppHelper.ProgressCallback() {
                        @Override
                        public void callback() {
                            AppData.App.saveToken(user.getToken());
                            AppData.App.saveUser(user);
                            //如果是车主登录后，且没有认证则打开认证页面
                            //////////////////////////////
                            if (!PackageUtil.isClient() && user.getStatus() == User.UNAUTHORIZED) {
                                startActivity(PackageUtil.getSmIntent("IdentifyActivity"));
                            }
                            //////////////////////////////
                            EventBus.getDefault().post(AppConstant.EVENT_UPDATE_LOGIN);
                            activity.finish();
                        }
                    });
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                AppHelper.progError2dle(btn_go);
            }

            @Override
            public void netStart(int code) {
                btn_go.setProgress(50);
            }
        });
    }
}
