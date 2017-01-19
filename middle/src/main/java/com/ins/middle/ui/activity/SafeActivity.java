package com.ins.middle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.ins.middle.R;
import com.ins.middle.ui.activity.BaseAppCompatActivity;
import com.ins.middle.ui.activity.BaseBackActivity;
import com.ins.middle.utils.PackageUtil;
import com.sobey.common.utils.ClickUtils;

public class SafeActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe);
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

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        findViewById(R.id.item_safe_pswuser).setOnClickListener(this);
        findViewById(R.id.item_safe_pswpay).setOnClickListener(this);
        findViewById(R.id.item_safe_identify).setOnClickListener(this);
    }

    private void initData() {
    }

    private void initCtrl() {
    }

    @Override
    public void onClick(View v) {
        //屏蔽快速双击事件
        if (ClickUtils.isFastDoubleClick()) return;

        int i = v.getId();
        if (i == R.id.item_safe_pswuser) {
            Intent intent = new Intent(this, ModifyPswUserActivity.class);
            startActivity(intent);

        } else if (i == R.id.item_safe_pswpay) {
            Intent intent = new Intent(this, ModifyPswPayActivity.class);
            startActivity(intent);

        } else if (i == R.id.item_safe_identify) {
            startActivity(PackageUtil.getSmIntent("IdentifyActivity"));
        }
    }
}
