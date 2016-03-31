package com.doctor.sun.ui.activity.doctor;

import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.doctor.sun.R;
import com.doctor.sun.bean.Constants;
import com.doctor.sun.databinding.ActivityMeBinding;
import com.doctor.sun.entity.doctor;
import com.doctor.sun.http.Api;
import com.doctor.sun.http.callback.ApiCallback;
import com.doctor.sun.http.callback.TokenCallback;
import com.doctor.sun.module.ProfileModule;
import com.doctor.sun.ui.handler.MeHandler;
import com.doctor.sun.ui.model.FooterViewModel;
import com.doctor.sun.ui.model.HeaderViewModel;
import com.doctor.sun.util.JacksonUtils;

import io.ganguo.library.Config;


/**
 * Created by rick on 10/23/15.
 * 22我的收藏
 */
public class MeActivity extends BaseDoctorActivity {

    private ActivityMeBinding binding;
    private ProfileModule api = Api.of(ProfileModule.class);

    public static Intent makeIntent(Context context) {
        Intent i = new Intent(context, MeActivity.class);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_me);
        binding.setFooter(FooterViewModel.getInstance(this, realm, R.id.tab_three));
        HeaderViewModel header = new HeaderViewModel(this);
        header.setMidTitle("我");
        binding.setHeader(header);
        doctor doctor = TokenCallback.getdoctorProfile();
        binding.setData(doctor);
        binding.setHandler(new MeHandler(doctor));
        if (doctor != null) {
            binding.tvTest.setText(doctor.getLevel());
        } else {
            binding.tvTest.setVisibility(View.GONE);
        }

        api.doctorProfile().enqueue(new ApiCallback<doctor>() {
            @Override
            protected void handleResponse(doctor response) {
                doctor data = response;
                Config.putString(Constants.doctor_PROFILE, JacksonUtils.toJson(data));
                binding.setData(data);
            }
        });
    }
}
