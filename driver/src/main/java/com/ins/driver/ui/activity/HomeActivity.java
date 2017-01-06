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
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.ins.driver.R;
import com.ins.driver.common.HomeHelper;
import com.ins.driver.common.NetHelper;
import com.ins.driver.map.MyOnGetRoutePlanResultListener;
import com.ins.driver.ui.dialog.DialogNavi;
import com.ins.middle.utils.MarkHelper;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.common.Locationer;
import com.ins.middle.entity.CarMap;
import com.ins.middle.entity.EventOrder;
import com.ins.middle.entity.EventIdentify;
import com.ins.middle.entity.Trip;
import com.ins.middle.entity.User;
import com.ins.middle.ui.activity.BaseAppCompatActivity;
import com.ins.middle.ui.activity.CityActivity;
import com.ins.middle.ui.activity.LoginActivity;
import com.ins.middle.ui.activity.MeDetailActivity;
import com.ins.middle.ui.activity.MsgClassActivity;
import com.ins.middle.ui.activity.ServerActivity;
import com.ins.middle.ui.activity.SettingActivity;
import com.ins.middle.ui.activity.TripActivity;
import com.ins.middle.ui.activity.WalletActivity;
import com.ins.middle.ui.dialog.DialogLoading;
import com.ins.middle.ui.dialog.DialogSure;
import com.ins.middle.utils.AppHelper;
import com.ins.middle.utils.GlideUtil;
import com.ins.middle.utils.MapHelper;
import com.ins.middle.utils.PushValiHelper;
import com.ins.middle.utils.SnackUtil;
import com.ins.middle.view.DriverView;
import com.shelwee.update.UpdateHelper;
import com.sobey.common.utils.ClickUtils;
import com.sobey.common.utils.PermissionsUtil;
import com.sobey.common.utils.PhoneUtils;
import com.sobey.common.utils.StrUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseAppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, Locationer.LocationCallback, OnGetGeoCoderResultListener {

    private UpdateHelper updateHelper;
    //网络请求辅助
    public NetHelper netHelper;
    //定位器
    public Locationer locationer;
    //路径检索器
    private RoutePlanSearch mSearch = null;
    private GeoCoder geoSearch = null; // 搜索模块，也可去掉地图模块独立使用

    private DrawerLayout drawer;
    private ImageView img_navi_header;
    private NavigationView navi;

    private View showingroup;

    public DriverView driverView;
    private ImageView img_user;
    private TextView text_username;
    private TextView text_title;
    public MapView mapView;
    public BaiduMap baiduMap;
    public TextView btn_go;
    public View btn_fresh;
    //    public TextView btn_new;
    public View img_new;
    public CheckBox check_lu;
    private View btn_relocate;
    private View btn_mapfresh;


    private DialogLoading dialogLoading;
    private DialogSure dialogSure;
    private DialogNavi dialogNavi;

    //前后司机集合
    public List<CarMap> cars = new ArrayList<>();
    //当前城市
    private String city;
    private String nowcity;
    private String nowdistrict;
    //是否在线
    public boolean isOnline = false;
    //当前行程
    public List<Trip> trips;
    //是否需要查询路线
    private boolean needSearchRout = false;
    //当前定位位置
    public static LatLng nowLatLng;


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
            if (AppData.App.getUser() != null) {
                //登录
                netHelper.netGetTrip();
            } else {
                //注销
                if (trips != null) trips.clear();
            }
        } else if (flag == AppConstant.EVENT_UPDATE_ME) {
            setUserData();
        }
    }

    @Subscribe
    public void onEventMainThread(EventIdentify eventIdentify) {
        String aboutsystem = eventIdentify.getAboutsystem();
        String msg = eventIdentify.getMsg();
        if ("15".equals(aboutsystem)) {
            //审核通过
//            User user = AppData.App.getUser();
//            user.setStatus(User.AUTHENTICATED);
//            AppData.App.saveUser(user);
//            setUserData();
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            netHelper.getInfo();
        } else if ("16".equals(aboutsystem)) {
            //审核不通过
//            User user = AppData.App.getUser();
//            user.setStatus(User.UNAUTHORIZED);
//            AppData.App.saveUser(user);
//            setUserData();
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            netHelper.getInfo();
        }
    }

    @Subscribe
    public void onEventMainThread(EventOrder eventOrder) {
        String aboutOrder = eventOrder.getAboutOrder();
        int orderId = eventOrder.getOrderId();
        String msg = eventOrder.getMsg();
        if ("4".equals(aboutOrder)) {
            //接到乘客,乘客已经上车（本地推送）
            netHelper.netGetTrip();
            //把driverview中的订单状态修改
            driverView.setVisibility(View.GONE);
            Trip trip = driverView.getTrip();
            if (trip != null) {
                trip.setStatus(Trip.STA_2005);
            }
        } else if ("5".equals(aboutOrder)) {
            //已经到达目的地
            //目前司机抵达不需要在推送中处理
        } else if ("6".equals(aboutOrder) && PushValiHelper.pushDMattch(trips, orderId)) {
            //司机端 ： 匹配到有新的订单
            SnackUtil.showSnack(showingroup, msg, onNewMsgclickListener);
//            SnackUtil.showSnack(showingroup, "您有一条新的订单", onNewMsgclickListener);
            //获取行程信息
            netHelper.netGetTrip();
        } else if ("8".equals(aboutOrder) && PushValiHelper.pushDHasPayFirst(trips, orderId)) {
            //司机端 ： 定金支付成功
            netHelper.netGetTrip();
            SnackUtil.showSnack(showingroup, msg);
//            SnackUtil.showSnack(showingroup, "乘客已支付定金");
        } else if ("9".equals(aboutOrder) && PushValiHelper.pushDStart(trips, orderId)) {
            //司机出发
        } else if ("13".equals(aboutOrder) && PushValiHelper.pushDHasPayLsat(trips, orderId)) {
            //司机端 ： 尾款支付成功
            SnackUtil.showSnack(showingroup, msg);
//            SnackUtil.showSnack(showingroup, "乘客已支付尾款");
        } else if ("14".equals(aboutOrder) && PushValiHelper.pushDCancle(trips, orderId)) {
            //乘客取消了订单
            HomeHelper.setFresh(this);
            SnackUtil.showSnack(showingroup, msg);
            //取消订单后正在规划的路线失效
            MyOnGetRoutePlanResultListener.enable = true;
//            SnackUtil.showSnack(showingroup, "乘客取消了订单，系统正在重新为您寻找乘客");
        } else if ("102".equals(aboutOrder)) {
            //乘客已经全部下车，司机选择继续接单，回滚初始状态(本地推送)
            baiduMap.clear();
            if (trips != null) trips.clear();
            setTrip(null);
            netHelper.netOnOff(true, nowcity + "," + nowdistrict, MapHelper.LatLng2Str(nowLatLng));
        } else if ("103".equals(aboutOrder)) {
            //乘客已经全部下车(本地推送)
            baiduMap.clear();
            if (trips != null) trips.clear();
            setTrip(null);

            //更新本地状态为下线状态
            User user = AppData.App.getUser();
            user.setIsOnline(0);
            AppData.App.saveUser(user);
            setOnLineData(user);
        }
    }

    @Subscribe
    public void onEventMainThread(Trip trip) {
        drawer.closeDrawer(Gravity.LEFT);
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
        if (updateHelper != null) updateHelper.onDestory();
        if (dialogLoading != null) dialogLoading.dismiss();
        if (dialogSure != null) dialogSure.dismiss();
        if (dialogNavi != null) dialogNavi.dismiss();
    }

    private void initBase() {
        updateHelper = new UpdateHelper.Builder(this)
                .checkUrl(AppData.Url.version_driver)
                .isHintNewVersion(false)
                .build();
        updateHelper.check();
        netHelper = new NetHelper(this);
        dialogLoading = new DialogLoading(this, "正在处理");
        dialogSure = new DialogSure(this, "您确定要上线？");
        dialogSure.setOnOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSure.hide();
                netHelper.netOnOff(!btn_go.isSelected(), nowcity + "," + nowdistrict, MapHelper.LatLng2Str(nowLatLng));
            }
        });
        dialogNavi = new DialogNavi(this);
    }

    private void initView() {
        showingroup = findViewById(R.id.showingroup);
        driverView = (DriverView) findViewById(R.id.driverView);
        mapView = (MapView) findViewById(R.id.mapView);
        locationer = new Locationer(mapView);
        baiduMap = mapView.getMap();
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        navi = (NavigationView) findViewById(R.id.navi);
        img_user = (ImageView) findViewById(R.id.img_home_user);
        text_title = (TextView) findViewById(R.id.text_home_title);
        btn_go = (TextView) findViewById(R.id.btn_go);
        btn_fresh = findViewById(R.id.btn_fresh);
        img_new = findViewById(R.id.img_home_order_new);
        btn_relocate = findViewById(R.id.btn_map_relocate);
        btn_mapfresh = findViewById(R.id.btn_map_fresh);
        check_lu = (CheckBox) findViewById(R.id.check_map_lu);

        btn_fresh.setOnClickListener(this);
        findViewById(R.id.img_home_msg).setOnClickListener(this);
        findViewById(R.id.img_home_order).setOnClickListener(this);
        img_navi_header = (ImageView) navi.getHeaderView(0).findViewById(R.id.img_navi_header);
        text_username = (TextView) navi.getHeaderView(0).findViewById(R.id.text_navi_username);
        img_navi_header.setOnClickListener(this);
        btn_go.setOnClickListener(this);
        btn_relocate.setOnClickListener(this);
        btn_mapfresh.setOnClickListener(this);
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

        //司机给乘客打电话时发送统计请求
        driverView.setOnDriverCallListener(new DriverView.OnDriverCallListener() {
            @Override
            public void onDriverCall(int orderId) {
                netHelper.netDriverCall(orderId);
            }
        });
        //导航按钮点击事件
        driverView.setOnNaviListenner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Trip trip = driverView.getTrip();
                com.ins.driver.utils.AppHelper.onStartNavi(dialogNavi, trip);
            }
        });
        //定位回调
        locationer.setCallback(this);
        //搜索相关，初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(new MyOnGetRoutePlanResultListener(this));
        // 初始化搜索模块，注册事件监听
        geoSearch = GeoCoder.newInstance();
        geoSearch.setOnGetGeoCodeResultListener(this);

        baiduMap.setOnMarkerClickListener(onMarkerClickListener);

        //测试
        btn_fresh.setVisibility(AppData.Config.showFreshBtn ? View.VISIBLE : View.GONE);
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
        img_new.setVisibility(View.GONE);
        //不需要
//        //司机面板初始设置为未登录状态
//        HomeHelper.setUnLogin(this);
        //详情面板默认不可见
        driverView.setVisibility(View.GONE);
    }

    /////////////////////////////////
    //////////设置数据的方法
    /////////////////////////////////

    public void setUserData() {
        User user = AppData.App.getUser();
        if (user != null) {
            text_username.setText(user.getNickName());
            GlideUtil.loadCircleImg(this, img_navi_header, R.drawable.default_header, AppHelper.getRealImgPath(user.getAvatar()));
            setOnLineData(user);
            //btn_go.setVisibility(View.VISIBLE);
            //HomeHelper.setInit(this);
        } else {
            text_username.setText("未登录");
            GlideUtil.loadCircleImg(this, img_navi_header, R.drawable.default_header);
            setOnLineData(null);
            HomeHelper.setUnLogin(this);
        }
    }

    public void setOnLineData(User user) {
        if (user == null) {
            isOnline = false;
        } else {
            if (user.getIsOnline() == 1) {
                HomeHelper.setOnline(this);
                isOnline = true;
            } else {
                HomeHelper.setOffline(this);
                isOnline = false;
            }
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
            Log.e("liao", "trips == null 移除所有路径点");
            if (MyOnGetRoutePlanResultListener.overlay != null)
                MyOnGetRoutePlanResultListener.overlay.removeFromMap();
        }
        //每次获取行程都拉取前后司机位置
        netHelper.netDriverLat();
    }

    public void setPassengerPosition(List<Trip> trips) {
        if (!StrUtils.isEmpty(trips)) {
            for (Trip trip : trips) {
                //起始地坐标和目的地坐标不能为空，保险起见的验证
                if (!StrUtils.isEmpty(trip.getFromLat()) && !StrUtils.isEmpty(trip.getToLat())) {
                    final LatLng formLat = MapHelper.str2LatLng(trip.getFromLat());
                    LatLng toLat = MapHelper.str2LatLng(trip.getToLat());
                    MarkHelper.addPassengerMark(mapView, trip, formLat);
                }
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
                carfind.addMove(mapView, MapHelper.str2LatLng(driver.getLatAndLongit()));
            } else {
                carfind = new CarMap(true);
                carfind.setDriver(driver);  //setDriver必须在addMove之后，因为addMove前可能mark还未生成，故无法获取点击事件
                carfind.addMove(mapView, MapHelper.str2LatLng(driver.getLatAndLongit()));
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
                MyOnGetRoutePlanResultListener.enable = true;
                mSearch.drivingSearch((new DrivingRoutePlanOption()).from(PlanNode.withLocation(nowLatLng)).to(PlanNode.withLocation(formLat)));
                //查询一次后就不再查询了
                needSearchRout = false;
            }
        }
    }

    private void setCity(String city) {
        this.city = city;
        text_title.setText(!StrUtils.isEmpty(city) ? city : "定位失败");
    }

    ////////////////////////////////////
    /////////监听回调
    ////////////////////////////////////

    private static final int RESULT_CITY = 0xf101;

    //页面返回回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_CITY:
                if (resultCode == RESULT_OK) {
                    String city = data.getStringExtra("city");
                    if (!StrUtils.isEmpty(city)) {
                        this.city = city;
                        setCity(city);
                        geoSearch.geocode(new GeoCodeOption().city(city).address(city));
                    }
                }
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //屏蔽快速双击事件
        if (ClickUtils.isFastDoubleClick()) return false;

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
            case R.id.nav_setting:
                intent.setClass(this, SettingActivity.class);
                //司机端需要额外的参数（下线时需要下线）
                boolean needOffline = false;
                if (trips != null) {
                    //在行程中不能下线
                    needOffline = false;
                } else {
                    //不在行程中，在线则需要下线
                    needOffline = isOnline;
                }
                intent.putExtra("needOffline", needOffline);
                startActivity(intent);
                break;
        }
        //drawer.closeDrawer(Gravity.LEFT);
        return false;
    }

    @Override
    public void onClick(View v) {
        //屏蔽快速双击事件
        if (ClickUtils.isFastDoubleClick()) return;

        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.img_home_msg:
                intent.setClass(this, MsgClassActivity.class);
                startActivity(intent);
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
                if (!StrUtils.isEmpty(city)) {
                    intent.setClass(this, CityActivity.class);
                    intent.putExtra("city", nowcity);
                    startActivityForResult(intent, RESULT_CITY);
                } else {
                    Toast.makeText(this, "正在定位中...", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_go:
                if (AppData.App.getUser() != null) {
                    if (!StrUtils.isEmpty(city)) {
                        if (btn_go.isSelected()) {
                            dialogSure.setMsg("您确定要下线吗？");
                        } else {
                            dialogSure.setMsg("您确定要上线吗？");
                        }
                        dialogSure.show();
                    } else {
                        Toast.makeText(this, "定位中，稍后再试", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    intent.setClass(this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.btn_map_relocate:
                MapHelper.zoomByPoint(baiduMap, nowLatLng);
                if (!StrUtils.isEmpty(city) && !city.equals(nowcity)) {
                    setCity(nowcity);
                }
                break;
            case R.id.btn_fresh:
                HomeHelper.setFresh(this);
                break;
            case R.id.btn_map_fresh:
                netHelper.netDriverLat();
                break;
        }
    }

    //新消息按钮回调
    private View.OnClickListener onNewMsgclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeActivity.this, ProgActivity.class);
            startActivity(intent);
        }
    };

    //地图标注（乘客头像）点击回调
    private BaiduMap.OnMarkerClickListener onMarkerClickListener = new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            Bundle bundle = marker.getExtraInfo();
            if (bundle == null) {
                return true;
            }
            //有trip，说明是用户标注
            if (bundle.containsKey("trip")) {
                Trip trip = (Trip) bundle.getSerializable("trip");
                MapHelper.zoomByPoint(baiduMap, marker.getPosition());
                driverView.setVisibility(View.VISIBLE);
                driverView.setPassenger(trip.getPassenger(), trip);
                //点击头像重新规划路线
                setPassengerRoute(nowLatLng, trip, false);
            } else if (bundle.containsKey("driver")) {
                //这个功能又不要了，我艹你麻痹
//                //driver，说明是司机标注
//                User driver = (User) bundle.getSerializable("driver");
//                MapHelper.zoomByPoint(baiduMap, marker.getPosition());
//                driverView.setVisibility(View.VISIBLE);
//                driverView.setDriver(driver, null);
                User driver = (User) bundle.getSerializable("driver");
                if (driver != null) {
                    PhoneUtils.call(HomeActivity.this, driver.getMobile());
                }
            }
            return true;
        }
    };

    //定位回调
    @Override
    public void onLocation(LatLng latLng, String city, String district, boolean isFirst) {
        //定位成功后保存定位坐标
        this.nowLatLng = latLng;
        //定位成功后保存定位城市
        this.nowcity = city;
        this.nowdistrict = district;
        if (isFirst) {
            //第一次定位成功后设置城市
            setCity(city);
            //第一次定位成功后请求周围司机
            //netHelper.netDriverLat();
        }
        //每次定位成功后检查司机是否在线，在线则不断上传自己位置
        if (isOnline) netHelper.netUpdateLat(latLng);
        //每次定位成功后检查司机是否在线，在线则不断获取前后司机的位置
        //if (isOnline) netHelper.netDriverLat();//现在不是每次定位请求了，改成手动刷新
        //每次定位成功后检查查询路线标准位，如果允许则进行路线查询
        if (needSearchRout) setPassengerRoute(latLng, trips, true);
    }

    //检索回调
    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            return;
        }
        LatLng location = result.getLocation();
        MapHelper.zoomToPosition(mapView, location);
    }

    //反检索回调
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
    }
}
