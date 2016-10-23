package com.example.administrator.qlda;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.example.administrator.qlda.message.data.MyTime;

import java.util.Calendar;


public class EditEmailActivity extends ActionBarActivity implements Constant{
    EditText edtTo,edtMessage,edtSubject;
    TextView tvDate,tvTime,tvFrom;

    ImageButton imgBtnAdd,imgBtnUndo;
    Calendar cal;

    EmailAccount account;
    Data data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);


        addControls();
        addListener();

        getData();
    }

    private void addListener() {
        imgBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //        addData();
                updateData();
            }
        });

        imgBtnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
        edtTo = (EditText)findViewById(R.id.edtToObj);
        edtMessage = (EditText)findViewById(R.id.edtMessage);
        edtSubject= (EditText)findViewById(R.id.edtSubject);

        tvDate = (TextView)findViewById(R.id.tvDate);
        tvTime= (TextView)findViewById(R.id.tvTime);
        tvFrom = (TextView)findViewById(R.id.tvFrom);

        imgBtnAdd = (ImageButton)findViewById(R.id.btnOK);
        imgBtnUndo = (ImageButton)findViewById(R.id.btnUndo);
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


    private void updateData(){
        Intent intent = getIntent();
        String mess="";
        int check = isValidData();

        // if data is valid
        if( check == 0){

            //return data message
            //
            data.getMessage().setSubject(edtSubject.getText().toString());
            data.getMessage().setTo(edtTo.getText().toString());
            data.getMessage().setMessage(edtMessage.getText().toString());
            data.getTime().setDate(tvDate.getText().toString());
            data.getTime().setTime(tvTime.getTag().toString());
            data.getTime().setStatus(0);

            MyDatabase myDatabase = new MyDatabase(this);
            myDatabase.getDatabase();
            myDatabase.updateMessageByItem(data);
            // send back to detail message activity
            Bundle bundle = new Bundle();
            bundle.putSerializable("MESSAGECONTENT", data);
            intent.putExtra("DATA", bundle);
            setResult(EDIT_EMAIL_RESPONSE,intent);
            finish();

            mess = "Succeed";
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

    private int isValidData(){
        int check=0;
        if(tvFrom.getText().toString().trim().length() == 0){
            return 2;
        }
        if(edtTo.getText().toString().trim().length()==0){
            return 3;
        }
        if(!edtTo.getText().toString().contains("@")){
            return 4;
        }


        String tim = (String) tvTime.getTag();
        System.out.println("@@@@ --- "+tim);
        MyTime myTime = new MyTime(tvDate.getText()+"",tim,0,0);
        Calendar calendar= myTime.getDateTime();
        Calendar currentDate = Calendar.getInstance();
        currentDate.getTime();

        System.out.println("--@@ 1 " + tvTime.getText());
        System.out.println("--@@ 2 " + tvTime.getTag());
        System.out.println("--@@ " + currentDate);

        System.out.println("-----CURRENT 1 : " + currentDate.getTime() +" -n--- date 1: "+ calendar.getTime());
        System.out.println("-----CURRENT : " + currentDate.getTimeInMillis() +" --- date : "+ calendar.getTimeInMillis());
        // error alarm date < current date
        if(currentDate.getTimeInMillis()+ 10000 > calendar.getTimeInMillis()){
            return 1;
        }

        return check;
    }

    private void getData(){
        MyDatabase myDatabase = new MyDatabase(this);
        myDatabase.getDatabase();
        account= myDatabase.loadTbl_User();

        cal = Calendar.getInstance();
        Intent in = getIntent();
        Bundle bundle = in.getBundleExtra("DATA");
        //
        data = (Data)bundle.getSerializable("MESSAGECONTENT");

        tvFrom.setText(account.getUsername());

        edtSubject.setText(data.getMessage().getSubject());
        edtTo.setText(data.getMessage().getTo());
        edtMessage.setText(data.getMessage().getMessage());

        tvDate.setText(data.getTime().getDate());
        tvTime.setText(data.getTime().getAMTime());
        tvTime.setTag(data.getTime().getTime());
    }
}
