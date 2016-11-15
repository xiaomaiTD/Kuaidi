package com.ins.driver.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ins.driver.R;
import com.ins.middle.view.ProgView;
import com.ins.middle.entity.Trip;
import com.ins.middle.entity.User;
import com.ins.middle.utils.AppHelper;
import com.ins.middle.utils.GlideUtil;
import com.sobey.common.interfaces.OnRecycleItemClickListener;
import com.sobey.common.utils.PhoneUtils;
import com.sobey.common.utils.StrUtils;

import java.util.List;


public class RecycleAdapterProg extends RecyclerView.Adapter<RecycleAdapterProg.Holder> {

    private Context context;
    private List<Trip> results;

    public List<Trip> getResults() {
        return results;
    }

    public RecycleAdapterProg(Context context, List<Trip> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public RecycleAdapterProg.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_prog, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecycleAdapterProg.Holder holder, final int position) {
        final Trip trip = results.get(holder.getLayoutPosition());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) onItemClickListener.onItemClick(holder);
            }
        });
        holder.progView.setOnProgListener(new ProgView.OnProgListener() {
            @Override
            public void onRequestFirstMoney() {
                if (onRecycleProgListener != null)
                    onRecycleProgListener.onRequestFirstMoney(holder.progView, trip);
            }

            @Override
            public void onGetPassenger() {
                if (onRecycleProgListener != null)
                    onRecycleProgListener.onGetPassenger(holder.progView, trip);
            }

            @Override
            public void onArrive() {
                if (onRecycleProgListener != null)
                    onRecycleProgListener.onArrive(holder.progView, trip);
            }
        });

        final User passenger = trip.getPassenger();

        if (passenger != null) {
            GlideUtil.loadCircleImg(context, holder.img_header, R.drawable.default_header, AppHelper.getRealImgPath(passenger.getAvatar()));
            holder.text_name.setText(passenger.getNickName());
            holder.text_start.setText(trip.getFromAdd());
            holder.text_end.setText(trip.getToAdd());
            holder.text_typecount.setText(trip.getOrderType() == 0 ? "包车" : "拼车" + "-" + trip.getPeoples() + "人");
            holder.img_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhoneUtils.call(context, passenger.getMobile());
                }
            });

            switch (trip.getStatus()) {
                case 2001:
                    //订单还未匹配到司机
                    break;
                case 2002:
                    holder.progView.setStep(1);
                    break;
                case 2003:
                    holder.progView.setStep(2);
                    break;
                case 2004:
                    holder.progView.setStep(3);
                    break;
                case 2005:
                    holder.progView.setStep(4);
                    break;
                case 2006:
                    holder.progView.setStep(5);
                    break;
                case 2007:
                    //订单已取消
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private ImageView img_header;
        private TextView text_name;
        private TextView text_typecount;
        private TextView text_start;
        private TextView text_end;
        private ImageView img_call;
        private ProgView progView;

        public Holder(View itemView) {
            super(itemView);
            img_header = (ImageView) itemView.findViewById(R.id.img_passenger_header);
            text_name = (TextView) itemView.findViewById(R.id.text_passenger_name);
            text_typecount = (TextView) itemView.findViewById(R.id.text_passenger_typecount);
            text_start = (TextView) itemView.findViewById(R.id.text_passenger_start);
            text_end = (TextView) itemView.findViewById(R.id.text_passenger_end);
            img_call = (ImageView) itemView.findViewById(R.id.img_passenger_call);
            progView = (ProgView) itemView.findViewById(R.id.progView);
        }
    }

    private OnRecycleProgListener onRecycleProgListener;
    private OnRecycleItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnRecycleItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnRecycleProgListener(OnRecycleProgListener onRecycleProgListener) {
        this.onRecycleProgListener = onRecycleProgListener;
    }

    public interface OnRecycleProgListener {
        void onRequestFirstMoney(ProgView progView, Trip trip);

        void onGetPassenger(ProgView progView, Trip trip);

        void onArrive(ProgView progView, Trip trip);
    }

}
