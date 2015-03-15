package com.wangyeming.chatrobot;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.wangyeming.chatrobot.adapter.ConversationAdapter;
import com.wangyeming.chatrobot.http.HttpHelp;
import com.wangyeming.chatrobot.tuling.Tuling;
import com.wangyeming.chatrobot.tuling.json.TulingJson;
import com.wangyeming.chatrobot.util.DensityUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private final String WELCOME_MESSAGE = "喵，有什么要请教我的？";

    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private ConversationAdapter mAdapter;
    private List<Map<String, Object>> conversationDisplay = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRecyclerView(); //设置聊天recyclerView
        setInput();  //设置输入框属性
        /*new Thread(new Runnable() {

            @Override
            public void run() {
                String response = getResponse();
                parse(response);
            }
        }).start();*/
        welcomeMessage();  //显示欢迎消息
        mAdapter.notifyDataSetChanged();
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

    /**
     * 设置recyclerView
     */
    public void setRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.con_recycler);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ConversationAdapter(this, conversationDisplay);
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * 设置欢迎语
     */
    public void welcomeMessage() {
        Map<String, Object> conversationMap = new HashMap<>();
        conversationMap.put("isRobot", true);
        conversationMap.put("message", WELCOME_MESSAGE);
        Long currentTimeMillis = System.currentTimeMillis(); //获取当前时间
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
        String LgTime = sdFormat.format(currentTimeMillis);
        conversationMap.put("date", LgTime);
        conversationMap.put("avatar", R.mipmap.cat2);
        conversationDisplay.add(conversationMap);
    }

    public void setInput() {
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.input_line);
        final EditText editText = (EditText) findViewById(R.id.input);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int lines = editText.getLineCount();
                float dp;
                if (lines > 2 && lines <= 4) {
                    dp = 70 + (lines - 2) * 35;
                } else if(lines >4) {
                    dp = 140;
                }
                else {
                    dp = 70;
                }
                int px = DensityUtil.dip2px(MainActivity.this, dp);
                Log.d("wym", "px " + px + " dp " + dp);
                linearLayout.getLayoutParams().height = px;
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
