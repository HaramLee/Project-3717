package com.example.haram.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class ListAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;

    public ListAdapter(Activity a, ArrayList<HashMap<String, String>> d){
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount(){
        return data.size();
    }

    public Object getItem(int position){
        return position;
    }

    public long getItemId(int position){
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View vi = convertView;
        if(convertView == null){
            vi = inflater.inflate(R.layout.list_row, null);
        }
        TextView date = (TextView)vi.findViewById(R.id.date);
        TextView day = (TextView)vi.findViewById(R.id.day);
        TextView start = (TextView)vi.findViewById(R.id.start);
        TextView end = (TextView)vi.findViewById(R.id.end);
        TextView summary = (TextView)vi.findViewById(R.id.summary);
        TextView month = (TextView)vi.findViewById(R.id.month);
        TextView year = (TextView)vi.findViewById(R.id.year);

        HashMap<String, String> cal = new HashMap<String, String>();
        cal = data.get(position);

        date.setText(cal.get(MainActivity.KEY_DATE));
        day.setText(cal.get(MainActivity.KEY_DAY));
        start.setText(cal.get(MainActivity.KEY_START));
        end.setText(cal.get(MainActivity.KEY_END));
        summary.setText(cal.get(MainActivity.KEY_SUMMARY));
        month.setText(cal.get(MainActivity.KEY_MONTH));
        year.setText(cal.get(MainActivity.KEY_YEAR));

        return vi;
    }
}
