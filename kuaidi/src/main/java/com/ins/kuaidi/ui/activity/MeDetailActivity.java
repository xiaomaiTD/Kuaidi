package com.ins.kuaidi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ins.kuaidi.R;
import com.ins.kuaidi.common.LoadingViewUtil;
import com.ins.kuaidi.utils.GlideUtil;
import com.sobey.common.helper.CropHelperSys;

public class MeDetailActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;

    private ImageView img_me_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medetail);
        setToolbar();

        initBase();
        initView();
        initData();
        initCtrl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initBase() {
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        img_me_header = (ImageView) findViewById(R.id.img_me_header);
        findViewById(R.id.btn_right).setOnClickListener(this);
    }

    private void initData() {
//        showin = LoadingViewUtil.showin(showingroup, R.layout.layout_loading, showin);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //加载成功
//                initCtrl();
//                LoadingViewUtil.showout(showingroup, showin);
//
//                //加载失败
////                LoadingViewUtil.showin(showingroup,R.layout.layout_lack,showin,new View.OnClickListener(){
////                    @Override
////                    public void onClick(View v) {
////                        initData();
////                    }
////                });
//            }
//        }, 500);
    }

    private void initCtrl() {
        GlideUtil.LoadCircleImgTest(this, img_me_header);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_right:
                intent.setClass(this, MeEditActivity.class);
                startActivity(intent);
                break;
        }
    }
}
