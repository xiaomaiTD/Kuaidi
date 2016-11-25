package com.ins.kuaidi.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.ins.kuaidi.R;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.User;
import com.kyleduo.switchbutton.SwitchButton;
import com.sobey.common.common.LoadingViewUtil;
import com.ins.middle.ui.activity.BaseAppCompatActivity;
import com.ins.middle.ui.activity.BaseBackActivity;
import com.sobey.common.utils.MD5Util;

import org.greenrobot.eventbus.EventBus;
import org.xutils.http.RequestParams;

public class PaywaySetActivity extends BaseBackActivity implements CompoundButton.OnCheckedChangeListener {

    private ViewGroup showingroup;
    private View showin;
    private int type;

    private SwitchButton switch_paywayset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paywayset);
        setToolbar();

        initBase();
        initView();
        initData();
        initCtrl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initBase() {
        if (getIntent().hasExtra("type")) {
            type = getIntent().getIntExtra("type", 0);
        }
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        switch_paywayset = (SwitchButton) findViewById(R.id.switch_paywayset);
    }

    private void initData() {
        setData();
    }

    private void initCtrl() {
        switch_paywayset.setOnCheckedChangeListener(this);
    }

    private void setData() {
        User user = AppData.App.getUser();
        if (user.getFristPayMethod() == 0) {
            if (type == 0) {
                switch_paywayset.setCheckedImmediately(true);
            } else {
                switch_paywayset.setCheckedImmediately(false);
            }
        } else {
            if (type == 0) {
                switch_paywayset.setCheckedImmediately(false);
            } else {
                switch_paywayset.setCheckedImmediately(true);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (type == 0) {
            netCommit(isChecked ? 0 : 1);
        } else {
            netCommit(isChecked ? 1 : 0);
        }
    }

    public void netCommit(int type) {
        RequestParams params = new RequestParams(AppData.Url.updateUser);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("fristPayMethod", type + "");
        CommonNet.samplepost(params, User.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                Toast.makeText(PaywaySetActivity.this, text, Toast.LENGTH_SHORT).show();
                User user = (User) pojo;
                AppData.App.saveUser(user);
                EventBus.getDefault().post(AppConstant.EVENT_UPDATE_PAYWAY);
                finish();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(PaywaySetActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netStart(int status) {
                switch_paywayset.setEnabled(false);
            }

            @Override
            public void netEnd(int status) {
                switch_paywayset.setEnabled(true);
            }
        });
    }
}
