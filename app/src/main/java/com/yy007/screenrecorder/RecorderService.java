package com.yy007.screenrecorder;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.projection.MediaProjection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Think on 2017/2/15.
 */

public class RecorderService extends Service {
    private final String TAG = RecorderService.class.getSimpleName();
    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;
    private View mFloatLayout;
    private ImageView iv;
    private TextView tv;
    private ScreenRecorder mRecorder;
    private boolean isPlay = false;
    private MediaProjection mMediaProjection;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        RecorderService getService() {
            return RecorderService.this;
        }
    }

    public void setRecorder(ScreenRecorder mRecorder) {
        this.mRecorder = mRecorder;
    }

    public void setMediaProjection(MediaProjection mediaProjection) {
        this.mMediaProjection = mediaProjection;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "oncreat");
        createFloatView();
        myHander = new MyHandler();
    }

    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);
        Log.i(TAG, "mWindowManager--->" + mWindowManager);
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 0;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

         /*// 设置悬浮窗口长宽数据
        wmParams.width = 200;
        wmParams.height = 80;*/

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = inflater.inflate(R.layout.recorder_service_layout, null);
        //添加mFloatLayout
//        mWindowManager.addView(mFloatLayout, wmParams);
        //浮动窗口按钮
        iv = (ImageView) mFloatLayout.findViewById(R.id.window_iv);
        tv = (TextView) mFloatLayout.findViewById(R.id.window_tv);

//        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
//                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
//                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth() / 2);
//        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight() / 2);
        //设置监听浮动窗口的触摸移动
        mFloatLayout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                wmParams.x = (int) event.getRawX() - mFloatLayout.getMeasuredWidth() / 2;
                Log.i(TAG, "RawX" + event.getRawX());
                Log.i(TAG, "X" + event.getX());
                //减25为状态栏的高度
                wmParams.y = (int) event.getRawY() - mFloatLayout.getMeasuredHeight() / 2 - 25;
                Log.i(TAG, "RawY" + event.getRawY());
                Log.i(TAG, "Y" + event.getY());
                //刷新
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                return false;  //此处必须返回false，否则OnClickListener获取不到监听
            }
        });

        iv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!isPlay) {
                    isPlay = true;
                    Intent intent = new Intent();  //Itent就是我们要发送的内容
                    intent.setAction(PlayState.PLAY_STATE_START);   //设置你这个广播的action，只有和这个action一样的接受者才能接受者才能接收广播
                    sendBroadcast(intent);   //发送广播

                    iv.setImageResource(R.drawable.window_record_pause);
                } else {
                    isPlay = false;
                    setQuit();
                    iv.setImageResource(R.drawable.window_record_play);

                }

            }
        });
    }

    private void setQuit() {
        if (mRecorder != null) {
            mRecorder.quit();
            mRecorder = null;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        removeView();
    }

    public void removeView() {
        mWindowManager.removeView(mFloatLayout);
    }

    public void addView() {
        isPlay = false;
        iv.setImageResource(R.drawable.window_record_play);
        mWindowManager.addView(mFloatLayout, wmParams);
    }

    public void getTime() {
        startTime = System.currentTimeMillis();
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (isPlay) {
                    try {
                        Thread.sleep(100);
                        myHander.sendEmptyMessage(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private MyHandler myHander;

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (tv != null) {
                        tv.setText(millis2FitTimeSpan(startTime, System.currentTimeMillis()));
                    }
                    break;
            }
        }
    }

    private long startTime;

    private static String millis2FitTimeSpan(long millis0, long millis1) {
        long millis = Math.abs(millis1 - millis0);
        if (millis <= 0) return "00:00";
        return new SimpleDateFormat("mm:ss", Locale.getDefault()).format(new Date(millis));
    }

}
