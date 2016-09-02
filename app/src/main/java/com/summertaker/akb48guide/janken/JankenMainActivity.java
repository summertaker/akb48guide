package com.summertaker.akb48guide.janken;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
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

    float mDensity;

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
    int mWinCount = 0;
    int mDrawCount = 0;
    int mLoseCount = 0;

    boolean mIsFirstLoading = true;
    int mMemberIndex = 0;
    String mPictureUrl;

    LinearLayout mLoProgress;
    boolean mProgressRenderFinished = false;
    TextView mTvProgressInfo;
    TextView mTvProgressTotal;
    ProgressBar mPbProgress;

    LinearLayout mLoNextMember;
    CardView mCvNextMember;
    float mNextMemberX;
    float mNextMemberY;
    boolean mNextMemberRendered = false;
    ProgressBar mPbNextMemberPictureLoading;
    ImageView mIvNextMemberPicture;
    //TextView mTvRemainMemberCounterText;

    CardView mCvMatchMember;
    float mMatchMemberX;
    float mMatchMemberY;
    boolean mMatchMemberRendered = false;
    RelativeLayout mLoMatchMemberPictureLoading;
    ProgressBar mPbMatchMemberPictureLoading;
    ImageView mIvMatchMemberPicture;
    TextView mTvMatchMemberPictureCaption;
    RelativeLayout mLoMatchMemberAction;
    TextView mTvMatchMemberActionIcon;

    RelativeLayout mLoReady;
    int mReadyCounterValue = 3;

    ArrayList<ImageView> mMyMemberImageViews = new ArrayList<>();
    LinearLayout.LayoutParams mParams;
    LinearLayout.LayoutParams mParamsNoMargin;

    LinearLayout mLoMyMemberList;
    RelativeLayout mLoMyMemberCounter;
    TextView mTvMyMemberCounterText;

    LinearLayout mLoUserAction;
    float mUserActionY;
    LinearLayout mLoUserActionScissors;
    TextView mTvUserActionScissorsIcon;
    TextView mTvUserActionScissorsText;
    LinearLayout mLoUserActionRock;
    TextView mTvUserActionRockIcon;
    TextView mTvUserActionRockText;
    LinearLayout mLoUserActionPaper;
    TextView mTvUserActionPaperIcon;
    TextView mTvUserActionPaperText;
    boolean mUserActionRendered = false;

    LinearLayout mLoGuide;
    Button mBtnStartGame;
    //TextView mLoSelectMessage;

    RelativeLayout mLoReadyCounter;
    TextView mReadyCounterText;
    RelativeLayout mLoReadySelect;

    RelativeLayout mLoJudge;
    TextView mTvJudgeIcon;
    TextView mTvJudgeText;
    int mMemberSelectAction;
    int mUserSelectAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.janken_main_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#004d40"));
        }

        mContext = JankenMainActivity.this;
        mDensity = mContext.getResources().getDisplayMetrics().density;

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

        //--------------------------
        // 상단 바 배경 이미지 설정
        //--------------------------
        /*int bg = R.drawable.war_sashi;
        switch (Util.getRandom(1, 4)) {
            case 1:
                bg = R.drawable.war_mayu;
                break;
            case 2:
                bg = R.drawable.war_jurina;
                break;
            case 3:
                bg = R.drawable.war_sayaka;
                break;
            case 4:
                bg = R.drawable.war_sashi;
                break;
        }
        LinearLayout loTop = (LinearLayout) findViewById(R.id.loTop);
        loTop.setBackground(ContextCompat.getDrawable(mContext, bg));*/

        /*TextView tvGroupNgt48  = (TextView) findViewById(R.id.tvGroupNgt48);
        tvGroupNgt48.setTypeface(font);
        TextView tvGroupHkt48  = (TextView) findViewById(R.id.tvGroupHkt48);
        tvGroupHkt48.setTypeface(font);
        TextView tvGroupNmb48  = (TextView) findViewById(R.id.tvGroupNmb48);
        tvGroupNmb48.setTypeface(font);
        TextView tvGroupSke48  = (TextView) findViewById(R.id.tvGroupSke48);
        tvGroupSke48.setTypeface(font);
        TextView tvGroupAkb48  = (TextView) findViewById(R.id.tvGroupAkb48);
        tvGroupAkb48.setTypeface(font);*/

        //----------------------
        // 진행 상태 설정
        //----------------------
        mLoProgress = (LinearLayout) findViewById(R.id.loProgress);
        mLoProgress.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mLoProgress.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mProgressRenderFinished = true;
                        onRenderFinished();
                    }
                });
        TextView tvProgressTitle = (TextView) findViewById(R.id.tvProgressTitle);
        String progressTitle = mGroupData.getName() + "에 도전";
        tvProgressTitle.setText(progressTitle);
        mTvProgressInfo = (TextView) findViewById(R.id.tvProgressInfo);
        mTvProgressTotal = (TextView) findViewById(R.id.tvProgressTotal);
        mPbProgress = (ProgressBar) findViewById(R.id.pbProgress);
        mPbProgress.setProgress(0);

        //--------------------
        // 다음 멤버 설정
        //--------------------
        mLoNextMember = (LinearLayout) findViewById(R.id.loNextMember);
        mCvNextMember = (CardView) findViewById(R.id.cvNextMember);
        mPbNextMemberPictureLoading = (ProgressBar) findViewById(R.id.pbNextMemberPictureLoading);
        mIvNextMemberPicture = (ImageView) findViewById(R.id.ivNextMemberPicture);
        mIvNextMemberPicture.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mIvNextMemberPicture.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mNextMemberRendered = true;
                        onRenderFinished();
                    }
                });

        //mTvRemainMemberCounterText = (TextView) findViewById(R.id.tvRemainMemberCounterText);
        //TextView tvRemainMemberCounterIcon = (TextView) findViewById(R.id.tvRemainMemberCounterIcon);
        //tvRemainMemberCounterIcon.setTypeface(font);
        //String text = (mGroupMemberList.size() - mMemberIndex) + "명";
        //mTvRemainMemberCounterText.setText(text);

        mCvMatchMember = (CardView) findViewById(R.id.cvMatchMember);
        mCvMatchMember.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
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
        mLoMatchMemberAction = (RelativeLayout) findViewById(R.id.loMatchMemberAction);
        mTvMatchMemberActionIcon = (TextView) findViewById(R.id.tvMatchMemberActionIcon);
        mTvMatchMemberActionIcon.setTypeface(font);

        int width = (int) (47 * mDensity);
        int height = (int) (60 * mDensity);
        int margin = (int) (6 * mDensity);
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

        // 가위 버튼
        mLoUserActionScissors = (LinearLayout) findViewById(R.id.loUserActionScissors);
        mTvUserActionScissorsIcon = (TextView) findViewById(R.id.tvUserActionScissorsIcon);
        mTvUserActionScissorsIcon.setTypeface(font);
        mTvUserActionScissorsText = (TextView) findViewById(R.id.tvUserActionScissorsText);

        // 바위 버튼
        mLoUserActionRock = (LinearLayout) findViewById(R.id.loUserActionRock);
        mTvUserActionRockIcon = (TextView) findViewById(R.id.tvUserActionRockIcon);
        mTvUserActionRockIcon.setTypeface(font);
        mTvUserActionRockText = (TextView) findViewById(R.id.tvUserActionRockText);

        // 보 버튼
        mLoUserActionPaper = (LinearLayout) findViewById(R.id.loUserActionPaper);
        mTvUserActionPaperIcon = (TextView) findViewById(R.id.tvUserActionPaperIcon);
        mTvUserActionPaperIcon.setTypeface(font);
        mTvUserActionPaperText = (TextView) findViewById(R.id.tvUserActionPaperText);

        mLoGuide = (LinearLayout) findViewById(R.id.loGuide);
        //mLoSelectMessage = (TextView) findViewById(R.id.loSelectMessage);
        mBtnStartGame = (Button) findViewById(R.id.btnStartGame);

        // 레디 카운터 - 준비
        mLoReady = (RelativeLayout) findViewById(R.id.loReady);
        TextView tvReadyBorder = (TextView) findViewById(R.id.tvReadyBorder);
        tvReadyBorder.setTypeface(font);
        TextView tvReadyOuter = (TextView) findViewById(R.id.tvReadyOuter);
        tvReadyOuter.setTypeface(font);
        TextView tvReadyInner = (TextView) findViewById(R.id.tvReadyInner);
        tvReadyInner.setTypeface(font);

        // 레디 카운터 - 숫자
        mLoReadyCounter = (RelativeLayout) findViewById(R.id.loReadyCounter);
        mReadyCounterText = (TextView) findViewById(R.id.tvReadyCounterText);
        mReadyCounterText.setText(String.valueOf(mReadyCounterValue));
        TextView tvReadyCounterBorder = (TextView) findViewById(R.id.tvReadyCounterBorder);
        tvReadyCounterBorder.setTypeface(font);
        TextView tvReadyCounterOuter = (TextView) findViewById(R.id.tvReadyCounterOuter);
        tvReadyCounterOuter.setTypeface(font);
        TextView tvReadyCounterInner = (TextView) findViewById(R.id.tvReadyCounterInner);
        tvReadyCounterInner.setTypeface(font);

        // 레디 카운터 - 선택
        mLoReadySelect = (RelativeLayout) findViewById(R.id.loReadySelect);
        TextView tvReadySelectBorder = (TextView) findViewById(R.id.tvReadySelectBorder);
        tvReadySelectBorder.setTypeface(font);
        TextView tvReadySelectOuter = (TextView) findViewById(R.id.tvReadySelectOuter);
        tvReadySelectOuter.setTypeface(font);
        TextView tvReadySelectInner = (TextView) findViewById(R.id.tvReadySelectInner);
        tvReadySelectInner.setTypeface(font);

        mLoJudge = (RelativeLayout) findViewById(R.id.loJudge);
        mTvJudgeIcon = (TextView) findViewById(R.id.tvJudgeIcon);
        mTvJudgeIcon.setTypeface(font);
        mTvJudgeText = (TextView) findViewById(R.id.tvJudgeText);
    }

    private void onRenderFinished() {
        if (mProgressRenderFinished && mNextMemberRendered && mMatchMemberRendered && mUserActionRendered) {

            mNextMemberX = mLoNextMember.getX() - mLoNextMember.getWidth() - 90;
            mNextMemberY = mLoNextMember.getY() - mLoNextMember.getHeight();

            mMatchMemberX = mCvMatchMember.getX();
            mMatchMemberY = mCvMatchMember.getY();

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
                updateProgress();

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

                if (mIsFirstLoading) {
                    initUserActionBar();
                } else {
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
        mCvMatchMember.animate().x(mNextMemberX).y(mNextMemberY).scaleX(0.25f).scaleY(0.25f).alpha(1f).setDuration(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                mCvMatchMember.animate().setListener(null);

                mCvMatchMember.animate().x(mMatchMemberX).y(mMatchMemberY).scaleX(1f).scaleY(1f).alpha(1f).setDuration(700).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        mCvMatchMember.animate().setListener(null);
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

                        mBtnStartGame.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setUserActionButton(false);
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
        runCounter();
    }

    public class MyInterpolator implements Interpolator {
        // easeInOutQuint
        public float getInterpolation(float t) {
            float x = t * 2.0f;
            if (t < 0.5f) return 0.5f * x * x * x * x * x;
            x = (t - 0.5f) * 2 - 1;
            return 0.5f * x * x * x * x * x + 1;
        }
    }

    private void runCounter() {
        mLoReadyCounter.setVisibility(View.VISIBLE);

        MyInterpolator myInterpolator = new MyInterpolator();
        mLoReadyCounter.animate().scaleX(1.4f).scaleY(1.4f).alpha(0.0f).setDuration(900)/*.setInterpolator(myInterpolator)*/
                .setListener(new Animator.AnimatorListener() {
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
                            //mLoSelectMessage.setVisibility(View.VISIBLE);
                            mLoReadySelect.setVisibility(View.VISIBLE);
                            setUserActionButton(true);
                        } else {
                            mLoReadyCounter.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(0).setListener(new Animator.AnimatorListener() {
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

    private void onUserAction(View view) {
        mLoReadySelect.setVisibility(View.GONE);
        setUserActionButton(false);
        //mLoSelectMessage.setVisibility(View.GONE);

        // 선택한 버튼 반전시키기
        view.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_danger));
        for (int index = 0; index < ((ViewGroup) view).getChildCount(); ++index) {
            View nextChild = ((ViewGroup) view).getChildAt(index);
            TextView tvIcon = (TextView) nextChild;
            tvIcon.setTextColor(Color.WHITE);
            tvIcon.setTextColor(Color.WHITE);
        }

        //---------------------------------------------
        // 상대 멤버의 가위바위보 중 하나를 랜덤으로 설정
        //---------------------------------------------
        mMemberSelectAction = Util.getRandom(1, 3);
        int stringId = 0;
        switch (mMemberSelectAction) {
            case 1:
                stringId = R.string.fa_hand_scissors_o;
                break;
            case 2:
                stringId = R.string.fa_hand_rock_o;
                break;
            case 3:
                stringId = R.string.fa_hand_paper_o;
                break;
        }
        mTvMatchMemberPictureCaption.setVisibility(View.INVISIBLE);
        mLoMatchMemberAction.setVisibility(View.VISIBLE);
        mTvMatchMemberActionIcon.setText(getString(stringId));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doJudge();
            }
        }, 1000);
    }

    private void doJudge() {
        boolean win = false;
        boolean draw = false;
        boolean lose = false;

        switch (mUserSelectAction) {
            case Config.JANKEN_ACTION_SCESSORS:
                switch (mMemberSelectAction) {
                    case Config.JANKEN_ACTION_SCESSORS:
                        draw = true;
                        break;
                    case Config.JANKEN_ACTION_ROCK:
                        lose = true;
                        break;
                    case Config.JANKEN_ACTION_PAPER:
                        win = true;
                        break;
                }
                break;
            case Config.JANKEN_ACTION_ROCK:
                switch (mMemberSelectAction) {
                    case Config.JANKEN_ACTION_SCESSORS:
                        win = true;
                        break;
                    case Config.JANKEN_ACTION_ROCK:
                        draw = true;
                        break;
                    case Config.JANKEN_ACTION_PAPER:
                        lose = true;
                        break;
                }
                break;
            case Config.JANKEN_ACTION_PAPER:
                switch (mMemberSelectAction) {
                    case Config.JANKEN_ACTION_SCESSORS:
                        lose = true;
                        break;
                    case Config.JANKEN_ACTION_ROCK:
                        win = true;
                        break;
                    case Config.JANKEN_ACTION_PAPER:
                        draw = true;
                        break;
                }
                break;
        }

        win = true;
        draw = false;
        lose = false;

        final boolean isWin = win;
        final boolean isDraw = draw;
        final boolean isLose = lose;

        if (isWin) {
            mWinCount++;
            mTvJudgeText.setText("승");
        } else if (isDraw) {
            mDrawCount++;
            mTvJudgeText.setText("무승부");
        } else if (isLose) {
            mLoseCount++;
            mTvJudgeText.setText("패");
        }
        mLoJudge.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoJudge.animate().scaleX(1f).scaleY(1f).setDuration(0).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoJudge.animate().setListener(null);
                        mLoJudge.animate().scaleX(0f).scaleY(0f).setDuration(1000).setListener(new Animator.AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mLoJudge.animate().setListener(null);
                                mLoJudge.setVisibility(View.GONE);

                                mLoMatchMemberAction.setVisibility(View.GONE);
                                mTvMatchMemberPictureCaption.setVisibility(View.VISIBLE);

                                setUserActionButton(false);

                                if (isWin) {
                                    onWin();
                                } else if (isDraw) {
                                    onDraw();
                                } else if (isLose) {
                                    onLose();
                                }
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
        }, 1000);
    }

    private void animateJudge() {

    }

    private void onWin() {
        //float dest = 360;
        //if (mLoMyMemberCounter.getRotation() == 360) {
        //    dest = 0;
        //}
        mCvMatchMember.animate().rotation(360).scaleXBy(-0.5f).scaleYBy(-0.5f).setDuration(800).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCvMatchMember.animate().setListener(null);

                mCvMatchMember.animate().x(-130f).y(1400f).scaleXBy(-0.3f).scaleYBy(-0.3f).alpha(0.0f).setDuration(500).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        mCvMatchMember.animate().setListener(null);

                        //--------------------------------------------
                        // 승리했을 때 상단 진행 바와 다음 멤버 보여주기
                        //--------------------------------------------
                        mLoProgress.setVisibility(View.VISIBLE);

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

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

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

                mCvMatchMember.animate().rotation(0).setDuration(0);
                loadMatchMember();
            }

            @Override
            public void onError() {

            }
        });
    }

    private void onDraw() {
        setReady();
        //animateReady();
    }

    private void onLose() {
        final int index = mMyMemberImageViews.size() - 1;

        if (index >= 0) {
            //-------------------------------------
            // 상대 멤버의 승리 애니메이션 보여주기
            //-------------------------------------
            mCvMatchMember.animate().scaleXBy(0.1f).scaleYBy(0.1f).setDuration(400).setListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mCvMatchMember.animate().setListener(null);
                    mCvMatchMember.animate().scaleX(1f).scaleY(1f).setDuration(500);
                    removeMyMember();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        } else {
            finish();
        }
    }

    private void removeMyMember() {
        final int index = mMyMemberImageViews.size() - 1;
        final ImageView iv = mMyMemberImageViews.get(index);

        //-------------------------------------
        // 내 멤버 목록에서 한명 제거하기
        //-------------------------------------
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
                    mLoMyMemberCounter.animate().alpha(0f).setDuration(400).setListener(new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
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

    private void setReady() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animateReady();
            }
        }, 500);
    }

    private void animateReady() {
        mLoReady.setVisibility(View.VISIBLE);
        mLoReady.animate().scaleX(1.0f).scaleY(1.0f).alpha(0.0f).setDuration(0).setListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mLoReady.animate().setListener(null);

                mLoReady.animate().scaleX(1.3f).scaleY(1.3f).alpha(1.0f).setDuration(1000).setListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoReady.animate().setListener(null);

                        mLoReady.animate().scaleX(1.0f).scaleY(1.0f).alpha(0.0f).setDuration(1400).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                mLoReady.animate().setListener(null);
                                mLoReady.setVisibility(View.GONE);

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

    private void setUserActionButton(boolean enable) {
        int iconColor, textColor;
        if (enable) {
            iconColor = Color.parseColor("#ddffffff");
            textColor = Color.parseColor("#eeffffff");

            mLoUserActionScissors.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_success));
            mLoUserActionScissors.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mUserSelectAction = Config.JANKEN_ACTION_SCESSORS;
                    onUserAction(view);
                }
            });
            mLoUserActionRock.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_success));
            mLoUserActionRock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mUserSelectAction = Config.JANKEN_ACTION_ROCK;
                    onUserAction(view);
                }
            });
            mLoUserActionPaper.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_success));
            mLoUserActionPaper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mUserSelectAction = Config.JANKEN_ACTION_PAPER;
                    onUserAction(view);
                }
            });
        } else {
            iconColor = Color.parseColor("#55000000");
            textColor = Color.parseColor("#aa000000");

            mLoUserActionScissors.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_janken));
            mLoUserActionScissors.setOnClickListener(null);

            mLoUserActionRock.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_janken));
            mLoUserActionRock.setOnClickListener(null);

            mLoUserActionPaper.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_janken));
            mLoUserActionPaper.setOnClickListener(null);
        }

        mTvUserActionScissorsIcon.setTextColor(iconColor);
        mTvUserActionScissorsText.setTextColor(textColor);

        mTvUserActionRockIcon.setTextColor(iconColor);
        mTvUserActionRockText.setTextColor(textColor);

        mTvUserActionPaperIcon.setTextColor(iconColor);
        mTvUserActionPaperText.setTextColor(textColor);
    }

    private void updateProgress() {
        int count = mMemberIndex;
        int total = mGroupMemberList.size();

        //String info = mWinCount + "승 " + mLoseCount + "패";
        //mTvProgressInfo.setText(info);

        String text = "남은 멤버: " + String.format(getString(R.string.s_people), total - count);
        mTvProgressTotal.setText(text);

        float progress = (float) count / (float) total;
        int progressValue = (int) (progress * 100.0);
        mPbProgress.setProgress(progressValue);
    }
}
