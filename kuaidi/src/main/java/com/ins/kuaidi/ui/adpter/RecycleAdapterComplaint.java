package com.ins.kuaidi.ui.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ins.kuaidi.R;
import com.ins.kuaidi.entity.Complaint;
import com.ins.kuaidi.entity.TestEntity;
import com.sobey.common.interfaces.OnRecycleItemClickListener;

import java.util.List;


public class RecycleAdapterComplaint extends RecyclerView.Adapter<RecycleAdapterComplaint.Holder> {
    private Context context;
    private List<Complaint> results;

    public List<Complaint> getResults() {
        return results;
    }

    public RecycleAdapterComplaint(Context context, List<Complaint> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public RecycleAdapterComplaint.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_complaint, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecycleAdapterComplaint.Holder holder, final int position) {
        final Complaint complaint = results.get(holder.getLayoutPosition());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onItemClick(holder);
                holder.check.setChecked(!holder.check.isChecked());
            }
        });
        holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                complaint.setCheck(isChecked);
            }
        });

        holder.text_item_complaint.setText(complaint.getCountent());
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        public CheckBox check;
        public TextView text_item_complaint;

        public Holder(View itemView) {
            super(itemView);
            check = (CheckBox) itemView.findViewById(R.id.check_item_complaint);
            text_item_complaint = (TextView) itemView.findViewById(R.id.text_item_complaint);
        }
    }

    private OnRecycleItemClickListener listener;

    public void setOnItemClickListener(OnRecycleItemClickListener listener) {
        this.listener = listener;
    }
}
