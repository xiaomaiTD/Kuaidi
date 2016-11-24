package com.ins.middle.ui.activity;

import android.annotation.TargetApi;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ins.middle.R;
import com.ins.middle.utils.GlideUtil;

public class TestActivity extends BaseAppCompatActivity{

    private ViewGroup showingroup;
    private View showin;

    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setToolbar();

        initBase();
        initView();
        initData();
        initCtrl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initBase() {
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initView() {
        img = (ImageView) findViewById(R.id.img);
        GradientDrawable gradientDrawable = new GradientDrawable();
        int c1 = ContextCompat.getColor(this, R.color.bank_blue_start);
        int c2 = ContextCompat.getColor(this, R.color.bank_blue_end);
        gradientDrawable.setColors(new int[]{c1,c2});
    }

    private void initData() {
    }

    private void initCtrl() {
    }

}
