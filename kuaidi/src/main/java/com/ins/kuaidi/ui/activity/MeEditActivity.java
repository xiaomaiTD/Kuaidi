package com.ins.kuaidi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ins.kuaidi.R;
import com.ins.middle.ui.dialog.DialogLoading;
import com.ins.middle.ui.dialog.DialogPopupPhoto;
import com.ins.middle.utils.GlideUtil;
import com.sobey.common.helper.CropHelperSys;
import com.sobey.common.utils.PermissionsUtil;
import com.sobey.common.utils.StrUtils;
import com.ins.middle.ui.activity.BaseBackActivity;
public class MeEditActivity extends BaseBackActivity implements View.OnClickListener, CropHelperSys.CropInterface {

    private CropHelperSys cropHelper = new CropHelperSys(this);
    private DialogLoading loadingDialog;
    private DialogPopupPhoto popup;

    private ViewGroup showingroup;
    private View showin;

    private ImageView img_meedit_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meedit);
        setToolbar();

        initBase();
        initView();
        initData();
        initCtrl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popup != null) popup.dismiss();
        if (loadingDialog != null) loadingDialog.dismiss();
    }

    private void initBase() {
        loadingDialog = new DialogLoading(this, "正在上传");
        popup = new DialogPopupPhoto(this);
        popup.setOnCameraListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.hide();
                cropHelper.startCamera();
            }
        });
        popup.setOnPhotoListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.hide();
                cropHelper.startPhoto();
            }
        });
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        img_meedit_header = (ImageView) findViewById(R.id.img_meedit_header);
        findViewById(R.id.lay_meedit_header).setOnClickListener(this);
        findViewById(R.id.btn_right).setOnClickListener(this);
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
        GlideUtil.LoadCircleImgTest(this, img_meedit_header);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_meedit_header:
                if (PermissionsUtil.requsetPhoto(this, findViewById(R.id.showingroup))) {
                    popup.show();
                }
                break;
            case R.id.btn_right:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cropHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void cropResult(String path) {
        if (!StrUtils.isEmpty(path)) {
            Log.e("liao", path);
            GlideUtil.loadCircleImg(this, img_meedit_header, R.drawable.test, path);
        }
    }
}
