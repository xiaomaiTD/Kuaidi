package com.ins.kuaidi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.ins.kuaidi.R;
import com.ins.kuaidi.common.AppData;
import com.ins.kuaidi.entity.User;
import com.ins.kuaidi.ui.fragment.StartUpFragment;
import com.ins.kuaidi.utils.AppHelper;
import com.sobey.common.view.DotView;

public class StartUpActivity extends BaseAppCompatActivity {

    private ViewPager viewPager;
    private DotView dotView;

    private int[] srcs = new int[]{R.drawable.test, R.drawable.test, R.drawable.test, R.drawable.test, R.drawable.test};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        AppHelper.saveStartUp();

        initView();
        initData();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.startup_viewpager);
        dotView = (DotView) findViewById(R.id.startup_dot);
    }

    private void initData() {
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        dotView.setViewPager(viewPager);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "title";
        }

        @Override
        public int getCount() {
            return srcs.length;
        }

        @Override
        public Fragment getItem(int position) {
            return StartUpFragment.newInstance(srcs[position], position == srcs.length - 1);
        }
    }

    public void onGo(View v) {
        Intent intent = new Intent();
        User user = AppData.App.getUser();
        String token = AppData.App.getToken();
        if (user != null && token != null && token != "") {
            //去首页
            intent.setClass(this, HomeActivity.class);
        } else {
            //去登录页
            intent.setClass(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
