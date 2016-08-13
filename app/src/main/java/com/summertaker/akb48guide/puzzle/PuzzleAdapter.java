package com.summertaker.akb48guide.puzzle;

import android.content.Context;
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
import com.summertaker.akb48guide.data.MemberData;
import com.summertaker.akb48guide.util.Translator;
import com.summertaker.akb48guide.util.Util;

import java.util.ArrayList;

public class PuzzleAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private GroupData mGroupData;
    private ArrayList<MemberData> mDataList = null;

    private Translator mTranslator;

    public PuzzleAdapter(Context context, GroupData groupData, ArrayList<MemberData> dataList) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mGroupData = groupData;
        this.mDataList = dataList;

        mTranslator = new Translator(context);
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
        final MemberData memberData = mDataList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();

            switch (memberData.getGroupId()) {
                case Config.GROUP_ID_AKB48:
                    convertView = mLayoutInflater.inflate(R.layout.puzzle_grid_item_akb48, null);
                    break;
                case Config.GROUP_ID_JKT48:
                    convertView = mLayoutInflater.inflate(R.layout.puzzle_grid_item_jkt48, null);
                    break;
                default:
                    convertView = mLayoutInflater.inflate(R.layout.puzzle_grid_item, null);
                    break;
            }

            holder.loLoading = (RelativeLayout) convertView.findViewById(R.id.loLoading);
            holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pbLoading);
            Util.setProgressBarColor(holder.pbLoading, 0, null);

            holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            //holder.tvCount = (TextView) convertView.findViewById(R.id.tvCount);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // http://cdn.akb48.co.jp/cache/image/?path=%2Fmembers%2Fprofile20150511%2Fteam_B_png%2Fogasawara_mayu.png
        String imageUrl = memberData.getThumbnailUrl();
        //Log.e(mTag, "imageUrl: " + imageUrl);

        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.loLoading.setVisibility(View.GONE);
            //holder.ivPicture.setImageResource(R.drawable.anonymous);
        } else {
            if (memberData.getGroupId().equals(Config.GROUP_ID_JKT48)) {
                holder.ivPicture.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }

            if (mGroupData.getId().equals(Config.GROUP_ID_NGT48)) {
                Picasso.with(mContext).load(imageUrl).resize(200, 250).into(holder.ivPicture, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        holder.loLoading.setVisibility(View.GONE);
                        holder.ivPicture.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        holder.loLoading.setVisibility(View.GONE);
                    }
                });
            } else {
                Picasso.with(mContext).load(imageUrl).into(holder.ivPicture, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        holder.loLoading.setVisibility(View.GONE);
                        holder.ivPicture.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        holder.loLoading.setVisibility(View.GONE);
                    }
                });
            }
        }

        String name = memberData.getName();
        name = mTranslator.translateTeam(memberData.getGroupId(), name);
        holder.tvName.setText(name);

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout loLoading;
        ProgressBar pbLoading;
        ImageView ivPicture;
        TextView tvName;
        TextView tvCount;
    }
}
