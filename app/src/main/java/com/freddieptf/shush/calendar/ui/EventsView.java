package com.freddieptf.shush.calendar.ui;

import com.freddieptf.shush.calendar.data.model.Event;

import java.util.List;

/**
 * Created by freddieptf on 20/08/16.
 */
public interface EventsView extends BaseView{
    void onLoadEvents(List<Event> events);
    void showProgress(boolean show);
    void showEmptyView(boolean show);
}
