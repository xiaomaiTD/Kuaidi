package com.ins.middle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ins.middle.R;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.entity.User;
import com.ins.middle.entity.Wallet;
import com.ins.middle.utils.PackageUtil;
import com.sobey.common.common.LoadingViewUtil;
import com.sobey.common.utils.NumAnim;
import com.sobey.common.utils.NumUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.http.RequestParams;

public class WalletActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;

    private TextView text_wallet_payway;
    private TextView text_wallet_money;
    private TextView text_wallet_couponcount;

    private View lay_wallet_payway;
    private View lay_wallet_money;
    private View lay_wallet_coupon;
    private View lay_wallet_bankcard;

    private Wallet wallet;

    @Subscribe
    public void onEventMainThread(Integer flag) {
        if (flag == AppConstant.EVENT_UPDATE_PAYWAY) {
            setPayWayData();
        }
    }

    @Subscribe
    public void onEventMainThread(String flagSpc) {
        if (AppConstant.EVENT_CASH_MONEY.equals(AppConstant.getFlag(flagSpc))) {
            float money = Float.parseFloat(AppConstant.getStr(flagSpc));
            wallet.setBalance(money);
            setWalletData(wallet);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
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
        lay_wallet_payway = findViewById(R.id.lay_wallet_payway);
        lay_wallet_money = findViewById(R.id.lay_wallet_money);
        lay_wallet_coupon = findViewById(R.id.lay_wallet_coupon);
        lay_wallet_bankcard = findViewById(R.id.lay_wallet_bankcard);

        text_wallet_payway = (TextView) findViewById(R.id.text_wallet_payway);
        text_wallet_money = (TextView) findViewById(R.id.text_wallet_money);
        text_wallet_couponcount = (TextView) findViewById(R.id.text_wallet_couponcount);

        lay_wallet_payway.setOnClickListener(this);
        lay_wallet_money.setOnClickListener(this);
        lay_wallet_coupon.setOnClickListener(this);
        lay_wallet_bankcard.setOnClickListener(this);
    }

    private void initData() {
        netGetWallet();
        setPayWayData();
    }

    private void initCtrl() {
        if (PackageUtil.isClient()) {
            lay_wallet_payway.setVisibility(View.VISIBLE);
            lay_wallet_coupon.setVisibility(View.VISIBLE);
        } else {
            lay_wallet_payway.setVisibility(View.GONE);
            lay_wallet_coupon.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        int i = v.getId();
        if (i == R.id.lay_wallet_payway) {
            startActivity(PackageUtil.getSmIntent("PaywayActivity"));
        } else if (i == R.id.lay_wallet_money) {
            intent.setClass(this, MoneyActivity.class);
            intent.putExtra("money", wallet.getBalance());
            startActivity(intent);
        } else if (i == R.id.lay_wallet_coupon) {
            startActivity(PackageUtil.getSmIntent("CouponActivity"));
        } else if (i == R.id.lay_wallet_bankcard) {
            User user = AppData.App.getUser();
            if (user != null && user.getStatus() == User.AUTHENTICATED) {
                //已认证，调整银行卡页面
                intent.setClass(this, BankCardActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "请先进行实名认证", Toast.LENGTH_SHORT).show();
                startActivity(PackageUtil.getSmIntent("IdentifyActivity"));
            }
        }
    }

    private void setWalletData(Wallet wallet) {
        if (wallet != null) {
            text_wallet_money.setText(NumUtil.NumberFormat(wallet.getBalance(), 2) + "元");
            text_wallet_couponcount.setText(wallet.getCoupon() + "张");
        }
    }

    private void setPayWayData() {
        User user = AppData.App.getUser();
        if (user != null) {
            if (user.getFristPayMethod() == 0) {
                text_wallet_payway.setText("支付宝支付");
                text_wallet_payway.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.icon_zhifubao), null, null, null);
            } else {
                text_wallet_payway.setText("微信支付");
                text_wallet_payway.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.icon_weixin), null, null, null);
            }
        }
    }

    public void netGetWallet() {
        RequestParams params = new RequestParams(AppData.Url.wallet);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("flag", "0");
        CommonNet.samplepost(params, Wallet.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                if (pojo != null) {
                    wallet = (Wallet) pojo;
                    setWalletData(wallet);
                    LoadingViewUtil.showout(showingroup, showin);
                } else {
                    showin = LoadingViewUtil.showin(showingroup, R.layout.layout_lack, showin, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initData();
                        }
                    });
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(WalletActivity.this, text, Toast.LENGTH_SHORT).show();
                showin = LoadingViewUtil.showin(showingroup, R.layout.layout_fail, showin, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initData();
                    }
                });
            }

            @Override
            public void netStart(int status) {
                showin = LoadingViewUtil.showin(showingroup, R.layout.layout_loading, showin);
            }
        });
    }
}
