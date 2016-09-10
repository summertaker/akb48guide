package com.summertaker.akb48guide;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.summertaker.akb48guide.common.BaseDataAdapter;
import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.data.MenuData;
import com.summertaker.akb48guide.util.Typefaces;
import com.summertaker.akb48guide.util.Util;

import java.util.ArrayList;

public class MainMenuAdapter extends BaseDataAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<MenuData> mDataList;

    Typeface mFont;
    int[] mFaIcons = new int[3];

    public MainMenuAdapter(Context context, ArrayList<MenuData> dataList) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDataList = dataList;

        mFont = Typefaces.get(mContext, "fontawesome-webfont.ttf");
        mFaIcons[0] = R.string.fa_hand_scissors_o;
        mFaIcons[1] = R.string.fa_hand_rock_o;
        mFaIcons[2] = R.string.fa_hand_paper_o;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final MenuData menuData = mDataList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.main_item, null);

            holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);

            holder.loIcon = (RelativeLayout) convertView.findViewById(R.id.loIcon);
            holder.tvBack = (TextView) convertView.findViewById(R.id.tvBack);
            holder.tvBack.setTypeface(mFont);
            holder.tvIcon = (TextView) convertView.findViewById(R.id.tvIcon);
            holder.tvIcon.setTypeface(mFont);

            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (menuData.getFaTextIcon() != 0) {
            holder.ivIcon.setVisibility(View.GONE);
            holder.loIcon.setVisibility(View.VISIBLE);

            holder.tvBack.setText(menuData.getFaBackIcon());
            holder.tvBack.setTextColor(menuData.getFaBackColor());
            holder.tvIcon.setTextColor(menuData.getFaTextColor());
            if (menuData.getId().equals(Config.MAIN_ACTION_JANKEN)) {
                int random = Util.getRandom(0, 2);
                holder.tvIcon.setText(mFaIcons[random]);
            } else {
                holder.tvIcon.setText(menuData.getFaTextIcon());
            }
        } else {
            holder.loIcon.setVisibility(View.GONE);
            holder.ivIcon.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(menuData.getDrawable()).into(holder.ivIcon);
        }
        String title = menuData.getTitle();
        holder.tvTitle.setText(title);

        return convertView;
    }

    static class ViewHolder {
        ImageView ivIcon;
        RelativeLayout loIcon;
        TextView tvBack;
        TextView tvIcon;
        TextView tvTitle;
    }
}
