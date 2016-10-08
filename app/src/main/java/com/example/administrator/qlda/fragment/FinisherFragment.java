package com.example.administrator.qlda.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.qlda.Constant;
import com.example.administrator.qlda.DetailEmailActivity;
import com.example.administrator.qlda.DetailMessageActivity;
import com.example.administrator.qlda.R;
import com.example.administrator.qlda.adapter.MyAdapter;
import com.example.administrator.qlda.database.MyDatabase;
import com.example.administrator.qlda.message.data.Data;

import java.util.ArrayList;

/**
 * Created by Administrator on 10/3/2016.
 */
public class FinisherFragment extends Fragment implements Constant, AdapterView.OnItemClickListener{
    ListView lv;
    TextView tv;
    ArrayList<Data> myArr;
    Data data;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v  = inflater.inflate(R.layout.finisher_layout,container,false);
        lv = (ListView)v.findViewById(R.id.lvFinisher);
        tv = (TextView)v.findViewById(R.id.tvNoItem);
        refresh();
        return v;
    }

    public void refresh(){
        MyDatabase myDatabase = new MyDatabase(getActivity());
        myDatabase.getDatabase();

        myArr= myDatabase.loadDataTbl_Message_Data(1);

        if(myArr.size()>0) {
            tv.setVisibility(View.INVISIBLE);
            MyAdapter adapter = new MyAdapter(getActivity(),R.layout.my_item_layout,myArr);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(this);


        }
        else{
            tv.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        data = myArr.get(i);
        int requestCode;

        Intent in;
        if(data.getTime().getType()==0){
            in = new Intent(getActivity(), DetailMessageActivity.class);
            requestCode = OPEN_DETAIL_MESS_ACTIVITY;
        }
        else{
            in = new Intent(getActivity(), DetailEmailActivity.class);
            requestCode = OPEN_DETAIL_EMAIL_ACTIVITY;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("MESSAGECONTENT", data);
        in.putExtra("DATA", bundle);
        startActivityForResult(in, requestCode);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == SOMETHING_CHANGE){
            refresh();
        }
    }
}
