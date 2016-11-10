package com.ins.middle.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ins.middle.R;
import com.ins.middle.entity.TestEntity;
import com.ins.middle.entity.Trip;
import com.ins.middle.utils.AppHelper;
import com.sobey.common.interfaces.OnRecycleItemClickListener;
import com.sobey.common.utils.TimeUtil;

import java.util.Date;
import java.util.List;


public class RecycleAdapterTrip extends RecyclerView.Adapter<RecycleAdapterTrip.Holder> {

    private Context context;
    private List<Trip> results;

    public List<Trip> getResults() {
        return results;
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onItemClick(holder);
            }
        });
        Trip trip = results.get(holder.getLayoutPosition());

        holder.text_trip_time.setText(TimeUtil.getTimeFor("MM月dd日 HH:mm", new Date(trip.getCreateTime())));
        holder.text_trip_start.setText(trip.getFromAdd());
        holder.text_trip_end.setText(trip.getToAdd());
        holder.text_trip_cartype.setText(trip.getOrderType() == 0 ? "包车" : "拼车");
        holder.text_trip_status.setText(AppHelper.getOrderType(trip.getOrderType()));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private TextView text_trip_time;
        private TextView text_trip_cartype;
        private TextView text_trip_status;
        private TextView text_trip_start;
        private TextView text_trip_end;

        public Holder(View itemView) {
            super(itemView);
            text_trip_time = (TextView) itemView.findViewById(R.id.text_trip_time);
            text_trip_cartype = (TextView) itemView.findViewById(R.id.text_trip_cartype);
            text_trip_status = (TextView) itemView.findViewById(R.id.text_trip_status);
            text_trip_start = (TextView) itemView.findViewById(R.id.text_trip_start);
            text_trip_end = (TextView) itemView.findViewById(R.id.text_trip_end);
        }
    }

    private OnRecycleItemClickListener listener;

    public void setOnItemClickListener(OnRecycleItemClickListener listener) {
        this.listener = listener;
    }
}
