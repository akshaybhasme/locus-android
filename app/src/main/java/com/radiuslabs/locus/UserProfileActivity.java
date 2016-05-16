package com.radiuslabs.locus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.radiuslabs.locus.adapters.NewsFeedAdapter;
import com.radiuslabs.locus.imagetransformations.CircleTransform;
import com.radiuslabs.locus.models.Story;
import com.radiuslabs.locus.models.User;
import com.radiuslabs.locus.restservices.RestClient;
import com.radiuslabs.locus.restservices.responses.NewsFeedResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {

    public static final String TAG = "UserProfileActivity";

    public static final String EXTRA_USER_ID = "UserID";

    private NewsFeedAdapter mAdapter;
    private ImageView ivProfilePic;
    private List<User> users;

    private Callback<NewsFeedResponse> listStoryCallback = new Callback<NewsFeedResponse>() {
        @Override
        public void onResponse(Call<NewsFeedResponse> call, Response<NewsFeedResponse> response) {
            if (response.isSuccessful()) {
                mAdapter.setStories(response.body().getStories());
                users.addAll(response.body().getUsers());
                mAdapter.setUsers(users);
            }
        }

        @Override
        public void onFailure(Call<NewsFeedResponse> call, Throwable t) {
            t.printStackTrace();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new NewsFeedAdapter(new ArrayList<Story>(), getSupportFragmentManager());
        mRecyclerView.setAdapter(mAdapter);

        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);

        users = new ArrayList<>();

//        RestClient.getInstance().getUserService().getSelf().enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                if (response.isSuccessful()) {
//                    User u = response.body();
//                    setUserProfile(u);
//                    users.add(u);
//                    RestClient.getInstance().getStoryService().getUserStories().enqueue(listStoryCallback);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });

        User u = (User) getIntent().getSerializableExtra(EXTRA_USER_ID);
        setUserProfile(u);
        users.add(u);
        RestClient.getInstance().getStoryService().getUserStories(u.get_id()).enqueue(listStoryCallback);
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
        String fullName = user.getFirst_name() + " " + user.getLast_name();

        if (!Util.isStringEmpty(user.getProfile_pic()))
            Picasso
                    .with(this)
                    .load(user.getProfile_pic())
                    .transform(new CircleTransform())
                    .into(ivProfilePic);

        ((TextView) findViewById(R.id.tvUserName)).setText(fullName);
    }
}
