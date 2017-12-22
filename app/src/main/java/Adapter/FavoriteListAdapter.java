package Adapter;

import android.graphics.Color;
import android.widget.ArrayAdapter;

import com.example.yuerancai.myapplication.NewsItem;
import com.example.yuerancai.myapplication.R;
import com.example.yuerancai.myapplication.favorItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
/**
 * Created by yuerancai on 2017/11/29.
 */

public class FavoriteListAdapter extends BaseAdapter {
    private ArrayList<favorItem> list;
    private Context mContext;

    public FavoriteListAdapter(Context context, ArrayList<favorItem> itemarray) {
        this.mContext=context;
        this.list=itemarray;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.itm_list_favor,parent,false);
        TextView symbol = (TextView) convertView.findViewById(R.id.symbol);
        TextView price = (TextView) convertView.findViewById(R.id.price);
        TextView  change = (TextView) convertView.findViewById(R.id.change);
        symbol.setText(list.get(position).getSymbol());
        price.setText(list.get(position).getPrice());
        change.setText(list.get(position).getChange());
        if(list.get(position).getChange().charAt(0)=='-'){
            change.setTextColor(Color.RED);
        }else{
            change.setTextColor(Color.GREEN);
        }

        return convertView;
    }

    public void addData (favorItem item) {
        list.add(item);
        notifyDataSetChanged();
    }
    public void updateData (ArrayList<favorItem> item) {
        this.list=item;
        notifyDataSetChanged();
    }
}
