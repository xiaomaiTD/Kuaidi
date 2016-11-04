package com.ins.kuaidi.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ins.kuaidi.R;
import com.sobey.common.common.LoadingViewUtil;
import com.ins.middle.entity.TestEntity;
import com.ins.kuaidi.ui.activity.MeDetailActivity;
import com.ins.kuaidi.ui.activity.SaleActivity;
import com.ins.kuaidi.ui.adapter.RecycleAdapterSale;
import com.liaoinstan.springview.container.AliFooter;
import com.liaoinstan.springview.container.AliHeader;
import com.liaoinstan.springview.widget.SpringView;
import com.sobey.common.interfaces.OnRecycleItemClickListener;

import java.util.ArrayList;
import java.util.List;
import com.ins.middle.ui.fragment.BaseFragment;

/**
 * Created by Administrator on 2016/6/2 0002.
 */
public class SalePeopleFragment extends BaseFragment implements OnRecycleItemClickListener {

    private int position;
    private View rootView;
    private ViewGroup showingroup;
    private View showin;

    private RecyclerView recyclerView;
    private SpringView springView;
    private List<TestEntity> results = new ArrayList<>();
    private RecycleAdapterSale adapter;

    private SaleActivity activity;

    public static Fragment newInstance(int position) {
        SalePeopleFragment f = new SalePeopleFragment();
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
        rootView = inflater.inflate(R.layout.fragment_salepeople, container, false);
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
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycle);
        springView = (SpringView) getView().findViewById(R.id.spring);
    }

    private void initData() {
        showin = LoadingViewUtil.showin(showingroup, R.layout.layout_loading, showin);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //加载成功
                List<TestEntity> results = adapter.getResults();
                results.clear();
                results.add(new TestEntity());
                results.add(new TestEntity());
                results.add(new TestEntity());
                freshCtrl();
                LoadingViewUtil.showout(showingroup, showin);

                //加载失败
//                LoadingViewUtil.showin(showingroup,R.layout.layout_lack,showin,new View.OnClickListener(){
//                    @Override
//                    public void onClick(View v) {
//                        initData();
//                    }
//                });
            }
        }, 500);
    }

    private void initCtrl() {
        adapter = new RecycleAdapterSale(getActivity(), results);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        adapter.setOnItemClickListener(this);
        springView.setHeader(new AliHeader(getActivity(), false));
        springView.setFooter(new AliFooter(getActivity(), false));
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        springView.onFinishFreshAndLoad();
                    }
                }, 2000);
            }

            @Override
            public void onLoadmore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        springView.onFinishFreshAndLoad();
                        Snackbar.make(showingroup, "没有更多的数据了", Snackbar.LENGTH_SHORT).show();
                    }
                }, 2000);
            }
        });
    }

    private void freshCtrl() {
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
        Intent intent = new Intent(getActivity(), MeDetailActivity.class);
        startActivity(intent);
    }
}
