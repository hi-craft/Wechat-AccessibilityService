package com.yh.autocontrolwechat;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActivityLogin extends Activity implements View.OnClickListener {
    public static Boolean hasaccept = false;
    private TextView mBtnLogin;
    private int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    public static String IMEI = "0";
    private View progress;
    private EditText lockopener;
    private View mInputLayout;
    private ValueAnimator animator;
    private float mWidth, mHeight;
    private LinearLayout mPsw;
    public String my_message;
    public String my_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermission();
        }
        initView();
    }


    /**
     * 针对android6.0以上动态获取权限
     */
    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            getImei();
        }
    }

    /**
     * 是否开启授予读取imei权限
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            getImei();
        }
    }

    /**
     * 获取用户imei标识
     */
    public void getImei() {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        IMEI = tm.getDeviceId();
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("imei",IMEI);
        editor.commit();
        Toast.makeText(this,"IMEI："+IMEI,Toast.LENGTH_SHORT).show();
    }
    private void initView() {
        mBtnLogin = findViewById(R.id.main_btn_login);
        lockopener = findViewById(R.id.imei_input);

        progress = findViewById(R.id.layout_progress);
        mInputLayout = findViewById(R.id.input_layout);
        mPsw = findViewById(R.id.input_layout_psw);

        mBtnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(IMEI.length()<15){
            Toast.makeText(this,"激活码格式错误或未授予读取电话权限,请更正后重试",Toast.LENGTH_SHORT).show();
            return;
        }
        // 计算出控件的高与宽
        mWidth = mBtnLogin.getMeasuredWidth();
        mHeight = mBtnLogin.getMeasuredHeight();
        // 隐藏输入框
        mPsw.setVisibility(View.INVISIBLE);

        inputAnimator(mInputLayout, mWidth, mHeight);

    }
    /**
     * 恢复初始状态
     */
   //延时1s执行

    private void recovery() {
            progress.setVisibility(View.GONE);
        mInputLayout.setVisibility(View.VISIBLE);
        mPsw.setVisibility(View.VISIBLE);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mInputLayout.getLayoutParams();
        params.leftMargin = 0;
        params.rightMargin = 0;
        mInputLayout.setLayoutParams(params);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout, "scaleX", 0.5f, 1f);
        animator2.setDuration(300);
        animator2.setInterpolator(new AccelerateDecelerateInterpolator());
        animator2.start();
    }
    private void beginCheck() {
        /**
         * 301 激活码有误    304用户已存在  200激活成功
         */
        try {

            JSONObject obj = new JSONObject();
            obj.put("imei", IMEI);
            obj.put("lockopener", lockopener.getText().toString().trim());
            System.out.println("---------------------" + obj);
            String baseUrl = "http://120.78.3.23:8080/api/addUser";
            // 新建一个对象
            URL url = new URL(baseUrl);
            // 打开一个HttpURLConnection连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            // 设置连接超时时间
            urlConn.setConnectTimeout(5 * 1000);
            //设置从主机读取数据超时
            urlConn.setReadTimeout(5 * 1000);
            // Post请求必须设置允许输出 默认false
            urlConn.setDoOutput(true);
            //设置请求允许输入 默认是true
            urlConn.setDoInput(true);
            // Post请求不能使用缓存
            urlConn.setUseCaches(false);
            // 设置为Post请求
            urlConn.setRequestMethod("POST");
            //设置本次连接是否自动处理重定向
            urlConn.setInstanceFollowRedirects(true);
            // 配置请求Content-Type
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.setRequestProperty("Charset", "UTF-8");
            urlConn.setRequestProperty("Connection", "Keep-Alive");
            byte[] data = (obj.toString()).getBytes();
            urlConn.setRequestProperty("Content-Length", String.valueOf(data.length));
            // 开始连接
            urlConn.connect();
            OutputStream out = urlConn.getOutputStream();
            DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
            dos.write((obj.toString()).getBytes());
            // 发送请求参数
            dos.flush();
            dos.close();
            // 判断请求是否成功
            System.out.println(urlConn.getResponseCode() + "---------------------");
            if (urlConn.getResponseCode() == 200) {
                // 获取返回的数据
                InputStream inputStream = urlConn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                parseJSONWithGSON(String.valueOf(response));
                if(my_code.equals("301")){
//                    Toast.makeText(this,"激活码错误",Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recovery();
                        }
                    }, 1000);
                }else if(my_code.equals("200")||my_code.equals("304")){
                    SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                    editor.putBoolean("hasaccept",true);
                    editor.commit();
                    Log.e("hasaccept","hasaccept已经重置为true");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }
                }, 2000);
                }
                Log.e("---------------------", "Post方式请求成功，result--->" + response.substring(1, response.length() - 1));
                Toast.makeText(this,my_message,Toast.LENGTH_SHORT).show();
//                Toast.makeText(this,my_code,Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,"网络错误,请稍后重试",Toast.LENGTH_SHORT).show();
                Log.e("---------------------", "Post方式请求失败" + urlConn.getResponseCode());
            }
            // 关闭连接
            urlConn.disconnect();

            Log.e("---------------------","goThere");
        } catch (Exception e) {
            Log.e("---------------------", e.toString());
        }


    }

    /**
     * 屏蔽用户手动返回
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK)
            return true;
        return super.onKeyDown(keyCode,event);
    }

    /**
     * 服务器返回结果解析
     * @param str
     */
    private void parseJSONWithGSON(String str) {
        try {
            String strSplit = str.substring(1, str.length() - 1);
            String[] strarr = strSplit.split("\\,");
            my_message = strarr[0].split("\\:")[1];
            my_code = strarr[1].split("\\:")[1];
            Log.e("-----------------parse", my_message);
            Log.e("-----------------parse", my_code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 输入框的动画效果
     * @param view 控件
     * @param w    宽
     * @param h    高
     */
    private void inputAnimator(final View view, float w, float h) {

        AnimatorSet set = new AnimatorSet();

        animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view
                        .getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout,
                "scaleX", 1f, 0.5f);
        set.setDuration(300);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                /**
                 * 动画结束后，先显示加载的动画，然后再隐藏输入框
                 */
                progress.setVisibility(View.VISIBLE);
                progressAnimator(progress);
                mInputLayout.setVisibility(View.INVISIBLE);
                beginCheck();

            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });

    }

    /**
     * 出现进度动画
     *
     * @param view
     */
    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
                animator, animator2);
        animator3.setDuration(300);
        animator3.setInterpolator(new JellyInterpolator());
        animator3.start();

    }



}
