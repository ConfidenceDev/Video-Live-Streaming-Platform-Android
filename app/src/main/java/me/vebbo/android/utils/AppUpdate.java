package me.vebbo.android.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import me.vebbo.android.R;
import me.vebbo.android.activities.HomeActivity;

public class AppUpdate {

    private AppCompatActivity context;
    private Typeface font;

    public AppUpdate(AppCompatActivity context, Typeface font) {
        this.context = context;
        this.font = font;
    }

    public void appUpdate() {
        AlertDialog homeUpdateDialog = new AlertDialog.Builder(context).create();
        homeUpdateDialog.setTitle(context.getString(R.string.app_update));
        homeUpdateDialog.setMessage(context.getString(R.string.app_update_msg));
        homeUpdateDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getResources().getString(R.string.update), null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openStore(context.getResources().getString(R.string.store_url));
            }
        });
        homeUpdateDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getResources().getString(R.string.website), null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getResources().getString(R.string.web_add))));
            }
        });
        homeUpdateDialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getResources().getString(R.string.finish), null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                context.finishAffinity();
            }
        });
        homeUpdateDialog.setCancelable(false);
        homeUpdateDialog.show();

        TextView alertTitle = homeUpdateDialog.getWindow().findViewById(R.id.alertTitle);
        TextView textView = homeUpdateDialog.getWindow().findViewById(android.R.id.message);
        Button button1 = homeUpdateDialog.getWindow().findViewById(android.R.id.button1);
        Button button2 = homeUpdateDialog.getWindow().findViewById(android.R.id.button2);
        Button button3 = homeUpdateDialog.getWindow().findViewById(android.R.id.button3);

        alertTitle.setTypeface(font);
        textView.setTypeface(font);
        button1.setTypeface(font);
        button2.setTypeface(font);
        button3.setTypeface(font);
    }

    private void openStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}

