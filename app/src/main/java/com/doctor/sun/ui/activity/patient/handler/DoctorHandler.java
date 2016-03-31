package com.doctor.sun.ui.activity.patient.handler;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.doctor.sun.bean.AppointmentType;
import com.doctor.sun.dto.DoctorDTO;
import com.doctor.sun.entity.doctor;
import com.doctor.sun.ui.activity.patient.doctorDetailActivity;
import com.doctor.sun.ui.adapter.ViewHolder.BaseViewHolder;
import com.doctor.sun.ui.adapter.core.BaseAdapter;
import com.doctor.sun.ui.adapter.core.OnItemClickListener;

/**
 * Created by lucas on 1/8/16.
 */
public class doctorHandler implements Parcelable {
    private doctor data;
    private boolean isSelected;

    public doctorHandler(doctor DoctorDTO) {
        data = DoctorDTO;
    }

    public OnItemClickListener select() {
        return new OnItemClickListener() {
            @Override
            public void onItemClick(BaseAdapter adapter, View view, BaseViewHolder vh) {
                setSelected(!isSelected());
                view.setSelected(isSelected());
            }
        };
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private boolean isSelected() {
        return isSelected;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.data, 0);
    }

    protected doctorHandler(Parcel in) {
        this.data = in.readParcelable(DoctorDTO.class.getClassLoader());
    }

    public static final Creator<doctorHandler> CREATOR = new Creator<doctorHandler>() {
        public doctorHandler createFromParcel(Parcel source) {
            return new doctorHandler(source);
        }

        public doctorHandler[] newArray(int size) {
            return new doctorHandler[size];
        }
    };

    public void detail(View view) {
        Intent intent = doctorDetailActivity.makeIntent(view.getContext(), data, AppointmentType.DETAIL);
        view.getContext().startActivity(intent);
    }
}
