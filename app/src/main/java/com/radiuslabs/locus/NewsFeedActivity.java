package com.radiuslabs.locus;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.radiuslabs.locus.adapters.NewsFeedAdapter;
import com.radiuslabs.locus.imagetransformations.CircleTransform;
import com.radiuslabs.locus.models.Story;
import com.radiuslabs.locus.persistence.AppPersistence;
import com.radiuslabs.locus.restservices.RestClient;
import com.radiuslabs.locus.restservices.responses.NewsFeedResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFeedActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private NewsFeedAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(android.support.design.R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new NewsFeedAdapter(new ArrayList<Story>(), getSupportFragmentManager());
        mRecyclerView.setAdapter(mAdapter);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        View header = getLayoutInflater().inflate(R.layout.drawer_header, null);
        TextView name = (TextView) header.findViewById(R.id.tvName);
        name.setText(Util.user.getFirst_name() + " " + Util.user.getLast_name());
        ImageView ivProfile = (ImageView) header.findViewById(R.id.ivProfilePic);
        Picasso.with(this).load(Util.user.getProfile_pic()).transform(new CircleTransform()).into(ivProfile);

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsFeedActivity.this, UserProfileActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawers();
            }
        });

        navigationView.addHeaderView(header);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.action_search:
                        Intent searchIntent = new Intent(NewsFeedActivity.this, SearchContentActivity.class);
                        startActivity(searchIntent);
                        return true;

                    case R.id.action_logout:
                        new AppPersistence(NewsFeedActivity.this).setAccessToken(null);
                        finish();
                        return true;
                }

                return false;
            }
        });

        // Get the location manager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);

        RestClient.getInstance().getStoryService().getNewsFeed(1, location.getLatitude(), location.getLongitude()).enqueue(new Callback<NewsFeedResponse>() {
            @Override
            public void onResponse(Call<NewsFeedResponse> call, Response<NewsFeedResponse> response) {
                if (response.isSuccessful()) {
                    mAdapter.setStories(response.body().getStories());
                    mAdapter.setUsers(response.body().getUsers());
                }
            }

            @Override
            public void onFailure(Call<NewsFeedResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_news_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_post:
                Intent postIntent = new Intent(NewsFeedActivity.this, StoryCaptureActivity.class);
                startActivity(postIntent);
                return true;

            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.LEFT);
        }
        return super.onOptionsItemSelected(item);
    }
}
