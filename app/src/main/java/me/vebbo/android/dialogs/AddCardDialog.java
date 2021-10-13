package me.vebbo.android.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import me.vebbo.android.activities.EditAccountActivity;
import me.vebbo.android.interfaces.StartListener;

import static me.vebbo.android.utils.Constant.CARD_NAME;
import static me.vebbo.android.utils.Constant.CARD_NUMBER;
import static me.vebbo.android.utils.Constant.EMAIL;
import static me.vebbo.android.utils.Constant.MONTH;
import static me.vebbo.android.utils.Constant.OPT1;
import static me.vebbo.android.utils.Constant.OPT2;
import static me.vebbo.android.utils.Constant.OPT3;
import static me.vebbo.android.utils.Constant.STORE;
import static me.vebbo.android.utils.Constant.TOKEN;
import static me.vebbo.android.utils.Constant.USER_ID;
import static me.vebbo.android.utils.Constant.UTC;
import static me.vebbo.android.utils.Constant.YEAR;
import static me.vebbo.android.utils.Manage.showToast;

public class AddCardDialog {

    private App app;
    private Dialog dialog;
    private Window window;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private EditText mEmailField, mCardNameField, mCardNumberField,
            mMonthField, mYearField, mCVVField;
    private Button mSaveBtn;
    private ImageView mCloseBtn;
    private TextView mAddCardText;
    private AppCompatActivity context;

    private Card card;
    private Charge charge;
    private Transaction transact = null;
    private Dialog subDialog;

    public AddCardDialog(AppCompatActivity context) {
        this.context = context;
        app = (App) context.getApplication();
        dialog = new Dialog(this.context);
        window = dialog.getWindow();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mPreferences.edit();
    }

    public void saveCard(boolean isFromStream, String naira, String amt, int timeVal, Dialog subDialog) {
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(.7f);
        }
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_add_card);

        this.subDialog = subDialog;
        mAddCardText = dialog.findViewById(R.id.addCardText);
        mEmailField = dialog.findViewById(R.id.edit_email_address);
        mCardNameField = dialog.findViewById(R.id.edit_card_name);
        mCardNumberField = dialog.findViewById(R.id.edit_card_number);
        mMonthField = dialog.findViewById(R.id.edit_expiry_month);
        mYearField = dialog.findViewById(R.id.edit_expiry_year);
        mCVVField = dialog.findViewById(R.id.edit_cvv);
        mSaveBtn = dialog.findViewById(R.id.save_card_button);
        mCloseBtn = dialog.findViewById(R.id.closeAddCardBtn);

        try {
            final String email = mPreferences.getString(EMAIL, "");
            final String cardName = mPreferences.getString(CARD_NAME, "");
            final String cardNum = mPreferences.getString(CARD_NUMBER, "");
            final int month = mPreferences.getInt(MONTH, 0);
            final int year = mPreferences.getInt(YEAR, 0);

            if (!TextUtils.isEmpty(email)) {
                mEmailField.setText(email);
            }
            if (!TextUtils.isEmpty(cardName)) {
                mCardNameField.setText(cardName);
            }
            if (!TextUtils.isEmpty(cardNum)) {
                mCardNumberField.setText(cardNum);
            }
            if (month != 0) {
                mMonthField.setText(String.valueOf(month));
            }
            if (year != 0) {
                mYearField.setText(String.valueOf(year));
            }

            if (!isFromStream) {
                mYearField.setImeOptions(EditorInfo.IME_ACTION_DONE);
                mCVVField.setEnabled(false);
            }else{
                mAddCardText.setText(context.getResources()
                        .getString(R.string.to_pay)
                        .concat(" ")
                        .concat(context.getResources()
                        .getString(R.string.dollar_sign)
                        .concat(amt)));

                mSaveBtn.setText(context.getResources().getString(R.string.proceed));
            }

            mSaveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!validateForm()) {
                        return;
                    }

                    final String cardEmail = mEmailField.getText().toString().trim();
                    final String cardNam = mCardNameField.getText().toString().trim();
                    final String cardNumber = mCardNumberField.getText().toString()
                            .replace("-", "").replace(" ", "").trim();
                    final int expiryMonth = Integer.parseInt(mMonthField.getText().toString().trim());
                    final int expiryYear = Integer.parseInt(mYearField.getText().toString().trim());
                    final String cvv = mCVVField.getText().toString().trim();

                    if (!isFromStream) {
                        saveDetails(cardEmail, cardNam, cardNumber, expiryMonth, expiryYear);
                        showToast(context, context.getResources().getString(R.string.saved), Toast.LENGTH_LONG);
                        closeIntent();
                    } else {
                        if (!validateCVV()) {
                            return;
                        }
                        saveDetails(cardEmail, cardNam, cardNumber, expiryMonth, expiryYear);
                        card = new Card(cardNumber, expiryMonth, expiryYear, cvv);

                        if (card.isValid()) {
                            disableInit();
                            performCharge(naira, cardEmail, timeVal);

                        } else {
                            showToast(context,
                                    context.getResources().getString(R.string.card_not_valid),
                                    Toast.LENGTH_LONG);
                        }
                    }
                }
            });
            mCloseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeIntent();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void saveDetails(String cardEmail, String cardNam, String cardNumber,
                             int expiryMonth, int expiryYear) {
        mEditor.putString(EMAIL, cardEmail);
        mEditor.putString(CARD_NAME, cardNam);
        mEditor.putString(CARD_NUMBER, cardNumber);
        mEditor.putInt(MONTH, expiryMonth);
        mEditor.putInt(YEAR, expiryYear);
        mEditor.apply();
    }

    protected void performCharge(String fee, String email, int timeVal) {
        String total = null;
        if (timeVal == 10) {
            total = String.valueOf(3 * Integer.parseInt(fee));
        } else if (timeVal == 20) {
            total = String.valueOf(5 * Integer.parseInt(fee));
        } else if (timeVal == 30) {
            total = String.valueOf(7 * Integer.parseInt(fee));
        }

        if (total != null) {
            charge = new Charge();
            charge.setCard(card);
            charge.setEmail(email);
            charge.setAmount(Integer.parseInt(total.concat("00")));
            charge.setReference("VebboAndroid_" + Calendar.getInstance().getTimeInMillis());

            PaystackSdk.chargeCard(context, charge, new Paystack.TransactionCallback() {
                @Override
                public void onSuccess(Transaction transaction) {
                    transact = transaction;

                    try {
                        final String token = UUID.randomUUID().toString();
                        final Date utc = new Date(System.currentTimeMillis());

                        JSONObject obj = new JSONObject();
                        obj.put(USER_ID, app.getSocketStream().id());
                        obj.put(TOKEN, token);
                        obj.put(UTC, utc);
                        app.getSocketStream().emit(STORE, obj);

                        if (timeVal == 10){
                            mEditor.putString(OPT1, token);
                            mEditor.apply();
                        }else if (timeVal == 20){
                            mEditor.putString(OPT2, token);
                            mEditor.apply();
                        }else {
                            mEditor.putString(OPT3, token);
                            mEditor.apply();
                        }

                        onSuccessful();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void beforeValidate(Transaction transaction) {
                }

                @Override
                public void onError(Throwable error, Transaction transaction) {
                    if (error instanceof ExpiredAccessCodeException) {
                        return;
                    }
                    enableInit();
                    transact = transaction;
                    if (transact != null) {
                        showToast(context, context.getResources().getString(R.string.transaction_err), Toast.LENGTH_LONG);
                    } else {
                        showToast(context, context.getResources().getString(R.string.transaction_failed), Toast.LENGTH_LONG);
                    }
                }
            });
        }
    }

    private void onSuccessful() {
        showToast(context, context.getResources().getString(R.string.completed), Toast.LENGTH_SHORT);
        enableInit();
        transact = null;
        closeIntent();
        if (subDialog != null){
            subDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            subDialog.dismiss();
        }
    }

    private void onFailed(Exception e) {
        showToast(context, context.getResources().getString(R.string.err) + e.getMessage(), Toast.LENGTH_LONG);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError(context.getResources().getString(R.string.required));
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String cardName = mCardNameField.getText().toString();
        if (TextUtils.isEmpty(cardName)) {
            mCardNameField.setError(context.getResources().getString(R.string.required));
            valid = false;
        } else {
            mCardNameField.setError(null);
        }

        String cardNumber = mCardNumberField.getText().toString();
        if (TextUtils.isEmpty(cardNumber)) {
            mCardNumberField.setError(context.getResources().getString(R.string.required));
            valid = false;
        } else {
            mCardNumberField.setError(null);
        }

        String expiryMonth = mMonthField.getText().toString();
        if (TextUtils.isEmpty(expiryMonth)) {
            mMonthField.setError(context.getResources().getString(R.string.required));
            valid = false;
        } else {
            mMonthField.setError(null);
        }

        String expiryYear = mYearField.getText().toString();
        if (TextUtils.isEmpty(expiryYear)) {
            mYearField.setError(context.getResources().getString(R.string.required));
            valid = false;
        } else {
            mYearField.setError(null);
        }

        return valid;
    }

    private boolean validateCVV() {
        boolean valid = true;

        String cvv = mCVVField.getText().toString();
        if (TextUtils.isEmpty(cvv)) {
            mCVVField.setError(context.getResources().getString(R.string.required));
            valid = false;
        } else {
            mCVVField.setError(null);
        }

        return valid;
    }

    private void disableInit() {
        mEmailField.setEnabled(false);
        mCardNameField.setEnabled(false);
        mCardNumberField.setEnabled(false);
        mMonthField.setEnabled(false);
        mYearField.setEnabled(false);
        mCVVField.setEnabled(false);
        mSaveBtn.setEnabled(false);
    }

    private void enableInit() {
        mEmailField.setEnabled(true);
        mCardNameField.setEnabled(true);
        mCardNumberField.setEnabled(true);
        mMonthField.setEnabled(true);
        mYearField.setEnabled(true);
        mCVVField.setEnabled(true);
        mSaveBtn.setEnabled(true);
    }

    private void closeIntent() {
        if (transact != null) {
            AlertDialog.Builder tipsAlert = new AlertDialog.Builder(context);
            tipsAlert.setMessage(context.getResources().getString(R.string.cancel_payment))
                    .setPositiveButton(context.getResources().getString(R.string.wait), null)
                    .setNegativeButton(context.getResources().getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    transact = null;
                                    closeDialog();
                                }
                            });
            AlertDialog start_builder = tipsAlert.create();
            start_builder.show();

        } else {
            closeDialog();
        }
    }

    private void closeDialog() {
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.dismiss();
    }
}
