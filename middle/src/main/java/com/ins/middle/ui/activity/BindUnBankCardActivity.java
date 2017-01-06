package com.ins.middle.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ins.middle.R;
import com.ins.middle.common.AppData;
import com.ins.middle.common.AppVali;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.utils.AppHelper;
import com.ins.middle.utils.GlideUtil;
import com.sobey.common.utils.MD5Util;
import com.sobey.common.view.PswView;
import com.sobey.common.view.virtualKeyboardView.VirtualKeyboardView;

import org.xutils.http.RequestParams;

/**
 * type 0:解绑银行卡 1:提现
 */
public class BindUnBankCardActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;

    private TextView btn_go;
    private PswView pswView;
    private VirtualKeyboardView keybord;

    private int cardId;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bindunbankcard);

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
        if (getIntent().hasExtra("type")) {
            type = getIntent().getIntExtra("type", 0);
        }
        if (getIntent().hasExtra("id")) {
            cardId = getIntent().getIntExtra("id", 0);
        }
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        btn_go = (TextView) findViewById(R.id.btn_go);
        pswView = (PswView) findViewById(R.id.pswView);
        keybord = (VirtualKeyboardView) findViewById(R.id.keybord);
        btn_go.setOnClickListener(this);
    }

    private void initData() {
    }

    private void initCtrl() {
        if (type == 0) {
            setToolbar("解绑银行卡");
            btn_go.setText("确认解绑");
        } else {
            setToolbar("提现");
            btn_go.setText("确认");
        }
        //设置点击弹出自定义虚拟键盘
        keybord.setClickShowKeybord(pswView);
        //默认键盘可见
        keybord.setVisibility(View.VISIBLE);
        //设置虚拟键盘点击事件
        AppHelper.AttachKeybordWithPswView(keybord, pswView);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_go) {
            String psw = pswView.getPsw();
            if (type == 0) {
                String msg = AppVali.delBankCard(cardId, psw);
                if (msg != null) {
                    Toast.makeText(BindUnBankCardActivity.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    netAddBankCard(cardId, psw);
                }
            } else {
                netPaypsw(psw);
            }
        }
    }

    public void netAddBankCard(final int cardId, String psw) {
        RequestParams params = new RequestParams(AppData.Url.delBankCard);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("id", cardId + "");
        params.addBodyParameter("payPassword", MD5Util.md5(psw));
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                Toast.makeText(BindUnBankCardActivity.this, text, Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(BindUnBankCardActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netStart(int status) {
                btn_go.setEnabled(false);
            }

            @Override
            public void netEnd(int status) {
                btn_go.setEnabled(true);
            }
        });
    }

    public void netPaypsw(String psw) {
        RequestParams params = new RequestParams(AppData.Url.paypsw);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("payPassword", MD5Util.md5(psw));
        CommonNet.samplepost(params, Boolean.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                boolean success = (Boolean) pojo;
                if (success) {
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    netSetError(0, "密码错误");
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(BindUnBankCardActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netStart(int status) {
                btn_go.setEnabled(false);
            }

            @Override
            public void netEnd(int status) {
                btn_go.setEnabled(true);
            }
        });
    }
}
