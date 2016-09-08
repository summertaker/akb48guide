package com.summertaker.akb48guide.janken;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;
import com.summertaker.akb48guide.GroupSelectGridAdapter;
import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.BaseActivity;
import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.data.DataManager;
import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.util.Typefaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class JankenGroupActivity extends BaseActivity {

    Typeface mFont;

    private String mAction;
    ArrayList<GroupData> mGroupDataList;
    ArrayList<GroupData> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.janken_group_activity);

        mContext = JankenGroupActivity.this;

        Intent intent = getIntent();
        mAction = intent.getStringExtra("action");
        if (mAction == null) {
            mAction = "";
        }
        //String title = intent.getStringExtra("title");

        String title = getString(R.string.rock_paper_scissors);

        initBaseToolbar(Config.TOOLBAR_ICON_BACK, title);
        mBaseToolbar.setBackgroundColor(Color.parseColor("#00796b"));

        DataManager dataManager = new DataManager(mContext);
        mGroupDataList = dataManager.getGroupList(mAction);

        mDataList = new ArrayList<>();

        initUi();
    }

    private void initUi() {
        ArrayList<GroupData> akb48GroupList = new ArrayList<>();
        for (GroupData groupData : mGroupDataList) {
            switch (groupData.getId()) {
                case Config.GROUP_ID_AKB48:
                case Config.GROUP_ID_SKE48:
                case Config.GROUP_ID_NMB48:
                case Config.GROUP_ID_HKT48:
                    groupData.setLocked(true);
                    akb48GroupList.add(groupData);
                    break;
                case Config.GROUP_ID_NGT48:
                    groupData.setLocked(false);
                    akb48GroupList.add(groupData);
                    break;
            }
        }
        Collections.reverse(akb48GroupList);
        for (GroupData groupData : akb48GroupList) {
            mDataList.add(groupData);
        }

        ArrayList<GroupData> snh48GroupList = new ArrayList<>();
        for (GroupData groupData : mGroupDataList) {
            switch (groupData.getId()) {
                case Config.GROUP_ID_SNH48:
                case Config.GROUP_ID_BEJ48:
                    groupData.setLocked(true);
                    snh48GroupList.add(groupData);
                    break;
                case Config.GROUP_ID_GNZ48:
                    groupData.setLocked(false);
                    snh48GroupList.add(groupData);
                    break;
            }
        }
        Collections.reverse(snh48GroupList);
        for (GroupData groupData : snh48GroupList) {
            mDataList.add(groupData);
        }

        ArrayList<GroupData> jkt48GroupList = new ArrayList<>();
        for (GroupData groupData : mGroupDataList) {
            switch (groupData.getId()) {
                case Config.GROUP_ID_JKT48:
                    groupData.setLocked(false);
                    jkt48GroupList.add(groupData);
                    break;
            }
        }
        Collections.reverse(jkt48GroupList);
        for (GroupData groupData : jkt48GroupList) {
            mDataList.add(groupData);
        }

        ExpandableHeightGridView gridView = (ExpandableHeightGridView) findViewById(R.id.gridView);
        if (gridView != null) {
            gridView.setExpanded(true);
            gridView.setAdapter(new JankenGroupAdapter(mContext, mDataList));
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GroupData groupData = (GroupData) parent.getItemAtPosition(position);
                    //if (!groupData.isLocked()) {
                        goActivity(groupData);
                    //}
                }
            });
        }

        /*ExpandableHeightGridView gvSnh48 = (ExpandableHeightGridView) findViewById(R.id.gvSnh48);
        if (gvSnh48 != null) {
            gvSnh48.setExpanded(true);
            gvSnh48.setAdapter(new JankenGroupAdapter(mContext, snh48GroupList));
            gvSnh48.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GroupData groupData = (GroupData) parent.getItemAtPosition(position);
                    goActivity(groupData);
                }
            });
        }*/

        /*ExpandableHeightGridView gvJkt48 = (ExpandableHeightGridView) findViewById(R.id.gvJkt48);
        if (gvJkt48 != null) {
            gvJkt48.setExpanded(true);
            gvJkt48.setAdapter(new JankenGroupAdapter(mContext, jkt48GroupList));
            gvJkt48.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GroupData groupData = (GroupData) parent.getItemAtPosition(position);
                    goActivity(groupData);
                }
            });
        }*/
    }

    private void createUi() {
        // http://stackoverflow.com/questions/15210548/how-to-use-a-icons-and-symbols-from-font-awesome-on-native-android-application
        //Typeface mFont = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf" );
        mFont = Typefaces.get(mContext, "fontawesome-webfont.ttf");

        //int width = (int) (47 * mDensity);
        //int height = (int) (53 * mDensity);

        LinearLayout loAkb48 = (LinearLayout) findViewById(R.id.loAkb48);
        loAkb48.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goActivity(mGroupDataList.get(0));
            }
        });
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

    private void addGroup(LinearLayout root, String text, String outerColor, String innerColor, String textColor) {
        RelativeLayout loItem = new RelativeLayout(mContext);
        loItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        /*TextView tvCircleBorder = new TextView(mContext);
        setParam(tvCircleBorder);
        tvCircleBorder.setText(getString(R.string.fa_circle));
        tvCircleBorder.setTextColor(Color.parseColor("#333333"));
        tvCircleBorder.setTextSize(70);
        tvCircleBorder.setTypeface(mFont);
        loItem.addView(tvCircleBorder);*/

        /*TextView tvOuter = new TextView(mContext);
        setParam(tvOuter);
        tvOuter.setText(getString(R.string.fa_circle));
        tvOuter.setTextColor(Color.parseColor(outerColor));
        tvOuter.setTextSize(70);
        tvOuter.setTypeface(mFont);
        loItem.addView(tvOuter);*/

        /*TextView tvInner = new TextView(mContext);
        setParam(tvInner);
        tvInner.setText(getString(R.string.fa_circle));
        tvInner.setTextColor(Color.parseColor(innerColor));
        tvInner.setTextSize(68);
        tvInner.setTypeface(mFont);
        tvInner.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_danger));
        loItem.addView(tvInner);*/

        LinearLayout ll = new LinearLayout(mContext);
        setParam(ll);
        ll.setPadding(20, 20, 20, 20);
        ll.setGravity(Gravity.CENTER_HORIZONTAL);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_info));

        TextView tvGroup = new TextView(mContext);
        tvGroup.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        tvGroup.setText(text);
        tvGroup.setTextColor(Color.parseColor(textColor));
        tvGroup.setTextSize(13);
        tvGroup.setGravity(Gravity.CENTER);
        ll.addView(tvGroup);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0x55000000); // Changes this drawbale to use a single color instead of a gradient
        gd.setCornerRadius(36);
        gd.setStroke(1, 0xFF000000);

        TextView tvTeam = new TextView(mContext);
        LinearLayout.LayoutParams lpTeam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tvTeam.setLayoutParams(lpTeam);
        tvTeam.setPadding(0, 5, 0, 10);
        tvTeam.setBackground(gd);
        tvTeam.setText("N III");
        //tvTeam.setBackgroundColor(Color.parseColor("#555555"));
        tvTeam.setTextColor(Color.parseColor("#ffffff"));
        tvTeam.setTextSize(13);
        tvTeam.setGravity(Gravity.CENTER);
        ll.addView(tvTeam);

        loItem.addView(ll);

        /*ImageView iv = new ImageView(mContext);
        setParam(iv);
        iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.logo_akb48));
        loItem.addView(iv);*/

        root.addView(loItem);
    }

    private void addButton(LinearLayout root) {
        RelativeLayout rl = new RelativeLayout(mContext);
        rl.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        //LinearLayout ll = new LinearLayout(mContext, null, R.style.DangerButton);

        Button btn = new Button(mContext, null, R.style.DangerMiniButton);
        setParam(btn);
        btn.setText("도전");
        //btn.setBackgroundColor(ContextCompat.getColor(mContext, R.color.button_danger));
        //btn.setTextColor(Color.parseColor("#999999")); // 7cb342
        btn.setTextSize(13);
        rl.addView(btn);

        root.addView(rl);
    }

    private void addArrow(LinearLayout root) {
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

    private void goActivity(GroupData groupData) {
        Intent intent = new Intent(this, JankenTeamActivity.class);
        intent.putExtra("action", mAction);
        intent.putExtra("groupData", groupData);
        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
