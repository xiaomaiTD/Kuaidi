package com.ins.middle.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ins.middle.R;
import com.ins.middle.common.AppData;
import com.ins.middle.utils.GlideUtil;
import com.ins.middle.ui.activity.BaseAppCompatActivity;
import com.ins.middle.ui.activity.BaseBackActivity;
import com.sobey.common.utils.PhoneUtils;
import com.sobey.common.utils.StrUtils;

public class ServerActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;

    private ImageView img_server_header;
    private TextView text_server_phone;
    private View lay_server_phone;

    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
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
        phone = AppData.App.getPhone();
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        img_server_header = (ImageView) findViewById(R.id.img_server_header);
        text_server_phone = (TextView) findViewById(R.id.text_server_phone);
        lay_server_phone = findViewById(R.id.lay_server_phone);
    }

    private void initData() {
    }

    private void initCtrl() {
        text_server_phone.setText(!StrUtils.isEmpty(phone) ? phone : "");
        lay_server_phone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.lay_server_phone) {
            if (!StrUtils.isEmpty(phone)) {
                PhoneUtils.call(this, phone);
            } else {
                Toast.makeText(this, "获取号码失败，请重启应用后尝试", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
