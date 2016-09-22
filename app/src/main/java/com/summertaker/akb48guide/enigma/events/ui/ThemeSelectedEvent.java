package com.summertaker.akb48guide.enigma.events.ui;

import com.summertaker.akb48guide.enigma.events.AbstractEvent;
import com.summertaker.akb48guide.enigma.events.EventObserver;
import com.summertaker.akb48guide.enigma.themes.Theme;

public class ThemeSelectedEvent extends AbstractEvent {

	public static final String TYPE = ThemeSelectedEvent.class.getName();
	public final Theme theme;

	public ThemeSelectedEvent(Theme theme) {
		this.theme = theme;
	}

	@Override
	protected void fire(EventObserver eventObserver) {
		eventObserver.onEvent(this);
	}

	@Override
	public String getType() {
		return TYPE;
	}

}
