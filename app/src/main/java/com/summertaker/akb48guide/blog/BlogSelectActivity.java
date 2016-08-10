package com.summertaker.akb48guide.blog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;
import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.BaseActivity;
import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.data.DataManager;
import com.summertaker.akb48guide.data.SiteData;

import java.util.ArrayList;

public class BlogSelectActivity extends BaseActivity {

    ArrayList<SiteData> mSiteDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_select_activity);

        mContext = BlogSelectActivity.this;

        initBaseToolbar(Config.TOOLBAR_ICON_BACK, getString(R.string.blog));

        DataManager dataManager = new DataManager(mContext);
        mSiteDatas = dataManager.getBlogList();

        //initListView();
        initGridView();
    }

    private void initListView() {
        LinearLayout loListView = (LinearLayout) findViewById(R.id.loListView);
        if (loListView != null) {
            loListView.setVisibility(View.VISIBLE);
            ListView listView = (ListView) findViewById(R.id.listView);
            if (listView != null) {
                BlogSelectListAdapter adapter = new BlogSelectListAdapter(mContext, mSiteDatas);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SiteData siteData = (SiteData) parent.getItemAtPosition(position);
                        itemClick(siteData);
                    }
                });
            }
        }
    }

    private void initGridView() {
        ScrollView svGridView = (ScrollView) findViewById(R.id.loGridView);
        if (svGridView != null) {
            svGridView.setVisibility(View.VISIBLE);
            ExpandableHeightGridView gridView = (ExpandableHeightGridView) findViewById(R.id.gridView);
            if (gridView != null) {
                BlogSelectGridAdapter adapter = new BlogSelectGridAdapter(mContext, mSiteDatas);
                gridView.setExpanded(true);
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SiteData siteData = (SiteData) parent.getItemAtPosition(position);
                        itemClick(siteData);
                    }
                });
            }
        }
    }

    private void itemClick(SiteData siteData) {
        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(siteData.getMobileUrl()));
        //startActivity(intent);

        Intent intent;
        switch (siteData.getId()) {
            //case Config.BLOG_ID_NGT48_PHOTOLOG:
            //    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(siteData.getMobileUrl()));
            //    startActivity(intent);
            //    break;
            case Config.BLOG_ID_SKE48_SELECTED:
            case Config.BLOG_ID_NMB48_OFFICIAL:
            case Config.BLOG_ID_NGT48_PHOTOLOG:
                //intent = new Intent(mContext, BlogRssActivity.class);
                intent = new Intent(mContext, BlogArticleListActivity.class);
                intent.putExtra("siteData", siteData);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            default:
                intent = new Intent(mContext, BlogSiteListActivity.class);
                intent.putExtra("siteData", siteData);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //hideToolbarProgressBar();

        if (resultCode == Config.RESULT_CODE_FINISH) {
            SiteData siteData = (SiteData) data.getSerializableExtra("siteData");
            itemClick(siteData);
        }

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
