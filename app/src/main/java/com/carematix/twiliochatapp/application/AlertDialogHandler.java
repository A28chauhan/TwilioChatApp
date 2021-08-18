package com.carematix.twiliochatapp.application;

import android.content.Context;
import android.content.DialogInterface;

public
class AlertDialogHandler {

    public static void displayAlertWithMessage(String message, Context context) {
       // AlertDialog.Builder builder =new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
        androidx.appcompat.app.AlertDialog.Builder builder =new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setMessage(message).setCancelable(false).setPositiveButton("OK", null);
        builder.show();
    }

    public static void displayCancellableAlertWithHandler(String message, Context context,
                                                          DialogInterface.OnClickListener handler) {

        androidx.appcompat.app.AlertDialog.Builder builder =new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setMessage(message).setCancelable(false).setPositiveButton("OK", handler).setNegativeButton("Cancel", null);
        builder.show();

    }
}
