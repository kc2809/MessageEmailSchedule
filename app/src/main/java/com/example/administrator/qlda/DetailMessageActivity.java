package com.example.administrator.qlda;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.qlda.adapter.RecipentAdapter;
import com.example.administrator.qlda.database.MyDatabase;
import com.example.administrator.qlda.message.data.Data;

import java.util.ArrayList;


public class DetailMessageActivity extends ActionBarActivity implements Constant{
    TextView tvMessage,tvScheduleTime,tvStatus;
    ListView lvRecipent;
    ImageButton btnUndo,btnEdit,btnDelete,btnSend;

    ArrayList<String> myArr = new ArrayList<>();
    RecipentAdapter adapter;

    Data data;

    boolean somethingChange = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_message);

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
        if(Transfer.getInstance().sendData(this,data) == true){
            Intent in = getIntent();
            setResult(SOMETHING_CHANGE, in);
        }
        finish();
    }

    private void edit() {
        Intent in = new Intent(this,EditMessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("MESSAGECONTENT", data);
        in.putExtra("DATA", bundle);
        startActivityForResult(in, EDIT_REQUEST);
    }

    private void deleteItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure ? ").setIcon(R.drawable.trash1)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MyDatabase myDatabase = new MyDatabase(DetailMessageActivity.this);
                        myDatabase.getDatabase();
                        myDatabase.deleteMessageById(data.getId());
                        //if status = pending sequen alarm again
                        if (data.getTime().getStatus() == 0) {
                            Transfer.getInstance().sequenceAlarm(DetailMessageActivity.this);
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


        lvRecipent = (ListView)findViewById(R.id.lvRecipent);

    }

    private void getData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("DATA");
        data = (Data) bundle.getSerializable("MESSAGECONTENT");


    }

    private void setData(){
        String status=null;
        tvMessage.setText(data.getMessage().getMessage());
        tvScheduleTime.setText(data.getTime().toString());
        if(data.getTime().getStatus() == 0){
            status = "PENDING";
        }
        else if(data.getTime().getStatus() == 1){
            status = "FINISHED";
        }
        else{
            status = "DISCARED";
        }
        tvStatus.setText(status);


        myArr.add(data.getMessage().getTo());
        adapter = new RecipentAdapter(this,R.layout.recipent_item_layout,myArr);
        lvRecipent.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == EDIT_MESSAGE_RESPONSE){
            Bundle bundle = data.getBundleExtra("DATA");
            Data newData = (Data)bundle.getSerializable("MESSAGECONTENT");
            tvMessage.setText(newData.getMessage().getMessage());
            tvScheduleTime.setText(newData.getTime().toString());
            tvStatus.setText("PENDING");

            myArr.add(newData.getMessage().getTo());
            adapter = new RecipentAdapter(this,R.layout.recipent_item_layout,myArr);
            lvRecipent.setAdapter(adapter);

            somethingChange = true;
        }
    }
}
