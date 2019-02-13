package com.yh.autocontrolwechat;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;


public class LongRunningService extends IntentService {
    private static int time;

    public LongRunningService() {
        super("LongRunningService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        int LongCheck = sp.getInt("LongCheck", 0);
        Log.e("监控", "LongRunningService get it");
        Log.e("监控", "LongCheck" + String.valueOf(LongCheck));
        if (LongCheck != 0) {
            if (LongCheck != 0) {
                Log.e("监控", "LongRunningService LongCheck Access");
                time = sp.getInt("time", 0);
                Log.e("监控", "间隔时间:" + String.valueOf(time));
                AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
                int times = time * 60 * 1000;
                long triggerAtTime = SystemClock.elapsedRealtime() + times;
                Intent i = new Intent(LongRunningService.this, AlarmReceiver.class);
                PendingIntent pi = PendingIntent.getBroadcast(LongRunningService.this, 0, i, 0);
                manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
            } else {
                return;
            }
        }


    }
}
//public class LongRunningService extends Service {
//    private static int time;
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//    @Override
//    public int onStartCommand(Intent intent,int flags,int startId){
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.e("监控","LongRunningService get it");
//                SharedPreferences sp = getSharedPreferences("data",MODE_PRIVATE);
//                time = sp.getInt("time",0);
//                Log.e("监控", "间隔时间:"+String.valueOf(time));
//                AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//                int times = time*60*500;
//                long triggerAtTime = SystemClock.elapsedRealtime()+times;
//                Intent i = new Intent(LongRunningService.this,AlarmReceiver.class);
//                PendingIntent pi = PendingIntent.getBroadcast(LongRunningService.this,0,i,0);
//                manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
//
//            }
//        }).start();
//
//        return super.onStartCommand(intent,flags,startId);
//    }
//}


