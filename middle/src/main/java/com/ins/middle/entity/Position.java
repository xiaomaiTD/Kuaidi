package com.ins.middle.entity;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.SuggestionResult;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/27.
 */

public class Position implements Serializable{

    //城市
    private String city;
    //地址
    private String key;
    //详细地址
    private String district;
    //坐标
    private LatLng latLng;

    public Position(LatLng latLng, String key, String city) {
        this.latLng = latLng;
        this.key = key;
        this.city = city;
    }

    public Position() {
    }

    public Position(String district) {
        this.district = district;
    }

    public Position(SuggestionResult.SuggestionInfo suggest) {
        key = suggest.key;
        city = suggest.city;
        district = suggest.district;
        latLng = suggest.pt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
