package com.ins.middle.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ins.middle.R;
import com.ins.middle.common.AppData;
import com.ins.middle.utils.PackageUtil;
import com.shelwee.update.UpdateHelper;
import com.shelwee.update.listener.OnUpdateListener;
import com.shelwee.update.pojo.UpdateInfo;
import com.shelwee.update.utils.VersionUtil;

public class VersionActivity extends BaseBackActivity {

    UpdateHelper updateHelper;

    private TextView vname;
    private TextView vcheck;
    private ImageView img_version_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initBase();
        initView();
        initData();
        initCtrl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateHelper!=null) updateHelper.onDestory();
    }

    private void initBase() {
        if (PackageUtil.isClient()){
            updateHelper = new UpdateHelper.Builder(this)
                    .checkUrl(AppData.Url.version_passenger)
                    .build();
        }else {
            updateHelper = new UpdateHelper.Builder(this)
                    .checkUrl(AppData.Url.version_driver)
                    .build();
        }
    }

    private void initView() {
        vname = (TextView) findViewById(R.id.version_vname);
        vcheck = (TextView) findViewById(R.id.version_check);
        img_version_logo = (ImageView) findViewById(R.id.img_version_logo);
    }

    private void initData() {
    }

    private void initCtrl() {
        String version = VersionUtil.getVersion(this);
        vname.setText(version);
        vcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateHelper.check(new OnUpdateListener() {
                    @Override
                    public void onStartCheck() {
                        Log.e("liao","onStartCheck");
                    }
                    @Override
                    public void onFinishCheck(UpdateInfo info) {
                        Log.e("liao","onFinishCheck");
                    }
                    @Override
                    public void onStartDownload() {
                        Log.e("liao","onStartDownload");
                    }
                    @Override
                    public void onInstallApk() {
                        Log.e("liao","onInstallApk");
                    }
                    @Override
                    public void onFinshDownload() {
                        Log.e("liao","onFinshDownload");
                    }
                    @Override
                    public void onDownloading(int progress) {
//                        Log.e("liao","onDownloading:"+progress);
                    }
                });
            }
        });
        if (PackageUtil.isClient()){
            img_version_logo.setImageResource(R.drawable.logo_kuaidi);
        }else {
            img_version_logo.setImageResource(R.drawable.logo_driver);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
