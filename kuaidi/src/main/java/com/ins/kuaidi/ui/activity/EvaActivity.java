package com.ins.kuaidi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

import com.ins.kuaidi.R;
import com.ins.kuaidi.common.LoadingViewUtil;
import com.ins.kuaidi.utils.GlideUtil;
import com.sobey.common.view.singlepopview.MySinglePopupWindow;

import java.util.ArrayList;

public class EvaActivity extends BaseBackActivity implements View.OnClickListener{

    private ViewGroup showingroup;
    private View showin;

    private MySinglePopupWindow popupSingle;

    private View lay_eva_detail;
    private View btn_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eva);
        setToolbar();

        initBase();
        initView();
//        initCtrl();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupSingle != null) popupSingle.dismiss();
    }

    private void initBase() {
        popupSingle = new MySinglePopupWindow(this);
        popupSingle.setResults(new ArrayList<String>(){{add("投诉");}});
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
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        lay_eva_detail = findViewById(R.id.lay_eva_detail);
        btn_show = findViewById(R.id.btn_show);

        btn_show.setOnClickListener(this);
        findViewById(R.id.btn_right).setOnClickListener(this);
        findViewById(R.id.btn_go).setOnClickListener(this);
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
        }, 1000);
    }

    private void initCtrl() {
        GlideUtil.LoadCircleImgTest(this, findViewById(R.id.img_driver_header));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_right:
                popupSingle.showPopupWindow(v);
                break;
            case R.id.btn_show:
                lay_eva_detail.setVisibility(View.VISIBLE);
                btn_show.setVisibility(View.GONE);
                break;
            case R.id.btn_go:
                break;
        }
    }
}
