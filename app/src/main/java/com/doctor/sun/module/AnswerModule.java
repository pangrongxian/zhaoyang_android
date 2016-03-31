package com.doctor.sun.module;

import com.doctor.sun.dto.ApiDTO;
import com.doctor.sun.entity.Answer;
import com.doctor.sun.ui.handler.QCategoryHandler;

import java.util.List;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by rick on 11/24/15.
 * https://gitlab.cngump.com/ganguo_web/zhaoyangyisheng_server/wikis/question#%E8%8E%B7%E5%8F%96%E9%97%AE%E5%8D%B7
 * 用户问题模块API
 */
public interface AnswerModule {

    @GET("question/questionnaires/{AppointMentId}")
    Call<ApiDTO<List<Answer>>> answers(@Path("AppointMentId") int AppointMentId);

    @FormUrlEncoded
    @POST("question/questionnaires/{AppointMentId}")
    Call<ApiDTO<List<Answer>>> saveAnswers(
            @Path("AppointMentId") int AppointMentId,
            @Field("answer") String answer);


//    保存问卷答案
//
//    接口地址：http://域名/question/questionnaires/{预约单ID}

    @GET("question/questionnaires-scale/{AppointMentId}/")
    Call<ApiDTO<List<QCategoryHandler>>> category(@Path("AppointMentId") int AppointMentId);

    @GET("question/questionnaires-scale/{AppointMentId}/{categoryId}/show")
    Call<ApiDTO<List<Answer>>> categoryDetail(
            @Path("AppointMentId") int AppointMentId
            , @Path("categoryId") String categoryId);
}
