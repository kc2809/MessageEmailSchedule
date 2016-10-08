package com.example.administrator.qlda.message.data;

import java.io.Serializable;

/**
 * Created by Administrator on 9/25/2016.
 */
public class Data implements Serializable {
    MessageData message;
    MyTime time;
    int id;

    public Data(MessageData message, MyTime time, int id) {
        this.message = message;
        this.time = time;
        this.id = id;
    }

    public Data(MessageData message, MyTime time) {
        this.message = message;
        this.time = time;
        id= 0;
    }

    public Data() {
        id = 0;
    }

    public MessageData getMessage() {
        return message;
    }

    public void setMessage(MessageData message) {
        this.message = message;
    }

    public MyTime getTime() {
        return time;
    }

    public void setTime(MyTime time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "id = "+ id+ " - To: "+ message.getTo()+ "  Message: "+ message.getMessage();
    }
}
