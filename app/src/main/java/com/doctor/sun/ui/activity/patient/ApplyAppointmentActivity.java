package com.doctor.sun.ui.activity.patient;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.doctor.sun.R;
import com.doctor.sun.bean.Constants;
import com.doctor.sun.databinding.PActivityApplyAppointMentBinding;
import com.doctor.sun.entity.AppointMent;
import com.doctor.sun.entity.MedicalRecord;
import com.doctor.sun.entity.doctor;
import com.doctor.sun.http.Api;
import com.doctor.sun.http.callback.ApiCallback;
import com.doctor.sun.module.AppointmentModule;
import com.doctor.sun.module.ProfileModule;
import com.doctor.sun.ui.activity.BaseActivity2;
import com.doctor.sun.ui.handler.AppointmentHandler;
import com.doctor.sun.ui.model.HeaderViewModel;
import com.doctor.sun.ui.widget.SelectRecordDialog;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 确认预约
 * <p/>
 * Created by lucas on 1/22/16.
 */
public class ApplyAppointMentActivity extends BaseActivity2 {
    private PActivityApplyAppointMentBinding binding;
    private ProfileModule api = Api.of(ProfileModule.class);
    private AppointmentModule AppointmentModule = Api.of(AppointmentModule.class);
    private MedicalRecord record;

    public static Intent makeIntent(Context context, doctor doctor, String bookTime, String type, String recordId) {
        Intent i = new Intent(context, ApplyAppointMentActivity.class);
        i.putExtra(Constants.doctor, doctor);
        i.putExtra(Constants.BOOKTIME, bookTime);
        i.putExtra(Constants.TYPE, type);
        i.putExtra(Constants.RECORDID, recordId);
        return i;
    }

    private doctor getdoctorData() {
        return getIntent().getParcelableExtra(Constants.doctor);
    }

    private String getBookTime() {
        return getIntent().getStringExtra(Constants.BOOKTIME);
    }

    private String getType() {
        return getIntent().getStringExtra(Constants.TYPE);
    }

    private String getRecordId() {
        return getIntent().getStringExtra(Constants.RECORDID);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.p_activity_apply_AppointMent);
        HeaderViewModel header = new HeaderViewModel(this);
        header.setMidTitle("确认预约");
        binding.setHeader(header);
        api.recordDetail(getRecordId()).enqueue(new ApiCallback<MedicalRecord>() {
            @Override
            protected void handleResponse(MedicalRecord response) {
                binding.tvMedcialRecord.setText(response.getRecordDetail());
                record = response;
            }
        });
        binding.setData(getdoctorData());
        binding.tvTime.setText("预约时间：" + getBookTime());
        binding.tvType.setText("预约类型：" + AppointmentType());
        binding.rbAlipay.setChecked(true);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parse = null;
        try {
            parse = format.parse(getBookTime());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        final String time = String.valueOf(parse.getTime()).substring(0, 10);
        binding.tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final doctor doctorData = getdoctorData();
                if (record != null) {
                    doctorData.setRecordId(String.valueOf(record.getMedicalRecordId()));
                }
                AppointmentModule.orderAppointMent(String.valueOf(doctorData.getId()), time, getType(), doctorData.getRecordId(), doctorData.getDuration()).enqueue(new ApiCallback<AppointMent>() {
                    @Override
                    protected void handleResponse(AppointMent response) {
                        response.setRecordId(Integer.parseInt(doctorData.getRecordId()));
                        AppointmentHandler handler = new AppointmentHandler(response);
                        if (binding.rbWechat.isChecked()) {
                            handler.payWithWeChat(ApplyAppointMentActivity.this);
                        } else {
                            handler.payWithAlipay(ApplyAppointMentActivity.this);
                        }
                    }
                });
            }
        });
        binding.llyMedicalRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SelectRecordDialog.showRecordDialog(ApplyAppointMentActivity.this, new SelectRecordDialog.SelectRecordListener() {
                    @Override
                    public void onSelectRecord(SelectRecordDialog dialog, MedicalRecord selected) {
                        binding.tvMedcialRecord.setText(selected.getRecordDetail());

                        record = selected;
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    @NonNull
    private String AppointmentType() {
        String type = "";
        switch (getType()) {
            case "1":
                type = "详细咨询";
                break;
            case "2":
                type = "简捷复诊";
                break;
        }
        return type;
    }
}
