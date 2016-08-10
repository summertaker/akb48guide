package com.summertaker.akb48guide.rawphoto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.data.WebData;
import com.summertaker.akb48guide.parser.Akb48ShopParser;
import com.summertaker.akb48guide.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RawPhotoDetailActivity extends BaseActivity {

    GroupData mGroupData;
    WebData mWebData;

    String mTitle;

    ArrayList<WebData> mWebDataList = new ArrayList<>();

    LinearLayout mLoLoading;

    String mUserAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.raw_photo_detail_activity);

        mContext = RawPhotoDetailActivity.this;
        mResources = mContext.getResources();

        Intent intent = getIntent();
        mGroupData = (GroupData) intent.getSerializableExtra("groupData");
        mWebData = (WebData) intent.getSerializableExtra("webData");
        //Log.e(mTag, "id: " + mSiteData.getId());

        mTitle = getString(R.string.raw_photo);
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(mWebData.getName());
        //tvTitle.setBackgroundColor(ContextCompat.getColor(mContext, R.color.akb48background));

        mLoLoading = (LinearLayout) findViewById(R.id.loLoading);
        ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(pbLoading, Config.PROGRESS_BAR_COLOR_LIGHT, null);

        String url = mWebData.getUrl();
        mUserAgent = Config.USER_AGENT_WEB;
        if (mGroupData.getRawPhotoUrl().contains(Config.AKB48_GROUP_SHOP_DOMAIN)) {
            mUserAgent = Config.USER_AGENT_MOBILE;
        }
        requestData(url, mUserAgent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.raw_photo_detail, menu);
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
        if (response.isEmpty()) {
            alertAndFinish(getString(R.string.data_not_found));
        } else {
            Akb48ShopParser akb48ShopParser = new Akb48ShopParser();
            String msg = akb48ShopParser.parseMobileDetail(response, mWebDataList);
            renderData(msg);
        }
    }

    private void renderData(String msg) {
        mLoLoading.setVisibility(View.GONE);

        if (!msg.isEmpty()) {
            LinearLayout loMsg = (LinearLayout) findViewById(R.id.loMsg);
            loMsg.setVisibility(View.VISIBLE);
            TextView tvMsg = (TextView) findViewById(R.id.tvMsg);
            tvMsg.setText(msg);
        } else if (mWebDataList.size() == 0) {
            alertAndFinish(getString(R.string.data_not_found));
        } else {
            mLoLoading.setVisibility(View.GONE);

            LinearLayout loPicture = (LinearLayout) findViewById(R.id.loPicture);
            loPicture.setVisibility(View.VISIBLE);

            float density = mContext.getResources().getDisplayMetrics().density;

            int height = (int) (512 * density);
            int margin = (int) (1 * density);

            LinearLayout.LayoutParams parmas = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
            parmas.setMargins(0, margin, 0, 0);
            LinearLayout.LayoutParams paramsNoMargin = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);

            int count = 0;
            for (WebData webData : mWebDataList) {
                //Log.e(mTag, webData.getImageUrl());

                ImageView iv = new ImageView(mContext);
                if (count == 0) {
                    iv.setLayoutParams(paramsNoMargin);
                } else {
                    iv.setLayoutParams(parmas);
                }
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goWebSite();
                    }
                });
                loPicture.addView(iv);

                Picasso.with(mContext).load(webData.getImageUrl()).into(iv);
                count++;
            }

            /*int height = (int) (272 * density);
            int margin = (int) (30 * density);

            LinearLayout.LayoutParams parmas = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            parmas.setMargins(0, margin, 0, 0);
            LinearLayout.LayoutParams paramsNoMargin = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            mLoLoading.setVisibility(View.GONE);

            int count = 0;
            for (WebData webData : mWebDataList) {
                //Log.e(mTag, webData.getImageUrl());

                ProportionalImageView iv = new ProportionalImageView(mContext);
                if (count == 0) {
                    iv.setLayoutParams(paramsNoMargin);
                } else {
                    iv.setLayoutParams(parmas);
                }
                loPicture.addView(iv);

                Picasso.with(mContext).load(webData.getImageUrl()).into(iv);
                count++;
            }*/
        }
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
