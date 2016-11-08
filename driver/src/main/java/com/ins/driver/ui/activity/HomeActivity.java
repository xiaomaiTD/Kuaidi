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
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.ins.driver.R;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.common.Locationer;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.entity.User;
import com.ins.middle.ui.activity.BaseAppCompatActivity;
import com.ins.middle.ui.activity.LoginActivity;
import com.ins.middle.ui.activity.MeDetailActivity;
import com.ins.middle.ui.activity.SettingActivity;
import com.ins.middle.ui.dialog.DialogLoading;
import com.ins.middle.utils.AppHelper;
import com.ins.middle.utils.GlideUtil;
import com.sobey.common.utils.PermissionsUtil;
import com.sobey.common.utils.StrUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.http.RequestParams;

public class HomeActivity extends BaseAppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, Locationer.LocationCallback {

    private Locationer locationer;

    private DrawerLayout drawer;
    private ImageView img_navi_header;
    private NavigationView navi;

    private ImageView img_user;
    private TextView text_username;
    private TextView text_title;
    private MapView mapView;
    private BaiduMap baiduMap;
    private TextView btn_go;
    private View btn_relocate;

    private DialogLoading dialogLoading;

    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setToolbar(null, false);
        EventBus.getDefault().register(this);
        PermissionsUtil.checkAndRequestPermissions(this);

        initBase();
        initView();
        initCtrl();
        initData();
    }

    @Subscribe
    public void onEventMainThread(Integer flag) {
        if (flag == AppConstant.EVENT_UPDATE_LOGIN) {
            setUserData();
        } else if (flag == AppConstant.EVENT_UPDATE_ME) {
            setUserData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationer.stopLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationer.startlocation();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        locationer.stopLocation();
        // 关闭定位图层
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
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
        locationer = new Locationer(mapView);
        baiduMap = mapView.getMap();
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        navi = (NavigationView) findViewById(R.id.navi);
        img_user = (ImageView) findViewById(R.id.img_home_user);
        text_title = (TextView) findViewById(R.id.text_home_title);
        btn_go = (TextView) findViewById(R.id.btn_go);
        btn_relocate = findViewById(R.id.btn_map_relocate);

        findViewById(R.id.img_home_msg).setOnClickListener(this);
        findViewById(R.id.img_home_order).setOnClickListener(this);
        img_navi_header = (ImageView) navi.getHeaderView(0).findViewById(R.id.img_navi_header);
        text_username = (TextView) navi.getHeaderView(0).findViewById(R.id.text_navi_username);
        img_navi_header.setOnClickListener(this);
        btn_go.setOnClickListener(this);
        btn_relocate.setOnClickListener(this);
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

        //定位回调
        locationer.setCallback(this);
    }

    private void initData() {
        setUserData();
        locationer.startlocation();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.nav_trip:
                break;
            case R.id.nav_wallet:
                break;
            case R.id.nav_server:
                break;
            case R.id.nav_sale:
                break;
            case R.id.nav_setting:
                intent.setClass(this, SettingActivity.class);
                startActivity(intent);
//                intent.setClass(this, IdentifyActivity.class);
//                startActivity(intent);
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
                if (AppData.App.getUser() != null) {
                    intent.setClass(this, MeDetailActivity.class);
                    startActivity(intent);
                } else {
                    intent.setClass(this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.img_home_user:
                drawer.openDrawer(Gravity.LEFT);
                break;
            case R.id.text_home_title:
//                intent.setClass(this, SearchAddressActivity.class);
//                startActi
            case R.id.btn_go:
                if (!StrUtils.isEmpty(city)) {
                    netOnOff(!btn_go.isSelected());
                } else {
                    Toast.makeText(this, "定位中，稍后再试", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_map_relocate:
                locationer.isFirstLoc = true;
                break;
        }
    }

    private void setUserData() {
        User user = AppData.App.getUser();
        if (user != null) {
            text_username.setText(user.getNickName());
            GlideUtil.loadCircleImg(this, img_navi_header, R.drawable.default_header, AppHelper.getRealImgPath(user.getAvatar()));
            btn_go.setVisibility(View.VISIBLE);
            setOnLineData(user);
        } else {
            text_username.setText("未登陆");
            GlideUtil.loadCircleImg(this, img_navi_header, R.drawable.default_header);
            btn_go.setVisibility(View.GONE);
        }
    }

    private void setOnLineData(User user) {
        if (user.getIsOnline() == 1) {
            //当前用户在线
            btn_go.setText("下线");
            btn_go.setSelected(true);
            isOnline = true;
        } else {
            //当前用户不在线
            btn_go.setText("上线");
            btn_go.setSelected(false);
            isOnline = false;
        }
    }

    private boolean isOnline = false;

    //定位回调
    @Override
    public void onLocation(LatLng latLng, String city, boolean isFirst) {
        if (isFirst) text_title.setText(city);
        this.city = city;
        if (isOnline) netUpdateLat(latLng);
    }

    private void netOnOff(final boolean onoff) {
        RequestParams params = new RequestParams(AppData.Url.onOffLine);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("flag", onoff ? "1" : "0");
        params.addBodyParameter("cityName", city);
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                Toast.makeText(HomeActivity.this, text, Toast.LENGTH_SHORT).show();
                //dialogLoading.hide();
                btn_go.setEnabled(true);
                //保存在线状态
                User user = AppData.App.getUser();
                user.setIsOnline(onoff ? 1 : 0);
                AppData.App.saveUser(user);
                //更新UI
                setOnLineData(user);
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(HomeActivity.this, text, Toast.LENGTH_SHORT).show();
                //dialogLoading.hide();
                btn_go.setEnabled(true);
            }

            @Override
            public void netStart(int code) {
                //dialogLoading.show();
                btn_go.setEnabled(false);
            }
        });
    }

    private void netUpdateLat(LatLng latLng) {
        String lat = latLng.longitude + "," + latLng.latitude;
        RequestParams params = new RequestParams(AppData.Url.updateLat);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("lat", lat);
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(HomeActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netStart(int code) {
            }
        });
    }


}
