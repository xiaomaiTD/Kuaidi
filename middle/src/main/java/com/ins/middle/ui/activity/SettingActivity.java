package com.ins.middle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ins.middle.R;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.ui.dialog.DialogSure;
import com.shelwee.update.utils.VersionUtil;
import com.sobey.common.common.MyActivityCollector;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

public class SettingActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;
    private TextView text_setting_version;

    private View item_setting_logout;

    private DialogSure dialogSureLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setToolbar();
        EventBus.getDefault().register(this);

        initBase();
        initView();
        initData();
        initCtrl();
    }

    @Subscribe
    public void onEventMainThread(Integer flag) {
        if (flag == AppConstant.EVENT_UPDATE_LOGIN) {
            setUserData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (dialogSureLogout != null) dialogSureLogout.dismiss();
    }

    private void initBase() {
        handler = new Handler();
        dialogSureLogout = new DialogSure(this, "确认退出当前账号？");
        dialogSureLogout.setOnOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netLogout();
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
        item_setting_logout = findViewById(R.id.item_setting_logout);
        item_setting_logout.setOnClickListener(this);

    }

    private void initData() {
    }

    private void initCtrl() {
        setUserData();
        ((TextView) findViewById(R.id.text_setting_version)).setText(VersionUtil.getVersion(this));
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        int i = v.getId();
        if (i == R.id.item_setting_suggest) {
            intent.setClass(this, FeedbackActivity.class);
            startActivity(intent);
        } else if (i == R.id.item_setting_caulse) {
            intent.setClass(this, WebActivity.class);
            intent.putExtra("url", "http://cn.bing.com");
            intent.putExtra("title", "使用协议与隐私政策");
            startActivity(intent);

        } else if (i == R.id.item_setting_about) {
            intent.setClass(this, WebActivity.class);
            intent.putExtra("url", "http://cn.bing.com");
            intent.putExtra("title", "关于我们");
            startActivity(intent);

        } else if (i == R.id.item_setting_safe) {
            if (AppData.App.getUser() != null) {
                intent.setClass(this, SafeActivity.class);
                startActivity(intent);
            } else {
                intent.setClass(this, LoginActivity.class);
                startActivity(intent);
            }
        } else if (i == R.id.item_setting_version) {
            intent.setClass(this, VersionActivity.class);
            startActivity(intent);

        } else if (i == R.id.item_setting_logout) {
            dialogSureLogout.show();
        }
    }

    //////////////////////////////////////
    //////////登出接口
    /////////////////////////////////////
    private boolean islogouting = false;
    private boolean isruning = false;
    private Callback.Cancelable cancelable;
    private Handler handler;
    private Runnable runnable;

    private void netLogout() {
        if (islogouting) return;

        RequestParams params = new RequestParams(AppData.Url.logout);
        params.addHeader("token", AppData.App.getToken());
        cancelable = CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                handler.removeCallbacks(runnable);
                dialogSureLogout.hide();
                setlogout();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(SettingActivity.this, text, Toast.LENGTH_SHORT).show();
                dialogSureLogout.hide();
            }

            @Override
            public void netEnd(int code) {
                islogouting = false;
            }

            @Override
            public void netStart(int code) {
                islogouting = true;
            }
        });

        if (isruning) return;

        isruning = true;
        runnable = new Runnable() {
            @Override
            public void run() {
                cancelable.cancel();
                dialogSureLogout.hide();
                setlogout();
            }
        };
        handler.postDelayed(runnable, 2000);
    }

    private void setlogout() {
        AppData.App.removeUser();
        AppData.App.removeToken();
//        setUserData();
        EventBus.getDefault().post(AppConstant.EVENT_UPDATE_LOGIN);
    }

    private void setUserData() {
        if (AppData.App.getUser() != null) {
            item_setting_logout.setVisibility(View.VISIBLE);
        } else {
            item_setting_logout.setVisibility(View.GONE);
        }
    }
}
