package com.ins.middle.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ins.middle.R;
import com.ins.middle.utils.GlideUtil;
import com.ins.middle.ui.activity.BaseAppCompatActivity;
import com.ins.middle.ui.activity.BaseBackActivity;
public class ServerActivity extends BaseBackActivity implements View.OnClickListener{

    private ViewGroup showingroup;
    private View showin;

    private ImageView img_server_header;

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
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        img_server_header = (ImageView) findViewById(R.id.img_server_header);
    }

    private void initData() {
    }

    private void initCtrl() {
//        GlideUtil.LoadCircleImgTest(this, img_server_header);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_right) {
        }
    }
}
