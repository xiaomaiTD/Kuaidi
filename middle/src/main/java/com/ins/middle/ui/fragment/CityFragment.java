package com.ins.middle.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ins.middle.R;
import com.ins.middle.common.AppData;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.City;
import com.ins.middle.ui.adapter.ListAdapterCity;
import com.sobey.common.common.LoadingViewUtil;
import com.sobey.common.ui.fragment.BaseSelectFragment;
import com.sobey.common.utils.StrUtils;

import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/2 0002.
 */
public class CityFragment extends BaseSelectFragment implements View.OnClickListener {

    private TextView text_city_nowcity;
    private String city;

    public static Fragment newInstance(int position, String city) {
        CityFragment f = new CityFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        b.putString("city", city);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.city = getArguments().getString("city");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_city, container, false);
    }

    @Override
    protected void initBaseMe() {

    }

    @Override
    protected void initViewMe() {
        getView().findViewById(R.id.btn_right).setOnClickListener(this);
        text_city_nowcity = (TextView) getView().findViewById(R.id.text_city_nowcity);

        text_city_nowcity.setText("当前城市：" + city);
    }

    @Override
    protected void initCtrlMe() {
        adapter = new ListAdapterCity(getActivity(), SourceDateList);
        sortListView.setAdapter((ListAdapterCity) adapter);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                City city = (City) adapter.getResults().get(pos);
//                EventBus.getDefault().post(AppConstant.makeFlagStr(AppConstant.EVENT_HOME_CITY, city.getCar_title()));
//                getActivity().finish();
                Intent intent = new Intent();
                intent.putExtra("city", city.getCar_title());
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
        text_city_nowcity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                EventBus.getDefault().post(AppConstant.makeFlagStr(AppConstant.EVENT_HOME_CITY, city));
//                getActivity().finish();
                Intent intent = new Intent();
                intent.putExtra("city", city);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
    }

    @Override
    protected void initData() {
        netGetCities();
//        showin = LoadingViewUtil.showin(showingroup, R.layout.layout_loading, showin);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //加载成功
//                ArrayList<CharSort> CharSorts = new ArrayList<>();
//                CharSorts.add(new City("张全蛋", "啥时候还我钱啊", "http://img0.imgtn.bdimg.com/it/u=407111158,1901948900&fm=21&gp=0.jpg"));
//                CharSorts.add(new City("张三", "李四在哪了？", "http://img3.imgtn.bdimg.com/it/u=126707278,3863598239&fm=21&gp=0.jpg"));
//                CharSorts.add(new City("王尼玛", "蛋蛋打麻将去了", "http://img3.imgtn.bdimg.com/it/u=2966925534,3303838549&fm=21&gp=0.jpg"));
//                CharSorts.add(new City("美尼玛", "兔子，约不约", "http://img5.imgtn.bdimg.com/it/u=3417293762,3718020601&fm=21&gp=0.jpg"));
//                CharSorts.add(new City("暴尼玛2", "之所以给你留言，是因为老板还没发工资，导致心情不好", "http://v1.qzone.cc/avatar/201407/23/11/01/53cf257510369254.jpg!200x200.jpg"));
//                SourceDateList.clear();
//                SourceDateList.addAll(CharSorts);
//
//                freshCtrl();
//                LoadingViewUtil.showout(showingroup, showin);
//
//                //加载失败
////                LoadingViewUtil.showin(showingroup,R.layout.layout_lack,showin,new View.OnClickListener(){
////                    @Override
////                    public void onClick(View v) {
////                        initData();
////                    }
////                });
//            }
//        }, 1000);
    }

    private void netGetCities() {
        RequestParams params = new RequestParams(AppData.Url.getcities);
        params.addHeader("token", AppData.App.getToken());
        CommonNet.samplepost(params, new TypeToken<List<City>>() {
        }.getType(), new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, Object pojo, String text, Object obj) {
                if (pojo == null) netSetError(code, "接口异常");
                else {
                    List<City> cities = (ArrayList<City>) pojo;
                    if (!StrUtils.isEmpty(cities)) {
//                        adapter.getResults().clear();
//                        adapter.getResults().addAll(contacts);
                        SourceDateList.clear();
                        SourceDateList.addAll(cities);
                        freshCtrl();

                        LoadingViewUtil.showout(showingroup, showin);
                    } else {
                        showin = LoadingViewUtil.showin(showingroup, R.layout.layout_lack, showin, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initData();
                            }
                        });
                    }
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                showin = LoadingViewUtil.showin(showingroup, R.layout.layout_fail, showin, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initData();
                    }
                });
            }

            @Override
            public void netStart(int status) {
                showin = LoadingViewUtil.showin(showingroup, R.layout.layout_loading, showin);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_right) {
            getActivity().finish();
        }
    }
}
