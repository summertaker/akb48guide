package com.summertaker.akb48guide.rawphoto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;
import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.BaseActivity;
import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.data.GroupData;

import java.util.ArrayList;

public class RawPhotoMainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.raw_photo_main_activity);

        mContext = RawPhotoMainActivity.this;

        String title = getString(R.string.raw_photo);
        initBaseToolbar(Config.TOOLBAR_ICON_BACK, title);

        ArrayList<GroupData> dataList1 = new ArrayList<>();
        dataList1.add(new GroupData(Config.GROUP_ID_AKB48, "AKB48", R.drawable.logo_akb48, null,
                "http://shopping.akb48-group.com/products/list.php?akb48&category_id=3"
        ));
        dataList1.add(new GroupData(Config.GROUP_ID_SKE48, "SKE48", R.drawable.logo_ske48, null,
                "http://shopping.akb48-group.com/products/list.php?ske48&category_id=126"
        ));
        dataList1.add(new GroupData(Config.GROUP_ID_NMB48, "NMB48", R.drawable.logo_nmb48, null,
                "http://shopping.akb48-group.com/products/list.php?nmb48&category_id=506"
        ));
        dataList1.add(new GroupData(Config.GROUP_ID_HKT48, "HKT48", R.drawable.logo_hkt48, null,
                "http://shopping.akb48-group.com/products/list.php?hkt48&category_id=245"
        ));

        ArrayList<GroupData> dataList2 = new ArrayList<>();
        dataList2.add(new GroupData(Config.GROUP_ID_AKB48, "AKB48", R.drawable.logo_akb48,
                "http://www.akb48.co.jp/about/members/",
                "http://recyclekan.ja.shopserve.jp/"
        ));
        dataList2.add(new GroupData(Config.GROUP_ID_SKE48, "SKE48", R.drawable.logo_ske48,
                "http://www.ske48.co.jp/profile/list.php",
                "http://recyclekan.ja.shopserve.jp/"
        ));
        dataList2.add(new GroupData(Config.GROUP_ID_NMB48, "NMB48", R.drawable.logo_nmb48,
                "http://www.nmb48.com/member/",
                "http://recyclekan.ja.shopserve.jp/"
        ));
        dataList2.add(new GroupData(Config.GROUP_ID_HKT48, "HKT48", R.drawable.logo_hkt48,
                "http://www.hkt48.jp/profile/",
                "http://recyclekan.ja.shopserve.jp/"
        ));
        dataList2.add(new GroupData(Config.GROUP_ID_NGT48, "NGT48", R.drawable.logo_ngt48,
                "http://ngt48.jp/profile",
                "http://recyclekan.ja.shopserve.jp/"
        ));

        ExpandableHeightGridView gridView1 = (ExpandableHeightGridView) findViewById(R.id.gridView1);
        if (gridView1 != null) {
            gridView1.setExpanded(true);
            gridView1.setAdapter(new RawPhotoMainAdapter(mContext, dataList1));
            gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GroupData groupData = (GroupData) parent.getItemAtPosition(position);
                    runActivity(groupData);
                }
            });
        }

        ExpandableHeightGridView gridView2 = (ExpandableHeightGridView) findViewById(R.id.gridView2);
        if (gridView2 != null) {
            gridView2.setExpanded(true);
            gridView2.setAdapter(new RawPhotoMainAdapter(mContext, dataList2));
            gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GroupData groupData = (GroupData) parent.getItemAtPosition(position);
                    runActivity(groupData);
                }
            });
        }
    }

    public void runActivity(GroupData groupData) {
        Intent intent = new Intent(this, RawPhotoSelectActivity.class);
        intent.putExtra("groupData", groupData);
        //showToolbarProgressBar();
        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //hideToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
