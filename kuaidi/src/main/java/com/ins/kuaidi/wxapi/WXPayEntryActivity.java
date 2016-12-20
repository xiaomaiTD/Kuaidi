package com.ins.kuaidi.wxapi;

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
import com.ins.kuaidi.ui.activity.EvaActivity;
import com.ins.kuaidi.ui.activity.PaywayActivity;
import com.ins.middle.entity.PayData;
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.EventOrder;
import com.ins.middle.entity.Trip;
import com.ins.middle.entity.User;
import com.ins.middle.ui.dialog.DialogLoading;
import com.ins.middle.ui.dialog.DialogMsg;
import com.ins.middle.ui.dialog.DialogSure;
import com.sobey.common.common.LoadingViewUtil;
import com.ins.middle.ui.activity.BaseBackActivity;
import com.sobey.common.utils.NumUtil;
import com.sobey.common.utils.StrUtils;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.xutils.http.RequestParams;

import java.util.LinkedHashMap;
import java.util.Map;

import paytest.ins.com.library_alipay.PayResult;
import paytest.ins.com.library_alipay.SignUtils;

//type:0 支付定金 1：支付定金成功 2：支付尾款 3：支付尾款成功
public class WXPayEntryActivity extends BaseBackActivity implements View.OnClickListener, IWXAPIEventHandler {

    //微信支付appId
    public static String appid = "wxa3fb670d7394832e";
    private IWXAPI api;

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
    private int payType;    //支付方式 0:支付宝 1:微信 2:余额支付
    private int orderId;
    private Trip trip;
    private PayData payData;

    private DialogLoading dialogLoading;
    private DialogMsg dialogSure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        setToolbar();

        //微信初始化
        api = WXAPIFactory.createWXAPI(this, appid);
        api.registerApp(appid);
        api.handleIntent(getIntent(), this);

        initBase();
        initView();
        initCtrl();
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialogLoading != null) dialogLoading.dismiss();
        if (dialogSure != null) dialogSure.dismiss();
    }

    private void initBase() {
        dialogLoading = new DialogLoading(this, "正在请求支付");
        dialogSure = new DialogMsg(this, "您还没有安装微信，请先下载安装微信最新客户端，或者选择其他支付方式");
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
        //setPayType(-1);
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
                    if (payType == 0) {
                        netPayZhifubao(orderId, 0);
                    } else if (payType == 1) {
                        netPayWeixin(orderId, 0);
                    } else if (payType == 2) {
                        netPayCash(orderId, 0);
                    }
                } else if (type == 2) {
                    if (payType == 0) {
                        netPayZhifubao(orderId, 1);
                    } else if (payType == 1) {
                        netPayWeixin(orderId, 1);
                    } else if (payType == 2) {
                        netPayCash(orderId, 1);
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
            text_pay_payway.setClickable(true);
        } else if (type == 1) {
            setToolbar("定金支付");
            text_pay_this_name.setText("定金费用");
            text_pay_title.setText("支付成功");
            img_pay_status.setVisibility(View.VISIBLE);
            btn_go.setVisibility(View.GONE);
            text_pay_payway.setClickable(false);
        } else if (type == 2) {
            setToolbar("结算");
            text_pay_this_name.setText("尾款费用");
            text_pay_title.setText("尾款支付");
            img_pay_status.setVisibility(View.GONE);
            btn_go.setVisibility(View.VISIBLE);
            text_pay_payway.setClickable(true);
        } else if (type == 3) {
            setToolbar("结算");
            text_pay_this_name.setText("尾款费用");
            text_pay_title.setText("支付成功");
            img_pay_status.setVisibility(View.VISIBLE);
            btn_go.setVisibility(View.GONE);
            text_pay_payway.setClickable(false);
        }
    }

    private void setPayData(PayData paydata) {
        text_pay_money.setText(NumUtil.num2half(paydata.getActualPay(), 2) + "");
        text_pay_total.setText(NumUtil.num2half(paydata.getTotal(), 2) + "元");
        text_pay_this.setText(NumUtil.num2half(paydata.getThisTotalPay(), 2) + "元");
        text_pay_coupon.setText("-" + NumUtil.num2half(paydata.getCoupon(), 2) + "元");
        text_pay_balance.setText(NumUtil.num2half(paydata.getBalance(), 2) + "元");
        lay_pay_coupon.setVisibility(paydata.getCoupon() == 0 ? View.GONE : View.VISIBLE);
        lay_pay_balance.setVisibility(paydata.getBalance() == 0 ? View.GONE : View.VISIBLE);

        if (paydata.getActualPay() == 0) {
            setPayType(2);
        } else {
            setPayType(-1);
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
            payType = type;
            text_pay_payway.setText("支付宝支付");
            text_pay_payway.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_zhifubao, 0, R.drawable.icon_item_next, 0);
            text_pay_payway.setClickable(true);
        } else if (type == 1) {
            payType = type;
            text_pay_payway.setText("微信支付");
            text_pay_payway.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_weixin, 0, R.drawable.icon_item_next, 0);
            text_pay_payway.setClickable(true);
        } else if (type == 2) {
            payType = type;
            text_pay_payway.setText("余额支付");
            text_pay_payway.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_item_next, 0);
            text_pay_payway.setClickable(false);
        }
    }


    /**
     * 请求支付信息
     */
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
                Toast.makeText(WXPayEntryActivity.this, text, Toast.LENGTH_SHORT).show();
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

    /**
     * 余额支付
     */
    public void netPayCash(int orderId, final int flag) {
        RequestParams params = new RequestParams(AppData.Url.pay);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("orderId", orderId + "");
        params.addBodyParameter("flag", flag + "");
        CommonNet.samplepost(params, Float.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                Toast.makeText(WXPayEntryActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                paySuccess();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(WXPayEntryActivity.this, text, Toast.LENGTH_SHORT).show();
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

    /**
     * 支付宝支付
     */
    public void netPayZhifubao(int orderId, final int flag) {
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
                        PayTask alipay = new PayTask(WXPayEntryActivity.this);
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
                Toast.makeText(WXPayEntryActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netStart(int status) {
                btn_go.setEnabled(false);
            }
        });
    }

    /**
     * 支付宝支付回调
     */
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
                        Toast.makeText(WXPayEntryActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        paySuccess();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(WXPayEntryActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(WXPayEntryActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
            btn_go.setEnabled(true);
        }

        ;
    };

    /**
     * 微信支付
     */
    public void netPayWeixin(int orderId, final int flag) {
        if (!api.isWXAppInstalled()) {
            dialogSure.show();
            return;
        }
        RequestParams params = new RequestParams(AppData.Url.signWeixin);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("orderId", orderId + "");
        params.addBodyParameter("flag", flag + "");
        params.addBodyParameter("ip", "101.201.222.161");
        CommonNet.samplepost(params, new TypeToken<LinkedHashMap<String, String>>() {
        }.getType(), new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                Map<String, String> map = (LinkedHashMap<String, String>) pojo;

                try {
                    if (!StrUtils.isEmpty(map)) {
                        if (!map.containsKey("retcode")) {
                            final PayReq req = new PayReq();
                            req.appId = appid;
                            req.partnerId = map.get("partnerid");
                            req.prepayId = map.get("prepayid");
                            req.nonceStr = map.get("noncestr");
                            req.timeStamp = map.get("timestamp");
                            req.packageValue = map.get("package");
                            req.sign = map.get("sign");

                            String log = "req.appId:\n" + req.appId +
                                    "\nreq.partnerId:\n" + req.partnerId +
                                    "\nreq.prepayId:\n" + req.prepayId +
                                    "\nreq.nonceStr:\n" + req.nonceStr +
                                    "\nreq.timeStamp:\n" + req.timeStamp +
                                    "\nreq.packageValue:\n" + req.packageValue +
                                    "\nreq.sign:\n" + req.sign;
                            Log.e("pay", log);

                            api.sendReq(req);
                        } else {
                            Log.d("PAY_GET", "返回错误" + map.get("retmsg"));
                            Toast.makeText(WXPayEntryActivity.this, "返回错误" + map.get("retmsg"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("PAY_GET", "服务器请求错误");
                        Toast.makeText(WXPayEntryActivity.this, "服务器请求错误", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("PAY_GET", "异常：" + e.getMessage());
                    Toast.makeText(WXPayEntryActivity.this, "异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(WXPayEntryActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netStart(int status) {
                btn_go.setEnabled(false);
            }
        });
    }

    /**
     * 微信支付回调
     */
    @Override
    public void onReq(BaseReq baseReq) {
    }

    /**
     * 微信支付回调
     */
    @Override
    public void onResp(BaseResp resp) {
        Log.d("weixin", "onPayFinish, errCode = " + resp.errCode);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case 0:
                    //成功
                    paySuccess();
                    break;
                case -1:
                    //失败
                    Toast.makeText(this, "失败：-1", Toast.LENGTH_SHORT).show();
                    break;
                case -2:
                    //用户取消
                    Toast.makeText(this, "用户取消：-2", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        btn_go.setEnabled(true);
    }
}
