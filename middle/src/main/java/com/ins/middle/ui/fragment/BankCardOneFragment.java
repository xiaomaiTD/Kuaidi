package com.ins.middle.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.ins.middle.R;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.common.AppVali;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.ui.activity.BindBankCardActivity;
import com.ins.middle.ui.dialog.DialogLoading;

import org.greenrobot.eventbus.EventBus;
import org.xutils.http.RequestParams;


/**
 * Created by Administrator on 2016/6/2 0002.
 */
public class BankCardOneFragment extends BaseFragment implements View.OnClickListener {

    private int position;
    private View rootView;
    private ViewGroup showingroup;
    private View showin;

    private View btn_go;
    private BindBankCardActivity activity;

    private EditText edit_bindbankcard_realname;
    private EditText edit_bindbankcard_num;


    public static Fragment newInstance(int position) {
        BankCardOneFragment f = new BankCardOneFragment();
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
        rootView = inflater.inflate(R.layout.fragment_bindbankcardone, container, false);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initBase() {
        activity = (BindBankCardActivity) getActivity();
    }

    private void initView() {
        showingroup = (ViewGroup) getView().findViewById(R.id.showingroup);
        btn_go = getView().findViewById(R.id.btn_go);
        edit_bindbankcard_realname = (EditText) getView().findViewById(R.id.edit_bindbankcard_realname);
        edit_bindbankcard_num = (EditText) getView().findViewById(R.id.edit_bindbankcard_num);
    }

    private void initData() {
    }

    private void initCtrl() {
        edit_bindbankcard_realname.setText(AppData.App.getUser().getRealName());
        btn_go.setOnClickListener(this);
    }

    private void freshCtrl() {
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_go) {
            String bankNum = edit_bindbankcard_num.getText().toString();
            String msg = AppVali.bank(bankNum);
            if (msg != null) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            } else {
                netGetBank(bankNum);
            }
        }
    }

    public void netGetBank(final String bankNum) {
        RequestParams params = new RequestParams(AppData.Url.getBank);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("bankNum", bankNum);
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                CommonEntity com = (CommonEntity) pojo;
                String bankName = com.getBankName();
                EventBus.getDefault().post(AppConstant.makeFlagStr(AppConstant.EVENT_DIALOGLOGON_BANK, bankNum + "|" + bankName));
                activity.next();
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void netStart(int status) {
                btn_go.setEnabled(false);
            }

            @Override
            public void netEnd(int status) {
                btn_go.setEnabled(true);
            }
        });
    }
}
