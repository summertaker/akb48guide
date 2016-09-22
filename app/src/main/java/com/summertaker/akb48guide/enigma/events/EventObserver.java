package com.summertaker.akb48guide.enigma.events;

import com.summertaker.akb48guide.enigma.events.ui.StartEvent;
import com.summertaker.akb48guide.enigma.events.ui.ThemeSelectedEvent;

public interface EventObserver {

	/*void onEvent(FlipCardEvent event);

	void onEvent(DifficultySelectedEvent event);

	void onEvent(HidePairCardsEvent event);

	void onEvent(FlipDownCardsEvent event);*/

	void onEvent(StartEvent event);

	void onEvent(ThemeSelectedEvent event);

	/*void onEvent(GameWonEvent event);

	void onEvent(BackGameEvent event);

	void onEvent(NextGameEvent event);

	void onEvent(ResetBackgroundEvent event);*/

}
