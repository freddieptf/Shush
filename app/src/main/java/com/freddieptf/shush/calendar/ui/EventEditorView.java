package com.freddieptf.shush.calendar.ui;

import com.freddieptf.shush.calendar.data.model.Event;
import com.freddieptf.shush.calendar.data.model.ShushProfile;

/**
 * Created by freddieptf on 25/10/16.
 */

public interface EventEditorView extends BaseView {
    void onEventLoad(String eventName, ShushProfile shushProfile);
    void onShushProfileSaved(Event updatedEvent, String error);
}
