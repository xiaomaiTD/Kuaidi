package com.ins.middle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ins.middle.R;
import com.ins.middle.entity.Car;
import com.ins.middle.entity.Trip;
import com.ins.middle.entity.User;
import com.ins.middle.utils.AppHelper;
import com.ins.middle.utils.GlideUtil;
import com.sobey.common.utils.NumUtil;
import com.sobey.common.utils.PhoneUtils;
import com.sobey.common.utils.StrUtils;

/**
 * Created by Administrator on 2016/11/1.
 */

public class DriverView extends FrameLayout {

    private ViewGroup root;
    private Context context;
    private LayoutInflater inflater;

    //dirver
    private View lay_driver;
    private ImageView img_driver_header;
    private TextView text_driver_name;
    private TextView text_driver_carinfo;
    private TextView text_driver_star;
    private TextView text_driver_ordercount;
    private RatingBar rating_driver;
    private ImageView img_dirver_call;
    private ImageView img_dirver_cancel;
    //passenger
    private View lay_passenger;
    private ImageView img_passenger_header;
    private TextView text_passenger_name;
    private TextView text_passenger_typecount;
    private TextView text_passenger_start;
    private TextView text_passenger_end;
    private View img_passenger_call;
    private View img_passenger_navi;
    private View lay_prog_msg;
    private ProgView prog_passenger;

    private User user;
    private Trip trip;

    public DriverView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public DriverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public DriverView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        root = (ViewGroup) inflater.inflate(R.layout.driverpassengerview, this, true);
        initBase();
        initView();
        initCtrl();
    }

    private void initBase() {
    }

    private void initView() {
        //driver
        lay_driver = root.findViewById(R.id.lay_driver);
        img_driver_header = (ImageView) root.findViewById(R.id.img_driver_header);
        text_driver_name = (TextView) root.findViewById(R.id.text_driver_name);
        text_driver_carinfo = (TextView) root.findViewById(R.id.text_driver_carinfo);
        text_driver_star = (TextView) root.findViewById(R.id.text_driver_star);
        text_driver_ordercount = (TextView) root.findViewById(R.id.text_driver_ordercount);
        rating_driver = (RatingBar) root.findViewById(R.id.rating_driver_star);
        img_dirver_call = (ImageView) root.findViewById(R.id.img_dirver_call);
        img_dirver_cancel = (ImageView) root.findViewById(R.id.img_dirver_cancel);

        //passenger
        lay_passenger = root.findViewById(R.id.lay_passenger);
        img_passenger_header = (ImageView) root.findViewById(R.id.img_passenger_header);
        text_passenger_name = (TextView) root.findViewById(R.id.text_passenger_name);
        text_passenger_typecount = (TextView) root.findViewById(R.id.text_passenger_typecount);
        text_passenger_start = (TextView) root.findViewById(R.id.text_passenger_start);
        text_passenger_end = (TextView) root.findViewById(R.id.text_passenger_end);
        img_passenger_call = root.findViewById(R.id.img_passenger_call);
        img_passenger_navi = root.findViewById(R.id.img_passenger_navi);
        lay_prog_msg = root.findViewById(R.id.lay_prog_msg);
        prog_passenger = (ProgView) root.findViewById(R.id.progView);

        //可能没有这个功能
        prog_passenger.setVisibility(GONE);
        //乘客取消订单按钮默认不可见
        img_dirver_cancel.setVisibility(GONE);
        //乘客打电话给司机钮默认不可见
        img_dirver_call.setVisibility(GONE);
        //消息菜单不可见
        lay_prog_msg.setVisibility(GONE);
    }

    private void initCtrl() {
        //设置一个空的监听器以屏蔽手势
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        img_passenger_call.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    PhoneUtils.call(context, user.getMobile());
                    if (onDriverCallListener != null && trip != null) {
                        onDriverCallListener.onDriverCall(trip.getId());
                    }
                }
            }
        });
        img_dirver_call.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    PhoneUtils.call(context, user.getMobile());
                }
            }
        });
        img_dirver_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCancleClickListener != null) {
                    if (trip != null) onCancleClickListener.onCancleClick(trip.getId());
                }
            }
        });
    }

    public void setDriver(User user, Trip trip) {
        lay_driver.setVisibility(VISIBLE);
        lay_passenger.setVisibility(GONE);
        this.user = user;
        this.trip = trip;
        if (user != null) {
            GlideUtil.loadCircleImg(context, img_driver_header, R.drawable.default_header, AppHelper.getRealImgPath(user.getAvatar()));
            text_driver_name.setText(AppHelper.getDriverNickName(user.getRealName()));
            text_driver_ordercount.setText(user.getOrderCount() + "单");
            Car car = user.getCar();
            if (!StrUtils.isEmpty(car)) {
                text_driver_carinfo.setText(car.getCarBrand() + " " + car.getCarColor() + " " + car.getCarCard());
                //暂时没有评分
                text_driver_star.setText(NumUtil.NumberFormat(user.getEvaFen(), 1));
                rating_driver.setRating(NumUtil.NumberFormatFloat(user.getEvaFen(), 1));
            }
        }
        //检查订单状态设置取消按钮
        if (trip != null) {
            //乘客上车之前的订单都可以取消（2005之前，不包括2005）
            if (trip.getStatus() < Trip.STA_2005) {
                img_dirver_cancel.setVisibility(VISIBLE);
                img_dirver_call.setVisibility(VISIBLE);
            } else {
                img_dirver_cancel.setVisibility(GONE);
                img_dirver_call.setVisibility(GONE);
            }
        } else {
            img_dirver_cancel.setVisibility(GONE);
            img_dirver_call.setVisibility(VISIBLE);
        }
    }

    public void setPassenger(User user, Trip trip) {
        lay_driver.setVisibility(GONE);
        lay_passenger.setVisibility(VISIBLE);
        this.user = user;
        this.trip = trip;
        if (user != null) {
            GlideUtil.loadCircleImg(context, img_passenger_header, R.drawable.default_header, AppHelper.getRealImgPath(user.getAvatar()));
            text_passenger_name.setText(user.getNickName());
        }
        if (trip != null) {
            text_passenger_start.setText(trip.getFromAdd());
            text_passenger_end.setText(trip.getToAdd());
            text_passenger_typecount.setText(trip.getOrderType() == 0 ? "包车" : "拼车" + "-" + trip.getPeoples() + "人");
        }
        //检查订单状态设置取消按钮
        if (trip != null) {
            //乘客上车之前的订单都可以取消（2005之前，不包括2005）
            if (trip.getStatus() < Trip.STA_2005) {
                img_passenger_call.setVisibility(VISIBLE);
            } else {
                img_passenger_call.setVisibility(GONE);
            }
        } else {
            img_passenger_call.setVisibility(GONE);
        }
    }

    public void setOnNaviListenner(OnClickListener onClickListener){
        img_passenger_navi.setOnClickListener(onClickListener);
    }

    public Trip getTrip() {
        return trip;
    }

    public void setOnCancleClickListener(OnCancleClickListener onCancleClickListener) {
        this.onCancleClickListener = onCancleClickListener;
    }

    public void setOnDriverCallListener(OnDriverCallListener onDriverCallListener) {
        this.onDriverCallListener = onDriverCallListener;
    }

    private OnCancleClickListener onCancleClickListener;
    private OnDriverCallListener onDriverCallListener;

    public interface OnCancleClickListener {
        void onCancleClick(int orderId);
    }

    public interface OnDriverCallListener {
        void onDriverCall(int orderId);
    }
}
