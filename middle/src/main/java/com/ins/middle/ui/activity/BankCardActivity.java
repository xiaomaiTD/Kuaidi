package com.ins.middle.ui.activity;

import android.content.Intent;
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
import com.ins.middle.R;
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.BankCard;
import com.ins.middle.entity.User;
import com.ins.middle.ui.dialog.DialogSure;
import com.ins.middle.utils.AppHelper;
import com.sobey.common.common.LoadingViewUtil;
import com.ins.middle.ui.adapter.RecycleAdapterBankCard;
import com.liaoinstan.springview.container.AliFooter;
import com.liaoinstan.springview.container.AliHeader;
import com.liaoinstan.springview.widget.SpringView;
import com.sobey.common.interfaces.OnRecycleItemClickListener;
import com.sobey.common.utils.StrUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

public class BankCardActivity extends BaseBackActivity implements OnRecycleItemClickListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private SpringView springView;
    private List<BankCard> results = new ArrayList<>();
    private RecycleAdapterBankCard adapter;

    private ViewGroup showingroup;
    private View showin;

    DialogSure dialogSure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankcard);
        setToolbar();

        initBase();
        initView();
        initCtrl();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialogSure != null) dialogSure.dismiss();
    }

    private void initBase() {
        dialogSure = new DialogSure(this);
        dialogSure.setOnOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //有提现密码则提现 1，没有则设置提现密码 0
                if (AppData.App.getUser().getHasPayPassword()==1) {
                    Intent intent = new Intent(BankCardActivity.this, BindUnBankCardActivity.class);
                    intent.putExtra("id", cardId);
                    startActivityForResult(intent, RESULT_BINDUNBANKCARD);
                    dialogSure.dismiss();
                }else {
                    //去设置提现密码
                    Toast.makeText(BankCardActivity.this, "您还没有设置提现密码，请先设置", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BankCardActivity.this, ModifyPswPayActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        springView = (SpringView) findViewById(R.id.spring);
        findViewById(R.id.btn_right).setOnClickListener(this);
    }

    private void initData() {
        netGetTrips(0);
    }

    private void initCtrl() {
        adapter = new RecycleAdapterBankCard(this, results);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        adapter.setOnItemClickListener(this);
        springView.setHeader(new AliHeader(this, false));
        springView.setFooter(new AliFooter(this, false));
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
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_right) {
            Intent intent = new Intent(this, BindBankCardActivity.class);
            startActivityForResult(intent, RESULT_BINDBANKCARD);
        }
    }

    private int cardId;

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
        BankCard card = adapter.getResults().get(viewHolder.getLayoutPosition());
        cardId = card.getId();
        dialogSure.setTitle("解绑" + card.getBankName());
        dialogSure.setMsg(AppHelper.getUnSeeBankNum(card.getBankNum()));
        dialogSure.show();
    }

    private static final int RESULT_BINDBANKCARD = 0xf101;
    private static final int RESULT_BINDUNBANKCARD = 0xf102;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_BINDBANKCARD:
                if (resultCode == RESULT_OK) {
                    Log.e("liao", "RESULT_OK");
                    netGetTrips(1);
                }
                break;
            case RESULT_BINDUNBANKCARD:
                if (resultCode == RESULT_OK) {
                    Log.e("liao", "RESULT_OK");
                    netGetTrips(1);
                }
                break;
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
        final RequestParams params = new RequestParams(AppData.Url.findBankCard);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("pageNO", type == 0 || type == 1 ? "1" : page + 1 + "");
        params.addBodyParameter("pageSize", PAGE_COUNT + "");
        cancelable = CommonNet.samplepost(params, new TypeToken<List<BankCard>>() {
        }.getType(), new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                if (pojo == null) netSetError(code, "错误：返回数据为空");
                else {
                    List<BankCard> cards = (ArrayList<BankCard>) pojo;
                    //有数据才添加，否则显示lack信息
                    if (!StrUtils.isEmpty(cards)) {
                        List<BankCard> results = adapter.getResults();
                        if (type == 0 || type == 1) {
                            results.clear();
                            page = 1;
                        } else {
                            page++;
                        }
                        results.addAll(cards);
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
                Toast.makeText(BankCardActivity.this, text, Toast.LENGTH_SHORT).show();
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
