package com.ins.kuaidi.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ins.kuaidi.R;
import com.ins.kuaidi.entity.LineConfig;
import com.ins.middle.entity.Seat;
import com.sobey.common.utils.NumUtil;
import com.sobey.common.utils.SpannableStringUtils;

import java.util.ArrayList;
import java.util.List;


public class RecycleAdapterSeat extends RecyclerView.Adapter<RecycleAdapterSeat.Holder> {

    private Context context;
    private List<Seat> results;
    private TextView text_price;
    private TextView text_notice;
    private TextView text_count;

    private int maxseat = 4;
    private int selectPosition = 0;
    private LineConfig lineConfig;

    public void setLineConfig(LineConfig lineConfig) {
        this.lineConfig = lineConfig;

        if (lineConfig != null) {
            //设置车容量和默认选中位置
            maxseat = lineConfig.getCarCapacity();
            //需求变更，默认人数为1人
            //selectPosition = maxseat - 1;
            selectPosition = 0;
            results.clear();
            results.addAll(getInitSeats(maxseat));
            notifyDataSetChanged();
            setNotice(new Seat(maxseat));
            setCount(new Seat(maxseat));
            setPrice(new Seat(maxseat));
        } else {
            setPrice(new Seat(maxseat));
        }
    }

    public void setPriceView(TextView textView) {
        this.text_price = textView;
        setPrice(new Seat(maxseat));
    }

    public void setNoticeView(TextView textView) {
        this.text_notice = textView;
        setNotice(new Seat(maxseat));
    }

    public void setCountView(TextView textView) {
        this.text_count = textView;
        setCount(new Seat(maxseat));
    }

    public List<Seat> getResults() {
        return results;
    }

    public RecycleAdapterSeat(Context context) {
        this.context = context;
        this.results = getInitSeats(maxseat);
    }

    @Override
    public RecycleAdapterSeat.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_seat, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecycleAdapterSeat.Holder holder, final int position) {
        final Seat seat = results.get(holder.getLayoutPosition());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPosition != position) {
                    int lastPosition = selectPosition;
                    selectPosition = position;
                    notifyItemChanged(selectPosition);
                    notifyItemChanged(lastPosition);
                    setNotice(seat);
                    setCount(seat);
                    setPrice(seat);
                }
            }
        });

        if (holder.getLayoutPosition() == selectPosition) {
            ((TextView) holder.itemView).setTextColor(ContextCompat.getColor(context, R.color.kd_yellow));
            holder.itemView.setBackgroundResource(R.drawable.shape_rect_corner_none_line_yellow);
        } else {
            ((TextView) holder.itemView).setTextColor(ContextCompat.getColor(context, R.color.com_text_light));
            holder.itemView.setBackgroundResource(R.drawable.shape_rect_corner_none_line_light);
        }
        ((TextView) holder.itemView).setText(seat.getCount() + "人");
    }

    private List<Seat> getInitSeats(int maxseat) {
        ArrayList<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= 4 * maxseat; i++) {
            seats.add(new Seat(i));
        }
        return seats;
    }

    private void setPrice(Seat seat) {
        if (text_price != null) {
            if (lineConfig != null) {
                float price = seat.getCount() * lineConfig.getPayMoney() * lineConfig.getDiscount() / 10;
                text_price.setText(NumUtil.num2half(price) + "元起");
            } else {
                text_price.setText("价格获取中");
            }
        }
    }

    private void setNotice(Seat seat) {
        if (text_notice != null) {
            int carcount = (seat.getCount() - 1) / maxseat + 1;
            SpannableString str = SpannableStringUtils.create(context, new String[]{"若要包 ", carcount + "辆", " 车，请选择 ", carcount * maxseat + "人"}, new int[]{R.color.com_text_light, R.color.kd_yellow, R.color.com_text_light, R.color.kd_yellow});
            text_notice.setText(str);
        }
    }

    private void setCount(Seat seat) {
        if (text_count != null) {
            int carcount = (seat.getCount() - 1) / maxseat + 1;
            boolean isFull = seat.getCount() % maxseat == 0 ? true : false;
            String str = carcount + "辆车(" + (isFull ? "包车" : "拼车") + ")";
            text_count.setText(str);
        }
    }

    public int getSelectCount() {
        return results.get(selectPosition).getCount();
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }
}
