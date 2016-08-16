package com.summertaker.akb48guide.rawphoto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import com.summertaker.akb48guide.common.CacheManager;
import com.summertaker.akb48guide.common.BaseActivity;
import com.summertaker.akb48guide.common.BaseApplication;
import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.data.MemberData;
import com.summertaker.akb48guide.data.WebData;
import com.summertaker.akb48guide.parser.Akb48ShopParser;
import com.summertaker.akb48guide.parser.BaseParser;
import com.summertaker.akb48guide.parser.ChibakanParser;
import com.summertaker.akb48guide.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class RawPhotoSelectActivity extends BaseActivity {

    GroupData mGroupData;

    String mTitle;

    ArrayList<WebData> mWebDataList = new ArrayList<>();
    ArrayList<MemberData> mMemberDataList = new ArrayList<>();

    RawPhotoSelectAdapter mAdapter;

    LinearLayout mLoLoading;

    CacheManager mCacheManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.raw_photo_select_activity);

        mContext = RawPhotoSelectActivity.this;
        mResources = mContext.getResources();

        Intent intent = getIntent();
        mGroupData = (GroupData) intent.getSerializableExtra("groupData");
        //Log.e(mTag, "id: " + mSiteData.getId());

        mTitle = getString(R.string.raw_photo) + " / " + mGroupData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, mTitle);

        mLoLoading = (LinearLayout) findViewById(R.id.loLoading);
        ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(pbLoading, Config.PROGRESS_BAR_COLOR_LIGHT, null);

        mCacheManager = new CacheManager(mContext);

        String url = mGroupData.getRawPhotoUrl();

        if (url.contains(Config.AKB48_GROUP_SHOP_DOMAIN)) {
            requestData(url, Config.USER_AGENT_MOBILE);
            //mRetrofit = new Retrofit.Builder().baseUrl(RetroApi.BASE_URL_AKB48_SHOP).build();
            //mRetroApi = mRetrofit.create(RetroApi.class);
            //requestRetro(url, Config.USER_AGENT_MOBILE);
        } else {
            requestData(url, Config.USER_AGENT_WEB);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mGroupData.getRawPhotoUrl().contains(Config.AKB48_GROUP_SHOP_DOMAIN)) {
            getMenuInflater().inflate(R.menu.raw_photo_select, menu);
        }
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

        if (url.equals(mGroupData.getRawPhotoUrl())) {

            if (url.contains(Config.AKB48_GROUP_SHOP_DOMAIN)) {
                // AKB48 그룹 공식 샵인 경우
                Akb48ShopParser akb48ShopParser = new Akb48ShopParser();
                akb48ShopParser.parseMobileIndex(response, mWebDataList);
                renderData();
            } else {
                ChibakanParser chibakanParser = new ChibakanParser();
                chibakanParser.parseIndex(response, mGroupData, mWebDataList);

                // 멤버사진 정보를 공식사이트에서 가져오기
                String memberUrl = mGroupData.getUrl();
                String userAgent = Config.USER_AGENT_WEB;
                requestData(memberUrl, userAgent);
            }
        }

        if (url.equals(mGroupData.getUrl())) {
            // 멤버사진 정보 파싱하기
            BaseParser baseParser = new BaseParser();
            baseParser.parseMemberList(response, mGroupData, mMemberDataList, null, false);

            updateData();
            renderData();
        }
    }

    private void updateData() {
        for (WebData webData : mWebDataList) {
            for (MemberData memberData : mMemberDataList) {
                String name = Util.removeSpace(memberData.getName());
                if (name.equals(webData.getName())) {
                    //Log.e(mTag, memberData.getName() + " " + memberData.getThumbnailUrl());
                    webData.setImageUrl(memberData.getThumbnailUrl());
                    break;
                }
            }
        }
    }

    private void requestRetro(final String url, final String userAgent) {

        // http://shopping.akb48-group.com/products/list.php?akb48&category_id=1841
        String[] array = url.split("category_id=");
        String categoryId = array[1];

        switch (mGroupData.getId()) {
            case Config.GROUP_ID_AKB48:
                mRetroCall = mRetroApi.getRawPhotoAkb48(userAgent, categoryId, "");
                break;
            case Config.GROUP_ID_SKE48:
                mRetroCall = mRetroApi.getRawPhotoSke48(userAgent, categoryId, "");
                break;
            case Config.GROUP_ID_NMB48:
                mRetroCall = mRetroApi.getRawPhotoNmb48(userAgent, categoryId, "");
                break;
            case Config.GROUP_ID_HKT48:
                mRetroCall = mRetroApi.getRawPhotoHkt48(userAgent, categoryId, "");
                break;
        }
        mRetroCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                //Log.e(mTag, "onResponse: " + call.request().url().toString());
                try {
                    String html = response.body().string();
                    parseRetro(url, html);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(mTag, "onResponse: " + e.getMessage());
                    alertNetworkErrorAndFinish(e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(mTag, "onFailure: " + call.request().url().toString());
                Log.e(mTag, "onFailure: " + t.getMessage());
                alertNetworkErrorAndFinish(t.getMessage());
            }
        });
    }

    private void parseRetro(String url, String response) {
        Akb48ShopParser akb48ShopParser = new Akb48ShopParser();
        akb48ShopParser.parseMobileIndex(response, mWebDataList);

        renderData();
    }

    private void renderData() {
        mLoLoading.setVisibility(View.GONE);

        if (mWebDataList.size() == 0) {
            alertAndFinish(mErrorMessage);
        } else {
            GridView gridView = (GridView) findViewById(R.id.gridView);
            if (gridView != null) {
                if (mGroupData.getId().equals(Config.GROUP_ID_AKB48) &&
                        !mGroupData.getRawPhotoUrl().contains(Config.AKB48_GROUP_SHOP_DOMAIN)) {
                    gridView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                }
                gridView.setVisibility(View.VISIBLE);

                String title = mTitle + " (" + mWebDataList.size() + ")";
                mBaseToolbar.setTitle(title);

                mAdapter = new RawPhotoSelectAdapter(this, mGroupData, mWebDataList);
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
        Intent intent;
        if (mGroupData.getRawPhotoUrl().contains(Config.AKB48_GROUP_SHOP_DOMAIN)) {
            intent = new Intent(mContext, RawPhotoOfficialListActivity.class);
        } else {
            intent = new Intent(mContext, RawPhotoListActivity.class);
        }
        intent.putExtra("groupData", mGroupData);
        intent.putExtra("webData", webData);

        startActivityForResult(intent, 100);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goWebSite() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mGroupData.getRawPhotoUrl()));
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
