package com.example.administrator.qlda.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.qlda.message.data.Data;
import com.example.administrator.qlda.message.data.EmailAccount;
import com.example.administrator.qlda.message.data.MessageData;
import com.example.administrator.qlda.message.data.MyTime;

import java.util.ArrayList;

public class MyDatabase {
    SQLiteDatabase database=null;
    Context context;
    public MyDatabase(Context context){
        this.context = context;
    }


    public SQLiteDatabase getDatabase(){
        database = context.openOrCreateDatabase("messdata.db", Context.MODE_PRIVATE,null);

        //if database exist
        if(database!=null) {
            //if table time exist
            if (isTableExist(database, "tbl_message_data")) {
                return database;
            } else {
               //create message data table
                createMessageTable();
                createUserTable();
            }

        }
        return database;
    }

    public boolean isTableExist(SQLiteDatabase database,String tableName){
        Cursor cursor = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName
                + "'",null);
        if(cursor!=null){
            if(cursor.getCount() >0){
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }
    //----------- CREATE TABLE
    public void createMessageTable(){
        String sql = "CREATE TABLE [tbl_message_data] (\n" +
                "[id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "[name] TEXT  NULL,\n" +
                "[toobj] TEXT  NULL,\n" +
                "[subject] TEXT  NULL,\n" +
                "[message] TEXT  NULL,\n" +
                "[date] DATE  NULL,\n" +
                "[time] TIME  NULL,\n" +
                "[type] INTEGER  NULL,\n" +
                "[status] INTEGER  NULL\n" +
                ")";
        database.execSQL(sql);
    }

    public void createUserTable(){
        String sql = "CREATE TABLE [tbl_user] (\n" +
                "[username] TEXT  NOT NULL PRIMARY KEY,\n" +
                "[password] TEXT  NULL\n" +
                ")";
        database.execSQL(sql);
    }

    /*---------------INSERT DATA-----------------
     *------------------------------------------
    */
    public boolean insertTbl_Message(Data data){
        ContentValues content = new ContentValues();
        //get message content
        MessageData messContent = data.getMessage();
        //get alarm time content
        MyTime time = data.getTime();

        //add to content
        content.put("toobj",messContent.getTo());
        content.put("message",messContent.getMessage());
        content.put("name",messContent.getDisplayName());
        if(messContent.getSubject() !=null){
            content.put("subject",messContent.getSubject());
        }

        //add time
        content.put("date",time.getDate());
        content.put("time",time.getTime());
        content.put("type",time.getType());
        content.put("status",time.getStatus());

        if(database.insert("tbl_message_data",null,content) == -1){
        //    Toast.makeText(context, "Insert DATA error", Toast.LENGTH_LONG).show();
            return false;
        }
        else{
         //   Toast.makeText(context, "Insert DATA successfully", Toast.LENGTH_LONG).show();
            return true;
        }
    }

    public boolean insertTbl_User(EmailAccount account){
        ContentValues content = new ContentValues();

        content.put("username",account.getUsername());
        content.put("password",account.getPassword());

        if(database.insert("tbl_user",null,content)== -1){
            // ERROR
            return false;
        }
        else{
            //SUCCESS
            return true;
        }
    }

    /*---------------LOAD DATA-----------------
  *------------------------------------------
 */
    //--------------------------------------------------------------
    //------ -1 to load all message
    //--- 0 load pending message
    //--- 1 load finisher message
    //---- 2 load discared message
    public ArrayList<Data> loadDataTbl_Message_Data(int clause){
        ArrayList<Data> arr =new ArrayList<>();
        Cursor cursor ;
        if(clause == -1){
            cursor   = database.query("tbl_message_data",null,null,null,null,null,"date ASC, time ASC");
        }
        else{
            cursor   = database.query("tbl_message_data",null,"status = "+clause,null,null,null,"date ASC, time ASC");
        }
        int count = cursor.getCount();
        if(count != 0){
            arr = new ArrayList();
            cursor.moveToFirst();
            while(cursor.isAfterLast() == false){
            //    MessageData messageData = new MessageData(cursor.getString(1), cursor.getString(2),cursor.getString(3));
                MessageData messageData = new MessageData(cursor.getString(2), cursor.getString(3),cursor.getString(4),
                        cursor.getString(1));

                MyTime tempTime = new MyTime(cursor.getString(5),cursor.getString(6), cursor.getInt(7), cursor.getInt(8));
                Data tempData = new Data(messageData,tempTime, Integer.parseInt(cursor.getString(0)));
                arr.add(tempData);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arr;
    }

    public EmailAccount loadTbl_User(){
        EmailAccount account = null;

        Cursor cursor;
        cursor = database.query("tbl_user",null,null,null,null,null,null);
        int count = cursor.getCount();
        if(count!=0){
            cursor.moveToFirst();
            account= new EmailAccount(cursor.getString(0),cursor.getString(1));
        }
        cursor.close();

        return account;
    }



    public boolean updateMessageStatusById(int status,int id){
        ContentValues values = new ContentValues();
        values.put("status", status);
        int ret = database.update("tbl_message_data",values,"id ="+id,null);
        if(ret==0){
            //failed
            return false;
        }
        else{
            return true;
        }
    }

    public void deleteAllTable_MessageData(){
        database.execSQL("delete from tbl_message_data");
        database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE name='tbl_message_data'");
    }

    public void deleteMessageById(int id){
        String sql = "delete from tbl_message_data where id = '"+id+ "'";
        database.execSQL(sql);
    }

    public void deleteAllTable_User(){
        database.execSQL("delete from tbl_user");
    }

    public boolean updateMessageByItem(Data data){
        ContentValues values = new ContentValues();
        values.put("name",data.getMessage().getDisplayName());
        values.put("toobj",data.getMessage().getTo());
        values.put("message",data.getMessage().getMessage());
        if(data.getMessage().getSubject() !=null){
            values.put("subject",data.getMessage().getSubject());
        }
        values.put("date",data.getTime().getDate());
        values.put("time",data.getTime().getTime());
        values.put("status",data.getTime().getStatus());

        int ret = database.update("tbl_message_data",values,"id ="+data.getId(),null);
        if(ret==0){
            //failed
            return false;
        }
        else{
            return true;
        }
    }
}
