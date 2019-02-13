package com.yh.autocontrolwechat;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

//public class FxService extends Service {
//    static final String receive_show = "Float_window_show";
//    //注册广播
//    private MsgReceiver msgReceiver;
//    //定义浮动窗口布局
//    LinearLayout mFloatLayout;
//    WindowManager.LayoutParams wmParams;
//    //创建浮动窗口设置布局参数的对象
//    WindowManager mWindowManager;
//    TextView mFloatView_textView;
//    private static final String TAG = "FxService";
//
//    /**
//     * 广播接收器
//     **/
//
//    public class MsgReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            //此处从广播中取出数据，并写入你想要的函数
//            String string = intent.getStringExtra(receive_show);
//            mFloatView_textView.setText(string);
//        }
//    }
//
//    @Override
//    public void onCreate() {
//
//
//        // TODO Auto-generated method stub
//        super.onCreate();
//        Log.i(TAG, "on_create");
//        createFloatView();
//        //动态注册广播接收器
//        msgReceiver = new MsgReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("com.application.mac.RECEIVER");
//        registerReceiver(msgReceiver, intentFilter);
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    private void createFloatView() {
//
//
//        wmParams = new WindowManager.LayoutParams();
//        //获取的是WindowManagerImpl.CompatModeWrapper
//        mWindowManager = (WindowManager) getSystemService(getApplication().WINDOW_SERVICE);
//        int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
//        wmParams.flags = flags;
//        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//        //TYPE_SYSTEM_ALERT
//        wmParams.format = PixelFormat.TRANSLUCENT;
//        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
//        wmParams.gravity = Gravity.TOP;
//        LayoutInflater inflater = LayoutInflater.from(getApplication());
//        //获取浮动窗口视图所在布局
//        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.suspended_window, null);
//        //添加mFloatLayout
//        mWindowManager.addView(mFloatLayout, wmParams);
//        //浮动窗口按钮
//        mFloatView_textView = mFloatLayout.findViewById(R.id.numbersChange);
////        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//
//    }
//
//    @Override
//    public void onDestroy() {
//        // TODO Auto-generated method stub
//        super.onDestroy();
//        //注销广播
//        unregisterReceiver(msgReceiver);
//        if (mFloatLayout != null) {
//            //移除悬浮窗口
//            mWindowManager.removeView(mFloatLayout);
//        }
//    }
//}

