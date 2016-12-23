package com.ins.middle.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/12 0012.
 */
public class Cash implements Serializable {
    private float cashRate;
    private float driverDeposit;
    private float passengerDeposit;

    public float getCashRate() {
        return cashRate;
    }

    public void setCashRate(float cashRate) {
        this.cashRate = cashRate;
    }

    public float getDriverDeposit() {
        return driverDeposit;
    }

    public void setDriverDeposit(float driverDeposit) {
        this.driverDeposit = driverDeposit;
    }

    public float getPassengerDeposit() {
        return passengerDeposit;
    }

    public void setPassengerDeposit(float passengerDeposit) {
        this.passengerDeposit = passengerDeposit;
    }
}
