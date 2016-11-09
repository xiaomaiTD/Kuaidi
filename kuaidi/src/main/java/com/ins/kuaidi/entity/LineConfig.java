package com.ins.kuaidi.entity;

import java.io.Serializable;

/**
 * 功能：路线配置
 * 编写人员：lzh
 * 时间：2016年11月4日下午4:17:52
 */
public class LineConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    /**
     * 线路id
     */
    private int lineId;
    /**
     * 往返 0：往 1：返
     */
    private int flag;
    /**
     * 有车的时间
     */
    private int haveCarTime;
    /**
     * 当前车容量
     */
    private int carCapacity;
    /**
     * 有车是否开启 0:未开启 1:开启
     */
    private int haveStatus;

    /**
     * 开始时间 包车 时间戳
     */
    private int startTime;
    /**
     * 开始时间 包车
     */
    private String startTimes;
    /**
     * 结束时间 包车 时间戳
     */
    private int endTime;
    /**
     * 结束时间 包车
     */
    private String endTimes;

    /**
     * 返回给移动端的数据
     */
    private long now;
    /**
     * 路线支付金额
     */
    private float payMoney;

    /**
     * 折扣 如 9.0折
     */
    private float discount;


    public float getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(float payMoney) {
        this.payMoney = payMoney;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getHaveCarTime() {
        return haveCarTime;
    }

    public void setHaveCarTime(int haveCarTime) {
        this.haveCarTime = haveCarTime;
    }

    public int getCarCapacity() {
        return carCapacity;
    }

    public void setCarCapacity(int carCapacity) {
        this.carCapacity = carCapacity;
    }

    public int getHaveStatus() {
        return haveStatus;
    }

    public void setHaveStatus(int haveStatus) {
        this.haveStatus = haveStatus;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public String getStartTimes() {
        return startTimes;
    }

    public void setStartTimes(String startTimes) {
        this.startTimes = startTimes;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String getEndTimes() {
        return endTimes;
    }

    public void setEndTimes(String endTimes) {
        this.endTimes = endTimes;
    }


}
