package com.ins.kuaidi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

import com.ins.kuaidi.R;
import com.sobey.common.common.LoadingViewUtil;
import com.ins.middle.ui.activity.BaseAppCompatActivity;
import com.ins.middle.ui.activity.BaseBackActivity;
public class WalletActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
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
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        findViewById(R.id.lay_wallet_payway).setOnClickListener(this);
        findViewById(R.id.lay_wallet_money).setOnClickListener(this);
        findViewById(R.id.lay_wallet_coupon).setOnClickListener(this);
        findViewById(R.id.lay_wallet_bankcard).setOnClickListener(this);
    }

    private void initData() {
        showin = LoadingViewUtil.showin(showingroup, R.layout.layout_loading, showin);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //加载成功
                initCtrl();
                LoadingViewUtil.showout(showingroup, showin);

                //加载失败
//                LoadingViewUtil.showin(showingroup,R.layout.layout_lack,showin,new View.OnClickListener(){
//                    @Override
//                    public void onClick(View v) {
//                        initData();
//                    }
//                });
            }
        }, 500);
    }

    private void initCtrl() {
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.lay_wallet_payway:
                intent.setClass(this, PaywayActivity.class);
                startActivity(intent);
                break;
            case R.id.lay_wallet_money:
                intent.setClass(this, MoneyActivity.class);
                startActivity(intent);
                break;
            case R.id.lay_wallet_coupon:
                intent.setClass(this, CouponActivity.class);
                startActivity(intent);
                break;
            case R.id.lay_wallet_bankcard:
                intent.setClass(this, BankCardActivity.class);
                startActivity(intent);
                break;
        }
    }
}
