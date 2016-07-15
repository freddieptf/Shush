package com.freddieptf.shush.calNextras.ui.frags;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.freddieptf.shush.R;
import com.freddieptf.shush.calNextras.ui.views.CreateEventChooser;
import com.freddieptf.shush.calNextras.ui.views.CreateEventDialog;
import com.freddieptf.shush.calendar.Utils.AlarmUtils;
import com.freddieptf.shush.calendar.adapters.EventsAdapter;
import com.freddieptf.shush.calendar.adapters.EventsLoader;
import com.freddieptf.shush.calendar.data.DbHelper;
import com.freddieptf.shush.calendar.model.EventModel;
import com.freddieptf.shush.calendar.ui.MainActivity;
import com.freddieptf.shush.calendar.ui.ViewEventActivity;
import com.freddieptf.shush.calendar.ui.views.AutoFitRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fred on 12/7/15.
 */
public class CustomGroupFragment extends Fragment {

    @Bind(R.id.recycler) AutoFitRecyclerView recyclerView;
    @Bind(R.id.text) TextView textView;
    EventsAdapter eventsAdapter;
    private static final String TAG = "CustomGroupFragment";
    final int LOADER_ID = 454;

    @OnClick(R.id.fab_add) void onclick(){
        CreateEventChooser chooser = new CreateEventChooser();
        Bundle b = new Bundle();
        b.putString(MainActivity.TABlE_NAME_KEY, tableName);
        chooser.setArguments(b);
        chooser.setStyle(DialogFragment.STYLE_NO_TITLE,
                android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);
        chooser.show(getActivity().getSupportFragmentManager(), "ev");
    }

    String tableName;
    DbHelper dbHelper;

    private LoaderManager.LoaderCallbacks<List<EventModel>> eventLoader = new LoaderManager.LoaderCallbacks<List<EventModel>>() {
        @Override
        public Loader<List<EventModel>> onCreateLoader(int id, Bundle args) {
            return new EventsLoader(getContext(), tableName);
        }

        @Override
        public void onLoadFinished(Loader<List<EventModel>> loader, List<EventModel> data) {
            if(textView != null) {
                if (data.size() > 0)
                    textView.setVisibility(View.GONE);
                else textView.setVisibility(View.VISIBLE);
                eventsAdapter.swapData(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<EventModel>> loader) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customgroup_events, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, eventLoader);
        android.util.Log.d(TAG, "onActivityCreated: " + tableName);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        tableName = getArguments().getString(MainActivity.TABlE_NAME_KEY);
        eventsAdapter = new EventsAdapter(getContext());
        dbHelper = new DbHelper(getContext());

        eventsAdapter.setOnEventClick(new EventsAdapter.onEventClick() {
            @Override
            public void onClick(EventModel event) {
                Intent intent = new Intent(getActivity(), ViewEventActivity.class);
                intent.putExtra(ViewEventActivity.EVENT_KEY, event);
                intent.putExtra(MainActivity.TABlE_NAME_KEY, tableName);
                getActivity().startActivity(intent);
            }
        });

        eventsAdapter.setOnEventLongClick(new EventsAdapter.onEventLongClick() {
            @Override
            public void onLongClick(final EventModel event) {
                final MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext());
                builder.title("Delete Event")
                        .content("Are you sure you want to delete " + event.getTitle())
                        .negativeText("No")
                        .positiveText("Yes");
                MaterialDialog dialog = builder.build();
                builder.onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });
                builder.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dbHelper.deleteRowById(tableName, event.getId());
                        AlarmUtils alarmUtils = new AlarmUtils(getContext(), event);
                        alarmUtils.cancelAlarm();
                        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, eventLoader);
                    }
                });
                dialog.show();
            }
        });
        recyclerView.setAdapter(eventsAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(CreateEventDialog.EVENT_CREATED));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "receive");
            if(intent.getAction().equals(CreateEventDialog.EVENT_CREATED)){
                getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, eventLoader);
            }
        }
    };
}
