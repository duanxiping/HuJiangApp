package com.hujiang.hujiangapp.api;

import com.hujiang.hujiangapp.model.ApiResp;
import com.hujiang.hujiangapp.model.AttendResult;
import com.hujiang.hujiangapp.model.BuildCompany;
import com.hujiang.hujiangapp.model.CardOcrInfo;
import com.hujiang.hujiangapp.model.Dict;
import com.hujiang.hujiangapp.model.Empty;
import com.hujiang.hujiangapp.model.FaceLog;
import com.hujiang.hujiangapp.model.FaceLogNumber;
import com.hujiang.hujiangapp.model.FaceResult;
import com.hujiang.hujiangapp.model.Hire;
import com.hujiang.hujiangapp.model.ImageResource;
import com.hujiang.hujiangapp.model.Page;
import com.hujiang.hujiangapp.model.Project;
import com.hujiang.hujiangapp.model.Team;
import com.hujiang.hujiangapp.model.User;
import com.hujiang.hujiangapp.model.WorkType;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @FormUrlEncoded
    @POST("users/signIn")
    Call<ApiResp<User>> signIn(@Field(value = "userName") String userName,
                              @Field(value = "password") String password);

    @FormUrlEncoded
    @POST("users/modifyPassword")
    Call<ApiResp<Empty>> modifyPassword(@Field(value = "userId") long userId,
                                        @Field(value = "oldPassword") String oldPassword,
                                        @Field(value = "newPassword") String newPassword);

    @Multipart
    @POST("admin/files/uploadImage")
    Call<ApiResp<ImageResource>> uploadImage(@Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("enrolls/getAliOcrIdCard")
    Call<ApiResp<CardOcrInfo>> ocrIdCard(@Field(value = "url") String url,
                                @Field(value = "configStr") String configStr);

    @FormUrlEncoded
    @POST("enrolls/getAliOcrBankCard")
    Call<ApiResp<CardOcrInfo>> ocrBankCard(@Field(value = "url") String url);

    @FormUrlEncoded
    @POST("enrolls/faceVerify")
    Call<ApiResp<FaceResult>> faceVerify(@Field(value = "imageUrl1") String imageUrl1,
                                @Field(value = "imageUrl2") String imageUrl2);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("hires/save")
    Call<ApiResp<Hire>> saveHire(@Body Hire hire);

    @FormUrlEncoded
    @POST("projects/query")
    Call<ApiResp<List<Project>>> queryProjects(@Field(value = "userId") long userId);

    @POST("projects/query")
    Call<ApiResp<List<Project>>> queryAllProjects();

    @FormUrlEncoded
    @POST("buildCompanys/query")
    Call<ApiResp<List<BuildCompany>>> queryBuildCompany(@Field(value = "projectId") long projectId);

    @FormUrlEncoded
    @POST("teams/query")
    Call<ApiResp<List<Team>>> queryTeam(@Field(value = "projectId") long projectId);

    @POST("workTypes/listAll")
    Call<ApiResp<List<WorkType>>> queryWorkType();

    @POST("workTypes/findByHot")
    Call<ApiResp<List<WorkType>>> queryHotWorkType();

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("faceLogs/appFaceLog")
    Call<ApiResp<AttendResult>> faceAttend(@Body FaceLog faceLog);

    @FormUrlEncoded
    @POST("hires/queryHires")
    Call<ApiResp<Page<Hire>>> queryHires(@Field(value = "userId") long userId,
                                         @Field(value = "projectId") long projectId,
                                         @Field(value = "page") int page,
                                         @Field(value = "size") int size);

    @POST("hires/synchronizationHire")
    Call<ApiResp<List<Empty>>> synchronizationHire();

    @FormUrlEncoded
    @POST("faceLogs/queryTodayFaceLog")
    Call<ApiResp<Page<FaceLog>>> queryTodayFaceLogs(@Field(value = "userId") long userId,
                                                    @Field(value = "page") int page,
                                                    @Field(value = "size") int size);

    @POST("faceLogs/queryFaceLogNumber")
    Call<ApiResp<FaceLogNumber>> queryFaceLogNumber();

    @FormUrlEncoded
    @POST("dicts/getDictsByCategory")
    Call<ApiResp<List<Dict>>> queryDict(@Field(value = "categoryKey") String categoryKey);
}
