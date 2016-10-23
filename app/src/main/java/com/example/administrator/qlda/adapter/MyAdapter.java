package com.example.administrator.qlda.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.administrator.qlda.Constant;
import com.example.administrator.qlda.EditEmailActivity;
import com.example.administrator.qlda.EditMessageActivity;
import com.example.administrator.qlda.R;
import com.example.administrator.qlda.Transfer;
import com.example.administrator.qlda.database.MyDatabase;
import com.example.administrator.qlda.message.data.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Administrator on 9/26/2016.
 */
public class MyAdapter extends ArrayAdapter<Data> implements Constant{
    Activity context = null;
    ArrayList<Data> myArray = null;
    int layoutId;


    public MyAdapter(Activity context, int resource, ArrayList<Data> arr) {
        super(context, resource, arr);
        this.context = context;
        this.layoutId = resource;
        myArray = arr;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(layoutId,null);
        if(myArray.size()>0 && position>=0){
             final Data data = myArray.get(position);


            final ImageView imgIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
            final TextView tvToObj = (TextView)convertView.findViewById(R.id.tvToObj);
            final TextView contentObj = (TextView)convertView.findViewById(R.id.tvContent);
            final TextView timeObj = (TextView)convertView.findViewById(R.id.tvTime);
            final ImageButton btnDelete = (ImageButton)convertView.findViewById(R.id.imgBtnDelete);


            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopup(view,position,data);
                }
            });

            if(data.getTime().getType() == 0){
                imgIcon.setImageResource(R.drawable.smsic1);
            }
            if(data.getTime().getType() == 1){
                imgIcon.setImageResource(R.drawable.gmail);
            }


            tvToObj.setText(data.getMessage().getDisplayName());
            contentObj.setText(data.getMessage().getMessage());
            timeObj.setText(data.getTime().toString());
        }

        return convertView;
    }

    public void showPopup(View v,final int position,final Data data){
        PopupMenu popup = new PopupMenu(context, v);
        try {
            Class<?> classPopupMenu = Class.forName(popup
                    .getClass().getName());
            Field mPopup = classPopupMenu.getDeclaredField("mPopup");
            mPopup.setAccessible(true);
            Object menuPopupHelper = mPopup.get(popup);
            Class<?> classPopupHelper = Class.forName(menuPopupHelper
                    .getClass().getName());
            Method setForceIcons = classPopupHelper.getMethod(
                    "setForceShowIcon", boolean.class);
            setForceIcons.invoke(menuPopupHelper, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MenuInflater inflate = popup.getMenuInflater();
        inflate.inflate(R.menu.my_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menuEdit:
                        actionEdit(data);
                        break;
                    case R.id.menuDelete:
                        actionDelete(position,data);
                        break;
                }
                return false;
            }
        });

        popup.show();
    }

    private void actionEdit(Data data){
        Intent in ;

        int requestCode;
        if(data.getTime().getType() == 0)
        {
            in = new Intent(context, EditMessageActivity.class);
            requestCode = EDIT_REQUEST;
        }
        else{
            in = new Intent(context, EditEmailActivity.class);
            requestCode = EDIT_EMAIL_REQUEST;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("MESSAGECONTENT",data);
        in.putExtra("DATA", bundle);
        context.startActivityForResult(in,requestCode);
    }

    private void actionDelete(final int position,final Data data){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure to delete this message... ? ")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MyDatabase myDatabase = new MyDatabase(context);
                                    myDatabase.getDatabase();
                                    myDatabase.deleteMessageById(data.getId());

                                    if(position == 0 && data.getTime().getStatus()==0){
                                        Transfer.getInstance().sequenceAlarm(context);
                                    }
                                    myArray.remove(position);
                                    notifyDataSetChanged();

                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
    }

}
