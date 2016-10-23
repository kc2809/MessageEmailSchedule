package com.example.administrator.qlda;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.administrator.qlda.message.data.Data;
import com.example.administrator.qlda.message.data.MessageData;
import com.example.administrator.qlda.message.data.MyTime;
import com.example.administrator.qlda.message.data.PhoneContact;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class AddMessageActivity extends Activity implements Constant {
    AutoCompleteTextView edtTo;
    EditText     edtMessage;
    TextView tvDate,tvTime;

    ImageButton imgBtnAdd,imgBtnUndo;
    Calendar cal;

    ArrayList<PhoneContact> myArr;
    ArrayAdapter<PhoneContact> adapter;

    PhoneContact contactSelected=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

   //     overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_right);

        setContentView(R.layout.activity_add_message);

        addControls();

        setDataForAutoCompleteText();
        addListener();
        getDefaultInfo();
    }

    private void setDataForAutoCompleteText() {
        myArr = Transfer.getInstance().getAllPhoneContact(this);
        adapter = new ArrayAdapter<PhoneContact>(this,android.R.layout.simple_list_item_1,myArr);

        edtTo.setAdapter(adapter);
        edtTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                contactSelected = (PhoneContact) adapterView.getAdapter().getItem(i);
                edtTo.setText(contactSelected.getDisplayName());
            }
        });
    }

    private boolean checkIsValidNumber(){
        String s = edtTo.getText().toString();
        for(int i =0; i< myArr.size();++i){
            if(myArr.get(i).getDisplayName().equals(s)) return true;
        }


        return false;
    }

    private void addListener() {
        imgBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
            }
        });

        imgBtnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                setResult(CANCEL_ADD_MESSAGE, intent);
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
        edtTo = (AutoCompleteTextView)findViewById(R.id.edtTo);
        edtMessage = (EditText)findViewById(R.id.eddtMessage);
        tvDate = (TextView)findViewById(R.id.tvDate);
        tvTime= (TextView)findViewById(R.id.tvTime);
        imgBtnAdd = (ImageButton)findViewById(R.id.btnSend);
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

    private void addData(){
        String mess="";
        int check = isValidData();
        String to=null;
        String displayName;

        // if data is valid
        if( check == 0){
            Intent intent = getIntent();
            //
            if(checkIsValidNumber()==true){
                to = contactSelected.getPhoneNumber().toString();
                displayName = contactSelected.getDisplayName();
            }
            else{
                to = edtTo.getText().toString();
                displayName = edtTo.getText().toString();
            }

            //return data message
            MessageData messageData = new MessageData(to,null,edtMessage.getText().toString(),displayName);
            MyTime myTime = new MyTime(tvDate.getText()+"",tvTime.getTag()+"",0,0);
            Data myData = new Data(messageData,myTime);

            Bundle bundle = new Bundle();
            bundle.putSerializable("MessageContent",myData);
            intent.putExtra("DATA",bundle);

            setResult(ADD_MESSAGE_SUCCEED,intent);
            mess= "Add data successfully";
            finish();
        }
        else if(check == 2){
            mess= "Recipent is empty.";
        }
        else if(check == 3){
            mess = "Message is empty.";
        }
        else{
            mess= "The schedule time is smaller than current time";
        }
        Toast.makeText(this,mess,Toast.LENGTH_LONG).show();
    }

    private int isValidData(){

        if(edtTo.getText().toString().trim().length() == 0 ) return 2;
        if(edtMessage.getText().toString().trim().length() == 0 ) return 3;


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

        return 0;
    }
}
