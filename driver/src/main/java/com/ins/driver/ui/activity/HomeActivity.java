package com.ins.driver.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ins.driver.R;
import com.ins.middle.entity.TestEntity;
import com.ins.middle.ui.activity.BaseAppCompatActivity;
import com.ins.middle.ui.dialog.DialogLoading;
import com.ins.middle.utils.GlideUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class HomeActivity extends BaseAppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private DrawerLayout drawer;
    private ImageView img_navi_header;
    private NavigationView navi;

    private ImageView img_user;
    private TextView text_title;
    private MapView mapView;
    private BaiduMap baiduMap;

    private DialogLoading dialogLoading;

//    private static final int RESULT_SEARCHADDRESS = 0xf101;
    private int type = 0;   //0:点击出发地点 1:点击目的地

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setToolbar(null, false);
        EventBus.getDefault().register(this);

        initBase();
        initView();
        initCtrl();
        initData();
    }

    @Subscribe
    public void onEventMainThread(TestEntity position) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (dialogLoading != null) dialogLoading.dismiss();
    }

    private long exitTime;
    @Override
    public void onBackPressed() {
        //双击退出
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            super.finish();
        }
    }

    private void initBase() {
        dialogLoading = new DialogLoading(this, "正在处理");
    }

    private void initView() {
        mapView = (MapView) findViewById(R.id.mapView);
        baiduMap = mapView.getMap();
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        navi = (NavigationView) findViewById(R.id.navi);
        img_user = (ImageView) findViewById(R.id.img_home_user);
        text_title = (TextView) findViewById(R.id.text_home_title);

        findViewById(R.id.img_home_msg).setOnClickListener(this);
        findViewById(R.id.img_home_order).setOnClickListener(this);
        img_navi_header = (ImageView) navi.getHeaderView(0).findViewById(R.id.img_navi_header);
        img_navi_header.setOnClickListener(this);
    }

    private void initCtrl() {
        text_title.setOnClickListener(this);
        img_user.setOnClickListener(this);
        //navi进展滚动条
        if (navi != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navi.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
        navi.setNavigationItemSelectedListener(this);

        //初始化百度地图
        baiduMap.getUiSettings().setRotateGesturesEnabled(false);        //禁止旋转手势
        baiduMap.getUiSettings().setOverlookingGesturesEnabled(false);   //禁止俯视手势
        baiduMap.getUiSettings().setCompassEnabled(false);               //禁止指南针图层
        mapView.showZoomControls(false);                                 //禁止缩放控件
        mapView.showScaleControl(false);                                 //禁止比例尺
        mapView.setPadding(0, 0, 0, 100);      //设置底部内边距


        ///测试的页面跳转流程
        findViewById(R.id.btn_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProgActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        GlideUtil.loadCircleImg(this, img_navi_header, R.drawable.test, "http://tupian.qqjay.com/tou3/2016/0725/037697b0e2cbb48ccb5a8c4d1ef0f65c.jpg");
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.nav_setting:
                intent.setClass(this,IdentifyActivity.class);
                startActivity(intent);
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.img_home_msg:
//                intent.setClass(this, MsgClassActivity.class);
//                startActivity(intent);
                break;
            case R.id.img_home_order:
//                intent.setClass(this, MsgClassActivity.class);
//                startActivity(intent);
                break;
            case R.id.img_navi_header:
//                intent.setClass(this, MeDetailActivity.class);
//                startActivity(intent);
                break;
            case R.id.img_home_user:
                drawer.openDrawer(Gravity.LEFT);
                break;
            case R.id.text_home_title:
//                intent.setClass(this, SearchAddressActivity.class);
//                startActivity(intent);
                break;
        }
    }
}
