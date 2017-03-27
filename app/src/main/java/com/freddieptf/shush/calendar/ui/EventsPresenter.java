package com.freddieptf.shush.calendar.ui;

import android.content.Context;
import android.util.Log;

import com.freddieptf.shush.calendar.data.EventsRepository;
import com.freddieptf.shush.calendar.data.model.Event;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by freddieptf on 19/10/16.
 */

public class EventsPresenter implements BasePresenter, Observer<List<Event>> {

    private final EventsView view;
    private EventsRepository repository;
    private static final String TAG = "CalendarEventsPresenter";

    public EventsPresenter(EventsView view, EventsRepository repository){
        this.view = view;
        this.repository = repository;
        view.setPresenter(this);
    }

    public void subscribe(Context context){
        if(context != null) {
            view.showProgress(true);
            repository.getEvents(context)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this);
        }
    }

    public void unSubscribe(){

    }

    public void addObserver(EventsRepository.RepositoryObserver observer){
        repository.addRepositoryObserver(observer);
    }

    public void removeObserver(EventsRepository.RepositoryObserver observer){
        repository.removeObserver(observer);
    }

    @Override
    public void onComplete() {
        view.showProgress(false);
    }

    @Override
    public void onError(Throwable e) {
        Log.d(TAG, "onError: " + e.getMessage());
    }

    @Override
    public void onNext(List<Event> events) {
        view.onLoadEvents(events);
        if(events.isEmpty()) view.showEmptyView(true);
        else view.showEmptyView(false);
    }

    @Override
    public void onSubscribe(Disposable d) {

    }
}
