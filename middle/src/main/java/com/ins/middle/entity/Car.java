package com.ins.middle.entity;

import java.io.Serializable;

/**
 *  汽车实体
 * @author QimouXie
 *
 */
public class Car implements Serializable {

	/**主键ID*/
	private int id;
	
	/**汽车品牌*/
	private String carBrand;
	
	/**汽车车牌*/
	private String carCard;
	
	/**汽车颜色*/
	private String carColor;
	
	/**司机ID*/
	private int driverId;
	
	/**汽车所有人*/
	private String carOwner;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCarBrand() {
		return carBrand;
	}

	public void setCarBrand(String carBrand) {
		this.carBrand = carBrand;
	}

	public String getCarCard() {
		return carCard;
	}

	public void setCarCard(String carCard) {
		this.carCard = carCard;
	}

	public String getCarColor() {
		return carColor;
	}

	public void setCarColor(String carColor) {
		this.carColor = carColor;
	}

	public int getDriverId() {
		return driverId;
	}

	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}

	public String getCarOwner() {
		return carOwner;
	}

	public void setCarOwner(String carOwner) {
		this.carOwner = carOwner;
	}
	
	

}
