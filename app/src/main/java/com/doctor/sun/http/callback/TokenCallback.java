package com.doctor.sun.http.callback;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.doctor.sun.bean.Constants;
import com.doctor.sun.dto.PatientDTO;
import com.doctor.sun.entity.doctor;
import com.doctor.sun.entity.Patient;
import com.doctor.sun.entity.RecentAppointment;
import com.doctor.sun.entity.Token;
import com.doctor.sun.http.Api;
import com.doctor.sun.im.Messenger;
import com.doctor.sun.module.AuthModule;
import com.doctor.sun.module.ProfileModule;
import com.doctor.sun.ui.activity.doctor.EditDoctorInfoActivity;
import com.doctor.sun.ui.activity.doctor.MainActivity;
import com.doctor.sun.ui.activity.doctor.RegisterActivity;
import com.doctor.sun.ui.activity.doctor.ReviewResultActivity;
import com.doctor.sun.util.JacksonUtils;

import io.ganguo.library.Config;
import io.ganguo.library.common.LoadingHelper;

/**
 * Created by rick on 11/18/15.
 */
public class TokenCallback {
    public static final String TAG = TokenCallback.class.getSimpleName();
    public static final int ISFIRSTTIME = 1;
    public static final int NOTFIRSTTIME = 2;

    public static void checkToken(final Activity context) {
        if (Config.getString(Constants.TOKEN) != null) {
            switch (Config.getInt(Constants.USER_TYPE, -1)) {
                case AuthModule.doctor_TYPE: {

                    loaddoctorProfile(context);

                    break;
                }
                case AuthModule.doctor_PASSED: {

                    Intent i = com.doctor.sun.ui.activity.doctor.MainActivity.makeIntent(context);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        context.finishAffinity();
                    } else {
                        context.finish();
                    }
                    context.startActivity(i);

                    break;
                }
                case AuthModule.PATIENT_TYPE: {

                    Patient patientProfile = getPatientProfile();
                    if (patientProfile == null) {
                        loadPatientProfile(context);
                    } else {
                        Intent i = com.doctor.sun.ui.activity.patient.MainActivity.makeIntent(context);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            context.finishAffinity();
                        } else {
                            context.finish();
                        }
                        context.startActivity(i);
                        context.finish();
                    }
                    break;
                }
                default: {

                    break;
                }

            }
        }
    }

    private static void loaddoctorProfile(final Activity context) {
        ProfileModule profileModule = Api.of(ProfileModule.class);
        LoadingHelper.showMaterLoading(context, "正在加载个人信息");
        profileModule.doctorProfile().enqueue(new ApiCallback<doctor>() {
            @Override
            protected void handleResponse(doctor response) {
                LoadingHelper.hideMaterLoading();
                doctor data = response;
                Log.e(TAG, "handleResponse: " + data);
                Config.putString(Constants.doctor_PROFILE, JacksonUtils.toJson(data));
                if (data == null) {
                    Intent i = RegisterActivity.makeIntent(context, AuthModule.doctor_TYPE);
                    context.startActivity(i);
                    context.finish();
                } else switch (data.getStatus()) {
                    case doctor.STATUS_PASS: {
//                        Log.e(TAG, "firstTime: " + Config.getInt(Constants.PASSFIRSTTIME, -1));
                        Config.putInt(Constants.USER_TYPE, AuthModule.doctor_PASSED);
                        Intent i = MainActivity.makeIntent(context);
                        context.startActivity(i);
                        context.finish();
                        break;
                    }
                    case doctor.STATUS_PENDING: {
                        Intent i = ReviewResultActivity.makeIntent(context, data);
                        Config.putInt(Constants.PASSFIRSTTIME, ISFIRSTTIME);
                        context.startActivity(i);
                        context.finish();
                        break;
                    }
                    case doctor.STATUS_REJECT: {
                        Intent i = ReviewResultActivity.makeIntent(context, data);
                        Config.putInt(Constants.PASSFIRSTTIME, ISFIRSTTIME);
                        context.startActivity(i);
                        context.finish();
                        break;
                    }
                    default: {
                        Config.putInt(Constants.USER_TYPE, AuthModule.doctor_PASSED);
                        Intent i = RegisterActivity.makeIntent(context, AuthModule.doctor_TYPE);
                        context.startActivity(i);
                        context.finish();
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                LoadingHelper.hideMaterLoading();
                Toast.makeText(context, "医生未填写个人资料", Toast.LENGTH_SHORT).show();
                Intent intent = EditDoctorInfoActivity.makeIntent(context, null);
                context.startActivity(intent);
            }
        });
    }

    public static void loadPatientProfile(final Activity context) {
        ProfileModule profileModule = Api.of(ProfileModule.class);
        LoadingHelper.showMaterLoading(context, "正在加载个人信息");
        profileModule.patientProfile().enqueue(new ApiCallback<PatientDTO>() {
            @Override
            protected void handleResponse(PatientDTO response) {
                LoadingHelper.hideMaterLoading();
                PatientDTO data = response;
                Log.e(TAG, "handleResponse: " + data.toString());
                String value = null;
                try {
                    value = JacksonUtils.toJson(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Config.putString(Constants.PATIENT_PROFILE, value);
                if (data == null) {
                    Intent i = RegisterActivity.makeIntent(context, AuthModule.PATIENT_TYPE);
                    context.startActivity(i);
                    context.finish();
                } else {
                    Intent i = com.doctor.sun.ui.activity.patient.MainActivity.makeIntent(context);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        context.finishAffinity();
                    } else {
                        context.finish();
                    }
                    context.startActivity(i);
                    context.finish();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                t.printStackTrace();
                LoadingHelper.hideMaterLoading();
            }
        });
    }

    public static void handleToken(Token token) {
        Config.putString(Constants.TOKEN, token.getToken());
        Config.putInt(Constants.USER_TYPE, token.getType());
        Config.putString(Constants.VOIP_ACCOUNT, JacksonUtils.toJson(token.getAccount()));
        Messenger.getInstance().login(token.getAccount());
    }

    public static String getToken() {
        return Config.getString(Constants.TOKEN);
    }

    public static Patient getPatientProfile() {
        String json = Config.getString(Constants.PATIENT_PROFILE);
        if (json == null) {
            return null;
        }
        PatientDTO patient = JacksonUtils.fromJson(json, PatientDTO.class);
        return patient.getInfo();
    }

    public static RecentAppointment getRecentAppointment() {
        String json = Config.getString(Constants.PATIENT_PROFILE);
        if (json == null) {
            return null;
        }
        PatientDTO patient = JacksonUtils.fromJson(json, PatientDTO.class);
        return patient.getRecent_AppointMent();
    }

    public static doctor getdoctorProfile() {
        String json = Config.getString(Constants.doctor_PROFILE);
        if (json == null) {
            return new doctor();
        }
        doctor doctor = JacksonUtils.fromJson(json, doctor.class);
        return doctor;
    }

}
