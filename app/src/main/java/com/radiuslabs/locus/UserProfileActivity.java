package com.radiuslabs.locus;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.radiuslabs.locus.adapters.NewsFeedAdapter;
import com.radiuslabs.locus.models.Story;
import com.radiuslabs.locus.models.User;
import com.radiuslabs.locus.restservices.RestClient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {

    public static final String TAG = "UserProfileActivity";

    private NewsFeedAdapter mAdapter;
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView ivProfilePic;
    private TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new NewsFeedAdapter(new ArrayList<Story>());
        mRecyclerView.setAdapter(mAdapter);

        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        tvUserName = (TextView) findViewById(R.id.tvUserName);

        RestClient.getInstance().getStoryService().getUserStories().enqueue(new Callback<List<Story>>() {
            @Override
            public void onResponse(Call<List<Story>> call, Response<List<Story>> response) {
                if (response.isSuccessful()) {
                    mAdapter.setStories(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Story>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        RestClient.getInstance().getUserService().getSelf().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    setUserProfile(response.body());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUserProfile(User user) {
        tvUserName.setText(
                user.getFirst_name()
                        + " "
                        + user.getLast_name()
        );
        Picasso.with(this).load(user.getProfile_pic()).into(ivProfilePic);
    }
}
