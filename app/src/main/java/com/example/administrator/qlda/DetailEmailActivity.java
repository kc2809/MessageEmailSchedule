package com.example.administrator.qlda;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.qlda.adapter.RecipentAdapter;
import com.example.administrator.qlda.database.MyDatabase;
import com.example.administrator.qlda.message.data.Data;
import com.example.administrator.qlda.message.data.EmailAccount;
import com.example.administrator.qlda.send.email.GMailSender;

import java.util.ArrayList;


public class DetailEmailActivity extends Activity implements Constant {
    TextView tvMessage,tvScheduleTime,tvStatus,tvFrom,tvSubject;
    ListView lvRecipent;
    ImageButton btnUndo,btnEdit,btnDelete,btnSend;

    ArrayList<String> myArr = new ArrayList<>();
    RecipentAdapter adapter;

    Data data;
    EmailAccount account;

    boolean somethingChange = false;
    MyDatabase myDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_email);

        addControls();

        getData();
        setData();
        addListener();

    }
    View.OnClickListener myClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnUndo:
                    undo();
                    break;
                case R.id.btnEdit:
                    edit();
                    break;
                case R.id.btnDelete:
                    deleteItem();
                    break;
                case R.id.btnSend:
                    send();
                    break;
            }
        }
    };

    private void send(){
        sendEmail();
    }

    private void edit() {
        Intent in = new Intent(this,EditEmailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("MESSAGECONTENT", data);
        in.putExtra("DATA", bundle);
        startActivityForResult(in, EDIT_EMAIL_REQUEST);
    }

    private void deleteItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure ? ").setIcon(R.drawable.trash1)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MyDatabase myDatabase = new MyDatabase(DetailEmailActivity.this);
                        myDatabase.getDatabase();
                        myDatabase.deleteMessageById(data.getId());
                        //if status = pending sequen alarm again
                        if (data.getTime().getStatus() == 0) {
                            Transfer.getInstance().sequenceAlarm(DetailEmailActivity.this);
                        }
                        Intent in = getIntent();
                        setResult(SOMETHING_CHANGE, in);
                        finish();
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

    private void undo() {
        if(somethingChange == true){
            Intent in = getIntent();
            setResult(SOMETHING_CHANGE,in);
        }
        finish();
    }

    private void addListener() {
        btnUndo.setOnClickListener(myClick);
        btnSend.setOnClickListener(myClick);
        btnDelete.setOnClickListener(myClick);
        btnEdit.setOnClickListener(myClick);
    }

    private void addControls() {
        tvMessage = (TextView)findViewById(R.id.tvMessage);
        tvScheduleTime = (TextView)findViewById(R.id.tvScheduleTime);
        tvStatus= (TextView)findViewById(R.id.tvStatus);
        lvRecipent = (ListView)findViewById(R.id.lvRecipent);
        btnUndo = (ImageButton)findViewById(R.id.btnUndo);
        btnEdit = (ImageButton)findViewById(R.id.btnEdit);
        btnDelete = (ImageButton)findViewById(R.id.btnDelete);
        btnSend = (ImageButton)findViewById(R.id.btnSend);
        tvFrom = (TextView)findViewById(R.id.tvFromObj);
        tvSubject = (TextView)findViewById(R.id.tvSubject);

        lvRecipent = (ListView)findViewById(R.id.lvRecipent);

    }

    private void getData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("DATA");
        data = (Data) bundle.getSerializable("MESSAGECONTENT");

        myDatabase = new MyDatabase(this);
        myDatabase.getDatabase();
    }

    private void setData(){
        String status=null;

        account= myDatabase.loadTbl_User();

        tvFrom.setText(account.getUsername());
        tvSubject.setText(data.getMessage().getSubject());
        tvMessage.setText(data.getMessage().getMessage());
        tvScheduleTime.setText(data.getTime().toString());
        if(data.getTime().getStatus() == 0){
            status = "PENDING";
        }
        else if(data.getTime().getStatus() == 1){
            status = "FINISHED";
        }
        else{
            status = "DISCARDED";
        }
        tvStatus.setText(status);


        myArr.add(data.getMessage().getTo());
        adapter = new RecipentAdapter(this,R.layout.recipent_item_layout,myArr);
        lvRecipent.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == EDIT_EMAIL_RESPONSE){
            Bundle bundle = data.getBundleExtra("DATA");
            Data newData = (Data)bundle.getSerializable("MESSAGECONTENT");
            tvSubject.setText(newData.getMessage().getMessage());
            tvMessage.setText(newData.getMessage().getMessage());
            tvScheduleTime.setText(newData.getTime().toString());
            tvStatus.setText("PENDING");

            System.out.println("----haha @@@"+ newData.getMessage().getTo());
            myArr.add(newData.getMessage().getTo());
            adapter = new RecipentAdapter(this,R.layout.recipent_item_layout,myArr);
            lvRecipent.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            somethingChange = true;
        }
    }

    private void sendEmail(){
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
                    Intent in = getIntent();
                    setResult(SOMETHING_CHANGE, in);
                    finish();
                }
                else{
                    //failed
                    Toast.makeText(getApplicationContext(),"EMAIL CAN NOT SEND \nPlease check your wifi",Toast.LENGTH_LONG).show();
                    myDatabase.updateMessageStatusById(2, data.getId());
                    finish();
                }
            }
        };
        myAsync.execute(account.getUsername(), account.getPassword(), data.getMessage().getSubject(),
                data.getMessage().getMessage(), data.getMessage().getTo());
    }

}
