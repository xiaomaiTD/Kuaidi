package com.ins.middle.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ins.middle.R;
import com.ins.middle.common.AppData;
import com.ins.middle.entity.Msg;
import com.ins.middle.entity.TestEntity;
import com.ins.middle.utils.AppHelper;
import com.ins.middle.utils.GlideUtil;
import com.sobey.common.interfaces.OnRecycleItemClickListener;

import java.util.List;


public class RecycleAdapterMsg extends RecyclerView.Adapter<RecycleAdapterMsg.Holder> {

    private Context context;
    private List<Msg> results;

    public List<Msg> getResults() {
        return results;
    }

    public RecycleAdapterMsg(Context context, List<Msg> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public RecycleAdapterMsg.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_msg, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecycleAdapterMsg.Holder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onItemClick(holder);
            }
        });
        Msg msg = results.get(holder.getLayoutPosition());

        GlideUtil.loadImg(context, holder.img_msg_pic, R.drawable.default_real, AppHelper.getRealImgPath(msg.getCover()));
        holder.text_msg_title.setText(msg.getTitle());
        holder.text_msg_content.setText(msg.getRemark());
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private ImageView img_msg_pic;
        private TextView text_msg_title;
        private TextView text_msg_content;

        public Holder(View itemView) {
            super(itemView);
            img_msg_pic = (ImageView) itemView.findViewById(R.id.img_msg_pic);
            text_msg_title = (TextView) itemView.findViewById(R.id.text_msg_title);
            text_msg_content = (TextView) itemView.findViewById(R.id.text_msg_content);
        }
    }

    private OnRecycleItemClickListener listener;

    public void setOnItemClickListener(OnRecycleItemClickListener listener) {
        this.listener = listener;
    }
}
