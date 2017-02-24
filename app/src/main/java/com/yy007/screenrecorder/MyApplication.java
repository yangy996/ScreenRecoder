package com.yy007.screenrecorder;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Think on 2017/2/17.
 */

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onCreate() {
        super.onCreate();
        setService();
        registerActivityLifecycleCallbacks(this);
    }

    private void setService() {
        playbackStatus = new PlaybackStatus();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PlayState.PLAY_STATE_START);
        filter.addAction(PlayState.PLAY_STATE_STOP);
        registerReceiver(playbackStatus, filter);
    }

    private PlaybackStatus playbackStatus;
    private List<Activity> list = new ArrayList<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        list.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        list.remove(activity);
        if (list.isEmpty()) {
            unregisterReceiver(playbackStatus);
        }
    }

    public class PlaybackStatus extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(PlayState.PLAY_STATE_START)) {
                Intent i = new Intent();
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setClass(getApplicationContext(), ScreenRecorderActivity.class);
                startActivity(i);
            }
        }
    }

}
