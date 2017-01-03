package com.ins.middle.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/12 0012.
 */
public class CashHis implements Serializable {


    /**
     * 申请提现 4001
     * 银行正在处理中 4002
     * 处理完成  4003
     * 处理失败  4004
     */

    private int id;
    private String bankName;
    private String bankAcc;
    private float money;
    private long createTime;
    private int status;

    public String getStatusName(){
        switch (status){
            case 4001:
                return "申请提现";
            case 4002:
                return "银行正在处理中";
            case 4003:
                return "处理完成";
            case 4004:
                return "处理失败";
            default:
                return "状态错误";
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAcc() {
        return bankAcc;
    }

    public void setBankAcc(String bankAcc) {
        this.bankAcc = bankAcc;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
