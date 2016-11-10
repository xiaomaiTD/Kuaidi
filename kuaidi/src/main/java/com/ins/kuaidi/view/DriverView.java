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
import com.ins.kuaidi.ui.adapter.RecycleAdapterSeat;
import com.ins.kuaidi.utils.AppHelper;
import com.ins.middle.entity.Car;
import com.ins.middle.entity.Position;
import com.ins.middle.entity.User;
import com.ins.middle.utils.GlideUtil;
import com.sobey.common.utils.StrUtils;

import io.techery.properratingbar.ProperRatingBar;

/**
 * Created by Administrator on 2016/11/1.
 */

public class DriverView extends FrameLayout {

    private ViewGroup root;
    private Context context;
    private LayoutInflater inflater;

    private ImageView img_driver_header;
    private TextView text_driver_name;
    private TextView text_driver_carinfo;
    private TextView text_driver_star;
    private TextView text_driver_ordercount;
    private ProperRatingBar rating_driver_star;

    private User driver;

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
        root = (ViewGroup) inflater.inflate(R.layout.layout_home_driver, this, true);
        initBase();
        initView();
        initCtrl();
    }

    private void initBase() {
    }

    private void initView() {
        img_driver_header = (ImageView) root.findViewById(R.id.img_driver_header);
        text_driver_name = (TextView) root.findViewById(R.id.text_driver_name);
        text_driver_carinfo = (TextView) root.findViewById(R.id.text_driver_carinfo);
        text_driver_star = (TextView) root.findViewById(R.id.text_driver_star);
        text_driver_ordercount = (TextView) root.findViewById(R.id.text_driver_ordercount);
        rating_driver_star = (ProperRatingBar) root.findViewById(R.id.rating_driver_star);
    }

    private void initCtrl() {
    }

    public void setDriver(User user) {
        driver = user;
        if (driver != null) {
            GlideUtil.loadCircleImg(context, img_driver_header, R.drawable.default_header, driver.getAvatar());
            text_driver_name.setText(user.getRealName());
            text_driver_ordercount.setText(driver.getOrderCount() + "单");
            Car car = driver.getCar();
            if (!StrUtils.isEmpty(car)) {
                text_driver_carinfo.setText(car.getCarBrand() + " " + car.getCarColor() + " " + car.getCarCard());
                //暂时没有评分
//                text_driver_star.setText("");
//                rating_driver_star.setRating(1);
            }
        }
    }
}
