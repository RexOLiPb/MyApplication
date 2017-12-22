package com.example.yuerancai.myapplication;

import android.net.Uri;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.content.Intent;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import android.graphics.Color;

import java.util.Timer;
import java.util.TimerTask;

import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import Adapter.FavoriteListAdapter;
import Adapter.StockDetailAdapter;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    AutoCompleteTextView auto;
    RequestQueue requestQueue;
    Button quoBtn;
    Button clearBtn;
    SharedPreferences share;
    ListView favorListView;
    ImageButton refreshBtn;
    ProgressBar bar;
    ProgressBar load;
    Switch switchBtn;
    public FavoriteListAdapter fAdapter;
    private ArrayList<favorItem> list;
    private String[] StockContents = new String[3];
    private Timer timer;
    private Timer lala;
    private ArrayList<favorItem> defaultlist;
    private String[] sort = {"Sort by","Default","Symbol", "Price","Change"};
    private String[] orderarr = {"Order","Ascending","Descending"};
    private String sortby = "";
    private String order = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);
        auto = (AutoCompleteTextView)findViewById(R.id.autocompletetext);
        bar = (ProgressBar)findViewById(R.id.progress_bar);
        bar.setVisibility(View.GONE);
        load = (ProgressBar)findViewById(R.id.load);
        load.setVisibility(View.GONE);
        quoBtn = (Button)findViewById(R.id.button);
        clearBtn = (Button)findViewById(R.id.button3);
        refreshBtn = (ImageButton)findViewById(R.id.refresh);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItem();
            }
        });
        quoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                quote();
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                clear();
            }
        });
        favorListView = (ListView) findViewById(R.id.favorlist);
        favorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemName = list.get(position).getSymbol();
                Intent intent = new Intent(MainActivity.this, StockNameActivity.class);
                intent.putExtra("key", itemName);
                startActivity(intent);
            }
        });
        list = new ArrayList<>();
        fAdapter = new FavoriteListAdapter(this, list);
        favorListView.setAdapter(fAdapter);
        defaultlist = new ArrayList<>();
        registerForContextMenu(favorListView);
        makeSpinner();
        AorD();
        auto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                bar.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bar.setVisibility(View.VISIBLE);
                if(s.toString().equals("")){
                    bar.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String Dest = auto.getText().toString();
                //bar.setVisibility(View.GONE);
                Log.d("Tag",Dest);
                //String url = "http://hw8nodejs-env.us-east-2.elasticbeanstalk.com/test?country="+Dest;
                String url = "http://hw8nodejs-env.us-east-2.elasticbeanstalk.com/test?country="+Dest;
                request(url);

            }
        });
        switchBtn = (Switch)findViewById(R.id.switch1);
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Log.d("switch","true");
                    timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {@Override public void run() {getItem();}}, 0, 5000);
                }else{
                    Log.d("switch","false");
                    timer.cancel();
                }
            }
        });


        }

    @Override
    protected void onResume() {
        super.onResume();
        list = new ArrayList<>();
        sortby = "Sort by";

        order = "Order";
        initialListview();
    }

    private void quote(){
        String stockName = auto.getText().toString().split("-")[0];
        if (stockName != null && !stockName.equals("")) {
            Intent intent = new Intent(MainActivity.this, StockNameActivity.class);
            intent.putExtra("key", stockName);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Please enter a stock name or symbol.", Toast.LENGTH_SHORT).show();
        }
    }

    private void request(String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.d("Length", response.length()+"");
                        try{
                            bar.setVisibility(View.GONE);
                            int len = Math.min(response.length(), 5);
                            String[] jStr = new String[len];
                            for(int i=0; i<len; i++){
                                JSONObject obj = response.getJSONObject(i);
                                String res= obj.getString("Symbol")+"-"+obj.getString("Name")+"("+obj.getString("Exchange")+")";
                                jStr[i]=res;
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_dropdown_item_1line, jStr);
                            auto.setAdapter(adapter);
                            auto.setThreshold(1);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e){

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                bar.setVisibility(View.GONE);
            }
        }
        );
        requestQueue.add(jsonArrayRequest);
    }

    private void clear(){
        auto.setText("");
    }

    private void initialListview(){
        share = getSharedPreferences("default", Context.MODE_PRIVATE);
        Map<String,?> keys = share.getAll();


        getItem();
    }

    private void getData(Set<String> fetch){
        getItem();
        fAdapter = new FavoriteListAdapter(this, list);
        favorListView.setAdapter(fAdapter);
    }

    private void getItem(){
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                load.setVisibility(View.VISIBLE);
            }

        });
        share = getSharedPreferences("default", Context.MODE_PRIVATE);
        final Map<String,?> entries = share.getAll();
        list = new ArrayList<>();
        for(String symbol: entries.keySet()){
            String url = "http://hw9-env.us-east-2.elasticbeanstalk.com/priceS?stockSymbol="+symbol;
            Log.d("url2nd", url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                Log.d("response2nd",response.toString());
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
                                StockContents[0] = response.getJSONObject("Meta Data").getString("2. Symbol");
                                StockContents[1] = Math.floor(lastPrice * 100) / 100+"";
                                StockContents[2] = Math.floor(change * 100) / 100 + "("+Math.floor(changePer * 100) / 100+"%)";
                                list.add(new favorItem(StockContents[0],StockContents[1],StockContents[2]));
                                Log.d("wifheiuwgi",list.size()+"");
                                Log.d("sifuhsef","skjfheiusfh");
                                Log.d("wifheiuwgi",entries.size()+"");
                                if(list.size()==entries.size()-1){
                                    load.setVisibility(View.GONE);
                                }
                                fAdapter.updateData(list);
                            } catch (JSONException e){

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
            );
            requestQueue.add(jsonObjectRequest);
            Log.d("symbol2",symbol);
        }


    }
    private void makeSpinner(){
        Spinner spinner = (Spinner)findViewById(R.id.sortby);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(MainActivity.this,R.layout.spinner, sort){
            @Override
            public boolean isEnabled(int position){
                if(position == 0 || sort[position].equals(sortby))
                {
                    // Disable the second item from Spinner
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                    ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0||sort[position].equals(sortby)) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }

        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                sortby = parent.getItemAtPosition(pos)+"";
                if(sortby.equals("Symbol")){
                    Collections.sort(list, new Comparator<favorItem>() {
                        @Override
                        public int compare(final favorItem object1, final favorItem object2) {
                            return object1.getSymbol().compareTo(object2.getSymbol());
                        }
                    });
                    if(order.equals("Ascending")){
                        fAdapter.updateData(list);
                    }else if(order.equals("Descending")){
                        Collections.reverse(list);
                        fAdapter.updateData(list);
                    }

                }else if(sortby.equals("Price")){
                    Log.d("sort2","price");
                    Collections.sort(list, new Comparator<favorItem>() {
                        @Override
                        public int compare(final favorItem object1, final favorItem object2) {
                            double diff = Double.valueOf(object1.getPrice())-Double.valueOf(object2.getPrice());
                            if(diff>0) return 1;
                            else if(diff<0) return -1;
                            else return 0;
                        }
                    });
                    if(order.equals("Ascending")){
                        Log.d("sort2","price_a");
                        fAdapter.updateData(list);
                    }else if(order.equals("Descending")){
                        Collections.reverse(list);
                        Log.d("sort2","price_d");
                        fAdapter.updateData(list);
                    }
                }else if(sortby.equals("Change")){
                    Collections.sort(list, new Comparator<favorItem>() {
                        @Override
                        public int compare(final favorItem object1, final favorItem object2) {
                            String a = object1.getChange().split("\\(")[0];
                            String b = object2.getChange().split("\\(")[0];
                            double diff = Double.valueOf(a)-Double.valueOf(b);
                            if(diff>0) return 1;
                            else if(diff<0) return -1;
                            else return 0;
                        }
                    });
                    if(order.equals("Ascending")){
                        Log.d("sort2","change_a");
                        fAdapter.updateData(list);
                    }else if(order.equals("Descending")){
                        Collections.reverse(list);
                        Log.d("sort2","change_d");
                        fAdapter.updateData(list);
                    }
                }else if(order.equals("Default")){
                    list = defaultlist;
                    fAdapter.updateData(defaultlist);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    private void AorD(){
        final Spinner spinner = (Spinner)findViewById(R.id.order);
        ArrayAdapter<CharSequence> adapter =new ArrayAdapter<CharSequence>(MainActivity.this,R.layout.spinner,orderarr){
            @Override
            public boolean isEnabled(int position){
                if(position == 0 || orderarr[position].equals(order))
                {
                    // Disable the second item from Spinner
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0||orderarr[position].equals(order)) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                order = parent.getItemAtPosition(pos)+"";
                Log.d("sort",sortby);

                    if(sortby.equals("Symbol")){
                        Collections.sort(list, new Comparator<favorItem>() {
                            @Override
                            public int compare(final favorItem object1, final favorItem object2) {
                                return object1.getSymbol().compareTo(object2.getSymbol());
                            }
                        });
                        if(order.equals("Ascending")){
                            fAdapter.updateData(list);
                        }else if(order.equals("Descending")){
                            Collections.reverse(list);
                            fAdapter.updateData(list);
                        }

                    }else if(sortby.equals("Price")){
                        Log.d("sort2","price");
                        Collections.sort(list, new Comparator<favorItem>() {
                            @Override
                            public int compare(final favorItem object1, final favorItem object2) {
                                double diff = Double.valueOf(object1.getPrice())-Double.valueOf(object2.getPrice());
                                if(diff>0) return 1;
                                else if(diff<0) return -1;
                                else return 0;
                            }
                        });
                        if(order.equals("Ascending")){
                            Log.d("sort2","price_a");
                            fAdapter.updateData(list);
                        }else if(order.equals("Descending")){
                            Collections.reverse(list);
                            Log.d("sort2","price_d");
                            fAdapter.updateData(list);
                        }
                    }else if(sortby.equals("Change")){
                        Collections.sort(list, new Comparator<favorItem>() {
                            @Override
                            public int compare(final favorItem object1, final favorItem object2) {
                                String a = object1.getChange().split("\\(")[0];
                                String b = object2.getChange().split("\\(")[0];
                                double diff = Double.valueOf(a)-Double.valueOf(b);
                                if(diff>0) return 1;
                                else if(diff<0) return -1;
                                else return 0;
                            }
                        });
                        if(order.equals("Ascending")){
                            Log.d("sort2","change_a");
                            fAdapter.updateData(list);
                        }else if(order.equals("Descending")){
                            Collections.reverse(list);
                            Log.d("sort2","change_d");
                            fAdapter.updateData(list);
                        }
                    }
//
//
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Remove from Favorites?");
        menu.add(0, v.getId(), 0, "Yes");
        menu.add(0, v.getId(), 0, "No");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(item.getTitle()=="Yes"){
            share = getSharedPreferences("default", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = share.edit();
            editor.remove(list.get(info.position).getSymbol());
            editor.apply();
            list.remove(info.position);
            fAdapter.updateData(list);
            Toast.makeText(getApplicationContext(),"Selected Yes",Toast.LENGTH_LONG).show();
        }
        return super.onContextItemSelected(item);
    }

}
