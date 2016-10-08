package com.example.administrator.qlda.message.data;


import java.io.Serializable;
import java.util.Calendar;

public class MyTime implements Serializable {
    String date;
    String time;
    int type;
    int status;

    public MyTime(String date, String time, int type, int status) {
        this.date = date;
        this.time = time;
        this.type = type;
        this.status = status;
    }

    public MyTime() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String getAMTime(){
        String am=" AM";
        String strAm[] = time.split(":");
        int hourOfDay = Integer.parseInt(strAm[0]);
        int minute = Integer.parseInt(strAm[1]);
        if(hourOfDay>12){
            hourOfDay -=12;
           am = " PM";
        }


        return hourOfDay+":"+minute+am;
    }

    @Override
    public String toString() {
        return date+"        "+getAMTime();
    }

    public Calendar getDateTime(){
        Calendar calendar = Calendar.getInstance();
        //get date
        String strArrtmpDate[] = date.split("/");
        int dayOfMonth = Integer.parseInt(strArrtmpDate[0]);
        int monthOfYear = Integer.parseInt(strArrtmpDate[1])-1;
        int year = Integer.parseInt(strArrtmpDate[2]);

        int hourOfDay;
        int minute;
        //get time
//        String strAm[] = time.split(" ");
//        String strTempTime[] = strAm[0].split(":");
//        hourOfDay = Integer.parseInt(strTempTime[0]);
//        minute = Integer.parseInt(strTempTime[1]);
//
//        if(strAm[1].equals("PM")){
//            hourOfDay += 12;
//        }

        System.out.println("--- con me no: "+ time);
        String strAm[] = time.split(":");
        hourOfDay = Integer.parseInt(strAm[0]);
        minute = Integer.parseInt(strAm[1]);

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        System.out.println("-------------CALENDAR"+calendar.getTime().toString());
        return calendar;
    }
}
