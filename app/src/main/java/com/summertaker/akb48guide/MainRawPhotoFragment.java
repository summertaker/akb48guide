package com.summertaker.akb48guide;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.summertaker.akb48guide.common.BaseFragment;
import com.summertaker.akb48guide.common.BaseFragmentInterface;
import com.summertaker.akb48guide.data.DataManager;
import com.summertaker.akb48guide.data.SiteData;

import java.util.ArrayList;

public class MainRawPhotoFragment extends BaseFragment implements BaseFragmentInterface {

    private int mPosition;
    private String mAction;

    private Callback mCallback;

    public interface Callback {
        void onRawPhotoSiteSelected(String action, SiteData siteData);

        void onError(String message);
    }

    public static MainRawPhotoFragment newInstance(int position, String action) {
        MainRawPhotoFragment fragment = new MainRawPhotoFragment();

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("action", action);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = container.getContext();
        View rootView = inflater.inflate(R.layout.main_raw_photo_fragment, container, false);

        //mPosition = getArguments().getInt("position", 0);

        DataManager dataManager = new DataManager(mContext);
        ArrayList<SiteData> electionList = dataManager.getRawPhotoSiteList();

        MainRawPhotoAdapter gridAdapter = new MainRawPhotoAdapter(mContext, electionList);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SiteData siteData = (SiteData) parent.getItemAtPosition(position);
                mCallback.onRawPhotoSiteSelected(mAction, siteData);
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
