package com.ins.driver.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ins.driver.R;

/**
 * Created by Administrator on 2016/11/1.
 */

public class ProgView extends FrameLayout implements View.OnClickListener {

    private ViewGroup root;
    private Context context;
    private LayoutInflater inflater;

    private TextView text_prog_one;
    private TextView text_prog_two;
    private TextView text_prog_three;
    private View lay_prog_one;
    private View lay_prog_two;
    private View lay_prog_three;

    public ProgView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ProgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public ProgView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        root = (ViewGroup) inflater.inflate(R.layout.progview, this, true);
        initBase();
        initView();
        initCtrl();
    }

    private void initBase() {
    }

    private void initView() {
        text_prog_one = (TextView) root.findViewById(R.id.text_prog_one);
        text_prog_two = (TextView) root.findViewById(R.id.text_prog_two);
        text_prog_three = (TextView) root.findViewById(R.id.text_prog_three);
        lay_prog_one = root.findViewById(R.id.lay_prog_one);
        lay_prog_two = root.findViewById(R.id.lay_prog_two);
        lay_prog_three = root.findViewById(R.id.lay_prog_three);

        lay_prog_one.setOnClickListener(this);
        lay_prog_two.setOnClickListener(this);
        lay_prog_three.setOnClickListener(this);
    }

    private void initCtrl() {
        setStep(1);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.lay_prog_one:
            case R.id.lay_prog_two:
            case R.id.lay_prog_three:
                if (onProgListener!=null){
                    if (step==1){
                        onProgListener.onRequestFirstMoney();
                    }else if (step==4){
                        onProgListener.onGetPassenger();
                    }else if (step==5){
                        onProgListener.onArrive();
                    }
                }
//                step++;
//                if (step > 5) {
//                    step = 1;
//                }
//                setStep(step);
                break;
        }
    }

    int step = 1;

    public void setStep(int step) {
        this.step = step;
        switch (step) {
            case 1:
                //2002
                //司机发送支付定金状态（初始状态）
                setBtnStatus(0, 1, false);
                setBtnStatus(1, 2, false);
                setBtnStatus(2, 2, false);
                break;
            case 2:
                //2003
                //司机等待乘客支付定金状态
                setBtnStatus(0, 0, false);
                setBtnStatus(1, 2, false);
                setBtnStatus(2, 2, false);
                break;
            case 3:
                //2004
                //已支付定金开始接客状态
                setBtnStatus(0, 0, true);
                setBtnStatus(1, 1, false);
                setBtnStatus(2, 2, false);
                break;
            case 4:
                //2005
                //已接客开始送去目的地状态
                setBtnStatus(0, 0, true);
                setBtnStatus(1, 0, true);
                setBtnStatus(2, 1, false);
                break;
            case 5:
                //2006
                //已送达状态（终止状态）
                setBtnStatus(0, 0, true);
                setBtnStatus(1, 0, true);
                setBtnStatus(2, 0, true);
                break;
        }
    }

    private void setBtnStatus(int position, int status, boolean needicon) {
        TextView textView;
        View groupView;
        int color;
        int bk;
        String text;
        if (status == 0) {
            color = R.color.com_text_blank;
            if (position == 0) {
                bk = R.drawable.icon_prog_white_start;
            } else {
                bk = R.drawable.icon_prog_white_end;
            }
        } else if (status == 1) {
            color = R.color.white;
            if (position == 0) {
                bk = R.drawable.icon_prog_yellow_start;
            } else {
                bk = R.drawable.icon_prog_yellow_end;
            }
        } else {
            color = R.color.white;
            if (position == 0) {
                bk = R.drawable.icon_prog_gray_start;
            } else {
                bk = R.drawable.icon_prog_gray_end;
            }
        }

        //设置位置
        if (position == 0) {
            textView = text_prog_one;
            groupView = lay_prog_one;
            text = "支付定金";
        } else if (position == 1) {
            textView = text_prog_two;
            groupView = lay_prog_two;
            text = "接到乘客";
        } else {
            textView = text_prog_three;
            groupView = lay_prog_three;
            text = "已送达";
        }

        //设置过程
        if (groupView != null) {
            groupView.setBackgroundResource(bk);
        }
        if (textView != null) {
            textView.setTextColor(ContextCompat.getColor(context, color));
            textView.setText(text);
            textView.setCompoundDrawablesWithIntrinsicBounds(needicon ? ContextCompat.getDrawable(context, R.drawable.icon_prog_finish) : null, null, null, null);
        }
    }


    private OnProgListener onProgListener;

    public void setOnProgListener(OnProgListener onProgListener) {
        this.onProgListener = onProgListener;
    }

    public interface OnProgListener{
        void onRequestFirstMoney();
        void onGetPassenger();
        void onArrive();
    }
}
