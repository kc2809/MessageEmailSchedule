package com.example.administrator.qlda.receive.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.example.administrator.qlda.MainActivity;
import com.example.administrator.qlda.Transfer;
import com.example.administrator.qlda.database.MyDatabase;
import com.example.administrator.qlda.message.data.Data;
import com.example.administrator.qlda.message.data.EmailAccount;
import com.example.administrator.qlda.send.email.GMailSender;

public class SendMessageService extends Service {
    MyDatabase myDatabase;
    Data data;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
//        Date d = Calendar.getInstance().getTime();
//        SimpleDateFormat sdf = new SimpleDateFormat("hh::mm::ss a");
//        Toast.makeText(this, "CREATE.. " + sdf.format(d), Toast.LENGTH_LONG).show();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myDatabase = new MyDatabase(getApplicationContext());
        myDatabase.getDatabase();

        Bundle bundle = intent.getBundleExtra("data");
        data  = (Data)bundle.getSerializable("MESSAGECONTENT");

        if(data.getTime().getType()==0){
            Transfer.getInstance().sendData(getApplicationContext(), data);
            stopSelf();
        }
        else{
            sendEmail();
        }

      // sendData(data);
//        MyAsyncClass thread = new MyAsyncClass(getApplicationContext(),"zhuukhanhz@gmail.com","seshoumaru2");
//        thread.execute("zhuukhanhz@gmail.com", "test subject ", message);


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Transfer.getInstance().sequenceAlarm(getApplicationContext());
        MainActivity.isChanged = true;
        super.onDestroy();
    }



    private void sendEmail(){
        EmailAccount account = myDatabase.loadTbl_User();
        AsyncTask<String,Void,Integer> myAsync = new AsyncTask<String, Void, Integer>() {

            GMailSender sender;
            @Override
            protected Integer doInBackground(String... strings) {
                sender = new GMailSender(strings[0],strings[1]);

                int checkError;

                try {
                    // Add subject, Body, your mail Id, and receiver mail Id.
                    //     sender.sendMail("Subject 2 ", "this is  body 222","zhuukhanhz@gmail.com", "zhuukhanhz@gmail.com");

                    // subject, message, username , to email
                    sender.sendMail(strings[2], strings[3],strings[0], strings[4]);
                    checkError = 0;
                }

                catch (Exception ex) {
                    checkError = 1;
                    ex.printStackTrace();
                }
                return checkError;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                if(integer==0){
                    //succeed
                    Toast.makeText(getApplicationContext(), "Email Sent", Toast.LENGTH_LONG).show();
                    myDatabase.updateMessageStatusById(1, data.getId());

                }
                else{
                    //failed
                    Toast.makeText(getApplicationContext(),"EMAIL CAN NOT SEND \nPlease check your wifi",Toast.LENGTH_LONG).show();
                    myDatabase.updateMessageStatusById(2, data.getId());
                }
                stopSelf();
            }
        };
        myAsync.execute(account.getUsername(),account.getPassword(),data.getMessage().getSubject(),
                data.getMessage().getMessage(), data.getMessage().getTo());
    }
}
