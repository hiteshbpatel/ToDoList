package com.example.sakshi.todolist_project;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.R.attr.handle;
import static android.R.attr.id;
import static android.R.attr.name;
import static android.R.attr.targetActivity;

public class CustomAdapter extends BaseAdapter{

    private Context context;
    private List<Data> list;
    LayoutInflater mLayoutInflator;
    TextView title;
    TextView desc;
    TextView date;
    int i;
    ArrayList<Data> arraylist;

    ImageView imageView;

    public CustomAdapter(Context context, List<Data> list, int i){
        this.context=context;
        this.list=list;
        this.i = i;
        mLayoutInflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arraylist = new ArrayList<>();
        arraylist.addAll(list);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        convertView = mLayoutInflator.inflate(R.layout.row,null);
        title = (TextView)convertView.findViewById(R.id.task);
        desc = (TextView)convertView.findViewById(R.id.desc);
        date = (TextView)convertView.findViewById(R.id.date);
        title.setText(list.get(position).getTitle());
        desc.setText(list.get(position).getDescription());
        Date inputDate=null;
        Calendar calendar = Calendar.getInstance();
        String getdate = list.get(position).getDate();
        String parts[] = getdate.split("-");
        String inputDateStr = String.format("%s/%s/%s", parts[0], parts[1],parts[2]);

        try {
            inputDate = new SimpleDateFormat("yyyy/MM/dd").parse(inputDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(inputDate);
        String day = parts[2];
        String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
        String monthoftheyear = calendar.getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.US);
        String year = parts[0];
        final String getfinaldate = dayOfWeek+", "+day+" "+monthoftheyear+" "+year;
        date.setText(getfinaldate);

        imageView = (ImageView)convertView.findViewById(R.id.thumbsup);
        if(i==1){
            imageView.setImageResource(R.mipmap.thumbsup);
        }else if(i==0){
            imageView.setImageResource(R.drawable.incomplete);
        }
        return convertView;
    }
    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());

        list.clear();
        if (charText.length() == 0) {
            list.addAll(arraylist);

        } else {
            for (Data postDetail : arraylist) {
                if (charText.length() != 0 && postDetail.getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    list.add(postDetail);
                }
            }
        }
        notifyDataSetChanged();
    }
}

