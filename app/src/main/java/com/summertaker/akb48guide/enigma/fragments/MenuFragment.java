package com.summertaker.akb48guide.enigma.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.enigma.common.Shared;
import com.summertaker.akb48guide.enigma.events.ui.StartEvent;
import com.summertaker.akb48guide.enigma.utils.Utils;

public class MenuFragment extends Fragment {

    private ImageView mTitle;
    private ImageView mStartGameButton;
    private ImageView mStartButtonLights;
    private ImageView mTooltip;
    private ImageView mSettingsGameButton;
    private ImageView mGooglePlayGameButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.enigma_menu_fragment, container, false);

        mStartGameButton = (ImageView) view.findViewById(R.id.start_game_button);
        mStartGameButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // animate title from place and navigation buttons from place
                animateAllAssetsOff(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Shared.eventBus.notify(new StartEvent());
                    }
                });
            }
        });

        // play background music
        //Music.playBackgroundMusic();

        return view;
    }

    protected void animateAllAssetsOff(AnimatorListenerAdapter adapter) {
        // start button
        ObjectAnimator startButtonAnimator = ObjectAnimator.ofFloat(mStartGameButton, "translationY", Utils.px(130));
        startButtonAnimator.setInterpolator(new AccelerateInterpolator(2));
        startButtonAnimator.setDuration(300);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(startButtonAnimator);
        animatorSet.addListener(adapter);
        animatorSet.start();
    }
}
