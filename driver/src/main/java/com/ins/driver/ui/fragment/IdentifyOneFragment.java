package com.ins.driver.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ins.driver.R;
import com.ins.driver.ui.activity.IdentifyActivity;
import com.ins.middle.ui.fragment.BaseFragment;

/**
 * Created by Administrator on 2016/6/2 0002.
 */
public class IdentifyOneFragment extends BaseFragment implements View.OnClickListener{

    private int position;
    private View rootView;
    private ViewGroup showingroup;
    private View showin;

    private IdentifyActivity activity;


    public static Fragment newInstance(int position) {
        IdentifyOneFragment f = new IdentifyOneFragment();
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
        rootView = inflater.inflate(R.layout.fragment_identifyone, container, false);
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
        getView().findViewById(R.id.item_identify).setOnClickListener(this);
    }

    private void initData() {
    }

    private void initCtrl() {
    }

    private void freshCtrl() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.item_identify:
                activity.next();
                break;
        }
    }
}
