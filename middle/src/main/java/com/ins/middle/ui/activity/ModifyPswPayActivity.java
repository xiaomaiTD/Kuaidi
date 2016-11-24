package com.ins.middle.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;

import com.ins.middle.R;
import com.ins.middle.common.AppData;
import com.ins.middle.entity.User;
import com.ins.middle.ui.fragment.ModifyPswPayOneFragment;
import com.ins.middle.ui.fragment.ModifyPswPayThreeFragment;
import com.ins.middle.ui.fragment.ModifyPswPayTwoFragment;
import com.ins.middle.ui.activity.BaseAppCompatActivity;
import com.ins.middle.ui.activity.BaseBackActivity;


/**
 * type:0 设置提现密码 1:修改提现密码
 */
public class ModifyPswPayActivity extends BaseAppCompatActivity {

    private ViewPager viewPager;
    private MyPagerAdapter pagerAdapter;
    private String[] title = new String[]{"修改提现密码", "修改提现密码", "修改提现密码"};

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
        setContentView(R.layout.activity_modifypswpay);
        setToolbar();

        initBase();
        initView();
        initData();
        initCtrl();
    }

    private void initBase() {
        type = AppData.App.getUser().getHasPayPassword();
        //type:0 设置提现密码 1:修改提现密码
        if (type == 0) {
            title = new String[]{"设置提现密码", "设置提现密码"};
        } else {
            title = new String[]{"修改提现密码", "修改提现密码", "修改提现密码"};
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
            //type:0 设置提现密码 1:修改提现密码
            if (type == 0) {
                if (position == 0) {
                    return ModifyPswPayTwoFragment.newInstance(position);
                } else if (position == 1) {
                    return ModifyPswPayThreeFragment.newInstance(position);
                }
            } else {
                if (position == 0) {
                    return ModifyPswPayOneFragment.newInstance(position);
                } else if (position == 1) {
                    return ModifyPswPayTwoFragment.newInstance(position);
                } else if (position == 2) {
                    return ModifyPswPayThreeFragment.newInstance(position);
                }
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
