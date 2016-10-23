package com.example.administrator.qlda;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.administrator.qlda.database.MyDatabase;
import com.example.administrator.qlda.message.data.Data;
import com.example.administrator.qlda.message.data.PhoneContact;
import com.example.administrator.qlda.receive.service.AlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Administrator on 10/4/2016.
 */
public class Transfer {
    private static Transfer instance = null;
    MyDatabase myDatabase;
    Context context=null;

    ArrayList<PhoneContact> arr=null;

    private Transfer(){
    }

    public static Transfer getInstance(){
        if(instance == null){
            instance = new Transfer();
        }
        return instance;
    }

    public boolean sendData(Context context,Data data){
        this.context = context;
        myDatabase = new MyDatabase(context);
        myDatabase.getDatabase();

        boolean result = true;
        // if data is message type
        if(data.getTime().getType()==0){
            result = sendSms(data);
        }
        // if data is email type
        if(data.getTime().getType()==1){

        }

        if(result == true){
            Toast.makeText(context, "Sent Successfully", Toast.LENGTH_LONG).show();
            myDatabase.updateMessageStatusById(1, data.getId());
        }
        else{
           Toast.makeText(context, "Something wrong happen", Toast.LENGTH_LONG).show();
            myDatabase.updateMessageStatusById(2, data.getId());
        }
        return result;
    }

    private boolean sendSms(Data data){
        final boolean[] check = {true};
        final SmsManager sms = SmsManager.getDefault();
        Intent msgSent = new Intent("ACTION_MSG_SENT");

        final PendingIntent pendingMsgSent = PendingIntent.getBroadcast(context, 0, msgSent, 0);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int result = getResultCode();
                String msg = "Message is sent successfully";
                if (result != Activity.RESULT_OK) {
                    check[0] = false;
                    msg = "Failed to send";
                }
                //      Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }, new IntentFilter("ACTION_MSG_SENT"));
        //gui tin nhan di
        try {
            sms.sendTextMessage(data.getMessage().getTo(), null, data.getMessage().getMessage(),
                    pendingMsgSent, null);
        }
        catch(Exception e){
            check[0]= false;
     //       Toast.makeText(context, "Something wrong happen", Toast.LENGTH_LONG).show();
        }
        return check[0];
    }

    public void sequenceAlarm(Context context) {
        if(myDatabase ==null){
            myDatabase = new MyDatabase(context);
            myDatabase.getDatabase();
        }
        ArrayList<Data> pendingArr = myDatabase.loadDataTbl_Message_Data(0);

        if(pendingArr.size()>0){
            Data dataAlarm = pendingArr.get(0);
            //----------
            Intent alertIntent = new Intent(context, AlarmReceiver.class);

            Bundle bundle = new Bundle();
            bundle.putSerializable("MESSAGECONTENT", dataAlarm);
            alertIntent.putExtra("DATA",bundle);
            //
            Calendar calendar = dataAlarm.getTime().getDateTime();

            AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                    PendingIntent.getBroadcast(context, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        }
        else{
            Intent alertIntent = new Intent(context,AlarmReceiver.class);
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
            PendingIntent pendingIntent =  PendingIntent.getBroadcast(context, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        }
    }

    public  ArrayList<PhoneContact> getAllPhoneContact(Context context){

        if(arr ==null){
            arr = new ArrayList<>();
            PhoneContact d = null;
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            Cursor cursor = context.getContentResolver().query(
                    uri,new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone._ID},null,null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+ " ASC");
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false){

                String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String  contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                int phoneContactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));

                d = new PhoneContact(contactName,contactNumber,phoneContactId);
                arr.add(d);

                d= null;
                cursor.moveToNext();
            }
            cursor.close();
            cursor = null;
        }
        return arr;
    }

}
