package com.shridhar.googleplaces;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shridhar.googleplaces.AppConstants.Constants.INDEX;
import static com.shridhar.googleplaces.AppConstants.Constants.KEYS;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.explore)
    LinearLayout explore;

    @BindView(R.id.search)
    LinearLayout search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        explore.setOnClickListener(this);
        search.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);

        }

        KEYS = new ArrayList<>();
        KEYS.add("AIzaSyBhA5Zpk8a9j0obu5LgS4gWZvYHGpYpN0g");
        KEYS.add("AIzaSyB11I-DsuY9cCx_4Qt0v52J3ETag3DRmGs");
        KEYS.add("AIzaSyAe9zhvFsXQeMSjWIrl3GlyP0_UHns-TCE");
        KEYS.add("AIzaSyDtyn235BxyTkkAT4mQgnosije8q1WD1uY");
        KEYS.add("AIzaSyA1j3SfWFEoZfyYrDU7GLPLk7Fc5R-mCAI");
        KEYS.add("AIzaSyAw2E173FgB4MD7InBn5nWfknkx8RKjdN8");
        KEYS.add("AIzaSyBYHwZWBmzq25AiMPNkL_fREN0dELVlNnI");
        KEYS.add("AIzaSyAaw2gVm8v-GA5PkHaiDKgnQfxhCYgC2n4");
        KEYS.add("AIzaSyCELk_cLDfY_hr51zK658U0s8bXIhaZ-gQ");
        KEYS.add("AIzaSyALMiXtWiYrIc7XPAw_3f_IQGZABif2MU0");
        KEYS.add("AIzaSyCrZ8CltZ4p0ZBYJONgRIVrk8KI8gMju6o");
        INDEX = 0;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.explore:
                openExploreTab();
                break;

            case R.id.search:
                openSearchTab();
                break;
        }

    }

    private void openSearchTab() {

        Intent intent = new Intent(MainActivity.this,SearchByText.class);
        startActivity(intent);

    }

    private void openExploreTab() {

        Intent intent = new Intent(MainActivity.this,Explore.class);
        startActivity(intent);

    }
}
