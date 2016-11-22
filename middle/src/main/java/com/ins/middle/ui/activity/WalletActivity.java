package com.ins.middle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ins.middle.R;
import com.ins.middle.common.AppData;
import com.ins.middle.entity.User;
import com.ins.middle.utils.PackageUtil;
import com.sobey.common.common.LoadingViewUtil;

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
        int i = v.getId();
        if (i == R.id.lay_wallet_payway) {
//            intent.setClass(this, PaywayActivity.class);
//            startActivity(intent);

        } else if (i == R.id.lay_wallet_money) {
//            intent.setClass(this, MoneyActivity.class);
//            startActivity(intent);

        } else if (i == R.id.lay_wallet_coupon) {
//            intent.setClass(this, CouponActivity.class);
//            startActivity(intent);
        } else if (i == R.id.lay_wallet_bankcard) {
            if (AppData.App.getUser().getStatus() == User.AUTHENTICATED) {
                //已认证，调整银行卡页面
                intent.setClass(this, BankCardActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "请先进行实名认证", Toast.LENGTH_SHORT).show();
                startActivity(PackageUtil.getSmIntent("IdentifyActivity"));
            }
        }
    }
}
