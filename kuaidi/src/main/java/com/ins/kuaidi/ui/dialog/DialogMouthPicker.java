package com.ins.kuaidi.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.ins.kuaidi.R;
import com.ins.kuaidi.utils.AppHelper;
import com.sobey.common.utils.StrUtils;
import com.sobey.common.utils.TimeUtil;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author Tom.Cai
 * @Function: 自定义对话框
 * @Date: 2013-10-28
 * @Time: 下午12:37:43
 */
public class DialogMouthPicker extends Dialog implements View.OnClickListener {

    private View root;
    private Date date;

    private Context context;
    private WheelView wheel_day;
    private WheelView wheel_hour;
    private WheelView wheel_mins;
    private ArrayWheelAdapter adapter_hour;
    private ArrayWheelAdapter adapter_mins;
    private View dialog_ok;
    private List<String> dataDays = new ArrayList<>();
    private List<String> dataHours = new ArrayList<>();
    private List<String> dataMins = new ArrayList<>();

    public DialogMouthPicker(Context context) {
        super(context, R.style.PopupDialog);
        this.context = context;
        setLoadingDialog();
    }

    private void setLoadingDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        root = inflater.inflate(R.layout.dialog_mouthpicker, null);// 得到加载view

        initBase();
        initView();
        initData();
        initCtrl();

        Window win = this.getWindow();
        win.setGravity(Gravity.BOTTOM);    //从下方弹出
        this.setCanceledOnTouchOutside(true);
        super.setContentView(root);
    }

    private int color_select;

    private void initBase() {
        color_select = ContextCompat.getColor(context, R.color.sb_text_blank);
    }

    private void initView() {
        wheel_day = (WheelView) root.findViewById(R.id.wheel_day);
        wheel_hour = (WheelView) root.findViewById(R.id.wheel_hour);
        wheel_mins = (WheelView) root.findViewById(R.id.wheel_mins);
        dialog_ok = root.findViewById(R.id.dialog_ok);
    }

    private void initData() {
        dataDays.add("今天");
        dataDays.add("明天");
        dataDays.add("后天");
        dataDays.add("大后天");
        dataHours.addAll(initTodayHoursData());
        dataMins.addAll(initNowMinsData());
    }

    private List<String> initAllHoursData() {
        ArrayList<String> strs = new ArrayList<>();
        for (int i = 0; i <= 23; i++) {
            strs.add(i + "点");
        }
        return strs;
    }

    private List<String> initTodayHoursData() {
        int hour = TimeUtil.getHour(new Date());
        ArrayList<String> strs = new ArrayList<>();
        strs.add("现在");
        for (int i = hour + 1; i <= 23; i++) {
            strs.add(i + "点");
        }
        return strs;
    }

    private List<String> initAllMinsData() {
        ArrayList<String> strs = new ArrayList<>();
        for (int i = 0; i <= 5; i++) {
            strs.add(i * 10 + "分");
        }
        return strs;
    }

    private List<String> initNowMinsData() {
        ArrayList<String> strs = new ArrayList<>();
        strs.add("现在");
        return strs;
    }

    private void initCtrl() {
        wheel_day.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onItemSelected(int position, Object o) {
                if (position == 0) {
                    if (!"现在".equals(dataHours.get(0))) {
                        dataHours.clear();
                        dataHours.addAll(initTodayHoursData());
                        adapter_hour.notifyDataSetChanged();
                        if (!"现在".equals(dataMins.get(0)) && wheel_hour.getCurrentPosition() == 0) {
                            dataMins.clear();
                            dataMins.addAll(initNowMinsData());
                            adapter_mins.notifyDataSetChanged();
                        }
                    }
                } else {
                    if ("现在".equals(dataHours.get(0))) {
                        dataHours.clear();
                        dataHours.addAll(initAllHoursData());
                        adapter_hour.notifyDataSetChanged();
                        if ("现在".equals(dataMins.get(0))) {
                            dataMins.clear();
                            dataMins.addAll(initAllMinsData());
                            adapter_mins.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        wheel_hour.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onItemSelected(int position, Object o) {
                Log.e("liao", "wheel_hour");
                if (position == 0) {
                    if ("现在".equals(dataHours.get(0))) {
                        if (!"现在".equals(dataMins.get(0))) {
                            dataMins.clear();
                            dataMins.addAll(initNowMinsData());
                            adapter_mins.notifyDataSetChanged();
                        }
                    }
                } else {
                    if ("现在".equals(dataMins.get(0))) {
                        dataMins.clear();
                        dataMins.addAll(initAllMinsData());
                        adapter_mins.notifyDataSetChanged();
                    }
                }
            }
        });

//        dialog_cancel.setOnClickListener(this);
        dialog_ok.setOnClickListener(this);

        WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
        style.selectedTextColor = color_select;//Color.parseColor("#0288ce");
        style.holoBorderColor = Color.parseColor("#00cccccc");
        style.textAlpha = 0.5f;
        style.textColor = Color.GRAY;
        style.selectedTextSize = 20;

        wheel_day.setWheelAdapter(new ArrayWheelAdapter(context));
        wheel_day.setSkin(WheelView.Skin.Holo);
        wheel_day.setWheelData(dataDays);
        wheel_day.setStyle(style);
        wheel_day.setWheelSize(5);

        adapter_hour = new ArrayWheelAdapter(context);
        wheel_hour.setWheelAdapter(adapter_hour);
        wheel_hour.setSkin(WheelView.Skin.Holo);
        wheel_hour.setWheelData(dataHours);
        wheel_hour.setStyle(style);
        wheel_hour.setWheelSize(5);

        adapter_mins = new ArrayWheelAdapter(context);
        wheel_mins.setWheelAdapter(adapter_mins);
        wheel_mins.setSkin(WheelView.Skin.Holo);
        wheel_mins.setWheelData(dataMins);
        wheel_mins.setStyle(style);
        wheel_mins.setWheelSize(5);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_cancel:
                dismiss();
                break;
            case R.id.dialog_ok:
                if (onOKlistener != null) {
                    String s1 = dataDays.get(wheel_day.getCurrentPosition());
                    String s2 = dataHours.get(wheel_hour.getCurrentPosition());
                    String s3 = dataMins.get(wheel_mins.getCurrentPosition());
                    if (!"现在".endsWith(s2)) {
                        int day = AppHelper.getdayBystr(s1);
                        s2 = StrUtils.subLastChart(s2, "点");
                        s3 = StrUtils.subLastChart(s3, "分");
                        onOKlistener.onOkClick(day, s2 + ":" + s3);
                    } else {
                        onOKlistener.onOkClick(4, "");
                    }
                }
                dismiss();
                break;
        }
    }

    private OnOkListener onOKlistener;

    public void setOnOKlistener(OnOkListener onOKlistener) {
        this.onOKlistener = onOKlistener;
    }

    public interface OnOkListener {
        void onOkClick(int day, String time);
    }
}
