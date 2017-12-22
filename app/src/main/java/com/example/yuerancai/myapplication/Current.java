package com.example.yuerancai.myapplication;
import Adapter.FavoriteListAdapter;
import Adapter.StockDetailAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.content.Intent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Iterator;
import android.widget.Spinner;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.facebook.FacebookSdk;
import android.net.Uri;
import com.facebook.share.model.ShareLinkContent;;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookDialog;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.ShareDialog;
import com.facebook.share.Sharer;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;

import com.google.gson.Gson;
/**
 * Created by yuerancai on 2017/11/22.
 */

public class Current extends Fragment{
    public String symbol;
    private String type = "";
    RequestQueue requestQueue;
    private ListView listview;
    private StockDetailAdapter cAdapter;
    private Button changeBtn;
    private ImageButton shareBtn;
    private ImageButton favBtn;
    private String[] StockContents ={"", "", "", "", "", "", "", "",};
    private WebView chart;
    private String pre = "";
    private Set<String> set;
    ShareDialog shareDialog;
    CallbackManager callbackManager;
    SharedPreferences sharelist;
    ProgressBar bar;
    public ArrayList<favorItem> favorList;
    private TextView errorDetail;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StockNameActivity parentActivity = (StockNameActivity) getActivity();
        this.symbol = parentActivity.stockName;
        set = new HashSet<String>();
        favorList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.current, container, false);
        listview = (ListView)rootView.findViewById(R.id.listview);
        bar = (ProgressBar)rootView.findViewById(R.id.progressBar2);
        bar.setVisibility(View.VISIBLE);
        errorDetail = (TextView)rootView.findViewById(R.id.errorDetail);
        makeTable();
        changeBtn = rootView.findViewById(R.id.change);
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebSettings webSettings = chart.getSettings();
                webSettings.setJavaScriptEnabled(true);
                chart.loadUrl("http://hw9v2-env.us-east-2.elasticbeanstalk.com/?symbol="+symbol+"&&type="+type);
                changeBtn.setEnabled(false);
                pre = type;
            }
        });
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, shareCallBack);
        shareBtn = rootView.findViewById(R.id.facebook);
        sharelist = getActivity().getSharedPreferences("default", Context.MODE_PRIVATE);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
       // Set<String> fetch = editor.getStringSet("List", null);
        favBtn = rootView.findViewById(R.id.favor);
        Map<String,?> keys = sharelist.getAll();
        if(keys.containsKey(symbol)){
            favBtn.setImageResource(R.drawable.filled);
        }
        favBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sharelist = getActivity().getSharedPreferences("default", Context.MODE_PRIVATE);
                Map<String, ?> allEntries = sharelist.getAll();
                boolean flag =false;
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    if(entry.getKey().toString().equals(symbol)){
                        flag = true;
                    }
                }
                if(flag==true){
                    //remove
                    favBtn.setImageResource(R.drawable.empty);
                    SharedPreferences.Editor editor = sharelist.edit();
                    editor.remove(symbol);
                    editor.apply();
                }else{
                    //addaap
                    favBtn.setImageResource(R.drawable.filled);

                    //fetch.add(symbol);
                    SharedPreferences.Editor editor = sharelist.edit();
                    Gson gson = new Gson();
                    favorItem item = new favorItem(symbol,StockContents[1],StockContents[2]);
                    String jsonCurProduct = StockContents[1]+","+StockContents[2];
                    Log.d("fetch",jsonCurProduct);
                    editor.putString(symbol,jsonCurProduct);
                    editor.apply();
                    Log.d("price",StockContents[1]);
                    Log.d("change",StockContents[2]);
                }
            }
        });
        chart = rootView.findViewById(R.id.webview1);
        makeSpinner(rootView);
        return rootView;
    }

    private void makeTable(){
        String url = "http://hw9v2-env.us-east-2.elasticbeanstalk.com/quote?quoteResult="+symbol;
        Log.d("url", url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                                Log.d("response",response.toString());
                                JSONObject meta= response.getJSONObject("Meta Data");
                                JSONObject time = response.getJSONObject("Time Series (Daily)");
                                Iterator key = response.getJSONObject("Time Series (Daily)").keys();
                                Object today = key.next();
                                Object pre = key.next();
                                String preday = pre.toString();
                                Double lastPrice = Double.valueOf(time.getJSONObject(today.toString()).getString("4. close"));
                                Double preClose = Double.valueOf(time.getJSONObject(preday).getString("4. close"));
                                Double change = lastPrice-preClose;
                                Double changePer = (change/preClose)*100;
                                Double open = Double.valueOf(time.getJSONObject(today.toString()).getString("1. open"));
                                Double low = Double.valueOf(time.getJSONObject(today.toString()).getString("3. low"));
                                Double high = Double.valueOf(time.getJSONObject(today.toString()).getString("2. high"));
                                StockContents[0] = response.getJSONObject("Meta Data").getString("2. Symbol");
                                StockContents[1] = Math.floor(lastPrice * 100) / 100+"";
                                StockContents[2] = Math.floor(change * 100) / 100 + "("+Math.floor(changePer * 100) / 100+"%)";
                                StockContents[3] = today.toString();
                                StockContents[4] = Math.floor(open * 100) / 100+"";
                                StockContents[5] = Math.floor(lastPrice * 100) / 100+"";
                                StockContents[6] = Math.floor(low* 100) / 100+" - "+Math.floor(high * 100) / 100;
                                StockContents[7] = Double.valueOf(time.getJSONObject(today.toString()).getString("5. volume"))+"";

                                cAdapter = new StockDetailAdapter(getContext(), StockContents);
                                listview.setAdapter(cAdapter);
                                bar.setVisibility(View.GONE);
                        } catch (JSONException e){
                            errorDetail.setText("Fail to load data");
                            errorDetail.setTextColor(Color.BLACK);
                            errorDetail.setTextSize(20);
                            bar.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    errorDetail.setText("Fail to load data");
                    errorDetail.setTextColor(Color.BLACK);
                    errorDetail.setTextSize(20);
                    bar.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void makeSpinner(View rootView){
        Spinner spinner = (Spinner)rootView.findViewById(R.id.indicators);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                type = parent.getItemAtPosition(pos)+"";

                if(!type.equals(pre)){
                    changeBtn.setEnabled(true);
                }else{
                    changeBtn.setEnabled(false);
                }
                Log.d("type", type);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    private FacebookCallback<Sharer.Result> shareCallBack = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };
    private void post(){

        String url = "http://hw9v2-env.us-east-2.elasticbeanstalk.com/fb?type="+type;
        RequestQueue mQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(response))
                        .build();
                        shareDialog.show(linkContent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        mQueue.add(stringRequest);
    }


}
