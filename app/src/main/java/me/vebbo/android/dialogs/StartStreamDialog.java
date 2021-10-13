package me.vebbo.android.dialogs;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import io.socket.emitter.Emitter;
import me.vebbo.android.App;
import me.vebbo.android.R;
import me.vebbo.android.activities.HomeActivity;
import me.vebbo.android.interfaces.StartListener;
import me.vebbo.android.utils.AppUpdate;
import me.vebbo.android.utils.NetworkConnection;

import static me.vebbo.android.utils.Constant.CHECK;
import static me.vebbo.android.utils.Constant.ENV;
import static me.vebbo.android.utils.Constant.EXISTS;
import static me.vebbo.android.utils.Constant.OPT1;
import static me.vebbo.android.utils.Constant.OPT2;
import static me.vebbo.android.utils.Constant.OPT3;
import static me.vebbo.android.utils.Constant.PLATFORM;
import static me.vebbo.android.utils.Constant.SELECTED;
import static me.vebbo.android.utils.Constant.STREAM;
import static me.vebbo.android.utils.Constant.TAG;
import static me.vebbo.android.utils.Constant.TOKEN;
import static me.vebbo.android.utils.Constant.UPDATE;
import static me.vebbo.android.utils.Constant.USER_ID;
import static me.vebbo.android.utils.Constant.UTC;
import static me.vebbo.android.utils.Manage.showToast;

public class StartStreamDialog {

    private App app;
    private AppCompatActivity context;
    private Dialog dialog;
    private Window window;
    private NetworkConnection networkConnection;
    private EditText tagField;
    private Button continueBtn;
    private int selected = 0;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private LinearLayout mainLin, lin2, lin3, lin4;
    private TextView min1, char_count;
    private ImageView img2, img3, img4, closeBtn;
    private String opt1, opt2, opt3, tag;
    private JSONObject profile;

    public StartStreamDialog(AppCompatActivity context) {
        this.context = context;
        app = (App) context.getApplication();
        dialog = new Dialog(this.context);
        window = dialog.getWindow();
        networkConnection = new NetworkConnection();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mPreferences.edit();
    }

    public void showDialog(JSONObject profile, String naira) {
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(.7f);
        }
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_start_stream);

        this.profile = profile;
        char_count = dialog.findViewById(R.id.tag_count);
        tagField = dialog.findViewById(R.id.tagField);
        continueBtn = dialog.findViewById(R.id.continue_btn);
        mainLin = dialog.findViewById(R.id.mainLin);
        min1 = dialog.findViewById(R.id.min1);
        lin2 = dialog.findViewById(R.id.lin2);
        lin3 = dialog.findViewById(R.id.lin3);
        lin4 = dialog.findViewById(R.id.lin4);
        img2 = dialog.findViewById(R.id.lock2);
        img3 = dialog.findViewById(R.id.lock3);
        img4 = dialog.findViewById(R.id.lock4);
        closeBtn = dialog.findViewById(R.id.closeBroadBtn);

        try {
            enableInit();
            updateUI();
            opt1 = mPreferences.getString(OPT1, "");
            opt2 = mPreferences.getString(OPT2, "");
            opt3 = mPreferences.getString(OPT3, "");

            if (!TextUtils.isEmpty(opt1)) {
                img2.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.unlock));
            }
            if (!TextUtils.isEmpty(opt2)) {
                img3.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.unlock));
            }
            if (!TextUtils.isEmpty(opt3)) {
                img4.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.unlock));
            }

            min1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    min1.setTextColor(context.getResources().getColor(R.color.colorAccent));
                    lin2.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_item_selection2, null));
                    lin3.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_item_selection2, null));
                    lin4.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_item_selection3, null));
                    selected = 5;
                }
            });

            lin2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    min1.setTextColor(context.getResources().getColor(R.color.black));
                    lin2.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_item_selected2, null));
                    lin3.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_item_selection2, null));
                    lin4.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_item_selection3, null));

                    selected = 10;
                }
            });

            lin3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    min1.setTextColor(context.getResources().getColor(R.color.black));
                    lin2.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_item_selection2, null));
                    lin3.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_item_selected2, null));
                    lin4.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_item_selection3, null));
                    selected = 20;
                }
            });

            lin4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    min1.setTextColor(context.getResources().getColor(R.color.black));
                    lin2.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_item_selection2, null));
                    lin3.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_item_selection2, null));
                    lin4.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_item_selected3, null));

                    selected = 30;
                }
            });

            tagField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String text = tagField.getText().toString();
                    text = text.replace("\n", " ");
                    String result = text.toCharArray().length + context.getResources().getString(R.string._16);
                    char_count.setText(result);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            continueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (networkConnection.isConnected(context)) {
                        tag = tagField.getText().toString().trim();
                        if (selected == 0) {
                            showToast(context,
                                    context.getResources().getString(R.string.select_time),
                                    Toast.LENGTH_SHORT);
                            return;
                        }
                        if (TextUtils.isEmpty(tag)) {
                            showToast(context,
                                    context.getResources().getString(R.string.field_empty),
                                    Toast.LENGTH_SHORT);
                            return;
                        }

                        if (selected == 5) {
                            final String token = UUID.randomUUID().toString();
                            loadStream(tag, profile, token);
                            return;
                        }
                        if (selected == 10 && TextUtils.isEmpty(opt1)) {
                            new AddCardDialog(context).saveCard(true, naira, "2.99", selected, dialog);
                            return;
                        }else if (selected == 20 && TextUtils.isEmpty(opt2)) {
                            new AddCardDialog(context).saveCard(true, naira, "4.99", selected, dialog);
                            return;
                        }if (selected == 30 && TextUtils.isEmpty(opt3)) {
                            new AddCardDialog(context).saveCard(true, naira, "6.99", selected, dialog);
                            return;
                        } else {
                            if (selected == 10){
                                checkToken(opt1);
                            }else if(selected == 20){
                                checkToken(opt2);
                            }else if(selected == 30){
                                checkToken(opt3);
                            }
                        }
                    } else {
                        showToast(context, context.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT);
                    }
                }
            });

            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeDialog();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void setUpdate(int timeVal, String token){
        if (timeVal == 10){
            opt1 = token;
            img2.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.unlock));
        }else if(timeVal == 20){
            opt2 = token;
            img3.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.unlock));
        }else {
            opt3 = token;
            img4.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.unlock));
        }
    }

    protected void checkToken(String token) {
        try {
            JSONObject obj = new JSONObject();
            obj.put(TOKEN, token);
            obj.put(SELECTED, selected);
            app.getSocketStream().emit(CHECK, obj);
            app.getSocketStream().on(CHECK, check_listener);
        } catch (JSONException js) {
            js.printStackTrace();
        }
    }

    protected final Emitter.Listener check_listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject result = (JSONObject) args[0];
                        boolean exist = result.getBoolean(EXISTS);
                        if (exist) {
                            loadStream(tag, profile, result.getString(TOKEN));
                        } else {
                            enableInit();
                            if (selected == 10){
                                img2.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.lock));
                                mEditor.remove(OPT1);
                                mEditor.apply();
                            }else if (selected == 20){
                                img3.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.lock));
                                mEditor.remove(OPT2);
                                mEditor.apply();
                            }else if(selected == 30){
                                img4.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.lock));
                                mEditor.remove(OPT3);
                                mEditor.apply();
                            }
                            showToast(context,
                                    context.getResources().getString(R.string.token_not_existing),
                                    Toast.LENGTH_SHORT);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    protected void disableInit(){
        mainLin.setEnabled(false);
        tagField.setEnabled(false);
        continueBtn.setEnabled(false);
    }

    protected void enableInit(){
        mainLin.setEnabled(true);
        tagField.setEnabled(true);
        continueBtn.setEnabled(true);
    }

    protected void loadStream(String tag, JSONObject profile, String token) {
        try {
            final Date utc = new Date(System.currentTimeMillis());
            profile.put(USER_ID, app.getSocketStream().id());
            profile.put(TOKEN, token);
            profile.put(UTC, utc);
            profile.put(SELECTED, String.valueOf(selected));
            profile.put(PLATFORM, ENV);
            profile.put(TAG, tag);
            app.getSocketStream().emit(STREAM, profile);

            enableInit();
            closeDialog();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(){
        selected = 0;
        min1.setTextColor(context.getResources().getColor(R.color.black));
        lin2.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_item_selection2, null));
        lin3.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_item_selection2, null));
        lin4.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_item_selection3, null));
    }

    protected void closeDialog() {
        app.getSocketStream().off(CHECK, check_listener);
        updateUI();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.dismiss();
    }
}
