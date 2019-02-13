package com.yh.autocontrolwechat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

public class WindowUtils {
    private static final String LOG_TAG = "WindowUtils";
    private static View mView = null;
    private static View mView1 = null;
    private static WindowManager mWindowManager = null;
    private static Context mContext = null;
    public static Boolean isShown = false;
    public static TextView textView = null;
    public static Button pauseButton = null;

    /**
     * 显示弹出框
     *
     * @param context
     */
    public static void showPopupWindow(final Context context) {


        if (isShown) {
            return;
        }
        isShown = true;

        // 获取应用的Context
        mContext = context.getApplicationContext();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mView = setUpView(context, R.layout.suspended_window);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        params.flags = flags;

        params.format = PixelFormat.TRANSLUCENT;

        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.TOP;
        textView = mView.findViewById(R.id.numbersChange);


        final WindowManager.LayoutParams params1 = new WindowManager.LayoutParams();
        params1.type = LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        mView1 = setUpView(context, R.layout.pauselayout);
        params1.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        int flags1 = LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params1.flags = flags1;
        params1.format = PixelFormat.TRANSLUCENT;
        params1.gravity = Gravity.RIGHT | Gravity.CENTER;
        params1.width = LayoutParams.WRAP_CONTENT;
        params1.height = LayoutParams.WRAP_CONTENT;
        pauseButton = mView1.findViewById(R.id.pauseButton);
        final SharedPreferences sp =context.getSharedPreferences("data", MODE_PRIVATE);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int who = sp.getInt("who",0);
                    if(who == 0){
                        ControlService.Mainthread.interrupt();
                    }else if(who == 1){
                        ControlService.MainthreadFriend.interrupt();
                    }
                    hidePopupWindow();
                    Intent i = new Intent();
                    i.setClassName("com.yh.autocontrolwechat", "com.yh.autocontrolwechat.MainActivity");
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        mWindowManager.addView(mView, params);
        mWindowManager.addView(mView1, params1);


    }

    private static View setUpView(Context context, int id) {
        View view;
        view = LayoutInflater.from(context).inflate(id, null);
        return view;
    }

    public static void updateTextView(int num, int str) {
        TextView textView = mView.findViewById(R.id.numbersChange);

        if (str == 0) {
            textView.setText("已成功发送" + num + "个群");
            Log.e("监控", "group get it" + str);
        } else if (str == 1) {
            Log.e("监控", "friend get it" + str);
            textView.setText("已成功发送" + num + "个好友");
        }

    }

    /**
     * 隐藏弹出框
     */
    public static void hidePopupWindow() {
        Log.e("监控", "igetit");
        mWindowManager.removeView(mView1);
        mWindowManager.removeView(mView);
        isShown = false;
    }

}