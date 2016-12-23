package com.ins.kuaidi.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ins.kuaidi.R;
import com.ins.middle.entity.TestEntity;
import com.ins.middle.entity.User;
import com.ins.middle.utils.AppHelper;
import com.ins.middle.utils.GlideUtil;
import com.sobey.common.interfaces.OnRecycleItemClickListener;

import java.util.List;


public class RecycleAdapterSale extends RecyclerView.Adapter<RecycleAdapterSale.Holder> {

    private Context context;
    private List<User> results;

    public List<User> getResults() {
        return results;
    }

    public RecycleAdapterSale(Context context, List<User> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public RecycleAdapterSale.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_sale, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecycleAdapterSale.Holder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onItemClick(holder);
            }
        });
        int pos = holder.getLayoutPosition();
        User user = results.get(pos);

        GlideUtil.loadCircleImg(context, holder.img_sale_header, R.drawable.default_header, AppHelper.getRealImgPath(user.getAvatar()));
        holder.text_sale_name.setText(user.getNickName());
        holder.text_sale_money.setText("贡献 " + user.getMoney() + "元");

        if (pos == 0) {
            holder.text_sale_num.setBackgroundResource(R.drawable.icon_sale_1);
            holder.text_sale_num.setText("");
        } else if (pos == 1) {
            holder.text_sale_num.setBackgroundResource(R.drawable.icon_sale_2);
            holder.text_sale_num.setText("");
        } else if (pos == 2) {
            holder.text_sale_num.setBackgroundResource(R.drawable.icon_sale_3);
            holder.text_sale_num.setText("");
        } else {
            holder.text_sale_num.setBackgroundResource(R.drawable.shape_oval_dark);
            holder.text_sale_num.setText(pos + 1 + "");
        }
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private ImageView img_sale_header;
        private TextView text_sale_num;
        private TextView text_sale_name;
        private TextView text_sale_money;

        public Holder(View itemView) {
            super(itemView);
            img_sale_header = (ImageView) itemView.findViewById(R.id.img_sale_header);
            text_sale_num = (TextView) itemView.findViewById(R.id.text_sale_num);
            text_sale_name = (TextView) itemView.findViewById(R.id.text_sale_name);
            text_sale_money = (TextView) itemView.findViewById(R.id.text_sale_money);
        }
    }

    private OnRecycleItemClickListener listener;

    public void setOnItemClickListener(OnRecycleItemClickListener listener) {
        this.listener = listener;
    }
}
