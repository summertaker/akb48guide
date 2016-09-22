package com.summertaker.akb48guide.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.BaseFragment;
import com.summertaker.akb48guide.common.BaseFragmentInterface;
import com.summertaker.akb48guide.data.DataManager;
import com.summertaker.akb48guide.data.SiteData;

import java.util.ArrayList;

public class MainYoutubeFragment extends BaseFragment implements BaseFragmentInterface {

    private int mPosition;
    private String mAction;

    private Callback mCallback;

    public interface Callback {
        void onYoutubeSelected(String action, SiteData siteData);

        void onError(String message);
    }

    public static MainYoutubeFragment newInstance(int position, String action) {
        MainYoutubeFragment fragment = new MainYoutubeFragment();

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("action", action);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = container.getContext();
        View rootView = inflater.inflate(R.layout.main_youtube_fragment, container, false);

        //mPosition = getArguments().getInt("position", 0);

        /*int[] pics = {R.drawable.pic_1, R.drawable.pic_2, R.drawable.pic_3, R.drawable.pic_4};
        Random random = new Random();
        int max = 3;
        int min = 0;
        int selected = random.nextInt(max - min + 1) + min;
        */
        //ImageView ivHeader = (ImageView) rootView.findViewById(R.id.ivHeader);
        //ivHeader.setImageResource(pics[selected]);

        DataManager dataManager = new DataManager(mContext);
        ArrayList<SiteData> dataList = dataManager.getYoutubeList();

        MainYoutubeAdapter gridAdapter = new MainYoutubeAdapter(mContext, dataList);
        //GridView gridView = (GridView) rootView.findViewById(R.id.gridView);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
        //ExpandableHeightGridView listView = (ExpandableHeightGridView) rootView.findViewById(R.id.gridView);
        //listView.setExpanded(true);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SiteData siteData = (SiteData) parent.getItemAtPosition(position);
                mCallback.onYoutubeSelected(mAction, siteData);
            }
        });

        return rootView;
    }

    public int getPosition() {
        return mPosition;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;
        if (context instanceof Activity) {
            activity = (Activity) context;

            try {
                mCallback = (Callback) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement Listener for Fragment.");
            }
        }
    }

    @Override
    public void refresh(String articleId) {

    }

    @Override
    public boolean canGoBack() {
        return false;
    }

    @Override
    public void goBack() {

    }
}
