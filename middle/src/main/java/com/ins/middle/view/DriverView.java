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
import com.sobey.common.utils.StrUtils;

import io.techery.properratingbar.ProperRatingBar;

import static com.ins.middle.R.id.rating_driver_star;

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
    //passenger
    private View lay_passenger;
    private ImageView img_passenger_header;
    private TextView text_passenger_name;
    private TextView text_passenger_typecount;
    private TextView text_passenger_start;
    private TextView text_passenger_end;
    private ProgView prog_passenger;

    private User user;

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
        rating_driver = (RatingBar) root.findViewById(rating_driver_star);

        //passenger
        lay_passenger = root.findViewById(R.id.lay_passenger);
        img_passenger_header = (ImageView) root.findViewById(R.id.img_passenger_header);
        text_passenger_name = (TextView) root.findViewById(R.id.text_passenger_name);
        text_passenger_typecount = (TextView) root.findViewById(R.id.text_passenger_typecount);
        text_passenger_start = (TextView) root.findViewById(R.id.text_passenger_start);
        text_passenger_end = (TextView) root.findViewById(R.id.text_passenger_end);
        prog_passenger = (ProgView) root.findViewById(R.id.progView);

        //可能没有这个功能
        prog_passenger.setVisibility(GONE);
    }

    private void initCtrl() {
        //设置一个空的监听器以屏蔽手势
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    public void setDriver(User user) {
        lay_driver.setVisibility(VISIBLE);
        lay_passenger.setVisibility(GONE);
        this.user = user;
        if (user != null) {
            GlideUtil.loadCircleImg(context, img_driver_header, R.drawable.default_header, AppHelper.getRealImgPath(user.getAvatar()));
            text_driver_name.setText(user.getRealName());
            text_driver_ordercount.setText(user.getOrderCount() + "单");
            Car car = user.getCar();
            if (!StrUtils.isEmpty(car)) {
                text_driver_carinfo.setText(car.getCarBrand() + " " + car.getCarColor() + " " + car.getCarCard());
                //暂时没有评分
                text_driver_star.setText(NumUtil.NumberFormat(user.getEvaFen(), 1));
                rating_driver.setRating(NumUtil.NumberFormatFloat(user.getEvaFen(), 1));
            }
        }
    }

    public void setPassenger(User user, Trip trip) {
        lay_driver.setVisibility(GONE);
        lay_passenger.setVisibility(VISIBLE);
        this.user = user;
        if (user != null) {
            GlideUtil.loadCircleImg(context, img_passenger_header, R.drawable.default_header, AppHelper.getRealImgPath(user.getAvatar()));
            text_passenger_name.setText(user.getNickName());
        }
        if (trip != null) {
            text_passenger_start.setText(trip.getFromAdd());
            text_passenger_end.setText(trip.getToAdd());
            text_passenger_typecount.setText(trip.getOrderType() == 0 ? "包车" : "拼车" + "-" + trip.getPeoples() + "人");
        }
    }
}
