package com.summertaker.akb48guide.rawphoto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.BaseDataAdapter;
import com.summertaker.akb48guide.data.WebData;
import com.summertaker.akb48guide.util.ProportionalImageView;

import java.util.ArrayList;

public class RawPhotoListAdapter extends BaseDataAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<WebData> mDataList = null;

    public RawPhotoListAdapter(Context context, ArrayList<WebData> webList) {
        this.mTag = "===== " + this.getClass().getSimpleName();
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDataList = webList;
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

            convertView = mLayoutInflater.inflate(R.layout.raw_photo_list_item, null);

            holder.ivPicture = (ProportionalImageView) convertView.findViewById(R.id.ivMemberPicture);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String imageUrl = webData.getImageUrl();
        //Log.e(mTag, "imageUrl: " + imageUrl);

        if (imageUrl == null || imageUrl.isEmpty()) {
            //holder.pbPictureLoading.setVisibility(View.GONE);
        } else {
            Picasso.with(mContext).load(imageUrl).into(holder.ivPicture, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    //holder.pbPictureLoading.setVisibility(View.GONE);
                    holder.ivPicture.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    //holder.pbPictureLoading.setVisibility(View.GONE);
                    holder.ivPicture.setVisibility(View.GONE);
                }
            });
        }

        holder.tvTitle.setText(webData.getName());

        return convertView;
    }

    static class ViewHolder {
        ProportionalImageView ivPicture;
        TextView tvTitle;
    }
}
