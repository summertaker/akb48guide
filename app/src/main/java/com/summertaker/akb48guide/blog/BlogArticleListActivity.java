package com.summertaker.akb48guide.blog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.data.SiteData;
import com.summertaker.akb48guide.data.WebData;
import com.summertaker.akb48guide.parser.Akb48Parser;
import com.summertaker.akb48guide.parser.BaseParser;
import com.summertaker.akb48guide.parser.Keyakizaka46Parser;
import com.summertaker.akb48guide.parser.Ngt48Parser;
import com.summertaker.akb48guide.parser.Nogizaka46Parser;
import com.summertaker.akb48guide.util.EndlessScrollListener;
import com.summertaker.akb48guide.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BlogArticleListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener { //}, BlogAdapterInterface {

    private String mTitle;
    private SiteData mSiteData;

    private Snackbar mSnackbar;

    private LinearLayout mLoLoading;
    LinearLayout mLoLoadingHorizontal;
    private TextView mTvLoadingCount;
    private ProgressBar mPbLoadingHorizontal;
    private LinearLayout mLoLoadingMore;
    private ProgressBar mPbLoadingHorizontalMore;

    private SwipeRefreshLayout mSwipeRefresh;

    private ArrayList<WebData> mWebDataList;
    private ArrayList<WebData> mNewDataList;
    private ArrayList<WebData> mArticleDataList;
    private BlogArticleListAdapter mAdapter;
    private ListView mListView;

    private int mMaxPage = 0;
    private int mCurrentPage = 1;
    private int mVisibleThreshold;

    private boolean mIsFirst = true;
    private boolean mIsLoading = false;
    private boolean mIsRefreshMode = false;
    private String mLatestDataId = null;

    int mItemCount = 0;
    int mItemTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_article_list_activity);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mSiteData = (SiteData) intent.getSerializableExtra("siteData");
        //mRssUrl = mSiteData.getRssUrl();

        mTitle = mSiteData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        RelativeLayout content = (RelativeLayout) findViewById(R.id.content);
        if (content != null) {
            mLoLoading = (LinearLayout) findViewById(R.id.loLoading);
            ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
            Util.setProgressBarColor(pbLoading, Config.PROGRESS_BAR_COLOR_LIGHT, null);

            LinearLayout loLoadingHorizontal = (LinearLayout) findViewById(R.id.loLoadingHorizontal);
            switch (mSiteData.getId()) {
                case Config.BLOG_ID_NGT48_PHOTOLOG:
                    loLoadingHorizontal.setVisibility(View.GONE);
                    break;
                default:
                    loLoadingHorizontal.setVisibility(View.VISIBLE);
                    break;
            }

            mLoLoadingHorizontal = (LinearLayout) findViewById(R.id.loLoadingHorizontal);
            mTvLoadingCount = (TextView) findViewById(R.id.tvLoadingCount);
            mPbLoadingHorizontal = (ProgressBar) findViewById(R.id.pbLoadingHorizontal);

            mLoLoadingMore = (LinearLayout) findViewById(R.id.loLoadingMore);
            ProgressBar pbLoadingMore = (ProgressBar) findViewById(R.id.pbLoadingMore);
            Util.setProgressBarColor(pbLoadingMore, Config.PROGRESS_BAR_COLOR_WHITE, null);
            mPbLoadingHorizontalMore = (ProgressBar) findViewById(R.id.pbLoadingHorizontalMore);

            mSnackbar = Snackbar.make(content, "", Snackbar.LENGTH_SHORT);
            View view = mSnackbar.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
            //tv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
            if (mSwipeRefresh != null) {
                mSwipeRefresh.setOnRefreshListener(this);

                //if (mRssUrl != null && !mRssUrl.isEmpty()) {
                //    mMaxPage = 1;
                //} else {
                switch (mSiteData.getId()) {
                    case Config.BLOG_ID_NGT48_MANAGER:
                        mVisibleThreshold = 2;
                        break;
                    case Config.BLOG_ID_NGT48_PHOTOLOG:
                        mMaxPage = 1;
                        break;
                }
                //}

                mWebDataList = new ArrayList<>();
                mNewDataList = new ArrayList<>();

                mListView = (ListView) findViewById(R.id.listView);
                if (mListView != null) {
                    mAdapter = new BlogArticleListAdapter(this, mSiteData, mWebDataList); //, this);
                    mListView.setAdapter(mAdapter);
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            onContentClick(position);
                        }
                    });
                    mListView.setOnScrollListener(new EndlessScrollListener(mVisibleThreshold) {
                        @Override
                        public boolean onLoadMore(int page, int totalItemsCount) {
                            //Log.e(mTag, "onLoadMore().page: " + page + " / " + mMaxPage);
                            if (mMaxPage == 1) {
                                showLastDataMessage();
                            } else if (mMaxPage > 1 && page > mMaxPage) {
                                showLastDataMessage();
                            } else {
                                mIsRefreshMode = false;
                                loadData();
                            }
                            return true; // ONLY if more data is actually being loaded; false otherwise.
                        }
                    });
                }
            }
        }

        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blog_article_list, menu);
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

    private void showLastDataMessage() {
        mSnackbar.setText(getString(R.string.this_is_the_last_of_the_data));
        mSnackbar.show();
    }

    private void loadData() {
        //Log.e(mTag, "loadData()... mCurrentPage: " + mCurrentPage);

        if (mIsLoading) {
            return;
        }

        String url = mSiteData.getUrl();

        if (!mIsRefreshMode) {
            //if (mMaxPage > 0 && mCurrentPage > mMaxPage) {
            //    return;
            //}
            //Log.e(mTag, "mMaxPage: " + mMaxPage);

            if (mCurrentPage > 1) {
                switch (mSiteData.getId()) {
                    case Config.BLOG_ID_AKB48_TEAM8:
                        url = url + "?p=" + mCurrentPage;
                        break;
                    case Config.BLOG_ID_NGT48_MANAGER:
                        url = url + "lite/?p=" + mCurrentPage;
                        break;
                    case Config.BLOG_ID_SKE48_SELECTED:
                    case Config.BLOG_ID_NMB48_OFFICIAL:
                        url = url + "page-" + mCurrentPage + ".html";
                        break;
                }
                //showToolbarProgressBar();
            }
        }

        String userAgent = Config.USER_AGENT_WEB;
        switch (mSiteData.getId()) {
            case Config.BLOG_ID_SKE48_SELECTED:
            case Config.BLOG_ID_NMB48_OFFICIAL:
            case Config.BLOG_ID_NGT48_MANAGER:
            case Config.BLOG_ID_NGT48_PHOTOLOG:
                userAgent = Config.USER_AGENT_MOBILE;
                break;
        }

        mIsLoading = true;

        if (!mIsFirst && !mIsRefreshMode) {
            mLoLoadingMore.setVisibility(View.VISIBLE);
        }

        //Log.e(mTag, url);
        requestData(url, userAgent);
    }

    /*private void setYearMonth() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, mMonthCount);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1; // beware of month indexing from zero

        mYearMonth = year + "";
        mYearMonth += (month < 10) ? "0" + month : month;
    }*/

    /*private String getNextBlogUrl() {
        setYearMonth();
        return "http://ameblo.jp/" + mAmebaId + "/imagelist-" + mYearMonth + ".html";
    }*/

    /*private String getNextJsonUrl() {
        setYearMonth();
        String url = "http://blogimgapi.ameba.jp/image_list/get.jsonp";
        url += "?limit=18&sp=false&page=2&ameba_id=" + mAmebaId + "&target_ym=" + mYearMonth;
        return url;
    }*/

    private void requestData(final String url, final String userAgent) {
        //Log.e(mTag, "url: " + url);
        //Log.e(mTag, "userAgent: " + userAgent);

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(mTag, "response: " + response.substring(0, 100));
                parseData(url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(mTag, "onErrorResponse(): " + url);
                //mSnackbar.setText(getString(R.string.network_error_occurred)).show();
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
    }

    private void parseData(String url, String response) {
        //Log.e(mTag, "response: " + response.substring(0, 100));
        //Log.e(mTag, "parseList()... " + mSiteData.getGroupId());

        if (response.isEmpty()) {
            if (!mErrorMessage.isEmpty()) {
                alertNetworkErrorAndFinish(mErrorMessage);
            } else {
                renderData();
            }
        } else {
            if (mItemTotal == 0) {
                //-------------------------------
                // 목록 파싱하기
                //-------------------------------
                switch (mSiteData.getId()) {
                    case Config.BLOG_ID_AKB48_TEAM8:
                        Akb48Parser akb48Parser = new Akb48Parser();
                        akb48Parser.parseTeam8ReportList(response, mNewDataList);
                        break;
                    case Config.BLOG_ID_SKE48_SELECTED:
                    case Config.BLOG_ID_NMB48_OFFICIAL:
                        BaseParser parser = new BaseParser();
                        parser.parseAmebaList(response, mNewDataList);
                        mItemTotal = mNewDataList.size();
                        break;
                    case Config.BLOG_ID_NGT48_MANAGER:
                        Ngt48Parser ngt48manager = new Ngt48Parser();
                        ngt48manager.parseLineBlogList(response, mNewDataList);
                        //Collections.sort(mNewDataList);
                        //Collections.sort(mNewDataList, Collections.reverseOrder());
                        break;
                    case Config.BLOG_ID_NGT48_PHOTOLOG:
                        Ngt48Parser ngt48photo = new Ngt48Parser();
                        ngt48photo.parseMemberBlogList(response, mNewDataList);
                        //Collections.sort(mNewDataList);
                        Collections.sort(mNewDataList, Collections.reverseOrder());
                        break;
                }
            }

            //-------------------------------
            // 항목별 사진 파싱하기
            //-------------------------------
            if (mItemTotal > 0) { // && mItemTotal >= mItemCount) {
                if (mItemCount > 0) {
                    BaseParser parser = new BaseParser();
                    String[] array = parser.parseAmebaArticle(response);

                    WebData webData = mNewDataList.get(mItemCount - 1);
                    webData.setContent(array[0]);
                    webData.setImageUrl(array[1]);
                }

                if (mItemCount < mItemTotal) {
                    WebData webData = mNewDataList.get(mItemCount);
                    String reqUrl = webData.getUrl();
                    String reqAgent = Config.USER_AGENT_MOBILE;

                    requestData(reqUrl, reqAgent);
                    updateProgress();

                    mItemCount++;
                } else {
                    mItemTotal = 0;
                    mItemCount = 0;
                    mPbLoadingHorizontalMore.setProgress(0);
                }
            }

            if (mItemTotal == 0) {
                renderData();
            }
        }
    }

    private void updateProgress() {
        //if (mItemCount == 0) {
        //    mLoLoadingHorizontal.setVisibility(View.VISIBLE);
        //}

        int count = mItemCount + 1;
        float progress = (float) count / (float) mItemTotal;
        int progressValue = (int) (progress * 100.0);
        //Log.e(mTag, mUrlCount + " / " + mUrlTotal + " = " + progressValue);

        if (mIsFirst) {
            String text = "( " + count + " / " + mItemTotal + " )";
            mTvLoadingCount.setText(text);
            mPbLoadingHorizontal.setProgress(progressValue);
        } else {
            mPbLoadingHorizontalMore.setProgress(progressValue);
        }
    }

    private void renderData() {
        //Log.e(mTag, "renderData()... mNewDataList.size(): " + mNewDataList.size());

        //String title = mTitle;
        //if (!mSiteData.getBlogUrl().contains(".xml")) {
        //    title += " (" + mCurrentPage + "/" + mMaxPage + ")";
        //}
        //mBaseToolbar.setTitle(title);

        if (mIsFirst) {
            mLoLoading.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mIsFirst = false;
        } else {
            mLoLoadingMore.setVisibility(View.GONE);
        }

        if (mNewDataList.size() == 0 && mCurrentPage == 1) {
            //Log.e(mTag, "mSiteData.getUrl(): " + mSiteData.getUrl());
            //Log.e(mTag, "mNewDataList.size(): " + mNewDataList.size());
            alertNetworkErrorAndFinish(mErrorMessage);
        } else {
            if (mIsRefreshMode) {
                //Log.e(mTag, "mNewDataList.size(): " + mNewDataList.size());

                int newCount = 0;
                ArrayList<WebData> uniqueList = new ArrayList<>();
                for (WebData webData : mNewDataList) {
                    if (webData.getId().equals(mLatestDataId)) {
                        break;
                    }
                    uniqueList.add(0, webData);
                    newCount++;
                }
                //Log.e(mTag, "uniqueList.size(): " + uniqueList.size());

                if (newCount == 0) {
                    mSnackbar.setText(getString(R.string.no_new_data)).show();
                } else {
                    Collections.reverse(uniqueList);
                    for (WebData webData : uniqueList) {
                        mWebDataList.add(0, webData);
                    }
                }
                mSwipeRefresh.setRefreshing(false);
                mIsRefreshMode = false;
                //goTop();
            } else {
                if (mNewDataList.size() == 0) {
                    showLastDataMessage();
                } else {
                    mWebDataList.addAll(mNewDataList);
                    mAdapter.notifyDataSetChanged();
                    mCurrentPage++;
                }
            }
        }

        if (mWebDataList.size() > 0) {
            WebData latestData = mWebDataList.get(0);
            mLatestDataId = latestData.getId();
        }
        //Log.e(mTag, "mLatestDataId: " + mLatestDataId);

        //hideToolbarProgressBar();
        mNewDataList.clear();
        mIsLoading = false;
    }

    private void goTop() {
        mListView.setSelection(0);
    }

    protected void onToolbarClick() {
        goTop();
    }

    @Override
    public void onRefresh() {
        //Log.e(mTag, "onRefresh().....");
        mIsRefreshMode = true;
        loadData();
    }

    //@Override
    public void onPictureClick(int position) {
        //Log.e(mTag, "onPictureClick(" + position + ")");

        onContentClick(position);

        /*WebData webData = mWebDataList.get(position);
        String imageUrl = webData.getImageUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
        startActivityForResult(intent, 100);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
    }

    //@Override
    public void onContentClick(int position) {
        //Log.e(mTag, "onContentClick(" + position + ")");

        WebData webData = mWebDataList.get(position);
        String url = webData.getUrl();
        if (url != null && !url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            //Intent intent = new Intent(mContext, WebViewActivity.class);
            //intent.putExtra("title", webData.getTitle());
            //intent.putExtra("url", webData.getUrl());
            //startActivity(intent);
            startActivityForResult(intent, 100);
            //showToolbarProgressBar();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    public void goWebSite() {
        //Log.e(mTag, "onContentClick(" + position + ")");

        if (mIsFirst) {
            mSnackbar.setText(getString(R.string.please_wait)).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mSiteData.getUrl()));
            //intent.putExtra("url", webData.getUrl());
            //startActivity(intent);
            startActivityForResult(intent, 100);
            //showToolbarProgressBar();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        hideToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
