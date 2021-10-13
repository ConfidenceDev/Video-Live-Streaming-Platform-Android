package me.vebbo.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.socket.emitter.Emitter;
import me.vebbo.android.App;
import me.vebbo.android.R;
import me.vebbo.android.adapters.CommentAdapter;
import me.vebbo.android.dialogs.NoteDialog;
import me.vebbo.android.dialogs.ProfileDialog;
import me.vebbo.android.dialogs.PublishDialog;
import me.vebbo.android.dialogs.StartStreamDialog;
import me.vebbo.android.utils.AppUpdate;
import me.vebbo.android.utils.CommaCounter;
import me.vebbo.android.utils.Constant;
import me.vebbo.android.utils.Counter;
import me.vebbo.android.utils.CustomTypefaceSpan;
import me.vebbo.android.utils.InputKeyboardMethod;
import me.vebbo.android.utils.NetworkConnection;
import me.vebbo.android.utils.Stream;
import me.vebbo.android.viewmodels.CommentViewModel;
import pl.droidsonroids.gif.GifImageView;

import static androidx.camera.core.CameraX.unbindAll;
import static me.vebbo.android.utils.Constant.AUDIO;
import static me.vebbo.android.utils.Constant.BIO;
import static me.vebbo.android.utils.Constant.CAMERA;
import static me.vebbo.android.utils.Constant.CLAPS;
import static me.vebbo.android.utils.Constant.COMMENT;
import static me.vebbo.android.utils.Constant.COMMENT_TEXT;
import static me.vebbo.android.utils.Constant.CONTENT;
import static me.vebbo.android.utils.Constant.DATA;
import static me.vebbo.android.utils.Constant.DOLLAR;
import static me.vebbo.android.utils.Constant.END;
import static me.vebbo.android.utils.Constant.FACEBOOK;
import static me.vebbo.android.utils.Constant.FLAG;
import static me.vebbo.android.utils.Constant.FLAG_UTC;
import static me.vebbo.android.utils.Constant.FONT;
import static me.vebbo.android.utils.Constant.HELP;
import static me.vebbo.android.utils.Constant.IMAGE;
import static me.vebbo.android.utils.Constant.INSTAGRAM;
import static me.vebbo.android.utils.Constant.IS_BACK;
import static me.vebbo.android.utils.Constant.LIVE;
import static me.vebbo.android.utils.Constant.LOCATION;
import static me.vebbo.android.utils.Constant.NAIRA;
import static me.vebbo.android.utils.Constant.NOTE;
import static me.vebbo.android.utils.Constant.ONLINE;
import static me.vebbo.android.utils.Constant.OPT1;
import static me.vebbo.android.utils.Constant.OPT2;
import static me.vebbo.android.utils.Constant.OPT3;
import static me.vebbo.android.utils.Constant.PROMPT;
import static me.vebbo.android.utils.Constant.ROTATION;
import static me.vebbo.android.utils.Constant.STATE;
import static me.vebbo.android.utils.Constant.STREAM;
import static me.vebbo.android.utils.Constant.STREAM_LIVE;
import static me.vebbo.android.utils.Constant.SUSPENSION;
import static me.vebbo.android.utils.Constant.TIMER;
import static me.vebbo.android.utils.Constant.TOKEN;
import static me.vebbo.android.utils.Constant.TWITTER;
import static me.vebbo.android.utils.Constant.UPDATE;
import static me.vebbo.android.utils.Constant.USERNAME;
import static me.vebbo.android.utils.Constant.USER_ID;
import static me.vebbo.android.utils.Constant.UTC;
import static me.vebbo.android.utils.Constant.VALUES;
import static me.vebbo.android.utils.Constant.WEBSITE;
import static me.vebbo.android.utils.Manage.setAvatar;
import static me.vebbo.android.utils.Manage.showToast;
import static me.vebbo.android.utils.Notice.helpNotice;
import static me.vebbo.android.utils.Stream.audioTrack;
import static me.vebbo.android.utils.Stream.preview;
import static me.vebbo.android.utils.Translation.secToTime;
import static me.vebbo.android.utils.Translation.setCamStream;

public class HomeActivity extends AppCompatActivity {

    protected App app;
    protected SharedPreferences mPreferences;
    protected SharedPreferences.Editor mEditor;
    protected static Typeface font;
    protected static long firstTime = 0;
    public static final int CLOSE_TIME = 2000;
    protected NetworkConnection networkConnection;
    protected CommaCounter commaCounter;
    protected Counter counter;
    protected InputKeyboardMethod inputKeyboardMethod;

    protected final int REQUEST_CODE_PERMISSIONS = 10011;
    protected final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    protected TextureView mCameraView;
    protected TextView mOnlineCount, mDisplay, mTimer, mTag, mClapsCount, mPromptCount;
    protected ImageView mProfileBtn, mSwapBtn, mMicBtn, mFlashBtn, mNoteBtn, mTvBtn,
            mCommentBtn, mStreamView, mFlagBtn, mSettingsBtn, mNavBtn, mClapsBtn;
    protected GifImageView mNoStreamView;
    protected Button mEndLiveBtn;
    protected EditText mCommentField;
    protected RelativeLayout mMainRel, mBottomRel;
    protected LinearLayout mPromptContainer;
    protected RecyclerView mCommentList;
    protected CommentAdapter commentAdapter;
    protected CommentViewModel commentViewModel;
    protected LinearLayoutManager mLayoutManager;

    protected static final int app_version = 1;

    //===================== Default =============================
    protected String naira = "450";
    protected JSONObject streamer_data;
    protected JSONObject profile;
    protected int img;
    protected String username, location, face, insta, twit, web, bio, flagUTC, opt1, opt2, opt3;
    protected boolean isHelp = false;

    //====================== Stream ============================
    protected Stream stream;
    public static boolean isFacingBack = false, isMic = false,
            isLive = false, isFlag = false, mShouldContinue = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_home);
        app = (App) getApplication();
        networkConnection = new NetworkConnection();
        commaCounter = new CommaCounter();
        counter = new Counter();
        inputKeyboardMethod = new InputKeyboardMethod();

        font = Typeface.createFromAsset(getAssets(), FONT);

        mSettingsBtn = findViewById(R.id.settings_btn);
        mCameraView = findViewById(R.id.camera_view);
        mStreamView = findViewById(R.id.stream_view);
        mNoStreamView = findViewById(R.id.no_stream_view);
        mOnlineCount = findViewById(R.id.online_count);
        mProfileBtn = findViewById(R.id.mainImg);
        mSwapBtn = findViewById(R.id.swap_btn);
        mMicBtn = findViewById(R.id.mic_btn);
        mFlashBtn = findViewById(R.id.flash_btn);
        mCommentList = findViewById(R.id.comment_list);
        mCommentBtn = findViewById(R.id.comment_btn);
        mCommentField = findViewById(R.id.comment_field);
        mFlagBtn = findViewById(R.id.flag_btn);
        mEndLiveBtn = findViewById(R.id.end_live_btn);
        mNavBtn = findViewById(R.id.nav_btn);
        mTag = findViewById(R.id.stream_tag);
        mTvBtn = findViewById(R.id.tv_btn);
        mClapsCount = findViewById(R.id.claps_count);
        mClapsBtn = findViewById(R.id.claps_btn);
        mTimer = findViewById(R.id.timer_count);
        mDisplay = findViewById(R.id.display_tag);
        mNoteBtn = findViewById(R.id.note_btn);
        mMainRel = findViewById(R.id.main_rel);
        mBottomRel = findViewById(R.id.bottom_rel);
        mPromptCount = findViewById(R.id.prompt_count);
        mPromptContainer = findViewById(R.id.prompt_container);

        stream = new Stream(this);
        isHelp = mPreferences.getBoolean(HELP, true);
        start_setup();
        functions();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        if (!networkConnection.isConnected(this)) {
            networkConnection.noInternet(this, font);
        }
        set_profile();
    }

    protected void set_profile() {
        try {
            img = mPreferences.getInt(IMAGE, 0);
            username = mPreferences.getString(USERNAME, "");
            location = mPreferences.getString(LOCATION, "");
            face = mPreferences.getString(FACEBOOK, "");
            insta = mPreferences.getString(INSTAGRAM, "");
            twit = mPreferences.getString(TWITTER, "");
            web = mPreferences.getString(WEBSITE, "");
            bio = mPreferences.getString(BIO, "");
            flagUTC = mPreferences.getString(FLAG_UTC, "");

            opt1 = mPreferences.getString(OPT1, "");
            opt2 = mPreferences.getString(OPT2, "");
            opt3 = mPreferences.getString(OPT3, "");

            profile = new JSONObject();
            profile.put(IMAGE, img);
            profile.put(USERNAME, username);
            profile.put(LOCATION, location);
            profile.put(BIO, bio);
            profile.put(FACEBOOK, face);
            profile.put(INSTAGRAM, insta);
            profile.put(TWITTER, twit);
            profile.put(WEBSITE, web);
        } catch (JSONException js) {
            js.printStackTrace();
        }
    }

    protected void start_setup() {
        if (app.getSocketStream() != null) {
            app.getSocketStream().connect();
            app.getSocketStream().on(CAMERA, camera_listener);
            app.getSocketStream().on(AUDIO, audio_listener);
            app.getSocketStream().on(ONLINE, online_listener);
            app.getSocketStream().on(UPDATE, update_listener);
            app.getSocketStream().on(LIVE, live_listener);
            app.getSocketStream().on(STREAM, stream_listener);
            app.getSocketStream().on(TIMER, timer_listener);
            app.getSocketStream().on(VALUES, values_listener);
            app.getSocketStream().on(CLAPS, claps_listener);
            app.getSocketStream().on(NOTE, note_listener);
            app.getSocketStream().on(PROMPT, prompt_listener);

            commentAdapter = new CommentAdapter();
            mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setReverseLayout(false);
            mLayoutManager.setStackFromEnd(true);
            mCommentList.setLayoutManager(mLayoutManager);
            mCommentList.setHasFixedSize(true);
            mCommentList.setItemViewCacheSize(0);
            mCommentList.setAdapter(commentAdapter);

            commentViewModel = new ViewModelProvider(this).get(CommentViewModel.class);
            commentViewModel.setCommentViewModel(this);
            commentViewModel.getListMutableLiveData().
                    observe(this, new Observer<List<JSONObject>>() {
                        @Override
                        public void onChanged(List<JSONObject> jsonObjects) {
                            commentAdapter.setCommentModelList(HomeActivity.this, jsonObjects);
                            commentAdapter.notifyDataSetChanged();
                            mCommentList.scrollToPosition(jsonObjects.size() - 1);
                        }
                    });
        } else {
            app.initialize();
        }
    }

    protected void functions() {
        if(isHelp){
            helpNotice(HomeActivity.this, font);
        }
        mDisplay.setSelected(true);
        mDisplay.setEnabled(false);
        stream.openStream();
        disableCamUI();
        mFlagBtn.setEnabled(false);

        mTvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!allPermissionsGranted()) {
                    ActivityCompat.requestPermissions(HomeActivity.this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
                } else {
                    if (networkConnection.isConnected(HomeActivity.this)) {
                        if (!app.getSocketStream().connected()) {
                            start_setup();
                            Toast.makeText(HomeActivity.this,
                                    getResources().getString(R.string.connecting_to_server),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (mTvBtn.getVisibility() == View.VISIBLE) {
                            goLiveFunction();
                        }
                    } else {
                        showToast(HomeActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT);
                    }
                }
            }
        });

        mEndLiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (networkConnection.isConnected(HomeActivity.this)) {
                    if (!app.getSocketStream().connected()) {
                        start_setup();
                        Toast.makeText(HomeActivity.this,
                                getResources().getString(R.string.connecting_to_server),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (mEndLiveBtn.getVisibility() == View.VISIBLE) {
                        endFunction();
                    }
                } else {
                    showToast(HomeActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT);
                }
            }
        });

        mSwapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFacingBack = !isFacingBack;
                offTorch();
                stream.startCamera(mCameraView);
            }
        });

        mFlashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (preview != null) {
                    loadPreviewTorch();
                }
            }
        });

        mMicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isMic) {
                    mShouldContinue = true;
                    stream.startStream();
                    isMic = false;
                    mMicBtn.setImageDrawable(ContextCompat.getDrawable(HomeActivity.this, R.mipmap.mic_block));
                } else {
                    mShouldContinue = false;
                    stream.stopStream();
                    isMic = true;
                    mMicBtn.setImageDrawable(ContextCompat.getDrawable(HomeActivity.this, R.mipmap.mic));
                }
            }
        });

        mProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (streamer_data != null) {
                    loadProfileDialog(streamer_data);
                }
            }
        });

        mNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (img > -1 && !TextUtils.isEmpty(username)) {
                    new PublishDialog(HomeActivity.this).showPublish(profile);
                    overridePendingTransition(R.anim.dialog_enter, R.anim.no_anim);
                } else {
                    showToast(HomeActivity.this,
                            getResources().getString(R.string.no_username_img),
                            Toast.LENGTH_LONG);
                }
            }
        });

        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSocket();
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.no_anim);
            }
        });

        mNavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomRel.getVisibility() == View.VISIBLE) {
                    mBottomRel.setVisibility(View.GONE);
                    mNavBtn.setImageDrawable(ContextCompat.getDrawable(HomeActivity.this, R.mipmap.dropup));
                } else {
                    mBottomRel.setVisibility(View.VISIBLE);
                    mNavBtn.setImageDrawable(ContextCompat.getDrawable(HomeActivity.this, R.mipmap.dropdown));
                }
            }
        });

        mClapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (networkConnection.isConnected(HomeActivity.this)) {
                    if (!app.getSocketStream().connected()) {
                        start_setup();
                        Toast.makeText(HomeActivity.this,
                                getResources().getString(R.string.connecting_to_server),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (streamer_data != null) {
                        app.getSocketStream().emit(CLAPS, app.getSocketStream().id());
                        mClapsBtn.setImageDrawable(ContextCompat.getDrawable(HomeActivity.this, R.mipmap.clap_se));
                    }
                }else{
                    showToast(HomeActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT);
                }
            }
        });

        mFlagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(HomeActivity.this, mFlagBtn);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.stream_menu, popup.getMenu());

                //===================== Menu ====================================
                Menu m = popup.getMenu();
                for (int i = 0; i < m.size(); i++) {
                    MenuItem mi = m.getItem(i);

                    //for applying a font to subMenu ...
                    SubMenu subMenu = mi.getSubMenu();
                    if (subMenu != null && subMenu.size() > 0) {
                        for (int j = 0; j < subMenu.size(); j++) {
                            MenuItem subMenuItem = subMenu.getItem(j);
                            applyFontToMenuItem(subMenuItem);
                        }
                    }
                    //the method we have create in activity
                    applyFontToMenuItem(mi);
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.action_report) {

                            try {
                                app.getSocketStream().emit(FLAG, app.getSocketStream().id());
                                if (streamer_data != null) {
                                    flagUTC = streamer_data.getString(UTC);
                                    mEditor.putString(FLAG_UTC, streamer_data.getString(UTC));
                                    mEditor.apply();
                                }
                                isFlag = true;
                                updateUI();
                                showToast(HomeActivity.this, getResources().getString(R.string.report_sent), Toast.LENGTH_SHORT);
                                return true;
                            } catch (JSONException js) {
                                js.printStackTrace();
                            }
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        mCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (networkConnection.isConnected(HomeActivity.this)) {
                String comment = mCommentField.getText().toString().trim();
                if (!TextUtils.isEmpty(comment)) {
                    if (!app.getSocketStream().connected()) {
                        start_setup();
                        Toast.makeText(HomeActivity.this,
                                getResources().getString(R.string.connecting_to_server),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    set_profile();

                    try {
                        if (img > -1 && !TextUtils.isEmpty(username)) {
                            Date utc = new Date(System.currentTimeMillis());

                            JSONObject object = new JSONObject();
                            object.put(UTC, utc.toString());
                            object.put(IMAGE, img);
                            object.put(USERNAME, username);
                            object.put(COMMENT_TEXT, comment);
                            object.put(DATA, profile);

                            inputKeyboardMethod.hideKeyboard(HomeActivity.this);
                            app.getSocketStream().emit(COMMENT, object);
                            mCommentField.setText(null);
                        } else {
                            showToast(HomeActivity.this,
                                    getResources().getString(R.string.no_username_img),
                                    Toast.LENGTH_LONG);
                        }
                    } catch (JSONException js) {
                        js.printStackTrace();
                    }
                }
                } else {
                    showToast(HomeActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT);
                }
            }
        });
    }

    protected void applyFontToMenuItem(MenuItem mi) {
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    protected void clearCommentList() {
        if (commentAdapter.getCommentModelList() != null && commentAdapter.getCommentModelList().size() > 0) {
            commentAdapter.getCommentModelList().clear();
            commentAdapter.notifyDataSetChanged();
        }
    }

    protected void loadProfileDialog(JSONObject jsonObject) {
        new ProfileDialog(HomeActivity.this).showProfile(jsonObject);
        overridePendingTransition(R.anim.dialog_enter, R.anim.no_anim);
    }

    protected void loadPreviewTorch() {
        if (preview.isTorchOn()) {
            preview.enableTorch(false);
            mFlashBtn.setImageDrawable(ContextCompat.getDrawable(HomeActivity.this, R.mipmap.flash_on));
        } else {
            preview.enableTorch(true);
            mFlashBtn.setImageDrawable(ContextCompat.getDrawable(HomeActivity.this, R.mipmap.flash_off));
        }
    }

    protected void enableCamUI() {
        mSwapBtn.setEnabled(true);
        mMicBtn.setEnabled(true);
        mFlashBtn.setEnabled(true);
        mNoteBtn.setEnabled(true);

        mNoteBtn.setAlpha(1f);
        mSwapBtn.setAlpha(1f);
        mMicBtn.setAlpha(1f);
        mFlashBtn.setAlpha(1f);
    }

    protected void disableCamUI() {
        mSwapBtn.setEnabled(false);
        mMicBtn.setEnabled(false);
        mFlashBtn.setEnabled(false);
        // mNoteBtn.setEnabled(false);

        // mNoteBtn.setAlpha(.7f);
        mSwapBtn.setAlpha(.7f);
        mMicBtn.setAlpha(.7f);
        mFlashBtn.setAlpha(.7f);
    }

    protected void goLiveFunction() {
        // TODO: Check suspension
        //suspensionNotice(HomeActivity.this, font, getTimeAgo(timestamp.toDate().getTime(), HomeActivity.this));
        set_profile();
        if (img > -1 && !TextUtils.isEmpty(username)) {
            new StartStreamDialog(HomeActivity.this).showDialog(profile, naira);
            overridePendingTransition(R.anim.dialog_enter, R.anim.no_anim);
        } else {
            showToast(HomeActivity.this, getResources().getString(R.string.no_username_img), Toast.LENGTH_LONG);
        }
    }


    protected boolean allPermissionsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : REQUIRED_PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                showToast(HomeActivity.this, getResources().getString(R.string.permission_return), Toast.LENGTH_LONG);
            }
        }
    }
    protected final Emitter.Listener camera_listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject object = (JSONObject) args[0];
                        byte[] data = (byte[]) object.get(DATA);
                        //byte[] data = decompressor((byte[]) object.get(DATA));
                        int rotationDeg = object.getInt(ROTATION);
                        boolean isBack = object.getBoolean(IS_BACK);

                        if (isBack) {
                            mStreamView.setScaleX(1);
                        } else {
                            mStreamView.setScaleX(-1);
                        }

                        mStreamView.setImageBitmap(setCamStream(data, rotationDeg));
                        /*if (isBlur) {
                            Bitmap blurBitMap = BlurBuilder.blur(HomeActivity.this, setCamStream(data, rotationDeg));
                            mStreamView.setImageBitmap(blurBitMap);
                        } else {
                            mStreamView.setImageBitmap(setCamStream(data, rotationDeg));
                        }*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    protected final Emitter.Listener audio_listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONObject object = (JSONObject) args[0];
                byte[] result = (byte[]) object.get(DATA);
                //byte[] result = decompressor((byte[]) object.get(DATA));

                if (audioTrack != null && !isFlag) {
                    audioTrack.write(result, 0, Objects.requireNonNull(result).length);
                }
            } catch (Exception a) {
                a.printStackTrace();
            }
        }
    };

    protected final Emitter.Listener online_listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int count = (int) args[0];
                    mOnlineCount.setText(commaCounter.getFormattedNumber(String.valueOf(count)));
                }
            });
        }
    };

    protected final Emitter.Listener prompt_listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int count = (int) args[0];
                    mPromptContainer.setVisibility(View.VISIBLE);
                    if (count == 0){
                        mPromptCount.setText(getResources().getString(R.string.now));
                        mPromptCount.setTextColor(getResources().getColor(R.color.trans_accent));
                    }else{
                        mPromptCount.setText(counter.countVal(count, HomeActivity.this));
                    }
                }
            });
        }
    };

    protected final Emitter.Listener note_listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject obj = (JSONObject) args[0];
                        if (!TextUtils.isEmpty(obj.getString(CONTENT))) {
                            mDisplay.setEnabled(true);
                            mDisplay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    new NoteDialog(HomeActivity.this).showNote(obj);
                                }
                            });

                            mDisplay.setText(obj.getString(CONTENT));
                            mDisplay.setVisibility(View.VISIBLE);
                        } else {
                            mDisplay.setEnabled(false);
                            mDisplay.setVisibility(View.GONE);
                        }
                    } catch (JSONException js) {
                        js.printStackTrace();
                    }
                }
            });
        }
    };

    protected final Emitter.Listener claps_listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try{
                        JSONObject obj = (JSONObject) args[0];
                        String Id = obj.getString(USER_ID);
                        int count = obj.getInt(CLAPS);

                        mClapsCount.setText(counter.countVal(count, HomeActivity.this));
                        if (!TextUtils.isEmpty(Id) && Id.equals(app.getSocketStream().id())){
                            mClapsBtn.setImageDrawable(ContextCompat.getDrawable(HomeActivity.this, R.mipmap.clap_se));
                        }
                    }catch (JSONException j){
                        j.printStackTrace();
                    }
                }
            });
        }
    };

    protected final Emitter.Listener update_listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int ver = (int) args[0];
                    if (ver > app_version) {
                        AppUpdate appUpdate = new AppUpdate(HomeActivity.this, font);
                        appUpdate.appUpdate();
                    }
                }
            });
        }
    };

    protected final Emitter.Listener values_listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject obj = (JSONObject) args[0];
                        naira = obj.getString(NAIRA);
                    } catch (JSONException js) {
                        js.printStackTrace();
                    }
                }
            });
        }
    };

    protected final Emitter.Listener timer_listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int time = (int) args[0];
                    mTimer.setText(secToTime(time));
                }
            });
        }
    };

    protected final Emitter.Listener stream_listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject obj = (JSONObject) args[0];
                        if (obj.getInt(STATE) == 0) {
                            showToast(HomeActivity.this,
                                    getResources().getString(R.string.full_queue), Toast.LENGTH_LONG);
                            return;
                        } else if (obj.getInt(STATE) == 1) {
                            showToast(HomeActivity.this,
                                    getResources().getString(R.string.added_to_queue), Toast.LENGTH_LONG);
                            return;
                        } else if (obj.getInt(STATE) == 2) {
                            setStreamData(obj);
                            startLive(obj);
                            return;
                        } else {
                            mEndLiveBtn.setVisibility(View.GONE);
                            mTvBtn.setVisibility(View.VISIBLE);
                            endCompleted();
                            return;
                        }
                    } catch (JSONException js) {
                        js.printStackTrace();
                    }
                }
            });
        }
    };

    protected final Emitter.Listener live_listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject obj = (JSONObject) args[0];
                        boolean check = obj.getBoolean(STREAM_LIVE);
                        if (check) {
                            setStreamData(obj);

                            if (!TextUtils.isEmpty(flagUTC) && obj.getString(UTC).equals(flagUTC)) {
                                endCompleted();
                                isFlag = true;
                            } else {
                                if (!TextUtils.isEmpty(flagUTC)) {
                                    mEditor.remove(FLAG_UTC);
                                    mEditor.apply();
                                }
                                showStreamView();
                            }
                        } else {
                            endCompleted();
                        }
                    } catch (JSONException js) {
                        js.printStackTrace();
                    }
                }
            });
        }
    };

    protected void setStreamData(JSONObject obj) {
        try {
            streamer_data = new JSONObject();
            streamer_data.put(USER_ID, obj.getString(USER_ID));
            streamer_data.put(UTC, obj.getString(UTC));
            streamer_data.put(IMAGE, obj.getInt(IMAGE));
            streamer_data.put(USERNAME, obj.getString(USERNAME));
            streamer_data.put(LOCATION, obj.getString(LOCATION));
            streamer_data.put(BIO, obj.getString(BIO));
            streamer_data.put(FACEBOOK, obj.getString(FACEBOOK));
            streamer_data.put(INSTAGRAM, obj.getString(INSTAGRAM));
            streamer_data.put(TWITTER, obj.getString(TWITTER));
            streamer_data.put(WEBSITE, obj.getString(WEBSITE));
            streamer_data.put(Constant.TAG, obj.getString(Constant.TAG));

            mProfileBtn.setImageDrawable(setAvatar(HomeActivity.this, obj.getInt(IMAGE)));
            mTag.setText(getResources().getString(R.string.tag) + obj.getString(Constant.TAG));
        } catch (JSONException js) {
            js.printStackTrace();
        }
    }

    protected void showStreamView() {
        inputKeyboardMethod.hideKeyboard(HomeActivity.this);
        disableCamUI();
        mNoStreamView.setVisibility(View.GONE);
        mCameraView.setVisibility(View.GONE);
        mStreamView.setVisibility(View.VISIBLE);
        mSettingsBtn.setEnabled(true);
        mFlagBtn.setEnabled(true);
        mFlagBtn.setVisibility(View.VISIBLE);
    }

    protected void startLive(JSONObject obj) {
        try{
            inputKeyboardMethod.hideKeyboard(HomeActivity.this);
            mShouldContinue = true;
            isMic = false;
            isLive = true;

            enableCamUI();
            stream.startStream();
            stream.startCamera(mCameraView);

            mNoStreamView.setVisibility(View.GONE);
            mStreamView.setVisibility(View.GONE);
            mCameraView.setVisibility(View.VISIBLE);
            mSettingsBtn.setEnabled(false);
            mSettingsBtn.setAlpha(.7f);
            mFlagBtn.setEnabled(false);
            mFlagBtn.setVisibility(View.GONE);
            mEndLiveBtn.setVisibility(View.VISIBLE);
            mTvBtn.setVisibility(View.GONE);

            String token = obj.getString(TOKEN);
            if (opt1.equals(token)){
                mEditor.remove(OPT1);
                mEditor.apply();
            }else if (opt2.equals(token)){
                mEditor.remove(OPT2);
                mEditor.apply();
            }else if(opt3.equals(token)){
                mEditor.remove(OPT3);
                mEditor.apply();
            }
        }catch (JSONException js){
            js.printStackTrace();
        }
    }

    protected void endFunction() {
        app.getSocketStream().emit(END, app.getSocketStream().id());
        mEndLiveBtn.setVisibility(View.GONE);
        mTvBtn.setVisibility(View.VISIBLE);
        unbindAll();
        endCompleted();
    }

    protected void endCompleted() {
        mPromptContainer.setVisibility(View.GONE);
        mShouldContinue = false;
        isMic = false;
        isLive = false;
        isFlag = false;
        streamer_data = null;
        mProfileBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.circle_bg, null));
        mClapsBtn.setImageDrawable(ContextCompat.getDrawable(HomeActivity.this, R.mipmap.clap));
        stream.stopStream();
        offTorch();
        disableCamUI();
        updateUI();
    }

    protected void updateUI() {
        mSettingsBtn.setEnabled(true);
        mSettingsBtn.setAlpha(1f);
        mStreamView.setImageBitmap(null);
        mStreamView.setVisibility(View.GONE);
        mCameraView.setVisibility(View.GONE);
        mSwapBtn.setVisibility(View.VISIBLE);
        mMicBtn.setVisibility(View.VISIBLE);
        mFlashBtn.setVisibility(View.VISIBLE);
        mFlagBtn.setVisibility(View.VISIBLE);
        mNoStreamView.setVisibility(View.VISIBLE);
    }

    protected void setSuspension() {
        final Date utc = new Date(System.currentTimeMillis());
        Map<String, Object> setMap = new HashMap<>();
        setMap.put(SUSPENSION, utc);
    }

    protected void suspensionDisplay() {
        showToast(HomeActivity.this, getResources().getString(R.string.suspension), Toast.LENGTH_LONG);
        endFunction();
    }

    protected void offTorch() {
        if (preview != null && preview.isTorchOn()) {
            preview.enableTorch(false);
            mFlashBtn.setImageDrawable(ContextCompat.getDrawable(HomeActivity.this, R.mipmap.flash_on));
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondTime = System.currentTimeMillis();
            if (!isLive) {
                if (secondTime - firstTime > CLOSE_TIME) {
                    showToast(HomeActivity.this, getResources().getString(R.string.back_exit), Toast.LENGTH_SHORT);
                    firstTime = secondTime;
                    return true;
                } else {
                    closeSocket();
                    clearLive();
                    System.exit(0);
                }
            } else {
                AlertDialog internetDialog = new AlertDialog.Builder(HomeActivity.this).create();
                internetDialog.setMessage(getResources().getString(R.string.stay_msg));
                internetDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.stay), null, null);
                internetDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.leave),
                        null, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                closeSocket();
                                clearLive();
                                System.exit(0);
                            }
                        });
                internetDialog.show();

                TextView textView = internetDialog.getWindow().findViewById(android.R.id.message);
                Button button1 = internetDialog.getWindow().findViewById(android.R.id.button1);
                Button button2 = internetDialog.getWindow().findViewById(android.R.id.button2);

                textView.setTypeface(font);
                button1.setTypeface(font);
                button2.setTypeface(font);

                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mEditor.putBoolean(HELP, false);
        mEditor.apply();
    }

    protected void closeSocket() {
        if (app.getSocketStream() != null) {

            if (isLive) {
                app.getSocketStream().emit(END, app.getSocketStream().id());
                unbindAll();
                isLive = false;
            }

            app.getSocketStream().disconnect();
            app.getSocketStream().off(CAMERA, camera_listener);
            app.getSocketStream().off(AUDIO, audio_listener);
            app.getSocketStream().off(ONLINE, online_listener);
            app.getSocketStream().off(UPDATE, update_listener);
            app.getSocketStream().off(LIVE, live_listener);
            app.getSocketStream().off(STREAM, stream_listener);
            app.getSocketStream().off(TIMER, timer_listener);
            app.getSocketStream().off(VALUES, values_listener);
            app.getSocketStream().off(CLAPS, claps_listener);
            app.getSocketStream().off(NOTE, note_listener);
            app.getSocketStream().off(PROMPT, prompt_listener);
        }
    }

    protected void clearLive() {
        stream.closeStream();
        stream.stopStream();
        offTorch();
        preview = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeSocket();
        clearLive();
    }
}