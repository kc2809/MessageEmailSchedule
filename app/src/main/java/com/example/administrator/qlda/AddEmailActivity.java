package com.example.administrator.qlda;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.administrator.qlda.database.MyDatabase;
import com.example.administrator.qlda.message.data.Data;
import com.example.administrator.qlda.message.data.EmailAccount;
import com.example.administrator.qlda.message.data.MessageData;
import com.example.administrator.qlda.message.data.MyTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class AddEmailActivity extends ActionBarActivity implements Constant{
    MyDatabase database;

    TextView tvFrom;
    EditText edtToObj,edtSubject,edtMessage;
    TextView tvDate,tvTime;

    ImageButton btnUndo,btnOk;

    ImageButton btnAdd;

    EmailAccount account = null;
    Calendar cal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 //       overridePendingTransition(R.transition.fadein, R.transition.fadeout);

        setContentView(R.layout.activity_add_email);

        addControls();
        addListener();

        getDefaultInfo();
        getData();
    }

    View.OnClickListener myClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnAddEmail:
                    doAddEmail();
                    break;
                case R.id.btnUndo:
                    doUndo();
                    break;
                case R.id.btnOK:
                    returnData();
                    break;
            }
        }
    };

    private void doUndo(){
        Intent intent = getIntent();
        setResult(CANCEL_ADD_MESSAGE, intent);
        finish();
    }

    public void add(){
        Intent intent = getIntent();
        //return data message
        MessageData messageData = new MessageData(edtToObj.getText()+"",edtSubject.getText()+"",edtMessage.getText().toString(),
                edtToObj.getText()+"");
        MyTime myTime = new MyTime(tvDate.getText()+"",tvTime.getTag()+"",1,0);
        Data myData = new Data(messageData,myTime);

        Bundle bundle = new Bundle();
        bundle.putSerializable("MessageContent", myData);
        intent.putExtra("DATA", bundle);

        setResult(ADD_EMAIL_SUCCEED,intent);
        finish();
    }
    private void returnData(){
        String mess="";
        int check = isValidData();


        // if data is valid
        if( check == 0){
            if(edtSubject.getText().toString().trim().length()==0){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure to send this email without subject ? ")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                add();
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
            else{
                add();
            }

        }
        else if(check == 2){
            mess = "Account is empty. \nChoose an account.";
        }
        else if(check == 3){
            mess = "Recipient is empty.";
        }
        else if (check == 4) {
            mess = "Unrecognized recipient.";

        } else {
            mess = "The schedule time is smaller than current time";
        }
        if(check!=0){
            Toast.makeText(this, mess, Toast.LENGTH_LONG).show();
        }

    }

    private void doAddEmail() {

        if(account!=null){
            showAlertLogoutDialog();
        }
        else{
            startLoginActivity();
        }

    }

    public void startLoginActivity(){
        database.deleteAllTable_User();
        account = null;
        tvFrom.setText("");
        Intent in = new Intent(AddEmailActivity.this,LoginEmailActivity.class);
        startActivityForResult(in, OPEN_LOGIN_ACTIVITY);
    }

    private void addListener() {
        btnAdd.setOnClickListener(myClick);
        btnUndo.setOnClickListener(myClick);
        btnOk.setOnClickListener(myClick);

        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });

    }

    private void addControls() {
        btnAdd = (ImageButton)findViewById(R.id.btnAddEmail);
        tvFrom = (TextView)findViewById(R.id.tvFrom);
        edtToObj = (EditText)findViewById(R.id.edtToObj);
        edtSubject = (EditText)findViewById(R.id.edtSubject);
        edtMessage = (EditText)findViewById(R.id.edtMessage);
        tvDate = (TextView)findViewById(R.id.tvDate);
        tvTime = (TextView)findViewById(R.id.tvTime);
        btnUndo = (ImageButton)findViewById(R.id.btnUndo);
        btnOk   = (ImageButton)findViewById(R.id.btnOK);
    }

    private void getData(){
        database = new MyDatabase(this);
        database.getDatabase();

        account = database.loadTbl_User();
        if(account!=null){
            tvFrom.setText(account.getUsername());
        }
        else{
            tvFrom.setText("");
        }
    }

    private void showAlertLogoutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to log out ? ").setTitle("Log out...")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        startLoginActivity();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == ADD_EMAIL_SUCCEED){
                Bundle bundle = data.getBundleExtra("DATA");
                EmailAccount account = (EmailAccount)bundle.getSerializable("ACCOUNT");
                database.insertTbl_User(account);
                this.account = account;
                tvFrom.setText(account.getUsername());
        }
    }

    private void getDefaultInfo(){

        cal = Calendar.getInstance();
        SimpleDateFormat sdf = null;
        //day/month/year format
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String strDate = sdf.format(cal.getTime());

        // display on screen
        tvDate.setText(strDate);

        // time format
        sdf = new SimpleDateFormat("hh:mm a",Locale.getDefault());
        tvTime.setText(sdf.format(cal.getTime()));

        sdf = new SimpleDateFormat("HH:mm",Locale.getDefault());
        tvTime.setTag(sdf.format(cal.getTime()));
    }

    private void setTime(){
        //show time picker dialog
        TimePickerDialog.OnTimeSetListener callback = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // update text view when change applied
                if(hourOfDay>12){
                    tvTime.setText((hourOfDay - 12 )+":"+minute+ " PM");
                }
                else{
                    tvTime.setText(hourOfDay+":"+minute+" AM");
                }
                tvTime.setTag(hourOfDay+":"+minute);
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE,minute);
            }
        };

        // process time when open it
        String s = tvTime.getTag().toString();
        String strArrTmp[] = s.split(":");
        int hourOfDay = Integer.parseInt(strArrTmp[0]);
        int minute = Integer.parseInt(strArrTmp[1]);
        TimePickerDialog pic = new TimePickerDialog(this,callback,hourOfDay,minute,false);
        pic.show();
    }

    private void setDate(){
        //show date picker dialog
        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //update text view when change applied
                tvDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                cal.set(year,monthOfYear,dayOfMonth);
            }
        };

        //process date when open it
        String s = tvDate.getText()+"";
        String strArrtmp[] = s.split("/");
        int dayOfMonth = Integer.parseInt(strArrtmp[0]);
        int monthOfYear = Integer.parseInt(strArrtmp[1])-1;
        int year = Integer.parseInt(strArrtmp[2]);
        DatePickerDialog pic = new DatePickerDialog(this,callback,year,monthOfYear,dayOfMonth);
        pic.setTitle("Set Date");
        pic.show();
    }

    private int isValidData(){

        if(tvFrom.getText().toString().trim().length() == 0){
            return 2;
        }
        if(edtToObj.getText().toString().trim().length()==0){
            return 3;
        }
        if(!edtToObj.getText().toString().contains("@")){
            return 4;
        }


        String tim = (String) tvTime.getTag();
        System.out.println("@@@@ --- "+tim);
        MyTime myTime = new MyTime(tvDate.getText()+"",tim,0,0);
        Calendar calendar= myTime.getDateTime();
        Calendar currentDate = Calendar.getInstance();
        currentDate.getTime();

        // error alarm date < current date
        if(currentDate.getTimeInMillis()+ 10000 > calendar.getTimeInMillis()){
            return 1;
        }
        return 0;
    }
}
