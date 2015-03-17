package com.wangyeming.chatrobot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;


public class WebViewActivity extends ActionBarActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            //actionBar.setDisplayShowTitleEnabled(false);
            actionBar.hide();
        }
        setWebView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web_view, menu);
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

    public void setWebView() {
        webView = (WebView) findViewById(R.id.webView);
        Intent intent = getIntent();
        String html = intent.getStringExtra("html");
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }
}
