package com.example.administrator.qlda.receive.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.administrator.qlda.MainActivity;
import com.example.administrator.qlda.R;
import com.example.administrator.qlda.message.data.Data;

public class AlarmReceiver extends BroadcastReceiver {

    String message;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getBundleExtra("DATA");
        Data myData = (Data)bundle.getSerializable("MESSAGECONTENT");

        System.out.println("RECEVER : " + myData.toString());

        createNotification(context, myData.getMessage().getMessage(), myData.getTime().toString(),
                "Alert RECEIVER");


        Intent in = new Intent(context,SendMessageService.class);
        in.putExtra("data", bundle);
        context.startService(in);

    }


    private void createNotification(Context context, String msg, String msgText, String alert) {

        PendingIntent notificIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        Notification.Builder noti = new Notification.Builder(context)
                .setContentTitle(message )
                .setContentText(msgText)
                .setTicker(alert)
                .setSmallIcon(R.drawable.ic_launcher);

        noti.setContentIntent(notificIntent);
        noti.setDefaults(Notification.DEFAULT_SOUND);

        noti.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, noti.build());

    }
}
