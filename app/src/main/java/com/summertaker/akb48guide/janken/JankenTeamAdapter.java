package com.summertaker.akb48guide.janken;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
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
import com.summertaker.akb48guide.common.Setting;
import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.data.MemberData;
import com.summertaker.akb48guide.data.TeamData;
import com.summertaker.akb48guide.util.Translator;
import com.summertaker.akb48guide.util.Util;

import java.util.ArrayList;

public class JankenTeamAdapter extends BaseDataAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private GroupData mGroupData;
    private ArrayList<TeamData> mDataList = null;

    public JankenTeamAdapter(Context context, GroupData groupData, ArrayList<TeamData> dataList) {
        this.mTag = "===== " + this.getClass().getSimpleName();
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mGroupData = groupData;
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
        final TeamData teamData = mDataList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.janken_team_item, null);

            holder.cvPicture = (CardView) convertView.findViewById(R.id.cvPicture);
            holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pbLoading);
            Util.setProgressBarColor(holder.pbLoading, 0, null);

            holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
            holder.ivLock = (ImageView) convertView.findViewById(R.id.ivLock);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MemberData memberData = teamData.getMemberData();

        if (teamData.isLocked()) {
            holder.cvPicture.setVisibility(View.GONE);
            holder.ivLock.setVisibility(View.VISIBLE);
        } else {
            holder.cvPicture.setVisibility(View.VISIBLE);
            holder.ivLock.setVisibility(View.GONE);

            String imageUrl = memberData.getThumbnailUrl();
            //Log.e(mTag, "imageUrl: " + imageUrl);
            if (imageUrl == null || imageUrl.isEmpty()) {
                holder.pbLoading.setVisibility(View.GONE);
                //holder.ivPicture.setImageResource(R.drawable.anonymous);
            } else {
                //if (memberData.getGroupId().equals(Config.GROUP_ID_JKT48)) {
                //    holder.ivPicture.setScaleType(ImageView.ScaleType.FIT_CENTER);
                //}

                if (mGroupData.getId().equals(Config.GROUP_ID_NGT48)) {
                    Picasso.with(mContext).load(imageUrl).resize(200, 250).into(holder.ivPicture, new com.squareup.picasso.Callback() {
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
                } else {
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
            }
        }

        String name = teamData.getName();
        //name = mTranslator.translateTeam(teamData.getGroupId(), name);
        holder.tvName.setText(name);

        return convertView;
    }

    static class ViewHolder {
        CardView cvPicture;
        ProgressBar pbLoading;
        ImageView ivPicture;
        ImageView ivLock;
        TextView tvName;
    }
}
