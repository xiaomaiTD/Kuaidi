package com.ins.driver.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ins.driver.common.HomeHelper;
import com.ins.driver.common.ProgNetHelper;
import com.ins.driver.ui.adapter.RecycleAdapterProg;
import com.ins.driver.R;
import com.ins.driver.ui.dialog.DialogPayStatus;
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.entity.EventOrder;
import com.ins.middle.entity.TestEntity;
import com.ins.middle.entity.Trip;
import com.ins.middle.entity.User;
import com.ins.middle.ui.activity.BaseBackActivity;
import com.ins.middle.ui.activity.TripActivity;
import com.ins.middle.ui.dialog.DialogSure;
import com.ins.middle.view.ProgView;
import com.liaoinstan.springview.container.AliFooter;
import com.liaoinstan.springview.container.AliHeader;
import com.liaoinstan.springview.widget.SpringView;
import com.sobey.common.common.LoadingViewUtil;
import com.sobey.common.interfaces.OnRecycleItemClickListener;
import com.sobey.common.utils.StrUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

public class ProgActivity extends BaseBackActivity implements OnRecycleItemClickListener {

    private RecyclerView recyclerView;
    private SpringView springView;
    private List<Trip> results = new ArrayList<>();
    private RecycleAdapterProg adapter;

    private ViewGroup showingroup;
    private View showin;

    private DialogPayStatus dialogPayStatus;
    private DialogSure dialogSure;

    @Subscribe
    public void onEventMainThread(EventOrder eventOrder) {
        String aboutOrder = eventOrder.getAboutOrder();
        Log.e("liao", "aboutOrderProg" + aboutOrder);
        if ("8".equals(aboutOrder)) {
            //定金支付成功
            int orderId = eventOrder.getOrderId();
            if (orderId != 0) {
                netGetTrips(1);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prog);
        setToolbar();
        EventBus.getDefault().register(this);

        initBase();
        initView();
        initCtrl();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (dialogSure != null) dialogSure.dismiss();
        if (dialogPayStatus != null) dialogPayStatus.dismiss();
    }

    private void initBase() {
        dialogPayStatus = new DialogPayStatus(this, "27.5");
        dialogSure = new DialogSure(this, "您已将乘客全部送达，是否继续接单？");
        dialogSure.setOnOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //把司机状态更新成在线状态
//                User user = AppData.App.getUser();
//                user.setIsOnline(1);
//                AppData.App.saveUser(user);

                EventOrder eventOrder = new EventOrder();
                eventOrder.setAboutOrder("102");
                EventBus.getDefault().post(eventOrder);
                finish();
            }
        });
        dialogSure.setOnCancleListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //把司机状态更新成离线状态
//                User user = AppData.App.getUser();
//                user.setIsOnline(0);
//                AppData.App.saveUser(user);

                EventOrder eventOrder = new EventOrder();
                eventOrder.setAboutOrder("103");
                EventBus.getDefault().post(eventOrder);
                finish();
            }
        });
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        springView = (SpringView) findViewById(R.id.spring);
    }

    private void initData() {
        netGetTrips(0);
    }

    private void initCtrl() {
        adapter = new RecycleAdapterProg(this, results);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        adapter.setOnRecycleProgListener(onRecycleProgListener);
        adapter.setOnItemClickListener(this);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this));
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

        dialogPayStatus.show();
    }

    private void freshCtrl() {
        adapter.notifyDataSetChanged();
    }

    ///////////////////////////////
    /////////事件回调
    ///////////////////////////////

    //行程进度控件事件回调
    private RecycleAdapterProg.OnRecycleProgListener onRecycleProgListener = new RecycleAdapterProg.OnRecycleProgListener() {
        @Override
        public void onRequestFirstMoney(ProgView progView, Trip trip) {
            ProgNetHelper.netReqFirstMoney(progView, trip.getId());
        }

        @Override
        public void onGetPassenger(ProgView progView, Trip trip) {
            ProgNetHelper.netReqGetPassenger(progView, trip.getId());
        }

        @Override
        public void onArrive(ProgView progView, Trip trip) {
            netArrive(progView, trip.getId());
        }
    };

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
        params.addBodyParameter("flag", "0");//1:所有行程 0 当前行程
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
                            showin = LoadingViewUtil.showin(showingroup, com.ins.middle.R.layout.layout_lack, showin, new View.OnClickListener() {
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
                Toast.makeText(ProgActivity.this, text, Toast.LENGTH_SHORT).show();
                if (type == 0) {
                    showin = LoadingViewUtil.showin(showingroup, com.ins.middle.R.layout.layout_fail, showin, new View.OnClickListener() {
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
                    showin = LoadingViewUtil.showin(showingroup, com.ins.middle.R.layout.layout_loading, showin);
                }
            }
        });
    }

    public void netArrive(final ProgView progView, final int orderId) {
        RequestParams params = new RequestParams(AppData.Url.arrive);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("orderId", orderId + "");
        CommonNet.samplepost(params, new TypeToken<Integer>() {
        }.getType(), new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                Toast.makeText(progView.getContext(), text, Toast.LENGTH_SHORT).show();
                Integer isArrive = (Integer) pojo;
                if (isArrive==1){
                    //如果乘客已经全部下车
                    dialogSure.show();
                }else {
                    //还没有全部下车
                }
                progView.setArrive();

//                EventOrder eventOrder = new EventOrder();
//                eventOrder.setAboutOrder("5");
//                eventOrder.setOrderId(orderId);
//                EventBus.getDefault().post(eventOrder);
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(progView.getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
        Trip trip = adapter.getResults().get(viewHolder.getLayoutPosition());
        if (trip.getStatus() >= 2005) {
            Toast.makeText(this, "该乘客已上车", Toast.LENGTH_SHORT).show();
        } else {
            EventBus.getDefault().post(trip);
            finish();
        }
    }
}
