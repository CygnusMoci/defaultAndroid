package com.moci.defaultandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import static android.os.Build.VERSION_CODES.M;

/**
 * @author : Cygnusmoci
 * @create : 2020-01-08 10:37
 * @description :
 */
public class SplashActivity extends Activity {

    public static final int EXTERNAL_STORAGE_REQ_CAMERA_CODE = 100;
    public static final int EXTERNAL_STORAGE_REQ_GALLERY_CODE = 101;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        setContentView(R.layout.activity_splash);
        mHandler = new Handler();
        reuqestExternalStorage();
    }

    private void reuqestExternalStorage(){
        if (android.os.Build.VERSION.SDK_INT >= M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //进行权限请求
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_REQ_GALLERY_CODE);
            } else {
                requestCameraPerm();
            }
        } else {
            requestCameraPerm();
        }
    }

    private void requestCameraPerm() {
        if (android.os.Build.VERSION.SDK_INT >= M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                //进行权限请求
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        EXTERNAL_STORAGE_REQ_CAMERA_CODE);
            } else {
                enterNextPage();
            }
        } else {
            enterNextPage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == EXTERNAL_STORAGE_REQ_CAMERA_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {// Permission Granted

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1800);

            } else
                enterNextPage();
        }else if (requestCode == EXTERNAL_STORAGE_REQ_GALLERY_CODE){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1800);
            }else{
                requestCameraPerm();
            }
        }
    }
    protected void enterNextPage(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 1800);
    }
}
