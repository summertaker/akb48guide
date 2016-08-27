package com.summertaker.akb48guide.janken;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.summertaker.akb48guide.R;

public class TransitionFragment extends Fragment {

    public TransitionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.transition_scene_1, container, false);
        return rootView;
    }

}