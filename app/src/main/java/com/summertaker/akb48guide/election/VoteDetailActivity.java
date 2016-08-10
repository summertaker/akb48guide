package com.summertaker.akb48guide.election;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.BaseActivity;
import com.summertaker.akb48guide.common.CacheManager;
import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.common.Setting;
import com.summertaker.akb48guide.data.DataManager;
import com.summertaker.akb48guide.data.ElectionData;
import com.summertaker.akb48guide.data.VoteData;
import com.summertaker.akb48guide.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class VoteDetailActivity extends BaseActivity {

    boolean mShowOfficialPhoto;

    ElectionData mElectionData;
    VoteData mVoteData;
    ArrayList<ElectionData> mElectionList;

    ArrayList<VoteData> mVoteDataList;
    int mMaxRanking = 0;
    int mMaxVote = 0;

    private ListView mListView;

    CacheManager mCacheManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vote_detail_activity);

        mContext = VoteDetailActivity.this;
        mResources = mContext.getResources();

        Setting setting = new Setting(mContext);
        mShowOfficialPhoto = setting.get(Config.SETTING_DISPLAY_OFFICIAL_PHOTO).equals(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_YES);
        //Log.e(mTag, "mShowOfficialPhoto: " + mShowOfficialPhoto);

        Intent intent = getIntent();

        mElectionData = (ElectionData) intent.getSerializableExtra("electionData");
        mVoteData = (VoteData) intent.getSerializableExtra("voteData");

        String title = mVoteData.getLocaleName() + " " + mVoteData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, title);

        View listHeader = getLayoutInflater().inflate(R.layout.vote_detail_header, null, false);
        mListView = (ListView) findViewById(R.id.listView);
        if (mListView != null) {
            mListView.addHeaderView(listHeader);
        }

        DataManager dataManager = new DataManager(mContext);
        mElectionList = dataManager.getElectionList();
        Collections.sort(mElectionList);

        loadCache();
    }

    private void loadCache() {
        mCacheManager = new CacheManager(mContext);
        JSONObject jsonObject = mCacheManager.loadJsonObject(Config.CACHE_ID_VOTES, 0);

        mVoteDataList = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            //Log.e(mTag, "jsonArray.length(): " + jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                String noSpaceName = Util.getString(object, "noSpaceName");
                if (!Util.isEqualString(noSpaceName, mVoteData.getNoSpaceName())) {
                    continue;
                }

                String rank = Util.getString(object, "rank");
                int rankNumber = Integer.parseInt(rank);
                if (rankNumber > mMaxRanking) {
                    mMaxRanking = rankNumber;
                }

                String vote = Util.getString(object, "vote");
                vote = vote.replace(",", "");
                int voteNumber = Integer.parseInt(vote);
                if (voteNumber > mMaxVote) {
                    mMaxVote = voteNumber;
                }

                VoteData data = new VoteData();
                data.setElectionCount(object.getInt("electionCount"));
                data.setSingleNumber(object.getInt("singleNumber"));
                data.setTeam(Util.getString(object, "team"));
                data.setConcurrentTeam(Util.getString(object, "concurrentTeam"));
                data.setName(Util.getString(object, "name"));
                data.setNoSpaceName(Util.getString(object, "noSpaceName"));
                data.setLocaleName(Util.getString(object, "localeName"));
                data.setFurigana(Util.getString(object, "furigana"));
                data.setRank(rank);
                data.setVote(vote);
                data.setThumbnailUrl(Util.getString(object, "thumbnailUrl"));

                //Log.e(mTag, data.getNoSpaceName() + " / " + data.getElectionCount() + " / " + data.getRank() + " / " + data.getSingleNumber());

                mVoteDataList.add(data);
            }
            Collections.sort(mVoteDataList, VoteData.compareToElectionCount);
        } catch (JSONException e) {
            Log.e(mTag, "ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        mMaxRanking = mMaxRanking + (mMaxRanking / 5);
        if (mMaxRanking > 80) {
            mMaxRanking = 80;
        }

        mMaxVote = mMaxVote + (mMaxVote / 5);

        VoteDetailAdapter voteDetailAdapter = new VoteDetailAdapter(mContext, mElectionData, mVoteDataList, mShowOfficialPhoto);
        mListView.setAdapter(voteDetailAdapter);

        drawRankingChart();
        drawVoteChart();
    }

    private void drawRankingChart() {
        LineChart rankingChart = (LineChart) mListView.findViewById(R.id.lineChartRanking);
        if (rankingChart == null) {
            return;
        }
        rankingChart.setDescription("");
        rankingChart.setNoDataTextDescription("You need to provide data for the chart.");
        rankingChart.setDragEnabled(false);
        rankingChart.setScaleEnabled(false);
        rankingChart.setPinchZoom(false);
        rankingChart.setHighlightPerTapEnabled(false);
        rankingChart.getAxisRight().setEnabled(false);

        Legend l = rankingChart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = rankingChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setTextSize(13f);
        xAxis.setYOffset(10f);

        YAxis leftAxis = rankingChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMinValue(0f);
        leftAxis.setAxisMaxValue(mMaxRanking);
        leftAxis.setInverted(true);
        leftAxis.setValueFormatter(new rankingYAxisValueFormatter());

        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < mElectionList.size(); i++) {
            xVals.add((i + 1) + "회");
        }

        ArrayList<Entry> yVals = new ArrayList<>();
        for (int i = 0; i < mElectionList.size(); i++) {
            float rank = 0f;
            for (VoteData voteData : mVoteDataList) {
                if (mElectionList.get(i).getCount() == voteData.getElectionCount()) {
                    rank = Float.parseFloat(voteData.getRank());
                    break;
                }
            }
            if (rank > 0f) {
                yVals.add(new Entry(rank, i));
            }
        }

        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        set1.setLineWidth(1f);
        set1.setColor(ContextCompat.getColor(mContext, R.color.salmon));
        set1.setCircleColor(ContextCompat.getColor(mContext, R.color.gray));
        set1.setCircleRadius(4f);
        set1.setDrawCircleHole(true);
        set1.setValueTextSize(11f);
        set1.setValueTextColor(ContextCompat.getColor(mContext, R.color.black));
        //set1.setDrawFilled(true);
        //set1.setFillColor(ContextCompat.getColor(mContext, R.color.light_pink));

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets

        LineData lineData = new LineData(xVals, dataSets);
        lineData.setValueFormatter(new rankingValueFormatter());

        rankingChart.setData(lineData);
        rankingChart.animateX(1000, Easing.EasingOption.EaseInOutQuart);
    }

    private void drawVoteChart() {
        BarChart voteChart = (BarChart) mListView.findViewById(R.id.barChartVote);
        if (voteChart == null) {
            return;
        }

        voteChart.setDrawBarShadow(false);
        voteChart.setDrawValueAboveBar(true);

        voteChart.setDescription("");
        voteChart.setNoDataTextDescription("You need to provide data for the chart.");
        voteChart.setDragEnabled(false);
        voteChart.setScaleEnabled(false);
        voteChart.setPinchZoom(false);
        voteChart.getAxisRight().setEnabled(false);

        Legend l = voteChart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = voteChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setYOffset(10f);

        YAxis leftAxis = voteChart.getAxisLeft();
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
        leftAxis.setAxisMaxValue(mMaxVote);
        leftAxis.setValueFormatter(new voteYAxisValueFormatter());

        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < mElectionList.size(); i++) {
            xVals.add((i + 1) + "회");
        }

        ArrayList<BarEntry> yVals = new ArrayList<>();
        for (int i = 0; i < mElectionList.size(); i++) {
            float vote = 0f;
            for (VoteData voteData : mVoteDataList) {
                if (mElectionList.get(i).getCount() == voteData.getElectionCount()) {
                    vote = Float.parseFloat(voteData.getVote());
                    break;
                }
            }
            if (vote > 0f) {
                yVals.add(new BarEntry(vote, i));
            }
        }

        BarDataSet set1 = new BarDataSet(yVals, "DataSet 1");
        set1.setColor(ContextCompat.getColor(mContext, R.color.light_salmon));
        set1.setBarSpacePercent(80f);
        set1.setHighLightAlpha(0);
        set1.setValueTextSize(10f);
        set1.setValueTextColor(ContextCompat.getColor(mContext, R.color.sea_green));

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets

        BarData lineData = new BarData(xVals, dataSets);
        lineData.setValueFormatter(new voteValueFormatter());

        voteChart.setData(lineData);
        voteChart.animateX(1000, Easing.EasingOption.EaseInOutQuart);
    }

    public class rankingValueFormatter implements ValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            DecimalFormat mFormat = new DecimalFormat("###,###"); // use one decimal
            return mFormat.format(value) + "위";
        }
    }

    // https://github.com/PhilJay/MPAndroidChart/wiki/The-YAxisValueFormatter-interface
    public class rankingYAxisValueFormatter implements YAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            DecimalFormat mFormat = new DecimalFormat("###,###"); // use one decimal
            if (value == 0f) {
                return "순위";
            } else {
                return mFormat.format(value) + "위";
            }
        }
    }

    public class voteValueFormatter implements ValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            DecimalFormat mFormat = new DecimalFormat("#,###,###"); // use one decimal
            return mFormat.format(value); // + "표";
        }
    }

    // https://github.com/PhilJay/MPAndroidChart/wiki/The-YAxisValueFormatter-interface
    public class voteYAxisValueFormatter implements YAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            DecimalFormat mFormat = new DecimalFormat("#,###,###"); // use one decimal
            return mFormat.format(value); // + "표";
        }
    }
}
