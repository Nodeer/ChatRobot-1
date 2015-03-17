package com.wangyeming.chatrobot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.Response;
import com.wangyeming.chatrobot.adapter.ConversationAdapter;
import com.wangyeming.chatrobot.http.HttpHelp;
import com.wangyeming.chatrobot.tuling.Tuling;
import com.wangyeming.chatrobot.tuling.json.Lists;
import com.wangyeming.chatrobot.tuling.json.TulingJson;
import com.wangyeming.chatrobot.util.DensityUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class MainActivity extends ActionBarActivity {

    private final String WELCOME_MESSAGE = "喵，有什么要请教我的？你可以陪我聊天，或者问我你想知道的问题" +
            "查火车，天气，价格，菜谱...，喵，我简直太厉害了，咩哈哈哈！";

    private String userId;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private ConversationAdapter mAdapter;
    private List<Map<String, Object>> conversationDisplay = new ArrayList<>();

    private LinearLayout linearLayout;
    private EditText editText;
    private Button sendButton;
    private ImageButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUserId();
        setRecyclerView(); //设置聊天recyclerView
        setInput();  //设置输入框属性
        displayMessage(WELCOME_MESSAGE, true, false);  //显示欢迎消息
        //检查网络状态
        if (!isConnected(getApplicationContext())) {
            setNetworkMethod(this);
        }
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
     * 设置userId
     */
    public void setUserId() {
        TelephonyManager telephonemanager = (TelephonyManager)
                this.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            String IMSI = telephonemanager.getSubscriberId();  //获取手机国际识别码IMEI
            userId = "chatRobot" + IMSI;
        } catch (Exception e) {
            e.printStackTrace();
            Random random = new Random(100);
            Long currentTimeMillis = System.currentTimeMillis();
            userId = random.toString() + currentTimeMillis.toString();
        }

    }

    /**
     * 设置recyclerView
     */
    public void setRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.con_recycler);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ConversationAdapter(this, conversationDisplay);
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * 设置输入框
     */
    public void setInput() {
        linearLayout = (LinearLayout) findViewById(R.id.input_line);
        editText = (EditText) findViewById(R.id.input);
        sendButton = (Button) findViewById(R.id.send_button);
        addButton = (ImageButton) findViewById(R.id.add_button);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int lines = editText.getLineCount();
                float dp;
                if (lines <= 4) {
                    dp = 60 + (lines - 1) * 60;
                } else {
                    dp = 200;
                }
                int px = DensityUtil.dip2px(MainActivity.this, dp);
                linearLayout.getLayoutParams().height = px;
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    addButton.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.GONE);
                } else {
                    addButton.setVisibility(View.GONE);
                    sendButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 获取图灵机器人响应
     *
     * @param sendMessage
     * @return
     */
    public Response getResponse(String sendMessage) {
        Log.d("wym", "message " + sendMessage);
        Tuling tuling = new Tuling();
        tuling.setInfo(sendMessage);
        tuling.setUserId(userId);
        String uri = tuling.generateUri();
        HttpHelp httpHelp = new HttpHelp(tuling);
        Response response = null;
        try {
            response = httpHelp.get(uri);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this,
                    "网络异常" , Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 解析响应消息
     *
     * @param response
     */
    public void parse(String response) {
        Log.d("wym", response);
        try {
            TulingJson tulingJson = JSON.parseObject(response, TulingJson.class);
            String code = tulingJson.code;
            switch (code) {
                case "100000":
                    //文本类数据
                    String text = tulingJson.text;
                    displayMessage(text, true, false);
                    break;
                case "200000":
                    //网址类数据
                    displayUrl(tulingJson);
                    break;
                case "302000":
                    //新闻
                    displayNews(tulingJson);
                    break;
                case "304000":
                    //应用、软件、下载
                    displayApp(tulingJson);
                    break;
                case "305000":
                    //列车
                    displayTraning(tulingJson);
                    break;
                case "306000":
                    //航班
                    displayFight(tulingJson);
                    break;
                case "308000":
                    //菜谱、视频、小说
                    displaySomething(tulingJson);
                    break;
                case "309000":
                    //酒店
                    displayHotel(tulingJson);
                    break;
                case "311000":
                    //价格
                    displayPrice(tulingJson);
                    break;
                case "40001":
                    //key的长度错误（32位）
                    Toast.makeText(MainActivity.this,
                            "（系统）key的长度错误（32位）", Toast.LENGTH_SHORT).show();
                    break;
                case "40002":
                    //请求内容为空
                    Toast.makeText(MainActivity.this,
                            "（系统）请求内容为空", Toast.LENGTH_SHORT).show();
                    break;
                case "40003":
                    //key错误或帐号未激活
                    Toast.makeText(MainActivity.this,
                            "（系统）请求内容为空", Toast.LENGTH_SHORT).show();
                    break;
                case "40004":
                    //当天请求次数已用完
                    Toast.makeText(MainActivity.this,
                            "（系统）当天请求次数已用完", Toast.LENGTH_SHORT).show();
                    break;
                case "40005":
                    //暂不支持该功能
                    Toast.makeText(MainActivity.this,
                            "（系统）暂不支持该功能", Toast.LENGTH_SHORT).show();
                    break;
                case "40006":
                    //服务器升级中
                    Toast.makeText(MainActivity.this,
                            "（系统）服务器升级中", Toast.LENGTH_SHORT).show();
                    break;
                case "40007":
                    //服务器数据格式异常
                    Toast.makeText(MainActivity.this,
                            "（系统）服务器数据格式异常", Toast.LENGTH_SHORT).show();
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this,
                    "网络异常，请检查网络是否被重定向或未登录", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 发送消息
     *
     * @param view
     */
    public void sendMessage(View view) {
        //检查网络状态
        if (!isConnected(getApplicationContext())) {
            setNetworkMethod(this);
            return;
        }
        final String message = editText.getText().toString();
        displayMessage(message, false, false);
        editText.setText(null); //清空输入框
        editText.clearFocus();
        new Thread(new Runnable() {

            @Override
            public void run() {
                Response response = getResponse(message);
                int response_code = 0;
                String response_string = null;
                try {
                    response_code = response.code();
                    Log.d("wym", "response_code "+ response_code);
                    response_string = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (response_code == 200) {
                    Message message = Message.obtain();
                    message.obj = response_string;
                    MainActivity.this.handler1.sendMessage(message);
                } else {
                    Toast.makeText(MainActivity.this,
                            "网络错误，状态码：" + response_code, Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            String response = msg.obj.toString();
            parse(response);
        }
    };

    /**
     * 获取当前时间
     *
     * @return
     */
    public String getCurrentTime() {
        Long currentTimeMillis = System.currentTimeMillis(); //获取当前时间
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
        String LgTime = sdFormat.format(currentTimeMillis);
        return LgTime;
    }

    /**
     * 显示消息
     *
     * @param sendMessage
     * @param isRobot
     */
    public void displayMessage(String sendMessage, Boolean isRobot, Boolean isHtml) {
        Map<String, Object> conversationMap = new HashMap<>();
        conversationMap.put("isRobot", isRobot);
        conversationMap.put("message", sendMessage);
        conversationMap.put("isHtml", isHtml);
        String LgTime = getCurrentTime();
        conversationMap.put("date", LgTime);
        if (isRobot) {
            conversationMap.put("avatar", R.mipmap.cat2);
        } else {
            conversationMap.put("avatar", R.mipmap.xiamu01);
        }
        conversationDisplay.add(0, conversationMap);
        //conversationDisplay.add(conversationMap);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 显示链接
     *
     * @param tulingJson
     */
    public void displayUrl(TulingJson tulingJson) {
        String text = tulingJson.text;
        String display = "<html><head><title>" + text + "</title></head>";
        String url = tulingJson.url;
        display = display + "<body><p><strong><a href=\"" + url + "\">点此链接</a></strong></p>"
                + "</body></html>";
        displayMessage(display, true, true);
    }

    /**
     * 下载应用
     *
     * @param tulingJson
     */
    public void displayApp(TulingJson tulingJson) {
        String text = tulingJson.text;
        String display = "<html><head><title>" + text + "</title></head><body>";
        for (Lists list : tulingJson.list) {
            String name = list.name;
            String count = list.count;
            String detailUrl = list.detailurl;
            String icon = list.icon;
            display = display + "<p><strong><a href=\"" + detailUrl + "\">" + name
                    + "</a></strong></p>";
            if (!icon.equals("")) {
                display = display + "<img src=\"" + icon + "\"/>";
            }
        }
        display = display + "</body></html>";
        displayMessage(display, true, true);//显示为html
    }

    /**
     * 显示新闻
     *
     * @param tulingJson
     */
    public void displayNews(TulingJson tulingJson) {
        String text = tulingJson.text;
        String display = "<html><head><title>" + text + "</title></head><body>";
        for (Lists list : tulingJson.list) {
            String article = list.article;
            String source = list.source;
            String detailUrl = list.detailurl;
            String icon = list.icon;
            display = display + "<p><strong><a href=\"" + detailUrl + "\">" + article
                    + "</a></strong></p>";
            if (!icon.equals("")) {
                display = display + "<img src=\"" + icon + "\"/>";
            }
        }
        display = display + "</body></html>";
        displayMessage(display, true, true);//显示为html
    }

    /**
     * 显示列车信息
     *
     * @param tulingJson
     */
    public void displayTraning(TulingJson tulingJson) {
        String text = tulingJson.text;
        String url = "";
        String display2 = "<html><head><title>" + text + "</title></head>"
                + "<body><table border=1><tr><td>班次</td>" +
                "<td>起点站</td><td>终点站</td><td>起始时间</td><td>到站时间</td>";
        for (Lists list : tulingJson.list) {
            String trainNum = list.trainnum;
            String start = list.start;
            String terminal = list.terminal;
            String starttime = list.starttime;
            String endtime = list.endtime;
            String detailUrl = list.detailurl;
            String icon = list.icon;
            display2 = display2 + "<tr><td><a href=\"" + detailUrl + "\">"
                    + trainNum + "</td><td>" + start + "</td><td>"
                    + terminal + "</td><td>" + starttime
                    + "</td><td>" + endtime + "</td>";
            url = detailUrl;
        }
        display2 = display2 + "</table></body></html>";
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("html", display2);
        startActivity(intent);
        String display = text + "\n" + "点击显示结果";
        displayMessage(display, true, false);
    }

    /**
     * 显示航班信息
     *
     * @param tulingJson
     */
    public void displayFight(TulingJson tulingJson) {
        String text = tulingJson.text;
        String display2 = "<html><head><title>" + text + "</title></head>"
                + "<body><table border=1><tr><td>航班</td>" +
                "<td>路线</td><td>起飞时间</td><td>到达时间</td><td>航班状态</td>";
        for (Lists list : tulingJson.list) {
            String flight = list.flight;
            String route = list.route;
            String starttime = list.starttime;
            String endtime = list.endtime;
            String detailUrl = list.detailurl;
            String state = list.state;
            String icon = list.icon;
            display2 = display2 + "<tr><td><a href=\"" + detailUrl + "\">"
                    + flight + "</td><td>" + route + "</td><td>"
                    + starttime + "</td><td>" + endtime
                    + "</td><td>" + state + "</td>";
        }
        display2 = display2 + "</table></body></html>";
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("html", display2);
        startActivity(intent);
        String display = text + "\n" + "点击显示结果";
        displayMessage(display, true, false);
    }

    /**
     * 显示 菜谱、视频、小说
     *
     * @param tulingJson
     */
    public void displaySomething(TulingJson tulingJson) {
        String text = tulingJson.text;
        String display = text + "\n" + "点击显示结果";
        displayMessage(display, true, false);
        Intent passIntent = new Intent();
        passIntent.setClass(this, CardActivity.class);
        Bundle bundleObject = new Bundle();
        bundleObject.putSerializable("TulingJson", tulingJson);
        passIntent.putExtras(bundleObject);  //传递自定义类
        startActivity(passIntent);

    }

    /**
     * 显示酒店信息
     *
     * @param tulingJson
     */
    public void displayHotel(TulingJson tulingJson) {

    }

    /**
     * 显示价格
     *
     * @param tulingJson
     */
    public void displayPrice(TulingJson tulingJson) {
        String text = tulingJson.text;
        String display = text + "\n" + "点击显示结果";
        displayMessage(display, true, false);
        Intent passIntent = new Intent();
        passIntent.setClass(this, CardActivity.class);
        Bundle bundleObject = new Bundle();
        bundleObject.putSerializable("TulingJson", tulingJson);
        passIntent.putExtras(bundleObject);  //传递自定义类
        startActivity(passIntent);
    }

    /**
     * 判断网络连接是否已开
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        boolean bisConnFlag = false;
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if (network != null) {
            bisConnFlag = conManager.getActiveNetworkInfo().isAvailable();
        }
        return bisConnFlag;
    }


    /**
     * 如果网络没有打开，提示打开网络设置界面
     *
     * @param context
     */
    public void setNetworkMethod(final Context context) {
        new AlertDialog.Builder(this).setTitle("网络设置提示").setMessage("网络连接不可用,是否进行设置?")
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                        context.startActivity(intent);
                    }
                }).setNegativeButton("取消", null).show();
    }
}
