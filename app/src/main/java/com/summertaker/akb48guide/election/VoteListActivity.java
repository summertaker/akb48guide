package com.summertaker.akb48guide.election;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.BaseActivity;
import com.summertaker.akb48guide.common.BaseApplication;
import com.summertaker.akb48guide.common.CacheManager;
import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.common.Setting;
import com.summertaker.akb48guide.data.DataManager;
import com.summertaker.akb48guide.data.ElectionData;
import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.data.MemberData;
import com.summertaker.akb48guide.data.VoteData;
import com.summertaker.akb48guide.data.WebData;
import com.summertaker.akb48guide.parser.NamuwikiParser;
import com.summertaker.akb48guide.parser.OriconParser;
import com.summertaker.akb48guide.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VoteListActivity extends BaseActivity {

    boolean mShowOfficialPhoto;

    ElectionData mElectionData;

    ArrayList<MemberData> mMemberDataList;
    ArrayList<VoteData> mVoteDataList;

    LinearLayout mLoLoading;
    ProgressBar mPbLoadingHorizontal;
    TextView mTvLoading;
    ProgressBar mPbLoading;

    CacheManager mCacheManager;

    private ArrayList<String> mUrlList;
    private int mUrlCount = 0;
    private int mUrlTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vote_list_activity);

        mContext = VoteListActivity.this;
        mResources = mContext.getResources();

        Setting setting = new Setting(mContext);
        mShowOfficialPhoto = setting.get(Config.SETTING_DISPLAY_OFFICIAL_PHOTO).equals(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_YES);
        //Log.e(mTag, "mShowOfficialPhoto: " + mShowOfficialPhoto);

        Intent intent = getIntent();

        mElectionData = (ElectionData) intent.getSerializableExtra("electionData");

        String title = mResources.getString(R.string.akb48_nth_single_selected_general_election);
        int singleNumber = mElectionData.getSingleNumber();
        String singleNumberSuffix = Util.getOrdinal(singleNumber);
        title = String.format(title, singleNumber + singleNumberSuffix);
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, title);

        mLoLoading = (LinearLayout) findViewById(R.id.loLoading);

        mPbLoadingHorizontal = (ProgressBar) findViewById(R.id.pbLoadingHorizontal);
        if (mPbLoadingHorizontal != null) {
            mPbLoadingHorizontal.setProgress(0);
        }

        mTvLoading = (TextView) findViewById(R.id.tvLoading);

        mPbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(mPbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

        mMemberDataList = new ArrayList<>();
        mVoteDataList = new ArrayList<>();

        mCacheManager = new CacheManager(mContext);

        loadCache();
        //initData();
    }

    private void loadCache() {
        JSONObject jsonObject = mCacheManager.loadJsonObject(Config.CACHE_ID_VOTES, 0);

        if (jsonObject != null) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                //Log.e(mTag, "jsonArray.length(): " + jsonArray.length());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    int electionCount = object.getInt("electionCount");
                    //Log.e(mTag, "electionCount: " + electionCount);
                    if (electionCount != mElectionData.getCount()) {
                        continue;
                    }

                    VoteData data = new VoteData();
                    data.setElectionCount(electionCount);
                    data.setSingleNumber(object.getInt("singleNumber"));
                    data.setTeam(Util.getString(object, "team"));
                    data.setConcurrentTeam(Util.getString(object, "concurrentTeam"));
                    data.setName(Util.getString(object, "name"));
                    data.setNoSpaceName(Util.getString(object, "noSpaceName"));
                    data.setLocaleName(Util.getString(object, "localeName"));
                    data.setFurigana(Util.getString(object, "furigana"));
                    data.setRank(Util.getString(object, "rank"));
                    data.setVote(Util.getString(object, "vote"));
                    data.setThumbnailUrl(Util.getString(object, "thumbnailUrl"));
                    //Log.e(mTag, "data.getSingleNumber(): " + data.getSingleNumber());

                    mVoteDataList.add(data);
                }
            } catch (JSONException e) {
                Log.e(mTag, "ERROR: " + e.getMessage());
                e.printStackTrace();
            }
            //Log.e(mTag, "mVoteDataList.size(): "  + mVoteDataList.size());
        }

        if (mVoteDataList.size() == 0) {
            initData();
        } else {
            renderData();
        }
    }

    private void initData() {
        mUrlList = new ArrayList<>();

        mUrlList.add("https://namu.wiki/w/AKB48/%EC%A0%84%20%EB%A9%A4%EB%B2%84%20%EC%9D%BC%EB%9E%8C"); // AKB48 전 멤버 일람
        mUrlList.add("https://namu.wiki/w/SKE48/%EC%A0%84%20%EB%A9%A4%EB%B2%84%20%EC%9D%BC%EB%9E%8C"); // SKE48 전 멤버 일람
        mUrlList.add("https://namu.wiki/w/NMB48/%EC%A0%84%20%EB%A9%A4%EB%B2%84%20%EC%9D%BC%EB%9E%8C"); // NMB48 전 멤버 일람
        mUrlList.add("https://namu.wiki/w/HKT48/%EC%A0%84%20%EB%A9%A4%EB%B2%84%20%EC%9D%BC%EB%9E%8C"); // HKT48 전 멤버 일람
        mUrlList.add("https://namu.wiki/w/NGT48/%EC%A0%84%20%EB%A9%A4%EB%B2%84%20%EC%9D%BC%EB%9E%8C"); // NGT48 전 멤버 일람

        // 第8回 (80人)
        mUrlList.add("http://www.oricon.co.jp/special/49055/");
        mUrlList.add("http://www.oricon.co.jp/special/49055/2/");
        mUrlList.add("http://www.oricon.co.jp/special/49055/3/");
        mUrlList.add("http://www.oricon.co.jp/special/49055/4/");
        mUrlList.add("http://www.oricon.co.jp/special/49055/5/");

        // 第7回 (80人)
        mUrlList.add("http://www.oricon.co.jp/special/47965/");
        mUrlList.add("http://www.oricon.co.jp/special/47965/2/");
        mUrlList.add("http://www.oricon.co.jp/special/47965/3/");
        mUrlList.add("http://www.oricon.co.jp/special/47965/4/");
        mUrlList.add("http://www.oricon.co.jp/special/47965/5/");

        //第6回 (80人)
        mUrlList.add("http://www.oricon.co.jp/music/special/2014/akb48_0607/index.html");
        mUrlList.add("http://www.oricon.co.jp/music/special/2014/akb48_0607/index2.html");
        mUrlList.add("http://www.oricon.co.jp/music/special/2014/akb48_0607/index3.html");
        mUrlList.add("http://www.oricon.co.jp/music/special/2014/akb48_0607/index4.html");
        mUrlList.add("http://www.oricon.co.jp/music/special/2014/akb48_0607/index5.html");

        //第5回 (64人)
        mUrlList.add("http://www.oricon.co.jp/music/special/2013/akb48_0608/");
        mUrlList.add("http://www.oricon.co.jp/music/special/2013/akb48_0608/index2.html");
        mUrlList.add("http://www.oricon.co.jp/music/special/2013/akb48_0608/index3.html");
        mUrlList.add("http://www.oricon.co.jp/music/special/2013/akb48_0608/index4.html");

        //第4回 (64人)
        mUrlList.add("http://www.oricon.co.jp/special/544/");

        //第3回 (40人)
        mUrlList.add("http://www.oricon.co.jp/special/532/");

        //第2回 (40人)
        mUrlList.add("http://www.oricon.co.jp/special/509/");

        //第1回 (30人)
        mUrlList.add("http://www.oricon.co.jp/special/506/");

        mUrlTotal = mUrlList.size();

        loadData();
    }

    private void loadData() {
        if (mUrlList.size() > mUrlCount) {
            requestData(mUrlList.get(mUrlCount), Config.USER_AGENT_WEB);
        }
    }

    /**
     * 네트워크 데이터 - 가져오기
     */
    private void requestData(final String url, final String userAgent) {
        //Log.e(mTag, "url: " + url);
        //Log.e(mTag, "userAgent: " + userAgent);

        //final String cacheId = Util.urlToId(url);
        //Log.e(mTag, "cacheId: " + cacheId);
        //String cacheData = null;
        //if (!url.contains("namu.wiki")) {
        //cacheData = mCacheManager.load(cacheId, 0);
        //}

        //if (cacheData == null) {
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(mTag, response);
                //mCacheManager.save(cacheId, response);
                parseData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(mTag, "url: " + url);
                mErrorMessage = Util.getErrorMessage(error);
                //parseData(url, "");
                alertNetworkErrorAndFinish(mErrorMessage);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", userAgent);
                return headers;
            }
        };
        String tag_string_req = "string_req";
        BaseApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
        //} else {
        //    parseData(url, cacheData);
        //}
    }

    private void parseData(String url, String response) {
        if (url.contains("oricon.co.jp")) {
            parseOricon(url, response);
        } else if (url.contains("namu.wiki")) {
            parseMember(url, response);
        }

        mUrlCount++;

        String text = "(" + mUrlCount + "/" + mUrlTotal + ")";
        mTvLoading.setText(text);

        float progress = (float) mUrlCount / (float) mUrlTotal;
        int progressValue = (int) (progress * 100.0);
        //Log.e(mTag, mUrlCount + " / " + mUrlTotal + " = " + progressValue);
        mPbLoadingHorizontal.setProgress(progressValue);

        if (mUrlCount < mUrlTotal) {
            loadData();
        } else {
            saveCache();
        }
    }

    private void parseMember(String url, String response) {
        //Log.e(mTag, "parseMember().url: " + url);

        GroupData groupData = new GroupData();

        if (url.contains("AKB48")) {
            groupData.setId(Config.GROUP_ID_AKB48);
            groupData.setName(getString(R.string.akb48));
        } else if (url.contains("SKE48")) {
            groupData.setId(Config.GROUP_ID_SKE48);
            groupData.setName(getString(R.string.ske48));
        } else if (url.contains("NMB48")) {
            groupData.setId(Config.GROUP_ID_NMB48);
            groupData.setName(getString(R.string.nmb48));
        } else if (url.contains("HKT48")) {
            groupData.setId(Config.GROUP_ID_HKT48);
            groupData.setName(getString(R.string.hkt48));
        } else if (url.contains("NGT48")) {
            groupData.setId(Config.GROUP_ID_NGT48);
            groupData.setName(getString(R.string.ngt48));
        }

        NamuwikiParser namuwikiParser = new NamuwikiParser();
        namuwikiParser.parse48List(response, groupData, mMemberDataList);
        //Log.e(mTag, "mMemberDataList.size(): " + mMemberDataList.size());
    }

    private void parseOricon(String url, String response) {
        //Log.e(mTag, "parseData().url: " + url);

        OriconParser oriconParser = new OriconParser();

        if (url.contains("/special/49055/")) {
            oriconParser.parse7List(response, 8, mVoteDataList); // 8th
        } else if (url.contains("/special/47965/")) {
            oriconParser.parse7List(response, 7, mVoteDataList); // 7th
        } else if (url.contains("/special/2014/")) {
            oriconParser.parse6List(response, 6, mVoteDataList); // 6th
        } else if (url.contains("/special/2013/")) {
            oriconParser.parse6List(response, 5, mVoteDataList); // 5th
        } else if (url.contains("/special/544/")) {
            oriconParser.parse4List(response, 4, mVoteDataList); // 4th
        } else if (url.contains("/special/532/")) {
            oriconParser.parse4List(response, 3, mVoteDataList); // 3rd
        } else if (url.contains("/special/509/")) {
            oriconParser.parse4List(response, 2, mVoteDataList); // 2nd
        } else if (url.contains("/special/506/")) {
            oriconParser.parse4List(response, 1, mVoteDataList); // 1st
        }

        //renderData();

        //YoutubeParser youtubeParser = new YoutubeParser();
        //youtubeParser.parse7List(response, mElectionData, mWebDataList);
        //renderData();
    }

    private void saveCache() {
        ArrayList<VoteData> voteDataList = new ArrayList<>();
        for (VoteData voteData : mVoteDataList) {
            //Log.e(mTag, voteData.getName());
            for (MemberData memberData : mMemberDataList) {
                if (Util.isEqualString(voteData.getNoSpaceName(), memberData.getNoSpaceName())) {
                    //Log.e(mTag, voteData.getName() + " / " + memberData.getNoSpaceName() + " / " + memberData.getNameKo());
                    voteData.setLocaleName(memberData.getNameKo());
                    break;
                }
            }
            if (voteData.getElectionCount() == mElectionData.getCount()) {
                voteDataList.add(voteData);
            }
        }

        Collections.sort(voteDataList);
        Collections.sort(mVoteDataList);

        DataManager dataManager = new DataManager(mContext);
        ArrayList<ElectionData> electionList = dataManager.getElectionList();

        try {
            JSONArray jsonArray = new JSONArray();
            for (VoteData data : mVoteDataList) {

                int sigleNumber = 0;
                for (ElectionData ed : electionList) {
                    if (ed.getCount() == data.getElectionCount()) {
                        sigleNumber = ed.getSingleNumber();
                        break;
                    }
                }

                JSONObject object = new JSONObject();
                object.put("electionCount", data.getElectionCount());
                object.put("singleNumber", sigleNumber);
                object.put("team", data.getTeam());
                object.put("concurrentTeam", data.getConcurrentTeam());
                object.put("name", data.getName());
                object.put("noSpaceName", data.getNoSpaceName());
                object.put("localeName", data.getLocaleName());
                object.put("furigana", data.getFurigana());
                object.put("rank", data.getRank());
                object.put("vote", data.getVote());
                object.put("thumbnailUrl", data.getThumbnailUrl());
                jsonArray.put(object);
            }
            mCacheManager.save(Config.CACHE_ID_VOTES, jsonArray);
        } catch (JSONException e) {
            Log.e(mTag, "ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        mVoteDataList.clear();
        mVoteDataList.addAll(voteDataList);

        renderData();
    }

    private void renderData() {
        mLoLoading.setVisibility(View.GONE);

        //Log.e(mTag, "mMemberDataList.size(): " + mMemberDataList.size());
        //Log.e(mTag, "mVoteDataList.size(): " + mVoteDataList.size());

        //String team = getString(R.string.team);
        //String trainee = getString(R.string.trainee);

        ListView listView = (ListView) findViewById(R.id.listView);
        if (listView != null) {
            listView.setVisibility(View.VISIBLE);

            VoteListAdapter adapter = new VoteListAdapter(this, mElectionData, mVoteDataList, mShowOfficialPhoto);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    VoteData voteData = (VoteData) parent.getItemAtPosition(position);
                    Intent intent = new Intent(mContext, VoteDetailActivity.class);
                    intent.putExtra("electionData", mElectionData);
                    intent.putExtra("voteData", voteData);
                    startActivityForResult(intent, 100);
                    showToolbarProgressBar();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e(mTag, "onActivityResult().resultCode: " + resultCode);
        hideToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
