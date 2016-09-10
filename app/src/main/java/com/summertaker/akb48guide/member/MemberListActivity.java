package com.summertaker.akb48guide.member;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
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
import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.data.MemberData;
import com.summertaker.akb48guide.data.TeamData;
import com.summertaker.akb48guide.parser.BaseParser;
import com.summertaker.akb48guide.parser.NamuwikiParser;
import com.summertaker.akb48guide.parser.WikipediaEnParser;
import com.summertaker.akb48guide.util.Translator;
import com.summertaker.akb48guide.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemberListActivity extends BaseActivity {

    boolean mShowOfficialPhoto;

    String mTitle;
    GroupData mGroupData;
    TeamData mTeamData;

    ProgressBar mPbLoading;
    ArrayList<TeamData> mTeamList;
    ArrayList<MemberData> mMemberList;
    ArrayList<MemberData> mWikiMemberList;

    String mLocale;
    BaseParser mWikiParser;
    //CacheManager mCacheManager;

    boolean isDataLoaded = false;
    boolean isWikiLoaded = false;

    Handler mHandler;
    Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = MemberListActivity.this;
        mResources = mContext.getResources();

        Setting setting = new Setting(mContext);
        mShowOfficialPhoto = setting.get(Config.SETTING_DISPLAY_OFFICIAL_PHOTO).equals(Config.SETTING_DISPLAY_OFFICIAL_PHOTO_YES);

        int contentView = mShowOfficialPhoto ? R.layout.member_grid_activity : R.layout.member_list_activity;
        setContentView(contentView);

        Intent intent = getIntent();
        mGroupData = (GroupData) intent.getSerializableExtra("groupData");
        mTeamData = (TeamData) intent.getSerializableExtra("teamData");
        mMemberList = (ArrayList<MemberData>) intent.getSerializableExtra("memberList");
        //Log.e(mTag, "mMemberList.size(): " + mMemberList.size());

        mTitle = mGroupData.getName();
        if (mTeamData != null) {
            String teamName = mTeamData.getName();
            //Translator translator = new Translator(mContext);
            //teamName = translator.translateTeam(mGroupData.getId(), teamName);
            mTitle += " " + teamName;
        }
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        mTeamList = new ArrayList<>();

        mPbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(mPbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

        mLocale = Util.getLocaleStrng(mContext);
        switch (mLocale) {
            case "KR":
                mWikiParser = new NamuwikiParser();
                break;
            default:
                mWikiParser = new WikipediaEnParser();
                break;
        }

        //mCacheManager = new CacheManager(mSharedPreferences);

        if (mMemberList == null) {
            //-----------------------------------------
            // 팀 구성이 없는 경우 (그룹 > 멤버)
            //-----------------------------------------
            mMemberList = new ArrayList<>();
            String url = mGroupData.getUrl();
            String userAgent = Config.USER_AGENT_WEB;
            requestData(url, userAgent);
        } else {
            //-----------------------------------------
            // 팀 구성이 있는 경우 (그룸 > 팀 > 멤버)
            //-----------------------------------------
            isDataLoaded = true;
        }

        requestWiki();
    }

    private void requestData(final String url, final String userAgent) {
        //Log.e(mTag, "url: " + url);
        //Log.e(mTag, "userAgent: " + userAgent);

        //final String cacheId = Util.urlToId(url);
        //Log.e(mTag, "cacheId: " + cacheId);
        //String cacheData = mCacheManager.load(cacheId);

        //if (cacheData == null) {
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(mTag, response.toString());
                //mCacheManager.save(cacheId, response);
                parseData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

        // Adding request to request queue
        String tag_string_req = "string_req";
        BaseApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
        //} else {
        //    parseData(url, cacheData);
        //}
    }

    private void requestWiki() {
        String url = mWikiParser.getUrl(mGroupData.getId());
        if (url == null || url.isEmpty()) {
            isWikiLoaded = true;
            renderData();
        } else {
            requestData(url, Config.USER_AGENT_WEB);
        }
    }

    private void parseData(String url, String response) {
        boolean isMobile = url.equals(mGroupData.getMobileUrl());

        if (url.contains("wiki")) {
            //Log.e(mTag, response);
            mWikiMemberList = new ArrayList<>();
            mWikiParser.parse48List(response, mGroupData, mWikiMemberList);
            isWikiLoaded = true;
        } else {
            BaseParser baseParser = new BaseParser();
            baseParser.parseMemberList(mContext, response, mGroupData, mMemberList, mTeamList, isMobile);
            isDataLoaded = true;
        }
        renderData();
    }

    private void renderData() {
        if (!isDataLoaded || !isWikiLoaded) {
            return;
        }

        mPbLoading.setVisibility(View.GONE);
        //Log.e(mTag, "mMemberList.size(): " + mMemberList.size());

        if (mMemberList.size() == 0) {
            alertNetworkErrorAndFinish(mErrorMessage);
        } else {
            String count = String.format(getResources().getString(R.string.s_people), mMemberList.size());
            mBaseToolbar.setTitle(mTitle + " (" + count + ")");

            final int wikiSize = mWikiMemberList.size();
            String locale = Util.getLocaleStrng(mContext);

            for (MemberData memberData : mMemberList) {
                if (wikiSize == 0) {
                    String localeName = memberData.getName(); //memberData.getNameEn();
                    memberData.setLocaleName(localeName);
                } else {
                    for (MemberData wikiData : mWikiMemberList) {
                        if (Util.isEqualString(memberData.getNoSpaceName(), wikiData.getNoSpaceName())) {
                            String localeName;
                            switch (locale) {
                                case "KR":
                                    localeName = wikiData.getNameKo();
                                    break;
                                default:
                                    localeName = wikiData.getNameEn();
                                    break;
                            }
                            if (localeName == null || localeName.isEmpty()) {
                                localeName = memberData.getName();
                                //localeName = memberData.getNameEn();
                            }
                            /*if (localeName == null || localeName.isEmpty()) {
                                localeName = memberData.getFurigana();
                            }*/

                            memberData.setLocaleName(localeName);
                            memberData.setGeneralManager(wikiData.isGeneralManager());         // 총감독
                            memberData.setManager(wikiData.isManager());                       // 지배인
                            memberData.setGeneralCaptain(wikiData.isGeneralCaptain());         // 그룹 캡틴
                            memberData.setCaptain(wikiData.isCaptain());                       // 캡틴,리더
                            memberData.setViceCaptain(wikiData.isViceCaptain());               // 부캡틴,부리더
                            memberData.setConcurrent(wikiData.isConcurrent());                 // 겸임
                            memberData.setGeneration(wikiData.getGeneration());
                            memberData.setBirthday(wikiData.getBirthday());
                            memberData.setNamuwikiUrl(wikiData.getNamuwikiUrl());
                            memberData.setNamuwikiInfo(wikiData.getNamuwikiInfo());
                            memberData.setConcurrentInfo(wikiData.getConcurrentInfo());
                            break;
                        } else {
                            String localeName = memberData.getName(); //memberData.getNameEn();
                            //if (localeName == null || localeName.isEmpty()) {
                            //    localeName = memberData.getFurigana();
                            //}
                            memberData.setLocaleName(localeName);
                        }
                    }
                }
            }

            if (mShowOfficialPhoto) {
                GridView gridView = (GridView) findViewById(R.id.gridView);
                if (gridView != null) {
                    RelativeLayout content = (RelativeLayout) findViewById(R.id.content);
                    if (content != null && mGroupData.getId().equals(Config.GROUP_ID_AKB48)) {
                        content.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                    }
                    gridView.setVisibility(View.VISIBLE);

                    MemberGridAdapter adapter = new MemberGridAdapter(mContext, mGroupData, mMemberList);
                    gridView.setAdapter(adapter);
                    gridView.setOnItemClickListener(itemClickListener);
                }
            } else {
                ListView listView = (ListView) findViewById(R.id.listView);
                if (listView != null) {
                    listView.setVisibility(View.VISIBLE);
                    MemberListAdapter adapter = new MemberListAdapter(mContext, mGroupData, mMemberList);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(itemClickListener);
                }
            }
        }
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MemberData memberData = (MemberData) parent.getItemAtPosition(position);

            Intent intent = new Intent(MemberListActivity.this, MemberDetailActivity.class);
            //Intent intent = new Intent(MemberListActivity.this, MemberPagerActivity.class);
            intent.putExtra("groupData", mGroupData);
            intent.putExtra("teamData", mTeamData);
            intent.putExtra("memberData", memberData);

            showToolbarProgressBar();
            startActivityForResult(intent, 100);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.i("AgeListActivity", "onActivityResult().resultCode: " + resultCode);

        hideToolbarProgressBar();
        //if (resultCode == Activity.RESULT_OK) {
        //}
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
