package com.ins.middle.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.ins.middle.R;
import com.ins.middle.ui.fragment.CityFragment;
import com.sobey.common.utils.KeyBoardUtil;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭的时候收起键盘，魅族手机有一个小的体验性问题（妈的垃圾厂商）
        KeyBoardUtil.hideKeybord(this);
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
