package com.summertaker.akb48guide.member;

import android.content.Context;
import android.content.res.Resources;
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
import com.summertaker.akb48guide.data.MemberData;
import com.summertaker.akb48guide.util.Util;

import java.util.ArrayList;

public class MemberGridAdapter extends BaseDataAdapter {
    private String mTag;
    private Context mContext;
    private Resources mResources;
    //private String mLocale;
    private LayoutInflater mLayoutInflater;
    private GroupData mGroupData;
    private ArrayList<MemberData> mDataList = null;

    private String mGeneralCaptain;
    private String mCaptain;
    private String mViceCaptain;

    public MemberGridAdapter(Context context, GroupData groupData, ArrayList<MemberData> dataList) {
        this.mTag = "===== " + this.getClass().getSimpleName();
        this.mContext = context;
        this.mResources = mContext.getResources();
        //this.mLocale = Util.getLocaleStrng(context);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mGroupData = groupData;
        this.mDataList = dataList;

        switch (groupData.getId()) {
            case Config.GROUP_ID_SKE48:
                this.mGeneralCaptain = groupData.getName() + " " + mContext.getString(R.string.captain);
                this.mCaptain = context.getResources().getString(R.string.leader);
                this.mViceCaptain = context.getResources().getString(R.string.vice_leader);
                break;
            default:
                this.mCaptain = context.getResources().getString(R.string.captain);
                this.mViceCaptain = context.getResources().getString(R.string.vice_captain);
                break;
        }
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
                    convertView = mLayoutInflater.inflate(R.layout.member_grid_item_akb48, null);
                    break;
                case Config.GROUP_ID_JKT48:
                    convertView = mLayoutInflater.inflate(R.layout.member_grid_item_jkt48, null);
                    break;
                default:
                    convertView = mLayoutInflater.inflate(R.layout.member_grid_item, null);
                    break;
            }

            holder.loLoading = (RelativeLayout) convertView.findViewById(R.id.loLoading);
            holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pbLoading);
            Util.setProgressBarColor(holder.pbLoading, 0, null);

            holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            //holder.tvCount = (TextView) convertView.findViewById(R.id.tvCount);
            holder.tvGeneralManager = (TextView) convertView.findViewById(R.id.tvGeneralManager);
            holder.tvGeneralCaptain = (TextView) convertView.findViewById(R.id.tvGeneralCaptain);
            holder.tvManager = (TextView) convertView.findViewById(R.id.tvManager);
            holder.tvCaptain = (TextView) convertView.findViewById(R.id.tvCaptain);
            holder.tvViceCaptain = (TextView) convertView.findViewById(R.id.tvViceCaptain);
            holder.tvConcurrentPosition = (TextView) convertView.findViewById(R.id.tvConcurrentPosition);

            switch (memberData.getGroupId()) {
                case Config.GROUP_ID_AKB48:
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.akb48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.akb48text));
                    break;
                case Config.GROUP_ID_SKE48:
                    holder.tvName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.ske48background));
                    holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.ske48text));
                    break;
                case Config.GROUP_ID_NMB48:
                    //holder.tvName.setBackgroundResource(R.drawable.bg_nmb48_team);
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

        //MemberData memberData = teamData.getMemberData();
        // http://cdn.akb48.co.jp/cache/image/?path=%2Fmembers%2Fprofile20150511%2Fteam_B_png%2Fogasawara_mayu.png
        String imageUrl = memberData.getThumbnailUrl();
        //Log.e(mTag, "imageUrl: " + imageUrl);

        if (imageUrl == null || imageUrl.isEmpty()) {
            holder.loLoading.setVisibility(View.GONE);
            //holder.ivPicture.setImageResource(R.drawable.anonymous);
        } else {
            switch (memberData.getGroupId()) {
                case Config.GROUP_ID_AKB48:
                case Config.GROUP_ID_JKT48:
                    holder.ivPicture.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    break;
            }

            //final String cacheId = Util.urlToId(imageUrl);
            //final String cacheUri = ImageUtil.getValidCacheUri(cacheId);
            //if (cacheUri != null) {
            //    imageUrl = cacheUri;
            //}

            /*Glide.with(mContext).load(imageUrl).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    holder.loLoading.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    holder.loLoading.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.ivPicture);*/

            /*
            // https://futurestud.io/blog/glide-callbacks-simpletarget-and-viewtarget-for-custom-view-classes
            Glide.with(mContext).load(imageUrl).asBitmap().dontAnimate() //.diskCacheStrategy(DiskCacheStrategy.RESULT)
                    //.override(Config.IMAGE_GRID3_WIDTH, Config.IMAGE_GRID3_HEIGHT)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                            holder.loLoading.setVisibility(View.GONE);
                            holder.ivPicture.setImageBitmap(bitmap);

                            //if (cacheUri == null) {
                            //    ImageUtil.saveBitmapToPng(bitmap, cacheId); // 캐쉬 저장
                            //}
                        }
                    });
            */

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

        String localeName = memberData.getLocaleName();
        if (localeName == null || localeName.isEmpty()) {
            localeName = memberData.getName();
        }
        holder.tvName.setText(localeName);
        holder.tvName.setVisibility(View.VISIBLE);

        /*int count = teamData.getMemberCount();
        String text = String.format(mResources.getString(R.string.s_people), count);
        text = " (" + text + ")";
        holder.tvCount.setText(text);*/

        // 총감독
        if (memberData.isGeneralManager()) {
            holder.tvGeneralManager.setVisibility(View.VISIBLE);
        } else {
            holder.tvGeneralManager.setVisibility(View.GONE);
        }

        // 그룹 캡틴
        if (memberData.isGeneralCaptain()) {
            holder.tvGeneralCaptain.setText(mGeneralCaptain);
            holder.tvGeneralCaptain.setVisibility(View.VISIBLE);
        } else {
            holder.tvGeneralCaptain.setVisibility(View.GONE);
        }

        // 지배인
        if (memberData.isManager()) {
            holder.tvManager.setVisibility(View.VISIBLE);
        } else {
            holder.tvManager.setVisibility(View.GONE);
        }

        // 캡틴,리더
        if (memberData.isCaptain()) {
            holder.tvCaptain.setText(mCaptain);
            holder.tvCaptain.setVisibility(View.VISIBLE);
        } else {
            holder.tvCaptain.setVisibility(View.GONE);
        }

        // 부캡틴,부리더
        if (memberData.isViceCaptain()) {
            holder.tvViceCaptain.setText(mViceCaptain);
            holder.tvViceCaptain.setVisibility(View.VISIBLE);
        } else {
            holder.tvViceCaptain.setVisibility(View.GONE);
        }

        // 겸임
        if (memberData.isConcurrent()) {
            String text = memberData.getConcurrentInfo();
            //text = mResources.getString(R.string.concurrent_position) + ": " + text;
            holder.tvConcurrentPosition.setText(text);
            holder.tvConcurrentPosition.setVisibility(View.VISIBLE);
        } else {
            holder.tvConcurrentPosition.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout loLoading;
        ProgressBar pbLoading;
        ImageView ivPicture;
        TextView tvName;
        TextView tvCount;
        TextView tvGeneralManager;
        TextView tvGeneralCaptain;
        TextView tvManager;
        TextView tvCaptain;
        TextView tvViceCaptain;
        TextView tvConcurrentPosition;
    }
}
