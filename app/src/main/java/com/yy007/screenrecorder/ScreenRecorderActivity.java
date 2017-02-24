package com.yy007.screenrecorder;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ScreenRecorderActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private ScreenRecorder mRecorder;
    private Button mButton;
    private Toolbar toolbar;
    private TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_recorder);
        initView();
        setAction();
    }

    private void initView() {
    }

    private void setAction() {
        //noinspection ResourceType
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        intent = new Intent(this, RecorderService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);

        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, REQUEST_CODE);
    }

    MediaProjection mediaProjection;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        if (mediaProjection == null) {
            Log.e("@@", "media projection is null");
            return;
        }
        // video size
        final int width = 720;
        final int height = 1280;
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ScreenRecorder");
        if (!file.exists()) {
            file.mkdirs();
        }

        File f = new File(file.getAbsolutePath() + File.separator +
                "record-" + width + "x" + height + "-" + System.currentTimeMillis() + ".mp4");
        final int bitrate = 6000000;
        mRecorder = new ScreenRecorder(width, height, bitrate, 1, mediaProjection, f.getAbsolutePath());
        Log.e("@@", "开始喽");
        mRecorder.start();

//        if (isPermissions()) {


        Toast.makeText(this, "Screen recorder is running...", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "未获取权限：" + getPermsList().toString(), Toast.LENGTH_SHORT).show();
//        }
        mService.setRecorder(mRecorder);
        mService.getTime();
        finish();
    }

    private Intent intent;

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
        super.onPause();
        Log.e("@@", "111");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("@@", "222");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("@@", "333");
        if (mConnection != null) {
            unbindService(mConnection);
        }
    }
}
