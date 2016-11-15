package com.ins.kuaidi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ins.kuaidi.R;
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.EventOrder;
import com.ins.middle.ui.dialog.DialogLoading;
import com.sobey.common.common.LoadingViewUtil;
import com.ins.middle.ui.activity.BaseBackActivity;

import org.greenrobot.eventbus.EventBus;
import org.xutils.http.RequestParams;

//type:0 支付定金 1：支付定金成功 2：支付尾款 3：支付尾款成功
public class PayActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;

    private ImageView img_pay_status;
    private TextView text_pay_title;
    private TextView text_pay_money;
    private TextView btn_go;

    private int type;
    private int orderId;

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
        if (getIntent().hasExtra("orderId")) {
            orderId = getIntent().getIntExtra("orderId", 0);
        }
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        btn_go = (TextView) findViewById(R.id.btn_go);
        img_pay_status = (ImageView) findViewById(R.id.img_pay_status);
        text_pay_title = (TextView) findViewById(R.id.text_pay_title);
        text_pay_money = (TextView) findViewById(R.id.text_pay_money);

        btn_go.setOnClickListener(this);
    }

    private void initData() {
        netGetPayData();
    }

    private void initCtrl() {
        setTypeData(type);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_go:
                if (type == 0) {
                    netPayFirst(orderId, 0);
                } else if (type == 2) {
                    netPayFirst(orderId, 1);
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
            intent.putExtra("orderId", orderId);
            startActivity(intent);
        }
    }

    private void setTypeData(int type) {
        if (type == 0) {
            setToolbar("定金支付");
            text_pay_title.setText("定金费用");
            img_pay_status.setVisibility(View.GONE);
            btn_go.setVisibility(View.VISIBLE);
        } else if (type == 1) {
            setToolbar("定金支付");
            text_pay_title.setText("支付成功");
            img_pay_status.setVisibility(View.VISIBLE);
            btn_go.setVisibility(View.GONE);
        } else if (type == 2) {
            setToolbar("结算");
            text_pay_title.setText("尾款支付");
            img_pay_status.setVisibility(View.GONE);
            btn_go.setVisibility(View.VISIBLE);
        } else if (type == 3) {
            setToolbar("结算");
            text_pay_title.setText("支付成功");
            img_pay_status.setVisibility(View.VISIBLE);
            btn_go.setVisibility(View.GONE);
        }
    }

    private void setPayData(float money) {
        text_pay_money.setText(money + "");
    }


    public void netGetPayData() {
        RequestParams params = new RequestParams(AppData.Url.requestBalance);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("flag", "0");
        params.addBodyParameter("orderId", orderId + "");
        CommonNet.samplepost(params, Float.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                if (pojo != null) {
                    Float money = (Float) pojo;
                    setPayData(money);
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

    public void netPayFirst(int orderId, final int flag) {
        RequestParams params = new RequestParams(AppData.Url.pay);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("orderId", orderId + "");
        params.addBodyParameter("flag", flag + "");
        CommonNet.samplepost(params, Float.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                Toast.makeText(PayActivity.this, text, Toast.LENGTH_SHORT).show();
                if (flag == 0) {
                    //定金支付成功
                    setTypeData(1);
                    EventOrder eventOrder = new EventOrder();
                    eventOrder.setAboutOrder("8");
                    EventBus.getDefault().post(eventOrder);
                } else {
                    //尾款支付成功
                    toEVA = true;
                    setTypeData(3);
                    EventOrder eventOrder = new EventOrder();
                    eventOrder.setAboutOrder("101");
                    EventBus.getDefault().post(eventOrder);
                }
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
}
