package com.ins.kuaidi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ins.kuaidi.R;
import com.ins.kuaidi.ui.fragment.SaleLevelFragment;
import com.ins.kuaidi.ui.fragment.SalePeopleFragment;
import com.ins.middle.common.AppData;
import com.ins.middle.entity.User;
import com.ins.middle.ui.activity.BaseAppCompatActivity;
import com.ins.middle.ui.activity.BaseBackActivity;
import com.ins.middle.ui.activity.LoginActivity;
import com.ins.sharesdk.dialog.ShareDialog;
//import com.sobey.share.sharesdk.dialog.ShareDialog;

public class SaleActivity extends BaseBackActivity implements View.OnClickListener {

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
        findViewById(R.id.btn_right).setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_right:
                User user = AppData.App.getUser();
                if (user != null) {
                    String url = AppData.Url.shareUrl + "?distributionCode=" + AppData.App.getUser().getDistributionCode();
                    ShareDialog shareDialog = new ShareDialog(this);
                    shareDialog.setShareData("陛下", "您有一封新的奏折~", url, "http://v1.qzone.cc/avatar/201408/04/16/16/53df416d26b6c338.jpg%21200x200.jpg");
                    shareDialog.show();
                } else {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
        }
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
