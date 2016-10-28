package com.ins.kuaidi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.ins.kuaidi.R;
import com.ins.kuaidi.common.LoadingViewUtil;
import com.ins.kuaidi.entity.Complaint;
import com.ins.kuaidi.entity.TestEntity;
import com.ins.kuaidi.ui.adpter.RecycleAdapterComplaint;
import com.ins.kuaidi.ui.adpter.RecycleAdapterTrip;
import com.liaoinstan.springview.container.AliFooter;
import com.liaoinstan.springview.container.AliHeader;
import com.liaoinstan.springview.widget.SpringView;
import com.sobey.common.interfaces.OnRecycleItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ComplaintActivity extends BaseBackActivity implements OnRecycleItemClickListener {

    private RecyclerView recyclerView;
    private List<Complaint> results = new ArrayList<>();
    private RecycleAdapterComplaint adapter;

    private ViewGroup showingroup;
    private View showin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        setToolbar();

        initBase();
        initView();
        initCtrl();
        initData();
    }

    private void initBase() {
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
    }

    private void initData() {
        showin = LoadingViewUtil.showin(showingroup, R.layout.layout_loading, showin);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //加载成功
                List<Complaint> results = adapter.getResults();
                results.clear();
                results.add(new Complaint("司机服务态度恶劣"));
                results.add(new Complaint("车内环境差"));
                results.add(new Complaint("危险驾驶"));
                results.add(new Complaint("耽误行程"));
                results.add(new Complaint("超员拉客"));
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
        }, 1000);
    }

    private void initCtrl() {
        adapter = new RecycleAdapterComplaint(this, results);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
//        recyclerView.setNestedScrollingEnabled(false);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        adapter.setOnItemClickListener(this);
    }

    private void freshCtrl() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
    }
}
