package com.ins.kuaidi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ins.kuaidi.R;
import com.ins.kuaidi.common.DividerItemDecoration;
import com.ins.kuaidi.common.LoadingViewUtil;
import com.ins.kuaidi.entity.TestEntity;
import com.ins.kuaidi.ui.adpter.RecycleAdapterMoney;
import com.liaoinstan.springview.container.AliFooter;
import com.liaoinstan.springview.container.AliHeader;
import com.liaoinstan.springview.widget.SpringView;
import com.sobey.common.helper.SwipeHelper;
import com.sobey.common.interfaces.OnRecycleItemClickListener;
import com.sobey.common.utils.NumAnim;

import java.util.ArrayList;
import java.util.List;

public class MoneyActivity extends BaseBackActivity implements OnRecycleItemClickListener,View.OnClickListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipe;
    private List<TestEntity> results = new ArrayList<>();
    private RecycleAdapterMoney adapter;

    private ViewGroup showingroup;
    private View showin;

    private TextView text_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);
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
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        text_money = (TextView) findViewById(R.id.text_money);
        findViewById(R.id.btn_go_cash).setOnClickListener(this);
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
                results.add(new TestEntity());
                results.add(new TestEntity());
                results.add(new TestEntity());
                results.add(new TestEntity());
                results.add(new TestEntity());
                results.add(new TestEntity());
                results.add(new TestEntity());
                results.add(new TestEntity());
                results.add(new TestEntity());
                results.add(new TestEntity());
                results.add(new TestEntity());
                results.add(new TestEntity());
                results.add(new TestEntity());
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
        }, 1000);
    }

    private void initCtrl() {
        adapter = new RecycleAdapterMoney(this, results);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        adapter.setOnItemClickListener(this);

        SwipeHelper.setSwipeListener(swipe, recyclerView, new SwipeHelper.OnSwiperFreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe.setRefreshing(false);
                    }
                }, 2000);
            }

            @Override
            public void onLoadmore() {
            }
        });

        NumAnim.startAnim(text_money, 1253.47f);   //第二个参数是textView要显示的价格
    }

    private void freshCtrl() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
        Intent intent = new Intent(this, TripDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_go_cash:
                Intent intent = new Intent(this, CashActivity.class);
                startActivity(intent);
                break;
        }
    }
}
