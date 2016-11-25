package com.ins.kuaidi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ins.kuaidi.R;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.entity.User;
import com.sobey.common.common.LoadingViewUtil;
import com.ins.middle.ui.activity.BaseAppCompatActivity;
import com.ins.middle.ui.activity.BaseBackActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PaywayActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;

    private View lay_weixin_open;
    private View lay_weixin_close;
    private View lay_zhifubao_open;
    private View lay_zhifubao_close;

    @Subscribe
    public void onEventMainThread(Integer flag) {
        if (flag == AppConstant.EVENT_UPDATE_PAYWAY) {
            initData();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payway);
        setToolbar();
        EventBus.getDefault().register(this);

        initBase();
        initView();
        initData();
        initCtrl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initBase() {
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        findViewById(R.id.card_payway_weixin).setOnClickListener(this);
        findViewById(R.id.card_payway_zhifubao).setOnClickListener(this);

        lay_weixin_open = findViewById(R.id.lay_weixin_open);
        lay_weixin_close = findViewById(R.id.lay_weixin_close);
        lay_zhifubao_open = findViewById(R.id.lay_zhifubao_open);
        lay_zhifubao_close = findViewById(R.id.lay_zhifubao_close);
    }

    private void initData() {
        setPayWayData();
    }

    private void initCtrl() {
    }

    private void setPayWayData() {
        User user = AppData.App.getUser();
        if (user != null) {
            if (user.getFristPayMethod() == 0) {
                setZhifubao(true);
                setWeixin(false);
            } else {
                setZhifubao(false);
                setWeixin(true);
            }
        }
    }

    private void setZhifubao(boolean isopen) {
        if (isopen) {
            lay_zhifubao_open.setVisibility(View.VISIBLE);
            lay_zhifubao_close.setVisibility(View.GONE);
        } else {
            lay_zhifubao_open.setVisibility(View.GONE);
            lay_zhifubao_close.setVisibility(View.VISIBLE);
        }
    }

    private void setWeixin(boolean isopen) {
        if (isopen) {
            lay_weixin_open.setVisibility(View.VISIBLE);
            lay_weixin_close.setVisibility(View.GONE);
        } else {
            lay_weixin_open.setVisibility(View.GONE);
            lay_weixin_close.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        User user = AppData.App.getUser();
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.card_payway_zhifubao:
                intent.setClass(this, PaywaySetActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
            case R.id.card_payway_weixin:
                intent.setClass(this, PaywaySetActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
                break;
        }
    }
}
