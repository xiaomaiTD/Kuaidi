package com.ins.kuaidi.entity;

import java.io.Serializable;

/**
 *   支付数据组装 实体类
 * @author QimouXie
 *
 */
public class PayData implements Serializable {
	
	/** 预计总费用*/
	private double total;
	
	/**本次需要支付的总金额*/
	private double thisTotalPay;
	
	/**优惠券抵扣金额*/
	private double coupon;
	
	/**余额支付*/
	private double balance;
	
	/**需要用支付宝 微信 实际支付*/
	private double actualPay;
	
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public double getThisTotalPay() {
		return thisTotalPay;
	}
	public void setThisTotalPay(double thisTotalPay) {
		this.thisTotalPay = thisTotalPay;
	}
	public double getCoupon() {
		return coupon;
	}
	public void setCoupon(double coupon) {
		this.coupon = coupon;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public double getActualPay() {
		return actualPay;
	}
	public void setActualPay(double actualPay) {
		this.actualPay = actualPay;
	}

	
}
