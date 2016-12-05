package com.ins.kuaidi.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.reflect.TypeToken;
import com.ins.kuaidi.R;
import com.ins.middle.entity.PayData;
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.EventOrder;
import com.ins.middle.entity.Trip;
import com.ins.middle.entity.User;
import com.ins.middle.ui.dialog.DialogLoading;
import com.sobey.common.common.LoadingViewUtil;
import com.ins.middle.ui.activity.BaseBackActivity;
import com.sobey.common.utils.NumUtil;

import org.greenrobot.eventbus.EventBus;
import org.xutils.http.RequestParams;

import java.util.LinkedHashMap;
import java.util.Map;

import paytest.ins.com.library_alipay.PayResult;
import paytest.ins.com.library_alipay.SignUtils;

//type:0 支付定金 1：支付定金成功 2：支付尾款 3：支付尾款成功
public class PayActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;

    private ImageView img_pay_status;
    private TextView text_pay_title;
    private TextView text_pay_money;
    private TextView btn_go;

    private TextView text_pay_total;
    private TextView text_pay_this_name;
    private TextView text_pay_this;
    private TextView text_pay_coupon;
    private TextView text_pay_balance;
    private TextView text_pay_payway;
    private View lay_pay_total;
    private View lay_pay_this;
    private View lay_pay_coupon;
    private View lay_pay_balance;

    private int type;
    private int orderId;
    private Trip trip;
    private PayData payData;

    private DialogLoading dialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        setToolbar();

        initBase();
        initView();
        initCtrl();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialogLoading != null) dialogLoading.dismiss();
    }

    private void initBase() {
        dialogLoading = new DialogLoading(this, "正在请求支付");
        if (getIntent().hasExtra("type")) {
            type = getIntent().getIntExtra("type", 0);
        }
        if (getIntent().hasExtra("trip")) {
            trip = (Trip) getIntent().getSerializableExtra("trip");
            orderId = trip.getId();
        }
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        btn_go = (TextView) findViewById(R.id.btn_go);
        img_pay_status = (ImageView) findViewById(R.id.img_pay_status);
        text_pay_title = (TextView) findViewById(R.id.text_pay_title);
        text_pay_money = (TextView) findViewById(R.id.text_pay_money);

        text_pay_total = (TextView) findViewById(R.id.text_pay_total);
        text_pay_this_name = (TextView) findViewById(R.id.text_pay_this_name);
        text_pay_this = (TextView) findViewById(R.id.text_pay_this);
        text_pay_coupon = (TextView) findViewById(R.id.text_pay_coupon);
        text_pay_balance = (TextView) findViewById(R.id.text_pay_balance);
        text_pay_payway = (TextView) findViewById(R.id.text_pay_payway);
        lay_pay_total = findViewById(R.id.lay_pay_total);
        lay_pay_this = findViewById(R.id.lay_pay_this);
        lay_pay_coupon = findViewById(R.id.lay_pay_coupon);
        lay_pay_balance = findViewById(R.id.lay_pay_balance);

        btn_go.setOnClickListener(this);
        text_pay_payway.setOnClickListener(this);
    }

    private void initData() {
        netGetPayData();
    }

    private void initCtrl() {
        setTypeData(type);
        setPayType(-1);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_go:
                if (payData == null) {
                    return;
                }
                if (type == 0) {
                    //实际支付金额不是0才进行三方支付，否则使用品台内部支付
                    if (payData.getActualPay() != 0) {
                        netPayFirstZhifubao(orderId, 0);
                    } else {
                        netPayFirstTest(orderId, 0);
                    }
                } else if (type == 2) {
                    if (payData.getActualPay() != 0) {
                        netPayFirstZhifubao(orderId, 1);
                    } else {
                        netPayFirstTest(orderId, 1);
                    }
                }
                break;
            case R.id.text_pay_payway:
                intent.setClass(this, PaywayActivity.class);
                intent.putExtra("type", 1);
                startActivityForResult(intent, RESULT_PAYWAY);
                break;
        }
    }

    private static final int RESULT_PAYWAY = 0xf101;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_PAYWAY:
                if (resultCode == RESULT_OK) {
                    int payType = data.getIntExtra("type", 0);
                    setPayType(payType);
                }
                break;
        }
    }

    private boolean toEVA = false;

    @Override
    public void finish() {
        super.finish();
        if (toEVA) {
            Intent intent = new Intent();
            intent.setClass(this, EvaActivity.class);
            intent.putExtra("trip", trip);
            startActivity(intent);
        }
    }

    private void setTypeData(int type) {
        if (type == 0) {
            setToolbar("定金支付");
            text_pay_this_name.setText("定金费用");
            text_pay_title.setText("定金费用");
            img_pay_status.setVisibility(View.GONE);
            btn_go.setVisibility(View.VISIBLE);
        } else if (type == 1) {
            setToolbar("定金支付");
            text_pay_this_name.setText("定金费用");
            text_pay_title.setText("支付成功");
            img_pay_status.setVisibility(View.VISIBLE);
            btn_go.setVisibility(View.GONE);
        } else if (type == 2) {
            setToolbar("结算");
            text_pay_this_name.setText("尾款费用");
            text_pay_title.setText("尾款支付");
            img_pay_status.setVisibility(View.GONE);
            btn_go.setVisibility(View.VISIBLE);
        } else if (type == 3) {
            setToolbar("结算");
            text_pay_this_name.setText("尾款费用");
            text_pay_title.setText("支付成功");
            img_pay_status.setVisibility(View.VISIBLE);
            btn_go.setVisibility(View.GONE);
        }
    }

    private void setPayData(PayData paydata) {
        text_pay_money.setText(NumUtil.num2half(paydata.getActualPay(), 2)+"");
        text_pay_total.setText(NumUtil.num2half(paydata.getTotal(), 2) + "元");
        text_pay_this.setText(NumUtil.num2half(paydata.getThisTotalPay(), 2) + "元");
        text_pay_coupon.setText("-" + NumUtil.num2half(paydata.getCoupon(), 2) + "元");
        text_pay_balance.setText(NumUtil.num2half(paydata.getBalance(), 2) + "元");
        lay_pay_coupon.setVisibility(paydata.getCoupon() == 0 ? View.GONE : View.VISIBLE);
        lay_pay_balance.setVisibility(paydata.getBalance() == 0 ? View.GONE : View.VISIBLE);

        if (paydata.getActualPay() == 0) {
            setPayType(2);
        }
    }

    /**
     * type 0:支付宝 1:微信 2:余额支付 -1 获取用户默认
     */
    private void setPayType(int type) {
        if (type == -1) {
            User user = AppData.App.getUser();
            if (user == null) return;
            type = user.getFristPayMethod();
        }
        if (type == 0) {
            text_pay_payway.setText("支付宝支付");
            text_pay_payway.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_zhifubao, 0, R.drawable.icon_item_next, 0);
        } else if (type == 1) {
            text_pay_payway.setText("微信支付");
            text_pay_payway.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_weixin, 0, R.drawable.icon_item_next, 0);
        } else if (type == 2) {
            text_pay_payway.setText("余额支付");
            text_pay_payway.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_item_next, 0);
        }
    }


    public void netGetPayData() {
        int flag = 0;
        if (type == 0 || type == 1) {
            flag = 0;
        } else if (type == 2 || type == 3) {
            flag = 1;
        }
        RequestParams params = new RequestParams(AppData.Url.requestBalance);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("flag", flag + "");
        params.addBodyParameter("orderId", orderId + "");
        CommonNet.samplepost(params, PayData.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                if (pojo != null) {
                    payData = (PayData) pojo;
                    setPayData(payData);
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
                Toast.makeText(PayActivity.this, text, Toast.LENGTH_SHORT).show();
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

    public void netPayFirstTest(int orderId, final int flag) {
        RequestParams params = new RequestParams(AppData.Url.pay);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("orderId", orderId + "");
        params.addBodyParameter("flag", flag + "");
        CommonNet.samplepost(params, Float.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                Toast.makeText(PayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                paySuccess();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(PayActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netEnd(int status) {
                dialogLoading.hide();
            }

            @Override
            public void netStart(int status) {
                dialogLoading.show();
            }
        });
    }

    ////////////////////////
    public void netPayFirstZhifubao(int orderId, final int flag) {
        RequestParams params = new RequestParams(AppData.Url.sign);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("orderId", orderId + "");
        params.addBodyParameter("flag", flag + "");
        CommonNet.samplepost(params, new TypeToken<LinkedHashMap<String, String>>() {
        }.getType(), new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                Map<String, String> map = (LinkedHashMap<String, String>) pojo;

                final String orderInfo = SignUtils.getOrderInfo(map);
                Log.e("liao", "orderInfo:" + orderInfo);

                Runnable payRunnable = new Runnable() {

                    @Override
                    public void run() {
                        // 构造PayTask 对象
                        PayTask alipay = new PayTask(PayActivity.this);
                        // 调用支付接口，获取支付结果
                        String result = alipay.pay(orderInfo, true);

                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }
                };

                // 必须异步调用
                Thread payThread = new Thread(payRunnable);
                payThread.start();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(PayActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 支付成功后的业务逻辑
     */
    private void paySuccess() {
        if (type == 0 || type == 1) {
            //定金支付成功
            setTypeData(1);
            EventOrder eventOrder = new EventOrder();
            eventOrder.setAboutOrder("8");
            EventBus.getDefault().post(eventOrder);
        } else if (type == 2 || type == 3) {
            //尾款支付成功
            toEVA = true;
            setTypeData(3);
            EventOrder eventOrder = new EventOrder();
            eventOrder.setAboutOrder("101");
            EventBus.getDefault().post(eventOrder);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(PayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        paySuccess();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(PayActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(PayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };
}
