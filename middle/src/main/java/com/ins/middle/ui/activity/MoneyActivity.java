package com.ins.middle.ui.activity;

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
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ins.middle.R;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.MoneyDetail;
import com.ins.middle.entity.User;
import com.ins.middle.utils.PackageUtil;
import com.sobey.common.common.DividerItemDecoration;
import com.sobey.common.common.LoadingViewUtil;
import com.ins.middle.entity.TestEntity;
import com.ins.middle.ui.adapter.RecycleAdapterMoney;
import com.sobey.common.helper.SwipeHelper;
import com.sobey.common.interfaces.OnRecycleItemClickListener;
import com.sobey.common.utils.NumAnim;
import com.sobey.common.utils.StrUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

public class MoneyActivity extends BaseBackActivity implements OnRecycleItemClickListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipe;
    private List<MoneyDetail> results = new ArrayList<>();
    private RecycleAdapterMoney adapter;

    private ViewGroup showingroup;
    private View showin;

    private TextView text_money;

    private float money;

    @Subscribe
    public void onEventMainThread(String flagSpc) {
        if (AppConstant.EVENT_CASH_MONEY.equals(AppConstant.getFlag(flagSpc))) {
            money = Float.parseFloat(AppConstant.getStr(flagSpc));
            NumAnim.startAnim(text_money, money);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);
        EventBus.getDefault().register(this);
        setToolbar();

        initBase();
        initView();
        initCtrl();
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initBase() {
        if (getIntent().hasExtra("money")) {
            money = getIntent().getFloatExtra("money", 0);
        }
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        text_money = (TextView) findViewById(R.id.text_money);
        findViewById(R.id.btn_go_cash).setOnClickListener(this);
        findViewById(R.id.btn_right).setOnClickListener(this);
    }

    private void initData() {
        netGetMoenyDetail(0);
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
                netGetMoenyDetail(1);
            }

            @Override
            public void onLoadmore() {
                netGetMoenyDetail(2);
            }
        });

        NumAnim.startAnim(text_money, money);   //第二个参数是textView要显示的价格
    }

    private void freshCtrl() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
//        Intent intent = new Intent(this, TripDetailActivity.class);
//        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_go_cash) {
            User user = AppData.App.getUser();
            if (user == null) return;
            if (user.getStatus() == User.AUTHENTICATED) {
                //已认证，进入提现
                Intent intent = new Intent(this, CashActivity.class);
                intent.putExtra("money", money);
                startActivity(intent);
            } else if (user.getStatus() == User.CERTIFICATIONING) {
                //认证中，跳转认证状态页面
                Toast.makeText(this, "实名认证中，等待工作人员审核通过后才可使用该功能", Toast.LENGTH_SHORT).show();
            }  else {
                Toast.makeText(this, "请先进行实名认证", Toast.LENGTH_SHORT).show();
                Intent identifyIntent = PackageUtil.getSmIntent("IdentifyActivity");
                identifyIntent.putExtra("type", 1);
                startActivity(identifyIntent);
            }
        } else if (i == R.id.btn_right) {
            Intent intent = new Intent(this, CashHisActivity.class);
            startActivity(intent);
        }
    }

    ///////////////////////////////////
    //////////////分页查询
    ///////////////////////////////////

    private Callback.Cancelable cancelable;
    private int page;
    private final int PAGE_COUNT = 10;
    private boolean needloadmore = true;

    /**
     * type:0 首次加载 1:下拉刷新 2:上拉加载
     *
     * @param type
     */
    private void netGetMoenyDetail(final int type) {
        //上拉加载的时候如果标志为false则不请求
        if (type == 2 && needloadmore == false) {
            return;
        }
        if (cancelable != null) cancelable.cancel();
        final RequestParams params = new RequestParams(AppData.Url.findWalletDetail);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("pageNO", type == 0 || type == 1 ? "1" : page + 1 + "");
        params.addBodyParameter("pageSize", PAGE_COUNT + "");
        cancelable = CommonNet.samplepost(params, new TypeToken<List<MoneyDetail>>() {
        }.getType(), new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                if (pojo == null) netSetError(code, "错误：返回数据为空");
                else {
                    List<MoneyDetail> coupons = (ArrayList<MoneyDetail>) pojo;
                    //有数据才添加，否则显示lack信息
                    if (!StrUtils.isEmpty(coupons)) {
                        List<MoneyDetail> results = adapter.getResults();
                        if (type == 0 || type == 1) {
                            results.clear();
                            page = 1;
                        } else {
                            page++;
                        }
                        results.addAll(coupons);
                        freshCtrl();
                        needloadmore = true;

                        if (type == 0) {
                            LoadingViewUtil.showout(showingroup, showin);
                        } else {
                            swipe.setRefreshing(false);
                        }
                    } else {
                        if (type == 0 || type == 1) {
                            showin = LoadingViewUtil.showin(showingroup, com.ins.middle.R.layout.layout_lack, showin, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    initData();
                                }
                            });
                        } else {
                            swipe.setRefreshing(false);
                            Snackbar.make(showingroup, "没有更多的数据了", Snackbar.LENGTH_SHORT).show();
                            needloadmore = false;
                        }
                    }
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(MoneyActivity.this, text, Toast.LENGTH_SHORT).show();
                if (type == 0) {
                    showin = LoadingViewUtil.showin(showingroup, com.ins.middle.R.layout.layout_fail, showin, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initData();
                        }
                    });
                } else {
                    swipe.setRefreshing(false);
                }
            }

            @Override
            public void netStart(int code) {
                if (type == 0) {
                    showin = LoadingViewUtil.showin(showingroup, com.ins.middle.R.layout.layout_loading, showin);
                }
            }
        });
    }
}
