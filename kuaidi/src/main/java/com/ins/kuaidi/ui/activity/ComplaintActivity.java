package com.ins.kuaidi.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.ins.kuaidi.R;
import com.ins.middle.common.AppData;
import com.ins.middle.common.AppVali;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.ui.dialog.DialogLoading;
import com.sobey.common.common.LoadingViewUtil;
import com.ins.middle.entity.Complaint;
import com.ins.kuaidi.ui.adapter.RecycleAdapterComplaint;
import com.sobey.common.interfaces.OnRecycleItemClickListener;

import java.util.ArrayList;
import java.util.List;

import com.ins.middle.ui.activity.BaseBackActivity;

import org.xutils.http.RequestParams;

public class ComplaintActivity extends BaseBackActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private List<Complaint> results = new ArrayList<>();
    private RecycleAdapterComplaint adapter;

    private ViewGroup showingroup;
    private View showin;
    private EditText edit_complain_describe;

    private int orderId;

    private DialogLoading dialogLoading;

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
        dialogLoading = new DialogLoading(this, "正在提交");
        if (getIntent().hasExtra("orderId")) {
            orderId = getIntent().getIntExtra("orderId", 0);
        }
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        edit_complain_describe = (EditText) findViewById(R.id.edit_complain_describe);

        findViewById(R.id.btn_right).setOnClickListener(this);
    }

    private void initData() {
        showin = LoadingViewUtil.showin(showingroup, R.layout.layout_loading, showin);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //加载成功
                List<Complaint> results = adapter.getResults();
                results.clear();
                results.add(new Complaint(0, "司机服务态度恶劣"));
                results.add(new Complaint(1, "车内环境差"));
                results.add(new Complaint(2, "危险驾驶"));
                results.add(new Complaint(3, "耽误行程"));
                results.add(new Complaint(4, "超员拉客"));
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
        }, 800);
    }

    private void initCtrl() {
        adapter = new RecycleAdapterComplaint(this, results);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
//        recyclerView.setNestedScrollingEnabled(false);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this));
    }

    private void freshCtrl() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_right:
                String detail = edit_complain_describe.getText().toString();
                String ids = adapter.getSelectIds();
                String msg = AppVali.complainadd(ids, detail);
                if (msg != null) {
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    netAddComplain(orderId, detail);
                }
                break;
        }
    }


    public void netAddComplain(int orderId, String content) {
        RequestParams params = new RequestParams(AppData.Url.complain);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("content", content);
        params.addBodyParameter("orderId", orderId + "");
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                Toast.makeText(ComplaintActivity.this, "我们会认真处理您的投诉内容", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(ComplaintActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netStart(int status) {
                dialogLoading.show();
            }

            @Override
            public void netEnd(int status) {
                dialogLoading.hide();
                ;
            }
        });
    }

}
