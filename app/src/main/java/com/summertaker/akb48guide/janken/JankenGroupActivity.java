package com.summertaker.akb48guide.janken;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;
import com.summertaker.akb48guide.GroupSelectActivity;
import com.summertaker.akb48guide.GroupSelectGridAdapter;
import com.summertaker.akb48guide.GroupSelectTextAdapter;
import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.birthday.BirthMonthActivity;
import com.summertaker.akb48guide.common.BaseActivity;
import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.common.Setting;
import com.summertaker.akb48guide.data.DataManager;
import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.member.TeamListActivity;
import com.summertaker.akb48guide.puzzle.PuzzleLevelActivity;
import com.summertaker.akb48guide.quiz.MemoryActivity;
import com.summertaker.akb48guide.quiz.QuizActivity;
import com.summertaker.akb48guide.quiz.SlideActivity;
import com.summertaker.akb48guide.quiz.SlideTextActivity;
import com.summertaker.akb48guide.rawphoto.RawPhotoSelectActivity;

import java.util.ArrayList;
import java.util.Random;

public class JankenGroupActivity extends BaseActivity {

    float mDensity;

    private String mAction;

    LinearLayout.LayoutParams mParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.janken_group_activity);

        mContext = JankenGroupActivity.this;
        mDensity = mContext.getResources().getDisplayMetrics().density;

        Intent intent = getIntent();
        mAction = intent.getStringExtra("action");
        String title = intent.getStringExtra("title");

        initBaseToolbar(Config.TOOLBAR_ICON_BACK, title);

        //Setting setting = new Setting(mContext);
        //String displayProfilePhoto = setting.get(Config.SETTING_DISPLAY_OFFICIAL_PHOTO);

        int width = (int) (47 * mDensity);
        int height = (int) (53 * mDensity);
        int margin = (int) (6 * mDensity);
        mParams = new LinearLayout.LayoutParams(width, height);
        mParams.setMargins(0, 0, margin, 0);

        LinearLayout loAkb48 = (LinearLayout) findViewById(R.id.loAkb48);

        RelativeLayout loItem = new RelativeLayout(mContext);
        loItem.setLayoutParams(new RelativeLayout.LayoutParams((int) (500 * mDensity), (int) (500 * mDensity)));
        loItem.setGravity(RelativeLayout.CENTER_HORIZONTAL);

        TextView tvCircleBorder = new TextView(mContext);
        tvCircleBorder.setLayoutParams(new LinearLayout.LayoutParams((int) (300 * mDensity), (int) (300 * mDensity)));
        tvCircleBorder.setText(getString(R.string.fa_circle));
        tvCircleBorder.setTextColor(Color.parseColor("blue"));
        loItem.addView(tvCircleBorder);

        TextView tvCircle = new TextView(mContext);
        tvCircle.setLayoutParams(new LinearLayout.LayoutParams((int) (200 * mDensity), (int) (200 * mDensity)));
        tvCircle.setText(getString(R.string.fa_circle));
        tvCircle.setTextColor(Color.parseColor("red"));
        loItem.addView(tvCircle);

        loAkb48.addView(loItem);
    }

    public void goActivity(GroupData groupData) {

        Intent intent = new Intent(this, JankenMainActivity.class);
        intent.putExtra("action", mAction);
        intent.putExtra("groupData", groupData);
        //showToolbarProgressBar();
        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
