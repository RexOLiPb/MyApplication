package Adapter;

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
/**
 * Created by yuerancai on 2017/11/22.
 */

public class StockDetailAdapter extends BaseAdapter {
    private String[] stockTitles = {"Stock Symbol", "Last Price", "Change", "Timestamp", "Open", "Close", "Day's Range", "Volume"};
    private String[] stockContents = new String[8];
    private static LayoutInflater inflater;

    public StockDetailAdapter(Context context, String[] stockDetailContents) {
        this.stockContents = stockDetailContents;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return stockTitles.length;
    }

    @Override
    public Object getItem(int position) {
        return stockContents[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class StockDetailCellHolder {
        TextView stockDetailCellTitle;
        TextView stockDetailCellContent;
        ImageView stockDetailCellImageView;
    }

    @Override
    public View getView (final int position, View convertView, ViewGroup parent) {
        StockDetailCellHolder holder;

        if (convertView == null) {
            holder = new StockDetailCellHolder();
            convertView = inflater.inflate(R.layout.list_cell_stock_detail, parent, false);
            holder.stockDetailCellTitle = (TextView) convertView.findViewById(R.id.stockDetailCellTitle);
            holder.stockDetailCellContent = (TextView) convertView.findViewById(R.id.stockDetailCellContent);
            holder.stockDetailCellImageView = (ImageView) convertView.findViewById(R.id.stockDetailCellImageView);
            convertView.setTag(holder);
        } else {
            holder = (StockDetailCellHolder) convertView.getTag();
        }
        Log.d("title",stockTitles[position]);
        holder.stockDetailCellTitle.setText(stockTitles[position]);
        holder.stockDetailCellContent.setText(stockContents[position]);
        if(position==2){
            if(stockContents[2].charAt(0)=='-'){
                holder.stockDetailCellImageView.setImageResource(R.drawable.down);
            }else{
                holder.stockDetailCellImageView.setImageResource(R.drawable.up);
            }
        }
        else{
            holder.stockDetailCellImageView.setImageResource(R.color.colorNone);
        }

        return convertView;
    }

    public void refreshData (String[] newContentList) {
        this.stockContents = newContentList;
        notifyDataSetChanged();
    }
}















