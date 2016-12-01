package com.ins.driver.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ins.driver.R;
import com.ins.middle.common.AppData;
import com.ins.middle.common.AppVali;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.entity.Eva;
import com.ins.middle.entity.PayData;
import com.ins.middle.entity.PayDataDriver;
import com.ins.middle.entity.Trip;
import com.ins.middle.ui.activity.BaseBackActivity;
import com.ins.middle.ui.activity.PayDetailActivity;
import com.ins.middle.view.DriverView;
import com.sobey.common.common.LoadingViewUtil;
import com.sobey.common.utils.StrUtils;
import com.sobey.common.view.singlepopview.MySinglePopupWindow;

import org.xutils.http.RequestParams;

import java.util.ArrayList;

public class TripDetailActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;

    private TextView text_tripdetail_money;
    private RatingBar rating_tripdetail;
    private TextView text_tripdetail_describe;

    private Trip trip;

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
    }

    private void initBase() {
        if (getIntent().hasExtra("trip")) {
            trip = (Trip) getIntent().getSerializableExtra("trip");
        }
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);

        findViewById(R.id.text_tripdetail_totaydetail).setOnClickListener(this);

        text_tripdetail_money = (TextView) findViewById(R.id.text_tripdetail_money);
        rating_tripdetail = (RatingBar) findViewById(R.id.rating_tripdetail);
        text_tripdetail_describe = (TextView) findViewById(R.id.text_tripdetail_describe);
    }

    private void initData() {
        if (trip != null) {
            netGetApplyDetail(trip.getId());
        }
    }

    private void initCtrl() {
        if (trip != null) {
            String driverDetail = trip.getDriverDetail();
            Gson gson = new Gson();
            PayDataDriver payDataDriver = gson.fromJson(driverDetail, PayDataDriver.class);

            text_tripdetail_money.setText(payDataDriver.getActualCheques() + "");
        }
        setEvaData(null);
    }

    private void setEvaData(Eva eva) {
        if (eva != null) {
            rating_tripdetail.setRating(((float) eva.getScoreCount()) / 5);
            text_tripdetail_describe.setText(!StrUtils.isEmpty(eva.getContent()) ? eva.getContent() : "");
            text_tripdetail_describe.setTextColor(ContextCompat.getColor(this, R.color.com_text_blank));
        } else {
            text_tripdetail_describe.setText("该乘客还未做出评价");
            text_tripdetail_describe.setTextColor(ContextCompat.getColor(this, R.color.kd_yellow));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_tripdetail_totaydetail:
                Intent intent = new Intent(this, PayDetailActivity.class);
                intent.putExtra("trip", trip);
                startActivity(intent);
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
}
