package com.ins.middle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.ins.middle.R;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.common.AppVali;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.entity.User;
import com.ins.middle.ui.activity.BaseBackActivity;
import com.ins.middle.ui.dialog.DialogLoading;
import com.sobey.common.utils.StrUtils;

import org.greenrobot.eventbus.EventBus;
import org.xutils.http.RequestParams;

import java.util.Arrays;
import java.util.List;

public class FeedbackActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;

    private DialogLoading loadingDialog;

    private EditText edit_feedback_describe;
    private View btn_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setToolbar();

        initBase();
        initView();
        initData();
        initCtrl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null) loadingDialog.dismiss();
    }

    private void initBase() {
        loadingDialog = new DialogLoading(this, "正在处理");
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        btn_right = findViewById(R.id.btn_right);
        btn_right.setOnClickListener(this);
        edit_feedback_describe = (EditText) findViewById(R.id.edit_feedback_describe);
    }

    private void initData() {
    }

    private void initCtrl() {
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        int i = v.getId();
        if (i == R.id.btn_right) {
            btn_right.setEnabled(false);

            String describe = edit_feedback_describe.getText().toString();

            String msg = AppVali.feedback(describe);
            if (!StrUtils.isEmpty(msg)) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                btn_right.setEnabled(true);
            } else {
                netCommit(describe);
            }
        }
    }

    private void netCommit(String describe) {
        RequestParams params = new RequestParams(AppData.Url.feedback);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("content", describe);
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                Toast.makeText(FeedbackActivity.this, "感谢您的建议", Toast.LENGTH_SHORT).show();
                loadingDialog.hide();
                btn_right.setEnabled(true);
                finish();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(FeedbackActivity.this, text, Toast.LENGTH_SHORT).show();
                loadingDialog.hide();
                btn_right.setEnabled(true);
            }

            @Override
            public void netStart(int code) {
                loadingDialog.show();
            }
        });
    }
}
