package com.ins.kuaidi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ins.kuaidi.R;
import com.ins.kuaidi.ui.activity.BindBankCardActivity;
import com.ins.kuaidi.ui.activity.SaleActivity;


/**
 * Created by Administrator on 2016/6/2 0002.
 */
public class SaleLevelFragment extends BaseFragment implements View.OnClickListener {

    private int position;
    private View rootView;
    private ViewGroup showingroup;
    private View showin;

    private SaleActivity activity;


    public static Fragment newInstance(int position) {
        SaleLevelFragment f = new SaleLevelFragment();
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
        rootView = inflater.inflate(R.layout.fragment_salelevel, container, false);
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
        activity = (SaleActivity) getActivity();
    }

    private void initView() {
        showingroup = (ViewGroup) getView().findViewById(R.id.showingroup);
        getView().findViewById(R.id.item_saleleve_one).setOnClickListener(this);
        getView().findViewById(R.id.item_saleleve_two).setOnClickListener(this);
        getView().findViewById(R.id.item_saleleve_three).setOnClickListener(this);
    }

    private void initData() {
    }

    private void initCtrl() {
    }

    private void freshCtrl() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_saleleve_one:
                activity.next();
                break;
            case R.id.item_saleleve_two:
                activity.next();
                break;
            case R.id.item_saleleve_three:
                activity.next();
                break;
        }
    }
}
