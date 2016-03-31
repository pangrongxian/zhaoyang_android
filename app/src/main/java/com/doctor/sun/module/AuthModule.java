package com.doctor.sun.module;

import com.doctor.sun.dto.ApiDTO;
import com.doctor.sun.entity.Token;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by rick on 11/17/15.
 * https://gitlab.cngump.com/ganguo_web/zhaoyangyisheng_server/wikis/auth
 * 登录注册模块API
 */
public interface AuthModule {
    int PATIENT_TYPE = 1;
    int doctor_TYPE = 2;
    int doctor_PASSED = 4;
    int FORGOT_PASSWORD = 16;

    @FormUrlEncoded
    @POST("auth/register")
    Call<ApiDTO<Token>> register(@Field("type") int type, @Field("phone") String phone,
                                 @Field("captcha") String captcha, @Field("password") String password);

    @FormUrlEncoded
    @POST("auth/sendCaptcha")
    Call<ApiDTO<String>> sendCaptcha(@Field("phone") String phone);


    @FormUrlEncoded
    @POST("auth/login")
    Call<ApiDTO<Token>> login(@Field("phone") String phone, @Field("password") String password);

    @FormUrlEncoded
    @POST("auth/reset")
    Call<ApiDTO<String>> reset(@Field("email") String email, @Field("phone") String phone,
                               @Field("password") String password, @Field("captcha") String captcha);

    @FormUrlEncoded
    @POST("auth/logout")
    Call<ApiDTO<String>> logout(@Field("token") String token);

}
