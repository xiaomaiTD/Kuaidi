package com.ins.middle.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/6/30 0030.
 */
public class CommonEntity {

    @SerializedName("id")
    private int id;

    @SerializedName("valiCode")
    private String valicode;

    @SerializedName("url")
    private String filePath;

    @SerializedName("location")
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValicode() {
        return valicode;
    }

    public void setValicode(String valicode) {
        this.valicode = valicode;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
