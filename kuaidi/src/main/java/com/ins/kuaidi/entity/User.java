package com.ins.kuaidi.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/30 0030.
 */
public class User implements Serializable {

    /**平台管理员*/
    public static final int PLATFORM_MANAGER = 1;
    /**片区管理员/片区经理*/
    public static final int AREA_MANAGER = 2;
    /**片区技术员*/
    public static final int AREA_TECH = 3;
    /**片区副经理*/
    public static final int AREA_ASSISTANT_MANAGER = 4;
    /**办事处人员*/
    public static final int OFFICE_WORKER = 5;
    /**水厂工作人员*/
    public static final int WATER_FACTORY_WORKER = 6;
    /**物业人员*/
    public static final int ESTATE_WORKER=7;


    private String msg;
    private int code;
    private String data;
    private long updatetime;
    private long createtime;
    private String realName;
    private String pwd;
    private int id;
    private String email;
    private String mobile;
    private String qq;
    private String avatar;
    private int roleId;
    private String roleName;
    private int status;
    private int gender;
    private String token;
    private int companyId;
    private int deviceType;
    private String deviceToken;
    private String jobNumber;
    private String jobTitle;
    private String userName;

    private String unitName;
    private String unitNumber;

    //新增
    private int idType;
    private int silence;
    //本地实例字段


    public int getSilence() {
        return silence;
    }

    public void setSilence(int silence) {
        this.silence = silence;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(long updatetime) {
        this.updatetime = updatetime;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
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

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "User{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", data='" + data + '\'' +
                ", updatetime=" + updatetime +
                ", createtime=" + createtime +
                ", realName='" + realName + '\'' +
                ", pwd='" + pwd + '\'' +
                ", id=" + id +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", qq='" + qq + '\'' +
                ", avatar='" + avatar + '\'' +
                ", roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", status=" + status +
                ", gender=" + gender +
                ", token='" + token + '\'' +
                ", companyId=" + companyId +
                ", deviceType=" + deviceType +
                ", deviceToken='" + deviceToken + '\'' +
                ", jobNumber='" + jobNumber + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
