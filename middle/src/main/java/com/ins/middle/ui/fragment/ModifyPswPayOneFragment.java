package com.ins.middle.ui.fragment;

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
import com.ins.middle.common.AppData;
import com.ins.middle.common.AppVali;
import com.ins.middle.common.CommonNet;
import com.ins.middle.entity.CommonEntity;
import com.ins.middle.ui.activity.ModifyPswPayActivity;
import com.sobey.common.helper.ValiHelper;
import com.sobey.common.utils.KeyBoardUtil;

import org.xutils.http.RequestParams;

/**
 * Created by Administrator on 2016/6/2 0002.
 */
public class ModifyPswPayOneFragment extends BaseFragment implements View.OnClickListener {

    private ValiHelper valiHelper;

    private int position;
    private View rootView;
    private ViewGroup showingroup;
    private View showin;

    private View btn_go;
    private TextView btn_go_vali;
    private EditText edit_modifypswpay_vali;

    private ModifyPswPayActivity activity;


    public static Fragment newInstance(int position) {
        ModifyPswPayOneFragment f = new ModifyPswPayOneFragment();
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
        rootView = inflater.inflate(R.layout.fragment_modifypswpayone, container, false);
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
        activity = (ModifyPswPayActivity) getActivity();
    }

    private void initView() {
        showingroup = (ViewGroup) getView().findViewById(R.id.showingroup);
        btn_go = getView().findViewById(R.id.btn_go);
        btn_go_vali = (TextView) getView().findViewById(R.id.btn_go_vali);
        edit_modifypswpay_vali = (EditText) getView().findViewById(R.id.edit_modifypswpay_vali);

        btn_go.setOnClickListener(this);
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
            String vali = edit_modifypswpay_vali.getText().toString();
            String msg = AppVali.vali(vali, valiHelper.valicode);
            if (msg != null) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            } else {
                KeyBoardUtil.hideKeybord(getActivity());
                activity.next();
            }
        } else if (i == R.id.btn_go_vali) {
            netGetValicode();
        }
    }

    private void netGetValicode() {
        RequestParams params = new RequestParams(AppData.Url.sendCode);
        params.addHeader("token", AppData.App.getToken());
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
}
