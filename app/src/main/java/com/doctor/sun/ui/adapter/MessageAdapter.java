package com.doctor.sun.ui.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;

import com.doctor.sun.R;
import com.doctor.sun.bean.Constants;
import com.doctor.sun.databinding.ItemPrescription2Binding;
import com.doctor.sun.databinding.ItemPrescriptionListBinding;
import com.doctor.sun.dto.PatientDTO;
import com.doctor.sun.dto.PrescriptionDTO;
import com.doctor.sun.entity.AppointMent;
import com.doctor.sun.entity.Avatar;
import com.doctor.sun.entity.doctor;
import com.doctor.sun.entity.Prescription;
import com.doctor.sun.entity.im.TextMsg;
import com.doctor.sun.http.Api;
import com.doctor.sun.http.callback.SimpleCallback;
import com.doctor.sun.http.callback.TokenCallback;
import com.doctor.sun.module.AuthModule;
import com.doctor.sun.module.ImModule;
import com.doctor.sun.ui.activity.patient.MedicineHelperActivity;
import com.doctor.sun.ui.adapter.ViewHolder.BaseViewHolder;
import com.doctor.sun.util.JacksonUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import io.ganguo.library.Config;

/**
 * Created by rick on 12/15/15.
 */
public class MessageAdapter extends SimpleAdapter<TextMsg, ViewDataBinding> {
    private String myAvatar;
    private String yourAvatar;
    private boolean shouldUpdate;

    public MessageAdapter(Context context, AppointMent data) {
        super(context);
        switch (Config.getInt(Constants.USER_TYPE, -1)) {
            case AuthModule.PATIENT_TYPE: {
                doctor doctor = data.getdoctor();
                String json = Config.getString(Constants.PATIENT_PROFILE);
                PatientDTO dto = JacksonUtils.fromJson(json, PatientDTO.class);
                yourAvatar = doctor.getAvatar();
                if (dto != null) {
                    myAvatar = dto.getInfo().getAvatar();
                } else {
                    myAvatar = "";
                }
                break;
            }
            default: {
                doctor doctor = TokenCallback.getdoctorProfile();
                yourAvatar = data.getAvatar();
                if (doctor != null) {
                    myAvatar = doctor.getAvatar();
                } else {
                    myAvatar = "";
                }
            }
        }
    }

    public MessageAdapter(MedicineHelperActivity context, String my, String your) {
        super(context);
        ImModule api = Api.of(ImModule.class);
        api.avatar(my, "").enqueue(new SimpleCallback<Avatar>() {
            @Override
            protected void handleResponse(Avatar response) {
                myAvatar = response.getAvatar();
                if (shouldUpdate) {
                    notifyDataSetChanged();
                } else {
                    shouldUpdate = true;
                }
            }
        });
        api.avatar(your, "").enqueue(new SimpleCallback<Avatar>() {
            @Override
            protected void handleResponse(Avatar response) {
                yourAvatar = response.getAvatar();
                if (shouldUpdate) {
                    notifyDataSetChanged();
                } else {
                    shouldUpdate = true;
                }

            }
        });
    }

    public String getMyAvatar() {
        return myAvatar;
    }

    public String getYourAvatar() {
        return yourAvatar;
    }

    @Override
    public void onBindViewBinding(BaseViewHolder<ViewDataBinding> vh, int position) {
        super.onBindViewBinding(vh, position);
        switch (vh.getItemViewType()) {
            case R.layout.item_prescription_list: {
                ItemPrescriptionListBinding binding = (ItemPrescriptionListBinding) vh.getBinding();
                binding.prescription.removeAllViews();
                TextMsg textMsg = get(position);
                PrescriptionDTO prescriptionDTO = JacksonUtils.fromJson(textMsg.getBody(), PrescriptionDTO.class);
                if (prescriptionDTO == null || prescriptionDTO.getDrug() == null) return;
                AppointMent AppointMent = prescriptionDTO.getAppointMentInfo();
                binding.name.setText(String.format("%s  %s", AppointMent.getRecordName(), AppointMent.getRelation()));
                binding.time.setText(String.format("%s  %s", AppointMent.getBookTime(), AppointMent.getType()));

                for (Prescription prescription : prescriptionDTO.getDrug()) {
                    ItemPrescription2Binding item = ItemPrescription2Binding.inflate(getInflater(), binding.prescription, true);
                    item.setData(prescription);
                }
            }
        }
    }

    public String selectedTime(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(time);
        String format = simpleDateFormat.format(calendar.getTime());
        return "用户于" + format + "选择用药";
    }
}
