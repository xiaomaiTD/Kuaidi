package com.ins.middle.entity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.ins.middle.R;
import com.ins.middle.utils.MapHelper;
import com.ins.middle.utils.MapUtil;
import com.ins.middle.utils.MarkHelper;

/**
 * Created by Administrator on 2016/10/24.
 */

public class CarMap {

    private LatLng start;
    private LatLng end;

    private Marker mMoveMarker;
    private Marker mBubbleMarker;
    private ValueAnimator animator;
    private int lastvalue;
    //是否显示气泡（车主端又改成了这样的需求，尼玛币）
    private boolean needBubble = false;

    public CarMap() {
    }

    public CarMap(boolean needBubble) {
        this.needBubble = needBubble;
    }

    private void addToMap(MapView mapView, LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().flat(true).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_car)).position(latLng);
        mMoveMarker = (Marker) mapView.getMap().addOverlay(markerOptions);
        start = latLng;

        if (needBubble) {
//            MarkerOptions bubbleMarkerOptions = new MarkerOptions().flat(true).anchor(0.5f, 1.0f).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_bubble_car)).position(latLng);
//            mBubbleMarker = (Marker) baiduMap.addOverlay(bubbleMarkerOptions);
            MarkHelper.addDriverMark(mapView, driver, latLng, new MarkHelper.CallBack() {
                @Override
                public void onGetMark(Overlay overlay) {
                    mBubbleMarker = (Marker) overlay;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("driver", driver);
                    mBubbleMarker.setExtraInfo(bundle);
                }
            });
        }
    }

    private void moveTo(final LatLng latLng) {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        //设置角度
        float angle = (float) MapUtil.getAngle(start, latLng);
        //angle==180，表示两次坐标位置一致，不设置角度
        if (angle != 180) mMoveMarker.setRotate(angle);
        final double latdis = latLng.latitude - start.latitude;
        final double londis = latLng.longitude - start.longitude;
        //设置平滑移动
        animator = ValueAnimator.ofInt(1, 30);
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                if (lastvalue == value) {
                    return;
                }
                lastvalue = value;
                double latper = latdis / 30 * value;
                double lonper = londis / 30 * value;
                end = new LatLng(start.latitude + latper, start.longitude + lonper);
                if (mMoveMarker != null) mMoveMarker.setPosition(end);
                if (needBubble && mBubbleMarker != null) mBubbleMarker.setPosition(end);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                start = end;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.start();
    }

    public void addMove(MapView mapView, LatLng latLng) {
        if (mapView == null || latLng == null) {
            return;
        }
        if (mMoveMarker == null) {
            addToMap(mapView, latLng);
        } else {
            moveTo(latLng);
        }
    }

    private User driver;

    public void setDriver(User driver) {
        this.driver = driver;
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("driver", driver);
//        mMoveMarker.setExtraInfo(bundle);
    }

    public User getDriver() {
        return driver;
    }

    public void removeFromMap() {
        if (mMoveMarker != null) {
            mMoveMarker.remove();
            mMoveMarker = null;
        }
        if (mBubbleMarker != null) {
            mBubbleMarker.remove();
            mBubbleMarker = null;
        }
    }
}
