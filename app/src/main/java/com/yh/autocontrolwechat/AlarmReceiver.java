package com.yh.autocontrolwechat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

//    if(MainActivity.loop){
//
        try {
            //初始化主程序是否已经开始执行 flag
            ControlService.begin = false;
            //标识数据由按钮发起
            SharedPreferences.Editor editor = context.getSharedPreferences("data", MODE_PRIVATE).edit();
            editor.putInt("isButton", 1);
            editor.commit();
            //文本框不为空,则将数据推入本地存储
            if (!MainActivity.TimeTostart.getText().toString().equals("")) {
                int time = Integer.parseInt(String.valueOf(MainActivity.TimeTostart.getText()));
                editor.putInt("time", time);
                editor.commit();
            }
            Log.e("监控", "AlarmReceiver get it");
            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context);
            builder.setContentInfo("WECHAT info")
                    .setContentText("定时任务即将在3秒后执行,请勿操作手机")
                    .setContentTitle("WECHAT")
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setSubText("通知")
                    .setTicker("定时任务即将在3秒后执行,请勿操作手机")
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("001","my_channel",NotificationManager.IMPORTANCE_DEFAULT);
                channel.enableLights(true);
                channel.setLightColor(Color.GREEN);
                channel.setShowBadge(true);
                manager.createNotificationChannel(channel);
                builder.setChannelId("001");
            }
            Notification n = builder.build();
            manager.notify(1,n);
            Thread.sleep(5000);
            WindowUtils.showPopupWindow(context);
            ControlService.Peopelpage = 0;
            ControlService.page = 0;
            ControlService.numbers = 1;
            Intent i = new Intent();
            i.setClassName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    MainActivity.button.performClick();
//                }
//            });
//            Thread.sleep(8000);
//            thread.start();
//            MainActivity.button.performClick();


//            MainActivity.diaoyongmainactivity();
        } catch (Exception e) {
            e.printStackTrace();
        }


//    Intent i = new Intent(context,MainActivity.);
//    context.startService(i);
//    }else{
//        return ;
//    }
    }

}


//    Intent i = new Intent(this,LongRunningService.class);
//    startService(i);
