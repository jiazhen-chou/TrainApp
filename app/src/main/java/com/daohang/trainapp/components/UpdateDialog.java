package com.daohang.trainapp.components;

import android.app.AlertDialog;
import android.content.Context;

public class UpdateDialog extends AlertDialog {
    protected UpdateDialog(Context context) {
        super(context);
    }

    protected UpdateDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected UpdateDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    private void init(){

    }
}
