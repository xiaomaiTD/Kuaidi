package com.ins.middle.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ins.middle.R;
import com.ins.middle.entity.Trip;
import com.ins.middle.ui.activity.TripActivity;
import com.ins.middle.utils.AppHelper;
import com.sobey.common.interfaces.OnRecycleItemClickListener;
import com.sobey.common.utils.StrUtils;
import com.sobey.common.utils.TimeUtil;

import java.util.Date;
import java.util.List;


public class RecycleAdapterTrip extends RecyclerView.Adapter<RecycleAdapterTrip.Holder> {

    private Context context;
    private List<Trip> results;
    private boolean isTocheck;

    public List<Trip> getResults() {
        return results;
    }

    public void setTocheck(boolean tocheck) {
        isTocheck = tocheck;
        notifyDataSetChanged();
    }

    public boolean isTocheck() {
        return isTocheck;
    }

    public RecycleAdapterTrip(Context context, List<Trip> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public RecycleAdapterTrip.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_trip, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecycleAdapterTrip.Holder holder, final int position) {
        final Trip trip = results.get(holder.getLayoutPosition());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTocheck && AppHelper.isFinishOrder(trip)) {
                    holder.check.setChecked(!holder.check.isChecked());
                } else {
                    if (listener != null) listener.onItemClick(holder);
                }
            }
        });
        holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                trip.setCheck(isChecked);
                ((TripActivity) context).setBtnRight();
            }
        });
        holder.text_trip_time.setText(TimeUtil.getTimeFor("MM月dd日 HH:mm", new Date(trip.getCreateTime())));
        holder.text_trip_start.setText(trip.getFromAdd());
        holder.text_trip_end.setText(trip.getToAdd());
        holder.text_trip_cartype.setText(trip.getOrderType() == 0 ? "包车" : "拼车");
        holder.text_trip_status.setText(AppHelper.getOrderType(trip));
        holder.check.setChecked(trip.isCheck());
        holder.lay_trip_linetitle.setVisibility(trip.isLineFlag() ? View.VISIBLE : View.GONE);

        if (isTocheck && AppHelper.isFinishOrder(trip)) {
            holder.check.setVisibility(View.VISIBLE);
            holder.img_trip_next.setVisibility(View.GONE);
        } else {
            holder.check.setVisibility(View.GONE);
            holder.img_trip_next.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public String getSelectIds() {
        String ret = "";
        for (Trip trip : results) {
            if (trip.isCheck()) {
                ret += trip.getId() + ",";
            }
        }
        ret = StrUtils.subLastChart(ret, ",");
        return ret;
    }

    public class Holder extends RecyclerView.ViewHolder {

        private View lay_trip_linetitle;
        private TextView text_trip_time;
        private TextView text_trip_cartype;
        private TextView text_trip_status;
        private TextView text_trip_start;
        private TextView text_trip_end;
        private ImageView img_trip_next;
        private CheckBox check;

        public Holder(View itemView) {
            super(itemView);
            lay_trip_linetitle = itemView.findViewById(R.id.lay_trip_linetitle);
            text_trip_time = (TextView) itemView.findViewById(R.id.text_trip_time);
            text_trip_cartype = (TextView) itemView.findViewById(R.id.text_trip_cartype);
            text_trip_status = (TextView) itemView.findViewById(R.id.text_trip_status);
            text_trip_start = (TextView) itemView.findViewById(R.id.text_trip_start);
            text_trip_end = (TextView) itemView.findViewById(R.id.text_trip_end);
            img_trip_next = (ImageView) itemView.findViewById(R.id.img_trip_next);
            check = (CheckBox) itemView.findViewById(R.id.check_trip);
        }
    }

    private OnRecycleItemClickListener listener;

    public void setOnItemClickListener(OnRecycleItemClickListener listener) {
        this.listener = listener;
    }
}
