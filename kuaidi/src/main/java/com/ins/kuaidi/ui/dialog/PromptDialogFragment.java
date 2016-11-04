package com.ins.kuaidi.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.ins.kuaidi.R;
import com.ins.kuaidi.ui.fragment.LoginPhoneFragment;
import com.ins.kuaidi.ui.fragment.LoginPswFragment;
import com.ins.kuaidi.ui.fragment.LoginValiFragment;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2016/11/3.
 */

public class PromptDialogFragment extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_loginphone, container);
        return view;
    }
}
