package com.summertaker.akb48guide.enigma.common;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.summertaker.akb48guide.R;

public class Music {

	public static boolean OFF = false;

	public static void playCorrent() {
		if (!OFF) {
			/*MediaPlayer mp = MediaPlayer.create(Shared.context, R.raw.correct_answer);
			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.reset();
					mp.release();
					mp = null;
				}

			});
			mp.start();*/
		}
	}

	public static void playBackgroundMusic() {

	}

	public static void showStar() {
		if (!OFF) {
			/*MediaPlayer mp = MediaPlayer.create(Shared.context, R.raw.star);
			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.reset();
					mp.release();
					mp = null;
				}

			});
			mp.start();*/
		}
	}
}
