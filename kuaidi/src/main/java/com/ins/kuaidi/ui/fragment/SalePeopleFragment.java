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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ins.kuaidi.R;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.SalePeople;
import com.ins.middle.entity.SalePeoplePojo;
import com.ins.middle.entity.Trip;
import com.ins.middle.entity.User;
import com.ins.middle.ui.activity.TripActivity;
import com.sobey.common.common.LoadingViewUtil;
import com.ins.middle.entity.TestEntity;
import com.ins.middle.ui.activity.MeDetailActivity;
import com.ins.kuaidi.ui.activity.SaleActivity;
import com.ins.kuaidi.ui.adapter.RecycleAdapterSale;
import com.liaoinstan.springview.container.AliFooter;
import com.liaoinstan.springview.container.AliHeader;
import com.liaoinstan.springview.widget.SpringView;
import com.sobey.common.interfaces.OnRecycleItemClickListener;

import java.util.ArrayList;
import java.util.List;

import com.ins.middle.ui.fragment.BaseFragment;
import com.sobey.common.utils.NumUtil;
import com.sobey.common.utils.StrUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

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
    private List<User> results = new ArrayList<>();
    private RecycleAdapterSale adapter;

    private TextView text_salepeople_level;
    private TextView text_salepeople_money;

    private SaleActivity activity;

    //分销等级1,2,3
    private int type;

    public static Fragment newInstance(int position) {
        SalePeopleFragment f = new SalePeopleFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        f.setArguments(b);
        return f;
    }

    @Subscribe
    public void onEventMainThread(String flagSpc) {
        if (AppConstant.EVENT_SALE.equals(AppConstant.getFlag(flagSpc))) {
            String value = AppConstant.getStr(flagSpc);
            String[] split = value.split("\\|");
            type = Integer.parseInt(split[0]);
            text_salepeople_level.setText(NumUtil.intToZH(type) + "级分销");
            text_salepeople_money.setText("累计获得 " + split[1]);
            netGetSalePeople(0);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.position = getArguments().getInt("position");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        //initData();
        initCtrl();
    }

    private void initBase() {
        activity = (SaleActivity) getActivity();
    }

    private void initView() {
        showingroup = (ViewGroup) getView().findViewById(R.id.showingroup);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycle);
        springView = (SpringView) getView().findViewById(R.id.spring);
        text_salepeople_level = (TextView) getView().findViewById(R.id.text_salepeople_level);
        text_salepeople_money = (TextView) getView().findViewById(R.id.text_salepeople_money);
    }

    private void initData() {
        netGetSalePeople(0);
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
                netGetSalePeople(1);
            }

            @Override
            public void onLoadmore() {
                netGetSalePeople(2);
            }
        });
    }

    private void freshCtrl() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
        User user = adapter.getResults().get(viewHolder.getLayoutPosition());
        Intent intent = new Intent(getActivity(), MeDetailActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
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
    private void netGetSalePeople(final int type) {
        if (cancelable != null) cancelable.cancel();
        final RequestParams params = new RequestParams(AppData.Url.salepeople);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("level", this.type + "");
        params.addBodyParameter("pageNO", type == 0 || type == 1 ? "1" : page + 1 + "");
        params.addBodyParameter("pageSize", PAGE_COUNT + "");
        cancelable = CommonNet.samplepost(params, SalePeoplePojo.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                if (pojo == null) netSetError(code, "错误：返回数据为空");
                else {
                    SalePeoplePojo salePeoplePojo = (SalePeoplePojo) pojo;
                    List<User> peoples = salePeoplePojo.getDlist();
                    //有数据才添加，否则显示lack信息
                    if (!StrUtils.isEmpty(peoples)) {
                        List<User> results = adapter.getResults();
                        if (type == 0 || type == 1) {
                            results.clear();
                            page = 1;
                        } else {
                            page++;
                        }
                        results.addAll(peoples);
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
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
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
}
