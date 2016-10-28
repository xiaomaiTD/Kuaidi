package com.ins.kuaidi.ui.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ins.kuaidi.R;
import com.ins.kuaidi.entity.Position;
import com.sobey.common.interfaces.OnRecycleItemClickListener;

import java.util.List;


public class RecycleAdapterSearchAddress extends RecyclerView.Adapter<RecycleAdapterSearchAddress.Holder> {

    private Context context;
    private List<Position> results;

    public List<Position> getResults() {
        return results;
    }

    public RecycleAdapterSearchAddress(Context context, List<Position> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public RecycleAdapterSearchAddress.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_searchaddress, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecycleAdapterSearchAddress.Holder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onItemClick(holder);
            }
        });
        Position pstion = results.get(holder.getLayoutPosition());

        holder.text_address.setText(pstion.getKey());
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private TextView text_address;

        public Holder(View itemView) {
            super(itemView);
            text_address = (TextView) itemView.findViewById(R.id.text_item_search_address);
        }
    }

    private OnRecycleItemClickListener listener;

    public void setOnItemClickListener(OnRecycleItemClickListener listener) {
        this.listener = listener;
    }
}
