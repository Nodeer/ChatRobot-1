package com.wangyeming.chatrobot;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.fastjson.JSON;
import com.wangyeming.chatrobot.http.HttpHelp;
import com.wangyeming.chatrobot.tuling.Tuling;
import com.wangyeming.chatrobot.tuling.json.TulingJson;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {

            @Override
            public void run() {
                String response = getResponse();
                parse(response);
            }
        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getResponse() {
        Tuling tuling = new Tuling();
        tuling.setInfo("南京今天天气");
        tuling.setUserId("123456");
        String uri = tuling.generateUri();
        HttpHelp httpHelp = new HttpHelp();
        String response = "";
        try {
            response = httpHelp.get(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public void parse(String response) {
        TulingJson tulingJson = JSON.parseObject(response, TulingJson.class);
        String code = tulingJson.code;
        Log.d("wym", "code " + code);
        switch (code) {
            case "100000":
                //文本类数据
                break;
            case "200000":
                //网址类数据
                break;
            case "302000":
                //应用、软件、下载
                break;
            case "304000":
                //新闻
                break;
            case "305000":
                //列车
                break;
            case "306000":
                //航班
                break;
            case "308000":
                //菜谱、视频、小说
                break;
            case "309000":
                //酒店
                break;
            case "311000":
                //价格
                break;
            case "40001":
                //key的长度错误（32位）
                break;
            case "40002":
                //请求内容为空
                break;
            case "40003":
                //key错误或帐号未激活
                break;
            case "40004":
                //当天请求次数已用完
                break;
            case "40005":
                //暂不支持该功能
                break;
            case "40006":
                //服务器升级中
                break;
            case "40007":
                //服务器数据格式异常
                break;
        }
    }
}
