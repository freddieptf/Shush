package com.freddieptf.shush.calendar.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.freddieptf.shush.R;
import com.freddieptf.shush.calendar.Utils.PrefUtils;
import com.freddieptf.shush.calendar.data.EventsRepository;
import com.freddieptf.shush.calendar.data.model.Event;
import com.freddieptf.shush.calendar.ui.widget.AutoFitRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fred on 12/7/15.
 */
public class EventsFragment extends Fragment implements EventsView,
        EventsRepository.RepositoryObserver {

    private EventsAdapter adapter;
    private EventsPresenter presenter;
    private static final String TAG = "CalendarEventsFragment";
    @Bind(R.id.recycler) AutoFitRecyclerView recyclerView;
    @Bind(R.id.placeholder) LinearLayout placeholder;

    @OnClick(R.id.button_sync)
    public void sync(){
        if(ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, 100);
        }else {
            PrefUtils.writeSyncPref(getContext(), true);
            if(presenter != null) presenter.subscribe(getContext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            PrefUtils.writeSyncPref(getContext(), true);
            if(presenter != null) presenter.subscribe(getContext());
        }else {
            //@ToDo what if i get rejected goddamit
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        adapter = new EventsAdapter();
        adapter.setClickCallback(event -> {
            Intent intent = new Intent(getActivity(), EventActivity.class);
            intent.putExtra(EventActivity.EVENT_KEY, event);
            getActivity().startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(presenter != null &&
                ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED){
            presenter.subscribe(getContext());
            presenter.addObserver(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(presenter != null){
            presenter.unSubscribe();
            presenter.removeObserver(this);
        }
    }

    @Override
    public void setPresenter(BasePresenter presenter) {
        this.presenter = (EventsPresenter) presenter;
    }

    @Override
    public void onLoadEvents(List<Event> events) {
        Log.d(TAG, "onLoadEvents: " + events.size());
        adapter.swapData(events);
    }

    @Override
    public void notifyDataChange() {
        if(presenter != null) presenter.subscribe(getContext());
    }

    @Override
    public void showProgress(boolean show) {

    }

    @Override
    public void showEmptyView(boolean show) {
        if(show) placeholder.setVisibility(View.VISIBLE);
        else placeholder.setVisibility(View.GONE);
    }
}
