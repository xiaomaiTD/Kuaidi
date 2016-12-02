package com.ins.middle.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ins.middle.R;
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.Eva;
import com.ins.middle.entity.Trip;
import com.ins.middle.utils.PackageUtil;
import com.sobey.common.common.LoadingViewUtil;
import com.ins.middle.entity.TestEntity;
import com.ins.middle.ui.adapter.RecycleAdapterTrip;
import com.liaoinstan.springview.container.AliFooter;
import com.liaoinstan.springview.container.AliHeader;
import com.liaoinstan.springview.widget.SpringView;
import com.sobey.common.interfaces.OnRecycleItemClickListener;

import java.util.ArrayList;
import java.util.List;

import com.ins.middle.ui.activity.BaseBackActivity;
import com.sobey.common.utils.StrUtils;

import org.greenrobot.eventbus.EventBus;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

public class TripActivity extends BaseBackActivity implements OnRecycleItemClickListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private SpringView springView;
    private List<Trip> results = new ArrayList<>();
    private RecycleAdapterTrip adapter;

    private TextView btn_right;

    private ViewGroup showingroup;
    private View showin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
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
        springView = (SpringView) findViewById(R.id.spring);
        btn_right = (TextView) findViewById(R.id.btn_right);
    }

    private void initData() {
        netGetTrips(0);
    }

    private void initCtrl() {
        adapter = new RecycleAdapterTrip(this, results);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        adapter.setOnItemClickListener(this);
        springView.setHeader(new AliHeader(this, false));
        springView.setFooter(new AliFooter(this, false));
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                netGetTrips(1);
            }

            @Override
            public void onLoadmore() {
                netGetTrips(2);
            }
        });

        //只有乘客可以删除行程
        if (PackageUtil.isClient()) {
            btn_right.setVisibility(View.VISIBLE);
            btn_right.setOnClickListener(this);
        } else {
            btn_right.setVisibility(View.GONE);
        }
    }

    private void freshCtrl() {
        adapter.notifyDataSetChanged();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (adapter.isTocheck()){
                adapter.setTocheck(false);
                setBtnRight();
            }else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_right) {
            if (btn_right.getText().equals("编辑")) {
                adapter.setTocheck(true);
            } else if (btn_right.getText().equals("取消")) {
                adapter.setTocheck(false);
            } else if (btn_right.getText().equals("删除")) {
                String selectIds = adapter.getSelectIds();
                Toast.makeText(this, selectIds, Toast.LENGTH_SHORT).show();
            }
            setBtnRight();
        }
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
        Trip trip = adapter.getResults().get(viewHolder.getLayoutPosition());
        //客户端点击进行中的行程会回到主页
        if (PackageUtil.isClient()) {
            //已送达的订单进入详情页，否则退回主页
            if (trip.getStatus() == 2006) {
                Intent intent = PackageUtil.getSmIntent("TripDetailActivity");
                intent.putExtra("orderId", trip.getId());
                intent.putExtra("trip", trip);
                startActivity(intent);
            } else {
                EventBus.getDefault().post(trip);
                finish();
            }
        } else {
            if (trip.getStatus() == 2006) {
                Intent intent = PackageUtil.getSmIntent("TripDetailActivity");
                intent.putExtra("orderId", trip.getId());
                intent.putExtra("trip", trip);
                startActivity(intent);
            } else {
                EventBus.getDefault().post(trip);
                finish();
            }
        }
    }

    public void setBtnRight() {
        if (!adapter.isTocheck()) {
            btn_right.setText("编辑");
        } else {
            if (StrUtils.isEmpty(adapter.getSelectIds())) {
                btn_right.setText("取消");
            } else {
                btn_right.setText("删除");
            }
        }
    }

    ///////////////////////////////////
    //////////////分页查询
    ///////////////////////////////////

    private Callback.Cancelable cancelable;
    private int page;
    private final int PAGE_COUNT = 10;

    /**
     * type:0 首次加载 1:下拉刷新 2:上拉加载
     *
     * @param type
     */
    private void netGetTrips(final int type) {
        if (cancelable != null) cancelable.cancel();
        final RequestParams params = new RequestParams(AppData.Url.getOrders);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("flag", "1");//1:所有行程
        params.addBodyParameter("pageNO", type == 0 || type == 1 ? "1" : page + 1 + "");
        params.addBodyParameter("pageSize", PAGE_COUNT + "");
        cancelable = CommonNet.samplepost(params, new TypeToken<List<Trip>>() {
        }.getType(), new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                if (pojo == null) netSetError(code, "错误：返回数据为空");
                else {
                    List<Trip> trips = (ArrayList<Trip>) pojo;
                    //有数据才添加，否则显示lack信息
                    if (!StrUtils.isEmpty(trips)) {
                        List<Trip> results = adapter.getResults();
                        if (type == 0 || type == 1) {
                            results.clear();
                            page = 1;
                        } else {
                            page++;
                        }
                        results.addAll(trips);
                        freshCtrl();

                        if (type == 0) {
                            LoadingViewUtil.showout(showingroup, showin);
                        } else {
                            springView.onFinishFreshAndLoad();
                        }
                    } else {
                        if (type == 0 || type == 1) {
                            showin = LoadingViewUtil.showin(showingroup, R.layout.layout_lack, showin, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    initData();
                                }
                            });
                        } else {
                            springView.onFinishFreshAndLoad();
                            Snackbar.make(showingroup, "没有更多的数据了", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(TripActivity.this, text, Toast.LENGTH_SHORT).show();
                if (type == 0) {
                    showin = LoadingViewUtil.showin(showingroup, R.layout.layout_fail, showin, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initData();
                        }
                    });
                } else {
                    springView.onFinishFreshAndLoad();
                }
            }

            @Override
            public void netStart(int code) {
                if (type == 0) {
                    showin = LoadingViewUtil.showin(showingroup, R.layout.layout_loading, showin);
                }
            }
        });
    }


}
