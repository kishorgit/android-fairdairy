package com.example.user.sunaitestingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.sunaitestingapp.R;

import java.util.ArrayList;

public class spinnercustomadapter extends BaseAdapter {
    Context context;
    ArrayList<String> imagelist;
    ArrayList<String> namelist ;
    ArrayList<String> pricelist;


    LayoutInflater inflter;

    public spinnercustomadapter(Context applicationContext,   ArrayList<String> imagelist,    ArrayList<String> namelist, ArrayList<String> pricelist) {
        this.context = applicationContext;
        this.imagelist = imagelist;
        this.namelist = namelist;
        this.pricelist = pricelist;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return namelist.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_row, null);
        de.hdodenhof.circleimageview.CircleImageView prodimg = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.prodimg);
        TextView prodname = (TextView) view.findViewById(R.id.prodname);
        TextView prodprice = (TextView) view.findViewById(R.id.prodprice);
//        prodimg.setImageResource(Integer.parseInt(imagelist.get(i)));
        prodname.setText(namelist.get(i));
        prodprice.setText("₹ "+pricelist.get(i));
        return view;
    }
}
