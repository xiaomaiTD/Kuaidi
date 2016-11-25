package com.ins.kuaidi.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ins.kuaidi.R;
import com.ins.kuaidi.entity.Coupon;
import com.ins.middle.entity.TestEntity;
import com.sobey.common.interfaces.OnRecycleItemClickListener;
import com.sobey.common.utils.NumUtil;
import com.sobey.common.utils.TimeUtil;

import java.util.Date;
import java.util.List;


public class RecycleAdapterCoupon extends RecyclerView.Adapter<RecycleAdapterCoupon.Holder> {

    private Context context;
    private List<Coupon> results;

    public List<Coupon> getResults() {
        return results;
    }

    public RecycleAdapterCoupon(Context context, List<Coupon> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public RecycleAdapterCoupon.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_coupon, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecycleAdapterCoupon.Holder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onItemClick(holder);
            }
        });

        //字体加粗
        holder.text_coupon_name.getPaint().setFakeBoldText(true);
        holder.text_coupon_money.getPaint().setFakeBoldText(true);
        holder.text_coupon_mark.getPaint().setFakeBoldText(true);

        Coupon coupon = results.get(holder.getLayoutPosition());
        holder.text_coupon_money.setText(NumUtil.NumberFormat(coupon.getMoney(),0) + "");
        holder.text_coupon_count.setText(coupon.getNumCoupon() + " 张");
        holder.text_coupon_detail.setText("每次行程仅限1张 有效日期至" + TimeUtil.getTimeFor("yyyy-MM-dd", new Date(coupon.getDueDate())));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private TextView text_coupon_money;
        private TextView text_coupon_mark;
        private TextView text_coupon_name;
        private TextView text_coupon_count;
        private TextView text_coupon_detail;

        public Holder(View itemView) {
            super(itemView);
            text_coupon_money = (TextView) itemView.findViewById(R.id.text_coupon_money);
            text_coupon_mark = (TextView) itemView.findViewById(R.id.text_coupon_mark);
            text_coupon_name = (TextView) itemView.findViewById(R.id.text_coupon_name);
            text_coupon_count = (TextView) itemView.findViewById(R.id.text_coupon_count);
            text_coupon_detail = (TextView) itemView.findViewById(R.id.text_coupon_detail);
        }
    }

    private OnRecycleItemClickListener listener;

    public void setOnItemClickListener(OnRecycleItemClickListener listener) {
        this.listener = listener;
    }
}
