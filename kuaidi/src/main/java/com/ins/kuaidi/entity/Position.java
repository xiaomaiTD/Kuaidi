package com.ins.kuaidi.entity;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.SuggestionResult;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/27.
 */

public class Position implements Serializable{

    private String key;
    private String city;
    private String district;
    private LatLng latLng;

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
