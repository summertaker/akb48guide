package com.summertaker.akb48guide.enigma.engine;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.enigma.common.Shared;
import com.summertaker.akb48guide.enigma.fragments.MenuFragment;
import com.summertaker.akb48guide.enigma.fragments.ThemeSelectFragment;

import java.util.ArrayList;
import java.util.List;

public class ScreenController {

	private static ScreenController mInstance = null;

    private FragmentManager mFragmentManager;

    private static List<Screen> openedScreens = new ArrayList<Screen>();

	private ScreenController() {
	}

	public static ScreenController getInstance() {
		if (mInstance == null) {
			mInstance = new ScreenController();
		}
		return mInstance;
	}

	public static enum Screen {
		MENU,
		GAME,
		DIFFICULTY,
		THEME_SELECT
	}

    public void openScreen(Screen screen) {
        mFragmentManager = Shared.activity.getSupportFragmentManager();

        if (screen == Screen.GAME && openedScreens.get(openedScreens.size() - 1) == Screen.GAME) {
            openedScreens.remove(openedScreens.size() - 1);
        } else if (screen == Screen.DIFFICULTY && openedScreens.get(openedScreens.size() - 1) == Screen.GAME) {
            openedScreens.remove(openedScreens.size() - 1);
            openedScreens.remove(openedScreens.size() - 1);
        }
        Fragment fragment = getFragment(screen);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
        openedScreens.add(screen);
    }

    private Fragment getFragment(Screen screen) {
        switch (screen) {
            case MENU:
                return new MenuFragment();
            case THEME_SELECT:
                return new ThemeSelectFragment();
            case DIFFICULTY:
                //return new DifficultySelectFragment();
            case GAME:
                //return new GameFragment();
            default:
                break;
        }
        return null;
    }
}
