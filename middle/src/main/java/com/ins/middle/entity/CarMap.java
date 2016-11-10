package com.ins.middle.entity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.ins.middle.R;
import com.ins.middle.utils.MapUtil;

/**
 * Created by Administrator on 2016/10/24.
 */

public class CarMap {

    private LatLng start;
    private LatLng end;

    private Marker mMoveMarker;
    private ValueAnimator animator;
    private int lastvalue;

    private void addToMap(BaiduMap baiduMap, LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().flat(true).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_car)).position(latLng);
        mMoveMarker = (Marker) baiduMap.addOverlay(markerOptions);
        start = latLng;
    }

    private void moveTo(final LatLng latLng) {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        //设置角度
        mMoveMarker.setRotate((float) MapUtil.getAngle(start, latLng));
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
                mMoveMarker.setPosition(end);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.e("liao", "onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                start = end;
                Log.e("liao", "onAnimationEnd");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.e("liao", "onAnimationCancel");

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    public void addMove(BaiduMap baiduMap, LatLng latLng) {
        if (mMoveMarker == null) {
            addToMap(baiduMap, latLng);
        } else {
            moveTo(latLng);
        }
    }
}
