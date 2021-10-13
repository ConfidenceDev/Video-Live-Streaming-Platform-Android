package me.vebbo.android.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import static me.vebbo.android.utils.Manage.showToast;

public class EditAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mName, mLocation, mFace, mInsta, mTwit, mWebsite, mBio;
    private Button mFinishBtn;
    private ImageView mBackBtn;
    private HorizontalScrollView mMoodScroll;
    private LinearLayout mMoodLin;
    private TextView mCharCount;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private int MOOD = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        setContentView(R.layout.activity_edit_account);

        //------------------------ Initialize ---------------------------------------------------
        mFinishBtn = findViewById(R.id.saveBtn);

        mName = findViewById(R.id.userNameField);
        mLocation = findViewById(R.id.locationField);
        mFace = findViewById(R.id.faceField);
        mInsta = findViewById(R.id.instaField);
        mTwit = findViewById(R.id.twitField);
        mWebsite = findViewById(R.id.webField);
        mBio = findViewById(R.id.bio_field);
        mMoodLin = findViewById(R.id.moodLin);
        mMoodScroll = findViewById(R.id.moodScroll);
        mCharCount = findViewById(R.id.char_count);
        mBackBtn = findViewById(R.id.editBack);

        mBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = mBio.getText().toString();
                text = text.replace("\n", " ");
                String result = text.toCharArray().length + getResources().getString(R.string._32);
                mCharCount.setText(result);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final int img = mPreferences.getInt(IMAGE, -1);
        final String username = mPreferences.getString(USERNAME, "");
        final String location = mPreferences.getString(LOCATION, "");
        final String face = mPreferences.getString(FACEBOOK, "");
        final String insta = mPreferences.getString(INSTAGRAM, "");
        final String twit = mPreferences.getString(TWITTER, "");
        final String web = mPreferences.getString(WEBSITE, "");
        final String bio = mPreferences.getString(BIO, "");

        if (img != -1) {
            selectAvatar(img);
        }
        if (!TextUtils.isEmpty(username)) {
            mName.setText(username);
        }
        if (!TextUtils.isEmpty(location)) {
            mLocation.setText(location);
        }
        if (!TextUtils.isEmpty(face)) {
            mFace.setText(face);
        }
        if (!TextUtils.isEmpty(insta)) {
            mInsta.setText(insta);
        }
        if (!TextUtils.isEmpty(twit)) {
            mTwit.setText(twit);
        }
        if (!TextUtils.isEmpty(web)) {
            mWebsite.setText(web);
        }
        if (!TextUtils.isEmpty(bio)) {
            mBio.setText(bio);
        }

        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = mName.getText().toString();
                final String location = mLocation.getText().toString();
                final String face = mFace.getText().toString();
                final String insta = mInsta.getText().toString();
                final String twit = mTwit.getText().toString();
                final String web = mWebsite.getText().toString();
                final String bio = mBio.getText().toString();

                mEditor.putInt(IMAGE, MOOD);
                mEditor.putString(USERNAME, name);
                mEditor.putString(LOCATION, location);
                mEditor.putString(FACEBOOK, face);
                mEditor.putString(INSTAGRAM, insta);
                mEditor.putString(TWITTER, twit);
                mEditor.putString(WEBSITE, web);
                mEditor.putString(BIO, bio);
                mEditor.apply();

                showToast(EditAccountActivity.this, getString(R.string.saved), Toast.LENGTH_SHORT);
                onBackPressed();
            }
        });

        findViewById(R.id.mood1).setOnClickListener(this);
        findViewById(R.id.mood2).setOnClickListener(this);
        findViewById(R.id.mood3).setOnClickListener(this);
        findViewById(R.id.mood4).setOnClickListener(this);
        findViewById(R.id.mood5).setOnClickListener(this);
        findViewById(R.id.mood6).setOnClickListener(this);
        findViewById(R.id.mood7).setOnClickListener(this);
        findViewById(R.id.mood8).setOnClickListener(this);
        findViewById(R.id.mood9).setOnClickListener(this);
        findViewById(R.id.mood10).setOnClickListener(this);
        findViewById(R.id.mood11).setOnClickListener(this);
        findViewById(R.id.mood12).setOnClickListener(this);
        findViewById(R.id.mood13).setOnClickListener(this);
        findViewById(R.id.mood14).setOnClickListener(this);
        findViewById(R.id.mood15).setOnClickListener(this);
        findViewById(R.id.mood16).setOnClickListener(this);
        findViewById(R.id.mood17).setOnClickListener(this);
        findViewById(R.id.mood18).setOnClickListener(this);
        findViewById(R.id.mood19).setOnClickListener(this);
        findViewById(R.id.mood20).setOnClickListener(this);
        findViewById(R.id.mood21).setOnClickListener(this);
        findViewById(R.id.mood22).setOnClickListener(this);
        findViewById(R.id.mood23).setOnClickListener(this);
        findViewById(R.id.mood24).setOnClickListener(this);

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void selectAvatar(int pos){
        MOOD = pos;
        mMoodLin.getChildAt(pos).setAlpha(.4f);

        mMoodScroll.post(new Runnable() {
            @Override
            public void run() {
                mMoodScroll.smoothScrollTo(mMoodLin.getChildAt(pos).getLeft(), 0);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.mood1){
            setOpacity(1);
        } else if(view.getId() == R.id.mood2){
            setOpacity(2);
        } else if(view.getId() == R.id.mood3){
            setOpacity(3);
        } else if(view.getId() == R.id.mood4){
            setOpacity(4);
        } else if(view.getId() == R.id.mood5){
            setOpacity(5);
        } else if(view.getId() == R.id.mood6){
            setOpacity(6);
        } else if(view.getId() == R.id.mood7){
            setOpacity(7);
        } else if(view.getId() == R.id.mood8){
            setOpacity(8);
        } else if(view.getId() == R.id.mood9){
            setOpacity(9);
        } else if(view.getId() == R.id.mood10){
            setOpacity(10);
        } else if(view.getId() == R.id.mood11){
            setOpacity(11);
        } else if(view.getId() == R.id.mood12){
            setOpacity(12);
        } else if(view.getId() == R.id.mood13){
            setOpacity(13);
        } else if(view.getId() == R.id.mood14){
            setOpacity(14);
        } else if(view.getId() == R.id.mood15){
            setOpacity(15);
        } else if(view.getId() == R.id.mood16){
            setOpacity(16);
        } else if(view.getId() == R.id.mood17){
            setOpacity(17);
        } else if(view.getId() == R.id.mood18){
            setOpacity(18);
        } else if(view.getId() == R.id.mood19){
            setOpacity(19);
        } else if(view.getId() == R.id.mood20){
            setOpacity(20);
        } else if(view.getId() == R.id.mood21){
            setOpacity(21);
        } else if(view.getId() == R.id.mood22){
            setOpacity(22);
        } else if(view.getId() == R.id.mood23){
            setOpacity(23);
        } else if(view.getId() == R.id.mood24){
            setOpacity(24);
        }
    }

    private void setOpacity(int pos){
        for (int i = 0; i < mMoodLin.getChildCount(); i++){
            mMoodLin.getChildAt(i).setAlpha(1f);
        }
        mMoodLin.getChildAt(--pos).setAlpha(.4f);
        MOOD = pos;

        showToast(EditAccountActivity.this, getString(R.string.selected), Toast.LENGTH_SHORT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}