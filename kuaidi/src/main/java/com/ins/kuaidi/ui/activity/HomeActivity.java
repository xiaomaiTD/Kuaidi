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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
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
import com.ins.kuaidi.common.WaitingHelper;
import com.ins.middle.utils.PushValiHelper;
import com.ins.kuaidi.wxapi.WXPayEntryActivity;
import com.ins.middle.entity.CarMap;
import com.ins.middle.entity.EventIdentify;
import com.ins.middle.entity.EventOrder;
import com.ins.middle.ui.activity.CityActivity;
import com.ins.middle.ui.activity.MsgClassActivity;
import com.ins.middle.ui.activity.WalletActivity;
import com.ins.middle.ui.dialog.DialogSure;
import com.ins.middle.utils.MapHelper;
import com.ins.middle.utils.SnackUtil;
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

    public WaitingHelper waitingHelper;
    private UpdateHelper updateHelper;
    public NetHelper netHelper;
    public Locationer locationer;
    private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

    public View showingroup;

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
    public View img_home_cancel;
    public ProgressBar progress_title;

    public DialogLoading dialogLoading;
    private DialogMouthPicker dialogTime;
    private DialogPopupMsg dialogPopupMsg;
    public DialogSure dialogSure;

    //    private static final int RESULT_SEARCHADDRESS = 0xf101;
    private int type = 0;   //0:点击出发地点 1:点击目的地
    //当前选择城市
    public String city;
    //当前定位城市
    public String nowcity;
    //当前定位位置
    public LatLng nowLatLng;
    public boolean isIn;
    //地理围栏
    public List<List<LatLng>> ptsArray = new ArrayList<>();
    public ArrayList<Overlay> areasLay = new ArrayList<>();
    public List<Trip> trips = new ArrayList<>();
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
            //如果改变了出发城市，那么重新拉取地理围栏
            if (!position.getCity().equals(city)) {
                setCity(position.getCity());
            }
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
    public void onEventMainThread(EventIdentify eventIdentify) {
        String aboutsystem = eventIdentify.getAboutsystem();
        String msg = eventIdentify.getMsg();
        if ("15".equals(aboutsystem)) {
            //审核通过
//            User user = AppData.App.getUser();
//            user.setStatus(User.AUTHENTICATED);
//            AppData.App.saveUser(user);
//            setUserData();
            SnackUtil.showSnack(showingroup, msg);
            netHelper.getInfo();
        } else if ("16".equals(aboutsystem)) {
            //审核不通过
//            User user = AppData.App.getUser();
//            user.setStatus(User.UNAUTHORIZED);
//            AppData.App.saveUser(user);
//            setUserData();
            SnackUtil.showSnack(showingroup, msg);
            netHelper.getInfo();
        }
    }

    @Subscribe
    public void onEventMainThread(EventOrder eventOrder) {
        String aboutOrder = eventOrder.getAboutOrder();
        int orderId = eventOrder.getOrderId();
        String msg = eventOrder.getMsg();
        if ("3".equals(aboutOrder) && PushValiHelper.pushPRequestPayFirst(trips, orderId)) {
            //请求支付定金
            netHelper.netGetTrip(orderId);
            SnackUtil.showSnack(showingroup, msg);
//            SnackUtil.showSnack(showingroup, "司机已经接到您的订单，请及时支付定金");
        } else if ("4".equals(aboutOrder) && PushValiHelper.pushPGetPassenger(trips, orderId)) {
            //接到乘客
            netHelper.netGetTrip(orderId);
            SnackUtil.showSnack(showingroup, msg);
//            SnackUtil.showSnack(showingroup, "司机已经接到您，正在接送其他乘客");
        } else if ("5".equals(aboutOrder) && PushValiHelper.pushPArrive(trips, orderId)) {
            //已经到达目的地
            //因为乘客支付后可继续下单的改动，这里不需要再刷新了
            //HomeHelper.setFresh(this);
            //设置行程为已到达状态，下次定位会检查状态从地图上移除司机位置
            if (trip != null) trip.setStatus(Trip.STA_2006);
            SnackUtil.showSnack(showingroup, msg);
//            SnackUtil.showSnack(showingroup, "司机已将您送达目的地，感谢您的使用");
        } else if ("7".equals(aboutOrder) && PushValiHelper.pushPMattch(trips, orderId)) {
            //乘客端： 订单已经匹配 已经分配给司机
            netHelper.netGetTrip();
            SnackUtil.showSnack(showingroup, msg);
//            SnackUtil.showSnack(showingroup, "已为您匹配到司机，等待司机确认");
        } else if ("9".equals(aboutOrder) && PushValiHelper.pushPStart(trips, orderId)) {
            //司机出发
            SnackUtil.showSnack(showingroup, msg);
//            SnackUtil.showSnack(showingroup, "司机已出发，正在前往目的地");
        } else if ("14".equals(aboutOrder) && PushValiHelper.pushPCancle(trips, orderId)) {
            //(后台)乘客取消了订单
            HomeHelper.setFresh(this);
            SnackUtil.showSnack(showingroup, msg);
//            SnackUtil.showSnack(showingroup, "客服人员取消了您的订单，如有疑问请联系客服");
        } else if ("8".equals(aboutOrder)) {
            //定金支付成功(乘客端本地的推送)
            //2004 乘客已支付预付款
            HomeHelper.setPayLastFalse(this);
            trip.setStatus(Trip.STA_2004);
        } else if ("101".equals(aboutOrder)) {
            //乘客已经支付尾款(乘客端本地的推送)
            //netHelper.netGetTrip();
            HomeHelper.setFresh(this);
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
                if (trip != null) {
                    netHelper.netCancleOrder(trip.getId());
                }
//                else {
//                    int orderId = (int) dialogSure.getObject();
//                    netHelper.netCancleOrder(orderId);
//                }
                dialogSure.hide();
            }
        });
    }

    private void initView() {
        showingroup = findViewById(R.id.showingroup);
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
        waitingHelper = new WaitingHelper(btn_go);
        btn_fresh = findViewById(R.id.btn_fresh);
        btn_relocate = findViewById(R.id.btn_map_relocate);
        progress_title = (ProgressBar) findViewById(R.id.progress_home_title);

        img_navi_header = (ImageView) navi.getHeaderView(0).findViewById(R.id.img_navi_header);
        text_username = (TextView) navi.getHeaderView(0).findViewById(R.id.text_navi_username);
        img_navi_header.setOnClickListener(this);
        btn_go.setOnClickListener(this);

        btn_fresh.setOnClickListener(this);
        findViewById(R.id.img_home_msg).setOnClickListener(this);
        img_home_cancel = findViewById(R.id.img_home_cancel);
        img_home_cancel.setOnClickListener(this);
        btn_relocate.setOnClickListener(this);

        progress_title.setVisibility(View.INVISIBLE);
        img_home_cancel.setVisibility(View.GONE);
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
                if (trip != null) {
                    dialogSure.show();
                }
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
        //设置取消行程按钮
        //订单尚未匹配成功时(2001)，才显示该按钮
        if (trip != null && trip.getStatus() == Trip.STA_2001) {
            img_home_cancel.setVisibility(View.VISIBLE);
        } else {
            img_home_cancel.setVisibility(View.GONE);
        }
    }

    //设置司机位置
    public void setCarData(LatLng latLng) {
        User driver = carMap.getDriver();
        if (driver != null && trip != null) {
            if (driver.getId() == trip.getDriver().getId()) {
                carMap.addMove(mapView, latLng);
            } else {
                carMap.removeFromMap();
                carMap.addMove(mapView, latLng);
            }
            carMap.setDriver(trip.getDriver());
        } else {
            carMap.addMove(mapView, latLng);
            if (trip != null) carMap.setDriver(trip.getDriver());
        }
    }

    public void setCity(String city) {
        this.city = city;
        text_title.setText(!StrUtils.isEmpty(city) ? city : "定位失败");
        netHelper.netGetArea(city);
    }

    //移动大头针开始，显示消失动画
    public void setBubbleOff() {
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

    //移动大头针结束，显示渐出动画
    public void setBubbleOn(LatLng latLng, boolean needSearch) {
        if (latLng == null) {
            return;
        }
        isIn = MapHelper.isInAreas(ptsArray, latLng);
        if (isIn) {
            //只有没有行程才显示摇杆
            if (trip == null) {
                //打车面板不可见并且中心面板可见的时候才显示打车气泡
                if (holdcarView.getVisibility() != View.VISIBLE && lay_map_center.getVisibility() == View.VISIBLE) {
                    lay_map_bubble.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.Landing).duration(200).playOn(lay_map_bubble);
                }
                //检索地址
                if (needSearch) mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
            }
        } else {
            //不在围栏里面则清除上车地点
            holdcarView.setStartPosition(null);
            if (trip == null) {
                if (needSearch) mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
            }
        }
    }

    public void setBubbleOn(LatLng latLng) {
        setBubbleOn(latLng, true);
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
                    String latlng = data.getStringExtra("latlng");
                    if (!StrUtils.isEmpty(city) && !StrUtils.isEmpty(latlng)) {
                        setCity(city);
//                        mSearch.geocode(new GeoCodeOption().city(city).address(city));
                        MapHelper.zoomToPosition(mapView, MapHelper.str2LatLng(latlng));
                    }
                }
                break;
        }
    }

    //导航菜单选择回调
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
        //屏蔽快速双击事件
        if (ClickUtils.isFastDoubleClick()) return;

        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.img_home_msg:
                intent.setClass(this, MsgClassActivity.class);
                startActivity(intent);
                break;
            case R.id.img_home_cancel:
                dialogSure.show();
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
                    intent.putExtra("latlng", MapHelper.LatLng2Str(nowLatLng));
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
                    intent.setClass(this, WXPayEntryActivity.class);
                    intent.putExtra("type", 0);
                    intent.putExtra("trip", trip);
                    startActivity(intent);
                } else if ("支付尾款".equals(btn_go.getText())) {
                    intent.setClass(this, WXPayEntryActivity.class);
                    intent.putExtra("type", 2);
                    intent.putExtra("trip", trip);
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
                if (trip != null) {
                    HomeHelper.setFresh(this, trip.getId());
                } else {
                    HomeHelper.setFresh(this);
                }
                break;
        }
    }

    //百度地图状态监听回调
    private BaiduMap.OnMapStatusChangeListener onMapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {
            holdcarView.setAlpha(0.1f);
            setBubbleOff();
        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {
        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            holdcarView.setAlpha(1f);
            setBubbleOn(mapStatus.target);
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
            intent.putExtra("latLng", nowLatLng);
            startActivity(intent);
        }

        @Override
        public void onEndClick(View v) {
            type = 1;
            Intent intent = new Intent(HomeActivity.this, SearchAddressActivity.class);
            intent.putExtra("city", city);
            intent.putExtra("latLng", nowLatLng);
            startActivity(intent);
        }
    };

    //定位回调
    @Override
    public void onLocation(LatLng latLng, String city, String district, boolean isFirst) {
        //定位成功后保存定位坐标
        this.nowLatLng = latLng;
        //定位成功后保存定位城市
        this.nowcity = city + district;
        if (isFirst) {
            setCity(city + district);
        }
        //当前有行程状态的时候（不是初始状态,并且已经匹配到司机），则要不断拉取司机位置信息
        reqDriverPosition();
    }

    //检索回调
    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
    }

    //反检索回调
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
        } else if (result.getAddressDetail() == null) {
            return;
        } else {
            if (isIn) {
                //县级改动引起的，这里始终修改为选择的城市
//            String newCity = result.getAddressDetail().city;
                String district = result.getAddressDetail().district;
                String newCity = city;
                //移动标杆，重新设置出发地
                Position position = new Position(result.getLocation(), result.getAddress(), newCity);
                if (com.ins.kuaidi.utils.AppHelper.needNetConfigStart(holdcarView, newCity)) {
                    holdcarView.setStartPosition(position);
                    netHelper.netGetLineConfig(position.getCity(), holdcarView.getEndPosition().getCity());
                } else {
                    holdcarView.setStartPosition(position);
                }
            } else {
                String district = result.getAddressDetail().district;
                String newCity = result.getAddressDetail().city;
                String address = newCity + district;
                Log.e("liao", address);
                if (!city.equals(address)) {
                    setCity(address);
                }
            }
        }
    }

    //请求司机位置并显示
    private void reqDriverPosition() {
        if (trip != null && trip.getDriver() != null) {
            //又改了需求：乘客到达目的地后（status<2006）就看不见司机了
            if (trip.getStatus() < Trip.STA_2006) {
                netHelper.netLatDriver(trip.getDriver().getLineId(), trip.getDriverId());
            } else {
                if (carMap != null) carMap.removeFromMap();
            }
        }
    }
}
