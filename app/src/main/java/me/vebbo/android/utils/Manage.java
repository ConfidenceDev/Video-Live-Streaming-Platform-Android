package me.vebbo.android.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import me.vebbo.android.BuildConfig;
import me.vebbo.android.R;

import static me.vebbo.android.utils.Constant.FONT;

public class Manage {

    private Context context;
    private File outputFile;
    protected static final String MOOD = "mood";

    public Manage(Context context) {
        this.context = context;
    }

    public static Drawable setAvatar(Context c, int ImageNum) {
        return ContextCompat.getDrawable(c,
                c.getResources().getIdentifier((MOOD + "_" + (++ImageNum)), "drawable",
                        c.getPackageName()));
    }

    public static void showToast(AppCompatActivity context, String msg, int duration){
        Toast toast = new Toast(context);
        TextView text = new TextView(context);
        text.setText(msg);
        text.setTextColor(context.getResources().getColor(R.color.white));
        text.setTextSize(14);
        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        text.setPadding(16, 12, 16, 12);
        text.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_toast, null));
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), FONT);
        text.setTypeface(typeface);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 10);
        toast.setDuration(duration);
        toast.setView(text);
        toast.show();
    }

    public void saveFile(String value){
        final String NAME = "VEBB0_";
        String dateTime = java.text.DateFormat.getDateTimeInstance().format(new Date());

        outputFile = new File(locationFolder(), (NAME + dateTime.replace(" ", "_")
                .replace(",", "_").trim() + ".txt"));
        FileOutputStream stream = null;
        try {
            if (!outputFile.exists())
                outputFile.createNewFile();

            stream = new FileOutputStream(outputFile);
            stream.write(value.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                    Toast toast = Toast.makeText(context,
                            context.getResources().getString(R.string.saved), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.START, 50, 50);
                    toast.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File locationFolder() {
        File localFile = new File(Environment.getExternalStorageDirectory() + "/"
                + context.getResources().getString(R.string.app_name));

        if (!localFile.exists()) {
            //Create Folder From Path
            localFile.mkdir();
        }

        return localFile;
    }

    public void shareApplication() {
        ApplicationInfo app = context.getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;

        Intent intent = new Intent(Intent.ACTION_SEND);
        // MIME of .apk is "application/vnd.android.package-archive".
        // but Bluetooth does not accept this. Let's use "*/*" instead.
        intent.setType("*/*");

        // Append file and send Intent
        File originalApk = new File(filePath);

        try {
            //Make new directory in new location
            File tempFile = new File(context.getExternalCacheDir() + "/ExtractedApk");
            //If directory doesn't exists create new
            if (!tempFile.isDirectory())
                if (!tempFile.mkdirs())
                    return;
            //Get application's name and convert to lowercase
            tempFile = new File(tempFile.getPath() + "/" + context.getString(app.labelRes)
                    .replace(" ", "").toLowerCase() + ".apk");
            //If file doesn't exists create new
            if (!tempFile.exists()) {
                if (!tempFile.createNewFile()) {
                    return;
                }
            }
            //Copy file to new location
            InputStream in = new FileInputStream(originalApk);
            OutputStream out = new FileOutputStream(tempFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
            //Open share dialog

            Uri newUri;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                newUri = FileProvider.getUriForFile(context.getApplicationContext(),
                        BuildConfig.APPLICATION_ID + ".provider", tempFile);
            } else {
                newUri = Uri.fromFile(tempFile);
            }

            intent.putExtra(Intent.EXTRA_STREAM, newUri);
            context.startActivity(Intent.createChooser(intent, context.getResources().
                    getString(R.string.share_app_using)));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
