package com.summertaker.akb48guide.youtube;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.BaseActivity;
import com.summertaker.akb48guide.common.BaseApplication;
import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.data.WebData;
import com.summertaker.akb48guide.data.SiteData;
import com.summertaker.akb48guide.parser.YoutubeParser;
import com.summertaker.akb48guide.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class YoutubeListActivity extends BaseActivity {

    ArrayList<SiteData> mSiteDataList;

    SiteData mSiteData;
    ArrayList<WebData> mWebDataList = new ArrayList<>();

    ProgressBar mPbLoading;

    int mWebsiteCount = 0;

    //CacheManager mCacheManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_list_activity);

        mContext = YoutubeListActivity.this;
        mResources = mContext.getResources();

        Intent intent = getIntent();
        mSiteData = (SiteData) intent.getSerializableExtra("websiteData");

        String title = getString(R.string.youtube);
        title = title + " - " + mSiteData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, title);

        mPbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(mPbLoading, Config.PROGRESS_BAR_COLOR_NORMAL, null);

        requestData(mSiteData.getUrl(), Config.USER_AGENT_WEB);

        /*DataManager dataManager = new DataManager();
        mSiteDataList = dataManager.getYoutubeList(mContext);
        for (SiteData websiteData : mSiteDataList) {
            requestData(websiteData.getUrl(), Config.USER_AGENT_WEB);
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blog_rss, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_open_in_web_browser) {
            goWebSite();
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
                //Log.e(mTag, response);
                //mCacheManager.save(cacheId, response);
                parseData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(mTag, "url: " + url);
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

        String tag_string_req = "string_req";
        BaseApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
        //} else {
        //    parseData(url, cacheData);
        //}
    }

    private void parseData(String url, String response) {
        YoutubeParser youtubeParser = new YoutubeParser();
        youtubeParser.parseList(response, mSiteData, mWebDataList);

        /*mWebsiteCount++;
        if (mWebsiteCount >= mSiteDataList.size()) {
            renderData();
        }*/

        renderData();
    }

    private void renderData() {
        mPbLoading.setVisibility(View.GONE);

        //String team = getString(R.string.team);
        //String trainee = getString(R.string.trainee);

        if (mWebDataList.size() == 0) {
            alertNetworkErrorAndFinish(mErrorMessage);
        } else {
            YoutubeListAdapter adapter = new YoutubeListAdapter(this, mSiteData, mWebDataList);

            /*GridView gridView = (GridView) findViewById(R.id.gridView);
            if (gridView != null) {
                gridView.setVisibility(View.VISIBLE);
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener(itemClickListener);
            }*/
            ListView listView = (ListView) findViewById(R.id.listView);
            if (listView != null) {
                listView.setVisibility(View.VISIBLE);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        WebData webData = (WebData) parent.getItemAtPosition(position);

                        String url = webData.getUrl();
                        if (url != null && !url.isEmpty()) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            //Intent intent = new Intent(mContext, WebViewActivity.class);
                            //intent.putExtra("title", webData.getTitle());
                            //intent.putExtra("url", webData.getUrl());
                            startActivity(intent);
                            //startActivityForResult(intent, 100);
                            //showToolbarProgressBar();
                            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    }
                });
            }
        }
    }

    public void goWebSite() {
        //Log.e(mTag, "onContentClick(" + position + ")");

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mSiteData.getUrl()));
        //intent.putExtra("url", webData.getUrl());
        startActivity(intent);
        //startActivityForResult(intent, 100);
        //showToolbarProgressBar();
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e(mTag, "onActivityResult().resultCode: " + resultCode);
        //hideToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
