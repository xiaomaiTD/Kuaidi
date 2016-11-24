package com.ins.middle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ins.middle.R;
import com.ins.middle.common.AppData;
import com.ins.middle.common.AppVali;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.entity.User;
import com.ins.middle.ui.dialog.DialogMsg;
import com.ins.middle.utils.PackageUtil;
import com.sobey.common.helper.ValiHelper;
import com.sobey.common.utils.KeyBoardUtil;
import com.sobey.common.utils.MD5Util;
import com.sobey.common.utils.StrUtils;

import org.xutils.http.RequestParams;

public class ModifyPswUserActivity extends BaseBackActivity implements View.OnClickListener {

    private ValiHelper valiHelper;

    private ViewGroup showingroup;
    private View showin;
    private View btn_go;
    private TextView btn_go_vali;
    private EditText edit_modifypswuser_vali;
    private EditText edit_modifypswuser_psw;
    private EditText edit_modifypswuser_pswrepet;

    private DialogMsg dialogMsg;

    private String phone;

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
        if (getIntent().hasExtra("phone")) {
            phone = getIntent().getStringExtra("phone");
        }
        dialogMsg = new DialogMsg(this, "密码修改成功，请重新登录");
        dialogMsg.setOnOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMsg.dismiss();
                Intent intent = new Intent(ModifyPswUserActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        btn_go = findViewById(R.id.btn_go);
        btn_go_vali = (TextView) findViewById(R.id.btn_go_vali);
        edit_modifypswuser_vali = (EditText) findViewById(R.id.edit_modifypswuser_vali);
        edit_modifypswuser_psw = (EditText) findViewById(R.id.edit_modifypswuser_psw);
        edit_modifypswuser_pswrepet = (EditText) findViewById(R.id.edit_modifypswuser_pswrepet);

        btn_go.setOnClickListener(this);
        btn_go_vali.setOnClickListener(this);

        valiHelper = new ValiHelper(btn_go_vali);
    }

    private void initData() {
    }

    private void initCtrl() {
        //如果没有phone参数，则代表是修改密码
        if (StrUtils.isEmpty(phone)) {
            setToolbar("修改密码");
        } else {
            //如果有phone参数，则代表是忘记（找回）密码
            setToolbar("忘记密码");
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_go) {
            String psw = edit_modifypswuser_psw.getText().toString();
            String pswrepet = edit_modifypswuser_pswrepet.getText().toString();
            String vali = edit_modifypswuser_vali.getText().toString();
            String msg = AppVali.modifypswuser(vali, valiHelper.valicode, psw, pswrepet);
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            } else {
                //如果没有phone参数，则代表是修改密码
                if (StrUtils.isEmpty(phone)) {
                    netModifypsw(psw);
                } else {
                    //如果有phone参数，则代表是忘记（找回）密码
                    netFindpsw(phone, psw);
                }
            }
        } else if (i == R.id.btn_go_vali) {
            netGetValicode(phone);
        }
    }

    private void netGetValicode(String phone) {
        RequestParams params = new RequestParams(AppData.Url.sendCode);
        params.addHeader("token", AppData.App.getToken());
        if (!StrUtils.isEmpty(phone)) {
            params.addBodyParameter("mobile", phone);
        }
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, final Object pojo, String text, Object obj) {
                if (pojo == null) netSetError(code, "接口异常");
                else {
                    Toast.makeText(ModifyPswUserActivity.this, text, Toast.LENGTH_SHORT).show();

                    //保持验证码和手机号
                    CommonEntity com = (CommonEntity) pojo;
                    valiHelper.valicode = com.getValicode();

                    //开始计时
                    valiHelper.start();
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(ModifyPswUserActivity.this, text, Toast.LENGTH_SHORT).show();
                btn_go_vali.setEnabled(true);
            }

            @Override
            public void netStart(int status) {
                btn_go_vali.setEnabled(false);
            }
        });
    }

    private void netFindpsw(String phone, String psw) {
        RequestParams params = new RequestParams(AppData.Url.resetPassword);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("flag", PackageUtil.isClient() ? "0" : "1");
        params.addBodyParameter("mobile", phone);
        params.addBodyParameter("password", MD5Util.md5(psw));
        CommonNet.samplepost(params, User.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, final Object pojo, String text, Object obj) {
                Toast.makeText(ModifyPswUserActivity.this, text, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(ModifyPswUserActivity.this, text, Toast.LENGTH_SHORT).show();
                btn_go.setEnabled(true);
            }

            @Override
            public void netStart(int status) {
                btn_go.setEnabled(false);
            }
        });
    }

    private void netModifypsw(String psw) {
        RequestParams params = new RequestParams(AppData.Url.updateUser);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("password", MD5Util.md5(psw));
        CommonNet.samplepost(params, User.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, final Object pojo, String text, Object obj) {
                Toast.makeText(ModifyPswUserActivity.this, text, Toast.LENGTH_SHORT).show();
                netLogout();
                AppData.App.removeToken();
                AppData.App.removeUser();
                dialogMsg.show();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(ModifyPswUserActivity.this, text, Toast.LENGTH_SHORT).show();
                btn_go.setEnabled(true);
            }

            @Override
            public void netStart(int status) {
                btn_go.setEnabled(false);
            }
        });
    }

    private void netLogout() {
        RequestParams params = new RequestParams(AppData.Url.logout);
        params.addHeader("token", AppData.App.getToken());
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                Toast.makeText(ModifyPswUserActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(ModifyPswUserActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
