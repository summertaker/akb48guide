package com.summertaker.akb48guide.janken;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
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

    boolean mIsFirstLoading = true;
    int mMemberIndex = 0;
    String mPictureUrl;

    CardView mCvNextMember;
    float mNextMemberX;
    float mNextMemberY;
    boolean mNextMemberRendered = false;
    ProgressBar mPbNextMemberPictureLoading;
    ImageView mIvNextMemberPicture;
    TextView mTvRemainMemberCounterText;

    CardView mCvMatchMember;
    float mMatchMemberX;
    float mMatchMemberY;
    boolean mMatchMemberRendered = false;

    RelativeLayout mLoMatchMemberPictureLoading;
    ProgressBar mPbMatchMemberPictureLoading;
    ImageView mIvMatchMemberPicture;
    TextView mTvMatchMemberPictureCaption;
    TextView mTvMatchMemberActionIcon;

    int mReadyCounterValue = 3;

    ArrayList<ImageView> mMyMemberImageViews = new ArrayList<>();
    LinearLayout.LayoutParams mParams;
    LinearLayout.LayoutParams mParamsNoMargin;

    LinearLayout mLoMyMemberList;
    RelativeLayout mLoMyMemberCounter;
    TextView mTvMyMemberCounterText;

    LinearLayout mLoUserAction;
    float mUserActionY;
    boolean mUserActionRendered = false;

    RelativeLayout mLoReadyCounter;
    TextView mReadyCounterText;

    RelativeLayout mLoGuide;
    RelativeLayout mLoReadyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.janken_main_activity);

        mContext = JankenMainActivity.this;

        Intent intent = getIntent();
        mAction = intent.getStringExtra("action");
        mGroupData = (GroupData) intent.getSerializableExtra("groupData");

        //String title = getString(R.string.rock_paper_scissors) + " / " + mGroupData.getName();
        //initBaseToolbar(Config.TOOLBAR_ICON_BACK, title);

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

        mCvNextMember = (CardView) findViewById(R.id.cvNextMember);
        mCvNextMember.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mNextMemberX = mCvNextMember.getX();
                        mNextMemberY = mCvNextMember.getY();
                        mCvNextMember.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mNextMemberRendered = true;
                        onRenderFinished();
                    }
                });
        mPbNextMemberPictureLoading = (ProgressBar) findViewById(R.id.pbNextMemberPictureLoading);
        //Util.setProgressBarColor(mPbNextMemberPictureLoading, Config.PROGRESS_BAR_COLOR_LIGHT, null);
        mIvNextMemberPicture = (ImageView) findViewById(R.id.ivNextMemberPicture);

        mTvRemainMemberCounterText = (TextView) findViewById(R.id.tvRemainMemberCounterText);
        TextView tvRemainMemberCounterIcon = (TextView) findViewById(R.id.tvRemainMemberCounterIcon);
        tvRemainMemberCounterIcon.setTypeface(font);
        String text = (mGroupMemberList.size() - mMemberIndex) + "";
        mTvRemainMemberCounterText.setText(text);

        mCvMatchMember = (CardView) findViewById(R.id.cvMatchMember);
        mCvMatchMember.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mMatchMemberX = mCvMatchMember.getX();
                        mMatchMemberY = mCvMatchMember.getY();
                        mCvMatchMember.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mMatchMemberRendered = true;
                        onRenderFinished();
                    }
                });
        mLoMatchMemberPictureLoading = (RelativeLayout) findViewById(R.id.loMatchMemberPictureLoading);
        mPbMatchMemberPictureLoading = (ProgressBar) findViewById(R.id.pbMatchMemberPictureLoading);
        Util.setProgressBarColor(mPbMatchMemberPictureLoading, Config.PROGRESS_BAR_COLOR_LIGHT, null);
        mIvMatchMemberPicture = (ImageView) findViewById(R.id.ivMatchMemberPicture);
        mTvMatchMemberPictureCaption = (TextView) findViewById(R.id.tvMatchMemberPictureCaption);
        mTvMatchMemberActionIcon = (TextView) findViewById(R.id.tvMatchMemberActionIcon);
        mTvMatchMemberActionIcon.setTypeface(font);

        mLoReadyCounter = (RelativeLayout) findViewById(R.id.loReadyCounter);
        mReadyCounterText = (TextView) findViewById(R.id.tvReadyCounterText);
        mReadyCounterText.setText(String.valueOf(mReadyCounterValue));
        TextView tvStartCounterBorder = (TextView) findViewById(R.id.tvReadyCounterBorder);
        tvStartCounterBorder.setTypeface(font);
        TextView tvStartCounterOuter = (TextView) findViewById(R.id.tvReadyCounterOuter);
        tvStartCounterOuter.setTypeface(font);
        TextView tvStartCounterInner = (TextView) findViewById(R.id.tvReadyCounterInner);
        tvStartCounterInner.setTypeface(font);

        float density = mContext.getResources().getDisplayMetrics().density;
        int width = (int) (47 * density);
        int height = (int) (60 * density);
        int margin = (int) (6 * density);
        mParams = new LinearLayout.LayoutParams(width, height);
        mParams.setMargins(0, 0, margin, 0);
        mParamsNoMargin = new LinearLayout.LayoutParams(width, height);

        mLoMyMemberCounter = (RelativeLayout) findViewById(R.id.loMyMemberCounter);
        mTvMyMemberCounterText = (TextView) findViewById(R.id.tvMyMemberCounterText);
        mLoMyMemberList = (LinearLayout) findViewById(R.id.loMyMemberList);
        TextView tvMyMemberCounterIcon = (TextView) findViewById(R.id.tvMyMemberCounterIcon);
        tvMyMemberCounterIcon.setTypeface(font);

        mLoUserAction = (LinearLayout) findViewById(R.id.loUserAction);
        mLoUserAction.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mUserActionY = mLoUserAction.getY();
                        mLoUserAction.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mUserActionRendered = true;
                        onRenderFinished();
                    }
                });

        LinearLayout loUserActionScissors = (LinearLayout) findViewById(R.id.loUserActionScissors);
        loUserActionScissors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.accent));
                if (!mIsProcessing) {
                    mIsProcessing = true;
                    doWin();
                }
            }
        });
        TextView tvUserActionScissorsIcon = (TextView) findViewById(R.id.tvUserActionScissorsIcon);
        tvUserActionScissorsIcon.setTypeface(font);

        LinearLayout loUserActionRock = (LinearLayout) findViewById(R.id.loUserActionRock);
        loUserActionRock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsProcessing) {
                    mIsProcessing = true;
                    doLose();
                }
            }
        });
        TextView tvUserActionRockIcon = (TextView) findViewById(R.id.tvUserActionRockIcon);
        tvUserActionRockIcon.setTypeface(font);

        LinearLayout loUserActionPaper = (LinearLayout) findViewById(R.id.loUserActionPaper);
        loUserActionPaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsProcessing) {
                    mIsProcessing = true;
                }
            }
        });
        TextView tvUserActionPaperIcon = (TextView) findViewById(R.id.tvUserActionPaperIcon);
        tvUserActionPaperIcon.setTypeface(font);

        mLoGuide = (RelativeLayout) findViewById(R.id.loGuide);
        mLoReadyMessage = (RelativeLayout) findViewById(R.id.loReadyMessage);
    }

    private void onRenderFinished() {
        if (mNextMemberRendered && mMatchMemberRendered && mUserActionRendered) {
            //mMatchMemberLoaded = false;
            //loadNextMember();
            loadMatchMember();
        }
    }

    private void loadMatchMember() {
        if (mMemberIndex == mGroupMemberList.size() - 1) {
            return;
        }

        MemberData memberData = mGroupMemberList.get(mMemberIndex);
        mPictureUrl = memberData.getImageUrl();
        if (mPictureUrl == null || mPictureUrl.isEmpty()) {
            mPictureUrl = memberData.getThumbnailUrl();
        }

        mTvMatchMemberPictureCaption.setText(memberData.getLocaleName());
        mLoMatchMemberPictureLoading.setVisibility(View.VISIBLE);
        mIvMatchMemberPicture.setVisibility(View.GONE);
        Picasso.with(mContext).load(mPictureUrl).into(mIvMatchMemberPicture, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                mPbMatchMemberPictureLoading.setVisibility(View.GONE);
                mIvMatchMemberPicture.setVisibility(View.VISIBLE);
                //animateMatchMember();
                mMemberIndex++;

                if (mIsFirstLoading) {
                    loadNextMember();
                } else {
                    animateMatchMember();
                }
            }

            @Override
            public void onError() {
                mLoMatchMemberPictureLoading.setVisibility(View.GONE);
            }
        });
    }

    private void loadNextMember() {
        if (mMemberIndex == mGroupMemberList.size() - 1) {
            mPbNextMemberPictureLoading.setVisibility(View.GONE);
            mIvNextMemberPicture.setVisibility(View.GONE);
            return;
        }

        MemberData memberData = mGroupMemberList.get(mMemberIndex);
        String imageUrl = memberData.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = memberData.getThumbnailUrl();
        }

        mPbNextMemberPictureLoading.setVisibility(View.VISIBLE);
        mIvNextMemberPicture.setVisibility(View.GONE);

        Picasso.with(mContext).load(imageUrl).into(mIvNextMemberPicture, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                mPbNextMemberPictureLoading.setVisibility(View.GONE);
                mIvNextMemberPicture.setVisibility(View.VISIBLE);

                String text = (mGroupMemberList.size() - mMemberIndex) + "";
                mTvRemainMemberCounterText.setText(text);

                if (mIsFirstLoading) {
                    Log.e(mTag, "a");
                    initUserActionBar();
                } else {
                    Log.e(mTag, "b");
                    setReady();
                }
            }

            @Override
            public void onError() {
                mPbNextMemberPictureLoading.setVisibility(View.GONE);
            }
        });
    }

    private void animateMatchMember() {
        float x = mNextMemberX - 260;
        float y = mNextMemberY - 290;

        mCvMatchMember.setVisibility(View.VISIBLE);
        mCvMatchMember.animate().x(x).y(y).scaleX(0.3f).scaleY(0.3f).alpha(1f).setDuration(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                mCvMatchMember.animate().setListener(null);
                mCvMatchMember.animate().x(mMatchMemberX).y(mMatchMemberY).scaleX(1f).scaleY(1f).alpha(1f).setDuration(500).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        mCvMatchMember.animate().setListener(null);
                        Log.e(mTag, "c");
                        loadNextMember();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void initUserActionBar() {
        float y = mUserActionY + 500;

        mLoUserAction.setVisibility(View.VISIBLE);
        mLoUserAction.animate().y(y).setDuration(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mLoUserAction.animate().y(mUserActionY).setDuration(500).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoUserAction.animate().setListener(null);
                        initMessage();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void initMessage() {
        mLoGuide.animate().alpha(0f).setDuration(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mLoGuide.animate().setListener(null);
                mLoGuide.setVisibility(View.VISIBLE);
                mLoGuide.animate().alpha(1f).setDuration(500).setListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoGuide.animate().setListener(null);

                        mLoGuide.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startGame();
                            }
                        });
                        mLoReadyMessage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startGame();
                            }
                        });
                        mIsFirstLoading = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void startGame() {
        mLoGuide.setVisibility(View.GONE);
        mLoReadyMessage.setVisibility(View.GONE);
        runCounter();
    }

    private void runCounter() {
        mLoReadyCounter.setVisibility(View.VISIBLE);
        mLoReadyCounter.animate().scaleX(1.5f).scaleY(1.5f).alpha(0f).setDuration(800).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mLoReadyCounter.animate().setListener(null);

                if (mReadyCounterValue == 1) {
                    mReadyCounterValue = 3;
                    mReadyCounterText.setText(String.valueOf(mReadyCounterValue));
                    mLoReadyCounter.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(0);
                    mLoReadyCounter.setVisibility(View.GONE);
                    mIsProcessing = false;
                    //doJudge();
                } else {
                    mLoReadyCounter.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(0).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            mLoReadyCounter.animate().setListener(null);

                            mReadyCounterValue--;
                            mReadyCounterText.setText(String.valueOf(mReadyCounterValue));
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
        mIvMatchMemberPicture.setVisibility(View.GONE);
        mTvMatchMemberActionIcon.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mIvMatchMemberPicture.setVisibility(View.VISIBLE);
                mTvMatchMemberActionIcon.setVisibility(View.GONE);
                doWin();
            }
        }, 1500);
    }

    private void doWin() {
        mCvMatchMember.animate().x(-150f).y(1250f).scaleXBy(-0.7f).scaleYBy(-0.7f).alpha(0f).setDuration(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mCvMatchMember.animate().setListener(null);
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
        iv.setLayoutParams(mParams);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mLoMyMemberList.addView(iv, 0);
        mMyMemberImageViews.add(iv);

        Picasso.with(mContext).load(mPictureUrl).into(iv, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                if (mMyMemberImageViews.size() == 1) {
                    mLoMyMemberCounter.setVisibility(View.VISIBLE);
                    mLoMyMemberCounter.animate().alpha(1f).setDuration(500);
                }

                if (mMyMemberImageViews.size() > 0) {
                    String text = mMyMemberImageViews.size() + "";
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
        mCvMatchMember.animate().scaleXBy(0.1f).scaleYBy(0.1f).setDuration(300).setListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mCvMatchMember.animate().setListener(null);
                mCvMatchMember.animate().scaleX(1f).scaleY(1f).setDuration(700);
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
                    iv.animate().setListener(null);

                    mMyMemberImageViews.remove(index);

                    String text = mMyMemberImageViews.size() + "";
                    mTvMyMemberCounterText.setText(text);

                    if (mMyMemberImageViews.size() == 0) {
                        //float dest = 360;
                        //if (mLoMyMemberCounter.getRotation() == 360) {
                        //    dest = 0;
                        //}
                        //ObjectAnimator ani = ObjectAnimator.ofFloat(mLoMyMemberCounter, "rotation", dest);
                        //ani.setDuration(1000);
                        //ani.start();
                        mLoMyMemberCounter.animate().alpha(0f).setDuration(500).setListener(new Animator.AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                //mLoMyMemberCounter.setVisibility(View.GONE);
                                mLoMyMemberCounter.animate().setListener(null);

                                mLoMyMemberList.removeView(iv);
                                setReady();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                    } else {
                        mLoMyMemberList.removeView(iv);
                        setReady();
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
    }

    private void setReady() {
        mLoGuide.setVisibility(View.GONE);

        mLoReadyMessage.animate().alpha(0f).setDuration(0).setListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mLoReadyMessage.animate().setListener(null);

                mLoReadyMessage.setVisibility(View.VISIBLE);
                mLoReadyMessage.animate().alpha(1f).setDuration(300).setListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoReadyMessage.animate().setListener(null);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
