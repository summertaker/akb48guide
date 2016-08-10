package com.summertaker.akb48guide.election;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.BaseActivity;
import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.data.DataManager;
import com.summertaker.akb48guide.data.ElectionData;

import java.util.ArrayList;

public class ElectionListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.election_list_activity);

        initBaseToolbar(Config.TOOLBAR_ICON_BACK, getString(R.string.election));

        DataManager dataManager = new DataManager(mContext);
        ArrayList<ElectionData> electionList = dataManager.getElectionList();

        ListView listView = (ListView) findViewById(R.id.listView);
        if (listView != null) {
            ElectionListAdapter dataAdapter = new ElectionListAdapter(mContext, electionList);
            listView.setAdapter(dataAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ElectionData electionData = (ElectionData) parent.getItemAtPosition(position);

                    Intent intent = new Intent(mContext, VoteListActivity.class);
                    intent.putExtra("electionData", electionData);

                    startActivityForResult(intent, 0);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //hideToolbarProgressBar();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
