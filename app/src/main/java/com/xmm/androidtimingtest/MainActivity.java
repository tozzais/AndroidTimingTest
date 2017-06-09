package com.xmm.androidtimingtest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView tvNum;
    private String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvNum = (TextView) findViewById(R.id.tv_number);
    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.button1:
                mHandler.sendEmptyMessageDelayed(1,1000);
                break;
            case R.id.button2:
                timer();
                break;
            case R.id.button3:
                startThread();
                break;
            case R.id.button4:
                alarmServiceStart();
                break;
            case R.id.button5:
                countDownTimer.start();
                break;
        }
    }



    /**
     * Handler的使用
     */
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                Log.e(TAG,"mHandler"+Thread.currentThread().getName());
                tvNum.setText((Integer.parseInt(tvNum.getText().toString())+1)+"");
                mHandler.sendEmptyMessageDelayed(1,1000);
            }
        }
    };

    /**
     * Timer的使用 结合TimerTask
     */
    private Timer timer;
    private void timer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "Timer:"+Thread.currentThread().getName());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvNum.setText((Integer.parseInt(tvNum.getText().toString())+1)+"");
                    }
                });
            }
        }, 1000, 1000);
    }

    /**
     * 使用Thread线程来实现定时器
     */
    private MyThread thread;
    private class MyThread extends Thread {
        public boolean stop;
        public void run() {
            while (!stop) {
                // 通过睡眠线程来设置定时时间
                try {
                    Thread.sleep(1000);
                    Log.e(TAG, "Thread:"+Thread.currentThread().getName());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvNum.setText((Integer.parseInt(tvNum.getText().toString())+1)+"");
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    };
    private void startThread() { //开始线程
        if (thread == null) {
            thread = new MyThread();
            thread.start();
        }
    }
    private void stopThread() { //结束线程
        if (thread != null) {
            thread.stop = true;
            thread = null;
        }
    }

    /**
     * AlarmManager
     */
    private String ACTION_NAME = "alarmService";
    private void alarmServiceStart(){
        //注册广播
        registerBoradcastReceiver();
        //启动AlarmManager
        Intent intent =new Intent(ACTION_NAME);
        PendingIntent sender=PendingIntent
                .getBroadcast(this, 0, intent, 0);
        long firstime= SystemClock.elapsedRealtime();
        AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
        //5秒一个周期，不停的发送广播
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP
                , firstime,1000, sender);
    }
    //注册广播
    public void registerBoradcastReceiver(){
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ACTION_NAME);
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ACTION_NAME)){
                tvNum.setText((Integer.parseInt(tvNum.getText().toString())+1)+"");
            }
        }
    };

    /**
     * 使用CountDownTimer   其实是android封装好的 倒计时类
     * 说明这里的10*1000表示总时间 也就是10s 1000表示多久执行一次
     */
    private CountDownTimer countDownTimer = new CountDownTimer(10*1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            tvNum.setText((Integer.parseInt(tvNum.getText().toString())+1)+"");
        }

        @Override
        public void onFinish() {
            tvNum.setText("执行结束");
        }
    };




}
