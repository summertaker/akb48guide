package com.summertaker.akb48guide.member;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.BaseActivity;
import com.summertaker.akb48guide.common.BaseApplication;
import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.common.Setting;
import com.summertaker.akb48guide.data.DataManager;
import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.data.MemberData;
import com.summertaker.akb48guide.data.TeamData;
import com.summertaker.akb48guide.parser.BaseParser;
import com.summertaker.akb48guide.parser.Hkt48Parser;
import com.summertaker.akb48guide.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TeamListActivity extends BaseActivity {

    boolean mShowOfficialPhoto;

    String mTitle;
    String mAction;
    GroupData mGroupData;
    ArrayList<MemberData> mGroupMemberList = new ArrayList<>();
    ArrayList<MemberData> mMobileMemberList = new ArrayList<>();
    ArrayList<TeamData> mTeamDataList = new ArrayList<>();

    boolean mIsHkt48DualMode = true;
    String mHkt48MobileUrl = "";

    ProgressBar mPbLoading;
    int mParseCount = 0;

    //CacheManager mCacheManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = TeamListActivity.this;
        mResources = mContext.getResources();

        Setting setting = new Setting(mContext);
        mShowOfficialPhoto = setting.get(Config.SETTING_DISPLAY_OFFICIAL_PHOTO).equals(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_YES);
        //Log.e(mTag, "mShowOfficialPhoto: " + mShowOfficialPhoto);

        int contentView = mShowOfficialPhoto ? R.layout.team_grid_activity : R.layout.team_list_activity;
        setContentView(contentView);

        Intent intent = getIntent();
        mAction = intent.getStringExtra("action");
        mGroupData = (GroupData) intent.getSerializableExtra("groupData");

        mTitle = mGroupData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        mPbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(mPbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

        //mCacheManager = new CacheManager(mSharedPreferences);
        String url = mGroupData.getUrl();
        String userAgent = Config.USER_AGENT_WEB;

        if (mIsHkt48DualMode) {
            // HKT48 웹과 모바일 사이트 모두 사용하는 경우
            switch (mGroupData.getId()) {
                case Config.GROUP_ID_HKT48:
                    // HKT48은 PC용 웹 사이트와 모바일용 웹 사이트 2개를 모두 사용한다.
                    // 모바일용 웹 사이트의 썸네일 이미지들 로딩 속도가 너무 느리다.
                    // 썸네일은 웹용 사용하고, 프로필 정보는 모바일 용을 사용한다.
                    DataManager dataManager = new DataManager(mContext);
                    ArrayList<GroupData> groupDataList = dataManager.getGroupList(mAction);
                    for (GroupData groupData : groupDataList) {
                        if (groupData.getId().equals(Config.GROUP_ID_HKT48)) {
                            mHkt48MobileUrl = groupData.getMobileUrl();
                            requestData(mHkt48MobileUrl, Config.USER_AGENT_MOBILE);
                            break;
                        }
                    }
                    break;
                case Config.GROUP_ID_NGT48:
                    //url = mGroupData.getMobileUrl();
                    //userAgent = Config.USER_AGENT_MOBILE;
                    break;
            }
        } else {
            switch (mGroupData.getId()) {
                //case Config.GROUP_ID_HKT48:
                case Config.GROUP_ID_NGT48:
                    //url = mGroupData.getMobileUrl();
                    //userAgent = Config.USER_AGENT_MOBILE;
                    break;
            }
        }

        requestData(url, userAgent);
    }

    /**
     * 네트워크 데이터 - 가져오기
     */
    private void requestData(final String url, final String userAgent) {
        //Log.e(mTag, "url: " + url);
        //Log.e(mTag, "userAgent: " + userAgent);

        //final String cacheId = Util.urlToId(url);
        //String cacheData = mCacheManager.load(cacheId);

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
                Log.e(mTag, "NETWORK ERROR: " + url);
                mErrorMessage = Util.getErrorMessage(error);
                parseData(url, "");
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

        BaseApplication.getInstance().addToRequestQueue(strReq, "strReq");
        //} else {
        //    parseData(url, cacheData);
        //}
    }

    private void parseData(String url, String response) {
        boolean isMobile = url.equals(mGroupData.getMobileUrl());

        if (mIsHkt48DualMode) {
            // HKT48 웹과 모바일 사이트 모두 사용하는 경우
            if (url.equals(mHkt48MobileUrl)) {
                Hkt48Parser hkt48Parser = new Hkt48Parser();
                hkt48Parser.parseMobileMemberList(response, mGroupData, mMobileMemberList, null);
                //Log.e(mTag, "mMobileMemberList.size() = " + mMobileMemberList.size());
            } else {
                //Log.e(mTag, "isMobile: " + isMobile + " / " + mGroupData.getId());
                BaseParser baseParser = new BaseParser();
                baseParser.parseMemberList(response, mGroupData, mGroupMemberList, mTeamDataList, isMobile);
                //Log.e(mTag, "mGroupMemberList.size() = " + mGroupMemberList.size());
            }

            mParseCount++;
            //Log.e(mTag, "mParseCount: " + mParseCount);

            if (mGroupData.getId().equals(Config.GROUP_ID_HKT48)) {
                if (mParseCount == 2) {
                    for (MemberData groupMember : mGroupMemberList) {
                        //Log.e(mTag, memberData.getName());
                        for (MemberData mobileMember : mMobileMemberList) {
                            if (Util.isEqualString(groupMember.getNoSpaceName(), mobileMember.getNoSpaceName())) {
                                groupMember.setProfileUrl(mobileMember.getProfileUrl());
                                break;
                            }
                        }
                    }
                    //Log.e(mTag, "go renderData()...");
                    renderData();
                }
            } else {
                renderData();
            }
        } else {
            BaseParser baseParser = new BaseParser();
            baseParser.parseMemberList(response, mGroupData, mGroupMemberList, mTeamDataList, isMobile);
            renderData();
        }
    }

    private void renderData() {
        mPbLoading.setVisibility(View.GONE);

        //String team = getString(R.string.team);
        //String trainee = getString(R.string.trainee);

        if (mGroupMemberList.size() == 0) {
            alertNetworkErrorAndFinish(mErrorMessage);
        } else {
            String title = getResources().getString(R.string.s_people, mGroupMemberList.size());
            title = " (" + title + ")";
            mBaseToolbar.setTitle(mTitle + title);

            if (mShowOfficialPhoto) {
                GridView gridView = (GridView) findViewById(R.id.gridView);
                if (gridView != null) {
                    gridView.setVisibility(View.VISIBLE);
                    if (mGroupData.getId().equals(Config.GROUP_ID_AKB48)) {
                        float density = mContext.getResources().getDisplayMetrics().density;
                        int spacing = (int) (10 * density);
                        gridView.setHorizontalSpacing(spacing);
                        gridView.setVerticalSpacing(spacing);

                        RelativeLayout content = (RelativeLayout) findViewById(R.id.content);
                        if (content != null && mGroupData.getId().equals(Config.GROUP_ID_AKB48)) {
                            content.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                        }
                    }
                    TeamGridAdapter adapter = new TeamGridAdapter(this, mGroupData, mTeamDataList);
                    gridView.setAdapter(adapter);
                    gridView.setOnItemClickListener(itemClickListener);
                }
            } else {
                TeamListAdapter adapter = new TeamListAdapter(this, mGroupData, mTeamDataList);
                ListView listView = (ListView) findViewById(R.id.listView);
                if (listView != null) {
                    listView.setVisibility(View.VISIBLE);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(itemClickListener);
                }
            }
        }
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TeamData teamData = (TeamData) parent.getItemAtPosition(position);
            //MemberData memberData = (MemberData) teamData.getMemberData();
            //Log.e(mTag, "memberData.getTeamName(): " + memberData.getTeamName());

            ArrayList<MemberData> memberList = new ArrayList<>();
            for (MemberData memberData : mGroupMemberList) {
                if (memberData.getTeamName().equals(teamData.getName())) {
                    //Log.e(mTag, ">> " + md.getTeamName());
                    memberData.setGroupId(mGroupData.getId());
                    memberList.add(memberData);
                }
            }
            startMemberListActivity(teamData, memberList);
        }
    };

    public void startMemberListActivity(TeamData teamData, ArrayList<MemberData> memberList) {
        Intent intent = new Intent(this, MemberListActivity.class);
        //Intent intent = new Intent(this, MemberRecyclerActivity.class);
        intent.putExtra("groupData", mGroupData);
        intent.putExtra("teamData", teamData);
        intent.putExtra("memberList", memberList);

        showToolbarProgressBar();

        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e(mTag, "onActivityResult().resultCode: " + resultCode);

        hideToolbarProgressBar();

        if (resultCode == Config.RESULT_CODE_FINISH) {
            Intent intent = new Intent();
            //intent.putExtra("articleId", mArticleId);
            setResult(Config.RESULT_CODE_FINISH, intent);
            finish();
        }

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
