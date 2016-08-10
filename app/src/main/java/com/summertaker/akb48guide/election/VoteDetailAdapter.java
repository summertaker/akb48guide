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

public class VoteDetailAdapter extends BaseDataAdapter {
    private String mTag;
    private Context mContext;
    private Resources mResources;
    private Translator mTranslator;
    private boolean mShowOfficialPhoto;

    private String mLocale;
    private int mElectionCount;
    private String mElectionTitle;
    private String mRankSuffix;
    private String mVoteSuffix;
    private LayoutInflater mLayoutInflater;
    private ArrayList<VoteData> mDataList = null;

    public VoteDetailAdapter(Context context, ElectionData electionData, ArrayList<VoteData> dataList, boolean showOfficialPhoto) {
        this.mTag = "===== " + this.getClass().getSimpleName();
        this.mContext = context;
        this.mResources = context.getResources();
        this.mLocale = Util.getLocaleStrng(mContext);
        this.mShowOfficialPhoto = showOfficialPhoto;

        this.mElectionCount = electionData.getCount();
        this.mElectionTitle = mResources.getString(R.string.akb48_nth_single_selected_general_election);

        switch (mLocale) {
            case "KR":
            case "JP":
                mRankSuffix = context.getResources().getString(R.string.suffix_rank);
                mVoteSuffix = context.getResources().getString(R.string.suffix_vote);
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

            if (mShowOfficialPhoto) {
                convertView = mLayoutInflater.inflate(R.layout.vote_detail_item, null);

                holder.loPictureLoading = (RelativeLayout) convertView.findViewById(R.id.loPictureLoading);
                holder.pbPictureLoading = (ProgressBar) convertView.findViewById(R.id.pbPictureLoading);
                Util.setProgressBarColor(holder.pbPictureLoading, 0, null);
                holder.ivPicture = (ImageView) convertView.findViewById(R.id.ivPicture);
            } else {
                convertView = mLayoutInflater.inflate(R.layout.vote_detail_item_text, null);
            }

            holder.tvElectionCount = (TextView) convertView.findViewById(R.id.tvElectionCount);
            holder.tvRank = (TextView) convertView.findViewById(R.id.tvRank);
            holder.tvRankSuffix = (TextView) convertView.findViewById(R.id.tvRankSuffix);

            holder.tvSenbatsu = (TextView) convertView.findViewById(R.id.tvSenbatsu);
            holder.tvMediaSenbatsu = (TextView) convertView.findViewById(R.id.tvMediaSenbatsu);
            holder.tvUnderGirls = (TextView) convertView.findViewById(R.id.tvUnderGirls);
            holder.tvNextGirls = (TextView) convertView.findViewById(R.id.tvNextGirls);
            holder.tvFutureGirls = (TextView) convertView.findViewById(R.id.tvFutureGirls);
            holder.tvUpcomingGirls = (TextView) convertView.findViewById(R.id.tvUpcomingGirls);

            holder.tvElectionTitle = (TextView) convertView.findViewById(R.id.tvElectionTitle);
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

        int electionCount = voteData.getElectionCount();
        String electionCountText = electionCount + "";
        switch (mLocale) {
            case "KR":
            case "JP":
                electionCountText = electionCountText + mResources.getString(R.string.suffix_time);
                break;
            default:
                electionCountText = electionCountText + Util.getOrdinal(electionCount);
                break;
        }
        holder.tvElectionCount.setText(electionCountText);

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

        String electionTitle = mElectionTitle;
        int singleNumber = voteData.getSingleNumber();
        String singleNumberSuffix = Util.getOrdinal(singleNumber);
        electionTitle = String.format(electionTitle, singleNumber + singleNumberSuffix);
        holder.tvElectionTitle.setText(electionTitle);

        String name = voteData.getLocaleName();
        //Log.e(mTag, "name: " + name);
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
        //Log.e(mTag, "concurrentTeamText: " + concurrentTeamText);
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

        TextView tvElectionCount;
        TextView tvRank;
        TextView tvRankSuffix;

        RelativeLayout loPictureLoading;
        ProgressBar pbPictureLoading;
        ImageView ivPicture;

        TextView tvSenbatsu;
        TextView tvMediaSenbatsu;
        TextView tvUnderGirls;
        TextView tvNextGirls;
        TextView tvFutureGirls;
        TextView tvUpcomingGirls;

        TextView tvElectionTitle;
        TextView tvName;
        TextView tvTeam;
        TextView tvConcurrentTeam;
        TextView tvVote;
    }
}

