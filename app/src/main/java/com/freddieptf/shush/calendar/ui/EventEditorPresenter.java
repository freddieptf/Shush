package com.freddieptf.shush.calendar.ui;

import android.content.Context;

import com.freddieptf.shush.calendar.data.EventsRepository;
import com.freddieptf.shush.calendar.data.model.Event;
import com.freddieptf.shush.calendar.data.model.ShushProfile;

/**
 * Created by freddieptf on 25/10/16.
 */

public class EventEditorPresenter implements BasePresenter {

    private final Event event;
    private final EventEditorView view;

    public EventEditorPresenter(Event event, EventEditorView view){
        this.event = event;
        this.view = view;
        view.setPresenter(this);
    }

    public void startLoad(){
        view.onEventLoad(event.getName(), event.getShushProfile());
    }

    public void saveShushProfile(Context context, ShushProfile profile){
        event.addShushProfile(profile);
        long id = EventsRepository.getInstance().saveShushProfile(context, event.getId(), profile);
        if(id > -1) view.onShushProfileSaved(event, "");
        else view.onShushProfileSaved(null, "Could not save ShushProfile");
    }

}
