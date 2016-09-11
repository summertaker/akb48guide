package com.summertaker.akb48guide.rawphoto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
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
import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.data.WebData;
import com.summertaker.akb48guide.parser.Akb48ShopParser;
import com.summertaker.akb48guide.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RawPhotoOfficialListActivity extends BaseActivity {

    GroupData mGroupData;
    WebData mWebData;

    String mTitle;

    RawPhotoOfficialListAdapter mAdapter;

    ArrayList<WebData> mWebDataList = new ArrayList<>();

    LinearLayout mLoLoading;

    String mUserAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.raw_photo_official_list_activity);

        mContext = RawPhotoOfficialListActivity.this;
        mResources = mContext.getResources();

        Intent intent = getIntent();
        mGroupData = (GroupData) intent.getSerializableExtra("groupData");
        mWebData = (WebData) intent.getSerializableExtra("webData");
        //Log.e(mTag, "id: " + mSiteData.getId());

        //mTitle = getString(R.string.raw_photo) + " / " + mGroupData.getName();
        mTitle = mWebData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        mLoLoading = (LinearLayout) findViewById(R.id.loLoading);
        ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(pbLoading, Config.PROGRESS_BAR_COLOR_LIGHT, null);

        String url = mWebData.getUrl();
        mUserAgent = Config.USER_AGENT_MOBILE;

        requestData(url, mUserAgent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.raw_photo_list, menu);
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

        BaseApplication.getInstance().addToRequestQueue(strReq, "strReq");
        //} else {
        //    parseData(url, cacheData);
        //}
    }

    private void parseData(String url, String response) {
        Akb48ShopParser akb48ShopParser = new Akb48ShopParser();
        String nextUrl = akb48ShopParser.parseMobileList(response, mWebData.getName(), mWebDataList);
        //Log.e(mTag, "nextUrl: " + nextUrl.length());

        if (!nextUrl.isEmpty()) {
            requestData(nextUrl, mUserAgent);
            //requestRetro(nextUrl, mUserAgent);
        } else {
            renderData();
        }
    }

    private void renderData() {
        //mPbLoading.setVisibility(View.GONE);
        mLoLoading.setVisibility(View.GONE);

        //String team = getString(R.string.team);
        //String trainee = getString(R.string.trainee);

        if (mWebDataList.size() == 0) {
            alertAndFinish(getString(R.string.data_not_found));
        } else {
            //String title = " (" + mWebDataList.size() + ")"; //getResources().getString(R.string.s_people, mBlogList.size());
            //mBaseToolbar.setTitle(mTitle + title);

            GridView gridView = (GridView) findViewById(R.id.gridView);
            if (gridView != null) {
                gridView.setVisibility(View.VISIBLE);

                mAdapter = new RawPhotoOfficialListAdapter(this, mWebDataList);
                gridView.setAdapter(mAdapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        WebData webData = (WebData) parent.getItemAtPosition(position);
                        goActivity(webData);
                    }
                });
            }
        }
    }

    private void goActivity(WebData webData) {
        Intent intent = new Intent(mContext, RawPhotoDetailActivity.class);
        intent.putExtra("groupData", mGroupData);
        intent.putExtra("webData", webData);

        startActivityForResult(intent, 100);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goWebSite() {
        //Log.e(mTag, siteData.getUrl());

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mWebData.getUrl()));
        //intent.putExtra("url", webData.getUrl());
        //startActivity(intent);
        startActivityForResult(intent, 100);
        //showToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //hideToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
