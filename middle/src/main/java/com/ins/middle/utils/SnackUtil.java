package com.ins.middle.utils;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidadvance.topsnackbar.TSnackbar;
import com.ins.middle.R;

/**
 * Created by Administrator on 2016/12/15.
 */

public class SnackUtil {

    public static void showSnack(final View showingroup, String msg) {
        showSnack(showingroup, msg, null);
    }

    public static void showSnack(final View showingroup, String msg, final View.OnClickListener listener) {
        String title = "黑马出行：\n";
        SpannableString msp = new SpannableString(title + msg);
        msp.setSpan(new RelativeSizeSpan(1.3f), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //设置TSnackbar
        final TSnackbar snackbar = TSnackbar.make(showingroup, msg, TSnackbar.LENGTH_LONG);
        if (listener != null) {
            snackbar.setDuration(TSnackbar.LENGTH_INDEFINITE);
        } else {
            snackbar.setDuration(TSnackbar.LENGTH_LONG);
        }
        snackbar.setIconLeft(R.drawable.logo_kuaidi, 60);
        snackbar.setIconPadding(30);
        //获取背景View
        View backView = snackbar.getView();
        //获取textView
        TextView textView = (TextView) backView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        //设置背景
        backView.setBackgroundColor(Color.parseColor("#7f000000"));
        //设置文字
        textView.setTextColor(Color.WHITE);
        textView.setMaxLines(3);
        textView.setLineSpacing(1f, 1.3f);
        textView.setText(msp);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v);
                    snackbar.dismiss();
                }
            }
        });
        backView.setPadding(0, 0, 0, 0);
        //显示
        snackbar.show();
    }
}
