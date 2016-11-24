package com.ins.middle.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/6/30 0030.
 */
public class SalePeoplePojo implements Serializable {

    private List<User> dlist;
    private int totalMoney;

    public List<User> getDlist() {
        return dlist;
    }

    public void setDlist(List<User> dlist) {
        this.dlist = dlist;
    }

    public int getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(int totalMoney) {
        this.totalMoney = totalMoney;
    }
}
