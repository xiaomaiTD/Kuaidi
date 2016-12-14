package com.ins.middle.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ins.middle.R;
import com.ins.middle.entity.MoneyDetail;
import com.ins.middle.entity.TestEntity;
import com.sobey.common.interfaces.OnRecycleItemClickListener;
import com.sobey.common.utils.NumUtil;
import com.sobey.common.utils.TimeUtil;

import java.util.Date;
import java.util.List;


public class RecycleAdapterMoney extends RecyclerView.Adapter<RecycleAdapterMoney.Holder> {

    private Context context;
    private List<MoneyDetail> results;

    public List<MoneyDetail> getResults() {
        return results;
    }

    public RecycleAdapterMoney(Context context, List<MoneyDetail> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public RecycleAdapterMoney.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_money, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecycleAdapterMoney.Holder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onItemClick(holder);
            }
        });
        MoneyDetail moneyDetail = results.get(holder.getLayoutPosition());

        holder.text_moneydetail_name.setText(moneyDetail.getReason());
        holder.text_moneydetail_time.setText(TimeUtil.getTimeFor("yyyy-MM-dd HH:mm", new Date(moneyDetail.getCreateDate())));
        if (moneyDetail.getType() == 0) {
            holder.text_moneydetail_money.setTextColor(ContextCompat.getColor(context, R.color.kd_yellow));
            holder.text_moneydetail_money.setText("+" + NumUtil.num2half(moneyDetail.getMoney()) + "元");
        } else {
            holder.text_moneydetail_money.setTextColor(ContextCompat.getColor(context, R.color.kd_weixin_green));
            holder.text_moneydetail_money.setText("-" + NumUtil.num2half(moneyDetail.getMoney()) + "元");
        }
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private TextView text_moneydetail_name;
        private TextView text_moneydetail_time;
        private TextView text_moneydetail_money;

        public Holder(View itemView) {
            super(itemView);
            text_moneydetail_name = (TextView) itemView.findViewById(R.id.text_moneydetail_name);
            text_moneydetail_time = (TextView) itemView.findViewById(R.id.text_moneydetail_time);
            text_moneydetail_money = (TextView) itemView.findViewById(R.id.text_moneydetail_money);
        }
    }

    private OnRecycleItemClickListener listener;

    public void setOnItemClickListener(OnRecycleItemClickListener listener) {
        this.listener = listener;
    }
}
