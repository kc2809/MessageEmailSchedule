package com.example.administrator.qlda;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.administrator.qlda.database.MyDatabase;
import com.example.administrator.qlda.fragment.DiscardedFragment;
import com.example.administrator.qlda.fragment.FinisherFragment;
import com.example.administrator.qlda.fragment.PendingFragment;
import com.example.administrator.qlda.message.data.Data;
import com.example.administrator.qlda.receive.service.AlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener,TabHost.OnTabChangeListener,Constant{


    ImageButton btnAdd;
    ViewPager viewPager;
    TabHost tabHost;

    MyFragmentPageAdapter myFragmentPageAdapter;

    MyDatabase myDatabase;

    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addControls();

        getDatabase();

        receiverFromService();

        initViewPage();
        initializeTabs();

        addListener();
    }


    private void receiverFromService() {
        IntentFilter intentFilter  = new IntentFilter();
        intentFilter.addAction("update.ui");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refresh();
            }
        };
        registerReceiver(receiver,intentFilter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    public void getDatabase(){
        // get database
        myDatabase = new MyDatabase(this);
        myDatabase.getDatabase();
  //      myDatabase.deleteAllTable_MessageData();
    }



    View.OnClickListener myClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.btnAdd:
                    doAddMessage();
                    break;
            }
        }
    };

    private void doAddMessage() {
        Intent intent = new Intent(MainActivity.this, ChoiceItemActivity.class);
        startActivityForResult(intent, OPEN_CHOICE_ACTIVITY, null);
    }

    private void addControls(){
        btnAdd = (ImageButton)findViewById(R.id.btnAdd);
    }

    private void addListener() {
        btnAdd.setOnClickListener(myClick);
    }

    public class FakeContent implements TabHost.TabContentFactory
    {
        Context context;
        public FakeContent(Context context) {
            this.context = context;
        }
        @Override
        public View createTabContent(String s) {

            View fakeView = new View(context);
            fakeView.setMinimumHeight(0);
            fakeView.setMinimumWidth(0);
            return fakeView;
        }
    }

    private void initViewPage() {
        viewPager = (ViewPager)findViewById(R.id.view_pager);
        List<android.support.v4.app.Fragment> listFragments = new ArrayList<>();
        listFragments.add(new PendingFragment());
        listFragments.add(new FinisherFragment());
        listFragments.add(new DiscardedFragment());

        myFragmentPageAdapter = new MyFragmentPageAdapter(
                getSupportFragmentManager());

        myFragmentPageAdapter.add(new PendingFragment());
        myFragmentPageAdapter.add(new FinisherFragment());
        myFragmentPageAdapter.add(new DiscardedFragment());

        viewPager.setAdapter(myFragmentPageAdapter);
        viewPager.setOnPageChangeListener(this);

    }


    private void initializeTabs() {
        tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        String[] tabNames = {"Pending","Sent","Discarded"};
        for(int i=0;i<tabNames.length;++i){
            TabHost.TabSpec tabSpec;
            tabSpec = tabHost.newTabSpec(tabNames[i]);
            tabSpec.setIndicator(tabNames[i]);
            tabSpec.setContent(new FakeContent(getApplicationContext()));
            tabHost.addTab(tabSpec);
        }
        tabHost.setOnTabChangedListener(this);
        tabHost.getTabWidget().setBackgroundColor(Color.parseColor("#FFFFFF"));
        setTabColor(tabHost);

    }
    public void setTabColor(TabHost tabHost){
        for(int i=0;i<tabHost.getTabWidget().getChildCount();++i){
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#FFFFFF"));
            TextView t = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            t.setTextColor(Color.parseColor("#45000000"));
        }
        tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.shape_bottom_color);
        TextView t1 = (TextView) tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).findViewById(android.R.id.title);
        t1.setTextColor(Color.parseColor("#009F9F"));
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int selectedItem) {
        tabHost.setCurrentTab(selectedItem);
//        if(isChanged == true){
//            refresh();
//            isChanged = false;
//        }

    }
    //ViewPager listern
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabChanged(String s) {
        int selectedItem = tabHost.getCurrentTab();
        viewPager.setCurrentItem(selectedItem);
        setTabColor(tabHost);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OPEN_CHOICE_ACTIVITY){
            if(resultCode == CLOSE_CHOICE_ACTIVITY){
                getDataFromAddActivity(data);
            }
        }
        if(resultCode == EDIT_MESSAGE_RESPONSE ||resultCode== EDIT_EMAIL_RESPONSE){
            refresh();
            Transfer.getInstance().sequenceAlarm(this);
        }

    }

    private void getDataFromAddActivity(Intent data) {
        Bundle bundle = data.getBundleExtra("DATA");
        Data myData = (Data)bundle.getSerializable("MessageContent");
        //     pendingArr.add(myData);
        myDatabase.insertTbl_Message(myData);
        ArrayList<Data> pendingArr = myDatabase.loadDataTbl_Message_Data(0);

        //update fragment listview
        PendingFragment newPending = new PendingFragment();
        myFragmentPageAdapter.replace(newPending, 0);
        myFragmentPageAdapter.notifyDataSetChanged();
        sequenceAlarm(pendingArr);
    }
    private void sequenceAlarm(ArrayList<Data> pendingArr){
        if(pendingArr.size()>0){
            Data dataAlarm = pendingArr.get(0);
            //----------
            Intent alertIntent = new Intent(this, AlarmReceiver.class);

            Bundle bundle = new Bundle();
            bundle.putSerializable("MESSAGECONTENT", dataAlarm);
            alertIntent.putExtra("DATA",bundle);
            //
            Calendar calendar = dataAlarm.getTime().getDateTime();

            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                    PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }


    public void refresh(){

        PendingFragment newPending = new PendingFragment();
        FinisherFragment newFinisher = new FinisherFragment();
        DiscardedFragment newDicared = new DiscardedFragment();

        myFragmentPageAdapter.replace(newPending, 0);
        myFragmentPageAdapter.replace(newFinisher,1);
        myFragmentPageAdapter.replace(newDicared,2);

        myFragmentPageAdapter.notifyDataSetChanged();

        //xin chao
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
