package com.example.yuerancai.myapplication;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

/**
 * Created by yuerancai on 2017/11/22.
 */

public class Historical extends Fragment{
    public String symbol;
    private WebView historical;
    ProgressBar bar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StockNameActivity parentActivity = (StockNameActivity) getActivity();
        this.symbol = parentActivity.stockName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.historical, container, false);
        bar = (ProgressBar)rootView.findViewById(R.id.bar1);
        bar.setVisibility(View.VISIBLE);
        historical = rootView.findViewById(R.id.webview2);
        drawHis();
        return rootView;
    }
    private void drawHis(){
        WebSettings webSettings = historical.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //Log.d("his","his");
        historical.loadUrl("http://hw9csci571.us-east-2.elasticbeanstalk.com/index.html?symbol="+symbol+"&&type=historical");
        bar.setVisibility(View.GONE);
    }
}
