package me.vebbo.android.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.exceptions.ExpiredAccessCodeException;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import me.vebbo.android.App;
import me.vebbo.android.R;
import me.vebbo.android.utils.NetworkConnection;

import static me.vebbo.android.utils.Constant.CARD_NAME;
import static me.vebbo.android.utils.Constant.CARD_NUMBER;
import static me.vebbo.android.utils.Constant.CONTENT;
import static me.vebbo.android.utils.Constant.EMAIL;
import static me.vebbo.android.utils.Constant.MONTH;
import static me.vebbo.android.utils.Constant.NOTE;
import static me.vebbo.android.utils.Constant.TOKEN;
import static me.vebbo.android.utils.Constant.UTC;
import static me.vebbo.android.utils.Constant.YEAR;
import static me.vebbo.android.utils.Manage.showToast;

public class PublishDialog {

    private App app;
    private AppCompatActivity context;
    private Dialog dialog;
    private Window window;
    private NetworkConnection networkConnection;
    private Button mPostBtn;
    private EditText mNote;
    private TextView mCharCount;

    private Card card;
    private Charge charge;
    private Transaction transact;

    public PublishDialog(AppCompatActivity context) {
        this.context = context;
        app = (App) context.getApplication();
        dialog = new Dialog(this.context);
        networkConnection = new NetworkConnection();
        window = dialog.getWindow();
    }

    public void showPublish(JSONObject profile) {
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(.7f);
        }
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_publish);

        ImageView closeBtn;

        mNote = dialog.findViewById(R.id.note_field);
        mCharCount = dialog.findViewById(R.id.note_count);
        mPostBtn = dialog.findViewById(R.id.note_post_btn);
        closeBtn = dialog.findViewById(R.id.close_publish_btn);

        try {
            mNote.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String text = mNote.getText().toString();
                    text = text.replace("\n", " ");
                    String result = text.toCharArray().length + context.getResources().getString(R.string._127);
                    mCharCount.setText(result);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            mPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String note = mNote.getText().toString().trim();
                    if (networkConnection.isConnected(context)) {
                        if (TextUtils.isEmpty(note)) {
                            showToast(context,
                                    context.getResources().getString(R.string.field_empty),
                                    Toast.LENGTH_LONG);
                            return;
                        }
                        try{
                            final String token = UUID.randomUUID().toString();
                            final Date utc = new Date(System.currentTimeMillis());

                            profile.put(CONTENT, note);
                            profile.put(TOKEN, token);
                            profile.put(UTC, utc);
                            app.getSocketStream().emit(NOTE, profile);
                            showToast(context,
                                    context.getResources().getString(R.string.note_sent),
                                    Toast.LENGTH_LONG);
                            closeDialog();
                        }catch (JSONException js){
                            js.printStackTrace();
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

    protected void closeDialog() {
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.dismiss();
    }
}