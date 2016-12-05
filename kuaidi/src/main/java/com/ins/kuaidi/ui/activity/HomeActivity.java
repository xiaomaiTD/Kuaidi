package com.ins.kuaidi.ui.activity;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ins.kuaidi.R;
import com.ins.kuaidi.common.HomeHelper;
import com.ins.kuaidi.common.NetHelper;
import com.ins.middle.entity.CarMap;
import com.ins.middle.entity.EventOrder;
import com.ins.middle.ui.activity.CityActivity;
import com.ins.middle.ui.activity.MsgClassActivity;
import com.ins.middle.ui.activity.WalletActivity;
import com.ins.middle.ui.dialog.DialogSure;
import com.ins.middle.utils.MapHelper;
import com.ins.middle.view.DriverView;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.common.AppVali;
import com.ins.middle.common.Locationer;
import com.ins.middle.entity.Position;
import com.ins.middle.entity.Trip;
import com.ins.middle.entity.User;
import com.ins.middle.ui.activity.BaseAppCompatActivity;
import com.ins.middle.ui.activity.LoginActivity;
import com.ins.middle.ui.activity.MeDetailActivity;
import com.ins.middle.ui.activity.ServerActivity;
import com.ins.middle.ui.activity.SettingActivity;
import com.ins.middle.ui.activity.TripActivity;
import com.ins.middle.ui.dialog.DialogLoading;
import com.ins.kuaidi.ui.dialog.DialogMouthPicker;
import com.ins.kuaidi.ui.dialog.DialogPopupMsg;
import com.ins.middle.utils.AppHelper;
import com.ins.middle.utils.GlideUtil;
import com.ins.kuaidi.view.HoldcarView;
import com.shelwee.update.UpdateHelper;
import com.sobey.common.utils.ClickUtils;
import com.sobey.common.utils.PermissionsUtil;
import com.sobey.common.utils.StrUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseAppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, Locationer.LocationCallback, OnGetGeoCoderResultListener {

    private UpdateHelper updateHelper;
    public NetHelper netHelper;
    public Locationer locationer;
    private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

    private DrawerLayout drawer;
    private ImageView img_navi_header;
    private NavigationView navi;

    public DriverView driverView;
    public HoldcarView holdcarView;
    public MapView mapView;
    public BaiduMap baiduMap;

    private ImageView img_user;
    private TextView text_username;
    private TextView text_title;
    private View lay_map_bubble;
    public View lay_map_center;
    public TextView btn_go;
    public View btn_fresh;
    private View btn_relocate;

    public DialogLoading dialogLoading;
    private DialogMouthPicker dialogTime;
    private DialogPopupMsg dialogPopupMsg;
    private DialogSure dialogSure;

    //    private static final int RESULT_SEARCHADDRESS = 0xf101;
    private int type = 0;   //0:点击出发地点 1:点击目的地
    private String city;
    private String nowcity;
    //当前定位位置
    private LatLng nowLatLng;
    boolean isIn;
    //地理围栏
    public List<List<LatLng>> ptsArray;
    public Trip trip;
    public CarMap carMap;

    //保存屏幕设置的点信息
    private List<Overlay> overlays = new ArrayList<>();

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
    public void onEventMainThread(Position position) {
        if (type == 0) {
            //选择了出发点
            MapHelper.zoomToPosition(mapView, position.getLatLng());
        } else {
            //选择了目的地
            if (com.ins.kuaidi.utils.AppHelper.needNetConfigEnd(holdcarView, position.getCity())) {
                holdcarView.setEndPosition(position);
                netHelper.netGetLineConfig(holdcarView.getStartPosition().getCity(), position.getCity());
            } else {
                holdcarView.setEndPosition(position);
            }
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
                netHelper.netGetTrip();
            } else {
                HomeHelper.setInit(this);
                trip = null;
            }
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
            netHelper.netGetTrip();
        } else if ("4".equals(aboutOrder)) {
            //接到乘客
            netHelper.netGetTrip();
        } else if ("5".equals(aboutOrder)) {
            //已经到达目的地
            HomeHelper.setFresh(this);
        } else if ("6".equals(aboutOrder)) {
            //司机端 ： 匹配到有新的订单
        } else if ("7".equals(aboutOrder)) {
            //乘客端： 订单已经匹配 已经分配给司机
            netHelper.netGetTrip();
        } else if ("8".equals(aboutOrder)) {
            //定金支付成功(乘客端本地的推送)
            //2004 乘客已支付预付款
            HomeHelper.setPayLastFalse(this);
            trip.setStatus(Trip.STA_2004);
        } else if ("9".equals(aboutOrder)) {
            //司机出发
        } else if ("101".equals(aboutOrder)) {
            //乘客已经支付尾款(乘客端本地的推送)
            netHelper.netGetTrip();
        }
    }

    @Subscribe
    public void onEventMainThread(Trip trip) {
        drawer.closeDrawer(Gravity.LEFT);
        carMap.removeFromMap();
        setTrip(trip);
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
        if (updateHelper != null) updateHelper.onDestory();
        if (dialogLoading != null) dialogLoading.dismiss();
        if (dialogTime != null) dialogTime.dismiss();
        if (dialogPopupMsg != null) dialogPopupMsg.dismiss();
        if (dialogSure != null) dialogSure.dismiss();
    }

    private long exitTime;

    private void initBase() {
        updateHelper = new UpdateHelper.Builder(this)
                .checkUrl(AppData.Url.version_passenger)
                .isHintNewVersion(false)
                .build();
        updateHelper.check();
        carMap = new CarMap();
        netHelper = new NetHelper(this);
        dialogLoading = new DialogLoading(this, "正在处理");
        dialogPopupMsg = new DialogPopupMsg(this);
        dialogSure = new DialogSure(this, "确定取消订单？");
        dialogTime = new DialogMouthPicker(this);
        dialogTime.setOnOKlistener(new DialogMouthPicker.OnOkListener() {
            @Override
            public void onOkClick(int day, String time) {
                holdcarView.setStartTime(day, time);
            }
        });
        dialogPopupMsg.setOnSendListener(new DialogPopupMsg.OnSendListener() {
            @Override
            public void onSendMsg(String msg) {
                holdcarView.setMsg(msg);
            }
        });
        dialogSure.setOnOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int orderId = (int) dialogSure.getObject();
                netHelper.netCancleOrder(orderId);
                dialogSure.hide();
            }
        });
    }

    private void initView() {
        driverView = (DriverView) findViewById(R.id.driverView);
        holdcarView = (HoldcarView) findViewById(R.id.holdcar);
        mapView = (MapView) findViewById(R.id.mapView);
        locationer = new Locationer(mapView);
        baiduMap = mapView.getMap();
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        navi = (NavigationView) findViewById(R.id.navi);
        img_user = (ImageView) findViewById(R.id.img_home_user);
        text_title = (TextView) findViewById(R.id.text_home_title);
        lay_map_bubble = findViewById(R.id.lay_map_bubble);
        lay_map_center = findViewById(R.id.lay_map_center);
        btn_go = (TextView) findViewById(R.id.btn_go);
        btn_fresh = findViewById(R.id.btn_fresh);
        btn_relocate = findViewById(R.id.btn_map_relocate);

        img_navi_header = (ImageView) navi.getHeaderView(0).findViewById(R.id.img_navi_header);
        text_username = (TextView) navi.getHeaderView(0).findViewById(R.id.text_navi_username);
        img_navi_header.setOnClickListener(this);
        btn_go.setOnClickListener(this);

        btn_fresh.setOnClickListener(this);
        findViewById(R.id.img_home_msg).setOnClickListener(this);
        findViewById(R.id.img_home_order).setOnClickListener(this);
        btn_relocate.setOnClickListener(this);

        lay_map_bubble.setVisibility(View.GONE);
        btn_go.setVisibility(View.GONE);
        btn_go.setEnabled(false);
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
        baiduMap.setOnMapStatusChangeListener(onMapStatusChangeListener);

        //设置holdcar事件
        holdcarView.setOnHoldcarListener(onHoldcarListener);

        //设置取消订单点击事件
        driverView.setOnCancleClickListener(new DriverView.OnCancleClickListener() {
            @Override
            public void onCancleClick(int orderId) {
                dialogSure.setObject(orderId);
                dialogSure.show();
            }
        });

        //定位回调
        locationer.setCallback(this);
        //搜索模块
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

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
        //打车界面初始不可见
        holdcarView.setVisibility(View.GONE);
        //车主面板默认不可见
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
        } else {
            text_username.setText("未登录");
            GlideUtil.loadCircleImg(this, img_navi_header, R.drawable.default_header);
        }
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
        HomeHelper.setTrip(this, trip);
        //设置上车地点位置(不是刚匹配成功状态，不是取消状态，并且是未支付状态)
        MapHelper.removeOverlays(overlays);
        if (trip != null && trip.getStatus() != 2001 && trip.getStatus() != 2007 && trip.getIsPay() == 0) {
            Overlay overlayStart = com.ins.kuaidi.utils.AppHelper.addMarkStartEnd(baiduMap, MapHelper.str2LatLng(trip.getFromLat()));
            Overlay overlayEnd = com.ins.kuaidi.utils.AppHelper.addMarkStartEnd(baiduMap, MapHelper.str2LatLng(trip.getToLat()));
            overlays.add(overlayStart);
            overlays.add(overlayEnd);
        }
        //设置司机位置
        if (trip != null && trip.getDriver() != null) {
            driverView.setDriver(trip.getDriver(), trip);
        }
    }

    //设置司机位置
    public void setCarData(LatLng latLng) {
        carMap.addMove(baiduMap, latLng);
    }

    private void setCity(String city) {
        this.city = city;
        text_title.setText(city);
        netHelper.netGetArea(city);
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
                        mSearch.geocode(new GeoCodeOption().city(city).address(city));
                    }
                }
                break;
        }
    }

    //导航菜单选择回调
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
//        drawer.closeDrawer(Gravity.LEFT);
        return false;
    }

    //页面内点击事件回调
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
                    intent.putExtra("city", city);
                    startActivityForResult(intent, RESULT_CITY);
                } else {
                    Toast.makeText(this, "正在定位中...", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.lay_map_bubble:
                if (AppData.App.getUser() != null) {
                    btn_go.setVisibility(View.VISIBLE);
                    lay_map_bubble.setVisibility(View.GONE);
                    holdcarView.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.Landing)
                            .duration(200)
                            .playOn(holdcarView);
                } else {
                    intent.setClass(this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.btn_go:
                //屏蔽快速双击事件
                if (ClickUtils.isFastDoubleClick()){
                    return;
                }
                if ("呼叫快车".equals(btn_go.getText())) {
                    Position startPosition = holdcarView.getStartPosition();
                    Position endPosition = holdcarView.getEndPosition();
                    String msg = AppVali.orderadd(holdcarView.getDay(), holdcarView.getTime(), holdcarView.getSelectCount(), startPosition, endPosition);
                    if (msg != null) {
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        netHelper.netOrderAdd(holdcarView.getDay(), holdcarView.getTime(), holdcarView.getSelectCount(),
                                MapHelper.LatLng2Str(startPosition.getLatLng()), MapHelper.LatLng2Str(endPosition.getLatLng()),
                                startPosition.getKey(), endPosition.getKey(),
                                startPosition.getCity(), endPosition.getCity(),
                                holdcarView.getMsg());
                    }
                } else if ("支付定金".equals(btn_go.getText())) {
                    intent.setClass(this, PayActivity.class);
                    intent.putExtra("type", 0);
                    intent.putExtra("trip", trip);
                    startActivity(intent);
                } else if ("支付尾款".equals(btn_go.getText())) {
                    intent.setClass(this, PayActivity.class);
                    intent.putExtra("type", 2);
                    intent.putExtra("trip", trip);
                    startActivity(intent);
                }
                break;
            case R.id.btn_map_relocate:
                MapHelper.zoomByPoint(baiduMap, nowLatLng);
                break;
            case R.id.btn_fresh:
                HomeHelper.setFresh(this);
                break;
        }
    }

    //百度地图状态监听回调
    private BaiduMap.OnMapStatusChangeListener onMapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {
            holdcarView.setAlpha(0.1f);
            if (isIn) {
                //打车面板不可见并且中心面板可见的时候才显示打车气泡
                if (holdcarView.getVisibility() != View.VISIBLE && lay_map_center.getVisibility() == View.VISIBLE) {
                    //只有没有行程并且行程为初始状态才显示摇杆
                    if (trip == null || trip.getStatus() == Trip.STA_2001) {
                        YoYo.with(Techniques.TakingOff).duration(200).playOn(lay_map_bubble);
                    }
                }
            }
        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {
        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            holdcarView.setAlpha(1f);
            isIn = MapHelper.isInAreas(ptsArray, mapStatus.target);
            if (isIn) {
                //只有没有行程才显示摇杆
                if (trip == null) {
                    //打车面板不可见并且中心面板可见的时候才显示打车气泡
                    if (holdcarView.getVisibility() != View.VISIBLE && lay_map_center.getVisibility() == View.VISIBLE) {
                        lay_map_bubble.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.Landing).duration(200).playOn(lay_map_bubble);
                    }
                    //检索地址
                    mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(mapStatus.target));
                }
            } else {
                //不在围栏里面则清除上车地点
                holdcarView.setStartPosition(null);
            }
        }
    };

    //自定义打车面板事件监听回调
    private HoldcarView.OnHoldcarListener onHoldcarListener = new HoldcarView.OnHoldcarListener() {
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
            intent.putExtra("city", city);
            startActivity(intent);
        }

        @Override
        public void onEndClick(View v) {
            type = 1;
            Intent intent = new Intent(HomeActivity.this, SearchAddressActivity.class);
            intent.putExtra("city", city);
            startActivity(intent);
        }
    };

    //定位回调
    @Override
    public void onLocation(LatLng latLng, String city, boolean isFirst) {
        //定位成功后保存定位坐标
        this.nowLatLng = latLng;
        //定位成功后保存定位城市
        this.nowcity = city;
        if (isFirst) {
            setCity(city);
        }
        //当前有行程状态的时候（不是初始状态,并且已经匹配到司机），则要不断拉取司机位置信息
        if (trip != null && trip.getDriver() != null) {
            netHelper.netLatDriver(trip.getDriver().getLineId(), trip.getDriverId());
        }
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
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
        } else {
            String newCity = result.getAddressDetail().city;
            //移动标杆，重新设置出发地
            Position position = new Position(result.getLocation(), result.getAddress(), newCity);
            if (com.ins.kuaidi.utils.AppHelper.needNetConfigStart(holdcarView, newCity)) {
                holdcarView.setStartPosition(position);
                netHelper.netGetLineConfig(position.getCity(), holdcarView.getEndPosition().getCity());
            } else {
                holdcarView.setStartPosition(position);
            }
        }
    }
}
