package com.ins.middle.entity;

import java.io.Serializable;

/**
 *   支付数据组装 实体类
 * @author QimouXie
 *
 */
public class PayDataDriver implements Serializable {
	private float actualCheques;
	private float depositPay;
	private float bosses;

	public float getActualCheques() {
		return actualCheques;
	}

	public void setActualCheques(float actualCheques) {
		this.actualCheques = actualCheques;
	}

	public float getDepositPay() {
		return depositPay;
	}

	public void setDepositPay(float depositPay) {
		this.depositPay = depositPay;
	}

	public float getBosses() {
		return bosses;
	}

	public void setBosses(float bosses) {
		this.bosses = bosses;
	}
}
