package com.semear.tec.palavrizapp.utils;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.AlertDialog;

public class Commons {

    public static void showAlert(Activity activity, String titleAlert, String textAlert, String btnAlert){

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        builder.setTitle(titleAlert)
                .setMessage(textAlert)
                .setPositiveButton(btnAlert, (dialog, which) -> {
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
