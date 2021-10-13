package me.vebbo.android.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;

import me.vebbo.android.dialogs.AddCardDialog;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import me.vebbo.android.R;
import me.vebbo.android.utils.Constant;
import me.vebbo.android.utils.Manage;

import static me.vebbo.android.utils.Constant.FONT;
import static me.vebbo.android.utils.Constant.MAIL;
import static me.vebbo.android.utils.Constant.MAIL_TO;
import static me.vebbo.android.utils.Constant.MAIL_TYPE;
import static me.vebbo.android.utils.Manage.showToast;
import static me.vebbo.android.utils.Notice.helpNotice;

public class SettingsActivity extends AppCompatActivity {

    private ConstraintLayout mAddCard, mAbout, mInvite, mShare, mRate, mFeedback, mLicense, mTerms, mHelp;
    private ImageView mBackBtn;
    private Button mEditAccBtn, mWebBtn;
    private Manage manage;
    private static Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        manage = new Manage(this);

        mAddCard = findViewById(R.id.con0);
        mAbout = findViewById(R.id.con1);
        mInvite = findViewById(R.id.con2);
        mShare = findViewById(R.id.con3);
        mRate = findViewById(R.id.con4);
        mFeedback = findViewById(R.id.con5);
        mLicense = findViewById(R.id.con6);
        mTerms = findViewById(R.id.con7);
        mHelp = findViewById(R.id.con8);
        mEditAccBtn = findViewById(R.id.editAccBtn);
        mWebBtn = findViewById(R.id.webBtn);
        mBackBtn = findViewById(R.id.settingsBack);

        font = Typeface.createFromAsset(getAssets(), FONT);

        //=============================== Buttons =====================================
        mEditAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, EditAccountActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.no_anim);
            }
        });

        mAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddCardDialog(SettingsActivity.this)
                        .saveCard(false, null, null,0, null);
                overridePendingTransition(R.anim.dialog_enter, R.anim.no_anim);
            }
        });

        mAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
                alertDialog.setTitle(getResources().getString(R.string.about));
                alertDialog.setMessage(getResources().getString(R.string.version));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok), null, null);
                alertDialog.show();

                TextView alertTitle = alertDialog.getWindow().findViewById(R.id.alertTitle);
                TextView textView = alertDialog.getWindow().findViewById(android.R.id.message);
                Button button2 = alertDialog.getWindow().findViewById(android.R.id.button2);

                alertTitle.setTypeface(font);
                textView.setTypeface(font);
                button2.setTypeface(font);
            }
        });

        mInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setData(Uri.parse(MAIL_TO));
                emailIntent.setType(MAIL_TYPE);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.invite_header));
                emailIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.invite_message));

                try {
                    startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.send_a_mail)));
                } catch (android.content.ActivityNotFoundException ex) {
                    showToast(SettingsActivity.this, getString(R.string.no_client), Toast.LENGTH_LONG);
                }
            }
        });

        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manage.shareApplication();
            }
        });

        mRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openStore(getResources().getString(R.string.store_url));
            }
        });

        mFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] TO = {MAIL};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setData(Uri.parse(MAIL_TO));
                emailIntent.setType(MAIL_TYPE);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.feedback_header));
                emailIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.feedback_message));

                try {
                    startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.send_a_mail)));
                } catch (android.content.ActivityNotFoundException ex) {
                    showToast(SettingsActivity.this, getString(R.string.no_client), Toast.LENGTH_LONG);
                }
            }
        });

        mLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, LicenseActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.no_anim);
            }
        });

        mTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, TermsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.no_anim);
            }
        });

        mHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpNotice(SettingsActivity.this, font);
            }
        });

        mWebBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent web_intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getResources().getString(R.string.web_add)));
                startActivity(web_intent);
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void openStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}