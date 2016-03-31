package com.doctor.sun.ui.activity.patient;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;

import com.doctor.sun.R;
import com.doctor.sun.bean.Constants;
import com.doctor.sun.databinding.ActivitydoctorInfoBinding;
import com.doctor.sun.dto.ApiDTO;
import com.doctor.sun.entity.AppointMent;
import com.doctor.sun.entity.Contact;
import com.doctor.sun.entity.ContactDetail;
import com.doctor.sun.entity.doctor;
import com.doctor.sun.http.Api;
import com.doctor.sun.http.callback.ApiCallback;
import com.doctor.sun.http.callback.SimpleCallback;
import com.doctor.sun.http.callback.TokenCallback;
import com.doctor.sun.module.ImModule;
import com.doctor.sun.module.ToolModule;
import com.doctor.sun.ui.activity.BaseActivity2;
import com.doctor.sun.ui.activity.doctor.ChattingRecordActivity;
import com.doctor.sun.ui.activity.doctor.HistoryRecordActivity;
import com.doctor.sun.ui.activity.doctor.ModifyNicknameActivity;
import com.doctor.sun.ui.model.HeaderViewModel;
import com.doctor.sun.ui.widget.CancelHistoryDialog;
import com.doctor.sun.ui.widget.SelectDialog;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.HashMap;

import io.ganguo.library.common.ToastHelper;
import io.ganguo.library.util.log.Logger;
import io.ganguo.library.util.log.LoggerFactory;
import retrofit.Call;

/**
 * 个人信息
 * <p/>
 * Created by Lynn on 12/29/15.
 */
public class doctorInfoActivity extends BaseActivity2 implements View.OnClickListener {
    private Logger logger = LoggerFactory.getLogger(doctorInfoActivity.class);
    private ActivitydoctorInfoBinding binding;
    private ImModule api = Api.of(ImModule.class);
    private Contact contact;
    private AppointMent AppointMent;

    public static Intent makeIntent(Context context, Contact data) {
        Intent i = new Intent(context, doctorInfoActivity.class);
        i.putExtra(Constants.DATA, data);
        return i;
    }


    private Contact getData() {
        return getIntent().getParcelableExtra(Constants.DATA);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_doctor_info);
        binding.setHeader(getHeaderViewModel());
        binding.blackList.setData("加入黑名单");
        binding.allowCall.setData("允许对方打电话");

        initListener();
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_check_history:
                startActivity(new Intent(doctorInfoActivity.this, ChattingRecordActivity.class)
                        .putExtra(Constants.PARAM_AppointMent, AppointMent));
                break;
            case R.id.tv_cancel_history:
                CancelHistoryDialog.showCancelHistoryDialog(doctorInfoActivity.this, binding.getData().getVoipAccount());
                break;
            case R.id.rl_history_record:
                startActivity(new Intent(doctorInfoActivity.this, HistoryRecordActivity.class)
                        .putExtra(Constants.PARAM_RECORD_ID, binding.getData().getRecordId()));
                break;
            case R.id.rl_modify_nickname:
                startActivityForResult(new Intent(doctorInfoActivity.this, ModifyNicknameActivity.class)
                        .putExtra(Constants.PARAM_NICKNAME, contact.getNickname())
                        .putExtra(Constants.PARAM_doctor_ID, contact.getdoctorId()), Constants.NICKNAME_REQUEST_CODE);
                break;
            case R.id.fl_send:
                if (AppointMent != null) {
                    SelectDialog.showSelectDialog(this, AppointMent);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.NICKNAME_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    binding.tvModify.setText(data.getStringExtra(Constants.PARAM_NICKNAME));
                    ToastHelper.showMessageMiddle(this, "修改成功");
                    break;
            }
        }
    }

    private void initListener() {
        binding.tvCheckHistory.setOnClickListener(this);
        binding.rlModifyNickname.setOnClickListener(this);
        binding.tvCancelHistory.setOnClickListener(this);
        binding.flSend.setOnClickListener(this);
        ((SwitchButton) binding.blackList.getRoot().findViewById(R.id.switch_button)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int checked;
                        if (isChecked) {
                            checked = 1;
                        } else {
                            checked = 0;
                        }
                        Call<ApiDTO<HashMap<String, String>>> call = api.doctorBan(contact.getdoctorId() + "", checked + "");
                        call.enqueue(new ApiCallback<HashMap<String, String>>() {
                            @Override
                            protected void handleResponse(HashMap<String, String> response) {
//                                ToastHelper.showMessage(getBaseContext(), "修改成功");
                            }
                        });
                    }
                });
        ((SwitchButton) binding.allowCall.getRoot().findViewById(R.id.switch_button)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int checked;
                if (isChecked) {
                    checked = 1;
                } else {
                    checked = 0;
                }
                Call<ApiDTO<HashMap<String, String>>> call = api.doctorCall(contact.getdoctorId() + "", checked + "");
                call.enqueue(new ApiCallback<HashMap<String, String>>() {
                    @Override
                    protected void handleResponse(HashMap<String, String> response) {
//                        ToastHelper.showMessage(getBaseContext(), "修改成功");
                    }
                });
            }
        });
    }

    private void initData() {
        contact = getData();
        getContactDetail();
    }

    private void getContactDetail() {
        api.doctorContact(contact.getdoctorId()).enqueue(new ApiCallback<ContactDetail>() {
            @Override
            protected void handleResponse(ContactDetail response) {
                binding.setData(response);
                bindAppointMent();
            }
        });
    }

    private void bindAppointMent() {
        ToolModule tool = Api.of(ToolModule.class);
        tool.doctorInfo(contact.getdoctorId()).enqueue(new SimpleCallback<doctor>() {
            @Override
            protected void handleResponse(doctor response) {
                AppointMent = new AppointMent();
                AppointMent.setVoipAccount(binding.getData().getVoipAccount());
                AppointMent.setPatientName(contact.getName());
                AppointMent.setAvatar(binding.getData().getAvatar());
                response.setVoipAccount(binding.getData().getVoipAccount());
                AppointMent.setdoctor(response);
            }
        });

    }

    @Nullable
    private HeaderViewModel getHeaderViewModel() {
        return new HeaderViewModel(this).setMidTitle("个人信息");
    }

}
