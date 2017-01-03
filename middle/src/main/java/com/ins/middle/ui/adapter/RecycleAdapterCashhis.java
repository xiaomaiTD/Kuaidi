package com.ins.middle.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ins.middle.R;
import com.ins.middle.entity.CashHis;
import com.ins.middle.entity.Msg;
import com.ins.middle.utils.AppHelper;
import com.ins.middle.utils.GlideUtil;
import com.sobey.common.interfaces.OnRecycleItemClickListener;
import com.sobey.common.utils.FontUtils;
import com.sobey.common.utils.NumUtil;
import com.sobey.common.utils.StrUtils;
import com.sobey.common.utils.TimeUtil;

import java.util.Date;
import java.util.List;


public class RecycleAdapterCashhis extends RecyclerView.Adapter<RecycleAdapterCashhis.Holder> {

    private Context context;
    private List<CashHis> results;

    public List<CashHis> getResults() {
        return results;
    }

    public RecycleAdapterCashhis(Context context, List<CashHis> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public RecycleAdapterCashhis.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_cashhis, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecycleAdapterCashhis.Holder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onItemClick(holder);
            }
        });
        CashHis cashHis = results.get(holder.getLayoutPosition());

        FontUtils.boldText(holder.text_cashhis_money);
        if (!StrUtils.isEmpty(cashHis.getBankName()) && !StrUtils.isEmpty(cashHis.getBankAcc())) {
            String name = cashHis.getBankName() + "（" + cashHis.getBankAcc().substring(cashHis.getBankAcc().length() - 4) + "）";
            holder.text_cashhis_name.setText(name);
        } else {
            holder.text_cashhis_name.setText("未知银行");
        }
        holder.text_cashhis_money.setText(NumUtil.num2half(cashHis.getMoney()));
        holder.text_cashhis_time.setText(TimeUtil.getTimeFor("MM月dd日 HH:mm", new Date(cashHis.getCreateTime())));
        holder.text_cashhis_status.setText(cashHis.getStatusName());
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private TextView text_cashhis_name;
        private TextView text_cashhis_money;
        private TextView text_cashhis_time;
        private TextView text_cashhis_status;

        public Holder(View itemView) {
            super(itemView);
            text_cashhis_name = (TextView) itemView.findViewById(R.id.text_cashhis_name);
            text_cashhis_money = (TextView) itemView.findViewById(R.id.text_cashhis_money);
            text_cashhis_time = (TextView) itemView.findViewById(R.id.text_cashhis_time);
            text_cashhis_status = (TextView) itemView.findViewById(R.id.text_cashhis_status);
        }
    }

    private OnRecycleItemClickListener listener;

    public void setOnItemClickListener(OnRecycleItemClickListener listener) {
        this.listener = listener;
    }
}
