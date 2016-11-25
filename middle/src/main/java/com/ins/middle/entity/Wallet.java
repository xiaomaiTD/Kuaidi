package com.ins.middle.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/12 0012.
 */
public class Wallet implements Serializable{
    private float balance;
    private int coupon;
    private int banks;

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public int getCoupon() {
        return coupon;
    }

    public void setCoupon(int coupon) {
        this.coupon = coupon;
    }

    public int getBanks() {
        return banks;
    }

    public void setBanks(int banks) {
        this.banks = banks;
    }
}
