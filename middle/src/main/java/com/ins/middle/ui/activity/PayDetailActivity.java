package com.ins.middle.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ins.middle.R;
import com.ins.middle.entity.PayData;
import com.ins.middle.entity.PayDataDriver;
import com.ins.middle.entity.Trip;
import com.ins.middle.entity.Wallet;
import com.ins.middle.utils.PackageUtil;
import com.sobey.common.common.LoadingViewUtil;
import com.sobey.common.utils.NumUtil;

public class PayDetailActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;

    private TextView text_paydetail_title;
    private TextView text_paydetail_money;
    private TextView text_paydetail_total;
    private TextView text_paydetail_first;
    private TextView text_paydetail_last;
    private TextView text_paydetail_coupon;
    private TextView text_paydetail_balance;
    private TextView text_paydetail_first_name;
    private TextView text_paydetail_last_name;

    private View lay_paydetail_total;
    private View lay_paydetail_coupon;
    private View lay_paydetail_balance;

    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paydetail);
        if (PackageUtil.isClient()) {
            setToolbar("支付明细");
        } else {
            setToolbar("收款明细");
        }
        initBase();
        initView();
//        initCtrl();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initBase() {
        if (getIntent().hasExtra("trip")) {
            trip = (Trip) getIntent().getSerializableExtra("trip");
        }
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        text_paydetail_title = (TextView) findViewById(R.id.text_paydetail_title);
        text_paydetail_money = (TextView) findViewById(R.id.text_paydetail_money);
        text_paydetail_total = (TextView) findViewById(R.id.text_paydetail_total);
        text_paydetail_first = (TextView) findViewById(R.id.text_paydetail_first);
        text_paydetail_last = (TextView) findViewById(R.id.text_paydetail_last);
        text_paydetail_coupon = (TextView) findViewById(R.id.text_paydetail_coupon);
        text_paydetail_balance = (TextView) findViewById(R.id.text_paydetail_balance);
        text_paydetail_first_name = (TextView) findViewById(R.id.text_paydetail_first_name);
        text_paydetail_last_name = (TextView) findViewById(R.id.text_paydetail_last_name);
        lay_paydetail_total = findViewById(R.id.lay_paydetail_total);
        lay_paydetail_coupon = findViewById(R.id.lay_paydetail_coupon);
        lay_paydetail_balance = findViewById(R.id.lay_paydetail_balance);
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
        }, 800);
    }

    private void initCtrl() {
        if (PackageUtil.isClient()) {
            text_paydetail_title.setText("实际支付");
            text_paydetail_first_name.setText("定金支付");
            text_paydetail_last_name.setText("尾款支付");
            lay_paydetail_total.setVisibility(View.VISIBLE);
            lay_paydetail_coupon.setVisibility(View.VISIBLE);
            lay_paydetail_balance.setVisibility(View.VISIBLE);
        } else {
            text_paydetail_title.setText("实际收款");
            text_paydetail_first_name.setText("定金");
            text_paydetail_last_name.setText("尾款");
            lay_paydetail_total.setVisibility(View.GONE);
            lay_paydetail_coupon.setVisibility(View.GONE);
            lay_paydetail_balance.setVisibility(View.GONE);
        }

        setTripDetail(trip);
    }

    private void setTripDetail(Trip trip) {
        String driverDetail = trip.getDriverDetail();
        String payDetail = trip.getPayDetail();
        String bossesPayDetail = trip.getBossesPayDetail();
        Gson gson = new Gson();

        PayDataDriver payDataDriver = gson.fromJson(driverDetail, PayDataDriver.class);
        PayData first = gson.fromJson(payDetail, PayData.class);
        PayData last = gson.fromJson(bossesPayDetail, PayData.class);

        if (PackageUtil.isClient()) {
            if (first != null && last != null) {
                //设置实际支付
                text_paydetail_money.setText(NumUtil.num2half(first.getActualPay() + last.getActualPay()) + "");
                //设置总金额
                text_paydetail_total.setText(trip.getPayMoney() + "元");
                //预付款
                text_paydetail_first.setText(NumUtil.num2half(first.getActualPay()) + "元");
                setPayWay(text_paydetail_first, first.getPayMethed());
                //尾款
                text_paydetail_last.setText(NumUtil.num2half(last.getActualPay()) + "元");
                setPayWay(text_paydetail_last, last.getPayMethed());
                //设置优惠券
                text_paydetail_coupon.setText("-" + NumUtil.num2half(first.getCoupon() + last.getCoupon()) + "元");
                //设置余额
                text_paydetail_balance.setText(NumUtil.num2half(first.getBalance() + last.getBalance()) + "元");
            }
        } else {
            if (payDataDriver != null) {
                text_paydetail_money.setText(NumUtil.num2half(payDataDriver.getActualCheques()) + "");
                text_paydetail_first.setText(NumUtil.num2half(payDataDriver.getDepositPay()) + "元");
                text_paydetail_last.setText(NumUtil.num2half(payDataDriver.getBosses()) + "元");
            }
        }
    }

    private void setPayWay(TextView textview, int payMethed) {
        if (PackageUtil.isClient()) {
            if (payMethed == 1) {
                textview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_zhifubao, 0, 0, 0);
            } else if (payMethed == 2) {
                textview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_weixin, 0, 0, 0);
            } else {
                textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        } else {
            textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.text_tripdetail_totaydetail:
//                break;
        }
    }
}
