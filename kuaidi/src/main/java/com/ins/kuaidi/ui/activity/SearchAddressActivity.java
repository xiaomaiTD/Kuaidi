package com.ins.kuaidi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.ins.kuaidi.R;
import com.ins.middle.entity.Position;
import com.ins.kuaidi.ui.adapter.RecycleAdapterSearchAddress;
import com.ins.middle.ui.activity.CityActivity;
import com.sobey.common.common.LoadingViewUtil;
import com.sobey.common.interfaces.OnRecycleItemClickListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import com.ins.middle.ui.activity.BaseBackActivity;
import com.sobey.common.utils.StrUtils;

public class SearchAddressActivity extends BaseBackActivity implements OnRecycleItemClickListener, OnGetSuggestionResultListener, OnGetPoiSearchResultListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private List<Position> results = new ArrayList<>();
    private RecycleAdapterSearchAddress adapter;

    private EditText edit_search;
    private TextView btn_cancel;
    private TextView btn_go_left;

    private ViewGroup showingroup;
    private View showin;

    //搜索建议
    private SuggestionSearch mSuggestionSearch = null;
    private PoiSearch mPoiSearch = null;

    //默认成都市
    private String city = "成都市";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchaddress);
        setToolbar(null, false);

        initBase();
        initView();
        initCtrl();
        initData();
    }

    @Override
    protected void onDestroy() {
        mSuggestionSearch.destroy();
        mPoiSearch.destroy();
        super.onDestroy();
    }

    private void initBase() {
        if (getIntent().hasExtra("city")) {
            city = getIntent().getStringExtra("city");
        }
        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
    }

    private void initView() {
        showingroup = (ViewGroup) findViewById(R.id.showingroup);
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        edit_search = (EditText) findViewById(R.id.edit_search);
        btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        btn_go_left = (TextView) findViewById(R.id.btn_go_left);

        btn_cancel.setOnClickListener(this);
        btn_go_left.setOnClickListener(this);
    }

    private void initData() {
    }

    private void initCtrl() {
        adapter = new RecycleAdapterSearchAddress(this, results);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        btn_go_left.setText(city);
        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 0) return;
                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */

                if (showin == null) {
                    showin = LoadingViewUtil.showin(showingroup, com.ins.middle.R.layout.layout_loading, showin);
                }
                //mSuggestionSearch.requestSuggestion((new SuggestionSearchOption()).keyword(s.toString()).city(city));
                mPoiSearch.searchInCity((new PoiCitySearchOption()).city(city).keyword(s.toString()).pageCapacity(15).pageNum(0));
            }
        });
    }

    private void freshCtrl() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_go_left:
                intent.setClass(this, CityActivity.class);
                intent.putExtra("city", city);
                startActivityForResult(intent, RESULT_CITY);
                break;
        }
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
        Position position = adapter.getResults().get(viewHolder.getLayoutPosition());
        EventBus.getDefault().post(position);
        finish();
    }

    private static final int RESULT_CITY = 0xf101;

    //页面返回回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_CITY:
                if (resultCode == RESULT_OK) {
                    String city = data.getStringExtra("city");
                    if (!StrUtils.isEmpty(city)) {
                        this.city = city;
                        btn_go_left.setText(city);
                    }
                }
                break;
        }
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        LoadingViewUtil.showout(showingroup, showin);
        if (res == null || res.getAllSuggestions() == null) {
            adapter.getResults().clear();
            freshCtrl();
            return;
        }
        List<Position> positions = new ArrayList<>();
        for (SuggestionResult.SuggestionInfo suggest : res.getAllSuggestions()) {
            if (suggest.key != null) {
                if (suggest.city.equals(city)) {
                    positions.add(new Position(suggest));
                }
            }
        }
        adapter.getResults().clear();
        adapter.getResults().addAll(positions);
        freshCtrl();
    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        LoadingViewUtil.showout(showingroup, showin);
        showin = null;
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(SearchAddressActivity.this, "未找到结果", Toast.LENGTH_LONG).show();
            adapter.getResults().clear();
            freshCtrl();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            List<Position> positions = new ArrayList<>();
            for (PoiInfo poi : result.getAllPoi()) {
                if (poi.city.equals(city)) {
                    positions.add(new Position(poi));
                }
            }
            adapter.getResults().clear();
            adapter.getResults().addAll(positions);
            freshCtrl();
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }
}