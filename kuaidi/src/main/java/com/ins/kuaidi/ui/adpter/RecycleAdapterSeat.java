package com.ins.kuaidi.ui.adpter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ins.kuaidi.R;
import com.ins.kuaidi.entity.Seat;
import com.sobey.common.utils.SpannableStringUtils;

import java.util.List;


public class RecycleAdapterSeat extends RecyclerView.Adapter<RecycleAdapterSeat.Holder> {

    private Context context;
    private List<Seat> results;
    private TextView text_notice;
    private TextView text_count;

    public void setNotice(TextView textView) {
        this.text_notice = textView;
        setNotice(new Seat(4));
    }

    public void setCount(TextView textView) {
        this.text_count = textView;
        setCount(new Seat(4));
    }

    private int selectPosition = 3;

    public List<Seat> getResults() {
        return results;
    }

    public RecycleAdapterSeat(Context context, List<Seat> results) {
        this.context = context;
        this.results = results;
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

    private void setNotice(Seat seat) {
        if (text_notice != null) {
            int carcount = (seat.getCount() - 1) / 4 + 1;
            SpannableString str = SpannableStringUtils.create(context, new String[]{"若要包 ", carcount + "辆", " 车，请选择 ", carcount * 4 + "人"}, new int[]{R.color.com_text_light, R.color.kd_yellow, R.color.com_text_light, R.color.kd_yellow});
            text_notice.setText(str);
        }
    }

    private void setCount(Seat seat) {
        if (text_count != null) {
            int carcount = (seat.getCount() - 1) / 4 + 1;
            boolean isFull = seat.getCount() % 4 == 0 ? true : false;
            String str = carcount + "辆车(" + (isFull ? "包车" : "拼车") + ")";
            text_count.setText(str);
        }
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
