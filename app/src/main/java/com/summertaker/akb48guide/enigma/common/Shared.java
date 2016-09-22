package com.summertaker.akb48guide.enigma.common;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.summertaker.akb48guide.enigma.engine.Engine;
import com.summertaker.akb48guide.enigma.events.EventBus;

public class Shared {

    public static Context context;
    public static FragmentActivity activity; // it's fine for this app, but better move to weak reference
    public static Engine engine;
    public static EventBus eventBus;

}
