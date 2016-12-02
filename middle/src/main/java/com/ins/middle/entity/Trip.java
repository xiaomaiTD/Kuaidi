package com.ins.middle.entity;

import com.baidu.mapapi.map.Overlay;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/12 0012.
 */
public class Trip implements Serializable{

    public static final int STA_2001 = 2001;//匹配成功
    public static final int STA_2002 = 2002;//司机发送支付定金状态
    public static final int STA_2003 = 2003;//司机等待乘客支付定金状态
    public static final int STA_2004 = 2004;//2004 乘客已支付预付款
    public static final int STA_2005 = 2005;//2005 司机接到乘客
    public static final int STA_2006 = 2006;//2006 乘客已抵达
    public static final int STA_2007 = 2007;//订单已取消


    /**主键ID*/
    private int id;

    /**订单号*/
    private String orderNumber;

    /**创建时间*/
    private long createTime;

    /**更新时间*/
    private long updateTime;

    /**订单状态*/
    private int status;

    /**出发地*/
    private int fromCityId;

    /**目的地*/
    private int toCityId;

    /**订单类型 0:包车 1:拼车*/
    private int orderType;

    /** 是否有效 0 无效 1 有效*/
    private int isValid;

    /**支付详细 JSON数据*/
    private String payDetail;

    private String bossesPayDetail;

    /**总支付金额*/
    private String payMoney;
    /**司机总支付金额*/
    private String driverDetail;

    /**预付金额*/
    private String payAdvance;

    /** 司机ID*/
    private int driverId;

    /**乘客ID*/
    private int passengerId;

    /**始发地 经纬度*/
    private String fromLat;

    /**目的地经纬度*/
    private String toLat;

    /**订单人数*/
    private int peoples;

    /**始发地 地址名称*/
    private String fromAdd;

    /**目的地 地址名称*/
    private String toAdd;

    /**是否支付定金 0:没有 1:发送了请求 但 未支付 2:已经支付成功*/
    private int isPayDeposit;

    /**是否支付尾款 0:否 1:已支付*/
    private int isPay;

    /**是否接到乘客 0:否 1: 是*/
    private int isReceivePassenger;

    /**是否送达目的地 0:否 1：是*/
    private int isArrive;

    /**乘客*/
    private User passenger;
    /**司机*/
    private User driver;



    //本地字段
//    @Expose
    private transient  Overlay mark;
    private boolean isCheck;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public Overlay getMark() {
        return mark;
    }

    public void setMark(Overlay mark) {
        this.mark = mark;
    }

    public Trip() {
    }

    public String getDriverDetail() {
        return driverDetail;
    }

    public void setDriverDetail(String driverDetail) {
        this.driverDetail = driverDetail;
    }

    public String getBossesPayDetail() {
        return bossesPayDetail;
    }

    public void setBossesPayDetail(String bossesPayDetail) {
        this.bossesPayDetail = bossesPayDetail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFromCityId() {
        return fromCityId;
    }

    public void setFromCityId(int fromCityId) {
        this.fromCityId = fromCityId;
    }

    public int getToCityId() {
        return toCityId;
    }

    public void setToCityId(int toCityId) {
        this.toCityId = toCityId;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

    public String getPayDetail() {
        return payDetail;
    }

    public void setPayDetail(String payDetail) {
        this.payDetail = payDetail;
    }

    public String getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(String payMoney) {
        this.payMoney = payMoney;
    }

    public String getPayAdvance() {
        return payAdvance;
    }

    public void setPayAdvance(String payAdvance) {
        this.payAdvance = payAdvance;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public String getFromLat() {
        return fromLat;
    }

    public void setFromLat(String fromLat) {
        this.fromLat = fromLat;
    }

    public String getToLat() {
        return toLat;
    }

    public void setToLat(String toLat) {
        this.toLat = toLat;
    }

    public int getPeoples() {
        return peoples;
    }

    public void setPeoples(int peoples) {
        this.peoples = peoples;
    }

    public String getFromAdd() {
        return fromAdd;
    }

    public void setFromAdd(String fromAdd) {
        this.fromAdd = fromAdd;
    }

    public String getToAdd() {
        return toAdd;
    }

    public void setToAdd(String toAdd) {
        this.toAdd = toAdd;
    }

    public int getIsPayDeposit() {
        return isPayDeposit;
    }

    public void setIsPayDeposit(int isPayDeposit) {
        this.isPayDeposit = isPayDeposit;
    }

    public int getIsPay() {
        return isPay;
    }

    public void setIsPay(int isPay) {
        this.isPay = isPay;
    }

    public int getIsReceivePassenger() {
        return isReceivePassenger;
    }

    public void setIsReceivePassenger(int isReceivePassenger) {
        this.isReceivePassenger = isReceivePassenger;
    }

    public int getIsArrive() {
        return isArrive;
    }

    public void setIsArrive(int isArrive) {
        this.isArrive = isArrive;
    }

    public User getPassenger() {
        return passenger;
    }

    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", status=" + status +
                ", fromCityId=" + fromCityId +
                ", toCityId=" + toCityId +
                ", orderType=" + orderType +
                ", isValid=" + isValid +
                ", payDetail='" + payDetail + '\'' +
                ", payMoney='" + payMoney + '\'' +
                ", payAdvance='" + payAdvance + '\'' +
                ", driverId=" + driverId +
                ", passengerId=" + passengerId +
                ", fromLat='" + fromLat + '\'' +
                ", toLat='" + toLat + '\'' +
                ", peoples=" + peoples +
                ", fromAdd='" + fromAdd + '\'' +
                ", toAdd='" + toAdd + '\'' +
                ", isPayDeposit=" + isPayDeposit +
                ", isPay=" + isPay +
                ", isReceivePassenger=" + isReceivePassenger +
                ", isArrive=" + isArrive +
                '}';
    }
}
