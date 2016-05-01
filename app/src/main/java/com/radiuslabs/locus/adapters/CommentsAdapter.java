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
import com.radiuslabs.locus.models.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private List<Comment> comments;

    private Context context;
    private LayoutInflater inflater;

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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.row_comment, new LinearLayout(context));

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvText.setText(comments.get(position).getText());
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
