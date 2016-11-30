package com.ins.middle.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ins.middle.R;
import com.ins.middle.entity.MsgClass;
import com.ins.middle.entity.TestEntity;
import com.ins.middle.utils.GlideUtil;
import com.sobey.common.interfaces.OnRecycleItemClickListener;

import java.util.List;


public class RecycleAdapterMsgclass extends RecyclerView.Adapter<RecycleAdapterMsgclass.Holder> {

    private Context context;
    private List<MsgClass> results;

    public List<MsgClass> getResults() {
        return results;
    }

    public RecycleAdapterMsgclass(Context context, List<MsgClass> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public RecycleAdapterMsgclass.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_msgclass, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecycleAdapterMsgclass.Holder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onItemClick(holder);
            }
        });
        MsgClass msgClass = results.get(holder.getLayoutPosition());

        holder.text_msgclass_title.setText(msgClass.getTitle());
        GlideUtil.LoadCircleImgTest(context, holder.img_msgclass_header);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private ImageView img_msgclass_header;
        private TextView text_msgclass_title;

        public Holder(View itemView) {
            super(itemView);
            img_msgclass_header = (ImageView) itemView.findViewById(R.id.img_msgclass_header);
            text_msgclass_title = (TextView) itemView.findViewById(R.id.text_msgclass_title);
        }
    }

    private OnRecycleItemClickListener listener;

    public void setOnItemClickListener(OnRecycleItemClickListener listener) {
        this.listener = listener;
    }
}
