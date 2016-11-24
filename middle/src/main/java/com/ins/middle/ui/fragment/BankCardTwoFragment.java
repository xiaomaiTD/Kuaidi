package com.ins.middle.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ins.middle.R;
import com.ins.middle.common.AppConstant;
import com.ins.middle.common.AppData;
import com.ins.middle.common.AppVali;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.ui.activity.BindBankCardActivity;
import com.sobey.common.helper.ValiHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.http.RequestParams;

/**
 * Created by Administrator on 2016/6/2 0002.
 */
public class BankCardTwoFragment extends BaseFragment implements View.OnClickListener {

    private ValiHelper valiHelper;

    private int position;
    private View rootView;
    private ViewGroup showingroup;
    private View showin;

    private BindBankCardActivity activity;

    private EditText edit_bindbankcard_phone;
    private EditText edit_bindbankcard_vali;
    private EditText edit_bindbankcard_bankname;
    private TextView btn_go_vali;
    private View btn_go;

    private String bankNum;
    private String bankName;

    public static Fragment newInstance(int position) {
        BankCardTwoFragment f = new BankCardTwoFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        f.setArguments(b);
        return f;
    }

    @Subscribe
    public void onEventMainThread(String flagSpc) {
        if (AppConstant.EVENT_BIND_BANK.equals(AppConstant.getFlag(flagSpc))) {
            String value = AppConstant.getStr(flagSpc);
            String[] split = value.split("\\|");
            bankNum = split[0];
            bankName = split[1];

            edit_bindbankcard_bankname.setText(bankName);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        this.position = getArguments().getInt("position");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bindbankcardtwo, container, false);
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
        activity = (BindBankCardActivity) getActivity();
    }

    private void initView() {
        showingroup = (ViewGroup) getView().findViewById(R.id.showingroup);
        btn_go = getView().findViewById(R.id.btn_go);
        btn_go_vali = (TextView) getView().findViewById(R.id.btn_go_vali);
        edit_bindbankcard_bankname = (EditText) getView().findViewById(R.id.edit_bindbankcard_bankname);
        edit_bindbankcard_phone = (EditText) getView().findViewById(R.id.edit_bindbankcard_phone);
        edit_bindbankcard_vali = (EditText) getView().findViewById(R.id.edit_bindbankcard_vali);
        btn_go_vali.setOnClickListener(this);

        valiHelper = new ValiHelper(btn_go_vali);
    }

    private void initData() {
    }

    private void initCtrl() {
        btn_go.setOnClickListener(this);
    }

    private void freshCtrl() {
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_go) {
            String phone = edit_bindbankcard_phone.getText().toString();
            String vali = edit_bindbankcard_vali.getText().toString();
            String msg = AppVali.addBankCard(phone, valiHelper.phone, vali, valiHelper.valicode, bankNum, bankName);
            if (msg != null) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            } else {
                netAddBankCard(bankNum, bankName, valiHelper.phone);
            }
        } else if (i == R.id.btn_go_vali) {
            String phone = edit_bindbankcard_phone.getText().toString();
            String msg = AppVali.phone(phone);
            if (msg != null) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            } else {
                netGetValicode(phone);
            }
        }
    }

    private void netGetValicode(String phone) {
        valiHelper.phone = phone;
        RequestParams params = new RequestParams(AppData.Url.sendCode);
        params.addBodyParameter("mobile", phone);
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(int code, final Object pojo, String text, Object obj) {
                if (pojo == null) netSetError(code, "接口异常");
                else {
                    Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();

                    //保持验证码和手机号
                    CommonEntity com = (CommonEntity) pojo;
                    valiHelper.valicode = com.getValicode();

                    //开始计时
                    valiHelper.start();
                }
            }

            @Override
            public void netSetError(int code, String text) {
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                btn_go_vali.setEnabled(true);
            }

            @Override
            public void netStart(int status) {
                btn_go_vali.setEnabled(false);
            }
        });
    }

    public void netAddBankCard(final String bankNum, final String bankName, final String phone) {
        RequestParams params = new RequestParams(AppData.Url.addBankCard);
        params.addHeader("token", AppData.App.getToken());
        params.addBodyParameter("bankNum", bankNum);
        params.addBodyParameter("bankName", bankName);
        params.addBodyParameter("mobile", phone);
        CommonNet.samplepost(params, CommonEntity.class, new CommonNet.SampleNetHander() {
            @Override
            public void netGo(final int code, Object pojo, String text, Object obj) {
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                activity.setResult(Activity.RESULT_OK);
                activity.finish();
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
