package me.vebbo.android.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import me.vebbo.android.R;

public class NetworkConnection {

    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = null;
        if (cm != null) {
            netinfo = cm.getActiveNetworkInfo();
        }

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            return (mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting());
        } else
            return false;
    }

    public void noInternet(Context context, Typeface font) {
        AlertDialog internetDialog = new AlertDialog.Builder(context).create();
        internetDialog.setTitle(context.getString(R.string.no_internet));
        internetDialog.setMessage(context.getString(R.string.internet_continue));
        internetDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getResources().getString(R.string.finish), null, null);
        internetDialog.show();

        TextView alertTitle = internetDialog.getWindow().findViewById(R.id.alertTitle);
        TextView textView = internetDialog.getWindow().findViewById(android.R.id.message);
        Button button1 = internetDialog.getWindow().findViewById(android.R.id.button1);

        alertTitle.setTypeface(font);
        textView.setTypeface(font);
        button1.setTypeface(font);
    }
}
