package com.radiuslabs.locus.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.radiuslabs.locus.R;
import com.radiuslabs.locus.imagetransformations.CircleTransform;
import com.radiuslabs.locus.models.Comment;
import com.radiuslabs.locus.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private List<Comment> comments;
    private HashMap<String, User> users;

    private Context context;
    private LayoutInflater inflater;

    private CircleTransform transform = new CircleTransform();

    public CommentsAdapter(List<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public void addComment(Comment comment) {
        if (this.comments == null)
            this.comments = new ArrayList<>();
        comments.add(comment);
        notifyDataSetChanged();
    }

    public void deleteComment(Comment comment) {
        this.comments.remove(comment);
        notifyDataSetChanged();
    }

    public void setUsers(HashMap<String, User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.row_comment, new LinearLayout(context));

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        User user = users.get(comment.getUserId());

        holder.tvText.setText(comment.getText());

        if (user != null) {
            holder.tvName.setText(user.getFullName());
            Picasso
                    .with(context)
                    .load(user.getProfile_pic())
                    .transform(transform)
                    .placeholder(R.drawable.placeholder_user)
                    .into(holder.ivProfilePic);
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfilePic;
        TextView tvName;
        TextView tvText;

        public ViewHolder(View v) {
            super(v);
            ivProfilePic = (ImageView) v.findViewById(R.id.ivProfilePic);
            tvName = (TextView) v.findViewById(R.id.tvName);
            tvText = (TextView) v.findViewById(R.id.tvText);
        }
    }
}
