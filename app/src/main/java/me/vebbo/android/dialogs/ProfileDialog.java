package me.vebbo.android.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.util.Objects;

import me.vebbo.android.App;
import me.vebbo.android.R;

import static me.vebbo.android.utils.Constant.BIO;
import static me.vebbo.android.utils.Constant.FACEBOOK;
import static me.vebbo.android.utils.Constant.IMAGE;
import static me.vebbo.android.utils.Constant.INSTAGRAM;
import static me.vebbo.android.utils.Constant.LOCATION;
import static me.vebbo.android.utils.Constant.TWITTER;
import static me.vebbo.android.utils.Constant.USERNAME;
import static me.vebbo.android.utils.Constant.WEBSITE;
import static me.vebbo.android.utils.Manage.setAvatar;
import static me.vebbo.android.utils.Manage.showToast;

public class ProfileDialog {

    private App app;
    private AppCompatActivity context;
    private Dialog dialog;
    private Window window;

    public ProfileDialog(AppCompatActivity context) {
        this.context = context;
        app = (App) context.getApplication();
        dialog = new Dialog(this.context);
        window = dialog.getWindow();
    }

    public void showProfile(JSONObject jsonObject) {
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(.7f);
        }
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_profile);

        TextView userName, userLocation, userBio;
        ImageView userImage, closeBtn, faceBtn, instaBtn, twittBtn;
        FloatingActionButton webBtn;

        userName = dialog.findViewById(R.id.dialogName);
        userLocation = dialog.findViewById(R.id.dialogLocation);
        userBio = dialog.findViewById(R.id.dialogBio);
        userImage = dialog.findViewById(R.id.dialogProfImage);
        closeBtn = dialog.findViewById(R.id.closeDialogBtn);
        faceBtn = dialog.findViewById(R.id.facebook_btn);
        instaBtn = dialog.findViewById(R.id.insta_btn);
        twittBtn = dialog.findViewById(R.id.twit_btn);
        webBtn = dialog.findViewById(R.id.page_btn);

        try {
            userImage.setImageDrawable(setAvatar(context, jsonObject.getInt(IMAGE)));
            userName.setText(jsonObject.getString(USERNAME));
            userLocation.setText(jsonObject.getString(LOCATION));
            userBio.setText(jsonObject.getString(BIO));

            String face = jsonObject.getString(FACEBOOK);
            String insta = jsonObject.getString(INSTAGRAM);
            String twit = jsonObject.getString(TWITTER);
            String website = jsonObject.getString(WEBSITE);

            faceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!TextUtils.isEmpty(face)) {
                        Intent intent = null;
                        try {
                            context.getPackageManager()
                                    .getPackageInfo("com.facebook.katana", 0);
                            String url = "https://www.facebook.com/" + face;
                            intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("fb://facewebmodal/f?href=" + url));

                        } catch (Exception e) {
                            // no Facebook app, revert to browser
                            String url = "https://facebook.com/" + face;
                            intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                        }
                        context.startActivity(intent);

                    } else {
                        showToast(context, context.getResources().getString(R.string.not_available), Toast.LENGTH_SHORT);
                    }
                }
            });

            instaBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(insta)) {
                        Uri uri = Uri.parse("http://instagram.com/_u/" + insta);
                        Intent instaIntent = new Intent(Intent.ACTION_VIEW, uri);
                        instaIntent.setPackage("com.instagram.android");

                        try {
                            context.startActivity(instaIntent);

                        } catch (Exception e) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://instagram.com/" + insta)));
                        }

                    } else {
                        showToast(context, context.getResources().getString(R.string.not_available), Toast.LENGTH_SHORT);
                    }
                }
            });

            twittBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(twit)) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("twitter://user?screen_name=" + twit));
                            context.startActivity(intent);
                        } catch (Exception e) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://twitter.com/#!/" + twit)));
                        }
                    } else {
                        showToast(context, context.getResources().getString(R.string.not_available), Toast.LENGTH_SHORT);
                    }
                }
            });

            webBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(website)) {
                        Uri webPage = Uri.parse(website);
                        if (!website.startsWith("http://") || !website.startsWith("https://")) {
                            webPage = Uri.parse("http://" + website);
                        }
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);
                        if (webIntent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(webIntent);
                        }
                    } else {
                        showToast(context, context.getResources().getString(R.string.not_available), Toast.LENGTH_SHORT);
                    }
                }
            });

            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
