package com.radiuslabs.locus.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.radiuslabs.locus.R;
import com.radiuslabs.locus.Util;
import com.radiuslabs.locus.imagetransformations.CircleTransform;
import com.radiuslabs.locus.models.Story;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {

    private List<Story> stories;
    private Transformation profilePicTransformation;

    public NewsFeedAdapter(List<Story> stories) {
        this.stories = stories;
        profilePicTransformation = new CircleTransform();
    }
    @Override
    public NewsFeedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_feed, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Story story = stories.get(position);

        holder.tvUserName.setText(Util.user.getFirst_name() + " " + Util.user.getLast_name());
        holder.tvContentText.setText(stories.get(position).getContent_text());

        Picasso
                .with(holder.ivContent.getContext())
                .load(story.getContent_url())
                .into(holder.ivContent);

        Picasso
                .with(holder.ivProfilePic.getContext())
                .load(Util.user.getProfile_pic())
                .transform(profilePicTransformation)
                .into(holder.ivProfilePic);
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }


    public List<Story> getStories() {
        return stories;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserName;
        TextView tvLocation;
        TextView tvContentText;
        ImageView ivContent;
        ImageView ivProfilePic;

        public ViewHolder(View v) {
            super(v);
            tvContentText = (TextView) v.findViewById(R.id.tvUserName);
            tvUserName = (TextView) v.findViewById(R.id.tvName);
            tvLocation = (TextView) v.findViewById(R.id.tvLocation);
            ivContent = (ImageView) v.findViewById(R.id.ivContent);
            ivProfilePic = (ImageView) v.findViewById(R.id.ivProfilePic);
        }

    }
}
