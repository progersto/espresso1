package com.miracas.espresso.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ProgressDialogHelper {
    ProgressDialog mProgressDialog;

    public ProgressDialogHelper() {

    }

    public ProgressDialogHelper(Context context, String message, String title) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public ProgressDialogHelper(Context context, String message, String title, boolean cancel,
                                DialogInterface.OnCancelListener onCancelListener) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(cancel);
        mProgressDialog.setOnCancelListener(onCancelListener);
        mProgressDialog.show();
    }

    public ProgressDialog getmProgressDialog() {
        return mProgressDialog;
    }

    public void setmProgressDialog(ProgressDialog mProgressDialog) {
        hide();
        this.mProgressDialog = mProgressDialog;

    }

    public void show() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void create(Context context, String message, String title) {
        if (mProgressDialog != null) {
            mProgressDialog.show();
        }
        mProgressDialog = ProgressDialog.show(context, title, message);

    }


    public void hide() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }


    }


    public void dismiss() {
        if(mProgressDialog!=null&& mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }

    }

    public void onDestroy() {
        hide();
    }


}


