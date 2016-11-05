package com.ins.driver.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;

import com.ins.driver.R;
import com.ins.driver.ui.fragment.IdentifyOneFragment;
import com.ins.driver.ui.fragment.IdentifyThreeFragment;
import com.ins.driver.ui.fragment.IdentifyTwoFragment;
import com.ins.middle.ui.activity.BaseAppCompatActivity;


/**
 * type:0 急修项目 1:保养项目
 */
public class IdentifyActivity extends BaseAppCompatActivity {

    private ViewPager viewPager;
    private MyPagerAdapter pagerAdapter;
    private String[] title = new String[]{"实名认证", "实名认证", "实名认证"};

    private TextView text_toolbar_title;

    private int type;

    public int getType() {
        return type;
    }

    public void next() {
        int position = viewPager.getCurrentItem();
        int count = viewPager.getChildCount();
        if (position < count - 1) {
            setPage(position + 1);
        }
    }

    public void last() {
        int position = viewPager.getCurrentItem();
        if (position > 0) {
            setPage(position - 1);
        } else if (position == 0) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        setToolbar();

        initBase();
        initView();
        initData();
        initCtrl();
    }

    private void initBase() {
        if (getIntent().hasExtra("type")) {
            type = getIntent().getIntExtra("type", 0);
        }
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        text_toolbar_title = (TextView) findViewById(R.id.text_toolbar_title);
        text_toolbar_title.setText(title[0]);
    }

    private void initData() {
    }

    private void initCtrl() {
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
    }

    private void setPage(int pos) {
        viewPager.setCurrentItem(pos);
        text_toolbar_title.setText(title[pos]);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }

        @Override
        public int getCount() {
            return title.length;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return IdentifyOneFragment.newInstance(position);
            } else if (position == 1) {
                return IdentifyTwoFragment.newInstance(position);
            } else if (position == 2) {
                return IdentifyThreeFragment.newInstance(position);
            }
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                last();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            last();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
