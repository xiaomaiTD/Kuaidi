package com.ins.kuaidi.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.alipay.sdk.app.PayTask;
import com.google.gson.reflect.TypeToken;
import com.ins.kuaidi.R;
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.sobey.common.utils.StrUtils;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;
import org.xutils.http.RequestParams;

import java.util.LinkedHashMap;
import java.util.Map;

import paytest.ins.com.library_alipay.SignUtils;

public class PayWeixinTestActivity extends Activity {

    public static String appid = "wxa3fb670d7394832e";

    private IWXAPI api;

    private TextView text_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay);

        text_log = (TextView) findViewById(R.id.text_log);

        api = WXAPIFactory.createWXAPI(this, appid);
        api.registerApp(appid);

        Button appayBtn = (Button) findViewById(R.id.appay_btn);
        appayBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                netPay();
            }
        });
    }


    public void netPay() {
        RequestParams params = new RequestParams("http://192.168.118.110:9528/Carpooling/mobile/wxPay/sign");
        params.addHeader("token", AppData.App.getToken());
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

                            text_log.setText(log);

                            api.sendReq(req);
                        } else {
                            Log.d("PAY_GET", "返回错误" + map.get("retmsg"));
                            Toast.makeText(PayWeixinTestActivity.this, "返回错误" + map.get("retmsg"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("PAY_GET", "服务器请求错误");
                        Toast.makeText(PayWeixinTestActivity.this, "服务器请求错误", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("PAY_GET", "异常：" + e.getMessage());
                    Toast.makeText(PayWeixinTestActivity.this, "异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(PayWeixinTestActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
