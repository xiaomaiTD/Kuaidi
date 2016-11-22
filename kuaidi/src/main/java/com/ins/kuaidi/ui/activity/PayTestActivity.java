package com.ins.kuaidi.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.EventOrder;
import com.ins.middle.ui.activity.BaseBackActivity;
import com.ins.middle.ui.dialog.DialogLoading;
import com.sobey.common.common.LoadingViewUtil;
import com.sobey.common.utils.StrUtils;

import org.greenrobot.eventbus.EventBus;
import org.xutils.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import paytest.ins.com.library_alipay.PayResult;
import paytest.ins.com.library_alipay.SignUtils;

//type:0 支付定金 1：支付定金成功 2：支付尾款 3：支付尾款成功
public class PayTestActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;

    private ImageView img_pay_status;
    private TextView text_pay_title;
    private TextView text_pay_money;
    private TextView btn_go;
    private TextView btn_go1;

    private DialogLoading dialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytest);
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
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        btn_go = (TextView) findViewById(R.id.btn_go);
        btn_go1 = (TextView) findViewById(R.id.btn_go1);
        img_pay_status = (ImageView) findViewById(R.id.img_pay_status);
        text_pay_title = (TextView) findViewById(R.id.text_pay_title);
        text_pay_money = (TextView) findViewById(R.id.text_pay_money);

        btn_go.setOnClickListener(this);
        btn_go1.setOnClickListener(this);
    }

    private void initData() {
    }

    private void initCtrl() {
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_go:
                netPay();
                break;
            case R.id.btn_go1:
//                pay();
                break;
        }
    }

    ////////////////////////
    public void netPay() {
        RequestParams params = new RequestParams(AppData.Url.sign);
        params.addHeader("token", AppData.App.getToken());
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
                        PayTask alipay = new PayTask(PayTestActivity.this);
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
                Toast.makeText(PayTestActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
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
                        Toast.makeText(PayTestActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(PayTestActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(PayTestActivity.this, "支付失败", Toast.LENGTH_SHORT).show();

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
