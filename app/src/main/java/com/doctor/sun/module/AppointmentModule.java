package com.doctor.sun.module;

import com.doctor.sun.dto.ApiDTO;
import com.doctor.sun.dto.DoctorDTO;
import com.doctor.sun.dto.DoctorPageDTO;
import com.doctor.sun.dto.PageDTO;
import com.doctor.sun.dto.WechatPayDTO;
import com.doctor.sun.entity.AppointMent;
import com.doctor.sun.entity.doctor;
import com.doctor.sun.entity.EmergencyCall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by rick on 11/20/15.
 */
public interface AppointmentModule {

    /**
     * https://gitlab.cngump.com/ganguo_web/zhaoyangyisheng_server/wikis/AppointMent#%E8%8E%B7%E5%8F%96%E6%89%80%E6%9C%89%E5%8C%BB%E7%94%9F%E5%88%97%E8%A1%A8
     *
     * @param query
     * @param titleParam
     * @return
     */
    @GET("AppointMent/alldoctor/")
    Call<ApiDTO<DoctorPageDTO<doctor>>> doctors(@Query("page") String page, @QueryMap HashMap<String, String> query, @Query("title[]") ArrayList<Integer> titleParam);

    @GET("AppointMent/recent-doctors")
    Call<ApiDTO<List<doctor>>> recentdoctors(@Query("page") String page, @QueryMap HashMap<String, String> query, @Query("title[]") ArrayList<Integer> titleParam);

    @FormUrlEncoded
    @POST("AppointMent/collect")
    Call<ApiDTO<String>> likedoctor(@Field("doctorId") String doctorId);

    @FormUrlEncoded
    @POST("AppointMent/un-collect")
    Call<ApiDTO<String>> unlikedoctor(@Field("doctorId") String doctorId);

    @GET("AppointMent/collectionList")
    Call<ApiDTO<PageDTO<doctor>>> favoritedoctors();

    @FormUrlEncoded
    @POST("AppointMent/AppointMent")
    Call<ApiDTO<AppointMent>> orderAppointMent(@Field("doctorId") String doctorId, @Field("bookTime") String bookTime, @Field("type") String type, @Field("recordId") String recordId);

    @FormUrlEncoded
    @POST("AppointMent/AppointMent")
    Call<ApiDTO<AppointMent>> orderAppointMent(@Field("doctorId") String doctorId, @Field("bookTime") String bookTime, @Field("type") String type, @Field("recordId") String recordId, @Field("takeTime") String taketime);

    @GET("AppointMent/pAppointList")
    Call<ApiDTO<PageDTO<AppointMent>>> pAppointMents(@Query("page") String page);

    @GET("AppointMent/pAppointList")
    Call<ApiDTO<PageDTO<AppointMent>>> pAppointMents(@Query("page") String page, @Query("type") int type);

    @FormUrlEncoded
    @POST("AppointMent/patient-cancel")
    Call<ApiDTO<String>> pCancel(@Field("AppointMentId") String AppointMentId);

    @GET("AppointMent/dAppointList")
    Call<ApiDTO<PageDTO<AppointMent>>> dAppointMents(@Query("page") String page, @Query("paid") String paid);

    @FormUrlEncoded
    @POST("AppointMent/doctor-cancel")
    Call<ApiDTO<String>> dCancel(@Field("AppointMentId") String AppointMentId, @Field("reason") String reason);

    @FormUrlEncoded
    @POST("AppointMent/remind-answer")
    Call<ApiDTO<String>> remind(@Field("AppointMentId") String AppointMentId, @Field("patientId") String patientId);


    @FormUrlEncoded
    @POST("AppointMent/doing")
    Call<ApiDTO<String>> startConsulting(@Field("AppointMentId") int AppointMentId);


    @GET("diagnosis/return-list")
    Call<ApiDTO<PageDTO<AppointMent>>> consultations(@Query("page") String page);

    @FormUrlEncoded
    @POST("diagnosis/accept-return")
    Call<ApiDTO<String>> acceptConsultation(@FieldMap Map<String, String> consultation);

    /**
     * @param id 会诊列表里的会诊ID returnListId
     * @return
     */
    @FormUrlEncoded
    @POST("diagnosis/refuse-return")
    Call<ApiDTO<String>> refuseConsultation(@Field("id") String id);

    @GET("AppointMent/dDoingList")
    Call<ApiDTO<PageDTO<AppointMent>>> dDoingList(@Query("page") String page);

    @GET("AppointMent/dFinishList")
    Call<ApiDTO<PageDTO<AppointMent>>> dFinishList(@Query("page") String page);

    @GET("AppointMent/pDoingList")
    Call<ApiDTO<PageDTO<AppointMent>>> pDoingList(@Query("page") String page);

    @GET("AppointMent/pFinishList")
    Call<ApiDTO<PageDTO<AppointMent>>> pFinishList(@Query("page") String page);

    @FormUrlEncoded
    @POST("AppointMent/evaluate-patient")
    Call<ApiDTO<String>> evaluatePatient(@Field("point") String point, @Field("AppointMentId") String AppointMentId, @Field("detail") String detail);

    @GET("urgent/doctor-list")
    Call<ApiDTO<PageDTO<AppointMent>>> urgentCalls(@Query("page") String page);

    @GET("urgent/patient-list")
    Call<ApiDTO<PageDTO<EmergencyCall>>> pUrgentCalls(@Query("page") String page);

    @FormUrlEncoded
    @POST("urgent/receive")
    Call<ApiDTO<String>> acceptUrgentCall(@Field("ucId") int ucId);

    @GET("AppointMent/record-histories")
    Call<ApiDTO<PageDTO<AppointMent>>> Patient(@Query("recordId") String recordId);

    @FormUrlEncoded
    @POST("AppointMent/evaluate-patient")
    Call<ApiDTO<String>> evaluatePatient(@Field("point") String point
            , @Field("AppointMentId") int AppointMentId
            , @Field("detail") String detail
            , @Field("content") String content);

    @FormUrlEncoded
    @POST("AppointMent/evaluate-doctor")
    Call<ApiDTO<String>> evaluatedoctor(@Field("point") String point
            , @Field("AppointMentId") int AppointMentId
            , @Field("detail") String detail
            , @Field("content") String content);

    @FormUrlEncoded
    @POST("AppointMent/pay")
    Call<ApiDTO<String>> pay(@Field("AppointMentId") String AppointMentId);


    /**
     * @param type 'alipay'或'wechat'(微信app支付)或'wechat_js'(微信公众号支付)。不传默认值为'alipay'
     * @return
     */
    @FormUrlEncoded
    @POST("pay/info")
    Call<ApiDTO<String>> buildOrder(@Field("AppointMentId") int id,
                                    @Field("type") String type);

    /**
     * @param type 'alipay'或'wechat'(微信app支付)或'wechat_js'(微信公众号支付)。不传默认值为'alipay'
     * @return
     */
    @FormUrlEncoded
    @POST("pay/info")
    Call<ApiDTO<WechatPayDTO>> buildWeChatOrder(@Field("AppointMentId") int id,
                                                @Field("type") String type);

    /**
     * @param type 'alipay'或'wechat'(微信app支付)或'wechat_js'(微信公众号支付)。不传默认值为'alipay'
     * @return
     */
    @FormUrlEncoded
    @POST("pay/build-order")
    Call<ApiDTO<String>> rechargeOrderWithAlipay(@Field("totalFee") int totalFee, @Field("body") String body,
                                                 @Field("type") String type);

    @FormUrlEncoded
    @POST("pay/build-order")
    Call<ApiDTO<WechatPayDTO>> rechargeOrderWithWechat(@Field("totalFee") int totalFee, @Field("body") String body,
                                                       @Field("type") String type);

    /**
     * @param type 'alipay'或'wechat'(微信app支付)或'wechat_js'(微信公众号支付)。不传默认值为'alipay'
     * @return
     */
    @FormUrlEncoded
    @POST("pay/build-order")
    Call<ApiDTO<String>> drugOrder(@Field("totalFee") int totalFee, @Field("body") String body,
                                   @Field("type") String type, @Field("drugOrderId") int drugOrderId);

    @FormUrlEncoded
    @POST("pay/build-order")
    Call<ApiDTO<WechatPayDTO>> drugOrderWithWechat(@Field("totalFee") int totalFee, @Field("body") String body,
                                                   @Field("type") String type, @Field("drugOrderId") int drugOrderId);
}
