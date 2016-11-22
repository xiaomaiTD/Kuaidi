package com.ins.middle.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/30 0030.
 */
public class User implements Serializable {

    /**未认证*/
    public static final int UNAUTHORIZED = 0;
    /**认证中*/
    public static final int CERTIFICATIONING = 1;
    /**已认证*/
    public static final int AUTHENTICATED = 2;

    private int id;
    private String realName;
    private int status;
    private String token;
    private String password;
    private String driveLicenseImg;
    private String driveLicenseNumber;
    private String driveingLicenseImg;
    private String driveingLicenseNumber;
    private String mobile;
    private String avatar;
    private int deviceType;
    private String deviceToken;
    private int gender;
    private String nickName;
    private String idCardNumber;
    private String idCardImgs;
    private String autograph;
    private int isValid;
    private long createtime;
    private int age;
    private int adminUserId;
    private long updatetime;
    private String email;

    //司机特色字段 1 在线0 不在线
    private int isOnline;
    private int orderCount;
    private int lineId; //线路id
    private Car car;
    private String latAndLongit;
    private int isStart;    //是否出发 0：否，1：出发
    private float evaFen;    //司机评分

    //乘客特有字段
    //分享码
    private String distributionCode;

    public float getEvaFen() {
        return evaFen;
    }

    public String getDistributionCode() {
        return distributionCode;
    }

    public void setDistributionCode(String distributionCode) {
        this.distributionCode = distributionCode;
    }

    public void setEvaFen(float evaFen) {
        this.evaFen = evaFen;
    }

    public int getIsStart() {
        return isStart;
    }

    public void setIsStart(int isStart) {
        this.isStart = isStart;
    }

    public String getLatAndLongit() {
        return latAndLongit;
    }

    public void setLatAndLongit(String latAndLongit) {
        this.latAndLongit = latAndLongit;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriveLicenseImg() {
        return driveLicenseImg;
    }

    public void setDriveLicenseImg(String driveLicenseImg) {
        this.driveLicenseImg = driveLicenseImg;
    }

    public String getDriveLicenseNumber() {
        return driveLicenseNumber;
    }

    public void setDriveLicenseNumber(String driveLicenseNumber) {
        this.driveLicenseNumber = driveLicenseNumber;
    }

    public String getDriveingLicenseImg() {
        return driveingLicenseImg;
    }

    public void setDriveingLicenseImg(String driveingLicenseImg) {
        this.driveingLicenseImg = driveingLicenseImg;
    }

    public String getDriveingLicenseNumber() {
        return driveingLicenseNumber;
    }

    public void setDriveingLicenseNumber(String driveingLicenseNumber) {
        this.driveingLicenseNumber = driveingLicenseNumber;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public String getIdCardImgs() {
        return idCardImgs;
    }

    public void setIdCardImgs(String idCardImgs) {
        this.idCardImgs = idCardImgs;
    }

    public String getAutograph() {
        return autograph;
    }

    public void setAutograph(String autograph) {
        this.autograph = autograph;
    }

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(int adminUserId) {
        this.adminUserId = adminUserId;
    }

    public long getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(long updatetime) {
        this.updatetime = updatetime;
    }
}
