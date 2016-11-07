package com.ins.middle.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.ui.activity.LoginActivity;
import com.ins.middle.utils.AppHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.http.RequestParams;

/**
 * Created by Administrator on 2016/6/2 0002.
 */
public class LoginValiFragment extends BaseFragment implements View.OnClickListener {

    private int position;
    private View rootView;
    private ViewGroup showingroup;
    private View showin;

    private LoginActivity activity;
    private CircularProgressButton btn_go;
    private TextView btn_go_vali;
    private TextView text_login_phone;
    private EditText edit_login_vali;

    public static Fragment newInstance(int position) {
        LoginValiFragment f = new LoginValiFragment();
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

    @Subscribe
    public void onEventMainThread(String flagSpc) {
        if (AppConstant.EVENT_DIALOGLOGON_VALI.equals(AppConstant.getFlag(flagSpc))) {
            String phone_vali = AppConstant.getStr(flagSpc);
            String[] split = phone_vali.split("\\|");
            phone = split[0];
            valicode = split[1];
            text_login_phone.setText(phone + "");
            sendTimeMessage();
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
        rootView = inflater.inflate(R.layout.fragment_loginvali, container, false);
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
        btn_go_vali = (TextView) getView().findViewById(R.id.btn_go_vali);
        text_login_phone = (TextView) getView().findViewById(R.id.text_login_phone);
        edit_login_vali = (EditText) getView().findViewById(R.id.edit_login_vali);
        btn_go.setIndeterminateProgressMode(true);
    }

    private void initData() {
    }

    private void initCtrl() {
        btn_go.setOnClickListener(this);
        btn_go_vali.setOnClickListener(this);
    }

    private void freshCtrl() {
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_go) {
            btn_go.setClickable(false);
            btn_go.setProgress(50);

            String vali = edit_login_vali.getText().toString();
            String msg = AppVali.valicode(valicode, vali);
            if (msg != null) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                btn_go.setClickable(true);
            } else {
                AppHelper.progOk2dle(btn_go, new AppHelper.ProgressCallback() {
                    @Override
                    public void callback() {
                        String phone_flag = phone + "|" + "1";
                        EventBus.getDefault().post(AppConstant.makeFlagStr(AppConstant.EVENT_DIALOGLOGON_PHONE, phone_flag));
                        activity.next();
                    }
                });
            }
        } else if (i == R.id.btn_go_vali) {
            netGetValicode(phone);
        }
    }


    private void netGetValicode(String phone) {
        RequestParams params = new RequestParams(AppData.Url.sendCode);
        params.addBodyParameter("mobile", phone);
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, final Object pojo, String text, Object obj) {
                if (pojo == null) netSetError(code, "接口异常");
                else {
                    Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();

                    //保持验证码和手机号
                    CommonEntity com = (CommonEntity) pojo;
                    valicode = com.getValicode();

                    //开始计时
                    time = MAXTIME;
                    sendTimeMessage();
                    btn_go_vali.setText(time + "");
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                btn_go_vali.setEnabled(true);
                time = 0;
            }

            @Override
            public void netStart(int status) {
                btn_go_vali.setEnabled(false);
            }
        });
    }

    ////////////////////////////
    //保持验证码
    private String valicode;
    //保持已验证手机号
    private String phone;

    ////////////////////////////
    //获取验证码计时

    private final static int MAXTIME = 60;
    private int time = MAXTIME;
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
                if (time > 0) {
                    btn_go_vali.setText("" + time);
                    time--;
                    LoginValiFragment.this.sendTimeMessage();
                } else {
                    btn_go_vali.setEnabled(true);
                    btn_go_vali.setText("获取验证码");
                    time = MAXTIME;
                }
            }
        }
    };
}
