package com.yy007.screenrecorder;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private ScreenRecorder mRecorder;
    private Button mButton;
    private Toolbar toolbar;
    private TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.color_bg));
        initListener();
        setAction();
    }

    private void initView() {
        tv = (TextView) findViewById(R.id.tv);
        mButton = (Button) findViewById(R.id.btn);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.color_bg));
    }

    private void initListener() {
        mButton.setOnClickListener(this);
    }

    private void setAction() {
        //检查权限
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW};
        checkPermissions(perms);
        setService();
    }

    private void setService() {
        intent = new Intent(this, RecorderService.class);
        startService(intent);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private RecorderService mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = ((RecorderService.MyBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("YY007H", "onStart");
    }

    private Intent intent;
    private boolean isFlag = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                if (isFlag) {
                    mButton.setText("停止录制");
                    isFlag = false;
                    mService.addView();
                    moveTaskToBack(true);
                } else {
                    mButton.setText("开始录制");
                    isFlag = true;
                    mService.removeView();
                }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("YY007H", "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConnection != null) {
            unbindService(mConnection);
        }
        if (intent != null) {
            stopService(intent);
        }
    }
}
