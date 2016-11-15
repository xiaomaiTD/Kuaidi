package com.ins.driver.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.ins.driver.R;
import com.ins.driver.common.HomeHelper;
import com.ins.driver.common.NetHelper;
import com.ins.driver.map.MyOnGetRoutePlanResultListener;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.common.Locationer;
import com.ins.middle.entity.CarMap;
import com.ins.middle.entity.EventOrder;
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
import com.ins.middle.utils.MapUtil;
import com.ins.middle.view.DriverView;
import com.sobey.common.utils.PermissionsUtil;
import com.sobey.common.utils.StrUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseAppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, Locationer.LocationCallback {

    //网络请求辅助
    private NetHelper netHelper;
    //定位器
    private Locationer locationer;
    //路径检索器
    private RoutePlanSearch mSearch = null;

    private DrawerLayout drawer;
    private ImageView img_navi_header;
    private NavigationView navi;

    public DriverView driverView;
    private ImageView img_user;
    private TextView text_username;
    private TextView text_title;
    private MapView mapView;
    private BaiduMap baiduMap;
    public TextView btn_go;
    public TextView btn_new;
    public CheckBox check_lu;
    private View btn_relocate;

    private DialogLoading dialogLoading;

    //前后司机集合
    private List<CarMap> cars = new ArrayList<>();
    //当前城市
    private String city;
    //是否在线
    public boolean isOnline = false;
    //当前行程
    public List<Trip> trips;
    //是否需要查询路线
    private boolean needSearchRout = false;
    //当前定位位置
    private LatLng nowLatLng;


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
            //切换用户后清楚地图所有标注
            if (baiduMap != null) baiduMap.clear();
            //登录后设置为初次登录状态
            locationer.isFirstLoc = true;
            //设置用户信息
            setUserData();
            //获取行程信息
            netHelper.netGetTrip();
        } else if (flag == AppConstant.EVENT_UPDATE_ME) {
            setUserData();
        }
    }

    @Subscribe
    public void onEventMainThread(EventOrder eventOrder) {
        String aboutOrder = eventOrder.getAboutOrder();
        Log.e("liao", "aboutOrder:" + aboutOrder);
        if ("3".equals(aboutOrder)) {
            //请求支付定金
        } else if ("4".equals(aboutOrder)) {
            //接到乘客,乘客已经上车
            if (eventOrder.getOrderId() != 0) {
                Trip trip = com.ins.driver.utils.AppHelper.getTripById(trips, eventOrder.getOrderId());
                //把已经上车的乘客从地图上移除
                if (trip.getMark() != null) trip.getMark().remove();
                trips.remove(trip);
                setTrip(trips);
            }
        } else if ("5".equals(aboutOrder)) {
            //已经到达目的地
        } else if ("6".equals(aboutOrder)) {
            //司机端 ： 匹配到有新的订单
            HomeHelper.setNewMsg(this);
            //获取行程信息
            netHelper.netGetTrip();
        } else if ("7".equals(aboutOrder)) {
            //乘客端： 订单已经匹配 已经分配给司机
        } else if ("8".equals(aboutOrder)) {
            //司机端 ： 定金支付成功
            if (eventOrder.getOrderId() != 0) {
                Trip trip = com.ins.driver.utils.AppHelper.getTripById(trips, eventOrder.getOrderId());
                trip.setStatus(2004);
            }
        } else if ("9".equals(aboutOrder)) {
            //司机出发
        }
    }

    @Subscribe
    public void onEventMainThread(Trip trip) {
        setPassengerRoute(nowLatLng, trip, true);
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
        driverView = (DriverView) findViewById(R.id.driverView);
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
        check_lu = (CheckBox) findViewById(R.id.check_map_lu);

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
        //开启关闭路况图
        check_lu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                baiduMap.setTrafficEnabled(isChecked);
            }
        });
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
        mSearch.setOnGetRoutePlanResultListener(new MyOnGetRoutePlanResultListener(mapView));


        baiduMap.setOnMarkerClickListener(onMarkerClickListener);
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
        //司机面板初始设置为不可用状态
        HomeHelper.setDisable(this);
        //详情面板默认不可见
        driverView.setVisibility(View.GONE);
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

    public void setTrip(List<Trip> trips) {
        //这里的trips是移除了已经上车乘客的
        this.trips = trips;
        HomeHelper.setTrip(this, trips);
        User user = AppData.App.getUser();
        if (user.getIsStart() == 0) {
            //未出发，设置乘客地点，并规划路线
            setPassengerPosition(trips);
        } else {
            //已出发，清理地图，规划到目的地路线并显示导航按钮
            Log.e("liao", "已出发");
        }
        //如果行程为空，则从UI上移除路径点
        if (StrUtils.isEmpty(trips)) {
            if (MyOnGetRoutePlanResultListener.overlay != null)
                MyOnGetRoutePlanResultListener.overlay.removeFromMap();
        }
    }

    public void setPassengerPosition(List<Trip> trips) {
        for (Trip trip : trips) {
            //起始地坐标和目的地坐标不能为空，保险起见的验证
            if (!StrUtils.isEmpty(trip.getFromLat()) && !StrUtils.isEmpty(trip.getToLat())) {
                final LatLng formLat = MapHelper.str2LatLng(trip.getFromLat());
                LatLng toLat = MapHelper.str2LatLng(trip.getToLat());
                com.ins.driver.utils.AppHelper.addPassengerMark(mapView, trip, formLat);
            }
        }
        //设置乘客信息的同时，进行路线规划
        //规划路线只规划集合内的第一个乘客
        if (!StrUtils.isEmpty(trips) && trips.size() > 0) {
            //设置检索路线标准位
            needSearchRout = true;
        }
    }

    public void setDriversPosiotin(List<User> drivers) {
        //检查车辆标注是否过期并移除
        com.ins.driver.utils.AppHelper.reMoveCar(cars, drivers);
        for (User driver : drivers) {
            CarMap carfind = com.ins.driver.utils.AppHelper.findCarByDriver(cars, driver.getId());
            if (carfind != null) {
                carfind.addMove(baiduMap, MapHelper.str2LatLng(driver.getLatAndLongit()));
            } else {
                carfind = new CarMap();
                carfind.addMove(baiduMap, MapHelper.str2LatLng(driver.getLatAndLongit()));
                carfind.setDriver(driver);  //setDriver必须在addMove之后，因为addMove前可能mark还未生成，故无法获取点击事件
                cars.add(carfind);
            }
        }
    }

    public void setPassengerRoute(LatLng nowLatLng, List<Trip> trips, boolean needzoom) {
        if (!StrUtils.isEmpty(trips) && trips.size() > 0) {
            setPassengerRoute(nowLatLng, trips.get(0), needzoom);
        }
    }

    public void setPassengerRoute(LatLng nowLatLng, Trip trip, boolean needzoom) {
        if (trip != null) {
            //起始地坐标不能为空，保险起见的验证
            if (!StrUtils.isEmpty(trip.getFromLat())) {
                MyOnGetRoutePlanResultListener.needzoom = needzoom;
                LatLng formLat = MapHelper.str2LatLng(trip.getFromLat());
                mSearch.drivingSearch((new DrivingRoutePlanOption()).from(PlanNode.withLocation(nowLatLng)).to(PlanNode.withLocation(formLat)));
                //查询一次后就不再查询了
                needSearchRout = false;
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
        drawer.closeDrawer(Gravity.LEFT);
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
                break;
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

    //地图标注（乘客头像）点击回调
    private BaiduMap.OnMarkerClickListener onMarkerClickListener = new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            Bundle bundle = marker.getExtraInfo();

            //有trip，说明是用户标注
            if (bundle.containsKey("trip")) {
                Trip trip = (Trip) bundle.getSerializable("trip");
                MapHelper.zoomByPoint(baiduMap, marker.getPosition());
                driverView.setVisibility(View.VISIBLE);
                driverView.setPassenger(trip.getPassenger(), trip);
                //点击头像重新规划路线
                setPassengerRoute(nowLatLng, trip, false);
            } else if (bundle.containsKey("driver")) {
                //driver，说明是司机标注
                User driver = (User) bundle.getSerializable("driver");
                MapHelper.zoomByPoint(baiduMap, marker.getPosition());
                driverView.setVisibility(View.VISIBLE);
                driverView.setDriver(driver);
            }
            return true;
        }
    };

    //定位回调
    @Override
    public void onLocation(LatLng latLng, String city, boolean isFirst) {
        //定位成功后保存定位坐标
        nowLatLng = latLng;
        //定位成功后保存定位城市
        this.city = city;
        //第一次定位成功后设置Title
        if (isFirst) text_title.setText(StrUtils.subLastChart(city, "市"));
        //每次定位成功后检查司机是否在线，在线则不断上传自己位置
        if (isOnline) netHelper.netUpdateLat(latLng);
        //每次定位成功后检查司机是否在线，在线则不断获取前后司机的位置
        if (isOnline) netHelper.netDriverLat();
        //每次定位成功后检查查询路线标准位，如果允许则进行路线查询
        if (needSearchRout) setPassengerRoute(latLng, trips, true);
    }
}
