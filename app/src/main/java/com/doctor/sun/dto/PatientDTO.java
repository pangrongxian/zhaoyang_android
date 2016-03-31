package com.doctor.sun.dto;

import com.doctor.sun.entity.Patient;
import com.doctor.sun.entity.RecentAppointment;

/**
 * Created by rick on 12/9/15.
 */
public class PatientDTO {




    /**
     * id : 25
     * doctor_name : 新医生
     * book_time : 2015-08-21 18:02－18:32
     * progress : 0/0
     * return_info : []
     */

    private RecentAppointment recent_AppointMent;

    public void setRecent_AppointMent(RecentAppointment recent_AppointMent) {
        this.recent_AppointMent = recent_AppointMent;
    }

    public RecentAppointment getRecent_AppointMent() {
        return recent_AppointMent;
    }

    private Patient info;

    public Patient getInfo() {
        return info;
    }

    public void setInfo(Patient info) {
        this.info = info;
    }

}
