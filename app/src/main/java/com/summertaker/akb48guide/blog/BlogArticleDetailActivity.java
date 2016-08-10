package com.summertaker.akb48guide.blog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.BaseActivity;
import com.summertaker.akb48guide.common.BaseApplication;
import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.data.SiteData;
import com.summertaker.akb48guide.data.WebData;
import com.summertaker.akb48guide.parser.Ske48Parser;
import com.summertaker.akb48guide.util.ProportionalImageView;
import com.summertaker.akb48guide.util.Util;

import java.util.HashMap;
import java.util.Map;

public class BlogArticleDetailActivity extends BaseActivity {

    SiteData mSiteData;
    SiteData mBlogData;
    WebData mWebData;

    LinearLayout mLoLoading;
    ProgressBar mPbLoading;

    boolean mIsFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_article_detail_activity);

        mContext = BlogArticleDetailActivity.this;

        Intent intent = getIntent();
        mSiteData = (SiteData) intent.getSerializableExtra("siteData");
        mBlogData = (SiteData) intent.getSerializableExtra("blogData");

        mWebData = new WebData();

        String title = mBlogData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, title);

        mLoLoading = (LinearLayout) findViewById(R.id.loLoading);
        mPbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(mPbLoading, Config.PROGRESS_BAR_COLOR_LIGHT, null);

        String url = mBlogData.getUrl();
        String userAgent = Config.USER_AGENT_WEB;
        switch (mBlogData.getBlogId()) {
            case Config.BLOG_ID_SKE48_MEMBER:
                userAgent = Config.USER_AGENT_MOBILE;
                break;
        }
        requestData(url, userAgent);
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
                //parseData(url, "");
                alertAndFinish(mErrorMessage);
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
        if (response.isEmpty()) {
            alertAndFinish(mErrorMessage);
        } else {
            switch (mSiteData.getId()) {
                case Config.BLOG_ID_SKE48_MEMBER:
                    Ske48Parser ske48Parser = new Ske48Parser();
                    ske48Parser.parseMobileBlogArticleList(response, mWebData);
                    renderPhoto();
                    break;
            }
        }
    }

    private void renderPhoto() {
        String imageUrl = mWebData.getImageUrl();

        if (imageUrl == null || imageUrl.isEmpty()) {
            renderData();
        } else {
            String[] imageArray = imageUrl.split("\\*");

            LinearLayout loPicture = (LinearLayout) findViewById(R.id.loPicture);
            loPicture.setVisibility(View.VISIBLE);

            float density = mContext.getResources().getDisplayMetrics().density;
            int height = (int) (272 * density);
            int margin = (int) (1 * density);
            LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
            mParams.setMargins(0, 0, margin, 0);
            LinearLayout.LayoutParams mParamsNoMargin = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);

            for (int i = 0; i < imageArray.length; i++) {
                //Log.e(mTag, "url[" + i + "]: " + imageArray[i]);

                String url = imageArray[i];
                if (url.isEmpty()) {
                    continue;
                }

                ProportionalImageView iv = new ProportionalImageView(mContext);
                if (i == imageArray.length - 1) {
                    iv.setLayoutParams(mParamsNoMargin);
                } else {
                    iv.setLayoutParams(mParams);
                }
                //iv.setAdjustViewBounds(true);
                //iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                loPicture.addView(iv);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goWebSite();
                    }
                });

                Picasso.with(mContext).load(url).into(iv, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        renderData();
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        }
    }

    private void renderData() {
        if (!mIsFirst) {
            return;
        }

        mIsFirst = false;

        mLoLoading.setVisibility(View.GONE);
        LinearLayout loItem = (LinearLayout) findViewById(R.id.loItem);
        loItem.setVisibility(View.VISIBLE);

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(mWebData.getTitle());

        TextView tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(mBlogData.getName());
        tvName.setVisibility(View.VISIBLE);

        TextView tvDate = (TextView) findViewById(R.id.tvDate);
        tvDate.setText(mWebData.getDate());

        TextView tvContent = (TextView) findViewById(R.id.tvContent);
        tvContent.setText(mWebData.getContent());
        tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goWebSite();
            }
        });

        Button btMore = (Button) findViewById(R.id.btMore);
        btMore.setVisibility(View.VISIBLE);
        btMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goWebSite();
            }
        });
    }

    public void goWebSite() {
        //Log.e(mTag, "onContentClick(" + position + ")");

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBlogData.getUrl()));
        //startActivity(intent);
        startActivityForResult(intent, 100);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //hideToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
