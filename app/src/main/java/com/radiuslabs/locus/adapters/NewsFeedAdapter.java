package com.radiuslabs.locus.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.radiuslabs.locus.CommentsDialog;
import com.radiuslabs.locus.R;
import com.radiuslabs.locus.Util;
import com.radiuslabs.locus.imagetransformations.CircleTransform;
import com.radiuslabs.locus.models.Like;
import com.radiuslabs.locus.models.Story;
import com.radiuslabs.locus.models.User;
import com.radiuslabs.locus.restservices.RestClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {

    public static final String TAG = "NewsFeedAdapter";

    private List<Story> stories;
    private HashMap<String, User> users;
    private Transformation profilePicTransformation;
    private FragmentManager fragmentManager;

    public NewsFeedAdapter(List<Story> stories, FragmentManager fragmentManager) {
        this.stories = stories;
        users = new HashMap<>();
        profilePicTransformation = new CircleTransform();
        this.fragmentManager = fragmentManager;
    }
    @Override
    public NewsFeedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_feed, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Story story = stories.get(position);
        User user = users.get(story.getUser_id());
        holder.tvUserName.setText(user.getFullName());
        holder.tvContentText.setText(stories.get(position).getContent_text());

        Picasso
                .with(holder.ivContent.getContext())
                .load(story.getContent_url())
                .into(holder.ivContent);

        Picasso
                .with(holder.ivProfilePic.getContext())
                .load(user.getProfile_pic())
                .transform(profilePicTransformation)
                .into(holder.ivProfilePic);

        holder.ibLike.setImageResource(R.drawable.ic_favorite_border_black_48dp);

        for (Like like : story.getLikes()) {
            if (like.getUser_id().equals(Util.user.get_id())) {
                story.setIsLiked(true);
                holder.ibLike.setImageResource(R.drawable.ic_favorite_black_48dp);
                break;
            }

        }

        holder.ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (story.isLiked()) {
                    RestClient.getInstance().getStoryService()
                            .unlikeStory(story.get_id())
                            .enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        Log.d(TAG, "Story liked!");
                                        holder.ibLike.setImageResource(R.drawable.ic_favorite_border_black_48dp);
                                        story.setIsLiked(false);
                                    } else
                                        Log.d(TAG, "Failed to like story");
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    t.printStackTrace();
                                }
                            });
                } else {
                    RestClient.getInstance().getStoryService()
                            .likeStory(story.get_id())
                            .enqueue(new retrofit2.Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        Log.d(TAG, "Story liked!");
                                        holder.ibLike.setImageResource(R.drawable.ic_favorite_black_48dp);
                                        story.setIsLiked(true);
                                    } else
                                        Log.d(TAG, "Failed to like story");
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    t.printStackTrace();
                                }
                            });
                }
            }
        });

        holder.ibComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentsDialog commentsDialog = new CommentsDialog();
                commentsDialog.show(fragmentManager, story.get_id());
                commentsDialog.setComments(story.getComments());
                commentsDialog.setStoryId(story.get_id());
            }
        });
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

    public void setUsers(List<User> users) {
        for (User user : users) {
            this.users.put(user.get_id(), user);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserName;
        TextView tvLocation;
        TextView tvContentText;
        ImageView ivContent;
        ImageView ivProfilePic;
        ImageButton ibLike;
        ImageButton ibComment;

        public ViewHolder(View v) {
            super(v);
            tvContentText = (TextView) v.findViewById(R.id.tvUserName);
            tvUserName = (TextView) v.findViewById(R.id.tvName);
            tvLocation = (TextView) v.findViewById(R.id.tvLocation);
            ivContent = (ImageView) v.findViewById(R.id.ivContent);
            ivProfilePic = (ImageView) v.findViewById(R.id.ivProfilePic);
            ibLike = (ImageButton) v.findViewById(R.id.ibLike);
            ibComment = (ImageButton) v.findViewById(R.id.ibComment);
        }

    }
}
