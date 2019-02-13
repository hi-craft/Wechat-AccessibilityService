package com.yh.autocontrolwechat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationClickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //todo 跳转之前要处理的逻辑
        ControlService.closeWindow();
        if(ControlService.Mainthread != null){
            ControlService.Mainthread.interrupt();
        }
        if(ControlService.MainthreadFriend!=null){
            ControlService.MainthreadFriend.interrupt();
        }
        Intent i = new Intent();
        i.setClassName("com.yh.autocontrolwechat", "com.yh.autocontrolwechat.MainActivity");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
