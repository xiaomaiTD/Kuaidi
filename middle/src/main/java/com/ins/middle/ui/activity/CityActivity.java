package com.ins.middle.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.ins.middle.R;
import com.ins.middle.ui.fragment.CityFragment;

public class CityActivity extends BaseAppCompatActivity {

    private Fragment contactFragment;

    private String city;
    private String latlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        setToolbar();

        initBase();

        contactFragment = CityFragment.newInstance(0, city, latlng);

        // 添加显示第一个fragment
        FragmentTransaction ftx = getSupportFragmentManager().beginTransaction();
        if (!contactFragment.isAdded()) {
            ftx.add(R.id.fragment_container, contactFragment, 0 + "");
        } else {
            ftx.show(contactFragment);
        }
        ftx.commit();
    }

    private void initBase() {
        if (getIntent().hasExtra("city")) {
            city = getIntent().getStringExtra("city");
        }
        if (getIntent().hasExtra("latlng")) {
            latlng = getIntent().getStringExtra("latlng");
        }
    }
}
