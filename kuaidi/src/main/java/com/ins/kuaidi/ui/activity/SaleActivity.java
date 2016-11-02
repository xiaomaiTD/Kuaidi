package com.ins.kuaidi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ins.kuaidi.R;
import com.ins.kuaidi.common.LoadingViewUtil;
import com.ins.kuaidi.entity.TestEntity;
import com.ins.kuaidi.ui.adpter.RecycleAdapterBankCard;
import com.ins.kuaidi.ui.fragment.BankCardOneFragment;
import com.ins.kuaidi.ui.fragment.BankCardTwoFragment;
import com.ins.kuaidi.ui.fragment.SaleLevelFragment;
import com.ins.kuaidi.ui.fragment.SalePeopleFragment;
import com.ins.kuaidi.utils.GlideUtil;
import com.liaoinstan.springview.container.AliFooter;
import com.liaoinstan.springview.container.AliHeader;
import com.liaoinstan.springview.widget.SpringView;
import com.sobey.common.interfaces.OnRecycleItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class SaleActivity extends BaseBackActivity {

    private ViewPager viewPager;
    private SaleActivity.MyPagerAdapter pagerAdapter;
    private String[] title = new String[]{"分销", "分销"};

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
        setContentView(R.layout.activity_sale);
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
        pagerAdapter = new SaleActivity.MyPagerAdapter(getSupportFragmentManager());
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
                return SaleLevelFragment.newInstance(position);
            } else if (position == 1) {
                return SalePeopleFragment.newInstance(position);
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
