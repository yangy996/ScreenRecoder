package com.yy007.screenrecorder;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Think on 2017/2/15.
 */

public class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private final int PERMISSION_REQUEST_CODE = 1;
    private boolean isPermissions;
    private List<String> permsList;

    public boolean isPermissions() {
        return isPermissions;
    }

    public List<String> getPermsList() {
        return permsList;
    }

    public void checkPermissions(String[] perms) {
        permsList = new ArrayList<>();

        if (EasyPermissions.hasPermissions(this, perms)) {
            Log.d("YY007H", "已获取权限");
            isPermissions = true;
        } else {
            EasyPermissions.requestPermissions(this, "必要的权限", PERMISSION_REQUEST_CODE, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //把申请权限的回调交由EasyPermissions处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //下面两个方法是实现EasyPermissions的EasyPermissions.PermissionCallbacks接口
    //分别返回授权成功和失败的权限
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d("YY007H", "获取成功的权限" + perms);
        isPermissions = true;
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d("YY007H", "获取失败的权限" + perms);
        isPermissions = false;
        permsList.addAll(perms);
    }
}
