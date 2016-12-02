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
import com.ins.middle.utils.AppHelper;
import com.ins.middle.utils.GlideUtil;
import com.sobey.common.utils.StrUtils;

import org.greenrobot.eventbus.EventBus;
import org.xutils.http.RequestParams;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2016/6/2 0002.
 */
public class IdentifyTwoFragment extends BaseFragment implements View.OnClickListener {

    private int position;
    private View rootView;
    private ViewGroup showingroup;
    private View showin;
    private View btn_go;

    private IdentifyActivity activity;

    private ImageView img_identify_idcardfront;
    private ImageView img_identify_idcardback;
    private TextView text_identify_idcardfront;
    private TextView text_identify_idcardback;
    private EditText edit_identify_name;
    private EditText edit_identify_idcardnum;

    private static final int RESULT_CAMERA = 0xf104;

    //表示当前点击的位置（正面，反面）
    private int cardposition;
    private String[] paths = new String[2];

    public static Fragment newInstance(int position) {
        IdentifyTwoFragment f = new IdentifyTwoFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.position = getArguments().getInt("position");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_identifyidcard, container, false);
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
    }

    private void initView() {
        showingroup = (ViewGroup) getView().findViewById(R.id.showingroup);
        img_identify_idcardfront = (ImageView) getView().findViewById(R.id.img_identify_idcardfront);
        img_identify_idcardback = (ImageView) getView().findViewById(R.id.img_identify_idcardback);
        text_identify_idcardfront = (TextView) getView().findViewById(R.id.text_identify_idcardfront);
        text_identify_idcardback = (TextView) getView().findViewById(R.id.text_identify_idcardback);
        edit_identify_name = (EditText) getView().findViewById(R.id.edit_identify_name);
        edit_identify_idcardnum = (EditText) getView().findViewById(R.id.edit_identify_idcardnum);
        btn_go = getView().findViewById(R.id.btn_go);

        img_identify_idcardfront.setOnClickListener(this);
        img_identify_idcardback.setOnClickListener(this);
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
            case R.id.img_identify_idcardfront:
                cardposition = 0;   //表示点击正面
                intent.setClass(getActivity(), CameraActivity.class);
                startActivityForResult(intent, RESULT_CAMERA);
                break;
            case R.id.img_identify_idcardback:
                cardposition = 1;   //表示点击背面
                intent.setClass(getActivity(), CameraActivity.class);
                startActivityForResult(intent, RESULT_CAMERA);
                break;
            case R.id.btn_go:
                String realName = edit_identify_name.getText().toString();
                String idcardnum = edit_identify_idcardnum.getText().toString();

                String msg = AppVali.vali_identify_passenger(paths, realName, idcardnum);
                if (!StrUtils.isEmpty(msg)) {
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                } else {
//                    List<String> pathsList = Arrays.asList(paths);
//                    loadingDialog.show();
//                    netUpload_Commit(pathsList);
                    EventBus.getDefault().post(new IdentifyThreeFragment.IdentifyBus(realName, idcardnum, paths[0], paths[1]));
                    activity.next();
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
                        paths[cardposition] = path;
                        //打印测试
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
        if (cardposition == 0) {
            GlideUtil.loadImg(getActivity(), img_identify_idcardfront, R.drawable.default_bk, path);
            text_identify_idcardfront.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            text_identify_idcardfront.setText("点击图片再次拍摄");
            text_identify_idcardfront.setTextColor(Color.WHITE);
        } else {
            GlideUtil.loadImg(getActivity(), img_identify_idcardback, R.drawable.default_bk, path);
            text_identify_idcardback.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            text_identify_idcardback.setText("点击图片再次拍摄");
            text_identify_idcardback.setTextColor(Color.WHITE);
        }
    }
}
