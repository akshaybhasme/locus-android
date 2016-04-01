package com.radiuslabs.locus;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

public class LauncherActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
