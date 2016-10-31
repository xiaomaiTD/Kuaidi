package com.ins.kuaidi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ins.kuaidi.R;
import com.ins.kuaidi.ui.dialog.DialogLoading;
import com.ins.kuaidi.ui.dialog.DialogPopupPhoto;
import com.ins.kuaidi.utils.GlideUtil;
import com.sobey.common.helper.CropHelperSys;
import com.sobey.common.utils.PermissionsUtil;
import com.sobey.common.utils.StrUtils;

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
//        showin = LoadingViewUtil.showin(showingroup, R.layout.layout_loading, showin);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //加载成功
//                initCtrl();
//                LoadingViewUtil.showout(showingroup, showin);
//
//                //加载失败
////                LoadingViewUtil.showin(showingroup,R.layout.layout_lack,showin,new View.OnClickListener(){
////                    @Override
////                    public void onClick(View v) {
////                        initData();
////                    }
////                });
//            }
//        }, 500);
    }

    private void initCtrl() {
        GlideUtil.LoadCircleImgTest(this, img_server_header);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_right:
                break;
        }
    }
}
