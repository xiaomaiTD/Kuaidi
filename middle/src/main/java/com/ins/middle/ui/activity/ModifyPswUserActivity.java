package com.ins.middle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ins.middle.R;
import com.ins.middle.ui.dialog.DialogMsg;

public class ModifyPswUserActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;

    private DialogMsg dialogMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifypswuser);
        setToolbar();

        initBase();
        initView();
        initData();
        initCtrl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialogMsg != null) dialogMsg.dismiss();
    }

    private void initBase() {
        dialogMsg = new DialogMsg(this, "密码修改成功，请重新登录");
        dialogMsg.setOnOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMsg.dismiss();
                Toast.makeText(ModifyPswUserActivity.this, "假装你已经重新登录了", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        findViewById(R.id.btn_go).setOnClickListener(this);
    }

    private void initData() {
    }

    private void initCtrl() {
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        int i = v.getId();
        if (i == R.id.btn_go) {
            dialogMsg.show();

        }
    }
}
