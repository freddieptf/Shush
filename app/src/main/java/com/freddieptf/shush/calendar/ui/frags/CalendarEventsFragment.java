package com.freddieptf.shush.calendar.ui.frags;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.freddieptf.shush.R;
import com.freddieptf.shush.calendar.Utils.PrefUtils;
import com.freddieptf.shush.calendar.adapters.EventsAdapter;
import com.freddieptf.shush.calendar.adapters.EventsLoader;
import com.freddieptf.shush.calendar.data.DbHelper;
import com.freddieptf.shush.calendar.model.EventModel;
import com.freddieptf.shush.calendar.ui.ViewEventActivity;
import com.freddieptf.shush.calendar.ui.views.AutoFitRecyclerView;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fred on 12/7/15.
 */
public class CalendarEventsFragment extends Fragment {

    //@TODO ...rewrite this. Create a presenter and view and model?...also maybe try some RxJava here. OKAY BRO?
    public final String LOG_TAG = getClass().getSimpleName();
    int calLoaderId = 10101;
    int eventLoaderId = 10202;
    EventsAdapter adapter;
    PrefUtils prefUtils;

    @OnClick(R.id.button_sync)
    public void syncCalEvents(){
        if(ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, 100);
        }else {
            getActivity().getSupportLoaderManager().initLoader(calLoaderId, null, calendarLoader);
            prefUtils.writeSyncPref(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getActivity().getSupportLoaderManager().initLoader(calLoaderId, null, calendarLoader);
            prefUtils.writeSyncPref(true);
        }else {
            //@ToDo what if i get rejected goddamit
        }
    }

    @Bind(R.id.recycler) AutoFitRecyclerView recyclerView;
    @Bind(R.id.placeholder) LinearLayout placeholder;

    public static String[] EVENTS_PROJECTION = { CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.DURATION,
            CalendarContract.Events.RDATE,
            CalendarContract.Events.DESCRIPTION,
    };

    public static final int COLUMN_TITLE = 1;
    public static final int COLUMN_START = 2;
    public static final int COLUMN_END = 3;
    public static final int COLUMN_DURATION = 4;

    private LoaderManager.LoaderCallbacks<Cursor> calendarLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(), CalendarContract.Events.CONTENT_URI,
                    EVENTS_PROJECTION, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int processedEvents = processCalendarEvents(getContext(), data);
                    Log.d(LOG_TAG, "Processed: " + processedEvents);
                    if(processedEvents > 0) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().getSupportLoaderManager().restartLoader(eventLoaderId, null, eventLoader);
                            }
                        });
                    }
                }
            });
            thread.start();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };

    private LoaderManager.LoaderCallbacks<List<EventModel>> eventLoader = new LoaderManager.LoaderCallbacks<List<EventModel>>() {
        @Override
        public Loader<List<EventModel>> onCreateLoader(int id, Bundle args) {
            return new EventsLoader(getContext(), DbHelper.CALENDAR_EVENTS_TABLE);
        }

        @Override
        public void onLoadFinished(Loader<List<EventModel>> loader, List<EventModel> data) {
            // this views might be null on resume..crashes...meh
            if(placeholder != null && recyclerView !=  null) {
                if (data == null || data.isEmpty()) placeholder.setVisibility(View.VISIBLE);
                else {
                    if (placeholder.getVisibility() != View.GONE)
                        placeholder.setVisibility(View.GONE);
                    adapter.swapData(data);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                Log.d(LOG_TAG, "eventsLoader: " + data.size());
            }
        }

        @Override
        public void onLoaderReset(Loader<List<EventModel>> loader) {

        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prefUtils = new PrefUtils(getContext());
        getActivity().getSupportLoaderManager().initLoader(eventLoaderId, null, eventLoader);
        if(prefUtils.allowSync())
            getActivity().getSupportLoaderManager().initLoader(calLoaderId, null, calendarLoader);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar_events, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        adapter = new EventsAdapter(getActivity());
        adapter.setOnEventClick(new EventsAdapter.onEventClick() {
            @Override
            public void onClick(EventModel event) {
                Intent intent = new Intent(getActivity(), ViewEventActivity.class);
                intent.putExtra(ViewEventActivity.EVENT_KEY, event);
                getActivity().startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.saveState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) adapter.restoreState(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    // probably does too much
    public static int processCalendarEvents(Context context, Cursor data){
        int i = 0;
        String[] INSTANCE_PROJECTION = new String[] {
                CalendarContract.Instances.EVENT_ID,      // 0
                CalendarContract.Instances.BEGIN,         // 1
                CalendarContract.Instances.END,           // 2
                CalendarContract.Instances.TITLE          // 3
        };
        Calendar currentTime = Calendar.getInstance();
        long startMillis = currentTime.getTimeInMillis();
        currentTime.add(Calendar.DATE, 7); // get the events in the next 7 days
        long endMillis = currentTime.getTimeInMillis();

        Cursor cursor = null;
        ContentResolver contentResolver = context.getContentResolver();
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        String selection = CalendarContract.Instances.EVENT_ID + " = ? ";
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);
        Uri uri = builder.build();

        String id, eventName, begin, end;
        String[] selectionArgs = new String[1];

        while (!data.isClosed() && data.moveToNext()) {
            selectionArgs[0] = data.getString(0);
            cursor = contentResolver.query(uri,
                    INSTANCE_PROJECTION,
                    selection,
                    selectionArgs,
                    null);

            if(cursor != null && cursor.moveToFirst()){
                if(cursor.getString(1) != null && cursor.getString(2) != null){
                    id = data.getString(0);
                    eventName = data.getString(COLUMN_TITLE);
                    begin = cursor.getString(1);
                    end = cursor.getString(2);

                    if(!validateValues(dbHelper, id, eventName, begin, end)) {
                        ContentValues cv = new ContentValues();
                        cv.put(DbHelper.COLUMN_ID, id);
                        cv.put(DbHelper.COLUMN_EVENT_NAME, eventName);
                        cv.put(DbHelper.COLUMN_BEGIN, begin);
                        cv.put(DbHelper.COLUMN_END, end);
                        cv.put(DbHelper.COLUMN_IS_SET, false);

                        if(!dbHelper.eventExists(id)) sqLiteDatabase.insert(DbHelper.CALENDAR_EVENTS_TABLE, null, cv);
                        else sqLiteDatabase.update(DbHelper.CALENDAR_EVENTS_TABLE, cv,
                                DbHelper.COLUMN_ID + " = ? ",  new String[]{id});
                        i++;
                    }
                }
            }

        }

        i += deleteOldEvents(dbHelper, startMillis);

        if(cursor != null && !cursor.isClosed()) cursor.close();
        if(sqLiteDatabase.isOpen()) sqLiteDatabase.close();
        return i;
    }

    public static boolean validateValues(DbHelper dbHelper, String id, String eventName, String begin, String end){
        Cursor cursor = dbHelper.query(new String[]{
                DbHelper.COLUMN_ID,
                DbHelper.COLUMN_EVENT_NAME,
                DbHelper.COLUMN_BEGIN,
                DbHelper.COLUMN_END
        }, DbHelper.COLUMN_ID + " = ? ", new String[]{id}, null);

        if(cursor == null || cursor.getCount() == 0) return false;

        cursor.moveToFirst();

        return eventName.equals(cursor.getString(1))
                && begin.equals(cursor.getString(2))
                && end.equals(cursor.getString(3));
    }

    public static int deleteOldEvents(DbHelper dbHelper, long startMillis){
        Cursor events = dbHelper.query(new String[]{ DbHelper.COLUMN_ID,
                DbHelper.COLUMN_END }, null, null, null);
        int deletes = 0;
        while (events != null && events.moveToNext()){
            if(Long.parseLong(events.getString(1)) < startMillis){
                deletes = dbHelper.deleteRowById(DbHelper.CALENDAR_EVENTS_TABLE, events.getString(0));
            }
        }
        if(events != null && !events.isClosed()) events.close();
        return deletes;
    }

}
