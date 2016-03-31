package com.doctor.sun.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rick on 11/20/15.
 * 医生首页
 */
public class DoctorIndex extends RealmObject{


    /**
     * login_num : 4
     * last_login : 2015-08-03 15:39
     * AppointMent_num : 1
     * consult_num : 0
     * transfer_num : 0
     * name : 钟医生
     */

    @JsonProperty("login_num")
    private int loginNum;
    @JsonProperty("last_login")
    private String lastLogin;
    @JsonProperty("AppointMent_num")
    private int AppointMentNum;
    @JsonProperty("consult_num")
    private int consultNum;
    @JsonProperty("transfer_num")
    private int transferNum;
    @PrimaryKey
    @JsonProperty("name")
    private String name;

    public void setLoginNum(int loginNum) {
        this.loginNum = loginNum;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setAppointMentNum(int AppointMentNum) {
        this.AppointMentNum = AppointMentNum;
    }

    public void setConsultNum(int consultNum) {
        this.consultNum = consultNum;
    }

    public void setTransferNum(int transferNum) {
        this.transferNum = transferNum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLoginNum() {
        return loginNum;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public int getAppointMentNum() {
        return AppointMentNum;
    }

    public int getConsultNum() {
        return consultNum;
    }

    public int getTransferNum() {
        return transferNum;
    }

    public String getName() {
        return name;
    }
}
