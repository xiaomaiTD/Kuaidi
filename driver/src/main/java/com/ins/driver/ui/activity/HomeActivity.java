package com.ins.driver.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ins.driver.R;
import com.ins.driver.common.DrivingRouteOverlay;
import com.ins.driver.common.HomeHelper;
import com.ins.driver.common.NetHelper;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.common.Locationer;
import com.ins.middle.entity.Trip;
import com.ins.middle.entity.User;
import com.ins.middle.ui.activity.BaseAppCompatActivity;
import com.ins.middle.ui.activity.LoginActivity;
import com.ins.middle.ui.activity.MeDetailActivity;
import com.ins.middle.ui.activity.ServerActivity;
import com.ins.middle.ui.activity.SettingActivity;
import com.ins.middle.ui.activity.TripActivity;
import com.ins.middle.ui.dialog.DialogLoading;
import com.ins.middle.utils.AppHelper;
import com.ins.middle.utils.GlideUtil;
import com.ins.middle.utils.MapHelper;
import com.sobey.common.utils.FileUtil;
import com.sobey.common.utils.PermissionsUtil;
import com.sobey.common.utils.StrUtils;
import com.sobey.common.utils.others.BitmapUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class HomeActivity extends BaseAppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, Locationer.LocationCallback {

    private NetHelper netHelper;
    private Locationer locationer;

    //搜索相关
    private RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
//    private boolean iszoom = true;
//    private DrivingRouteOverlay overlay;

    private DrawerLayout drawer;
    private ImageView img_navi_header;
    private NavigationView navi;

    private ImageView img_user;
    private TextView text_username;
    private TextView text_title;
    private MapView mapView;
    private BaiduMap baiduMap;
    public TextView btn_go;
    public TextView btn_new;
    private View btn_relocate;

    private DialogLoading dialogLoading;

    private String city;
    public boolean isOnline = false;

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

    @Subscribe
    public void onEventMainThread(Integer flag) {
        if (flag == AppConstant.EVENT_UPDATE_LOGIN) {
            setUserData();
            //获取行程信息
            netHelper.netGetTrip();
        } else if (flag == AppConstant.EVENT_UPDATE_ME) {
            setUserData();
        }
    }

    @Subscribe
    public void onEventMainThread(String flagSpc) {
        if (AppConstant.EVENT_JPUSH_ORDER.equals(AppConstant.getFlag(flagSpc))) {
            String aboutOrder = AppConstant.getStr(flagSpc);
            if ("3".equals(aboutOrder)) {
                //请求支付定金
            } else if ("4".equals(aboutOrder)) {
                //接到乘客
            } else if ("5".equals(aboutOrder)) {
                //已经到达目的地
            } else if ("6".equals(aboutOrder)) {
                //司机端 ： 匹配到有新的订单
                HomeHelper.setNewMsg(this);
                //获取行程信息
                netHelper.netGetTrip();
            } else if ("7".equals(aboutOrder)) {
                //乘客端： 订单已经匹配 已经分配给司机
            }
        }
    }

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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void initBase() {
        netHelper = new NetHelper(this);
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
        btn_new = (TextView) findViewById(R.id.btn_new);
        btn_relocate = findViewById(R.id.btn_map_relocate);

        findViewById(R.id.img_home_msg).setOnClickListener(this);
        findViewById(R.id.img_home_order).setOnClickListener(this);
        img_navi_header = (ImageView) navi.getHeaderView(0).findViewById(R.id.img_navi_header);
        text_username = (TextView) navi.getHeaderView(0).findViewById(R.id.text_navi_username);
        img_navi_header.setOnClickListener(this);
        btn_go.setOnClickListener(this);
        btn_new.setOnClickListener(this);
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


        //定位回调
        locationer.setCallback(this);
        //搜索相关，初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(onGetRoutePlanResultListener);


        PlanNode stMassNode = PlanNode.withCityNameAndPlaceName("北京", "天安门");
        PlanNode enMassNode = PlanNode.withCityNameAndPlaceName("上海", "东方明珠");
        mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stMassNode).to(enMassNode));
    }

    private void initData() {
        //设置用户信息
        setUserData();
        //获取行程信息
        if (AppData.App.getUser() != null) {
            netHelper.netGetTrip();
        }
        //开始定位
        locationer.startlocation();
        //消息面板初始不可见
        btn_new.setVisibility(View.GONE);
    }

    /////////////////////////////////
    //////////设置数据的方法
    /////////////////////////////////

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

    public void setOnLineData(User user) {
        if (user.getIsOnline() == 1) {
            HomeHelper.setOnline(this);
            isOnline = true;
        } else {
            HomeHelper.setOffline(this);
            isOnline = false;
        }
    }

    public void setPassengerPosition(List<Trip> trips) {
        for (Trip trip : trips) {
            final LatLng formLat = MapHelper.str2LatLng(trip.getFromLat());
            LatLng toLat = MapHelper.str2LatLng(trip.getToLat());
            String avatar = trip.getPassenger().getAvatar();
            if (!StrUtils.isEmpty(avatar)) {
                com.ins.driver.utils.AppHelper.addPassengerMark(this, baiduMap, AppHelper.getRealImgPath(avatar), formLat);
            }
        }
    }

    ////////////////////////////////////
    /////////监听回调
    ////////////////////////////////////

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.nav_trip:
                intent.setClass(this, TripActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_wallet:
                break;
            case R.id.nav_server:
                intent.setClass(this, ServerActivity.class);
                startActivity(intent);
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
                intent.setClass(this, ProgActivity.class);
                startActivity(intent);
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
                    netHelper.netOnOff(!btn_go.isSelected(), city);
                } else {
                    Toast.makeText(this, "定位中，稍后再试", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_new:
                intent.setClass(HomeActivity.this, ProgActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_map_relocate:
                locationer.isFirstLoc = true;
                break;
        }
    }

    //定位回调
    @Override
    public void onLocation(LatLng latLng, String city, boolean isFirst) {
        if (isFirst) text_title.setText(city);
        this.city = city;
        if (isOnline) netHelper.netUpdateLat(latLng);
    }

    //路线查询回调
    private OnGetRoutePlanResultListener onGetRoutePlanResultListener = new OnGetRoutePlanResultListener() {
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(HomeActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                if (result.getRouteLines().size() > 1) {
                    RouteLine route = result.getRouteLines().get(0);
                    DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(baiduMap);
                    baiduMap.setOnMarkerClickListener(overlay);
                    DrivingRouteOverlay routeOverlay = overlay;
                    overlay.setData(result.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();

                } else if (result.getRouteLines().size() == 1) {
//                    route = result.getRouteLines().get(0);
//                    DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
//                    routeOverlay = overlay;
//                    mBaidumap.setOnMarkerClickListener(overlay);
//                    overlay.setData(result.getRouteLines().get(0));
//                    overlay.addToMap();
//                    overlay.zoomToSpan();
//                    mBtnPre.setVisibility(View.VISIBLE);
//                    mBtnNext.setVisibility(View.VISIBLE);
                } else {
                    Log.d("route result", "结果数<0");
                    return;
                }

            }
        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
        }
    };


    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public int getLineColor() {
            return Color.RED;
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_location);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_location);
        }
    }
}
