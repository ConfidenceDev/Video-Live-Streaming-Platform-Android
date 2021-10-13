package me.vebbo.android.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import me.vebbo.android.App;
import me.vebbo.android.R;
import me.vebbo.android.activities.SettingsActivity;
import me.vebbo.android.dialogs.AddCardDialog;
import me.vebbo.android.dialogs.ProfileDialog;

import static me.vebbo.android.utils.Constant.COMMENT_TEXT;
import static me.vebbo.android.utils.Constant.DATA;
import static me.vebbo.android.utils.Constant.IMAGE;
import static me.vebbo.android.utils.Constant.SOCKET_ID;
import static me.vebbo.android.utils.Constant.USERNAME;
import static me.vebbo.android.utils.Constant.UTC;
import static me.vebbo.android.utils.Manage.setAvatar;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder>  {

    private AppCompatActivity context;
    private List<JSONObject> commentModelList;

    public void setCommentModelList(AppCompatActivity context, List<JSONObject> commentModelList) {
        this.context = context;
        this.commentModelList = commentModelList;
    }

    public List<JSONObject> getCommentModelList() {
        return commentModelList;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        try {
            if (position == (getItemCount() - 1)) {
                holder.mDivider.setVisibility(View.GONE);
            }else{
                holder.mDivider.setVisibility(View.VISIBLE);
            }

            holder.mCommentImg.setImageDrawable(setAvatar(context, commentModelList.get(position).getInt(IMAGE)));
            holder.mName.setText(commentModelList.get(position).getString(USERNAME));
            holder.mDate.setText(commentModelList.get(position).getString(UTC));
            holder.mComment.setText(commentModelList.get(position).getString(COMMENT_TEXT));

            holder.mCommentImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        new ProfileDialog(context).showProfile(commentModelList.get(position).getJSONObject(DATA));
                    }catch (JSONException js){
                        js.printStackTrace();
                    }
                }
            });
        }catch (JSONException js){
            js.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (commentModelList == null) {
            return 0;
        } else {
            return commentModelList.size();
        }
    }

    public static class CommentHolder extends RecyclerView.ViewHolder {

        private ImageView mCommentImg;
        private TextView mComment, mName, mDate;
        private RelativeLayout mDivider;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            mCommentImg = itemView.findViewById(R.id.commentImg);
            mComment = itemView.findViewById(R.id.commentItemText);
            mDate = itemView.findViewById(R.id.commentItemDate);
            mName = itemView.findViewById(R.id.commentItemName);
            mDivider = itemView.findViewById(R.id.comment_divider);
        }
    }
}
