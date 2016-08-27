package com.summertaker.akb48guide.janken;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.BaseActivity;
import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.util.Typefaces;

import java.util.ArrayList;

public class JankenMainActivity extends BaseActivity {

    String imageUrl;
    ArrayList<ImageView> mMyMemberImageViews = new ArrayList<>();

    private LinearLayout.LayoutParams mParams;
    private LinearLayout.LayoutParams mParamsNoMargin;
    LinearLayout mLoMyMember;
    CardView aniView;
    float aniViewX;
    float aniViewY;

    int mTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.janken_main_activity);

        mContext = JankenMainActivity.this;

        initBaseToolbar(Config.TOOLBAR_ICON_BACK, getString(R.string.app_name));

        float density = mContext.getResources().getDisplayMetrics().density;
        int width = (int) (47 * density);
        int height = (int) (60 * density);
        int margin = (int) (8 * density);
        mParams = new LinearLayout.LayoutParams(width, height);
        mParams.setMargins(0, 0, margin, 0);
        mParamsNoMargin = new LinearLayout.LayoutParams(width, height);

        mLoMyMember = (LinearLayout) findViewById(R.id.loMyMember);

        imageUrl = "http://cache.hkt48pc.qw.to/img/profile/images/0011_320.jpg";

        aniView = (CardView) findViewById(R.id.cvMember);
        final ImageView ivMember = (ImageView) findViewById(R.id.ivMember);
        Picasso.with(mContext).load(imageUrl).into(ivMember, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

            }
        });

        /*for (int i = 0; i < 20; i++) {
            ImageView iv = new ImageView(mContext);
            if (i == 0) {
                iv.setLayoutParams(mParamsNoMargin);
            } else {
                iv.setLayoutParams(mParams);
            }
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mLoMyMember.addView(iv);
            Picasso.with(mContext).load(imageUrl).into(iv);
        }*/

        // http://stackoverflow.com/questions/15210548/how-to-use-a-icons-and-symbols-from-font-awesome-on-native-android-application
        //Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf" );
        Typeface font = Typefaces.get(mContext, "fontawesome-webfont.ttf");

        TextView tvScissorsIcon = (TextView) findViewById(R.id.tvScissorsIcon);
        tvScissorsIcon.setTypeface(font);

        TextView tvRockIcon = (TextView) findViewById(R.id.tvRockIcon);
        tvRockIcon.setTypeface(font);

        TextView tvPaperIcon = (TextView) findViewById(R.id.tvPaperIcon);
        tvPaperIcon.setTypeface(font);
    }

    public void startAnimation(View view) {
        float dest = 0;
        //ImageView aniView = (ImageView) findViewById(R.id.ivMember);

        switch (view.getId()) {
            case R.id.btnRotate:
                dest = 360;
                if (aniView.getRotation() == 360) {
                    System.out.println(aniView.getAlpha());
                    dest = 0;
                }
                ObjectAnimator animation1 = ObjectAnimator.ofFloat(aniView, "rotation", dest);
                animation1.setDuration(1000);
                animation1.start();
                // Show how to load an animation from XML
                // Animation animation1 = AnimationUtils.loadAnimation(this,
                // R.anim.myanimation);
                // animation1.setAnimationListener(this);
                // animatedView1.startAnimation(animation1);

                removeMyMember();
                break;

            case R.id.btnGroup:
                /*ObjectAnimator fadeOut = ObjectAnimator.ofFloat(aniView, "alpha", 0f);
                fadeOut.setDuration(1000);

                ObjectAnimator mover = ObjectAnimator.ofFloat(aniView, "translationX", -500f, 0f);
                mover.setDuration(1000);

                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(aniView, "alpha", 0f, 1f);
                fadeIn.setDuration(1000);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(mover).with(fadeIn).after(fadeOut);
                animatorSet.start();*/

                /*ObjectAnimator hmover = ObjectAnimator.ofFloat(aniView, "x", 0f, -300f);
                //hmover.setDuration(1000);

                ObjectAnimator vmover = ObjectAnimator.ofFloat(aniView, "y", 0f, 1200f);
                //vmover.setDuration(1000);

                //ObjectAnimator mover = ObjectAnimator.ofFloat(aniView, "transition", 0f);
                //mover.setDuration(1000);

                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(aniView, "alpha", 0f);
                fadeOut.setDuration(1000);

                //ObjectAnimator fadeIn = ObjectAnimator.ofFloat(aniView, "alpha", 0f, 1f);
                //fadeIn.setDuration(1000);

                AnimatorSet animatorSet = new AnimatorSet();
                //animatorSet.play(fadeOut).after(hmover);
                animatorSet.playTogether(hmover, vmover);
                animatorSet.setDuration(300);
                animatorSet.start();*/

                aniViewX = aniView.getX();
                aniViewY = aniView.getY();
                // Log.e(mTag, "aniViewX: " + aniViewX + ", aniViewY: " + aniViewY);

                aniView.animate().setDuration(500).x(-300f).y(1200f).scaleXBy(-1f).scaleYBy(-1f).setListener(animatorListener);
                break;

            case R.id.btnFade:
                // demonstrate fading and adding an AnimationListener
                dest = 1;
                if (aniView.getAlpha() > 0) {
                    dest = 0;
                }
                ObjectAnimator animation3 = ObjectAnimator.ofFloat(aniView, "alpha", dest);
                animation3.setDuration(1000);
                animation3.start();
                break;

            case R.id.btnAnimate:
                // shows how to define a animation via code
                // also use an Interpolator (BounceInterpolator)
                Paint paint = new Paint();
                TextView aniTextView = (TextView) findViewById(R.id.textView1);
                float measureTextCenter = paint.measureText(aniTextView.getText().toString());
                dest = 0 - measureTextCenter;
                if (aniTextView.getX() < 0) {
                    dest = 0;
                }
                ObjectAnimator animation2 = ObjectAnimator.ofFloat(aniTextView, "x", dest);
                animation2.setDuration(1000);
                animation2.start();
                break;

            default:
                break;
        }
    }

    Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            //Log.e(mTag, "End..........");
            insertMyMember();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

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

    private void insertMyMember() {
        ImageView iv = new ImageView(mContext);
        //if (mMyMemberImageViews.size() == 0) {
        //    iv.setLayoutParams(mParamsNoMargin);
        //} else {
            iv.setLayoutParams(mParams);
        //}
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mLoMyMember.addView(iv, 0);
        mMyMemberImageViews.add(iv);

        Picasso.with(mContext).load(imageUrl).into(iv, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                aniView.animate().x(aniViewX).y(aniViewY).scaleX(1f).scaleY(1f).setDuration(0).setListener(null);
            }

            @Override
            public void onError() {

            }
        });
    }

    private void removeMyMember() {
        final int index = mMyMemberImageViews.size() - 1;
        if (index >= 0) {
            final ImageView iv = mMyMemberImageViews.get(index);

            iv.animate().setDuration(500).scaleX(0f).scaleY(0f).alpha(0f).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    //Log.e(mTag, "End..........");
                    mLoMyMember.removeView(iv);
                    mMyMemberImageViews.remove(index);
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
