package com.ins.kuaidi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ins.kuaidi.R;
import com.ins.kuaidi.common.LoadingViewUtil;
import com.ins.kuaidi.ui.dialog.DialogSure;
import com.ins.kuaidi.ui.fragment.IdentifyOneFragment;
import com.shelwee.update.utils.VersionUtil;

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
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.item_safe_pswuser:
                intent.setClass(this, ModifyPswUserActivity.class);
                startActivity(intent);
                break;
            case R.id.item_safe_pswpay:
                intent.setClass(this, ModifyPswPayActivity.class);
                startActivity(intent);
                break;
            case R.id.item_safe_identify:
                intent.setClass(this, IdentifyActivity.class);
                startActivity(intent);
                break;
        }
    }
}
