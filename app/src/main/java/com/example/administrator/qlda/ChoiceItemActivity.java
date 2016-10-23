package com.example.administrator.qlda;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;


public class ChoiceItemActivity extends Activity implements Constant{
    LinearLayout smsLayout,gmailLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_item);

        smsLayout = (LinearLayout)findViewById(R.id.llSms);
        gmailLayout = (LinearLayout)findViewById(R.id.llGmail);

        smsLayout.setOnClickListener(myClick);
        gmailLayout.setOnClickListener(myClick);
    }

    View.OnClickListener myClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.llSms:
                    doSmsIntent();
                    break;
                case R.id.llGmail:
                    doGmailIntent();
                    break;
            }
        }
    };

    private void doGmailIntent() {
        Intent intent = new Intent(this,AddEmailActivity.class);
        startActivityForResult(intent, OPEN_ADD_ACTIVITY);

    }

    private void doSmsIntent() {
        Intent intent = new Intent(this,AddMessageActivity.class);
        startActivityForResult(intent, OPEN_ADD_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OPEN_ADD_ACTIVITY){
            switch (resultCode){
                case ADD_MESSAGE_SUCCEED:
                    returnData(data);
                    break;
                case CANCEL_ADD_MESSAGE:
                    finish();
                    break;
                case ADD_EMAIL_SUCCEED:
                    returnData(data);
            }
        }
    }

    private void returnData(Intent data) {
        Bundle bundle = data.getBundleExtra("DATA");
        Intent intent = getIntent();
        intent.putExtra("DATA",bundle);
        setResult(CLOSE_CHOICE_ACTIVITY,intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choice_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

