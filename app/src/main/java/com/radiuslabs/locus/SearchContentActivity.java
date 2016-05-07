package com.radiuslabs.locus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;

import com.radiuslabs.locus.adapters.NewsFeedAdapter;
import com.radiuslabs.locus.models.Story;
import com.radiuslabs.locus.restservices.RestClient;
import com.radiuslabs.locus.restservices.responses.NewsFeedResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchContentActivity extends AppCompatActivity {

    public static final String EXTRA_TAG = "ExtraTag";

    private NewsFeedAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_content);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new NewsFeedAdapter(new ArrayList<Story>(), getSupportFragmentManager());
        mRecyclerView.setAdapter(mAdapter);

        String tag = getIntent().getStringExtra(EXTRA_TAG);

        final SearchView searchView = new SearchView(this);
        searchView.setIconified(false);

        if (tag != null) {
            searchView.setQuery(tag, false);
            getStoriesWithTag(tag);
        }

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setCustomView(searchView);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getStoriesWithTag(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void getStoriesWithTag(String tag) {
        RestClient.getInstance().getStoryService().getStoriesWithHashtag(tag)
                .enqueue(new Callback<NewsFeedResponse>() {
                    @Override
                    public void onResponse(Call<NewsFeedResponse> call, Response<NewsFeedResponse> response) {
                        if (response.isSuccessful()) {
                            mAdapter.setUsers(response.body().getUsers());
                            mAdapter.setStories(response.body().getStories());
                        }
                    }

                    @Override
                    public void onFailure(Call<NewsFeedResponse> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

}
