package com.yh.autocontrolwechat;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import static com.yh.autocontrolwechat.WechatUtils.findViewId;

public class ControlService extends AccessibilityService {
    //微信布局ID前缀
    private static final String BaseLayoutId = "com.tencent.mm:id/";
    public static boolean begin = false;//true 进程开始执行,不再监听
    //微信版本                                7.0.3
    //搜索框id
    /**
     * 聊天界面
     */
    //更多功能按钮
    private String chatuimorebutton = "amh";
    /**
     * 收藏页面
     */

    private String shoucangtitle = "bb";
    /**
     * 群组列表页面
     */
    private String groupbuttomview = "b0p";
    private String grouplist = "mi";
    private String theleatestgrouplist = "n7";
    public static int page = 0;
    public static int Peopelpage = 0;
    private static List<AccessibilityNodeInfo> listglobal = new ArrayList();
    private static List<AccessibilityNodeInfo> Peoplelistglobal = new ArrayList();
    private static Boolean check = false;
    private static int index;
    public static int numbers = 1;

    /**
     * ui记录
     * 微信主页:com.tencent.mm.ui.LauncherUI
     * 微信聊天页面:com.tencent.mm.ui.chatting.ChattingUI
     * 微信群聊页面:android.widget.FrameLayout
     * 微信收藏页面:android.widget.FrameLayout
     * 微信点击收藏弹窗页面:com.tencent.mm.ui.widget.a.c
     * 微信收藏发送成功:com.tencent.mm.plugin.fav.ui.FavSelectUI
     */
    public static final String WECHAT_CLASS_LAUNCHUI = "com.tencent.mm.ui.LauncherUI";
    public static final String WECHAT_CLASS_CHATUI = "com.tencent.mm.ui.chatting.ChattingUI";
    public static final String WECHAT_GROUP_OR_COLLECTION = "android.widget.FrameLayout";
    public static final String WECHAT_COLLECTION_CLICK = "com.tencent.mm.ui.widget.a.c";
    public static final String WECHAT_SUCCESS_SEND = "com.tencent.mm.plugin.fav.ui.FavSelectUI";

    /**
     * 群发好友相关class
     */
    private String Peoplebuttomview = "b0p";
    private String theleatestPeoplelist = "ng";
    private String sendMessage = "cs";
    private Thread thread;
    //广播服务
    public static Intent i;
    public static Thread Mainthread;
    public static Thread MainthreadFriend;
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String className = event.getClassName().toString();
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        int isButton = sp.getInt("isButton", 0);
        switch (event.getEventType()) {
            //监听通知栏消息状态改变
//            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
//                if (WeChat_PNAME.equals(event.getPackageName().toString())) {
//                    //拉起微信
//                    sendNotifacationReply(event);
//                }
//                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                //验证事件是否已经开始执行
                if (begin) {
                    return;
                }
                //验证事件是否由button发起
                if (isButton == 0) {
                    return;
                }
                switch (className) {
                    //微信首页
                    case WECHAT_CLASS_LAUNCHUI:
                        //获取who标识
                        int who = sp.getInt("who", 0);
                        if (who == 0 && begin == false) {
                            Mainthread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    loopsend();
                                }
                            });

//                            MyThread my = new MyThread();
//                            thread = new Thread(my);
//                            thread.start();
                            AlertClick();
                            Mainthread.start();
//                            test();
                        } else if (who == 1 && begin == false) {
                            MainthreadFriend= new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    looppeople();
                                }
                            });
                            AlertClick();
                            MainthreadFriend.start();
                        }
                        break;
                    //微信聊天页面
                    case WECHAT_CLASS_CHATUI:
                        WechatUtils.findTextAndClick(this, "返回");
                        break;
                }
                break;
        }
    }

    //测试方法
    private void test() {
        try {
            begin = true;
            gotogrouplist();
            SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
            editor.putInt("LongCheck", 1);
            editor.commit();
            WechatUtils.findTextAndClick(this, "返回");
            Thread.sleep(500);
            WechatUtils.findTextAndClick(this, "返回");
            Thread.sleep(3000);
            Intent i = new Intent(ControlService.this, LongRunningService.class);
            startService(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onInterrupt() {

    }


    //主程序发送消息操作
    private void handleFlow_click() {
        try {
            Thread.sleep(550);

            WechatUtils.findViewIdAndClick(this, BaseLayoutId + chatuimorebutton);

            Thread.sleep(550);

            WechatUtils.findTextAndClick(this, "我的收藏");

            Thread.sleep(550);

            WechatUtils.findViewIdAndClick(this, BaseLayoutId + shoucangtitle);

            Thread.sleep(550);

            WechatUtils.findTextAndClick(this, "发送");
            ChangeNumbers(0);
//            closeWindow();
            Thread.sleep(500);

            WechatUtils.findTextAndClick(this, "返回");

            Thread.sleep(550);

            WechatUtils.findTextAndClick(this, "返回");

            Thread.sleep(550);


            WechatUtils.findTextAndClick(this, "返回");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void ChangeNumbers(final int str) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Log.e("监控", "i got it" + numbers);
//                Intent intent_Receiver = new Intent("com.application.mac.RECEIVER");
//                if (str.equals("group")) {
//                    intent_Receiver.putExtra(receive_show, "已成功发送" + numbers + "个群");
//                } else {
//                    intent_Receiver.putExtra(receive_show, "已成功发送" + numbers + "个好友");
//                }
//
//                sendBroadcast(intent_Receiver);

                WindowUtils.updateTextView(numbers, str);
//                Log.e("监控","线程即将开始休眠");
//                MyThread myThread = new MyThread();
//                myThread.pauseThread();
//                Log.e("监控","线程休眠");

//                new Handler().postDelayed(new Runnable() {
//                    public void run() {
//                        Log.e("监控","线程即将重启");
//                        MyThread myThread = new MyThread();
//                        myThread.resumeThread();
//                        Log.e("监控","线程重启");
//                    }
//                }, 5000);
            }
        }).start();

    }


    //进入群组列表
    private void gotogrouplist() {
        try {
            WechatUtils.findTextAndClick(this, "返回");

            Thread.sleep(200);
            WechatUtils.findTextAndClick(this, "返回");

            Thread.sleep(200);

            WechatUtils.findTextAndClick(this, "通讯录");

            Thread.sleep(100);

            //第二次点击通讯录,防止微信已经打开且通讯录滑到下方

            WechatUtils.findTextAndClick(this, "通讯录");

            Thread.sleep(500);

            WechatUtils.findTextAndClick(this, "群聊");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    private void loopsend() {
        try {
            begin = true;

            //重置flag,使Accessibility Service不再响应事件
            SharedPreferences.Editor editors = getSharedPreferences("data", MODE_PRIVATE).edit();
            editors.putInt("isButton", 0);
            editors.commit();
            //初始化操作,确保后续操作生效
            if (page == 0) {
                WechatUtils.findTextAndClick(this, "返回");

                Thread.sleep(200);
                WechatUtils.findTextAndClick(this, "返回");

                Thread.sleep(200);

                WechatUtils.findTextAndClick(this, "通讯录");

                Thread.sleep(100);

                //第二次点击通讯录,防止微信已经打开且通讯录滑到下方

                WechatUtils.findTextAndClick(this, "通讯录");

            }
            gotogrouplist();

            Thread.sleep(500);
            //翻页
            scrollup(page);

            Thread.sleep(500);
            //检查是否到达尾页
            check = findViewId(this, BaseLayoutId + groupbuttomview);

            Thread.sleep(400);
            //获取当前页群组列列表
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(BaseLayoutId + theleatestgrouplist);
            if (!check) {
                Log.i("监控", "进入if循环体");
                Log.i("监控", "list.size():" + (list.size() - 1));
                /*-----------默认会获取到第二页的第一项,所以移除最后一项------------*/
                list.remove(list.size() - 1);
                //listglobal空间释放
                listglobal.clear();
                /*-----------追加到全局list------------*/
                listglobal.addAll(list);
                /*---------------------------end------------------------*/

                Thread.sleep(400);

                //循环派发点击事件
                for (int i = 0; i < list.size(); i++) {
                    if (i != 0) {
                        gotogrouplist();

                        Thread.sleep(500);

                        scrollup(page);
                    }
                    Log.e("监控", "第" + (page + 1) + "次循环," + "第" + (i + 1) + "次子循环");
                    Thread.sleep(500);

                    AccessibilityNodeInfo nodeInfos = getRootInActiveWindow();
                    List<AccessibilityNodeInfo> lists = nodeInfos.findAccessibilityNodeInfosByViewId(BaseLayoutId + theleatestgrouplist);
                    WechatUtils.performClick(lists.get(i));

                    handleFlow_click();
                    Log.e("监控","TimeMiddle:"+MainActivity.TimeMiddle.getText().toString());
                    if (!MainActivity.TimeMiddle.getText().toString().equals("")) {
                        Thread.sleep(1000 * Integer.parseInt(MainActivity.TimeMiddle.getText().toString()));
                    }
                    numbers++;
                }

                Thread.sleep(200);
                //当前页不为最后一页,递归执行函数
                //页数自增
                page++;
                loopsend();

            } else {


                //当前页为最后一页
                /*----------------System.out.println("else循环体-----------------");-----------------------*/
                Log.e("监控", "进入else循环体");
                //只有一页
                if (page == 0) {
                    Thread.sleep(400);
                    for (int i = 0; i < list.size(); i++) {
                        if (i != 0) {
                            gotogrouplist();
                            Thread.sleep(500);
                        }
                        Log.e("监控", "第" + (page + 1) + "次循环," + "第" + (i + 1) + "次子循环");
                        Thread.sleep(500);

                        AccessibilityNodeInfo nodeInfos = getRootInActiveWindow();
                        List<AccessibilityNodeInfo> lists = nodeInfos.findAccessibilityNodeInfosByViewId(BaseLayoutId + theleatestgrouplist);
                        WechatUtils.performClick(lists.get(i));
                        handleFlow_click();
                        if (!MainActivity.TimeMiddle.getText().toString().equals("")) {
                            Thread.sleep(1000 * Integer.parseInt(MainActivity.TimeMiddle.getText().toString()));
                        }
                        numbers++;
                    }
                } else {
                    //定位尾页第一个不重复元素的下标
                    for (int i = 0; i < list.size(); i++) {
                        if (listglobal.get(listglobal.size() - 1).getText().toString().equals(list.get(i).getText().toString())) {
                            index = i;
                            index++;
                            break;
                        } else {
                            continue;
                        }
                    }
                    Log.e("监控", "定位尾页重复下标:" + index);
                    /*----------------标识到达尾页----------------*/
                    Thread.sleep(500);
                    //循环派发点击事件
                    for (int i = index; i < list.size(); i++) {
                        if (i != index) {
                            gotogrouplist();
                        }
                        Log.e("监控", "第" + (page + 1) + "次循环," + "第" + (i - index + 1) + "次子循环");
                        Thread.sleep(500);

                        scrollup(page);

                        Thread.sleep(500);

                        AccessibilityNodeInfo nodeInfoss = getRootInActiveWindow();
                        List<AccessibilityNodeInfo> listss = nodeInfoss.findAccessibilityNodeInfosByViewId(BaseLayoutId + theleatestgrouplist);
                        WechatUtils.performClick(listss.get(i));
                        handleFlow_click();

                        if (!MainActivity.TimeMiddle.getText().toString().equals("")) {
                            Thread.sleep(1000 * Integer.parseInt(MainActivity.TimeMiddle.getText().toString()));
                        }

                        numbers++;
                    }
                }

//                Toast.makeText(this, "程序循环结束", Toast.LENGTH_LONG).show();
                //页面初始化
                page = 0;

                Thread.sleep(500);

                WechatUtils.findTextAndClick(this, "返回");

                Thread.sleep(500);

                WechatUtils.findTextAndClick(this, "返回");


                //全局返回操作
                WechatUtils.performBack(this);

                Thread.sleep(500);
                closeWindow();
                //将

                if (!TextUtils.isEmpty(MainActivity.TimeTostart.getText())) {
                    int time = Integer.parseInt(String.valueOf(MainActivity.TimeTostart.getText()));
                    //标识开启广播服务
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putInt("LongCheck", 1);
                    editor.putInt("time", time);
                    editor.commit();
                    Log.e("监控", "进入setTime");

                    SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
                    int loop = sp.getInt("loop", 0);
                    if (loop == 0) {
                        MainActivity.TimeTostart.setText("");
                    }
                    //开启广播服务
                    Intent i = new Intent(ControlService.this, LongRunningService.class);
                    startService(i);
                    Toast.makeText(ControlService.this, "程序将于" + time + "分钟后重新开始执行.请保持程序处于后台状态", Toast.LENGTH_SHORT).show();

                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putInt("LongCheck", 0);
                    editor.commit();
                }
                /*---------------------------end------------------------*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

//    private void setTime() {
//        try {
//            MainActivity.TimeTostart.setText("");
//            SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
//            int time = sp.getInt("time", 0);
//            Intent i = new Intent(this, LongRunningService.class);
//            startService(i);
//            Toast.makeText(this, "程序将于" + time + "分钟后重新开始执行", Toast.LENGTH_SHORT).show();
//            begin = false;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }

    private void friendMessageSend() {
        try {
            Thread.sleep(550);

            WechatUtils.findViewIdAndClick(this, BaseLayoutId + sendMessage);

            Thread.sleep(550);

            WechatUtils.findViewIdAndClick(this, BaseLayoutId + chatuimorebutton);

            Thread.sleep(550);

            WechatUtils.findTextAndClick(this, "我的收藏");

            Thread.sleep(550);

            WechatUtils.findViewIdAndClick(this, BaseLayoutId + shoucangtitle);

            Thread.sleep(550);

            WechatUtils.findTextAndClick(this, "发送");
            ChangeNumbers(1);
//            WechatUtils.findTextAndClick(this, "取消");

            Thread.sleep(550);

            WechatUtils.findTextAndClick(this, "返回");

            Thread.sleep(550);

            WechatUtils.findTextAndClick(this, "返回");

            Thread.sleep(550);

            WechatUtils.findTextAndClick(this, "返回");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void AlertClick() {
        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(ControlService.this);
        builder.setContentInfo("WECHAT info")
                .setContentText("群发任务正在执行")
                .setContentTitle("WECHAT")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSubText("通知")
                .setAutoCancel(true)
                .setTicker("点击返回应用主界面")
                .setWhen(System.currentTimeMillis());
        Intent intent = new Intent(this.getApplicationContext(), NotificationClickReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("002", "my_channel2", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setShowBadge(true);
            manager.createNotificationChannel(channel);
            builder.setChannelId("002");
        }
        Notification n = builder.build();
        n.flags = n.flags | Notification.FLAG_ONGOING_EVENT;
        manager.notify(2, n);
    }

    private void looppeople() {
        try {
            begin = true;

            Thread.sleep(550);

            WechatUtils.findTextAndClick(this, "返回");

            Thread.sleep(550);

            WechatUtils.findTextAndClick(this, "通讯录");

            if (Peopelpage != 0) {
                scrollup(1);
            }

            Thread.sleep(550);

            check = findViewId(this, BaseLayoutId + Peoplebuttomview);

            Thread.sleep(550);
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(BaseLayoutId + theleatestPeoplelist);
            if (!check) {
                /*-----------默认会获取到第二页的第一项,所以移除最后一项------------*/
                list.remove(list.size() - 1);
                /*----------------------清空listglobal--------------------*/
                Peoplelistglobal.clear();
                /*-----------追加到全局list------------*/
                Peoplelistglobal.addAll(list);
                /*---------------------------end------------------------*/
                Thread.sleep(650);

                for (int i = 0; i < list.size(); i++) {
                    if (i != 0) {
                        WechatUtils.findTextAndClick(this, "通讯录");
                    }

                    Thread.sleep(550);

                    AccessibilityNodeInfo nodeInfoFuck = getRootInActiveWindow();
                    List<AccessibilityNodeInfo> listFuck = nodeInfoFuck.findAccessibilityNodeInfosByViewId(BaseLayoutId + theleatestPeoplelist);
                    WechatUtils.performClick(listFuck.get(i));
                    friendMessageSend();
                    if (!MainActivity.TimeMiddle.getText().toString().equals("")) {
                        Thread.sleep(1000 * Integer.parseInt(MainActivity.TimeMiddle.getText().toString()));
                    }
                    numbers++;
                }

                Thread.sleep(250);

                Peopelpage++;
                looppeople();
                return;
            } else {
                if (Peopelpage == 0) {
                    Thread.sleep(400);
                    for (int i = 0; i < list.size(); i++) {
                        if (i != 0) {
                            gotogrouplist();
                            Thread.sleep(500);
                        }
                        Log.e("监控", "第" + (page + 1) + "次循环," + "第" + (i + 1) + "次子循环");
                        Thread.sleep(500);

                        AccessibilityNodeInfo nodeInfos = getRootInActiveWindow();
                        List<AccessibilityNodeInfo> lists = nodeInfos.findAccessibilityNodeInfosByViewId(BaseLayoutId + theleatestPeoplelist);
                        WechatUtils.performClick(lists.get(i));
                        handleFlow_click();
                        if (!MainActivity.TimeMiddle.getText().toString().equals("")) {
                            Thread.sleep(1000 * Integer.parseInt(MainActivity.TimeMiddle.getText().toString()));
                        }
                        numbers++;
                    }
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        if (Peoplelistglobal.get(Peoplelistglobal.size() - 1).getText().toString().equals(list.get(i).getText().toString())) {
                            index = i;
                            index++;
                            break;
                        } else {
                            continue;
                        }
                    }
                    /*----------------标识到达尾页----------------*/

                    Thread.sleep(550);

                    for (int i = index; i < list.size(); i++) {
                        if (i != index) {
                            WechatUtils.findTextAndClick(this, "通讯录");
                        }

                        Thread.sleep(550);

                        scrollup(Peopelpage);

                        Thread.sleep(550);

                        AccessibilityNodeInfo nodeInfoFuck = getRootInActiveWindow();
                        List<AccessibilityNodeInfo> listFuck = nodeInfoFuck.findAccessibilityNodeInfosByViewId(BaseLayoutId + theleatestPeoplelist);
                        WechatUtils.performClick(listFuck.get(i));
                        friendMessageSend();
                        if (!MainActivity.TimeMiddle.getText().toString().equals("")) {
                            Thread.sleep(1000 * Integer.parseInt(MainActivity.TimeMiddle.getText().toString()));
                        }
                        numbers++;
                    }
                    Thread.sleep(500);
                }
//                Toast.makeText(this, "程序循环结束", Toast.LENGTH_SHORT).show();

                Peopelpage = 0;

                Thread.sleep(500);

                WechatUtils.findTextAndClick(this, "返回");

                Thread.sleep(500);

                WechatUtils.findTextAndClick(this, "返回");

                closeWindow();

                WechatUtils.performBack(this);

                if (!TextUtils.isEmpty(MainActivity.TimeTostart.getText())) {
                    int time = Integer.parseInt(String.valueOf(MainActivity.TimeTostart.getText()));
                    //标识开启广播服务
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putInt("LongCheck", 1);
                    editor.putInt("time", time);
                    editor.commit();
                    Log.e("监控", "进入setTime");

                    SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
                    int loop = sp.getInt("loop", 0);
                    if (loop == 0) {
                        MainActivity.TimeTostart.setText("");
                    }
                    //开启广播服务
                    i = new Intent(ControlService.this, LongRunningService.class);
                    startService(i);
                    Toast.makeText(getApplicationContext(), "程序将于" + time + "分钟后重新开始执行.请保持程序处于后台状态", Toast.LENGTH_SHORT).show();

                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putInt("LongCheck", 0);
                    editor.commit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    private class MyThread extends Thread {
//        private final Object lock = new Object();
//        private boolean pause = false;
//
//        /**
//         * 调用这个方法实现暂停线程
//         */
//        public void pauseThread() {
//            pause = true;
//        }
//
//        /**
//         * 调用这个方法实现恢复线程的运行
//         */
//        public void resumeThread() {
//            pause = false;
//            synchronized (lock) {
//                lock.notifyAll();
//            }
//        }
//
//        /**
//         * 注意：这个方法只能在run方法里调用，不然会阻塞主线程，导致页面无响应
//         */
//        void onPause() {
//            synchronized (lock) {
//                try {
//                    lock.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        @Override
//        public void run() {
//            super.run();
//            try {
//
//                // 让线程处于暂停等待状态
//                while (pause) {
//                    onPause();
//                }
//                try {
//                    loopsend();
//                } catch (Exception e) {
//                    //捕获到异常之后，执行break跳出循环
//                    e.printStackTrace();
//                }
//
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    private void scrollup(Integer page) {
        try {
            if (page == 0) {
                return;
            }
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            List<AccessibilityNodeInfo> listNode = nodeInfo.findAccessibilityNodeInfosByViewId(BaseLayoutId + grouplist);
            AccessibilityNodeInfo Node = listNode.get(0);

            for (int i = 0; i < page; i++) {
                Thread.sleep(500);
                Node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void closeWindow() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                WindowUtils.hidePopupWindow();
//                Intent intent = new Intent(getApplicationContext(), FxService.class);
//                stopService(intent);
            }
        }).start();
    }

    private void resetAndReturnApp() {
        ActivityManager activtyManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activtyManager.getRunningTasks(3);
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos) {
            if (this.getPackageName().equals(runningTaskInfo.topActivity.getPackageName())) {
                activtyManager.moveTaskToFront(runningTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                return;
            }
        }
    }
    /**
     * ANR in com.yh.autocontrolwechat
     *     PID: 414
     *     Reason: Broadcast of Intent { flg=0x14 cmp=com.yh.autocontrolwechat/.AlarmReceiver (has extras) }
     *     Load: 0.0 / 0.0 / 0.0
     *     cpu0{ online=1 scaling_cur_freq=1843200 cpuinfo_max_freq=1843200 cpuinfo_min_freq=633600 }
     *     cpu1{ online=1 scaling_cur_freq=1843200 cpuinfo_max_freq=1843200 cpuinfo_min_freq=633600 }
     *     cpu2{ online=1 scaling_cur_freq=1843200 cpuinfo_max_freq=1843200 cpuinfo_min_freq=633600 }
     *     cpu3{ online=1 scaling_cur_freq=1843200 cpuinfo_max_freq=1843200 cpuinfo_min_freq=633600 }
     *     cpu4{ online=1 scaling_cur_freq=1747200 cpuinfo_max_freq=2208000 cpuinfo_min_freq=1113600 }
     *     cpu5{ online=1 scaling_cur_freq=1747200 cpuinfo_max_freq=2208000 cpuinfo_min_freq=1113600 }
     *     cpu6{ online=1 scaling_cur_freq=2208000 cpuinfo_max_freq=2208000 cpuinfo_min_freq=1113600 }
     *     cpu7{ online=1 scaling_cur_freq=2208000 cpuinfo_max_freq=2208000 cpuinfo_min_freq=1113600 }
     *     CPU usage from 136472ms to 0ms ago (2018-12-18 19:41:01.821 to 2018-12-18 19:43:18.294) with 99% awake:
     *       32% 3969/com.tencent.mm: 25% user + 6.3% kernel / faults: 251094 minor 1957 major
     *         17% 3969/com.tencent.mm: 14% user + 2.3% kernel
     *         5.3% 8658/RenderThread: 3.9% user + 1.4% kernel
     *         2.7% 4539/mmt_hworker: 1.9% user + 0.7% kernel
     *         1.3% 3988/HeapTaskDaemon: 1.2% user + 0.1% kernel
     *         0.7% 3979/Jit thread pool: 0.5% user + 0.1% kernel
     *         0.1% 8721/ListDataLoader$: 0.1% user + 0% kernel
     *         0.1% 31949/Binder:3969_E: 0.1% user + 0% kernel
     *         0.1% 5008/default: 0% user + 0% kernel
     *         0.1% 9076/Binder:3969_6: 0.1% user + 0% kernel
     *         0.1% 1475/ImageEngine_des: 0% user + 0% kernel
     *       23% 1748/system_server: 16% user + 6.3% kernel / faults: 19261 minor 222 major
     *         5.3% 2247/android.anim: 4.3% user + 0.9% kernel
     *         2.4% 4010/Binder:1748_11: 1.9% user + 0.5% kernel
     *         2% 24172/Binder:1748_1E: 1.5% user + 0.4% kernel
     *         1.8% 2695/Binder:1748_A: 1.4% user + 0.4% kernel
     *         1.5% 12263/Binder:1748_16: 1.1% user + 0.3% kernel
     *         1.1% 1814/ActivityManager: 0.8% user + 0.3% kernel
     *         1% 11587/Binder:1748_15: 0.7% user + 0.2% kernel
     *         0.8% 1841/android.display: 0.5% user + 0.3% kernel
     *         0.7% 2252/SensorService: 0.3% user + 0.3% kernel
     *         0.6% 1748/system_server: 0.4% user + 0.2% kernel
     *       9.8% 765/surfaceflinger: 5.3% user + 4.4% kernel / faults: 1222 minor 29 major
     *         4.6% 765/surfaceflinger: 3.2% user + 1.4% kernel
     *         0.9% 874/EventThread: 0.2% user + 0.7% kernel
     *         0.6% 872/EventThread: 0.3% user + 0.3% kernel
     *         0.6% 3248/Binder:765_5: 0.3% user + 0.3% kernel
     *         0.5% 818/Binder:765_2: 0.2% user + 0.3% kernel
     *         0.5% 1408/Binder:765_3: 0.2% user + 0.3% kernel
     *         0.4% 815/Binder:765_1: 0.1% user + 0.3% kernel
     *         0.4% 1852/Binder:765_4: 0.1% user + 0.3% kernel
     *         0.3% 875/CheckJankThread: 0% user + 0.3% kernel
     *         0.3% 817/DispSync: 0.1% user + 0.1% kernel
     *       4.1% 737/android.hardware.graphics.composer@2.1-service: 2.2% user + 1.8% kernel / faults: 83 minor
     *         2.6% 30985/HwBinder:737_3: 1.5% user + 1% kernel
     *         1.3% 877/HwBinder:737_2: 0.6% user + 0.6% kernel
     *         0% 820/SDM_EventThread: 0% user + 0% kernel
     *       3.4% 25537/mdss_fb0: 0% user + 3.4% kernel
     *       3.4% 589/logd: 1.9% user + 1.4% kernel / faults: 684 minor 114 major
     *         1.9% 602/logd.writer: 1.3% user + 0.6% kernel
     *         1% 31759/logd.reader.per: 0.1% user + 0.9% kernel
     *         0.3% 600/logd.daemon: 0.3% user + 0% kernel
     *       2.8% 2440/com.android.systemui: 2.1% user + 0.7% kernel / faults: 2986 minor 81 major
     *         1.2% 2440/ndroid.systemui: 1% user + 0.1% kernel
     *         0.5% 2563/ANRMonitor: 0.3% user + 0.2% kernel
     *         0.4% 2786/RenderThread: 0.3% user + 0.1% kernel
     *         0.1% 2843/Binder:2440_5: 0.1% user + 0% kernel
     *         0.1% 2454/Binder:2440_1: 0.1% user + 0% kernel
     *         0% 2768/Binder:2440_4: 0% user + 0% kernel
     *         0% 3762/Binder:2440_6: 0% user + 0% kernel
     *         0% 2576/pool-1-thread-1: 0% us
     * 2018-12-18 19:43:18.563 1748-1814/? E/ActivityManager: er + 0% kernel
     *         0% 2587/SysUiBg: 0% user + 0% kernel
     *         0% 2580/VolumeDialogCon: 0% user + 0% kernel
     *       2.7% 728/android.hardware.audio@2.0-service: 0% user + 2.7% kernel / faults: 2 minor
     *         2.7% 10879/writer: 0.8% user + 1.9% kernel
     *       2.6% 1639/adbd: 0.6% user + 2% kernel / faults: 623 minor
     *         1.1% 1639/adbd: 0.3% user + 0.7% kernel
     *         1% 1711/->transport: 0.1% user + 0.9% kernel
     *         0.4% 1717/<-transport: 0.1% user + 0.3% kernel
     *       2.5% 96/system: 0% user + 2.5% kernel
     *       2.3% 2420/com.sohu.inputmethod.sogou.vivo: 2.2% user + 0.1% kernel / faults: 234 minor 5 major
     *       1.7% 1047/audioserver: 1.2% user + 0.5% kernel / faults: 34 minor 5 major
     *       1.6% 31197/com.netease.cloudmusic:play: 1.5% user + 0% kernel / faults: 5024 minor 390 major
     *       1.5% 414/com.yh.autocontrolwechat: 1.3% user + 0.2% kernel / faults: 7158 minor 2 major
     *       1.4% 742/android.hardware.sensors@1.0-service: 0.6% user + 0.7% kernel / faults: 9 minor 6 major
     *       1.3% 8873/com.tencent.mm:push: 0.9% user + 0.4% kernel / faults: 5296 minor 2489 major
     *       1.2% 81/smem_native_rpm: 0% user + 1.2% kernel
     *       1.1% 21108/kworker/u16:3: 0% user + 1.1% kernel
     *       1% 19938/kworker/u16:0: 0% user + 1% kernel
     *       1% 20705/kworker/u16:16: 0% user + 1% kernel
     *       0.9% 31757/logcat: 0.2% user + 0.6% kernel / faults: 11 minor
     *       0.9% 26320/com.wandoujia.eyepetizer: 0.4% user + 0.4% kernel / faults: 2315 minor 457 major
     *       0.9% 736/android.hardware.graphics.allocator@2.0-service: 0.1% user + 0.8% kernel / faults: 918 minor
     *       0.8% 2668/com.vivo.upslide: 0.5% user + 0.2% kernel / faults: 4612 minor 1265 major
     *       0.7% 19729/kworker/u17:0: 0% user + 0.7% kernel
     *       0.7% 2687/com.android.phone: 0.4% user + 0.3% kernel / faults: 3237 minor 42 major
     *       0.7% 4496/kworker/u17:2: 0% user + 0.7% kernel
     *       0.7% 295/kgsl_worker_thr: 0% user + 0.7% kernel
     *       0.6% 27511/kworker/u16:9: 0% user + 0.6% kernel
     *       0.6% 3652/irq/48-1008000.: 0% user + 0.6% kernel
     *       0.6% 750/vendor.qti.hardware.perf@1.0-service: 0.2% user + 0.4% kernel / faults: 1290 minor
     *       0.6% 7/rcu_preempt: 0% user + 0.6% kernel
     *       0.6% 401/mmc-cmdqd/0: 0% user + 0.6% kernel
     *       0.5% 1063/rild: 0.5% user + 0% kernel / faults: 588 minor 26 major
     *       0.4% 21114/kworker/u16:18: 0% user + 0.4% kernel
     *       0.4% 591/servicemanager: 0.1% user + 0.2% kernel / faults: 6 minor
     *       0.4% 77/smem_native_lpa: 0% user + 0.4% kernel
     *       0.3% 29242/kworker/0:2: 0% user + 0.3% kernel
     *       0.3% 28957/kworker/1:2: 0% user + 0.3% kernel
     *       0.3% 2650/com.vivo.daemonService: 0.1% user + 0.1% kernel / faults: 3699 minor 97 major
     *       0.2% 2599/com.vivo.pem: 0.1% user + 0.1% kernel / faults: 4192 minor 860 major
     *       0.2% 39/rcuop/4: 0% user + 0.2% kernel
     *       0.2% 746/android.hardware.wifi@1.0-service: 0% user + 0.1% kernel / faults: 1 minor
     *       0.2% 784/com.tencent.mm:appbrand0: 0.1% user + 0% kernel / faults: 992 minor 201 major
     *       0.2% 90/kcompactd0: 0% user + 0.2% kernel
     *       0.2% 984/com.tencent.mm:appbrand1: 0.1% user + 0% kernel / faults: 1016 minor 146 major
     *       0.2% 195/vsync_retire_wo: 0% user + 0.2% kernel
     *       0.2% 4203/com.vivo.rms: 0.1% user + 0.1% kernel / faults: 179 minor 1 major
     *       0.1% 741/android.hardware.power@1.0-service: 0% user + 0.1% kernel
     *       0.1% 12255/com.tencent.tim:MSF: 0.1% user + 0% kernel / faults: 2500 minor 596 major
     *       0.1% 53/rcuop/6: 0% user + 0.1% kernel
     *       0.1% 2111/com.vivo.contentcatcher: 0.1% user + 0% kernel / faults: 1457 minor 274 major
     *       0.1% 764/lmkd: 0% user + 0.1% kernel
     *       0.1% 46/rcuop/5: 0% user + 0.1% kernel
     *       0.1% 10/rcuop/0: 0% user + 0.1% kernel
     *       0.1% 78/lpass_smem_glin: 0% user + 0.1% kernel
     *       0.1% 7224/com.tencent.tim: 0.1% user + 0% kernel / faults: 2600 minor 797 major
     *       0.1% 156/kswapd0: 0% user + 0.1% kernel
     *       0.1% 397/cfinteractive: 0% user + 0.1% kernel
     *       0.1% 17952/com.bbk.launcher2: 0% user + 0% kernel / faults: 1994 minor 189 major
     *       0.1% 1//init: 0
     * 2018-12-18 19:43:18.563 1748-1814/? E/ActivityManager: .1% user + 0% kernel / faults: 42 minor 1 major
     *       0.1% 1015/jbd2/dm-2-8: 0% user + 0.1% kernel
     *       0.1% 3764/android.process.media: 0% user + 0% kernel / faults: 261 minor 120 major
     *       0.1% 31979/kworker/4:1: 0% user + 0.1% kernel
     *       0% 2747/com.vivo.smartmultiwindow: 0% user + 0% kernel / faults: 114 minor 6 major
     *       0% 3994/com.baidu.map.location: 0% user + 0% kernel / faults: 519 minor 11 major
     *       0% 60/rcuop/7: 0% user + 0% kernel
     *       0% 1228/msm_irqbalance: 0% user + 0% kernel / faults: 2 minor
     *       0% 31890/kworker/2:5: 0% user + 0% kernel
     *       0% 2612/.dataservices: 0% user + 0% kernel / faults: 508 minor 19 major
     *       0% 17593/com.iqoo.secure:remote: 0% user + 0% kernel / faults: 720 minor 9 major
     *       0% 31104/com.netease.cloudmusic: 0% user + 0% kernel / faults: 3951 minor 272 major
     *       0% 3007/android.process.acore: 0% user + 0% kernel / faults: 2173 minor 3 major
     *       0% 29037/kworker/u16:8: 0% user + 0% kernel
     *       0% 1045/vivo_daemon: 0% user + 0% kernel / faults: 77 minor
     *       0% 12740/com.tencent.tim:mail: 0% user + 0% kernel / faults: 609 minor 252 major
     *       0% 3/ksoftirqd/0: 0% user + 0% kernel
     *       0% 49/migration/6: 0% user + 0% kernel
     *       0% 56/migration/7: 0% user + 0% kernel
     *       0% 633/com.bbk.account: 0% user + 0% kernel / faults: 2381 minor 345 major
     *       0% 1058/netd: 0% user + 0% kernel / faults: 127 minor 2 major
     *       0% 1230/rild: 0% user + 0% kernel / faults: 280 minor 5 major
     *       0% 24098/com.tencent.mm:tools: 0% user + 0% kernel / faults: 161 minor 23 major
     *       0% 8/rcu_sched: 0% user + 0% kernel
     *       0% 18/rcuop/1: 0% user + 0% kernel
     *       0% 1607/com.vivo.secime.service: 0% user + 0% kernel / faults: 14 minor
     *       0% 9597/wlan_logging_th: 0% user + 0% kernel
     *       0% 9602/cds_mc_thread: 0% user + 0% kernel
     *       0% 24217/com.tencent.mm:toolsmp: 0% user + 0% kernel / faults: 174 minor 86 major
     *       0% 31646/kworker/5:0: 0% user + 0% kernel
     *       0% 15/ksoftirqd/1: 0% user + 0% kernel
     *       0% 25/rcuop/2: 0% user + 0% kernel
     *       0% 35/migration/4: 0% user + 0% kernel
     *       0% 42/migration/5: 0% user + 0% kernel
     *       0% 490/core_ctl/4: 0% user + 0% kernel
     *       0% 841/xlog: 0% user + 0% kernel / faults: 141 minor
     *       0% 1060/wificond: 0% user + 0% kernel / faults: 14 minor 2 major
     *       0% 2960/com.qualcomm.qcrilmsgtunnel: 0% user + 0% kernel / faults: 1293 minor 4 major
     *       0% 3740/com.vivo.assistant: 0% user + 0% kernel / faults: 1511 minor 163 major
     *       0% 3775/com.teslacoilsw.launcher: 0% user + 0% kernel / faults: 206 minor 40 major
     *       0% 3910/com.vivo.abe:bin: 0% user + 0% kernel / faults: 3010 minor 131 major
     *       0% 4217/com.google.android.gms.persistent: 0% user + 0% kernel / faults: 538 minor 15 major
     *       0% 7185/com.tencent.mm:exdevice: 0% user + 0% kernel / faults: 306 minor 86 major
     *       0% 31132/com.netease.cloudmusic:browser: 0% user + 0% kernel / faults: 766 minor 316 major
     *       0% 32254/com.iqoo.secure: 0% user + 0% kernel / faults: 1456 minor 478 major
     *       0% 36/ksoftirqd/4: 0% user + 0% kernel
     *       0% 740/android.hardware.memtrack@1.0-service: 0% user + 0% kernel
     *       0% 1062/media.codec: 0% user + 0% kernel / faults: 21 minor
     *       0% 17815/com.google.process.gservices: 0% user + 0% kernel / faults: 722 minor 84 major
     *       0% 20800/com.android.vending: 0% user + 0% kernel / faults: 224 minor 242 major
     *       0% 27212/kworker/6:1: 0% user + 0% kernel
     *       0% 32348/com.google.process.gapps: 0% user + 0% kernel / faults: 55 minor 15 major
     *       0% 32369/com.google.android.play.games.ui: 0% user + 0% kernel / faults: 1131 minor 254 major
     *       0% 11/rcuos/0: 0% user + 0% kernel
     *       0% 32/rcuop/3: 0% user + 0% kernel
     *       0% 40/rcuos/4: 0% user + 0% kernel
     *       0% 43/ksoftirqd/5: 0% user + 0% kernel
     *       0% 50/ksoftirqd/6: 0% user + 0% kernel
     *       0% 54/rcuos/6: 0% user + 0% kernel
     *       0% 74/msm_watchdog: 0% user + 0% kernel
     *       0% 75/smem_native_mps: 0% user + 0% kernel
     *       0% 76/mpss_smem_glink: 0% user + 0% kernel
     *       0% 438/irq/666-ima-rdy: 0%
     * 2018-12-18 19:43:18.563 1748-1814/? E/ActivityManager: user + 0% kernel
     *       0% 539/kworker/4:1H: 0% user + 0% kernel
     *       0% 541/kworker/0:1H: 0% user + 0% kernel
     *       0% 834/sensors.qcom: 0% user + 0% kernel / faults: 99 minor
     *       0% 1036/cnd: 0% user + 0% kernel / faults: 134 minor
     *       0% 1039/thermal-engine: 0% user + 0% kernel / faults: 162 minor
     *       0% 1044/zygote: 0% user + 0% kernel / faults: 430 minor
     *       0% 1059/storaged: 0% user + 0% kernel / faults: 344 minor 12 major
     *       0% 2634/com.qualcomm.qti.telephonyservice: 0% user + 0% kernel / faults: 822 minor 1 major
     *       0% 3706/com.vivo.pushservice: 0% user + 0% kernel / faults: 22 minor
     *       0% 3871/com.google.android.gms: 0% user + 0% kernel / faults: 35 minor
     *       0% 8972/kworker/3:5: 0% user + 0% kernel
     *       0% 9652/wpa_supplicant: 0% user + 0% kernel / faults: 92 minor
     *       0% 26397/com.wandoujia.eyepetizer:pushservice: 0% user + 0% kernel / faults: 1730 minor 247 major
     *       0% 27273/com.tencent.tim:Daemon: 0% user + 0% kernel / faults: 113 minor 90 major
     *       0% 31807/com.android.settings: 0% user + 0% kernel / faults: 121 minor 104 major
     *       0% 31886/kworker/7:0: 0% user + 0% kernel
     *       0% 31887/kworker/u16:12: 0% user + 0% kernel
     *      +0% 3250/com.iflytek.speechsuite: 0% user + 0% kernel
     *     13% TOTAL: 7.7% user + 4.5% kernel + 0.3% iowait + 0.5% irq + 0.2% softirq
     *     CPU usage from 35076547ms to 35076547ms ago (1970-01-01 08:00:00.000 to 1970-01-01 08:00:00.000) with 0% awake:
     *     0% TOTAL: 0% user + 0% kernel
     */
/**
 * 2018-12-20 17:16:31.411 1719-1757/? E/ActivityManager: ANR in com.yh.autocontrolwechat
 *     PID: 5781
 *     Reason: Broadcast of Intent { flg=0x14 cmp=com.yh.autocontrolwechat/.AlarmReceiver (has extras) }
 *     Load: 0.0 / 0.0 / 0.0
 *     cpu0{ online=1 scaling_cur_freq=1843200 cpuinfo_max_freq=1843200 cpuinfo_min_freq=633600 }
 *     cpu1{ online=1 scaling_cur_freq=1843200 cpuinfo_max_freq=1843200 cpuinfo_min_freq=633600 }
 *     cpu2{ online=1 scaling_cur_freq=1843200 cpuinfo_max_freq=1843200 cpuinfo_min_freq=633600 }
 *     cpu3{ online=1 scaling_cur_freq=1843200 cpuinfo_max_freq=1843200 cpuinfo_min_freq=633600 }
 *     cpu4{ online=1 scaling_cur_freq=2208000 cpuinfo_max_freq=2208000 cpuinfo_min_freq=1113600 }
 *     cpu5{ online=1 scaling_cur_freq=2208000 cpuinfo_max_freq=2208000 cpuinfo_min_freq=1113600 }
 *     cpu6{ online=1 scaling_cur_freq=2208000 cpuinfo_max_freq=2208000 cpuinfo_min_freq=1113600 }
 *     cpu7{ online=1 scaling_cur_freq=2208000 cpuinfo_max_freq=2208000 cpuinfo_min_freq=1113600 }
 *     CPU usage from 241896ms to 0ms ago (2018-12-20 17:12:29.200 to 2018-12-20 17:16:31.096):
 *       20% 1719/system_server: 14% user + 5.8% kernel / faults: 100222 minor 7767 major
 *         2.8% 2275/android.anim: 2.4% user + 0.4% kernel
 *         1.1% 2681/Binder:1719_5: 0.8% user + 0.3% kernel
 *         1.1% 1756/android.bg: 0.4% user + 0.7% kernel
 *         1.1% 2738/Binder:1719_A: 0.7% user + 0.3% kernel
 *         0.9% 8182/Binder:1719_1C: 0.6% user + 0.2% kernel
 *         0.8% 12687/Binder:1719_20: 0.6% user + 0.2% kernel
 *         0.8% 2735/Binder:1719_8: 0.6% user + 0.1% kernel
 *         0.7% 1757/ActivityManager: 0.4% user + 0.2% kernel
 *         0.6% 3880/Binder:1719_19: 0.4% user + 0.1% kernel
 *         0.6% 1758/android.ui: 0.4% user + 0.1% kernel
 *       15% 13250/com.tencent.mm: 12% user + 3.1% kernel / faults: 206574 minor 8161 major
 *         7.2% 13250/com.tencent.mm: 6% user + 1.1% kernel
 *         2.9% 14246/RenderThread: 2.2% user + 0.6% kernel
 *         1.7% 13271/mmt_hworker: 1.3% user + 0.4% kernel
 *         0.9% 13260/HeapTaskDaemon: 0.8% user + 0% kernel
 *         0.3% 14245/mmt_hClickFlow: 0.2% user + 0.1% kernel
 *         0.1% 13306/default: 0% user + 0% kernel
 *         0.1% 8027/Binder:13250_F: 0% user + 0% kernel
 *         0.1% 14307/ListDataLoader$: 0.1% user + 0% kernel
 *         0% 1384/Binder:13250_9: 0% user + 0% kernel
 *         0% 21336/Binder:13250_8: 0% user + 0% kernel
 *       7.8% 764/surfaceflinger: 4.5% user + 3.2% kernel / faults: 1900 minor 286 major
 *         4.2% 764/surfaceflinger: 2.9% user + 1.3% kernel
 *         0.6% 879/EventThread: 0.2% user + 0.4% kernel
 *         0.5% 877/EventThread: 0.3% user + 0.1% kernel
 *         0.4% 827/Binder:764_2: 0.1% user + 0.2% kernel
 *         0.3% 826/Binder:764_1: 0.2% user + 0.1% kernel
 *         0.3% 1031/Binder:764_3: 0.1% user + 0.2% kernel
 *         0.3% 3026/Binder:764_5: 0.1% user + 0.1% kernel
 *         0.3% 828/DispSync: 0.1% user + 0.1% kernel
 *         0.2% 2965/Binder:764_4: 0% user + 0.1% kernel
 *         0.2% 880/CheckJankThread: 0% user + 0.1% kernel
 *       5.2% 2447/com.sohu.inputmethod.sogou.vivo: 4.6% user + 0.6% kernel / faults: 26885 minor 5839 major
 *         3.7% 2447/thod.sogou.vivo: 3.4% user + 0.2% kernel
 *         1.3% 11105/RenderThread: 0.9% user + 0.3% kernel
 *         0% 2465/HeapTaskDaemon: 0% user + 0% kernel
 *         0% 2460/ReferenceQueueD: 0% user + 0% kernel
 *         0% 26382/Binder:2447_8: 0% user + 0% kernel
 *         0% 2454/Jit thread pool: 0% user + 0% kernel
 *         0% 2490/Binder:2447_3: 0% user + 0% kernel
 *         0% 2582/queued-work-loo: 0% user + 0% kernel
 *         0% 23101/Binder:2447_6: 0% user + 0% kernel
 *       3.2% 736/android.hardware.graphics.composer@2.1-service: 1.8% user + 1.3% kernel / faults: 432 minor 60 major
 *         2.5% 881/HwBinder:736_1: 1.4% user + 1% kernel
 *         0.5% 15361/HwBinder:736_3: 0.3% user + 0.2% kernel
 *         0.1% 830/SDM_EventThread: 0% user + 0% kernel
 *       2.7% 2467/com.android.systemui: 1.9% user + 0.7% kernel / faults: 31717 minor 3003 major
 *         1.1% 2467/ndroid.systemui: 0.7% user + 0.3% kernel
 *         0.5% 2828/RenderThread: 0.5% user + 0% kernel
 *         0.3
 * 2018-12-20 17:16:31.411 1719-1757/? E/ActivityManager: % 2591/ANRMonitor: 0.1% user + 0.1% kernel
 *         0.1% 2892/Binder:2467_5: 0% user + 0% kernel
 *         0% 2479/Binder:2467_1: 0% user + 0% kernel
 *         0% 31786/Binder:2467_E: 0% user + 0% kernel
 *         0% 10438/Binder:2467_8: 0% user + 0% kernel
 *         0% 2629/SysUiBg: 0% user + 0% kernel
 *         0% 2614/pool-1-thread-1: 0% user + 0% kernel
 *         0% 2815/Binder:2467_4: 0% user + 0% kernel
 *       2.4% 2945/mdss_fb0: 0% user + 2.4% kernel
 *       1.7% 594/logd: 1.1% user + 0.6% kernel / faults: 1858 minor 1117 major
 *         1.3% 604/logd.writer: 0.9% user + 0.3% kernel
 *         0.4% 5357/logd.reader.per: 0% user + 0.3% kernel
 *         0% 602/logd.daemon: 0% user + 0% kernel
 *       1.4% 16648/adbd: 0.3% user + 1% kernel / faults: 1294 minor 4 major
 *         0.6% 16648/adbd: 0.2% user + 0.3% kernel
 *         0.5% 5126/->transport: 0% user + 0.4% kernel
 *         0.2% 5127/<-transport: 0% user + 0.1% kernel
 *       1.1% 96/system: 0% user + 1.1% kernel
 *       1% 2720/com.android.phone: 0.6% user + 0.4% kernel / faults: 12308 minor 1527 major
 *       0.9% 22754/com.iqoo.secure:remote: 0.8% user + 0.1% kernel / faults: 15561 minor 2021 major
 *       0.9% 14446/com.tencent.mm:push: 0.6% user + 0.3% kernel / faults: 10363 minor 2554 major
 *       0.8% 22896/com.google.android.gms: 0.5% user + 0.3% kernel / faults: 13568 minor 2539 major
 *       0.8% 28170/com.iqoo.secure: 0.8% user + 0% kernel / faults: 16557 minor 1629 major
 *       0.8% 81/smem_native_rpm: 0% user + 0.8% kernel
 *       0.8% 26811/kworker/u16:0: 0% user + 0.8% kernel
 *       0.8% 293/kgsl_worker_thr: 0% user + 0.8% kernel
 *       0.7% 25419/com.tencent.tim: 0.5% user + 0.2% kernel / faults: 16088 minor 8762 major
 *       0.7% 399/mmc-cmdqd/0: 0% user + 0.7% kernel
 *       0.7% 2642/com.vivo.pem: 0.3% user + 0.4% kernel / faults: 4989 minor 1967 major
 *       0.7% 156/kswapd0: 0% user + 0.7% kernel
 *       0.7% 26813/kworker/u16:6: 0% user + 0.7% kernel
 *       0.6% 2708/com.vivo.upslide: 0.4% user + 0.2% kernel / faults: 14578 minor 1470 major
 *       0.6% 30192/kworker/u16:3: 0% user + 0.6% kernel
 *       0.6% 2625/kworker/u16:2: 0% user + 0.6% kernel
 *       0.5% 1066/rild: 0.4% user + 0% kernel / faults: 721 minor 261 major
 *       0.5% 4939/com.android.vending: 0.3% user + 0.1% kernel / faults: 11132 minor 2220 major
 *       0.5% 3637/irq/48-1008000.: 0% user + 0.5% kernel
 *       0.4% 5354/logcat: 0.1% user + 0.2% kernel / faults: 75 minor 10 major
 *       0.4% 26816/kworker/u16:16: 0% user + 0.4% kernel
 *       0.4% 735/android.hardware.graphics.allocator@2.0-service: 0% user + 0.3% kernel / faults: 1100 minor 9 major
 *       0.4% 595/servicemanager: 0.1% user + 0.2% kernel / faults: 13 minor 4 major
 *       0.4% 4277/com.google.android.gms.persistent: 0.2% user + 0.1% kernel / faults: 8095 minor 1620 major
 *       0.4% 17071/kworker/u16:21: 0% user + 0.4% kernel
 *       0.3% 3557/kworker/0:2: 0% user + 0.3% kernel
 *       0.3% 24257/com.tencent.tim:MSF: 0.2% user + 0.1% kernel / faults: 7386 minor 5052 major
 *       0.3% 23294/kworker/u16:10: 0% user + 0.3% kernel
 *       0.3% 741/android.hardware.sensors@1.0-service: 0.1% user + 0.2% kernel / faults: 200 minor 30 major
 *       0.3% 7/rcu_preempt: 0% user + 0.3% kernel
 *       0.3% 2495/kworker/u17:0: 0% user + 0.3% kernel
 *       0.3% 3079/android.process.acore: 0.2% user + 0.1% kernel / faults: 6908 minor 2342 major
 *       0.3% 154/rsc_zram: 0% user + 0.3% kernel
 *       0.3% 25139/com.google.process.gservices: 0.2% user + 0.1% kernel / faults: 3707 minor 1410 major
 *       0.3% 1055/installd: 0% user + 0.2% kernel / faults: 335 minor 29 major
 *       0.3% 3675/com.vivo.rms: 0.1% user + 0.1% kernel / faults: 723 minor 135 major
 *       0.3% 749/vendor.qti.hardware.perf@1.0-service: 0% user + 0.2% kernel / faults: 1163 minor 59 major
 *       0.2% 31629/kworker/1:0: 0% user + 0.2% kernel
 *       0.2% 14370/kworker/u17:2: 0% user + 0.2% kernel
 *       0.2% 26812/kworker/u16:4: 0% user + 0.2% kernel
 *       0.2% 2690/com.vivo.daemonService: 0.1% user + 0.1% kernel / faults: 4272 minor 816 major
 *       0.2% 26814/kworker/u16:9: 0% us
 * 2018-12-20 17:16:31.411 1719-1757/? E/ActivityManager: er + 0.2% kernel
 *       0.2% 28119/com.android.packageinstaller: 0.1% user + 0% kernel / faults: 8065 minor 604 major
 *       0.2% 23071/kworker/u17:3: 0% user + 0.2% kernel
 *       0.2% 3760/com.vivo.assistant: 0.1% user + 0% kernel / faults: 13786 minor 3473 major
 *       0.2% 740/android.hardware.power@1.0-service: 0% user + 0.1% kernel / faults: 13 minor 4 major
 *       0.1% 1015/jbd2/dm-2-8: 0% user + 0.1% kernel
 *       0.1% 2908/irq/258-synapti: 0% user + 0.1% kernel
 *       0.1% 19877/com.vivo.contentcatcher: 0% user + 0% kernel / faults: 2967 minor 1418 major
 *       0.1% 39/rcuop/4: 0% user + 0.1% kernel
 *       0.1% 10/rcuop/0: 0% user + 0.1% kernel
 *       0.1% 29030/com.tencent.mm:appbrand0: 0.1% user + 0% kernel / faults: 2656 minor 1234 major
 *       0.1% 31136/kworker/0:0: 0% user + 0.1% kernel
 *       0.1% 3001/kworker/1:2: 0% user + 0.1% kernel
 *       0.1% 32685/kworker/3:3: 0% user + 0.1% kernel
 *       0.1% 1277/msm_irqbalance: 0% user + 0% kernel / faults: 2 minor 4 major
 *       0.1% 395/cfinteractive: 0% user + 0.1% kernel
 *       0.1% 3981/com.vivo.abe:bin: 0% user + 0% kernel / faults: 4515 minor 848 major
 *       0.1% 1052/audioserver: 0% user + 0% kernel / faults: 737 minor 236 major
 *       0.1% 763/lmkd: 0% user + 0% kernel
 *       0% 1060/mediaserver: 0% user + 0% kernel / faults: 1318 minor 334 major
 *       0% 195/vsync_retire_wo: 0% user + 0% kernel
 *       0% 2802/com.vivo.smartmultiwindow: 0% user + 0% kernel / faults: 824 minor 59 major
 *       0% 727/android.hardware.audio@2.0-service: 0% user + 0% kernel / faults: 556 minor 371 major
 *       0% 9402/com.tencent.mm:exdevice: 0% user + 0% kernel / faults: 2325 minor 2179 major
 *       0% 1//init: 0% user + 0% kernel / faults: 52 minor 136 major
 *       0% 3/ksoftirqd/0: 0% user + 0% kernel
 *       0% 90/kcompactd0: 0% user + 0% kernel
 *       0% 745/android.hardware.wifi@1.0-service: 0% user + 0% kernel / faults: 139 minor 56 major
 *       0% 10294/com.tencent.mm:appbrand1: 0% user + 0% kernel / faults: 1834 minor 949 major
 *       0% 26216/com.vivo.browser: 0% user + 0% kernel / faults: 1482 minor 957 major
 *       0% 53/rcuop/6: 0% user + 0% kernel
 *       0% 24754/com.tencent.tim:mail: 0% user + 0% kernel / faults: 877 minor 475 major
 *       0% 27547/kworker/u17:4: 0% user + 0% kernel
 *       0% 3691/com.vivo.abe: 0% user + 0% kernel / faults: 2340 minor 367 major
 *       0% 4337/com.vivo.permissionmanager: 0% user + 0% kernel / faults: 2609 minor 95 major
 *       0% 15/ksoftirqd/1: 0% user + 0% kernel
 *       0% 46/rcuop/5: 0% user + 0% kernel
 *       0% 3726/com.bbk.iqoo.logsystem: 0% user + 0% kernel / faults: 3551 minor 357 major
 *       0% 25/rcuop/2: 0% user + 0% kernel
 *       0% 1062/netd: 0% user + 0% kernel / faults: 515 minor 115 major
 *       0% 3798/com.teslacoilsw.launcher: 0% user + 0% kernel / faults: 3528 minor 560 major
 *       0% 23599/com.bbk.appstore: 0% user + 0% kernel / faults: 3122 minor 321 major
 *       0% 1327/netmgrd: 0% user + 0% kernel / faults: 2037 minor 294 major
 *       0% 23015/com.bbk.launcher2: 0% user + 0% kernel / faults: 2386 minor 119 major
 *       0% 1065/media.codec: 0% user + 0% kernel / faults: 1102 minor 412 major
 *       0% 1699/kworker/u16:7: 0% user + 0% kernel
 *       0% 2401/kworker/4:1: 0% user + 0% kernel
 *       0% 3776/android.process.media: 0% user + 0% kernel / faults: 1003 minor 769 major
 *       0% 18/rcuop/1: 0% user + 0% kernel
 *       0% 1050/vivo_daemon: 0% user + 0% kernel / faults: 25 minor 20 major
 *       0% 1289/rild: 0% user + 0% kernel / faults: 283 minor 151 major
 *       0% 4190/com.baidu.map.location: 0% user + 0% kernel / faults: 1029 minor 283 major
 *       0% 4368/com.google.android.syncadapters.calendar: 0% user + 0% kernel / faults: 2232 minor 840 major
 *       0% 2653/.dataservices: 0% user + 0% kernel / faults: 899 minor 230 major
 *       0% 60/rcuop/7: 0% user + 0% kernel
 *       0% 14334/com.vivo.secime.service: 0% user + 0% kernel / faults: 372 minor 64 major
 *       0% 20596/com.tencent.mm:tools: 0% user + 0% kernel / faults: 1048 minor 389 major
 *       0% 2407/com.vivo.appfilter: 0% user +
 * 2018-12-20 17:16:31.411 1719-1757/? E/ActivityManager:  0% kernel / faults: 1797 minor 454 major
 *       0% 3000/kworker/2:1: 0% user + 0% kernel
 *       0% 20375/com.google.android.play.games.ui: 0% user + 0% kernel / faults: 1844 minor 842 major
 *       0% 1049/zygote: 0% user + 0% kernel / faults: 677 minor 148 major
 *       0% 3721/com.vivo.pushservice: 0% user + 0% kernel / faults: 1175 minor 389 major
 *       0% 28862/com.google.process.gapps: 0% user + 0% kernel / faults: 1288 minor 521 major
 *       0% 8/rcu_sched: 0% user + 0% kernel
 *       0% 32/rcuop/3: 0% user + 0% kernel
 *       0% 42/migration/5: 0% user + 0% kernel
 *       0% 56/migration/7: 0% user + 0% kernel
 *       0% 1796/cds_mc_thread: 0% user + 0% kernel
 *       0% 4196/com.google.process.gapps: 0% user + 0% kernel / faults: 1247 minor 509 major
 *       0% 22663/com.android.providers.calendar: 0% user + 0% kernel / faults: 920 minor 355 major
 *       0% 30241/com.vivo.email:eas: 0% user + 0% kernel / faults: 465 minor 323 major
 *       0% 22/ksoftirqd/2: 0% user + 0% kernel
 *       0% 438/irq/666-ima-rdy: 0% user + 0% kernel
 *       0% 739/android.hardware.memtrack@1.0-service: 0% user + 0% kernel / faults: 17 minor 41 major
 *       0% 1040/dpmQmiMgr: 0% user + 0% kernel / faults: 18 minor 19 major
 *       0% 5082/com.vivo.gallery: 0% user + 0% kernel / faults: 772 minor 481 major
 *       0% 29/ksoftirqd/3: 0% user + 0% kernel
 *       0% 49/migration/6: 0% user + 0% kernel
 *       0% 491/core_ctl/4: 0% user + 0% kernel
 *       0% 1047/zygote64: 0% user + 0% kernel / faults: 819 minor 40 major
 *       0% 1058/media.extractor: 0% user + 0% kernel / faults: 682 minor 294 major
 *       0% 2555/kworker/3:0: 0% user + 0% kernel
 *       0% 32221/kworker/5:2: 0% user + 0% kernel
 *       0% 17/kworker/1:0H: 0% user + 0% kernel
 *       0% 35/migration/4: 0% user + 0% kernel
 *       0% 540/kworker/0:1H: 0% user + 0% kernel
 *       0% 853/xlog: 0% user + 0% kernel / faults: 107 minor 23 major
 *       0% 21057/kworker/6:3: 0% user + 0% kernel
 *       0% 28264/com.android.defcontainer: 0% user + 0% kernel / faults: 726 minor 163 major
 *       0% 11/rcuos/0: 0% user + 0% kernel
 *       0% 36/ksoftirqd/4: 0% user + 0% kernel
 *       0% 40/rcuos/4: 0% user + 0% kernel
 *       0% 43/ksoftirqd/5: 0% user + 0% kernel
 *       0% 74/msm_watchdog: 0% user + 0% kernel
 *       0% 77/smem_native_lpa: 0% user + 0% kernel
 *       0% 78/lpass_smem_glin: 0% user + 0% kernel
 *       0% 596/hwservicemanager: 0% user + 0% kernel / faults: 174 minor 115 major
 *       0% 726/android.hidl.allocator@1.0-service: 0% user + 0% kernel / faults: 679 minor 110 major
 *       0% 755/healthd: 0% user + 0% kernel / faults: 83 minor 54 major
 *       0% 775/sensors.qcom: 0% user + 0% kernel / faults: 95 minor 41 major
 *       0% 850/wlan_logging_th: 0% user + 0% kernel
 *       0% 1081/dpmd: 0% user + 0% kernel / faults: 258 minor 16 major
 *       0% 1228/dpmd: 0% user + 0% kernel / faults: 172 minor 83 major
 *       0% 2394/wpa_supplicant: 0% user + 0% kernel / faults: 165 minor 45 major
 *       0% 2997/com.qualcomm.qcrilmsgtunnel: 0% user + 0% kernel / faults: 398 minor 70 major
 *       0% 3741/com.vivo.gamewatch: 0% user + 0% kernel / faults: 723 minor 188 major
 *       0% 4022/iptables-restore: 0% user + 0% kernel / faults: 66 minor 89 major
 *       0% 4025/ip6tables-restore: 0% user + 0% kernel / faults: 64 minor 101 major
 *       0% 5643/com.vivo.safecenter: 0% user + 0% kernel / faults: 551 minor 114 major
 *       0% 20967/com.vivo.doubleinstance: 0% user + 0% kernel / faults: 728 minor 209 major
 *       0% 23143/com.tencent.tim:Daemon: 0% user + 0% kernel / faults: 251 minor 48 major
 *       0% 26376/kworker/2:0: 0% user + 0% kernel
 *       0% 28334/com.android.settings: 0% user + 0% kernel / faults: 479 minor 326 major
 *       0% 28514/com.vivo.game: 0% user + 0% kernel / faults: 616 minor 171 major
 *       0% 29066/com.vivo.weather.provider: 0% user + 0% kernel / faults: 557 minor 133 major
 *       0% 26/rcuos/2: 0% user + 0% kernel
 *       0% 47/rcuos/5: 0% user + 0% kernel
 *       0% 75/smem_native_mps: 0% user + 0% kernel
 *       0% 76/mpss_smem_glink: 0% user + 0% kernel
 *       0% 217/hwrng: 0% user + 0% kerne
 * 2018-12-20 17:16:31.411 1719-1757/? E/ActivityManager: l
 *       0% 538/kworker/5:1H: 0% user + 0% kernel
 *       0% 555/kworker/7:1H: 0% user + 0% kernel
 *       0% 557/ueventd: 0% user + 0% kernel
 *       0% 567/verity_acc: 0% user + 0% kernel / faults: 25 minor 20 major
 *       0% 737/android.hardware.health@1.0-service: 0% user + 0% kernel / faults: 305 minor 109 major
 *       0% 744/android.hardware.vibrator@1.0-service: 0% user + 0% kernel / faults: 278 minor 97 major
 *       0% 753/vendor.vivo.hardware.bbkts@1.0-service: 0% user + 0% kernel / faults: 120 minor 4 major
 *       0% 1039/cnd: 0% user + 0% kernel / faults: 179 minor 70 major
 *       0% 1043/thermal-engine: 0% user + 0% kernel / faults: 49 minor 10 major
 *       0% 1056/keystore: 0% user + 0% kernel / faults: 105 minor 49 major
 *       0% 1059/media.metrics: 0% user + 0% kernel / faults: 175 minor 99 major
 *       0% 1064/wificond: 0% user + 0% kernel / faults: 326 minor 93 major
 *       0% 1138/lowi-server: 0% user + 0% kernel / faults: 409 minor 137 major
 *       0% 2674/com.qualcomm.qti.telephonyservice: 0% user + 0% kernel / faults: 241 minor 70 major
 *       0% 3514/com.android.documentsui: 0% user + 0% kernel / faults: 334 minor 196 major
 *       0% 4120/com.bbk.account: 0% user + 0% kernel / faults: 587 minor 237 major
 *       0% 4614/com.google.android.gms.feedback: 0% user + 0% kernel / faults: 373 minor 246 major
 *       0% 4894/com.google.android.gms.unstable: 0% user + 0% kernel / faults: 746 minor 271 major
 *       0% 12480/com.android.BBKClock: 0% user + 0% kernel / faults: 256 minor 108 major
 *       0% 25278/com.bbk.updater: 0% user + 0% kernel / faults: 206 minor 62 major
 *       0% 27239/com.bbk.cloud: 0% user + 0% kernel / faults: 304 minor 146 major
 *       0% 28421/com.vivo.hiboard: 0% user + 0% kernel / faults: 865 minor 251 major
 *       0% 28494/com.android.keychain: 0% user + 0% kernel / faults: 423 minor 10 major
 *       0% 29297/com.tencent.mm:sandbox: 0% user + 0% kernel / faults: 167 minor 41 major
 *       0% 32493/kworker/7:1: 0% user + 0% kernel
 *      +0% 5680/kworker/6:0: 0% user + 0% kernel
 *      +0% 5781/com.yh.autocontrolwechat: 0% user + 0% kernel
 *      +0% 5897/com.android.camera: 0% user + 0% kernel
 *      +0% 5921/kworker/2:2: 0% user + 0% kernel
 *      +0% 5948/com.tencent.mm:toolsmp: 0% user + 0% kernel
 *      +0% 6189/kworker/3:1: 0% user + 0% kernel
 *      +0% 6190/kworker/3:2: 0% user + 0% kernel
 *      +0% 6223/kworker/0:3: 0% user + 0% kernel
 *      +0% 6623/com.android.vending:instant_app_installer: 0% user + 0% kernel
 *      +0% 6770/kworker/u16:11: 0% user + 0% kernel
 *     11% TOTAL: 6.6% user + 4.1% kernel + 0.3% iowait + 0.5% irq + 0.2% softirq
 *     CPU usage from 31188829ms to 31188829ms ago (1970-01-01 08:00:00.000 to 1970-01-01 08:00:00.000) with 0% awake:
 *     0% TOTAL: 0% user + 0% kernel
 */
/**
 * 2018-12-23 19:47:33.132 8151-8151/com.yh.autocontrolwechat E/AndroidRuntime: FATAL EXCEPTION: main
 *     Process: com.yh.autocontrolwechat, PID: 8151
 *     android.view.WindowManager$BadTokenException: Unable to add window android.view.ViewRootImpl$W@9f7b39 -- permission denied for window type 2003
 *         at android.view.ViewRootImpl.setView(ViewRootImpl.java:822)
 *         at android.view.WindowManagerGlobal.addView(WindowManagerGlobal.java:356)
 *         at android.view.WindowManagerImpl.addView(WindowManagerImpl.java:93)
 *         at com.yh.autocontrolwechat.WindowUtils.showPopupWindow(WindowUtils.java:64)
 *         at com.yh.autocontrolwechat.MainActivity.openWindow(MainActivity.java:185)
 *         at com.yh.autocontrolwechat.MainActivity$3.onClick(MainActivity.java:148)
 *         at android.view.View.performClick(View.java:6597)
 *         at android.view.View.performClickInternal(View.java:6574)
 *         at android.view.View.access$3100(View.java:778)
 *         at android.view.View$PerformClick.run(View.java:25885)
 *         at android.os.Handler.handleCallback(Handler.java:873)
 *         at android.os.Handler.dispatchMessage(Handler.java:99)
 *         at android.os.Looper.loop(Looper.java:193)
 *         at android.app.ActivityThread.main(ActivityThread.java:6669)
 *         at java.lang.reflect.Method.invoke(Native Method)
 *         at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:493)
 *         at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:858)
 * 2018-12-23 19:47:36.129 1936-1956/system_process E/memtrack: Couldn't load memtrack module
 * 2018-12-23 19:47:36.781 1734-1760/? E/storaged: getDiskStats failed with result NOT_SUPPORTED and size 0
 * 2018-12-23 19:47:38.304 1721-1886/? E/IPCThreadState: binder thread pool (4 threads) starved for 2164 ms
 * 2018-12-23 19:47:40.764 7967-8059/com.google.android.googlequicksearchbox:search E/EntrySyncManager: Cannot determine account name: drop request
 * 2018-12-23 19:47:40.764 7967-8059/com.google.android.googlequicksearchbox:search E/NowController: Failed to access data from EntryProvider. ExecutionException.
 *     java.util.concurrent.ExecutionException: com.google.android.apps.gsa.sidekick.main.h.n: Could not complete scheduled request to refresh entries. ClientErrorCode: 3
 *         at com.google.common.util.concurrent.d.eA(SourceFile:85)
 *         at com.google.common.util.concurrent.d.get(SourceFile:23)
 *         at com.google.common.util.concurrent.l.get(SourceFile:2)
 *         at com.google.android.apps.gsa.staticplugins.nowstream.b.a.be.cbB(SourceFile:49)
 *         at com.google.android.apps.gsa.staticplugins.nowstream.b.a.be.cbA(SourceFile:181)
 *         at com.google.android.apps.gsa.staticplugins.nowstream.b.a.bh.run(Unknown Source:2)
 *         at com.google.android.apps.gsa.shared.util.concurrent.at.run(SourceFile:4)
 *         at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:458)
 *         at java.util.concurrent.FutureTask.run(FutureTask.java:266)
 *         at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:458)
 *         at java.util.concurrent.FutureTask.run(FutureTask.java:266)
 *         at com.google.android.apps.gsa.shared.util.concurrent.b.g.run(Unknown Source:4)
 *         at com.google.android.apps.gsa.shared.util.concurrent.b.aw.run(SourceFile:4)
 *         at com.google.android.apps.gsa.shared.util.concurrent.b.aw.run(SourceFile:4)
 *         at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
 *         at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
 *         at java.lang.Thread.run(Thread.java:764)
 *         at com.google.android.apps.gsa.shared.util.concurrent.b.i.run(SourceFile:6)
 *      Caused by: com.google.android.apps.gsa.sidekick.main.h.n: Could not complete scheduled request to refresh entries. ClientErrorCode: 3
 *         at com.google.android.apps.gsa.staticplugins.nowstream.b.a.ar.az(Unknown Source:4)
 *         at com.google.common.util.concurrent.q.ap(SourceFile:7)
 *         at com.google.common.util.concurrent.p.run(SourceFile:32)
 *         at com.google.common.util.concurrent.bt.execute(SourceFile:3)
 *         at com.google.common.util.concurrent.d.b(SourceFile:275)
 *         at com.google.common.util.concurrent.d.addListener(SourceFile:135)
 *         at com.google.common.util.concurrent.p.b(SourceFile:3)
 *         at com.google.android.apps.gsa.shared.util.concurrent.h.a(SourceFile:16)
 *         at com.google.android.apps.gsa.shared.util.concurrent.h.a(SourceFile:13)
 *         at com.google.android.apps.gsa.staticplugins.nowstream.b.a.be.cbB(SourceFile:47)
 *         at com.google.android.apps.gsa.staticplugins.nowstream.b.a.be.cbA(SourceFile:181) 
 *         at com.google.android.apps.gsa.staticplugins.nowstream.b.a.bh.run(Unknown Source:2) 
 *         at com.google.android.apps.gsa.shared.util.concurrent.at.run(SourceFile:4) 
 *         at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:458) 
 *         at java.util.concurrent.FutureTask.run(FutureTask.java:266) 
 *         at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:458) 
 *         at java.util.concurrent.FutureTask.run(FutureTask.java:266) 
 *         at com.google.android.apps.gsa.shared.util.concurrent.b.g.run(Unknown Source:4) 
 *         at com.google.android.apps.gsa.shared.util.concurrent.b.aw.run(SourceFile:4) 
 *         at com.google.android.apps.gsa.shared.util.concurrent.b.aw.run(SourceFile:4) 
 *         at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167) 
 *         at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641) 
 *         at java.lang.Thread.run(Thread.java:764) 
 *         at com.google.android.apps.gsa.shared.util.concurrent.b.i.run(SourceFile:6) 
 */
/**Android9.0
 * 2018-12-23 18:06:25.760 5796-5796/com.yh.autocontrolwechat E/AndroidRuntime: FATAL EXCEPTION: main
 *     Process: com.yh.autocontrolwechat, PID: 5796
 *     android.view.WindowManager$BadTokenException: Unable to add window android.view.ViewRootImpl$W@78ccf7c -- permission denied for window type 2003
 *         at android.view.ViewRootImpl.setView(ViewRootImpl.java:822)
 *         at android.view.WindowManagerGlobal.addView(WindowManagerGlobal.java:356)
 *         at android.view.WindowManagerImpl.addView(WindowManagerImpl.java:93)
 *         at com.yh.autocontrolwechat.WindowUtils.showPopupWindow(WindowUtils.java:64)
 *         at com.yh.autocontrolwechat.MainActivity.openWindow(MainActivity.java:185)
 *         at com.yh.autocontrolwechat.MainActivity$3.onClick(MainActivity.java:148)
 *         at android.view.View.performClick(View.java:6597)
 *         at android.view.View.performClickInternal(View.java:6574)
 *         at android.view.View.access$3100(View.java:778)
 *         at android.view.View$PerformClick.run(View.java:25885)
 *         at android.os.Handler.handleCallback(Handler.java:873)
 *         at android.os.Handler.dispatchMessage(Handler.java:99)
 *         at android.os.Looper.loop(Looper.java:193)
 *         at android.app.ActivityThread.main(ActivityThread.java:6669)
 *         at java.lang.reflect.Method.invoke(Native Method)
 *         at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:493)
 *         at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:858)
 * 2018-12-23 18:06:25.769 1601-1601/? E/lowmemorykiller: Error writing /proc/5796/oom_score_adj; errno=22
 * 2018-12-23 18:06:25.810 1945-2054/system_process E/InputDispatcher: channel '439aaed com.yh.autocontrolwechat/com.yh.autocontrolwechat.MainActivity (server)' ~ Channel is unrecoverably broken and will be disposed!
 *     java.util.concurrent.ExecutionException: com.google.android.apps.gsa.sidekick.main.h.n: Could not complete scheduled request to refresh entries. ClientErrorCode: 3
 *         at com.google.common.util.concurrent.d.eA(SourceFile:85)
 *         at com.google.common.util.concurrent.d.get(SourceFile:23)
 *         at com.google.common.util.concurrent.l.get(SourceFile:2)
 *         at com.google.android.apps.gsa.staticplugins.nowstream.b.a.be.cbB(SourceFile:49)
 *         at com.google.android.apps.gsa.staticplugins.nowstream.b.a.be.cbA(SourceFile:181)
 *         at com.google.android.apps.gsa.staticplugins.nowstream.b.a.bh.run(Unknown Source:2)
 *         at com.google.android.apps.gsa.shared.util.concurrent.at.run(SourceFile:4)
 *         at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:458)
 *         at java.util.concurrent.FutureTask.run(FutureTask.java:266)
 *         at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:458)
 *         at java.util.concurrent.FutureTask.run(FutureTask.java:266)
 *         at com.google.android.apps.gsa.shared.util.concurrent.b.g.run(Unknown Source:4)
 *         at com.google.android.apps.gsa.shared.util.concurrent.b.aw.run(SourceFile:4)
 *         at com.google.android.apps.gsa.shared.util.concurrent.b.aw.run(SourceFile:4)
 *         at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
 *         at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
 *         at java.lang.Thread.run(Thread.java:764)
 *         at com.google.android.apps.gsa.shared.util.concurrent.b.i.run(SourceFile:6)
 *      Caused by: com.google.android.apps.gsa.sidekick.main.h.n: Could not complete scheduled request to refresh entries. ClientErrorCode: 3
 *         at com.google.android.apps.gsa.staticplugins.nowstream.b.a.ar.az(Unknown Source:4)
 *         at com.google.common.util.concurrent.q.ap(SourceFile:7)
 *         at com.google.common.util.concurrent.p.run(SourceFile:32)
 *         at com.google.common.util.concurrent.bt.execute(SourceFile:3)
 *         at com.google.common.util.concurrent.d.b(SourceFile:275)
 *         at com.google.common.util.concurrent.d.addListener(SourceFile:135)
 *         at com.google.common.util.concurrent.p.b(SourceFile:3)
 *         at com.google.android.apps.gsa.shared.util.concurrent.h.a(SourceFile:16)
 *         at com.google.android.apps.gsa.shared.util.concurrent.h.a(SourceFile:13)
 *         at com.google.android.apps.gsa.staticplugins.nowstream.b.a.be.cbB(SourceFile:47)
 *         at com.google.android.apps.gsa.staticplugins.nowstream.b.a.be.cbA(SourceFile:181) 
 *         at com.google.android.apps.gsa.staticplugins.nowstream.b.a.bh.run(Unknown Source:2) 
 *         at com.google.android.apps.gsa.shared.util.concurrent.at.run(SourceFile:4) 
 *         at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:458) 
 *         at java.util.concurrent.FutureTask.run(FutureTask.java:266) 
 *         at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:458) 
 *         at java.util.concurrent.FutureTask.run(FutureTask.java:266) 
 *         at com.google.android.apps.gsa.shared.util.concurrent.b.g.run(Unknown Source:4) 
 *         at com.google.android.apps.gsa.shared.util.concurrent.b.aw.run(SourceFile:4) 
 *         at com.google.android.apps.gsa.shared.util.concurrent.b.aw.run(SourceFile:4) 
 *         at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167) 
 *         at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641) 
 *         at java.lang.Thread.run(Thread.java:764) 
 *         at com.google.android.apps.gsa.shared.util.concurrent.b.i.run(SourceFile:6) 
 */
//    private void findViewIdAndClick(AccessibilityService accessibilityService, String id) {
//        ids = id;
//        AccessibilityNodeInfo accessibilityNodeInfo = accessibilityService.getRootInActiveWindow();
//        if (accessibilityNodeInfo == null) {
//            try {
//                Thread.sleep(100);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            findViewIdAndClick(this, ids);
//            return;
//        }
//
//        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
//        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
//            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
//                if (nodeInfo != null) {
//                    performClick(nodeInfo);
//                    ids = "";
//                    break;
//                } else {
//                    try {
//                        Thread.sleep(100);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    findViewIdAndClick(this, ids);
//                }
//            }
//        }
//    }
//
//    private void performClick(AccessibilityNodeInfo nodeInfo) {
//        if (nodeInfo == null) {
//            try {
//                Thread.sleep(100);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            performClick(nodeInfo);
//            return;
//        }
//        if (nodeInfo.isClickable()) {
//            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//        } else {
//            performClick(nodeInfo.getParent());
//        }
//    }
//
//    private void findTextAndClick(AccessibilityService accessibilityService, String text) {
//        texts = text;
//        AccessibilityNodeInfo accessibilityNodeInfo = accessibilityService.getRootInActiveWindow();
//        if (accessibilityNodeInfo == null) {
//            try {
//                Thread.sleep(100);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            findTextAndClick(this, texts);
//            return;
//        }
//
//        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
//        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
//            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
//                if (nodeInfo != null && (text.equals(nodeInfo.getText()) || text.equals(nodeInfo.getContentDescription()))) {
//                    if (nodeInfo != null) {
//                        performClick(nodeInfo);
//                        texts = "";
//                        break;
//                    } else {
//                        try {
//                            Thread.sleep(100);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        findTextAndClick(this, texts);
//                    }
//                }
//            }
//        }
//    }

    //    private void handleFlow_past() {
//        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
//        if (nodeInfo != null) {
//            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(BaseLayoutId + searchedittextid);
//            if (list != null && list.size() > 0) {
//                for (final AccessibilityNodeInfo node : list) {
//                    if (node.getClassName().equals("android.widget.EditText") && node.isEnabled()) {
//                        try {
//                            Thread.sleep(350);
//
//                            WechatUtils.pastContent(this, node, PinYinUtil.getPinYinUtil().getStringPinYin(WechatUtils.NAME));
//
//                            Thread.sleep(500);
//
//                            clickSearchResult();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * 点击搜索到的结果
//     */
//    private void clickSearchResult() {
//        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
//        if (nodeInfo != null) {
//            List<AccessibilityNodeInfo> list1 = nodeInfo.findAccessibilityNodeInfosByViewId(BaseLayoutId + searchlistviewid);
//            if (list1 != null && list1.size() > 0) {
//                AccessibilityNodeInfo listInfo = list1.get(0);
//                for (int i = 0; i < listInfo.getChildCount(); i++) {
//                    AccessibilityNodeInfo itemNodeInfo = listInfo.getChild(i);
//                    for (int j = 0; j < itemNodeInfo.getChildCount(); j++) {
//                        CharSequence name = itemNodeInfo.getChild(j).getText();
//                        Log.i(TAG, "childName:" + name);
//                        if (!TextUtils.isEmpty(name)
//                                && TextUtils.equals(PinYinUtil.getPinYinUtil().getStringPinYin(name.toString()),
//                                PinYinUtil.getPinYinUtil().getStringPinYin(WechatUtils.NAME))) {
//                            itemNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                            return;
//                        }
//                    }
//                }
//            }
//        }
//        WechatUtils.NAME = "";
//        WechatUtils.CONTENT = "";
//        isSendSuccess = true;
//        Log.i(TAG, "没有找到联系人");
//        try {
//            // 没找到联系人，一定是在搜索页面，这时候要先点一次返回 退出搜索页面，然后在退出微信
//            // 防止直接退出微信，下一次发微信直接调起微信显示搜索页面，这时候粘贴内容就跟上一次的内容追加了，结果就不是想要的了
//            Thread.sleep(100);
//            WechatUtils.findTextAndClick(this, "返回");
//            Thread.sleep(200);
//            sendBroadcast(new Intent("FIND_CONTANCT_RESULT"));
//            resetAndReturnApp();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private void handleFlow_ChatUI() {
//
//        //如果微信已经处于聊天界面，需要判断当前联系人是不是需要发送的联系人
//        String curUserName = WechatUtils.findTextById(this, BaseLayoutId + chatuiusernameid);
//        if (!TextUtils.isEmpty(curUserName)
//                && TextUtils.equals(PinYinUtil.getPinYinUtil().getStringPinYin(curUserName),
//                PinYinUtil.getPinYinUtil().getStringPinYin(WechatUtils.NAME))) {
//            WechatUtils.NAME = "";
//            if (TextUtils.isEmpty(WechatUtils.CONTENT)) {
//                if (WechatUtils.findViewId(this, BaseLayoutId + chatuiedittextid)) {
//                    //当前页面可能处于发送文字状态，需要切换成发送文本状态
//                    WechatUtils.findViewIdAndClick(this, BaseLayoutId + chatuiswitchid);
//                }
//                isSendSuccess = true;
//                return;
//            }
//            if (WechatUtils.findViewByIdAndPasteContent(this, BaseLayoutId + chatuiedittextid, WechatUtils.CONTENT)) {
//                sendContent();
//            } else {
//                //当前页面可能处于发送语音状态，需要切换成发送文本状态
//                WechatUtils.findViewIdAndClick(this, BaseLayoutId + chatuiswitchid);
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                if (WechatUtils.findViewByIdAndPasteContent(this, BaseLayoutId + chatuiedittextid, WechatUtils.CONTENT))
//                    sendContent();
//            }
//        } else {
//            //回到主界面
//            WechatUtils.findTextAndClick(this, "返回");
//
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            WechatUtils.findTextAndClick(this, "返回");//再次点击返回，目的是防止上一次返回到搜索页面，那样就阻塞住了
//        }
//    }
//
//    private void sendContent() {
//        //发送成功   能执行这一步，基本上就是发出去了
//        WechatUtils.findTextAndClick(this, "发送");
//        WechatUtils.NAME = "";
//        WechatUtils.CONTENT = "";
//        isSendSuccess = true;
//
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 拉起微信界面
//     *
//     * @param event 服务事件
//     */
//    private void sendNotifacationReply(AccessibilityEvent event) {
//        if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
//            Notification notification = (Notification) event.getParcelableData();
//            String content = notification.tickerText.toString();
//            String[] cc = content.split(":");
//
//            String receiveName = cc[0].trim();
//            String receciveScontent = cc[1].trim();
//
//            PendingIntent pendingIntent = notification.contentIntent;
//            try {
//                isSendSuccess = true;
//                pendingIntent.send();
//            } catch (PendingIntent.CanceledException e) {
//                e.printStackTrace();
//            }
//            Toast.makeText(this, content, Toast.LENGTH_LONG).show();
//        }
//    }


}
