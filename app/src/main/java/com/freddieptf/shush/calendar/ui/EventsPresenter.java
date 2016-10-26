package com.freddieptf.shush.calendar.ui;

import android.content.Context;
import android.util.Log;

import com.freddieptf.shush.calendar.data.EventsRepository;
import com.freddieptf.shush.calendar.data.model.Event;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by freddieptf on 19/10/16.
 */

public class EventsPresenter implements BasePresenter, Observer<List<Event>> {

    private final EventsView view;
    private EventsRepository repository;
    private static final String TAG = "CalendarEventsPresenter";
    private CompositeSubscription compositeSubscription;

    public EventsPresenter(EventsView view, EventsRepository repository){
        this.view = view;
        this.repository = repository;
        compositeSubscription = new CompositeSubscription();
        view.setPresenter(this);
    }

    public void subscribe(Context context){
        if(context != null) {
            view.showProgress(true);
            Subscription subscription = repository.getEvents(context)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this);
            compositeSubscription.add(subscription);
        }
    }

    public void unSubscribe(){
        compositeSubscription.clear();
    }

    public void addObserver(EventsRepository.RepositoryObserver observer){
        repository.addRepositoryObserver(observer);
    }

    public void removeObserver(EventsRepository.RepositoryObserver observer){
        repository.removeObserver(observer);
    }

    @Override
    public void onCompleted() {
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
}
