package com.summertaker.akb48guide.enigma.engine;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.enigma.common.Shared;
import com.summertaker.akb48guide.enigma.events.EventObserverAdapter;
import com.summertaker.akb48guide.enigma.events.ui.StartEvent;
import com.summertaker.akb48guide.enigma.events.ui.ThemeSelectedEvent;
import com.summertaker.akb48guide.enigma.themes.Theme;
import com.summertaker.akb48guide.enigma.themes.Themes;
import com.summertaker.akb48guide.enigma.utils.Utils;

public class Engine extends EventObserverAdapter {

	private static Engine mInstance = null;
	private ScreenController mScreenController;
	private Handler mHandler;

	private ImageView mBackgroundImage;
    private Theme mSelectedTheme;

	private Engine() {
		mScreenController = ScreenController.getInstance();
		mHandler = new Handler();
	}

	public static Engine getInstance() {
		if (mInstance == null) {
			mInstance = new Engine();
		}
		return mInstance;
	}

    public void setBackgroundImageView(ImageView backgroundImage) {
        mBackgroundImage = backgroundImage;
    }

    public void start() {
        //Shared.eventBus.listen(DifficultySelectedEvent.TYPE, this);
        //Shared.eventBus.listen(FlipCardEvent.TYPE, this);
        Shared.eventBus.listen(StartEvent.TYPE, this);
        Shared.eventBus.listen(ThemeSelectedEvent.TYPE, this);
        //Shared.eventBus.listen(BackGameEvent.TYPE, this);
        //Shared.eventBus.listen(NextGameEvent.TYPE, this);
        //Shared.eventBus.listen(ResetBackgroundEvent.TYPE, this);
    }

    public void stop() {
        //mPlayingGame = null;
        mBackgroundImage.setImageDrawable(null);
        mBackgroundImage = null;
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;

        //Shared.eventBus.unlisten(DifficultySelectedEvent.TYPE, this);
        //Shared.eventBus.unlisten(FlipCardEvent.TYPE, this);
        Shared.eventBus.unlisten(StartEvent.TYPE, this);
        Shared.eventBus.unlisten(ThemeSelectedEvent.TYPE, this);
        //Shared.eventBus.unlisten(BackGameEvent.TYPE, this);
        //Shared.eventBus.unlisten(NextGameEvent.TYPE, this);
        //Shared.eventBus.unlisten(ResetBackgroundEvent.TYPE, this);

        mInstance = null;
    }

    @Override
    public void onEvent(StartEvent event) {
        mScreenController.openScreen(ScreenController.Screen.THEME_SELECT);
    }

    @Override
    public void onEvent(ThemeSelectedEvent event) {
        mSelectedTheme = event.theme;

        //mScreenController.openScreen(ScreenController.Screen.DIFFICULTY);
        Toast.makeText(Shared.context, "Here.", Toast.LENGTH_SHORT).show();

        AsyncTask<Void, Void, TransitionDrawable> task = new AsyncTask<Void, Void, TransitionDrawable>() {

            @Override
            protected TransitionDrawable doInBackground(Void... params) {
                Bitmap bitmap = Utils.scaleDown(R.drawable.enigma_background, Utils.screenWidth(), Utils.screenHeight());
                Bitmap backgroundImage = Themes.getBackgroundImage(mSelectedTheme);
                backgroundImage = Utils.crop(backgroundImage, Utils.screenHeight(), Utils.screenWidth());
                Drawable backgrounds[] = new Drawable[2];
                backgrounds[0] = new BitmapDrawable(Shared.context.getResources(), bitmap);
                backgrounds[1] = new BitmapDrawable(Shared.context.getResources(), backgroundImage);
                TransitionDrawable crossfader = new TransitionDrawable(backgrounds);
                return crossfader;
            }

            @Override
            protected void onPostExecute(TransitionDrawable result) {
                super.onPostExecute(result);
                mBackgroundImage.setImageDrawable(result);
                result.startTransition(2000);
            }
        };
        task.execute();
    }
}
