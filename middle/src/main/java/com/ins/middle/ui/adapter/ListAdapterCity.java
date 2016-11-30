package com.ins.middle.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ins.middle.R;
import com.ins.middle.entity.City;
import com.ins.middle.utils.AppHelper;
import com.sobey.common.interfaces.CharSort;
import com.sobey.common.ui.adapter.BaseSelectListAdapter;

import java.util.List;

public class ListAdapterCity extends BaseSelectListAdapter {

    public ListAdapterCity(Context mContext, List<CharSort> results) {
        super(mContext, results);
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final City city = (City) results.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_list_city, null);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.line = (ImageView) view.findViewById(R.id.line);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(city.getSortLetters());
            viewHolder.line.setVisibility(View.GONE);
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
            viewHolder.line.setVisibility(View.VISIBLE);
        }

        if (results.get(position).getCar_title_html() != null) {
            viewHolder.tvTitle.setText(Html.fromHtml(results.get(position).getCar_title_html()));
        } else {
            viewHolder.tvTitle.setText(results.get(position).getCar_title());
        }

        //设置数据


        return view;
    }

    final static class ViewHolder {
        TextView tvLetter;
        TextView tvTitle;
        ImageView line;
    }
}