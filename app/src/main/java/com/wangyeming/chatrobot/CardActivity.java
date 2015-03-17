package com.wangyeming.chatrobot;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.wangyeming.chatrobot.adapter.FoodAdapter;
import com.wangyeming.chatrobot.tuling.json.Lists;
import com.wangyeming.chatrobot.tuling.json.TulingJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CardActivity extends ActionBarActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private FoodAdapter mAdapter;
    private List<Map<String, Object>> foodDisplay = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card, menu);
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

    public void init() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayShowTitleEnabled(false);
            actionBar.hide();
        }
        setRecyclerView();
        Bundle bundleObject = getIntent().getExtras();
        TulingJson tulingJson = (TulingJson) bundleObject.getSerializable("TulingJson");
        parseJson(tulingJson);
    }

    /**
     * 设置recyclerView
     */
    public void setRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.display_recycler);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FoodAdapter(this, foodDisplay);
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * 转存json数据
     *
     * @param tulingJson
     */
    public void parseJson(TulingJson tulingJson) {
        String code = tulingJson.code;
        switch (code) {
            case "308000":
                parseFood(tulingJson);
                break;
            case "311000":
                parsePrice(tulingJson);
                break;
        }

    }

    /**
     * 菜谱、视频、小说
     *
     * @param tulingJson
     */
    public void parseFood(TulingJson tulingJson) {
        String text = tulingJson.text;
        for (Lists list : tulingJson.list) {
            Map<String, Object> foodMap = new HashMap<>();
            String name = list.name;
            String info = list.info;
            String detailUrl = list.detailurl;
            String icon = list.icon;
            foodMap.put("name", name);
            foodMap.put("info", "原料：" + info);
            foodMap.put("detailUrl", detailUrl);
            foodMap.put("icon", icon);
            foodMap.put("sort", "food");
            foodDisplay.add(foodMap);
        }
    }

    /**
     * 价格
     *
     * @param tulingJson
     */
    public void parsePrice(TulingJson tulingJson) {
        String text = tulingJson.text;
        for (Lists list : tulingJson.list) {
            Map<String, Object> foodMap = new HashMap<>();
            String name = list.name;
            String price = list.price;
            String detailUrl = list.detailurl;
            String icon = list.icon;
            foodMap.put("name", name);
            foodMap.put("price", price);
            foodMap.put("detailUrl", detailUrl);
            foodMap.put("icon", icon);
            foodMap.put("sort", "price");
            foodDisplay.add(foodMap);
        }
    }
}
