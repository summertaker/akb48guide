package com.summertaker.akb48guide.rawphoto;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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
import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.data.WebData;
import com.summertaker.akb48guide.util.Util;

import java.util.ArrayList;

public class RawPhotoSelectAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private GroupData mGroupData;
    private ArrayList<WebData> mDataList = null;
    //private Translator mTranslator;

    public RawPhotoSelectAdapter(Context context, GroupData groupData, ArrayList<WebData> dataList) {
        this.mTag = "===== " + this.getClass().getSimpleName();
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mGroupData = groupData;
        this.mDataList = dataList;
        //mTranslator = new Translator(context);
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
        final WebData webData = mDataList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();

            if (mGroupData.getRawPhotoUrl().contains(Config.AKB48_GROUP_SHOP_DOMAIN)) {
                convertView = mLayoutInflater.inflate(R.layout.raw_photo_select_item_akb48shop, null);
            } else {
                switch (mGroupData.getId()) {
                    case Config.GROUP_ID_AKB48:
                        convertView = mLayoutInflater.inflate(R.layout.raw_photo_select_item_akb48, null);
                        break;
                    default:
                        convertView = mLayoutInflater.inflate(R.layout.raw_photo_select_item, null);
                        break;
                }
            }

            holder.loLoading = (RelativeLayout) convertView.findViewById(R.id.loLoading);
            holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pbLoading);
            Util.setProgressBarColor(holder.pbLoading, 0, null);

            holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);

            switch (mGroupData.getId()) {
                case Config.GROUP_ID_AKB48:
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.akb48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.akb48text));
                    break;
                case Config.GROUP_ID_SKE48:
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.ske48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.ske48text));
                    break;
                case Config.GROUP_ID_NMB48:
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.nmb48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.nmb48text));
                    break;
                case Config.GROUP_ID_HKT48:
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.hkt48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.hkt48text));
                    break;
                case Config.GROUP_ID_NGT48:
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.ngt48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.ngt48text));
                    break;
                case Config.GROUP_ID_JKT48:
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.jkt48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.jkt48text));
                    break;
                case Config.GROUP_ID_SNH48:
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.snh48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.snh48text));
                    break;
                case Config.GROUP_ID_BEJ48:
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bej48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.bej48text));
                    break;
                case Config.GROUP_ID_GNZ48:
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gnz48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.gnz48text));
                    break;
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String imageUrl = webData.getImageUrl();
        //Log.e(mTag, webData.getName() + " " + imageUrl);

        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.pbLoading.setVisibility(View.GONE);
            holder.ivPicture.setImageResource(R.drawable.anonymous);
        } else {
            if (imageUrl.contains("*")) {
                String[] array = imageUrl.split("\\*");
                int random = Util.getRandom(0, array.length - 1);
                imageUrl = array[random];
            }
            Picasso.with(mContext).load(imageUrl).into(holder.ivPicture, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    holder.pbLoading.setVisibility(View.GONE);
                    holder.ivPicture.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    holder.pbLoading.setVisibility(View.GONE);
                }
            });
        }

        String name = webData.getName();
        holder.tvName.setText(name);

        if (mGroupData.getRawPhotoUrl().contains(Config.AKB48_GROUP_SHOP_DOMAIN)) {
            holder.tvName.setVisibility(View.GONE);
        } else {
            holder.tvName.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    static class ViewHolder {

        RelativeLayout loLoading;
        ProgressBar pbLoading;

        ImageView ivPicture;
        TextView tvName;
    }
}
