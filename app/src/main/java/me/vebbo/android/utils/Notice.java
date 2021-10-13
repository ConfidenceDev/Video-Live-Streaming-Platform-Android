package me.vebbo.android.utils;

import android.graphics.Typeface;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import me.vebbo.android.R;
import me.vebbo.android.activities.SettingsActivity;

public class Notice {

    public static void suspensionNotice(AppCompatActivity context, Typeface font, String timeline) {
        AlertDialog internetDialog = new AlertDialog.Builder(context).create();
        internetDialog.setTitle(context.getResources().getString(R.string.suspension_notice_header));
        internetDialog.setMessage(context.getResources().getString(R.string.suspension_notice_msg)
                + " " +
                timeline);
        internetDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getResources().getString(R.string.finish), null, null);
        internetDialog.show();

        TextView alertTitle = internetDialog.getWindow().findViewById(R.id.alertTitle);
        TextView textView = internetDialog.getWindow().findViewById(android.R.id.message);
        Button button1 = internetDialog.getWindow().findViewById(android.R.id.button1);

        alertTitle.setTypeface(font);
        textView.setTypeface(font);
        button1.setTypeface(font);
    }

    public static void helpNotice(AppCompatActivity context, Typeface font){
        AlertDialog helpDialog = new AlertDialog.Builder(context).create();
        helpDialog.setTitle(context.getResources().getString(R.string.help));
        helpDialog.setMessage(context.getResources().getString(R.string.help1) +
                context.getResources().getString(R.string.help2) +
                context.getResources().getString(R.string.help3));
        helpDialog.setButton(AlertDialog.BUTTON_POSITIVE,
                context.getResources().getString(R.string.finish), null, null);
        helpDialog.show();

        TextView alertTitle = helpDialog.getWindow().findViewById(R.id.alertTitle);
        TextView textView = helpDialog.getWindow().findViewById(android.R.id.message);
        Button button2 = helpDialog.getWindow().findViewById(android.R.id.button2);

        alertTitle.setTypeface(font);
        textView.setTypeface(font);
        button2.setTypeface(font);
    }

}
