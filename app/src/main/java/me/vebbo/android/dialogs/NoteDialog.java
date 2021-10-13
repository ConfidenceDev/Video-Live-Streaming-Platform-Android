package me.vebbo.android.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.util.Objects;
import me.vebbo.android.App;
import me.vebbo.android.R;
import static me.vebbo.android.utils.Constant.CONTENT;
import static me.vebbo.android.utils.Constant.IMAGE;
import static me.vebbo.android.utils.Manage.setAvatar;

public class NoteDialog {

    private App app;
    private AppCompatActivity context;
    private Dialog dialog;
    private Window window;

    public NoteDialog(AppCompatActivity context) {
        this.context = context;
        app = (App) context.getApplication();
        dialog = new Dialog(this.context);
        window = dialog.getWindow();
    }

    public void showNote(JSONObject jsonObject) {
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(.7f);
        }
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_note);

        TextView content;
        ImageView userImage, closeBtn;

        content = dialog.findViewById(R.id.note_content);
        userImage = dialog.findViewById(R.id.noteImg);
        closeBtn = dialog.findViewById(R.id.closeNoteBtn);

        try {
            userImage.setImageDrawable(setAvatar(context, jsonObject.getInt(IMAGE)));
            content.setText(jsonObject.getString(CONTENT));

            userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ProfileDialog(context).showProfile(jsonObject);
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
