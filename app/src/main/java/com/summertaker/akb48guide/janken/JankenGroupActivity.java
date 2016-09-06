package com.summertaker.akb48guide.janken;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.BaseActivity;
import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.util.Typefaces;

public class JankenGroupActivity extends BaseActivity {

    float mDensity;
    Typeface mFont;

    private String mAction;

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

        // http://stackoverflow.com/questions/15210548/how-to-use-a-icons-and-symbols-from-font-awesome-on-native-android-application
        //Typeface mFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf" );
        mFont = Typefaces.get(mContext, "fontawesome-webfont.ttf");

        //int width = (int) (47 * mDensity);
        //int height = (int) (53 * mDensity);

        LinearLayout loAkb48 = (LinearLayout) findViewById(R.id.loAkb48);
        addButton(loAkb48);
        addGroup(loAkb48, "NGT48", "#e4e4e4", "#ffffff", "#f44336");
        addArrow(loAkb48);
        //addGroup(loAkb48, "HKT48", "#a1a1a1", "#888888", "#ffffff");
        addGroup(loAkb48, "HKT48", "#cccccc", "#cccccc", "#999999");
        addArrow(loAkb48);
        //addGroup(loAkb48, "NMB48", "#e4b47c", "#d79346", "#ffffff");
        addGroup(loAkb48, "NMB48", "#e4b47c", "#d79346", "#ffffff");
        addArrow(loAkb48);
        //addGroup(loAkb48, "SKE48", "#f7d886", "#f8b500", "#ffffff");
        addGroup(loAkb48, "SKE48", "#f7d886", "#f8b500", "#ffffff");
        addArrow(loAkb48);
        //addGroup(loAkb48, "AKB48", "#ff96c8", "#fc60aa", "#ffffff");
        addGroup(loAkb48, "AKB48", "#ff96c8", "#fc60aa", "#ffffff");

        LinearLayout loSnh48 = (LinearLayout) findViewById(R.id.loSnh48);
        addButton(loSnh48);
        addGroup(loSnh48, "GNZ48", "#b7c885", "#9fbf40", "#ffffff");
        addArrow(loSnh48);
        addGroup(loSnh48, "BEJ48", "#ff8ab4", "#fe2472", "#ffffff");
        addArrow(loSnh48);
        addGroup(loSnh48, "SNH48", "#bfe5f9", "#8ed2f5", "#ffffff");

        LinearLayout loJkt48 = (LinearLayout) findViewById(R.id.loJkt48);
        addButton(loJkt48);
        addGroup(loJkt48, "JKT48", "#ef9a9a", "#ef1c24", "#ffffff");
    }

    public void addGroup(LinearLayout root, String text, String outerColor, String innerColor, String textColor) {
        RelativeLayout loItem = new RelativeLayout(mContext);
        loItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        TextView tvCircleBorder = new TextView(mContext);
        setParam(tvCircleBorder);
        tvCircleBorder.setText(getString(R.string.fa_circle));
        tvCircleBorder.setTextColor(Color.parseColor("#777777"));
        tvCircleBorder.setTextSize(70);
        tvCircleBorder.setTypeface(mFont);
        loItem.addView(tvCircleBorder);

        /*TextView tvOuter = new TextView(mContext);
        setParam(tvOuter);
        tvOuter.setText(getString(R.string.fa_circle));
        tvOuter.setTextColor(Color.parseColor(outerColor));
        tvOuter.setTextSize(70);
        tvOuter.setTypeface(mFont);
        loItem.addView(tvOuter);*/

        TextView tvInner = new TextView(mContext);
        setParam(tvInner);
        tvInner.setText(getString(R.string.fa_circle));
        tvInner.setTextColor(Color.parseColor(innerColor));
        tvInner.setTextSize(68);
        tvInner.setTypeface(mFont);
        loItem.addView(tvInner);

        TextView tvTitle = new TextView(mContext);
        setParam(tvTitle);
        tvTitle.setText(text);
        tvTitle.setTextColor(Color.parseColor(textColor));
        tvTitle.setTextSize(13);
        tvTitle.setGravity(Gravity.CENTER);
        loItem.addView(tvTitle);

        /*ImageView iv = new ImageView(mContext);
        setParam(iv);
        iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.logo_akb48));
        loItem.addView(iv);*/

        root.addView(loItem);
    }

    public void addButton(LinearLayout root) {
        RelativeLayout loItem = new RelativeLayout(mContext);
        loItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        //LinearLayout ll = new LinearLayout(mContext, null, R.style.DangerButton);

        Button btn = new Button(mContext, null, R.style.DangerMiniButton);
        setParam(btn);
        btn.setText("도전");
        //btn.setBackgroundColor(ContextCompat.getColor(mContext, R.color.button_danger));
        //btn.setTextColor(Color.parseColor("#999999")); // 7cb342
        btn.setTextSize(13);
        loItem.addView(btn);

        root.addView(loItem);
    }

    public void addArrow(LinearLayout root) {
        RelativeLayout loItem = new RelativeLayout(mContext);
        loItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        TextView tvArrow = new TextView(mContext);
        setParam(tvArrow);
        tvArrow.setText(getString(R.string.fa_arrow_down));
        tvArrow.setTextColor(Color.parseColor("#888888")); // 7cb342
        tvArrow.setTextSize(18);
        tvArrow.setTypeface(mFont);
        loItem.addView(tvArrow);

        root.addView(loItem);
    }

    private void setParam(View view) {
        view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        view.setLayoutParams(lp);
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
