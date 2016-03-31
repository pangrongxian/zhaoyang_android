package com.doctor.sun.module;

import com.doctor.sun.dto.ApiDTO;
import com.doctor.sun.dto.PageDTO;
import com.doctor.sun.entity.Comment;
import com.doctor.sun.entity.doctor;
import com.doctor.sun.entity.Hospital;
import com.doctor.sun.entity.Photo;
import com.squareup.okhttp.RequestBody;

import java.util.List;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by rick on 11/18/15.
 * 基础API
 * https://gitlab.cngump.com/ganguo_web/zhaoyangyisheng_server/wikis/tool
 */
public interface ToolModule {

    @Multipart
    @POST("tool/upload")
    Call<ApiDTO<Photo>> uploadPhoto(@Part("photo\"; filename=\"photo\" ") RequestBody file);

    @GET("tool/recommenddoctors")
    Call<ApiDTO<List<doctor>>> recommenddoctor();

    @GET("tool/doctorInfo/{doctorId}")
    Call<ApiDTO<doctor>> doctorInfo(@Path("doctorId") int doctorId);

    @GET("tool/hospital/{hospitalId}")
    Call<ApiDTO<Hospital>> hospitalInfo(@Path("hospitalId") int hospitalId);

    @FormUrlEncoded
    @POST("AppointMent/collect")
    Call<ApiDTO<String>> likedoctor(@Field("doctorId") int doctorId);

    @FormUrlEncoded
    @POST("AppointMent/un-collect")
    Call<ApiDTO<String>> unlikedoctor(@Field("doctorId") int doctorId);

    @GET("profile/comments")
    Call<ApiDTO<PageDTO<Comment>>> comments(@Query("doctorId") int doctorId, @Query("page") String page);
}
