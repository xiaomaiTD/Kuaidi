package com.ins.middle.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ins.middle.R;
import com.ins.middle.entity.BankCard;
import com.ins.middle.entity.TestEntity;
import com.ins.middle.utils.AppHelper;
import com.ins.middle.utils.GlideUtil;
import com.sobey.common.interfaces.OnRecycleItemClickListener;

import java.util.List;


public class RecycleAdapterBankCard extends RecyclerView.Adapter<RecycleAdapterBankCard.Holder> {

    private Context context;
    private List<BankCard> results;

    public List<BankCard> getResults() {
        return results;
    }

    public RecycleAdapterBankCard(Context context, List<BankCard> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public RecycleAdapterBankCard.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_bankcard, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecycleAdapterBankCard.Holder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onItemClick(holder);
            }
        });
        BankCard card = results.get(holder.getLayoutPosition());

        holder.text_bankcard_bankname.setText(card.getBankName());
        holder.text_bankcard_banknum.setText(AppHelper.getUnSeeBankNum(card.getBankNum()));
//        if (card.getBankName().equals("中国银行")) {
//            holder.card_bankcard.setBackgroundResource(R.drawable.shape_bank_red);
//            GlideUtil.loadImg(context, holder.img_bankcard_logo, R.drawable.default_bk, "http://7xnfyf.com1.z0.glb.clouddn.com/zhongguo.png");
//        } else {
//            holder.card_bankcard.setBackgroundResource(R.drawable.shape_bank_blue);
//            GlideUtil.loadImg(context, holder.img_bankcard_logo, R.drawable.default_bk, "http://7xnfyf.com1.z0.glb.clouddn.com/jianse.png");
//        }
        holder.card_bankcard.setBackgroundResource(card.getBkSrc());
        GlideUtil.loadImg(context, holder.img_bankcard_logo, R.drawable.default_bk, AppHelper.getRealImgPath(card.getUrl()));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private ImageView img_bankcard_logo;
        private TextView text_bankcard_bankname;
        private TextView text_bankcard_banknum;
        private TextView text_bankcard_status;
        private View card_bankcard;

        public Holder(View itemView) {
            super(itemView);
            img_bankcard_logo = (ImageView) itemView.findViewById(R.id.img_bankcard_logo);
            text_bankcard_bankname = (TextView) itemView.findViewById(R.id.text_bankcard_bankname);
            text_bankcard_banknum = (TextView) itemView.findViewById(R.id.text_bankcard_banknum);
            text_bankcard_status = (TextView) itemView.findViewById(R.id.text_bankcard_status);
            card_bankcard = itemView.findViewById(R.id.card_bankcard);
        }
    }

    private OnRecycleItemClickListener listener;

    public void setOnItemClickListener(OnRecycleItemClickListener listener) {
        this.listener = listener;
    }
}
