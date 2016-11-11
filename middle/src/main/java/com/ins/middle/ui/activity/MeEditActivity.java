package com.ins.middle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ins.middle.R;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.common.AppVali;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.entity.User;
import com.ins.middle.ui.dialog.DialogAge;
import com.ins.middle.ui.dialog.DialogLoading;
import com.ins.middle.ui.dialog.DialogPopupPhoto;
import com.ins.middle.ui.dialog.DialogSex;
import com.ins.middle.utils.AppHelper;
import com.ins.middle.utils.GlideUtil;
import com.sobey.common.helper.CropHelperSys;
import com.sobey.common.utils.PermissionsUtil;
import com.sobey.common.utils.StrUtils;

import org.greenrobot.eventbus.EventBus;
import org.xutils.http.RequestParams;

import java.io.File;

public class MeEditActivity extends BaseBackActivity implements View.OnClickListener, CropHelperSys.CropInterface {

    private CropHelperSys cropHelper = new CropHelperSys(this);
    private DialogLoading loadingDialog;
    private DialogPopupPhoto popup;

    private ViewGroup showingroup;
    private View showin;

    private ImageView img_meedit_header;
    private EditText edit_meedit_nick;
    private TextView text_meedit_sex;
    private TextView text_meedit_age;
    private EditText edit_meedit_sign;

    private DialogSex dialogSex;
    private DialogAge dialogAge;

    private String imgurl;

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
        if (dialogSex != null) dialogSex.dismiss();
        if (dialogAge != null) dialogAge.dismiss();
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
        dialogSex = new DialogSex(this);
        dialogSex.setOnSexClickListener(new DialogSex.OnSexClickListener() {
            @Override
            public void onSexClick(int sex) {
                text_meedit_sex.setText(sex == 0 ? "男" : "女");
            }
        });
        dialogAge = new DialogAge(this);
        dialogAge.setOnAgeClickListener(new DialogAge.OnAgeClickListener() {
            @Override
            public void onAgeClick(int age) {
                text_meedit_age.setText(age + "后");
                text_meedit_age.setTextColor(ContextCompat.getColor(MeEditActivity.this, R.color.com_text_blank));
            }
        });
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        img_meedit_header = (ImageView) findViewById(R.id.img_meedit_header);
        edit_meedit_nick = (EditText) findViewById(R.id.edit_meedit_nike);
        edit_meedit_sign = (EditText) findViewById(R.id.edit_meedit_sign);
        text_meedit_sex = (TextView) findViewById(R.id.text_meedit_sex);
        text_meedit_age = (TextView) findViewById(R.id.text_meedit_age);


        findViewById(R.id.lay_meedit_header).setOnClickListener(this);
        findViewById(R.id.lay_meedit_sex).setOnClickListener(this);
        findViewById(R.id.lay_meedit_age).setOnClickListener(this);
        findViewById(R.id.btn_right).setOnClickListener(this);
    }

    private void initData() {
    }

    private void initCtrl() {
        setUserData();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.lay_meedit_header) {
            if (PermissionsUtil.requsetPhoto(this, findViewById(R.id.showingroup))) {
                popup.show();
            }
        } else if (i == R.id.lay_meedit_sex) {
            dialogSex.show();
        } else if (i == R.id.lay_meedit_age) {
            dialogAge.show();
        } else if (i == R.id.btn_right) {
            String nick = edit_meedit_nick.getText().toString();
            String gender = "男".equals(text_meedit_sex.getText().toString()) ? "0" : "1";
            String agestr = text_meedit_age.getText().toString();
            String age = "请选择".equals(agestr) ? null : StrUtils.subLastChart(agestr, "后");
            String sign = edit_meedit_sign.getText().toString();
            netCommit(nick, gender, age, sign, imgurl);
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
            Log.e("liao", "path:" + path);
            netUploadImg(path);
        }
    }

    private void setUserData() {
        User user = AppData.App.getUser();
        if (user != null) {
            GlideUtil.loadCircleImg(this, img_meedit_header, R.drawable.default_header, AppHelper.getRealImgPath(user.getAvatar()));
            edit_meedit_nick.setText(user.getNickName());
            edit_meedit_sign.setText(user.getAutograph());
            text_meedit_sex.setText(user.getGender() == 0 ? "男" : "女");
            if (user.getAge() == 0) {
                text_meedit_age.setText("请选择");
                text_meedit_age.setTextColor(ContextCompat.getColor(this, R.color.com_text_dark));
            } else {
                text_meedit_age.setText(user.getAge() + "后");
                text_meedit_age.setTextColor(ContextCompat.getColor(this, R.color.com_text_blank));
            }
        }
    }

    private void netUploadImg(String localPath) {
        RequestParams params = new RequestParams(AppData.Url.upload);
        params.setMultipart(true);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("files", new File(localPath));
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                if (pojo == null) netSetError(code, text);
                else {
                    Toast.makeText(MeEditActivity.this, text, Toast.LENGTH_SHORT).show();
                    CommonEntity commonEntity = (CommonEntity) pojo;
                    imgurl = commonEntity.getFilePath();
                    //上传完毕，设置头像链接
                    GlideUtil.loadCircleImg(MeEditActivity.this, img_meedit_header, R.drawable.default_header, AppHelper.getRealImgPath(imgurl));
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(MeEditActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netEnd(int code) {
                loadingDialog.hide();
            }

            @Override
            public void netStart(int code) {
                loadingDialog.show();
            }
        });
    }

    private void netCommit(String nick, String gender, String age, String sign, String imgurl) {
        RequestParams params = new RequestParams(AppData.Url.updateUser);
        params.addHeader("token", AppData.App.getToken());
        if (!StrUtils.isEmpty(nick)) {
            params.addBodyParameter("nickName", nick);
        }
        if (!StrUtils.isEmpty(gender)) {
            params.addBodyParameter("gender", gender);
        }
        if (!StrUtils.isEmpty(age)) {
            params.addBodyParameter("age", age);
        }
        if (!StrUtils.isEmpty(sign)) {
            params.addBodyParameter("autograph", sign);
        }
        if (!StrUtils.isEmpty(imgurl)) {
            params.addBodyParameter("avatar", imgurl);
        }
        CommonNet.samplepost(params, User.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                Toast.makeText(MeEditActivity.this, text, Toast.LENGTH_SHORT).show();
                User user = (User) pojo;
                AppData.App.saveUser(user);
                EventBus.getDefault().post(AppConstant.EVENT_UPDATE_ME);
                finish();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(MeEditActivity.this, text, Toast.LENGTH_SHORT).show();
                loadingDialog.hide();
            }

            @Override
            public void netStart(int code) {
                loadingDialog.show();
            }
        });
    }
}
