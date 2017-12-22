package com.example.yuerancai.myapplication;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListAdapter;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.android.volley.toolbox.JsonArrayRequest;
import Adapter.StockDetailAdapter;
import Adapter.StockNewsAdapter;
import android.content.Intent;
import android.net.Uri;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by yuerancai on 2017/11/22.
 */

public class News extends Fragment{
    private ListView listView;
    StockNewsAdapter nAdapter;
    ProgressBar bar;
    public String symbol;
    RequestQueue requestQueue;
    private ArrayList<NewsItem> itemArray=new ArrayList<>();
    private TextView errorNews;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StockNameActivity parentActivity = (StockNameActivity) getActivity();
        requestQueue= Volley.newRequestQueue(getContext());
        this.symbol = parentActivity.stockName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.news, container, false);
        listView = rootView.findViewById(R.id.listview2);
        bar = (ProgressBar)rootView.findViewById(R.id.bar2);
        errorNews = (TextView)rootView.findViewById(R.id.errorNews);
        bar.setVisibility(View.VISIBLE);
        //setListViewHeightBasedOnChildren(listView);
        findNews();
        return rootView;
    }

    private void findNews(){
        String url = "http://hw9csci571.us-east-2.elasticbeanstalk.com/news?newsResult="+ symbol;
        Log.d("url", url);
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("response",response.toString());
                        for(int i =0; i<5 ;i++){
                            try {
                                JSONObject cur = response.getJSONObject(i);

                                NewsItem a =new NewsItem(cur.getString("title"), cur.getString("time"), cur.getString("author"), cur.getString("link"));
                                itemArray.add(a);
                                nAdapter = new StockNewsAdapter(getContext(), itemArray);
                                listView.setAdapter(nAdapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemArray.get(position).getUrl()));
                                        startActivity(browserIntent);
                                    }
                                });
                            } catch (JSONException e) {
                                errorNews.setText("Fail to load data");
                                errorNews.setTextColor(Color.BLACK);
                                errorNews.setTextSize(20);
                                bar.setVisibility(View.GONE);
                            }
                            bar.setVisibility(View.GONE);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    errorNews.setText("Fail to load data");
                    errorNews.setTextColor(Color.BLACK);
                    errorNews.setTextSize(20);
                    bar.setVisibility(View.GONE);
                } catch (Exception e) {
                    errorNews.setText("Fail to load data");
                    errorNews.setTextColor(Color.BLACK);
                    errorNews.setTextSize(20);
                    bar.setVisibility(View.GONE);
                }
            }
        }
        );
        requestQueue.add(jsonObjectRequest);
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
