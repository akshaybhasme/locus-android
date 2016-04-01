package com.radiuslabs.locus;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ListView;

import com.radiuslabs.locus.adapters.InterestAdapter;
import com.radiuslabs.locus.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserInterestsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interests);

        ListView listView = (ListView) findViewById(R.id.lvInterests);

        InterestAdapter adapter = new InterestAdapter(UserInterestsActivity.this);

        List<User.Interest> interests = new ArrayList<>();

        for(int i = 0 ; i < 20 ; i++){
            User.Interest interest = new User.Interest();
            interest.setText("Interest "+(i+1));
            interests.add(interest);
        }

        listView.setAdapter(adapter);

        adapter.setInterests(interests);

    }

}
