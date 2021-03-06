package com.ins.driver.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ins.driver.R;
import com.ins.driver.ui.activity.IdentifyActivity;
import com.ins.middle.common.AppData;
import com.ins.middle.common.AppVali;
import com.ins.middle.common.CommonNet;
import com.ins.middle.common.Uploader;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.entity.User;
import com.ins.middle.ui.activity.CameraActivity;
import com.ins.middle.ui.dialog.DialogLoading;
import com.ins.middle.ui.fragment.BaseFragment;
import com.ins.middle.utils.GlideUtil;
import com.sobey.common.utils.StrUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.http.RequestParams;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2016/6/2 0002.
 */
public class IdentifyFourFragment extends BaseFragment implements View.OnClickListener {

    private Uploader uploader = new Uploader();

    private DialogLoading loadingDialog;

    private int position;
    private View rootView;
    private ViewGroup showingroup;
    private View showin;
    private TextView btn_go;

    private IdentifyActivity activity;

    private ImageView img_identify_travelcard;
    private TextView text_identify_travelcard;
    private EditText edit_identify_carnum;
    private EditText edit_identify_cartype;
    private EditText edit_identify_carcolor;
    private EditText edit_identify_carowner;
    private EditText edit_identify_travelcardnum;

    private static final int RESULT_CAMERA = 0xf104;

    private String path;
    private IdentifyThreeFragment.IdentifyBus identifyBus;


    public static Fragment newInstance(int position) {
        IdentifyFourFragment f = new IdentifyFourFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        this.position = getArguments().getInt("position");
    }


    @Subscribe
    public void onEventMainThread(IdentifyThreeFragment.IdentifyBus identifyBus) {
        this.identifyBus = identifyBus;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (loadingDialog != null) loadingDialog.dismiss();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_identifyfour, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initBase();
        initView();
        initData();
        initCtrl();
    }

    private void initBase() {
        activity = (IdentifyActivity) getActivity();
        loadingDialog = new DialogLoading(getActivity(), "正在上传");
    }

    private void initView() {
        showingroup = (ViewGroup) getView().findViewById(R.id.showingroup);
        img_identify_travelcard = (ImageView) getView().findViewById(R.id.img_identify_travelcard);
        text_identify_travelcard = (TextView) getView().findViewById(R.id.text_identify_travelcard);
        edit_identify_carnum = (EditText) getView().findViewById(R.id.edit_identify_carnum);
        edit_identify_cartype = (EditText) getView().findViewById(R.id.edit_identify_cartype);
        edit_identify_carcolor = (EditText) getView().findViewById(R.id.edit_identify_carcolor);
        edit_identify_carowner = (EditText) getView().findViewById(R.id.edit_identify_carowner);
        edit_identify_travelcardnum = (EditText) getView().findViewById(R.id.edit_identify_travelcardnum);
        btn_go = (TextView) getView().findViewById(R.id.btn_go);

        img_identify_travelcard.setOnClickListener(this);
        btn_go.setOnClickListener(this);
    }

    private void initData() {
    }

    private void initCtrl() {
    }

    private void freshCtrl() {
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.img_identify_travelcard:
                intent.setClass(getActivity(), CameraActivity.class);
                startActivityForResult(intent, RESULT_CAMERA);
                break;
            case R.id.btn_go:
                btn_go.setEnabled(false);

                String carnum = edit_identify_carnum.getText().toString();
                String cartype = edit_identify_cartype.getText().toString();
                String carcolor = edit_identify_carcolor.getText().toString();
                String carowner = edit_identify_carowner.getText().toString();
                String travelcardnum = edit_identify_travelcardnum.getText().toString();

                String msg = AppVali.vali_identify_drivertwo(path, carnum, cartype, carcolor, carowner, travelcardnum);
                if (!StrUtils.isEmpty(msg)) {
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    btn_go.setEnabled(true);
                } else {
                    if (identifyBus != null) {
                        List<String> pathsList = Arrays.asList(identifyBus.pathDriverCard, path, identifyBus.pathIdcardFirst, identifyBus.pathIdcardLast);
                        loadingDialog.show();
                        netUpload_Commit(pathsList);
                    } else {
                        Toast.makeText(getActivity(), "数据已丢失,请返回上一页重新上传驾驶证", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_CAMERA:
                if (resultCode == RESULT_OK) {
                    // 成功
                    String path = data.getStringExtra("path");
                    if (!StrUtils.isEmpty(path)) {
                        //保存当前照片路径
                        this.path = path;
                        //打印测试
                        testpritpaths();
                        setPicData(path);
                    } else {
                        Toast.makeText(getActivity(), "拍摄异常", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 失败
                }
                break;
        }
    }

    private void setPicData(String path) {
        GlideUtil.loadImg(getActivity(), img_identify_travelcard, R.drawable.default_bk, path);
        text_identify_travelcard.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        text_identify_travelcard.setText("点击图片再次拍摄");
        text_identify_travelcard.setTextColor(Color.WHITE);
    }

    private void testpritpaths() {
        Log.e("liao", "" + path);
    }

    private void netUpload_Commit(List<String> paths) {
        uploader.startUpload(paths, new Uploader.UploadCallback() {
            @Override
            public void uploadfiled(int code, String text) {
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                btn_go.setEnabled(true);
            }

            @Override
            public void uploadSuccess(List<String> urls) {
                loadingDialog.hide();
                netCommit(urls);
            }
        });
    }

    private void netCommit(final List<String> urls) {

        String carnum = edit_identify_carnum.getText().toString();
        String cartype = edit_identify_cartype.getText().toString();
        String carcolor = edit_identify_carcolor.getText().toString();
        String carowner = edit_identify_carowner.getText().toString();
        String travelcardnum = edit_identify_travelcardnum.getText().toString();

        RequestParams params = new RequestParams(AppData.Url.identify);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("realName", identifyBus.name);
        params.addBodyParameter("driveLicenseNum", identifyBus.driverCardNum);
        params.addBodyParameter("carCard", carnum);
        params.addBodyParameter("carBrand", cartype);
        params.addBodyParameter("carColor", carcolor);
        params.addBodyParameter("carOwner", carowner);
        //params.addBodyParameter("travelcardnum", travelcardnum);
        params.addBodyParameter("driveLicenseImg", urls.get(0));
        params.addBodyParameter("driveingLicenseImg", urls.get(1));
        params.addBodyParameter("idCardNum", identifyBus.idcardnum);
        params.addBodyParameter("idCardImgs", urls.get(2) + "," + urls.get(3));      //身份证图片(以,隔开)

        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                loadingDialog.hide();
                btn_go.setEnabled(true);
                //设置用户数据
                User user = AppData.App.getUser();
                user.setStatus(User.CERTIFICATIONING);
                AppData.App.saveUser(user);
                getActivity().finish();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                loadingDialog.hide();
                btn_go.setEnabled(true);
            }
        });
    }
}
