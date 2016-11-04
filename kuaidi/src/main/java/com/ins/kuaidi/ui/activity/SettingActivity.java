package com.ins.kuaidi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ins.kuaidi.R;
import com.ins.middle.ui.dialog.DialogSure;
import com.shelwee.update.utils.VersionUtil;
import com.ins.middle.ui.activity.BaseAppCompatActivity;
import com.ins.middle.ui.activity.BaseBackActivity;
public class SettingActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;
    private TextView text_setting_version;

    private DialogSure dialogSureLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setToolbar();

        initBase();
        initView();
        initData();
        initCtrl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialogSureLogout != null) dialogSureLogout.dismiss();
    }

    private void initBase() {
        dialogSureLogout = new DialogSure(this, "确认退出当前账号？");
        dialogSureLogout.setOnOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSureLogout.dismiss();
                Toast.makeText(SettingActivity.this, "假装你已经退出了", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        findViewById(R.id.item_setting_suggest).setOnClickListener(this);
        findViewById(R.id.item_setting_caulse).setOnClickListener(this);
        findViewById(R.id.item_setting_about).setOnClickListener(this);
        findViewById(R.id.item_setting_safe).setOnClickListener(this);
        findViewById(R.id.item_setting_version).setOnClickListener(this);
        findViewById(R.id.item_setting_logout).setOnClickListener(this);
    }

    private void initData() {
    }

    private void initCtrl() {
        ((TextView) findViewById(R.id.text_setting_version)).setText(VersionUtil.getVersion(this));
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.item_setting_suggest:
                intent.setClass(this, FeedbackActivity.class);
                startActivity(intent);
                break;
            case R.id.item_setting_caulse:
                intent.setClass(this, WebActivity.class);
                intent.putExtra("url", "http://cn.bing.com");
                intent.putExtra("title", "使用协议与隐私政策");
                startActivity(intent);
                break;
            case R.id.item_setting_about:
                intent.setClass(this, WebActivity.class);
                intent.putExtra("url", "http://cn.bing.com");
                intent.putExtra("title", "关于我们");
                startActivity(intent);
                break;
            case R.id.item_setting_safe:
                intent.setClass(this, SafeActivity.class);
                startActivity(intent);
                break;
            case R.id.item_setting_version:
                intent.setClass(this, VersionActivity.class);
                startActivity(intent);
                break;
            case R.id.item_setting_logout:
                dialogSureLogout.show();
                break;
        }
    }
}
