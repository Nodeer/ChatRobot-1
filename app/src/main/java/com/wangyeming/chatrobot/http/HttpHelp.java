package com.wangyeming.chatrobot.http;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.wangyeming.chatrobot.tuling.Tuling;

import java.io.IOException;

/**
 * @author Wang
 * @data 2015/3/14
 */
public class HttpHelp {

    private final static String API_URI = "http://www.tuling123.com/openapi/api";
    private final static String TULING_KEY = "fa3747334ed3f1a1520238ffa32ea415";

    private OkHttpClient client;
    private Tuling tuling;

    public HttpHelp(Tuling tuling) {
        client = new OkHttpClient();
        this.tuling = tuling;
    }

    public Response get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("text/json","charset=utf-8")
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }
}
