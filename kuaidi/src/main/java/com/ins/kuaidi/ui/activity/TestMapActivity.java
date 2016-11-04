package com.ins.kuaidi.ui.activity;

import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.SpatialRelationUtil;
import com.ins.kuaidi.R;
import com.ins.middle.entity.Car;
import com.sobey.common.utils.PermissionsUtil;

import java.util.ArrayList;
import java.util.List;
import com.ins.middle.ui.activity.BaseAppCompatActivity;
import com.ins.middle.ui.activity.BaseBackActivity;
public class TestMapActivity extends BaseAppCompatActivity {

    TextView text_latlng;

    MapView mapView;
    BaiduMap baiduMap;
    // 定位相关
    LocationClient locationClient;
    public MyLocationListenner locationListenner = new MyLocationListenner();

    private List<Car> cars = new ArrayList<>();


    //平滑行驶
    private Polyline mPolyline;
    private Marker mMoveMarker;
    private Handler mHandler;
    // 通过设置间隔时间和距离可以控制速度和图标移动的距离
    private static final int TIME_INTERVAL = 50;
    private static final double DISTANCE = 0.00006;

    private List<LatLng> pts = new ArrayList<LatLng>() {{
        add(new LatLng(30.700816, 104.050232));
        add(new LatLng(30.701064, 104.089901));
        add(new LatLng(30.690754, 104.109879));
        add(new LatLng(30.662894, 104.12612));
        add(new LatLng(30.653947, 104.122096));
        add(new LatLng(30.625485, 104.101974));
        add(new LatLng(30.588684, 104.096081));
        add(new LatLng(30.583834, 104.035571));
        add(new LatLng(30.639034, 104.040602));
        add(new LatLng(30.651089, 104.026804));
        add(new LatLng(30.681158, 104.028385));
    }};

    private static final LatLng[] latlngs = new LatLng[]{
            new LatLng(30.610878, 104.085373),
            new LatLng(30.61784, 104.10104),
            new LatLng(30.60603, 104.117137),
            new LatLng(30.591357, 104.104346),
            new LatLng(30.592601, 104.073013),
            new LatLng(30.62331, 104.063239),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testmap);

        PermissionsUtil.checkAndRequestPermissions(this);
        mHandler = new Handler(Looper.getMainLooper());

        initBase();
        initView();
        initData();
        initCtrl();
    }


    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
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
    }

    private void initBase() {
    }

    private void initView() {
        mapView = (MapView) findViewById(R.id.mapView);
        text_latlng = (TextView) findViewById(R.id.text_latlng);
        baiduMap = mapView.getMap();
        cars.add(new Car());
        cars.add(new Car());
        cars.add(new Car());
        cars.add(new Car());
        cars.add(new Car());
    }

    private void initData() {
    }

    private void initCtrl() {
        addOverlay();
        startlocation();

        //设置状态改变事件，监控移动停止时计算是否在打车区域内
        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                LatLng target = mapStatus.target;
                boolean isIn = SpatialRelationUtil.isPolygonContainsPoint(pts, target);
                text_latlng.setText(isIn ? "可以打车" : "不可打车");
            }
        });
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                setCarPosition(latLng);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    /**
     * 设置是否显示交通图
     */
    public void onLuClick(View view) {
        baiduMap.setTrafficEnabled(((CheckBox) view).isChecked());
    }

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

    private void addOverlay() {
        // 添加多边形
        OverlayOptions ooPolygon = new PolygonOptions().points(pts).stroke(new Stroke(5, 0xAA00FF00)).fillColor(0xAAFFFF00);
        baiduMap.addOverlay(ooPolygon);
    }

    private int clicktimes;

    private void setCarPosition(LatLng latLng) {
        clicktimes++;
        int count = cars.size();
        int index = clicktimes % count;
        cars.get(index).addMove(baiduMap, latLng);

    }
}
