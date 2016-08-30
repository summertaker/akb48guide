package com.summertaker.akb48guide.janken;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.summertaker.akb48guide.data.MemberData;
import com.summertaker.akb48guide.data.TeamData;
import com.summertaker.akb48guide.parser.Akb48Parser;
import com.summertaker.akb48guide.parser.BaseParser;
import com.summertaker.akb48guide.parser.NamuwikiParser;
import com.summertaker.akb48guide.parser.WikipediaEnParser;
import com.summertaker.akb48guide.util.Typefaces;
import com.summertaker.akb48guide.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JankenMainActivity extends BaseActivity {

    ProgressBar mPbLoading;

    String mAction;
    GroupData mGroupData;
    boolean mIsMobile = false;
    ArrayList<MemberData> mGroupMemberList = new ArrayList<>();
    ArrayList<MemberData> mWikiMemberList = new ArrayList<>();
    ArrayList<TeamData> mTeamDataList = new ArrayList<>();

    String mLocale;
    BaseParser mWikiParser;

    boolean mIsDataLoaded = false;
    boolean mIsWikiLoaded = false;
    boolean mIsProcessing = true;
    boolean mIsRenderFinished = false;

    boolean mIsFirst = true;
    int mMemberCount = 0;
    String mPictureUrl;

    CardView mCvPicture;
    float mCvPictureX;
    float mCvPictureY;

    CardView mCvRemainPicture;
    ProgressBar mPbRemainPictureLoading;
    ImageView mIvRemainPicture;
    TextView mTvRemainCounterText;

    RelativeLayout mLoMemberPictureLoading;
    ProgressBar mPbMemberPictureLoading;
    ImageView mIvMemberPicture;
    TextView mTvMemberPictureCaption;
    TextView mTvMemberActionIcon;

    int mReadyCounterValue = 3;

    ArrayList<ImageView> mMyMemberImageViews = new ArrayList<>();
    LinearLayout.LayoutParams mParams;
    LinearLayout.LayoutParams mParamsNoMargin;

    LinearLayout mLoMyMemberList;
    RelativeLayout mLoMyMemberCounter;
    TextView mTvMyMemberCounterText;
    RelativeLayout mLoCounter;
    TextView mCounterText;

    RelativeLayout mLoScreen;
    Button mBtnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.janken_main_activity);

        mContext = JankenMainActivity.this;

        Intent intent = getIntent();
        mAction = intent.getStringExtra("action");
        mGroupData = (GroupData) intent.getSerializableExtra("groupData");

        String title = getString(R.string.rock_paper_scissors) + " / " + mGroupData.getName();
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, title);

        mPbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        Util.setProgressBarColor(mPbLoading, Config.PROGRESS_BAR_COLOR_LIGHT, null);

        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.janken_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Intent intent = new Intent(this, JankenHitActivity.class);
        //startActivity(intent);
        return true;
    }

    private void loadData() {
        String url = mGroupData.getUrl();
        String userAgent = Config.USER_AGENT_WEB;

        switch (mGroupData.getId()) {
            case Config.GROUP_ID_AKB48:
                url = mGroupData.getMobileUrl();
                userAgent = Config.USER_AGENT_MOBILE;
                mIsMobile = true;
                break;
        }
        requestData(url, userAgent);

        mLocale = Util.getLocaleStrng(mContext);
        switch (mLocale) {
            case "KR":
                mWikiParser = new NamuwikiParser();
                break;
            default:
                mWikiParser = new WikipediaEnParser();
                break;
        }
        String mWikiUrl = mWikiParser.getUrl(mGroupData.getId());
        if (mWikiUrl == null || mWikiUrl.isEmpty()) {
            mIsWikiLoaded = true;
        } else {
            requestData(mWikiUrl, Config.USER_AGENT_WEB);
        }
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
    }

    private void parseData(String url, String response) {
        if (url.contains("wiki")) {
            mWikiParser.parse48List(response, mGroupData, mWikiMemberList);
            mIsWikiLoaded = true;
        } else {
            switch (mGroupData.getId()) {
                case Config.GROUP_ID_AKB48:
                    Akb48Parser akb48Parser = new Akb48Parser();
                    akb48Parser.parseMobileMemberAll(response, mGroupData, mGroupMemberList);
                    break;
                default:
                    BaseParser baseParser = new BaseParser();
                    baseParser.parseMemberList(response, mGroupData, mGroupMemberList, mTeamDataList, mIsMobile);
                    break;
            }
            mIsDataLoaded = true;
        }

        renderData();
    }

    private void renderData() {
        if (!mIsWikiLoaded || !mIsDataLoaded) {
            return;
        }

        if (mGroupMemberList.size() == 0) {
            alertNetworkErrorAndFinish(mErrorMessage);
        } else {
            Collections.shuffle(mGroupMemberList);
            for (MemberData memberData : mGroupMemberList) {
                if (mWikiMemberList.size() == 0) {
                    memberData.setLocaleName(memberData.getName()); //memberData.getNameEn();
                } else {
                    for (MemberData wikiData : mWikiMemberList) {
                        //Log.e(mTag, memberData.getNoSpaceName() + " = " + wikiData.getNoSpaceName());
                        if (Util.isEqualString(memberData.getNoSpaceName(), wikiData.getNoSpaceName())) {
                            String localeName;
                            switch (mLocale) {
                                case "KR":
                                    localeName = wikiData.getNameKo();
                                    break;
                                default:
                                    localeName = wikiData.getNameEn();
                                    break;
                            }
                            if (localeName == null || localeName.isEmpty()) {
                                localeName = memberData.getName();
                            }
                            //Log.e(mTag, "localeName: " + localeName);
                            memberData.setLocaleName(localeName);
                            break;
                        } else {
                            memberData.setLocaleName(memberData.getName()); //memberData.getNameEn();
                        }
                    }
                }
            }
            initUi();
        }
    }

    private void initUi() {
        RelativeLayout loLoading = (RelativeLayout) findViewById(R.id.loLoading);
        loLoading.setVisibility(View.GONE);

        FrameLayout loContainer = (FrameLayout) findViewById(R.id.loContainer);
        loContainer.setVisibility(View.VISIBLE);

        // http://stackoverflow.com/questions/15210548/how-to-use-a-icons-and-symbols-from-font-awesome-on-native-android-application
        //Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf" );
        Typeface font = Typefaces.get(mContext, "fontawesome-webfont.ttf");

        mCvRemainPicture = (CardView) findViewById(R.id.cvRemainPicture);
        mPbRemainPictureLoading = (ProgressBar) findViewById(R.id.pbRemainPictureLoading);
        //Util.setProgressBarColor(mPbRemainPictureLoading, Config.PROGRESS_BAR_COLOR_LIGHT, null);
        mIvRemainPicture = (ImageView) findViewById(R.id.ivRemainPicture);
        mTvRemainCounterText = (TextView) findViewById(R.id.tvRemainCounterText);
        TextView tvRemainCounterIcon = (TextView) findViewById(R.id.tvRemainCounterIcon);
        tvRemainCounterIcon.setTypeface(font);

        mCvPicture = (CardView) findViewById(R.id.cvMemberPicture);
        mCvPicture.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Layout has happened here.
                        mCvPictureX = mCvPicture.getX();
                        mCvPictureY = mCvPicture.getY();
                        //Log.e(mTag, mCvPictureX + ", " + mCvPictureY);
                        mIsRenderFinished = true;
                        // Don't forget to remove your listener when you are done with it.
                        mCvPicture.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
        mLoMemberPictureLoading = (RelativeLayout) findViewById(R.id.loMemberPictureLoading);
        mPbMemberPictureLoading = (ProgressBar) findViewById(R.id.pbMemberPictureLoading);
        Util.setProgressBarColor(mPbMemberPictureLoading, Config.PROGRESS_BAR_COLOR_LIGHT, null);
        mIvMemberPicture = (ImageView) findViewById(R.id.ivMemberPicture);
        mTvMemberPictureCaption = (TextView) findViewById(R.id.tvMemberPictureCaption);
        mTvMemberActionIcon = (TextView) findViewById(R.id.tvMemberActionIcon);
        mTvMemberActionIcon.setTypeface(font);

        mLoCounter = (RelativeLayout) findViewById(R.id.loCounter);
        mCounterText = (TextView) findViewById(R.id.tvCounterText);
        mCounterText.setText(String.valueOf(mReadyCounterValue));

        TextView tvCounterBorder = (TextView) findViewById(R.id.tvCounterBorder);
        tvCounterBorder.setTypeface(font);
        TextView tvCounterOuter = (TextView) findViewById(R.id.tvCounterOuter);
        tvCounterOuter.setTypeface(font);
        TextView tvCounterInner = (TextView) findViewById(R.id.tvCounterInner);
        tvCounterInner.setTypeface(font);

        mLoMyMemberCounter = (RelativeLayout) findViewById(R.id.loMyMemberCounter);
        mTvMyMemberCounterText = (TextView) findViewById(R.id.tvMyMemberCounterText);
        mLoMyMemberList = (LinearLayout) findViewById(R.id.loMyMemberList);
        TextView tvMyMemberCounterIcon = (TextView) findViewById(R.id.tvMyMemberCounterIcon);
        tvMyMemberCounterIcon.setTypeface(font);

        float density = mContext.getResources().getDisplayMetrics().density;
        int width = (int) (47 * density);
        int height = (int) (60 * density);
        int margin = (int) (6 * density);
        mParams = new LinearLayout.LayoutParams(width, height);
        mParams.setMargins(0, 0, margin, 0);
        mParamsNoMargin = new LinearLayout.LayoutParams(width, height);

        LinearLayout loScissors = (LinearLayout) findViewById(R.id.loScissors);
        loScissors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.accent));
                if (mIsRenderFinished && !mIsProcessing) {
                    mIsProcessing = true;
                    doWin();
                }
            }
        });
        TextView tvScissorsIcon = (TextView) findViewById(R.id.tvScissorsIcon);
        tvScissorsIcon.setTypeface(font);

        LinearLayout loRock = (LinearLayout) findViewById(R.id.loRock);
        loRock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsRenderFinished && !mIsProcessing) {
                    mIsProcessing = true;
                    doLose();
                }
            }
        });
        TextView tvRockIcon = (TextView) findViewById(R.id.tvRockIcon);
        tvRockIcon.setTypeface(font);

        LinearLayout loPaper = (LinearLayout) findViewById(R.id.loPaper);
        loPaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsRenderFinished && !mIsProcessing) {
                    mIsProcessing = true;
                }
            }
        });
        TextView tvPaperIcon = (TextView) findViewById(R.id.tvPaperIcon);
        tvPaperIcon.setTypeface(font);

        mLoScreen = (RelativeLayout) findViewById(R.id.loScreen);
        mBtnStart = (Button) findViewById(R.id.btnStart);
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsRenderFinished) {
                    //mIsProcessing = true;
                    startGame();
                }
            }
        });

        loadMatchMember();
    }

    private void loadMatchMember() {
        MemberData memberData = mGroupMemberList.get(mMemberCount);
        mPictureUrl = memberData.getImageUrl();
        if (mPictureUrl == null || mPictureUrl.isEmpty()) {
            mPictureUrl = memberData.getThumbnailUrl();
        }

        mTvMemberPictureCaption.setText(memberData.getLocaleName());

        mLoMemberPictureLoading.setVisibility(View.VISIBLE);
        mIvMemberPicture.setVisibility(View.GONE);
        Picasso.with(mContext).load(mPictureUrl).into(mIvMemberPicture, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                //mLoMemberPictureLoading.setVisibility(View.GONE);
                mPbMemberPictureLoading.setVisibility(View.GONE);
                mIvMemberPicture.setVisibility(View.VISIBLE);
                //loadNextMember();

                animateMatchMember();
            }

            @Override
            public void onError() {
                mLoMemberPictureLoading.setVisibility(View.GONE);
            }
        });

    }

    private void animateMatchMember() {
        mMemberCount++;
        if (mMemberCount == mGroupMemberList.size()) {
            return;
        }

        if (mIsFirst) {

            mIsFirst = false;
        } else {
            mCvPicture.animate().x(mCvPictureX).y(-1200f).scaleX(1f).scaleY(1f).setDuration(0).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    //Log.e(mTag, "End..........");
                    mCvPicture.animate().setListener(null);
                    mCvPicture.animate().y(mCvPictureY).setDuration(500);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
        loadNextMember();
    }

    private void loadNextMember() {
        MemberData remainMemberData = mGroupMemberList.get(mMemberCount);
        String imageUrl = remainMemberData.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = remainMemberData.getThumbnailUrl();
        }

        mPbRemainPictureLoading.setVisibility(View.VISIBLE);
        mIvRemainPicture.setVisibility(View.GONE);
        Picasso.with(mContext).load(imageUrl).into(mIvRemainPicture, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                mPbRemainPictureLoading.setVisibility(View.GONE);
                mIvRemainPicture.setVisibility(View.VISIBLE);

                String text = (mGroupMemberList.size() - mMemberCount) + "";
                mTvRemainCounterText.setText(text);

                mLoScreen.setVisibility(View.VISIBLE);
                mBtnStart.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
                mPbRemainPictureLoading.setVisibility(View.GONE);
            }
        });
    }

    private void startGame() {
        mBtnStart.animate().scaleX(0.5f).scaleY(0.5f).alpha(0f).setDuration(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mLoScreen.setVisibility(View.GONE);
                mBtnStart.setVisibility(View.GONE);
                mBtnStart.animate().scaleX(1f).scaleY(1f).alpha(100f).setDuration(0).setListener(null);
                runCounter();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void runCounter() {
        mLoCounter.setVisibility(View.VISIBLE);
        mLoCounter.animate().scaleX(1.5f).scaleY(1.5f).alpha(0f).setDuration(1000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mLoCounter.animate().setListener(null);
                if (mReadyCounterValue == 1) {
                    mReadyCounterValue = 3;
                    mCounterText.setText(String.valueOf(mReadyCounterValue));
                    mLoCounter.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(0);
                    mLoCounter.setVisibility(View.GONE);

                    mIsProcessing = false;

                    doJudge();
                } else {
                    mLoCounter.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(0).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            mLoCounter.animate().setListener(null);
                            mReadyCounterValue--;
                            mCounterText.setText(String.valueOf(mReadyCounterValue));
                            runCounter();
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void doJudge() {
        mIvMemberPicture.setVisibility(View.GONE);
        mTvMemberActionIcon.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mIvMemberPicture.setVisibility(View.VISIBLE);
                mTvMemberActionIcon.setVisibility(View.GONE);
                doWin();
            }
        }, 1500);
    }

    private void doWin() {

        // Log.e(mTag, "mCvPictureX: " + mCvPictureX + ", mCvPictureY: " + mCvPictureY);

        mCvPicture.animate().setDuration(500).x(-300f).y(1250f).scaleXBy(-1f).scaleYBy(-1f).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //Log.e(mTag, "End..........");
                addMyMember();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void addMyMember() {
        ImageView iv = new ImageView(mContext);
        //if (mMyMemberImageViews.size() == 0) {
        //    iv.setLayoutParams(mParamsNoMargin);
        //} else {
        iv.setLayoutParams(mParams);
        //}
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mLoMyMemberList.addView(iv, 0);
        mMyMemberImageViews.add(iv);

        Picasso.with(mContext).load(mPictureUrl).into(iv, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                mLoMyMemberCounter.setVisibility(View.VISIBLE);
                String text = mMyMemberImageViews.size() + "";
                if (mMyMemberImageViews.size() > 0) {
                    mTvMyMemberCounterText.setText(text);
                }
                loadMatchMember();
            }

            @Override
            public void onError() {

            }
        });
    }

    private void doLose() {
        /*float dest = 360;
        if (mCvPicture.getRotation() == 360) {
            dest = 0;
        }
        ObjectAnimator animation1 = ObjectAnimator.ofFloat(mCvPicture, "rotation", dest);
        animation1.setDuration(1000);
        animation1.start();*/

        mCvPicture.animate().scaleXBy(0.1f).scaleYBy(0.1f).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mCvPicture.animate().setListener(null);
                mCvPicture.animate().scaleX(1f).scaleY(1f).setDuration(700);
                removeMyMember();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void removeMyMember() {
        final int index = mMyMemberImageViews.size() - 1;
        if (index >= 0) {
            final ImageView iv = mMyMemberImageViews.get(index);
            iv.animate().scaleX(0.5f).scaleY(0.5f).alpha(0f).setDuration(700).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    //Log.e(mTag, "End..........");
                    mLoMyMemberList.removeView(iv);
                    mMyMemberImageViews.remove(index);

                    String text = mMyMemberImageViews.size() + "";
                    mTvMyMemberCounterText.setText(text);
                    if (mMyMemberImageViews.size() == 0) {
                        mLoMyMemberCounter.setVisibility(View.GONE);
                    }

                    //mIsProcessing = false;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
    }
}
