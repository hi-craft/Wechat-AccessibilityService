package com.yh.autocontrolwechat;

import android.app.Activity;

import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends Activity {
    //定义主按钮
    public static Button button;
    //定义主输入框
    public static EditText TimeTostart, TimeMiddle;

    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        mContext = this;
        /*--------------switch-----------------------------*/

        /*--------------数据初始化--------------------------*/
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putInt("who", 0);
        editor.putInt("time", 0);
        editor.putInt("LongCheck", 0);
        editor.putInt("isButton", 0);
        editor.commit();
        /*--------------数据初始化结束-----------------------*/
        /**
         * 是否自动循环发送
         */
        //获取是否自动重复执行任务switch按钮
        final Switch aSwitch = findViewById(R.id.s_v);
        //switch初始化
        aSwitch.setChecked(false);
        aSwitch.setSwitchTextAppearance(MainActivity.this, R.style.s_false);
        //switch监听事件
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //控制开关字体颜色
                if (b) {
                    ToastGlobal("已开启循环发送,程序将自动重复执行定时任务,如需取消请点击关闭按钮或关闭应用程序");
                    //将flag标识推入本地存储
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putInt("loop", 1);
                    editor.commit();
                    aSwitch.setSwitchTextAppearance(MainActivity.this, R.style.s_true);
                } else {
                    ToastGlobal("已关闭循环发送,程序将在执行完下一次定时任务后停止");
                    //将flag标识推入本地存储
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putInt("loop", 0);
                    editor.commit();
                    aSwitch.setSwitchTextAppearance(MainActivity.this, R.style.s_false);
                }
            }
        });
        /**
         * 发送群或发送好友
         */
        //发送群或者发送好友
        final Switch CheckqunSwitch = findViewById(R.id.s_v2);
        CheckqunSwitch.setChecked(false);
        CheckqunSwitch.setSwitchTextAppearance(MainActivity.this, R.style.s_false);
        CheckqunSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //控制开关字体颜色

                if (b) {
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    //将flag标识推入本地存储
                    editor.putInt("who", 1);
                    editor.commit();
                    ToastGlobal("发送收藏给好友");
                    CheckqunSwitch.setSwitchTextAppearance(MainActivity.this, R.style.s_true);
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    //将flag标识推入本地存储
                    editor.putInt("who", 0);
                    editor.commit();
                    ToastGlobal("发送收藏给群");
                    CheckqunSwitch.setSwitchTextAppearance(MainActivity.this, R.style.s_false);
                }
            }
        });
        /*----------------------end------------------------------*/
        button = findViewById(R.id.button);

        //定时任务时长文本框
        TimeTostart = findViewById(R.id.time_input);
        TimeMiddle = findViewById(R.id.time_inpu2);
        //主按钮点击事件
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                //初始化主程序是否已经开始执行 flag
                        try {
                            String baseUrl = "http://120.78.3.23:8080/test/check";
                            // 新建一个对象
                            URL url = new URL(baseUrl);
                            // 打开一个HttpURLConnection连接
                            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                            // 设置连接超时时间
                            urlConn.setConnectTimeout(5 * 1000);
                            //设置从主机读取数据超时
                            urlConn.setReadTimeout(5 * 1000);
                            // Post请求不能使用缓存
                            urlConn.setUseCaches(false);
                            // 设置为Post请求
                            urlConn.setRequestMethod("GET");
                            // 开始连接
                            urlConn.connect();
                            if (urlConn.getResponseCode() == 200) {
                                // 获取返回的数据
                                InputStream inputStream = urlConn.getInputStream();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                                StringBuilder response = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    response.append(line);
                                }
                                if (!response.toString().equals("200")) {
                                    ToastGlobal("服务启动失败,请开启网络");
                                    Log.e("监控", "response:" + response.toString());
                                    return;
                                }
                                Log.e("监控", "response:" + response.toString());
                            } else {
                                Log.e("监控", "response:" + urlConn.getResponseCode());
                                ToastGlobal("服务启动失败,请开启网络");
                                return;
                            }
                            // 关闭连接
                            urlConn.disconnect();
                        } catch (Exception e) {
                            Log.e("监控", "err:" + e.toString());
                            ToastGlobal("服务启动失败,请开启网络");
                            return;
                        }
                ControlService.begin = false;
                ControlService.numbers = 1;
                ControlService.page = 0;
                ControlService.Peopelpage = 0;
                //初始化用户是否已获得授权flag
                SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
                Boolean hasaccept = sp.getBoolean("hasaccept", false);
                Log.e("监控", "hasaccept" + hasaccept.toString());

                //文本框不为空,则将数据推入本地存储
                if (!TimeTostart.getText().toString().equals("")) {
                    int time = Integer.parseInt(String.valueOf(TimeTostart.getText()));
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putInt("time", time);
                    editor.commit();
                }
                //用户权限验证
                if (!hasaccept) {
                    Intent intent = new Intent(MainActivity.this, ActivityLogin.class);
                    startActivity(intent);
                    return;
                }
                //标识数据由按钮发起
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putInt("isButton", 1);
                editor.commit();
                //权限请求
                if (isAccessibilitySettingsOn(mContext)) {
                    Boolean isnotification = isNotificationEnabled(getApplicationContext());
                    if (!isnotification) {
                        Toast.makeText(MainActivity.this, "请允许通知权限,即将跳转到系统设置界面", Toast.LENGTH_LONG).show();
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        requestPermission();
                        return;
                    }
                    Log.e("监控", isnotification.toString());
                    openWindow();
//                    WindowUtils.showPopupWindow(MainActivity.this);
                } else {
                    Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(accessibleIntent);
                }
            }
        });
    }

    /**
     * 打开微信界面
     */
    private void openWChart() {
        try {
            Toast.makeText(MainActivity.this, "即将打开微信,请不要对手机进行操作", Toast.LENGTH_SHORT).show();
            Thread.sleep(3000);
            Intent intent = new Intent();
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.setClassName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean isNotificationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //8.0手机以上
            if (((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).getImportance() == NotificationManager.IMPORTANCE_NONE) {
                return false;
            }
        }
        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected void requestPermission() {
        // 6.0以上系统才可以判断权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE) {
            // 进入设置系统应用权限界面
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
            return;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 运行系统在5.x环境使用
            // 进入设置系统应用权限界面
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
            return;
        }
        return;
    }


    public void openWindow() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);//ACTION_MANAGE_OVERLAY_PERMISSION
                startActivity(intent);
                return;
            } else {
                //Android6.0以上

                WindowUtils.showPopupWindow(getApplicationContext());
                openWChart();
//                Intent intent = new Intent(MainActivity.this,FxService.class);
//                startService(intent);
            }
        } else {
            WindowUtils.showPopupWindow(getApplicationContext());
            openWChart();
            //Android6.0以下，不用动态声明权限
//            Intent intent = new Intent(MainActivity.this,FxService.class);
//            startService(intent);
        }

    }

    /**
     * 屏蔽用户返回
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return true;
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Toast提示
     */
    private void ToastGlobal(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }

    /**
     * 判断微信助手是否开启
     *
     * @param context
     * @return
     */
    public boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.i("URL", "错误信息为：" + e.getMessage());
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }
        return false;
    }
}

