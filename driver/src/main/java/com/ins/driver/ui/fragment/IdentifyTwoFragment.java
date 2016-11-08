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
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppVali;
import com.ins.middle.ui.activity.CameraActivity;
import com.ins.driver.ui.activity.IdentifyActivity;
import com.ins.middle.ui.fragment.BaseFragment;
import com.ins.middle.utils.GlideUtil;
import com.sobey.common.utils.StrUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
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

    private ImageView img_identify_drivercard;
    private TextView text_identify_drivercard;
    private EditText edit_identify_name;
    private EditText edit_identify_drivernum;

    private static final int RESULT_CAMERA = 0xf104;

    private String path;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_identifytwo, container, false);
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
        img_identify_drivercard = (ImageView) getView().findViewById(R.id.img_identify_drivercard);
        text_identify_drivercard = (TextView) getView().findViewById(R.id.text_identify_drivercard);
        edit_identify_name = (EditText) getView().findViewById(R.id.edit_identify_name);
        edit_identify_drivernum = (EditText) getView().findViewById(R.id.edit_identify_drivernum);
        btn_go = getView().findViewById(R.id.btn_go);

        img_identify_drivercard.setOnClickListener(this);
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
            case R.id.img_identify_drivercard:
                intent.setClass(getActivity(), CameraActivity.class);
                startActivityForResult(intent, RESULT_CAMERA);
                break;
            case R.id.btn_go:

                String realName = edit_identify_name.getText().toString();
                String idcardnum = edit_identify_drivernum.getText().toString();

                String msg = AppVali.vali_identify_driverone(path, realName, idcardnum);
                if (!StrUtils.isEmpty(msg)) {
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    btn_go.setEnabled(true);
                } else {
                    EventBus.getDefault().post(new IdentifyBus(realName, idcardnum, path));
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
        GlideUtil.loadImg(getActivity(), img_identify_drivercard, R.drawable.default_bk, path);
        text_identify_drivercard.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        text_identify_drivercard.setText("点击图片再次拍摄");
        text_identify_drivercard.setTextColor(Color.WHITE);
    }

    private void testpritpaths() {
        Log.e("liao", "" + path);
    }

    public class IdentifyBus implements Serializable {
        public IdentifyBus(String name, String driverCardNum, String path) {
            this.name = name;
            this.driverCardNum = driverCardNum;
            this.path = path;
        }

        public String name;
        public String driverCardNum;
        public String path;
    }
}
