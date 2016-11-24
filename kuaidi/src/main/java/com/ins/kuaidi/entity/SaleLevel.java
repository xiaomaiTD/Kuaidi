package com.ins.kuaidi.entity;

import java.io.Serializable;

/**
 * 支付数据组装 实体类
 *
 * @author QimouXie
 */
public class SaleLevel implements Serializable {
    private int level;
    private int money;
    private int peopleNum;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(int peopleNum) {
        this.peopleNum = peopleNum;
    }
}
