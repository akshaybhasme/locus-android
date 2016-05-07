package com.radiuslabs.locus;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFeedActivity extends AppCompatActivity {

    public static final String TAG = "NewsFeedActivity";

    public static final int REQUEST_POST_STORY = 111;

    private RecyclerView mRecyclerView;
    private NewsFeedAdapter mAdapter;
    private DrawerLayout drawerLayout;

    private boolean loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
//            actionBar.setHomeAsUpIndicator(android.support.design.R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new NewsFeedAdapter(new ArrayList<Story>(), getSupportFragmentManager());
        mRecyclerView.setAdapter(mAdapter);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        View header = getLayoutInflater().inflate(R.layout.drawer_header, null);
        TextView name = (TextView) header.findViewById(R.id.tvName);
        if (Util.user != null)
            name.setText(Util.user.getFullName());
        ImageView ivProfile = (ImageView) header.findViewById(R.id.ivProfilePic);
        Picasso.with(this).load(Util.user.getProfile_pic()).transform(new CircleTransform()).into(ivProfile);

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsFeedActivity.this, UserProfileActivity.class);
                intent.putExtra(UserProfileActivity.EXTRA_USER_ID, Util.user);
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
                        Intent intent = new Intent(NewsFeedActivity.this, LauncherActivity.class);
                        startActivity(intent);
                        finish();
                        return true;
                }

                return false;
            }
        });

        getStories(1);

        EndlessRecyclerOnScrollListener listener = new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page) {
                Log.d(TAG, "Current Page " + page);
                if (!loading)
                    getStories(page);
            }
        };

        mRecyclerView.addOnScrollListener(listener);

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
                startActivityForResult(postIntent, REQUEST_POST_STORY);
                return true;

            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.LEFT);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_POST_STORY:
                    mAdapter.setStories(null);
                    getStories(1);
                    return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getStories(int page) {
        loading = true;
        Location location = getLastKnownLocation();
        if (location != null) {
            RestClient.getInstance().getStoryService().getNewsFeed(page, location.getLatitude(), location.getLongitude()).enqueue(new Callback<NewsFeedResponse>() {
                @Override
                public void onResponse(Call<NewsFeedResponse> call, Response<NewsFeedResponse> response) {
                    if (response.isSuccessful()) {
                        mAdapter.addStories(response.body().getStories());
                        mAdapter.setUsers(response.body().getUsers());
                    }
                    loading = false;
                }

                @Override
                public void onFailure(Call<NewsFeedResponse> call, Throwable t) {
                    t.printStackTrace();
                    loading = false;
                }
            });
        } else {
            Snackbar.make(drawerLayout,
                    "We are not able to get your location. Please check your settings",
                    Snackbar.LENGTH_LONG).show();
        }
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(false);
        Location bestLocation = null;
        Log.d(TAG, "Providers found: " + providers.size());
        for (String provider : providers) {
            Log.d(TAG, "Provider: " + provider);
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

}
