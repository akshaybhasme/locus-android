package com.radiuslabs.locus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.radiuslabs.locus.adapters.NavigationDrawerAdapter;
import com.radiuslabs.locus.adapters.NewsFeedAdapter;
import com.radiuslabs.locus.models.Story;
import com.radiuslabs.locus.persistence.AppPersistence;

import java.util.ArrayList;

public class NewsFeedActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new NewsFeedAdapter(new ArrayList<Story>());
        mRecyclerView.setAdapter(mAdapter);

        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(NewsFeedActivity.this);
        adapter.setItems(Util.getNavigationItems());
        ListView nav = (ListView) findViewById(R.id.left_drawer);
        nav.setAdapter(adapter);
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
            case R.id.action_profile:
                Intent intent = new Intent(NewsFeedActivity.this, UserProfileActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_search:
                Intent searchIntent = new Intent(NewsFeedActivity.this, SearchContentActivity.class);
                startActivity(searchIntent);
                return true;

            case R.id.action_post:
                Intent postIntent = new Intent(NewsFeedActivity.this, StoryCaptureActivity.class);
                startActivity(postIntent);
                return true;

            case R.id.action_logout:
                new AppPersistence(this).setAccessToken(null);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
