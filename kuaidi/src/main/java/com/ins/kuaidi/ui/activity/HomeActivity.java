package com.ins.kuaidi.ui.activity;

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

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ins.kuaidi.R;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.entity.Position;
import com.ins.middle.entity.User;
import com.ins.middle.ui.activity.BaseAppCompatActivity;
import com.ins.middle.ui.activity.LoginActivity;
import com.ins.middle.ui.activity.MeDetailActivity;
import com.ins.middle.ui.activity.SettingActivity;
import com.ins.middle.ui.dialog.DialogLoading;
import com.ins.kuaidi.ui.dialog.DialogMouthPicker;
import com.ins.kuaidi.ui.dialog.DialogPopupMsg;
import com.ins.middle.utils.AppHelper;
import com.ins.middle.utils.GlideUtil;
import com.ins.kuaidi.view.HoldcarView;
import com.sobey.common.utils.PermissionsUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class HomeActivity extends BaseAppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private DrawerLayout drawer;
    private ImageView img_navi_header;
    private NavigationView navi;

    private HoldcarView holdcarView;
    private MapView mapView;
    private BaiduMap baiduMap;

    private ImageView img_user;
    private TextView text_username;
    private TextView text_title;
    private View lay_map_bubble;

    private DialogLoading dialogLoading;
    private DialogMouthPicker dialogTime;
    private DialogPopupMsg dialogPopupMsg;

    //    private static final int RESULT_SEARCHADDRESS = 0xf101;
    private int type = 0;   //0:点击出发地点 1:点击目的地

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
    public void onEventMainThread(Position position) {
        if (type == 0) {
            //选择了出发点
            holdcarView.setStartPosition(position);
        } else {
            //选择了目的地
            holdcarView.setEndPosition(position);
        }
    }

    @Subscribe
    public void onEventMainThread(Integer flag) {
        if (flag == AppConstant.EVENT_UPDATE_LOGIN) {
            setUserData();
        }else if (flag == AppConstant.EVENT_UPDATE_ME){
            setUserData();
        }
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        locationClient.stop();
        // 关闭定位图层
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (dialogLoading != null) dialogLoading.dismiss();
        if (dialogTime != null) dialogTime.dismiss();
        if (dialogPopupMsg != null) dialogPopupMsg.dismiss();
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
        dialogPopupMsg = new DialogPopupMsg(this);
        dialogTime = new DialogMouthPicker(this);
    }

    private void initView() {
        holdcarView = (HoldcarView) findViewById(R.id.holdcar);
        mapView = (MapView) findViewById(R.id.mapView);
        baiduMap = mapView.getMap();
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        navi = (NavigationView) findViewById(R.id.navi);
        img_user = (ImageView) findViewById(R.id.img_home_user);
        text_title = (TextView) findViewById(R.id.text_home_title);
        lay_map_bubble = findViewById(R.id.lay_map_bubble);

        img_navi_header = (ImageView) navi.getHeaderView(0).findViewById(R.id.img_navi_header);
        text_username = (TextView) navi.getHeaderView(0).findViewById(R.id.text_navi_username);
        img_navi_header.setOnClickListener(this);
        findViewById(R.id.img_home_msg).setOnClickListener(this);
        findViewById(R.id.img_home_order).setOnClickListener(this);
    }

    private void initCtrl() {
        text_title.setOnClickListener(this);
        img_user.setOnClickListener(this);
        lay_map_bubble.setOnClickListener(this);
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

        //设置移动监听
        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                YoYo.with(Techniques.TakingOff)
                        .duration(200)
                        .playOn(findViewById(R.id.lay_map_bubble));
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                YoYo.with(Techniques.Landing)
                        .duration(200)
                        .playOn(findViewById(R.id.lay_map_bubble));
            }
        });

        //设置holdcar事件
        holdcarView.setOnHoldcarListener(new HoldcarView.OnHoldcarListener() {
            @Override
            public void onSelectTimeClick(View v) {
                dialogTime.show();
            }

            @Override
            public void onRemarkClick(View v) {
                dialogPopupMsg.show();
            }

            @Override
            public void onStartClick(View v) {
                type = 0;
                Intent intent = new Intent(HomeActivity.this, SearchAddressActivity.class);
                startActivity(intent);
            }

            @Override
            public void onEndClick(View v) {
                type = 1;
                Intent intent = new Intent(HomeActivity.this, SearchAddressActivity.class);
                startActivity(intent);
            }
        });

        ///测试的页面跳转流程
        findViewById(R.id.btn_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PayActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        //设置用户信息
        setUserData();
        startlocation();
//        GlideUtil.loadCircleImg(this, img_navi_header, R.drawable.test, "http://tupian.qqjay.com/tou3/2016/0725/037697b0e2cbb48ccb5a8c4d1ef0f65c.jpg");
        GlideUtil.loadCircleImg(this, (ImageView) findViewById(R.id.img_driver_header), R.drawable.default_header, "http://tupian.qqjay.com/tou3/2016/0725/037697b0e2cbb48ccb5a8c4d1ef0f65c.jpg");
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.nav_trip:
                intent.setClass(this, TripActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_wallet:
                intent.setClass(this, WalletActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_server:
                intent.setClass(this, ServerActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_sale:
                intent.setClass(this, SaleActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_setting:
                intent.setClass(this, SettingActivity.class);
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
                intent.setClass(this, MsgClassActivity.class);
                startActivity(intent);
                break;
            case R.id.img_home_order:
//                intent.setClass(this, MsgClassActivity.class);
//                startActivity(intent);
                break;
            case R.id.img_navi_header:
                if (AppData.App.getUser()!=null) {
                    intent.setClass(this, MeDetailActivity.class);
                    startActivity(intent);
                }else {
                    intent.setClass(this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.img_home_user:
                drawer.openDrawer(Gravity.LEFT);
                break;
            case R.id.text_home_title:
                intent.setClass(this, SearchAddressActivity.class);
                startActivity(intent);
                break;
            case R.id.lay_map_bubble:
                intent.setClass(this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void setUserData() {
        User user = AppData.App.getUser();
        if (user != null) {
            text_username.setText(user.getNickName());
            GlideUtil.loadCircleImg(this, img_navi_header, R.drawable.default_header, AppHelper.getRealImgPath(user.getAvatar()));
        } else {
            text_username.setText("未登陆");
            GlideUtil.loadCircleImg(this, img_navi_header, R.drawable.default_header);
        }
    }

    ////////////////////////////////////
    /////////////定位相关
    /////////////////////////////////////
    LocationClient locationClient;
    public MyLocationListenner locationListenner = new MyLocationListenner();
    boolean isFirstLoc = true; // 是否首次定位
    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection())
                    .latitude(location.getLatitude()).longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(13.0f);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    private void startlocation() {
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        // 定位初始化
        locationClient = new LocationClient(this);
        locationClient.registerLocationListener(locationListenner);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);

        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
//        option.setOpenGps(true);//可选，默认false,设置是否使用gps
//        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
//        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
//        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        locationClient.setLocOption(option);
        locationClient.start();
    }
}
