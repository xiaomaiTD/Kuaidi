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
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.SpatialRelationUtil;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.reflect.TypeToken;
import com.ins.kuaidi.R;
import com.ins.kuaidi.entity.LineConfig;
import com.ins.kuaidi.utils.MapHelper;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.common.AppVali;
import com.ins.middle.common.CommonNet;
import com.ins.middle.common.Locationer;
import com.ins.middle.entity.CommonEntity;
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
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseAppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, Locationer.LocationCallback, OnGetGeoCoderResultListener {

    private Locationer locationer;
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

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
    private View btn_go;

    private DialogLoading dialogLoading;
    private DialogMouthPicker dialogTime;
    private DialogPopupMsg dialogPopupMsg;

    //    private static final int RESULT_SEARCHADDRESS = 0xf101;
    private int type = 0;   //0:点击出发地点 1:点击目的地
    private String city;
    boolean isIn;
    //地理围栏
    private List<LatLng> pts;

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

    @Subscribe
    public void onEventMainThread(Position position) {
        if (type == 0) {
            //选择了出发点
            MapHelper.zoomToPosition(mapView, position.getLatLng());
        } else {
            //选择了目的地
            if (com.ins.kuaidi.utils.AppHelper.needNetConfigEnd(holdcarView, position.getCity())) {
                netGetLineConfig(holdcarView.getStartPosition().getCity(), position.getCity());
            }
            holdcarView.setEndPosition(position);
        }
    }

    @Subscribe
    public void onEventMainThread(Integer flag) {
        if (flag == AppConstant.EVENT_UPDATE_LOGIN) {
            locationer.isFirstLoc = true;
            setUserData();
        } else if (flag == AppConstant.EVENT_UPDATE_ME) {
            setUserData();
        }
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
    }

    private void initView() {
        holdcarView = (HoldcarView) findViewById(R.id.holdcar);
        mapView = (MapView) findViewById(R.id.mapView);
        locationer = new Locationer(mapView);
        baiduMap = mapView.getMap();
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        navi = (NavigationView) findViewById(R.id.navi);
        img_user = (ImageView) findViewById(R.id.img_home_user);
        text_title = (TextView) findViewById(R.id.text_home_title);
        lay_map_bubble = findViewById(R.id.lay_map_bubble);
        btn_go = findViewById(R.id.btn_go);

        img_navi_header = (ImageView) navi.getHeaderView(0).findViewById(R.id.img_navi_header);
        text_username = (TextView) navi.getHeaderView(0).findViewById(R.id.text_navi_username);
        img_navi_header.setOnClickListener(this);
        btn_go.setOnClickListener(this);
        findViewById(R.id.img_home_msg).setOnClickListener(this);
        findViewById(R.id.img_home_order).setOnClickListener(this);

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
        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                holdcarView.setAlpha(0.1f);
                if (isIn) {
                    YoYo.with(Techniques.TakingOff)
                            .duration(200)
                            .playOn(lay_map_bubble);
                }
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                holdcarView.setAlpha(1f);
//                isIn = SpatialRelationUtil.isPolygonContainsPoint(pts, mapStatus.target);
                isIn = true;
                if (isIn) {
                    //打车面板不可见的时候才显示打车气泡
                    if (holdcarView.getVisibility() != View.VISIBLE) {
                        lay_map_bubble.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.Landing)
                                .duration(200)
                                .playOn(lay_map_bubble);
                    }

                    //检索地址
                    mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(mapStatus.target));
                } else {
                    //不在围栏里面则清除上车地点
                    holdcarView.setStartPosition(null);
                }
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

        //定位回调
        locationer.setCallback(this);
        //搜索模块
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
    }

    private void initData() {
        //设置用户信息
        setUserData();
        //开始定位
        locationer.startlocation();
        //打车界面初始不可见
        holdcarView.setVisibility(View.GONE);

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
                intent.setClass(this, SearchAddressActivity.class);
                startActivity(intent);
                break;
            case R.id.lay_map_bubble:
                btn_go.setVisibility(View.VISIBLE);
                lay_map_bubble.setVisibility(View.GONE);
                holdcarView.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.Landing)
                        .duration(200)
                        .playOn(holdcarView);
                break;
            case R.id.btn_go:
                Position startPosition = holdcarView.getStartPosition();
                Position endPosition = holdcarView.getEndPosition();
                String msg = AppVali.orderadd(holdcarView.getDay(), holdcarView.getTime(), holdcarView.getSelectCount(), startPosition, endPosition);
                if (msg != null) {
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    netOrderAdd(holdcarView.getDay(), holdcarView.getTime(), holdcarView.getSelectCount(),
                            MapHelper.LatLng2Str(startPosition.getLatLng()), MapHelper.LatLng2Str(endPosition.getLatLng()),
                            startPosition.getKey(), endPosition.getKey(),
                            startPosition.getCity(), endPosition.getCity(),
                            holdcarView.getMsg());
                }
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

    //定位回调
    @Override
    public void onLocation(LatLng latLng, String city, boolean isFirst) {
        if (isFirst) {
            text_title.setText(city);
            netGetArea(city);
        }
        this.city = city;
    }

    //检索回调
    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
    }

    //反检索回调
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
        } else {
            String newCity = result.getAddressDetail().city;
            Position position = new Position(result.getLocation(), result.getAddress(), newCity);
            if (com.ins.kuaidi.utils.AppHelper.needNetConfigStart(holdcarView, newCity)) {
                netGetLineConfig(holdcarView.getStartPosition().getCity(), newCity);
            }
            holdcarView.setStartPosition(position);
        }
    }

    private void netGetArea(String city) {
        RequestParams params = new RequestParams(AppData.Url.getArea);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("cityName", city);
        CommonNet.samplepost(params, new TypeToken<List<String>>() {
        }.getType(), new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                List<String> areas = (ArrayList<String>) pojo;
                pts = MapHelper.string2LatLng(areas);
                MapHelper.drawArea(mapView, pts);
                //dialogLoading.hide();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(HomeActivity.this, text, Toast.LENGTH_SHORT).show();
                //dialogLoading.hide();
            }

            @Override
            public void netStart(int code) {
                //dialogLoading.show();
            }
        });
    }

    private void netGetLineConfig(String fromCity, String toCity) {
        RequestParams params = new RequestParams(AppData.Url.getLineConfig);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("fromCityName", fromCity);
        params.addBodyParameter("toCityName", toCity);
        CommonNet.samplepost(params, LineConfig.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                LineConfig lineConfig = (LineConfig) pojo;
                holdcarView.setLineConfig(lineConfig);
                btn_go.setEnabled(true);
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(HomeActivity.this, text, Toast.LENGTH_SHORT).show();
                //线路不可用，设置叫车按钮不可点击
                btn_go.setEnabled(false);
            }

            @Override
            public void netStart(int code) {
            }
        });
    }

    private void netOrderAdd(int day, String time, int count, String fromLat, String toLat, String fromAdd, String toAdd, String fromCity, String toCity, String msg) {
        RequestParams params = new RequestParams(AppData.Url.orderadd);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("timeFlag", day + "");
        params.addBodyParameter("times", time);
        params.addBodyParameter("peoples", count + "");
        params.addBodyParameter("fromLat", fromLat);
        params.addBodyParameter("toLat", toLat);
        params.addBodyParameter("fromAdd", fromAdd);
        params.addBodyParameter("toAdd", toAdd);
        params.addBodyParameter("fromCityName", fromCity);
        params.addBodyParameter("toCityName", toCity);
        params.addBodyParameter("message", msg);
        CommonNet.samplepost(params, LineConfig.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                Toast.makeText(HomeActivity.this, text, Toast.LENGTH_SHORT).show();
//                LineConfig lineConfig = (LineConfig) pojo;
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
