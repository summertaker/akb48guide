package com.summertaker.akb48guide.election;

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
import com.summertaker.akb48guide.data.ElectionData;
import com.summertaker.akb48guide.data.VoteData;
import com.summertaker.akb48guide.util.Translator;
import com.summertaker.akb48guide.util.Util;

import java.util.ArrayList;

public class VoteListAdapter extends BaseDataAdapter {
    private String mTag;
    private Context mContext;
    private Resources mResources;
    private Translator mTranslator;
    private boolean mShowOfficialPhoto;

    private int mElectionCount;
    private String mRankSuffix;
    private String mVoteSuffix;
    private LayoutInflater mLayoutInflater;
    private ArrayList<VoteData> mDataList = null;

    public VoteListAdapter(Context context, ElectionData electionData, ArrayList<VoteData> dataList, boolean showOfficialPhoto) {
        this.mTag = "===== " + this.getClass().getSimpleName();
        this.mContext = context;
        this.mResources = context.getResources();
        this.mShowOfficialPhoto = showOfficialPhoto;

        this.mElectionCount = electionData.getCount();
        String locale = Util.getLocaleStrng(context);
        switch (locale) {
            case "KR":
            case "JP":
            case "CN":
                mRankSuffix = mResources.getString(R.string.suffix_rank);
                mVoteSuffix = mResources.getString(R.string.suffix_vote);
                break;
            default:
                break;
        }

        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDataList = dataList;

        this.mTranslator = new Translator(context);
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
        final VoteData voteData = mDataList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();

            //holder.loLoading = (RelativeLayout) convertView.findViewById(R.id.loLoading);
            //holder.pbLoading = (ProgressBar) convertView.findViewById(R.id.pbLoading);
            //Util.setProgressBarColor(holder.pbLoading, 0, null);

            if (mShowOfficialPhoto) {
                convertView = mLayoutInflater.inflate(R.layout.vote_list_item, null);

                holder.loPictureLoading = (RelativeLayout) convertView.findViewById(R.id.loMatchMemberPictureLoading);
                holder.pbPictureLoading = (ProgressBar) convertView.findViewById(R.id.pbMatchMemberPictureLoading);
                Util.setProgressBarColor(holder.pbPictureLoading, 0, null);
                holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivMatchMemberPicture);
            } else {
                convertView = mLayoutInflater.inflate(R.layout.vote_list_item_text, null);
            }

            holder.tvRank = (TextView) convertView.findViewById(R.id.tvRank);
            holder.tvRankSuffix = (TextView) convertView.findViewById(R.id.tvRankSuffix);

            holder.tvSenbatsu = (TextView) convertView.findViewById(R.id.tvSenbatsu);
            holder.tvMediaSenbatsu = (TextView) convertView.findViewById(R.id.tvMediaSenbatsu);
            holder.tvUnderGirls = (TextView) convertView.findViewById(R.id.tvUnderGirls);
            holder.tvNextGirls = (TextView) convertView.findViewById(R.id.tvNextGirls);
            holder.tvFutureGirls = (TextView) convertView.findViewById(R.id.tvFutureGirls);
            holder.tvUpcomingGirls = (TextView) convertView.findViewById(R.id.tvUpcomingGirls);

            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvTeam = (TextView) convertView.findViewById(R.id.tvTeam);
            holder.tvConcurrentTeam = (TextView) convertView.findViewById(R.id.tvConcurrentTeam);
            holder.tvVote = (TextView) convertView.findViewById(R.id.tvVote);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mShowOfficialPhoto) {
            String imageUrl = voteData.getThumbnailUrl();
            //Log.e(mTag, "imageUrl: " + imageUrl);

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

        String rank = voteData.getRank();
        int rankNumber = Integer.parseInt(rank);
        String rankText = rankNumber + "";
        holder.tvRank.setText(rankText);

        String rankSuffix = (mRankSuffix != null) ? mRankSuffix : Util.getOrdinal(rankNumber);
        holder.tvRankSuffix.setText(rankSuffix);

        holder.tvSenbatsu.setVisibility(View.GONE);
        holder.tvMediaSenbatsu.setVisibility(View.GONE);
        holder.tvUnderGirls.setVisibility(View.GONE);
        holder.tvNextGirls.setVisibility(View.GONE);
        holder.tvFutureGirls.setVisibility(View.GONE);
        holder.tvUpcomingGirls.setVisibility(View.GONE);

        switch (mElectionCount) {
            case 1:
            case 2:
            case 3:
                if (rankNumber <= 12) {
                    holder.tvMediaSenbatsu.setVisibility(View.VISIBLE);
                } else if (rankNumber <= 21) {
                    holder.tvSenbatsu.setVisibility(View.VISIBLE);
                } else {
                    holder.tvUnderGirls.setVisibility(View.VISIBLE);
                }
                break;
            default:
                if (rankNumber <= 16) {
                    holder.tvSenbatsu.setVisibility(View.VISIBLE);
                } else if (rankNumber <= 32) {
                    holder.tvUnderGirls.setVisibility(View.VISIBLE);
                } else if (rankNumber <= 48) {
                    holder.tvNextGirls.setVisibility(View.VISIBLE);
                } else if (rankNumber <= 64) {
                    holder.tvFutureGirls.setVisibility(View.VISIBLE);
                } else {
                    holder.tvUpcomingGirls.setVisibility(View.VISIBLE);
                }
                break;
        }

        String name = voteData.getLocaleName();
        if (name == null || name.isEmpty()) {
            name = voteData.getName();
        }
        holder.tvName.setText(name);

        String teamText = voteData.getTeam();
        if (teamText == null || teamText.isEmpty()) {
            holder.tvTeam.setVisibility(View.GONE);
        } else {
            teamText = mTranslator.translateGroupTeam(teamText);
            holder.tvTeam.setVisibility(View.VISIBLE);
            holder.tvTeam.setText(teamText);
        }

        String concurrentTeamText = voteData.getConcurrentTeam();
        if (concurrentTeamText == null || concurrentTeamText.isEmpty()) {
            holder.tvConcurrentTeam.setVisibility(View.GONE);
        } else {
            concurrentTeamText = mTranslator.translateGroupTeam(concurrentTeamText);
            holder.tvConcurrentTeam.setVisibility(View.VISIBLE);
            holder.tvConcurrentTeam.setText(concurrentTeamText);
        }

        String vote = voteData.getVote();
        int voteNumber = Integer.parseInt(vote.replace(",", ""));
        vote = Util.numberFormat(voteNumber) + mVoteSuffix;
        holder.tvVote.setText(vote);

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout loPictureLoading;
        ProgressBar pbPictureLoading;
        ImageView ivPicture;

        TextView tvRank;
        TextView tvRankSuffix;

        TextView tvSenbatsu;
        TextView tvMediaSenbatsu;
        TextView tvUnderGirls;
        TextView tvNextGirls;
        TextView tvFutureGirls;
        TextView tvUpcomingGirls;

        TextView tvName;
        TextView tvTeam;
        TextView tvConcurrentTeam;
        TextView tvVote;
    }
}
