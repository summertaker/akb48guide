package com.summertaker.akb48guide.youtube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.BaseDataAdapter;
import com.summertaker.akb48guide.data.WebData;
import com.summertaker.akb48guide.data.SiteData;

import java.util.ArrayList;

public class YoutubeListAdapter extends BaseDataAdapter {
    private String mTag;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    //private GroupData mGroupData;
    private ArrayList<WebData> mDataList = null;

    public YoutubeListAdapter(Context context, SiteData siteData, ArrayList<WebData> dataList) {
        this.mTag = "===== " + this.getClass().getSimpleName();
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        //this.mGroupData = groupData;
        this.mDataList = dataList;
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

            convertView = mLayoutInflater.inflate(R.layout.youtube_list_item, null);

            //holder.loLoading = (RelativeLayout) convertView.findViewById(R.id.loLoading);
            //holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pbLoading);
            //Util.setProgressBarColor(holder.pbLoading, 0, null);

            holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivMatchMemberPicture);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.tvInfo = (TextView) convertView.findViewById(R.id.tvInfo);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String imageUrl = webData.getThumbnailUrl();
        //Log.e(mTag, "imageUrl: " + imageUrl);

        //final String cacheId = Util.urlToId(imageUrl);
        //final String cacheUri = ImageUtil.getValidCacheUri(cacheId);
        //if (cacheUri != null) {
        //    imageUrl = cacheUri;
        //}

        Picasso.with(mContext).load(imageUrl).into(holder.ivPicture);

        /*Picasso.with(mContext).load(imageUrl).resize(200, 250).noFade().into(holder.ivPicture, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                holder.loLoading.setVisibility(View.GONE);
                //holder.ivPicture.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
                holder.loLoading.setVisibility(View.GONE);
            }
        });*/

        //Glide.with(mContext).load(imageUrl).into(holder.ivPicture);

        /*Glide.with(mContext).load(url).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        }).into(iv);*/

        /*// https://futurestud.io/blog/glide-callbacks-simpletarget-and-viewtarget-for-custom-view-classes
        Glide.with(mContext).load(imageUrl).asBitmap().dontAnimate() //.diskCacheStrategy(DiskCacheStrategy.RESULT)
                //.override(Config.IMAGE_GRID3_WIDTH, Config.IMAGE_GRID3_HEIGHT)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                        //holder.loLoading.setVisibility(View.GONE);
                        holder.ivPicture.setImageBitmap(bitmap);

                        //if (cacheUri == null) {
                        //    ImageUtil.saveBitmapToPng(bitmap, cacheId); // 캐쉬 저장
                        //}
                    }
                });*/

        String title = webData.getTitle();
        holder.tvTitle.setText(title);

        String info = webData.getDate();
        info = info + " - " + webData.getHit();
        info = info + " - " + webData.getTime();
        holder.tvInfo.setText(info);

        return convertView;
    }

    static class ViewHolder {
        //RelativeLayout loLoading;
        //ProgressBar pbLoading;
        ImageView ivPicture;
        TextView tvTitle;
        TextView tvInfo;
    }
}
