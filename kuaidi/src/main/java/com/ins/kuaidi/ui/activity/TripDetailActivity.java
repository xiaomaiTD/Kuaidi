package com.ins.kuaidi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ins.kuaidi.R;
import com.ins.middle.common.AppData;
import com.ins.middle.common.AppVali;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.entity.Eva;
import com.ins.middle.entity.PayData;
import com.ins.middle.entity.PayDataDriver;
import com.ins.middle.entity.Trip;
import com.ins.middle.ui.activity.PayDetailActivity;
import com.ins.middle.utils.PackageUtil;
import com.ins.middle.view.DriverView;
import com.ins.sharesdk.dialog.ShareDialog;
import com.ins.sharesdk.dialog.ShareHelper;
import com.sobey.common.common.LoadingViewUtil;
import com.ins.middle.utils.GlideUtil;
import com.sobey.common.utils.NumUtil;
import com.sobey.common.utils.StrUtils;
import com.sobey.common.view.singlepopview.MySinglePopupWindow;

import java.util.ArrayList;

import com.ins.middle.ui.activity.BaseAppCompatActivity;
import com.ins.middle.ui.activity.BaseBackActivity;

import org.xutils.http.RequestParams;

public class TripDetailActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;

    private DriverView driverView;
    private TextView text_tripdetail_money;
    private RatingBar rating_tripdetail;
    private TextView text_tripdetail_describe;
    private EditText edit_tripdetail_describe;
    private View lay_tripdetail_eva;
    private View lay_tripdetail_share;
    private View btn_go;

    private Trip trip;

    private MySinglePopupWindow popupSingle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tripdetail);
        setToolbar();

        initBase();
        initView();
        initCtrl();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupSingle != null) popupSingle.dismiss();
    }

    private void initBase() {
        if (getIntent().hasExtra("trip")) {
            trip = (Trip) getIntent().getSerializableExtra("trip");
        }
        popupSingle = new MySinglePopupWindow(this);
        popupSingle.setResults(new ArrayList<String>() {{
            add("投诉");
        }});
        popupSingle.setShadowView(findViewById(R.id.lay_shadow));
        popupSingle.setOnPopSingleClickListenner(new MySinglePopupWindow.OnPopSingleClickListenner() {
            @Override
            public void OnPopClick(String name) {
                if (trip != null) {
                    netIsComplain(trip.getId());
                } else {
                    Toast.makeText(TripDetailActivity.this, "错误：没有获取到订单", Toast.LENGTH_SHORT).show();
                }
                popupSingle.dismiss();
            }
        });
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);

        findViewById(R.id.text_tripdetail_totaydetail).setOnClickListener(this);
        findViewById(R.id.btn_right).setOnClickListener(this);
        findViewById(R.id.text_share_wechat).setOnClickListener(this);
        findViewById(R.id.text_share_wechatmoments).setOnClickListener(this);
        findViewById(R.id.text_share_qq).setOnClickListener(this);
        findViewById(R.id.text_share_xinlangweibo).setOnClickListener(this);

        driverView = (DriverView) findViewById(R.id.driverView);
        text_tripdetail_money = (TextView) findViewById(R.id.text_tripdetail_money);
        rating_tripdetail = (RatingBar) findViewById(R.id.rating_tripdetail);
        text_tripdetail_describe = (TextView) findViewById(R.id.text_tripdetail_describe);
        edit_tripdetail_describe = (EditText) findViewById(R.id.edit_tripdetail_describe);
        lay_tripdetail_eva = findViewById(R.id.lay_tripdetail_eva);
        lay_tripdetail_share = findViewById(R.id.lay_tripdetail_share);
        btn_go = findViewById(R.id.btn_go);

        btn_go.setOnClickListener(this);
        text_tripdetail_describe.setOnClickListener(this);
    }

    private void initData() {
        if (trip != null) {
            netGetApplyDetail(trip.getId());
        }
    }

    private void initCtrl() {
        if (trip != null) {
            String payDetail = trip.getPayDetail();
            String bossesPayDetail = trip.getBossesPayDetail();
            Gson gson = new Gson();
            PayData first = gson.fromJson(payDetail, PayData.class);
            PayData last = gson.fromJson(bossesPayDetail, PayData.class);

            driverView.setDriver(trip.getDriver(), trip);
            if (first != null && last != null) {
                text_tripdetail_money.setText(NumUtil.num2half(first.getActualPay() + last.getActualPay()) + "");
            }
        }

//        lay_tripdetail_eva.setVisibility(View.GONE);
        setEvaData(null);
    }

    private void setEvaData(Eva eva) {
        if (trip.getStatus() == Trip.STA_2007) {
            lay_tripdetail_eva.setVisibility(View.GONE);
            lay_tripdetail_share.setVisibility(View.GONE);
            text_tripdetail_describe.setVisibility(View.GONE);
        } else {
            if (eva != null) {
                rating_tripdetail.setIsIndicator(true);
                rating_tripdetail.setRating(((float) eva.getScoreCount()));
                text_tripdetail_describe.setText(!StrUtils.isEmpty(eva.getContent()) ? eva.getContent() : "您的评价，让我们做得更好");
                text_tripdetail_describe.setTextColor(ContextCompat.getColor(this, R.color.com_text_blank));
                text_tripdetail_describe.setClickable(false);
                lay_tripdetail_eva.setVisibility(View.GONE);
                lay_tripdetail_share.setVisibility(View.VISIBLE);
            } else {
                rating_tripdetail.setIsIndicator(false);
                rating_tripdetail.setRating(0);
                text_tripdetail_describe.setText("您还未做出评价");
                text_tripdetail_describe.setTextColor(ContextCompat.getColor(this, R.color.kd_yellow));
                text_tripdetail_describe.setClickable(true);
                lay_tripdetail_eva.setVisibility(View.VISIBLE);
                lay_tripdetail_share.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        String url = AppData.Url.shareUrl + "?distributionCode=" + AppData.App.getUser().getDistributionCode();
        switch (v.getId()) {
            case R.id.btn_go:
                String detail = edit_tripdetail_describe.getText().toString();
                int rating = (int) rating_tripdetail.getRating();
                String msg = AppVali.evaadd(rating, detail);
                if (msg != null) {
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    netAddeva(trip.getId(), rating, detail);
                }
                break;
            case R.id.btn_right:
                popupSingle.showPopupWindow(v);
                break;
            case R.id.text_tripdetail_describe:
                //lay_tripdetail_eva.setVisibility(View.VISIBLE);
                break;
            case R.id.text_tripdetail_totaydetail:
                Intent intent = new Intent(this, PayDetailActivity.class);
                intent.putExtra("trip", trip);
                startActivity(intent);
                break;
            case R.id.text_share_wechat:
                ShareHelper.showShareWeixin(this, "陛下", "您有一封新的奏折~", url, "http://v1.qzone.cc/avatar/201408/04/16/16/53df416d26b6c338.jpg%21200x200.jpg");
                break;
            case R.id.text_share_wechatmoments:
                ShareHelper.showSharePengyouquan(this, "陛下", "您有一封新的奏折~", url, "http://v1.qzone.cc/avatar/201408/04/16/16/53df416d26b6c338.jpg%21200x200.jpg");
                break;
            case R.id.text_share_qq:
                ShareHelper.showShareQQ(this, "陛下", "您有一封新的奏折~", url, "http://v1.qzone.cc/avatar/201408/04/16/16/53df416d26b6c338.jpg%21200x200.jpg");
                break;
            case R.id.text_share_xinlangweibo:
                ShareHelper.showShareSina(this, "陛下", "您有一封新的奏折~", url, "http://v1.qzone.cc/avatar/201408/04/16/16/53df416d26b6c338.jpg%21200x200.jpg");
                break;
        }
    }

    public void netGetApplyDetail(int orderId) {
        RequestParams params = new RequestParams(AppData.Url.iseva);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("orderId", orderId + "");
        CommonNet.samplepost(params, Eva.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                Eva eva = (Eva) pojo;
                setEvaData(eva);
                LoadingViewUtil.showout(showingroup, showin);
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(TripDetailActivity.this, text, Toast.LENGTH_SHORT).show();
                showin = LoadingViewUtil.showin(showingroup, com.ins.middle.R.layout.layout_fail, showin, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initData();
                    }
                });
            }

            @Override
            public void netStart(int status) {
                showin = LoadingViewUtil.showin(showingroup, com.ins.middle.R.layout.layout_loading, showin);
            }
        });
    }

    public void netAddeva(int orderId, int scoreCount, String content) {
        RequestParams params = new RequestParams(AppData.Url.addeva);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("content", content);
        params.addBodyParameter("scoreCount", scoreCount + "");
        params.addBodyParameter("orderId", orderId + "");
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                Toast.makeText(TripDetailActivity.this, text, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(TripDetailActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netStart(int status) {
                btn_go.setEnabled(false);
            }

            @Override
            public void netEnd(int status) {
                btn_go.setEnabled(true);
            }
        });
    }

    public void netIsComplain(int orderId) {
        RequestParams params = new RequestParams(AppData.Url.addeva);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("orderId", orderId + "");
        CommonNet.samplepost(params, Integer.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                int isComplain = (Integer) pojo;
                if (isComplain == 0) {
                    Intent intent = new Intent(TripDetailActivity.this, ComplaintActivity.class);
                    intent.putExtra("orderId", trip.getId());
                    startActivity(intent);
                }else {
                    Snackbar.make(showingroup, "您已经投诉过了", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Intent intent = new Intent(TripDetailActivity.this, ComplaintActivity.class);
                intent.putExtra("orderId", trip.getId());
                startActivity(intent);
            }
        });
    }
}
