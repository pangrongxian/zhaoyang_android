package com.doctor.sun.dto;

import com.doctor.sun.entity.AppointMent;
import com.doctor.sun.entity.Prescription;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by rick on 15/2/2016.
 */
public class PrescriptionDTO {


    @JsonProperty("AppointMent_info")
    private AppointMent AppointMentInfo;
    /**
     * mediaclName : 好好保护
     * productName :
     * interval : 每天
     * numbers : [{"早":"0.1"},{"午":"0.2"},{"晚":"0.3"},{"睡前":"100.5"}]
     * unit : 克
     * remark :
     */
    @JsonProperty("drug")
    private List<Prescription> drug;


    public AppointMent getAppointMentInfo() {
        return AppointMentInfo;
    }

    public void setAppointMentInfo(AppointMent AppointMentInfo) {
        this.AppointMentInfo = AppointMentInfo;
    }

    public List<Prescription> getDrug() {
        return drug;
    }

    public void setDrug(List<Prescription> drug) {
        this.drug = drug;
    }
}
