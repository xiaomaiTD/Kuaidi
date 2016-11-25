package com.ins.kuaidi.entity;

import java.io.Serializable;

/**
 * 支付数据组装 实体类
 *
 * @author QimouXie
 */
public class Coupon implements Serializable {
    private int id;
    private int userType;
    private int numCoupon;
    private int userId;

    private float money;
    private int voucherId;
    private int isUse;
    private long dueDate;
    private int walletType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getNumCoupon() {
        return numCoupon;
    }

    public void setNumCoupon(int numCoupon) {
        this.numCoupon = numCoupon;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public int getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(int voucherId) {
        this.voucherId = voucherId;
    }

    public int getIsUse() {
        return isUse;
    }

    public void setIsUse(int isUse) {
        this.isUse = isUse;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public int getWalletType() {
        return walletType;
    }

    public void setWalletType(int walletType) {
        this.walletType = walletType;
    }
}
