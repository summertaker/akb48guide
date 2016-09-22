package com.summertaker.akb48guide.enigma;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.enigma.common.Shared;
import com.summertaker.akb48guide.enigma.engine.Engine;
import com.summertaker.akb48guide.enigma.engine.ScreenController;
import com.summertaker.akb48guide.enigma.events.EventBus;
import com.summertaker.akb48guide.enigma.utils.Utils;

public class EnigmaMainActivity extends FragmentActivity {

    private ImageView mBackgroundImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enigma_main_activity);

        Shared.activity = this;
        Shared.context = getApplicationContext();
        Shared.engine = Engine.getInstance();
        Shared.eventBus = EventBus.getInstance();

        setBackgroundImage();

        Shared.engine.setBackgroundImageView(mBackgroundImage);
        Shared.engine.start();

        ScreenController.getInstance().openScreen(ScreenController.Screen.MENU);
    }

    @Override
    protected void onDestroy() {
        Shared.engine.stop();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //if (PopupManager.isShown()) {
        //    PopupManager.closePopup();
        //    if (ScreenController.getLastScreen() == ScreenController.Screen.GAME) {
        //        Shared.eventBus.notify(new BackGameEvent());
        //    }
        //} else if (ScreenController.getInstance().onBack()) {
            super.onBackPressed();
        //}
    }

    private void setBackgroundImage() {
        Bitmap bitmap = Utils.scaleDown(R.drawable.enigma_background, Utils.screenWidth(), Utils.screenHeight());
        bitmap = Utils.crop(bitmap, Utils.screenHeight(), Utils.screenWidth());
        bitmap = Utils.downscaleBitmap(bitmap, 2);

        mBackgroundImage = (ImageView) findViewById(R.id.background_image);
        mBackgroundImage.setImageBitmap(bitmap);
    }
}
