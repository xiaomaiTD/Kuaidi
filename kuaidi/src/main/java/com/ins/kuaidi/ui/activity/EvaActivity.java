package com.ins.kuaidi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ins.kuaidi.R;
import com.ins.middle.common.AppData;
import com.ins.middle.common.AppVali;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.entity.Trip;
import com.ins.middle.utils.GlideUtil;
import com.ins.middle.view.DriverView;
import com.ins.middle.view.ProgView;
import com.sobey.common.common.LoadingViewUtil;
import com.sobey.common.utils.StrUtils;
import com.sobey.common.view.singlepopview.MySinglePopupWindow;

import java.util.ArrayList;

import com.ins.middle.ui.activity.BaseBackActivity;

import org.xutils.http.RequestParams;

import io.techery.properratingbar.ProperRatingBar;

public class EvaActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;

    private MySinglePopupWindow popupSingle;

    private View lay_eva_detail;
    private View btn_show;
    private DriverView driverView;

    private TextView btn_go;
    private EditText edit_eva_describe;
    private RatingBar rating_eva;

    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eva);
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
                Intent intent = new Intent(EvaActivity.this, ComplaintActivity.class);
                startActivity(intent);
                popupSingle.dismiss();
            }
        });
    }

    private void initView() {
        driverView = (DriverView) findViewById(R.id.driverView);
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        lay_eva_detail = findViewById(R.id.lay_eva_detail);
        btn_show = findViewById(R.id.btn_show);
        edit_eva_describe = (EditText) findViewById(R.id.edit_eva_describe);
        rating_eva = (RatingBar) findViewById(R.id.rating_eva);
        btn_go = (TextView) findViewById(R.id.btn_go);

        btn_show.setOnClickListener(this);
        btn_go.setOnClickListener(this);
        findViewById(R.id.btn_right).setOnClickListener(this);
    }

    private void initData() {
    }

    private void initCtrl() {
        if (trip != null) {
            driverView.setDriver(trip.getDriver(), trip);
        }
    }

    private void showEva() {
        lay_eva_detail.setVisibility(View.VISIBLE);
        btn_show.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_right:
                popupSingle.showPopupWindow(v);
                break;
            case R.id.btn_show:
                showEva();
                break;
            case R.id.btn_go:
                String detail = edit_eva_describe.getText().toString();
                int rating = (int) rating_eva.getRating();
                String msg = AppVali.evaadd(rating, detail);
                if (msg != null) {
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    netAddeva(trip.getId(), rating, detail);
                }
                break;
        }
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
                Toast.makeText(EvaActivity.this, text, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(EvaActivity.this, text, Toast.LENGTH_SHORT).show();
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
}
