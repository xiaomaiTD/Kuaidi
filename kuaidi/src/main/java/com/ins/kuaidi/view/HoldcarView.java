package com.ins.kuaidi.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ins.kuaidi.R;
import com.ins.kuaidi.entity.LineConfig;
import com.ins.kuaidi.utils.AppHelper;
import com.ins.middle.entity.Position;
import com.ins.kuaidi.ui.adapter.RecycleAdapterSeat;

/**
 * Created by Administrator on 2016/11/1.
 */

public class HoldcarView extends FrameLayout implements View.OnClickListener {

    private ViewGroup root;
    private Context context;
    private LayoutInflater inflater;

    private RecyclerView recyclerView;
//    private List<Seat> results = new ArrayList<>();
    private RecycleAdapterSeat adapter;

    private TextView text_notice;
    private View lay_price;
    private TextView text_price;
    private TextView text_count;
    private ImageView img_hide;
    private ImageView img_show;
    private View lay_tab;
    private View lay_starttime;
    private TextView text_starttime;
    private View lay_remark;
    private TextView text_start;
    private TextView text_end;
    private View lay_seat;

    public HoldcarView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public HoldcarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public HoldcarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        inflater = LayoutInflater.from(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        root = (ViewGroup) inflater.inflate(R.layout.holdcarview, this, true);
        initBase();
        initView();
        initCtrl();
    }

    private void initBase() {
    }

    private void initView() {
        recyclerView = (RecyclerView) root.findViewById(R.id.recycle);
        text_notice = (TextView) root.findViewById(R.id.text_holdcar_notice);
        lay_price = root.findViewById(R.id.lay_holdcar_price);
        text_price = (TextView) root.findViewById(R.id.text_holdcar_price);
        text_count = (TextView) root.findViewById(R.id.text_holdcar_count);
        img_hide = (ImageView) root.findViewById(R.id.img_holdcar_hide);
        img_show = (ImageView) root.findViewById(R.id.img_holdcar_show);
        lay_tab = root.findViewById(R.id.lay_holdcar_tab);
        lay_starttime = root.findViewById(R.id.lay_holdcar_starttime);
        text_starttime = (TextView) root.findViewById(R.id.text_holdcar_starttime);
        lay_remark = root.findViewById(R.id.lay_holdcar_remark);
        text_start = (TextView) root.findViewById(R.id.text_holdcar_start);
        text_end = (TextView) root.findViewById(R.id.text_holdcar_end);
        lay_seat = root.findViewById(R.id.lay_holdcar_seat);

        img_hide.setVisibility(GONE);
        lay_tab.setVisibility(GONE);
        lay_price.setVisibility(GONE);
        lay_seat.setVisibility(GONE);

        img_hide.setOnClickListener(this);
        img_show.setOnClickListener(this);
        lay_starttime.setOnClickListener(this);
        lay_remark.setOnClickListener(this);
        text_start.setOnClickListener(this);
        text_end.setOnClickListener(this);
    }

    private void initCtrl() {
        //初始化座位数据并设置
        adapter = new RecycleAdapterSeat(context);
        adapter.setPriceView(text_price);
        adapter.setNoticeView(text_notice);
        adapter.setCountView(text_count);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        //隐藏和显示事件
    }



    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.img_holdcar_hide:
                img_show.setVisibility(VISIBLE);
                img_hide.setVisibility(GONE);
                lay_tab.setVisibility(GONE);
                break;
            case R.id.img_holdcar_show:
                img_show.setVisibility(GONE);
                img_hide.setVisibility(VISIBLE);
                lay_tab.setVisibility(VISIBLE);
                break;
            case R.id.lay_holdcar_starttime:
                if (onHoldcarListener != null) onHoldcarListener.onSelectTimeClick(v);
                break;
            case R.id.lay_holdcar_remark:
                if (onHoldcarListener != null) onHoldcarListener.onRemarkClick(v);
                break;
            case R.id.text_holdcar_start:
                if (onHoldcarListener != null) onHoldcarListener.onStartClick(v);
                break;
            case R.id.text_holdcar_end:
                if (onHoldcarListener != null) onHoldcarListener.onEndClick(v);
                break;
        }
    }

    private Position startPosition;
    private Position endPosition;
    private int day = 4;    //默认4：现在
    private String time;
    private String msg;
    //////////////设置数据的对外方法

    public void setStartPosition(Position position) {
        if (position != null) {
            text_start.setText(position.getCity() + " " + position.getKey());
            startPosition = position;
        } else {
            text_start.setText("出发地");
            startPosition = null;
        }
    }

    public void setEndPosition(Position position) {
        text_end.setText(position.getCity() + " " + position.getKey());
        endPosition = position;
        lay_price.setVisibility(VISIBLE);
        lay_seat.setVisibility(VISIBLE);
    }

    public void setStartTime(int day, String time) {
        this.day = day;
        this.time = time;
        if (day != 4) {
            text_starttime.setText(AppHelper.getTimeStr(day, time));
        } else {
            text_starttime.setText("现在");
        }
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public void setLineConfig(LineConfig lineConfig){
        adapter.setLineConfig(lineConfig);
    }

    /////////////////////////
    /////////get方法
    /////////////////////////

    public Position getStartPosition() {
        return startPosition;
    }

    public Position getEndPosition() {
        return endPosition;
    }

    public String getMsg() {
        return msg;
    }

    public int getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    public int getSelectCount(){
        return adapter.getSelectCount();
    }

    /////////////////////////接口

    private OnHoldcarListener onHoldcarListener;

    public void setOnHoldcarListener(OnHoldcarListener onHoldcarListener) {
        this.onHoldcarListener = onHoldcarListener;
    }

    public interface OnHoldcarListener {
        void onSelectTimeClick(View v);

        void onRemarkClick(View v);

        void onStartClick(View v);

        void onEndClick(View v);
    }
}
