package com.example.administrator.qlda;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.qlda.message.data.EmailAccount;
import com.example.administrator.qlda.send.email.GMailSender;


public class LoginEmailActivity extends Activity implements Constant {
    EditText title,edtUsername,edtPass;
    Button btnCancel,btnOk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        addControls();
        addListenner();

        title.setEnabled(false);
        edtUsername.requestFocus();
    }

    private void addListenner() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnEmailAccount();
            }
        });
    }

    private void addControls() {
        title = (EditText)findViewById(R.id.edtEmailTitlt);
        edtUsername = (EditText)findViewById(R.id.edtUsername);
        edtPass = (EditText)findViewById(R.id.edtPass);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnOk = (Button)findViewById(R.id.btnOK);

    }

    public void returnEmailAccount(){
      final  EmailAccount account = new EmailAccount(edtUsername.getText().toString(),edtPass.getText().toString());

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
                    Toast.makeText(LoginEmailActivity.this, "WELCOME", Toast.LENGTH_LONG).show();
                    returnAccountToActivity(account);

                }
                else{
                    //failed
                    Toast.makeText(LoginEmailActivity.this,"Account is incorrect! \nTry Again",Toast.LENGTH_LONG).show();
                }
            }
        };
        myAsync.execute(account.getUsername(),account.getPassword(),"Sign up Message Sequence","",
                account.getUsername());

    }

    private void returnAccountToActivity(EmailAccount account) {
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("ACCOUNT",account);
        intent.putExtra("DATA", bundle);
        setResult(ADD_EMAIL_SUCCEED,intent);
        finish();
    }
}
