package com.doctor.sun.ui.adapter;

import android.content.Context;

/**
 * Created by lucas on 1/18/16.
 */
public class AssignQuestionAdapter extends SimpleAdapter {
    private String AppointMentId;

    public String getAppointMentId() {
        return AppointMentId;
    }

    public AssignQuestionAdapter(Context context,String AppointMentId) {
        super(context);
        this.AppointMentId = AppointMentId;
    }

    public interface GetAppointMentId{
        String getAppointMentId();
    }
}
