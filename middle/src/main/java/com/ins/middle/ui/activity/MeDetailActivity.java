package com.ins.middle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ins.middle.R;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.entity.User;
import com.ins.middle.utils.AppHelper;
import com.ins.middle.utils.GlideUtil;
import com.ins.middle.utils.PackageUtil;
import com.sobey.common.utils.StrUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MeDetailActivity extends BaseBackActivity implements View.OnClickListener {

    private ViewGroup showingroup;
    private View showin;

    private ImageView img_me_header;
    private TextView text_me_name;
    private TextView text_me_sign;
    private TextView text_me_identify;
    private TextView text_me_identifystatus;
    private TextView text_me_fen;
    private TextView text_me_fenstatus;
    private View lay_me_fen;
    private View btn_go_right;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medetail);
        setToolbar();
        EventBus.getDefault().register(this);

        initBase();
        initView();
        initData();
        initCtrl();
    }

    @Subscribe
    public void onEventMainThread(Integer flag) {
        if (flag == AppConstant.EVENT_UPDATE_ME) {
            setUserData(AppData.App.getUser());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initBase() {
        if (getIntent().hasExtra("user")) {
            user = (User) getIntent().getSerializableExtra("user");
        }
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        img_me_header = (ImageView) findViewById(R.id.img_me_header);

        text_me_name = (TextView) findViewById(R.id.text_me_name);
        text_me_sign = (TextView) findViewById(R.id.text_me_sign);
        text_me_identify = (TextView) findViewById(R.id.text_me_identify);
        text_me_identifystatus = (TextView) findViewById(R.id.text_me_identifystatus);
        text_me_fen = (TextView) findViewById(R.id.text_me_fen);
        text_me_fenstatus = (TextView) findViewById(R.id.text_me_fenstatus);
        lay_me_fen = findViewById(R.id.lay_me_fen);

        btn_go_right = findViewById(R.id.btn_right);
        btn_go_right.setOnClickListener(this);
    }

    private void initData() {
    }

    private void initCtrl() {
        //有user参数，代表是查看别人的信息,没有则是自己的
        if (user != null) {
            btn_go_right.setVisibility(View.GONE);
            lay_me_fen.setVisibility(View.VISIBLE);
            text_me_fenstatus.setText(user.getMoney() + "元");
            setUserData(user);
        } else {
            btn_go_right.setVisibility(View.VISIBLE);
            lay_me_fen.setVisibility(View.GONE);
            setUserData(AppData.App.getUser());
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        int i = v.getId();
        if (i == R.id.btn_right) {
            intent.setClass(this, MeEditActivity.class);
            startActivity(intent);
        }
    }

    private void setUserData(User user) {
        if (user != null) {
            GlideUtil.loadCircleImg(this, img_me_header, R.drawable.default_header, AppHelper.getRealImgPath(user.getAvatar()));
            text_me_name.setText(user.getNickName());
            if (!StrUtils.isEmpty(user.getAutograph())) {
                text_me_sign.setText(user.getAutograph());
            }

            //设置认证方式
            if (PackageUtil.isClient()) {
                text_me_identify.setText("实名认证");
                text_me_identify.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.icon_safe_idcard), null, null, null);
            } else {
                text_me_identify.setText("车主认证");
                text_me_identify.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.icon_sale_drivercard), null, null, null);
            }

            //设置认证状态
            if (user.getStatus() == User.UNAUTHORIZED) {
                text_me_identifystatus.setText("未认证");
                text_me_identifystatus.setTextColor(ContextCompat.getColor(this, R.color.com_text_dark));
                text_me_identifystatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_me_identify, 0, 0, 0);
            } else if (user.getStatus() == User.CERTIFICATIONING) {
                text_me_identifystatus.setText("认证中");
                text_me_identifystatus.setTextColor(ContextCompat.getColor(this, R.color.com_text_dark));
                text_me_identifystatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_me_identify, 0, 0, 0);
            } else if (user.getStatus() == User.AUTHENTICATED) {
                text_me_identifystatus.setText("已认证");
                text_me_identifystatus.setTextColor(ContextCompat.getColor(this, R.color.com_text_blank));
                text_me_identifystatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_me_identify_hot, 0, 0, 0);
            }
        }
    }
}
