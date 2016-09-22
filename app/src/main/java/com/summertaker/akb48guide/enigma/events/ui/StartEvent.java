package com.summertaker.akb48guide.enigma.events.ui;

import com.summertaker.akb48guide.enigma.events.AbstractEvent;
import com.summertaker.akb48guide.enigma.events.EventObserver;

/**
 * When the 'back to menu' was pressed.
 */
public class StartEvent extends AbstractEvent {

	public static final String TYPE = StartEvent.class.getName();

	@Override
	protected void fire(EventObserver eventObserver) {
		eventObserver.onEvent(this);
	}

	@Override
	public String getType() {
		return TYPE;
	}

}
