package com.summertaker.akb48guide.blog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.BaseDataAdapter;
import com.summertaker.akb48guide.data.SiteData;
import com.summertaker.akb48guide.util.Util;

import java.util.ArrayList;

public class BlogSettingCheckAdapter extends BaseDataAdapter {
    private String mTag;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<SiteData> mDataList = null;

    private BlogAdapterInterface mCallback;
    //private Translator mTranslator;

    public BlogSettingCheckAdapter(Context context, ArrayList<SiteData> dataList, BlogAdapterInterface blogAdapterInterface) {
        this.mTag = "===== " + this.getClass().getSimpleName();
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDataList = dataList;

        this.mCallback = blogAdapterInterface;
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
        final SiteData siteData = mDataList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.blog_setting_check_item, null);

            holder.loLoading = (RelativeLayout) convertView.findViewById(R.id.loLoading);
            holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pbLoading);
            Util.setProgressBarColor(holder.pbLoading, 0, null);

            holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivMatchMemberPicture);
            holder.cbMember = (CheckBox) convertView.findViewById(R.id.cbMember);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // http://cdn.akb48.co.jp/cache/image/?path=%2Fmembers%2Fprofile20150511%2Fteam_B_png%2Fogasawara_mayu.png
        String imageUrl = siteData.getImageUrl();
        //Log.e(mTag, "imageUrl: " + imageUrl);
        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.loLoading.setVisibility(View.GONE);
            //holder.ivPicture.setImageResource(R.drawable.anonymous);
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

        holder.ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onCheckboxClick(position);
            }
        });

        holder.cbMember.setChecked(siteData.isChecked());
        holder.cbMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onCheckboxClick(position);
            }
        });

        String name = siteData.getName();
        holder.tvName.setText(name);

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout loLoading;
        ProgressBar pbLoading;
        ImageView ivPicture;
        CheckBox cbMember;
        TextView tvName;
    }
}

