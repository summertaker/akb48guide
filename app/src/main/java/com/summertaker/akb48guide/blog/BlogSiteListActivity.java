package com.summertaker.akb48guide.blog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.summertaker.akb48guide.data.SiteData;
import com.summertaker.akb48guide.parser.Ske48Parser;
import com.summertaker.akb48guide.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BlogSiteListActivity extends BaseActivity {

    SiteData mSiteData;

    Snackbar mSnackbar;
    String mTitle;

    //int mBlogTotal = 0;
    int mCheckCount = 0;
    BlogSiteListAdapter mAdapter;
    ArrayList<SiteData> mBlogList = new ArrayList<>();

    LinearLayout mLoLoading;
    ProgressBar mPbLoading;
    TextView tvLoadingName;
    TextView tvLoadingCount;
    ProgressBar mPbLoadingHorizontal;

    CacheManager mCacheManager;
    String mCacheId;
    String mCheckCacheId;
    boolean mIsCacheValid = false;

    ArrayList<SiteData> mCheckSettings = new ArrayList<>();
    ArrayList<SiteData> mBlogDateList = new ArrayList<>();

    String mClickId = "";

    boolean mIsLoadFinished = false;

    //JSONArray mCheckSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_site_list_activity);

        mContext = BlogSiteListActivity.this;
        mResources = mContext.getResources();

        Intent intent = getIntent();
        mSiteData = (SiteData) intent.getSerializableExtra("siteData");
        //Log.e(mTag, "id: " + mSiteData.getId());

        RelativeLayout content = (RelativeLayout) findViewById(R.id.content);
        mSnackbar = Snackbar.make(content, "", Snackbar.LENGTH_SHORT);
        View view = mSnackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
        //tv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        mTitle = mSiteData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        mLoLoading = (LinearLayout) findViewById(R.id.loLoading);
        mPbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(mPbLoading, Config.PROGRESS_BAR_COLOR_LIGHT, null);
        tvLoadingName = (TextView) findViewById(R.id.tvLoadingName);
        tvLoadingCount = (TextView) findViewById(R.id.tvLoadingCount);
        mPbLoadingHorizontal = (ProgressBar) findViewById(R.id.pbLoadingHorizontal);
        if (mPbLoadingHorizontal != null) {
            mPbLoadingHorizontal.setProgress(0);
        }

        mCacheManager = new CacheManager(mSharedPreferences);
        mCacheId = mSiteData.getId();

        // 캐쉬 데이터: 사용자가 선택한 체크할 블로그 URL 목록
        mCheckCacheId = mCacheId + Config.CACHE_ID_BLOG_CHECK_SUFFIX;
        JSONObject checkObject = mCacheManager.loadJsonObject(mCheckCacheId, 0); // Minutes, 0 = No Expire
        if (checkObject != null) {
            try {
                //Log.e(mTag, checkObject.toString());

                JSONArray jsonArray = checkObject.getJSONArray("data");
                //Log.e(mTag, "mCheckSettings.length(): " + mCheckSettings.length());

                //Log.e(mTag, "== SETTING.....................");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String id = obj.getString("id");
                    String name = obj.getString("name");
                    String url = obj.getString("url");
                    String date = obj.getString("date");
                    //Log.e(mTag, name + " / " + date);

                    SiteData data = new SiteData();
                    data.setId(id);
                    data.setName(name);
                    data.setUrl(url);
                    data.setUpdateCheckDate(date);
                    mCheckSettings.add(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(mTag, e.getMessage());
            }
        }

        // 사용자가 업데이터 체크 설정을 변경한 경우 캐쉬 데이터를 무시하고 체크를 진행한다.
        String listChanged = mSharedPreferences.getString(mCacheId + Config.CACHE_ID_BLOG_LIST_CHANGED, "");
        //Log.e(mTag, "listChanged : " + listChanged);
        if (!listChanged.isEmpty()) {
            Setting setting = new Setting(mContext);
            setting.set(mCacheId + Config.CACHE_ID_BLOG_LIST_CHANGED, "");
            mIsCacheValid = false;
        }

        String url = mSiteData.getUrl();
        String userAgent = Config.USER_AGENT_WEB;
        switch (mSiteData.getId()) {
            case Config.BLOG_ID_SKE48_MEMBER:
                userAgent = Config.USER_AGENT_MOBILE;
                break;
        }
        requestData(url, userAgent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blog_site_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            if (mIsLoadFinished) {
                goSettings();
            } else {
                mSnackbar.setText(getString(R.string.please_wait)).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                //Log.e(mTag, response.substring(0, 100));
                //mCacheManager.save(cacheId, response);
                parseData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(mTag, "onErrorResponse(): " + url);
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
        if (response.isEmpty() && url.equals(mSiteData.getUrl())) {
            renderData();
        } else {
            // 블로그 목록 파싱하기
            if (url.equals(mSiteData.getUrl())) {
                switch (mSiteData.getId()) {
                    case Config.BLOG_ID_SKE48_MEMBER:
                        Ske48Parser ske48Parser = new Ske48Parser();
                        //String text = ske48Parser.parseBlogSiteList(response, mBlogList);
                        ske48Parser.parseMobileBlogSiteList(response, mBlogList);
                        checkDate();
                        renderData();
                        break;
                }
            } else {
                if (mErrorMessage != null && !mErrorMessage.isEmpty()) {
                    mSnackbar.setText(mErrorMessage).show();
                }
            }
            //Log.e(mTag, "mBlogTotal: " + mBlogTotal);

            /*if (mSiteData.getId().equals(Config.BLOG_ID_SKE48_MEMBER)) {
                // 노기자카46인 경우 블로그 하나씩 접속해서 업데이트 날짜 가져오기
                if (mIsCacheValid || mCheckSettings.size() == 0) {
                    updateData();
                    renderData();
                } else {
                    if (mCheckCount > 0) {
                        //Nogizaka46Parser nogizaka46Parser = new Nogizaka46Parser();
                        //String date = nogizaka46Parser.parseBlogUpdateDate(response);
                        //mCheckSettings.get(mCheckCount - 1).setUpdateDate(date);
                    }

                    if (mCheckCount == mCheckSettings.size()) {
                        saveCache();
                        updateData();
                        renderData();
                    } else {
                        SiteData site = mCheckSettings.get(mCheckCount);
                        String siteName = site.getName();
                        String siteUrl = site.getUrl();
                        String userAgent = Config.USER_AGENT_WEB;
                        //Log.e(mTag, "Request... " + siteName);

                        updateProgress(siteName);
                        requestData(siteUrl, userAgent);

                        mCheckCount++;
                    }
                }
            }*/
        }
    }

    private void updateProgress(String name) {
        if (mCheckCount == 0) {
            LinearLayout loLoadingHorizontal = (LinearLayout) findViewById(R.id.loLoadingHorizontal);
            loLoadingHorizontal.setVisibility(View.VISIBLE);
        }

        tvLoadingName.setText(name);

        int count = mCheckCount + 1;

        String text = "( " + count + " / " + mCheckSettings.size() + " )";
        tvLoadingCount.setText(text);

        float progress = (float) count / (float) mCheckSettings.size();
        int progressValue = (int) (progress * 100.0);
        //Log.e(mTag, mUrlCount + " / " + mUrlTotal + " = " + progressValue);
        mPbLoadingHorizontal.setProgress(progressValue);
    }

    private void checkDate() {
        //Log.e(mTag, text);

        if (mCheckSettings.size() == 0) {
            return;
        }

        String checkFormat = "yyyy/MM/dd HH:mm:ss";
        String updateFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
        switch (mSiteData.getId()) {
            case Config.BLOG_ID_SKE48_MEMBER:
                updateFormat = "yyyy.MM.dd HH:mm:ss";
                break;
        }
        //Log.e(mTag, checkFormat + " / " + updateFormat);
        SimpleDateFormat checkSdf = new SimpleDateFormat(checkFormat, Locale.getDefault());
        SimpleDateFormat updateSdf = new SimpleDateFormat(updateFormat, Locale.getDefault());
        //updateSdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            //Log.e(mTag, "mCheckSettings.size(): " + mCheckSettings.size());
            //Log.e(mTag, "mBlogList.size(): " + mBlogList.size());

            for (int i = 0; i < mCheckSettings.size(); i++) {
                String settingId = mCheckSettings.get(i).getId();
                String checkDate = mCheckSettings.get(i).getUpdateCheckDate();
                //Log.e(mTag, "setting: " + mCheckSettings.get(i).getName() + " / " + checkDate + " / " + settingId);

                for (int j = 0; j < mBlogList.size(); j++) {
                    String id = mBlogList.get(j).getId();
                    String updateDate = mBlogList.get(j).getUpdateDate(); // 2016-07-17T14:34+09:00
                    //Log.e(mTag, "blog: " + mBlogList.get(j).getName() + " / " + updateDate + " / " + id);

                    if (id.equals(settingId)) {
                        //Log.e(mTag, "Found................");

                        Date d1 = updateSdf.parse(updateDate);
                        Date d2 = checkSdf.parse(checkDate);
                        long diff = d1.getTime() - d2.getTime();
                        //Log.e(mTag, "diff: " + diff);

                        boolean updated = (diff > 0);

                        mBlogList.get(j).setUpdateDate(updateDate);
                        mBlogList.get(j).setUpdateCheckDate(checkDate);
                        mBlogList.get(j).setUpdated(updated);
                        //Log.e(mTag, mBlogList.get(j).getName());
                        break;
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(mTag, e.getMessage());
        }
    }

    private void saveCache() {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray();
            for (SiteData site : mCheckSettings) {
                //Log.e(mTag, site.getName() + " / " + site.getUpdateCheckDate());

                JSONObject object = new JSONObject();
                object.put("id", site.getId());
                object.put("name", site.getName());
                object.put("url", site.getUrl());
                object.put("date", site.getUpdateCheckDate());
                jsonArray.put(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(mTag, e.getMessage());
        }

        mCacheManager.save(mCheckCacheId, jsonArray);
    }

    private void updateData() {
        /*String oldstring = "2011-01-18 00:00:00.0";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parseImage(oldstring);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        String format = "yyyy/MM/dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());

        try {
            //Log.e(mTag, "mBlogList.size(): " + mBlogList.size());

            for (int i = 0; i < mBlogList.size(); i++) {
                String id = mBlogList.get(i).getId();

                for (SiteData setting : mCheckSettings) {

                    if (id.equals(setting.getId())) {
                        String updateDate = setting.getUpdateDate();
                        String checkDate = setting.getUpdateCheckDate();
                        //Log.e(mTag, updateDate + " / " + checkDate);

                        Date d1 = sdf.parse(updateDate);
                        Date d2 = sdf.parse(checkDate);
                        long diff = d1.getTime() - d2.getTime();
                        //Log.e(mTag, "diff: " + diff);

                        boolean updated = (diff > 0);

                        mBlogList.get(i).setUpdateDate(updateDate);
                        mBlogList.get(i).setUpdateCheckDate(checkDate);
                        mBlogList.get(i).setUpdated(updated);
                        break;
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(mTag, e.getMessage());
        }
    }

    private void renderData() {
        //mPbLoading.setVisibility(View.GONE);
        mLoLoading.setVisibility(View.GONE);

        //String team = getString(R.string.team);
        //String trainee = getString(R.string.trainee);

        if (mBlogList.size() == 0) {
            alertNetworkErrorAndFinish(mErrorMessage);
        } else {
            String title = " (" + mBlogList.size() + ")"; //getResources().getString(R.string.s_people, mBlogList.size());
            mBaseToolbar.setTitle(mTitle + title);

            GridView gridView = (GridView) findViewById(R.id.gridView);
            if (gridView != null) {
                gridView.setVisibility(View.VISIBLE);
                mAdapter = new BlogSiteListAdapter(this, mBlogList);
                gridView.setAdapter(mAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SiteData siteData = (SiteData) parent.getItemAtPosition(position);
                        mClickId = siteData.getId();
                        //siteData.setId(mSiteData.getId());

                        goActivity(siteData);

                        /*Intent intent = new Intent(mContext, BlogArticleListActivity.class);
                        intent.putExtra("siteData", siteData);

                        showToolbarProgressBar();
                        startActivityForResult(intent, 0);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
                    }
                });
            }

            mIsLoadFinished = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdapter != null) {
            //Log.e(mTag, "mClickId: " + mClickId);

            String format = "yyyy/MM/dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            String now = sdf.format(calendar.getTime());

            for (int i = 0; i < mBlogList.size(); i++) {
                String id = mBlogList.get(i).getId();
                if (id.equals(mClickId)) {
                    //Log.e(mTag, "id: " + id);
                    mBlogList.get(i).setUpdated(false);
                    mBlogList.get(i).setUpdateCheckDate(now);
                    break;
                }
            }
            mAdapter.notifyDataSetChanged();

            for (int i = 0; i < mCheckSettings.size(); i++) {
                String id = mCheckSettings.get(i).getId();
                if (id.equals(mClickId)) {
                    //Log.e(mTag, "now: " + now);
                    mCheckSettings.get(i).setUpdateCheckDate(now);
                    break;
                }
            }
            saveCache();
        }
    }

    private void goActivity(SiteData siteData) {
        Intent intent = new Intent(mContext, BlogArticleDetailActivity.class);
        intent.putExtra("siteData", mSiteData);
        intent.putExtra("blogData", siteData);

        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goWebSite(SiteData siteData) {
        //Log.e(mTag, siteData.getUrl());

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(siteData.getUrl()));
        //intent.putExtra("url", webData.getUrl());
        startActivity(intent);
        //startActivityForResult(intent, 100);
        //showToolbarProgressBar();
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void goSettings() {
        Intent intent = new Intent(mContext, BlogSettingActivity.class);
        intent.putExtra("siteData", mSiteData);
        intent.putExtra("blogList", mBlogList);

        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Config.RESULT_CODE_FINISH) {
            mSiteData = (SiteData) data.getSerializableExtra("siteData");

            Intent intent = new Intent();
            intent.putExtra("siteData", mSiteData);
            setResult(Config.RESULT_CODE_FINISH, intent);
            finish();
        }
        hideToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
