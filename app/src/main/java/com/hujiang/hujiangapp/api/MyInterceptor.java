package com.hujiang.hujiangapp.api;

import com.blankj.utilcode.util.StringUtils;
import com.orhanobut.logger.Logger;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody requestBody = request.body();

        StringBuilder builder = new StringBuilder();
        if (requestBody instanceof FormBody) {
            FormBody formBody = (FormBody)requestBody;
            int count = formBody.size();
            for (int i = 0; i < count; ++i) {
                builder.append(formBody.encodedName(i));
                builder.append("=");
                builder.append(formBody.encodedValue(i));
                builder.append("; ");
            }
        }
        Logger.d("Request: %s\nMethod: %s\nParams: %s", request.url(), request.method(), builder.toString());

        Response response = chain.proceed(request);
        ResponseBody responseBody = response.body();
        String responseJson = responseBody.string();

        if (response.code() == 200) {
            String json = StringUtils.isEmpty(responseJson) ? "null" : responseJson;
            responseJson = String.format("{ \"status\": 200, \"error\": \"success\", \"errorCode\": \"0\", \"message\": \"success\", \"data\": %s}", json);
        }

        Logger.d("Response: %s\nStatus Code: %s\nJson: %s", request.url(), response.code(), responseJson);

        return response.newBuilder()
                .body(ResponseBody.create(responseBody.contentType(), responseJson))
                .build();
    }
}
