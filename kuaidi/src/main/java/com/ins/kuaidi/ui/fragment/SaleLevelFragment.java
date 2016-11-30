package com.ins.kuaidi.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ins.kuaidi.R;
import com.ins.kuaidi.entity.SaleLevel;
import com.ins.kuaidi.ui.activity.SaleActivity;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.ui.fragment.BaseFragment;
import com.sobey.common.common.LoadingViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/2 0002.
 */
public class SaleLevelFragment extends BaseFragment implements View.OnClickListener {

    private int position;
    private View rootView;
    private ViewGroup showingroup;
    private View showin;

    private SaleActivity activity;

    private TextView text_saleleve_peopleone;
    private TextView text_saleleve_peopletwo;
    private TextView text_saleleve_peoplethree;
    private TextView text_saleleve_moneyone;
    private TextView text_saleleve_moneytwo;
    private TextView text_saleleve_moneythree;


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

        text_saleleve_peopleone = (TextView) getView().findViewById(R.id.text_saleleve_peopleone);
        text_saleleve_peopletwo = (TextView) getView().findViewById(R.id.text_saleleve_peopletwo);
        text_saleleve_peoplethree = (TextView) getView().findViewById(R.id.text_saleleve_peoplethree);
        text_saleleve_moneyone = (TextView) getView().findViewById(R.id.text_saleleve_moneyone);
        text_saleleve_moneytwo = (TextView) getView().findViewById(R.id.text_saleleve_moneytwo);
        text_saleleve_moneythree = (TextView) getView().findViewById(R.id.text_saleleve_moneythree);
    }

    private void initData() {
        netGetPayData();
    }

    private void initCtrl() {
    }

    private void freshCtrl() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_saleleve_one: {
                String money = text_saleleve_moneyone.getText().toString();
                EventBus.getDefault().post(AppConstant.makeFlagStr(AppConstant.EVENT_SALE, 1 + "|" + money));
                activity.next();
                break;
            }
            case R.id.item_saleleve_two: {
                String money = text_saleleve_moneytwo.getText().toString();
                EventBus.getDefault().post(AppConstant.makeFlagStr(AppConstant.EVENT_SALE, 2 + "|" + money));
                activity.next();
                break;
            }
            case R.id.item_saleleve_three: {
                String money = text_saleleve_moneythree.getText().toString();
                EventBus.getDefault().post(AppConstant.makeFlagStr(AppConstant.EVENT_SALE, 3 + "|" + money));
                activity.next();
                break;
            }
        }
    }

    private void setLevelData(List<SaleLevel> levels) {
        text_saleleve_moneyone.setText(levels.get(0).getMoney()+"元");
        text_saleleve_moneytwo.setText(levels.get(1).getMoney()+"元");
        text_saleleve_moneythree.setText(levels.get(2).getMoney()+"元");
        text_saleleve_peopleone.setText(levels.get(0).getPeopleNum()+"人");
        text_saleleve_peopletwo.setText(levels.get(1).getPeopleNum()+"人");
        text_saleleve_peoplethree.setText(levels.get(2).getPeopleNum()+"人");
    }

    public void netGetPayData() {
        RequestParams params = new RequestParams(AppData.Url.salelevel);
        params.addHeader("token", AppData.App.getToken());
        CommonNet.samplepost(params, new TypeToken<List<SaleLevel>>() {
        }.getType(), new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                if (pojo != null) {
                    List<SaleLevel> levels = (ArrayList<SaleLevel>)pojo;
                    setLevelData(levels);
                    LoadingViewUtil.showout(showingroup, showin);
                } else {
                    showin = LoadingViewUtil.showin(showingroup, R.layout.layout_lack, showin, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initData();
                        }
                    });
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                showin = LoadingViewUtil.showin(showingroup, R.layout.layout_fail, showin, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initData();
                    }
                });
            }

            @Override
            public void netStart(int status) {
                showin = LoadingViewUtil.showin(showingroup, R.layout.layout_loading, showin);
            }
        });
    }
}
