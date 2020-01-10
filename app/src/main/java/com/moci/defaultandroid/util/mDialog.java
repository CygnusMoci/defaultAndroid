package com.moci.defaultandroid.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * @author : Cygnusmoci
 * @create : 2020-01-09 14:19
 * @description :
 */
public class mDialog {
    private Context activity;

    public mDialog(Context activity) {
        this.activity = activity;
    }


    /**
     * 显示一般的等待进度框(主线程中调用),返回被赋值的progressDialog,以方便在同一个类中关闭此ProgressDialog
     *
     * @param title          进度框标题
     * @param message        进度框显示信息
     * @param progressDialog 传入的全局变量，可为null
     */
    public ProgressDialog startProgressDialog(
            String title, String message, ProgressDialog progressDialog) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
        }
        progressDialog.setCancelable(false);
        // progressDialog.setTitle(title);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIcon(android.R.drawable.ic_dialog_info);
        progressDialog.setMessage(message);
        progressDialog.show();
        return progressDialog;
    }

    /**
     * 结束一般的进度进度框
     *
     * @param progressDialog 要结束的对象
     */
    public void endProgressDialog(ProgressDialog progressDialog) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    public ProgressDialog startProgressDialog(
            String message, ProgressDialog progressDialog) {
        return startProgressDialog(null,message,progressDialog);
    }

    public ProgressDialog startProgressDialog(
            String message) {
        return startProgressDialog(null,message,null);
    }
}
