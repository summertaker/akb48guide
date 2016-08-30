package com.summertaker.akb48guide.birthday;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.BaseDataAdapter;
import com.summertaker.akb48guide.data.BirthMonthData;
import com.summertaker.akb48guide.data.MemberData;
import com.summertaker.akb48guide.util.Util;

import java.util.ArrayList;

public class BirthMonthAdapter extends BaseDataAdapter {

    private Context mContext;
    private Resources mResources;
    private LayoutInflater mLayoutInflater;

    ArrayList<BirthMonthData> mDataList = new ArrayList<>();

    String mMonthSuffix;

    public BirthMonthAdapter(Context context, ArrayList<BirthMonthData> dataList) {
        this.mContext = context;
        this.mResources = mContext.getResources();
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDataList = dataList;

        this.mMonthSuffix = this.mResources.getString(R.string.month);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        BirthMonthData birthMonthData = mDataList.get(position);
        MemberData memberData = birthMonthData.getMemberData();

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.birth_month_item, null);

            holder = new ViewHolder();

            holder.loPicture = (RelativeLayout) view.findViewById(R.id.loMemberPicture);
            holder.loPicture.setVisibility(View.VISIBLE);

            holder.pbPictureLoading = (ProgressBar) view.findViewById(R.id.pbMemberPictureLoading);
            Util.setProgressBarColor(holder.pbPictureLoading, 0, null);
            holder.ivPicture = (ImageView) view.findViewById(R.id.ivMemberPicture);

            holder.tvMonth = (TextView) view.findViewById(R.id.tvMonth);
            holder.tvMonthSuffix = (TextView) view.findViewById(R.id.tvMonthSuffix);
            holder.tvName = (TextView) view.findViewById(R.id.tvName);
            holder.tvCount = (TextView) view.findViewById(R.id.tvCountIcon);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        String memberName = "-";
        String imageUrl = null;

        if (memberData != null) {
            imageUrl = memberData.getThumbnailUrl();
            memberName = memberData.getName();
        }
        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.pbPictureLoading.setVisibility(View.GONE);
            holder.ivPicture.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(R.drawable.anonymous).into(holder.ivPicture);
        } else {
            Picasso.with(mContext).load(imageUrl).into(holder.ivPicture, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    holder.pbPictureLoading.setVisibility(View.GONE);
                    holder.ivPicture.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    holder.pbPictureLoading.setVisibility(View.GONE);
                }
            });
        }

        String month = birthMonthData.getMonth();
        holder.tvMonth.setText(month);
        holder.tvMonthSuffix.setText(mMonthSuffix);

        holder.tvName.setText(memberName);

        int count = birthMonthData.getCount();
        String countText = "( " + String.format(mResources.getString(R.string.s_people), count) + " )";
        holder.tvCount.setText(countText);

        return view;
    }

    static class ViewHolder {
        RelativeLayout loPicture;
        ProgressBar pbPictureLoading;
        ImageView ivPicture;

        TextView tvMonth;
        TextView tvMonthSuffix;
        TextView tvName;
        TextView tvCount;
    }
}

