package com.doctor.sun.ui.handler;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.doctor.sun.R;
import com.doctor.sun.entity.Token;
import com.doctor.sun.http.Api;
import com.doctor.sun.http.callback.ApiCallback;
import com.doctor.sun.http.callback.DoNothingCallback;
import com.doctor.sun.http.callback.TokenCallback;
import com.doctor.sun.module.AuthModule;
import com.doctor.sun.ui.activity.doctor.EditDoctorInfoActivity;
import com.doctor.sun.ui.widget.AddMedicalRecordDialog;

import io.ganguo.library.util.Strings;

/**
 * Created by rick on 11/17/15.
 */
public class RegisterHandler extends BaseHandler {
    private static final int ONE_SECOND = 1000;
    private AuthModule api = Api.of(AuthModule.class);
    private RegisterInput mInput;
    private Handler handler;
    private int remainTime;

    private int registerType;

    private static final long DOUBLE_PRESS_INTERVAL = 600;
    private long lastPressTime = 0;

    public RegisterHandler(Activity context, int registerType) {
        super(context);
        this.registerType = registerType;
        handler = new Handler(Looper.myLooper());

        try {
            mInput = (RegisterInput) context;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("The host Activity must implement RegisterInput");
        }
    }

    public void sendCaptcha(View view) {
        String phone = mInput.getPhone();
        if (!Strings.isMobile(phone)) {
            Toast.makeText(getContext(), "手机号码格式错误", Toast.LENGTH_SHORT).show();
            return;
        }


        long pressTime = System.currentTimeMillis();
        if (pressTime - lastPressTime >= DOUBLE_PRESS_INTERVAL) {
            countDown(view);
            api.sendCaptcha(phone).enqueue(new DoNothingCallback());
        }
        lastPressTime = pressTime;
    }

    public void register(View view) {
        String phone = mInput.getPhone();
        if (!Strings.isMobile(phone)) {
            Toast.makeText(getContext(), "手机号码格式错误", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Strings.isEquals(mInput.getPassword(), mInput.getPassword2())) {
            Toast.makeText(getContext(), "密码不匹配", Toast.LENGTH_SHORT).show();
            return;
        }

        api.register(
                registerType, phone,
                mInput.getCaptcha(), mInput.getPassword()).enqueue(new ApiCallback<Token>() {
            @Override
            protected void handleResponse(Token response) {
                if (registerType == AuthModule.doctor_TYPE) {
                    registerdoctorSuccess(response);
                } else if (registerType == AuthModule.PATIENT_TYPE) {
                    registerPatientSuccess(response);

                }
            }
        });
    }

    private void registerPatientSuccess(Token response) {
        if (response != null) {
            TokenCallback.handleToken(response);
            new AddMedicalRecordDialog(getContext()).show();
        }
    }

    private void registerdoctorSuccess(Token response) {
        if (response == null) {
            //TODO
            Intent i = EditDoctorInfoActivity.makeIntent(getContext(), null);
            getContext().startActivity(i);
        } else {
            TokenCallback.handleToken(response);
            Intent i = EditDoctorInfoActivity.makeIntent(getContext(), null);
            getContext().startActivity(i);
        }
    }


    private void countDown(View view) {
        remainTime = 60;
        final ViewGroup parent = (ViewGroup) view;
        final TextView textView = (TextView) parent.getChildAt(0);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                remainTime -= 1;
                if (remainTime < 0) {
                    textView.setEnabled(true);
                    parent.setEnabled(true);
                    textView.setText("重新获取");
                    handler.removeCallbacks(this);
                } else {
                    textView.setText(getContext().getResources().getString(R.string.get_captcha, remainTime));
                    textView.setEnabled(false);
                    parent.setEnabled(false);
                    handler.postDelayed(this, ONE_SECOND);
                }
            }
        };
        handler.postDelayed(runnable, ONE_SECOND);
    }

    public void resetPassword(View view) {
        String phone = mInput.getPhone();
        if (!Strings.isMobile(phone)) {
            Toast.makeText(getContext(), "手机号码格式错误", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Strings.isEquals(mInput.getPassword(), mInput.getPassword2())) {
            Toast.makeText(getContext(), "两次输入的密码不相同", Toast.LENGTH_SHORT).show();
            return;
        }
        api.reset(mInput.getEmail(), phone, mInput.getPassword(), mInput.getCaptcha()).enqueue(new DoNothingCallback());
    }

    public interface RegisterInput {
        String getEmail();

        String getPhone();

        String getCaptcha();

        String getPassword();

        String getPassword2();
    }

}
