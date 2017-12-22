package Adapter;

import android.content.Context;
import java.util.ArrayList;
import com.example.yuerancai.myapplication.NewsItem;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.yuerancai.myapplication.R;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
/**
 * Created by yuerancai on 2017/11/24.
 */

public class StockNewsAdapter extends BaseAdapter{
    private ArrayList<NewsItem> list;
    private Context mContext;

    public StockNewsAdapter(Context context, ArrayList<NewsItem> itemarray) {
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
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_news,parent,false);
        TextView title = (TextView) convertView.findViewById(R.id.heading);
        TextView author = (TextView) convertView.findViewById(R.id.author);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        title.setText(list.get(position).getNewsHeading());
        author.setText(list.get(position).getAuthor());
        date.setText(list.get(position).getDate());
        return convertView;
    }
}
