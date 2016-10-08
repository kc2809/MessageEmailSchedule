package com.example.administrator.qlda.send.email;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class MyAsyncClass extends AsyncTask<String, Void, Integer> {
    Context context;
    GMailSender sender;
    String username,password;

    private boolean isSucceed;

    public MyAsyncClass(Context context,String username,String password){
        super();
        this.context = context;
        this.username= username;
        this.password = password;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        sender = new GMailSender(username,password);
    }

    @Override
    protected Integer doInBackground(String... mApi) {
        int checkError;

        try {
            // Add subject, Body, your mail Id, and receiver mail Id.
            //     sender.sendMail("Subject 2 ", "this is  body 222","zhuukhanhz@gmail.com", "zhuukhanhz@gmail.com");

                   sender.sendMail(mApi[1], mApi[2],username, mApi[0]);
            checkError = 0;
            isSucceed = true;
        }

        catch (Exception ex) {
            isSucceed = false;
            checkError = 1;
            ex.printStackTrace();
        }
        return checkError;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if(result == 0){
            Toast.makeText(context, "Email send", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(context, "Failed to Send Email", Toast.LENGTH_LONG).show();
        }
    }


}
