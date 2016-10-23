package com.example.administrator.qlda.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.administrator.qlda.R;
import com.example.administrator.qlda.message.data.PhoneContact;

import java.util.ArrayList;

/**
 * Created by Administrator on 10/4/2016.
 */
public class RecipentAdapter extends ArrayAdapter<PhoneContact> {
    Activity context;
    ArrayList<PhoneContact> myArr;
    int layoutId;
    public RecipentAdapter(Activity context, int resource, ArrayList<PhoneContact> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutId = resource;
        myArr = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(layoutId,null);
        if(myArr.size()>0 && position>=0){
            final TextView tvContact1 = (TextView)convertView.findViewById(R.id.tvContact1);
            final TextView tvContact2 = (TextView)convertView.findViewById(R.id.tvContact2);

            PhoneContact tempData= myArr.get(position);
            tvContact1.setText(tempData.getDisplayName());
            tvContact2.setText(tempData.getPhoneNumber());
        }


        return convertView;
    }
}
