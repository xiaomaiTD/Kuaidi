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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ins.driver.R;
import com.ins.driver.ui.activity.IdentifyActivity;
import com.ins.middle.ui.activity.CameraActivity;
import com.ins.middle.ui.fragment.BaseFragment;
import com.ins.middle.utils.GlideUtil;
import com.sobey.common.utils.StrUtils;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2016/6/2 0002.
 */
public class IdentifyThreeFragment extends BaseFragment implements View.OnClickListener {

    private int position;
    private View rootView;
    private ViewGroup showingroup;
    private View showin;

    private IdentifyActivity activity;

    private ImageView img_identify_travelcard;
    private TextView text_identify_travelcard;

    private static final int RESULT_CAMERA = 0xf104;

    private String path;

    public static Fragment newInstance(int position) {
        IdentifyThreeFragment f = new IdentifyThreeFragment();
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
        rootView = inflater.inflate(R.layout.fragment_identifythree, container, false);
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
        img_identify_travelcard = (ImageView) getView().findViewById(R.id.img_identify_travelcard);
        text_identify_travelcard = (TextView) getView().findViewById(R.id.text_identify_travelcard);

        img_identify_travelcard.setOnClickListener(this);
        getView().findViewById(R.id.btn_go).setOnClickListener(this);
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
                getActivity().finish();
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
        GlideUtil.loadImg(getActivity(), img_identify_travelcard, R.drawable.test, path);
        text_identify_travelcard.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        text_identify_travelcard.setText("点击图片再次拍摄");
        text_identify_travelcard.setTextColor(Color.WHITE);
    }

    private void testpritpaths() {
        Log.e("liao", "" + path);
    }
}
