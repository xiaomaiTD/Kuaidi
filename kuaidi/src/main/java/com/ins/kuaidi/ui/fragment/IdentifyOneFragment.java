package com.ins.kuaidi.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ins.kuaidi.R;
import com.ins.kuaidi.ui.activity.IdentifyActivity;
import com.ins.middle.common.AppData;
import com.ins.middle.entity.User;
import com.ins.middle.ui.fragment.BaseFragment;

/**
 * Created by Administrator on 2016/6/2 0002.
 */
public class IdentifyOneFragment extends BaseFragment implements View.OnClickListener{

    private int position;
    private View rootView;
    private ViewGroup showingroup;
    private View showin;

    private TextView text_identify_status;
    private View item_identify;

    private IdentifyActivity activity;


    public static Fragment newInstance(int position) {
        IdentifyOneFragment f = new IdentifyOneFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.position = getArguments().getInt("position");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_identifyone, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initBase();
        initView();
        initData();
        initCtrl();
    }

    private void initBase() {
        activity = (IdentifyActivity) getActivity();
    }

    private void initView() {
        showingroup = (ViewGroup) getView().findViewById(R.id.showingroup);
        item_identify = getView().findViewById(R.id.item_identify);
        text_identify_status = (TextView) getView().findViewById(R.id.text_identify_status);

        item_identify.setOnClickListener(this);
    }

    private void initData() {
    }

    private void initCtrl() {
        setStatusData();
    }

    private void freshCtrl() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.item_identify:
                activity.next();
                break;
        }
    }

    private void setStatusData(){
        User user = AppData.App.getUser();
        //设置认证状态
        if (user.getStatus()== User.UNAUTHORIZED) {
            text_identify_status.setText("未认证");
            text_identify_status.setTextColor(ContextCompat.getColor(getActivity(), com.ins.middle.R.color.com_text_dark));
            text_identify_status.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getActivity(), R.drawable.icon_setting_safe), null, null, null);
            item_identify.setClickable(true);
        }else if (user.getStatus()==User.CERTIFICATIONING){
            text_identify_status.setText("认证中");
            text_identify_status.setTextColor(ContextCompat.getColor(getActivity(), com.ins.middle.R.color.com_text_dark));
            text_identify_status.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getActivity(), R.drawable.icon_setting_safe), null, null, null);
            item_identify.setClickable(false);
        }else if (user.getStatus()==User.AUTHENTICATED){
            text_identify_status.setText("已认证");
            text_identify_status.setTextColor(ContextCompat.getColor(getActivity(), com.ins.middle.R.color.com_text_blank));
            text_identify_status.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getActivity(), R.drawable.icon_setting_safe), null, null, null);
            item_identify.setClickable(false);
        }
    }
}
