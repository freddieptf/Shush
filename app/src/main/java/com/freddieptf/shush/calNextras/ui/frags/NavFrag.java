package com.freddieptf.shush.calNextras.ui.frags;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freddieptf.shush.R;
import com.freddieptf.shush.calNextras.adapters.NavAdapter;
import com.freddieptf.shush.calNextras.ui.views.InputDialog;
import com.freddieptf.shush.calendar.data.DbHelper;
import com.freddieptf.shush.calendar.ui.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by fred on 12/7/15.
 */
public class NavFrag extends Fragment {

    @Bind(R.id.recycler) RecyclerView recyclerView;
    private static final String LOG_TAG = "NavFrag";
    DbHelper dbHelper;
    NavAdapter navAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navdrawer, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        dbHelper = new DbHelper(getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        navAdapter = new NavAdapter(getContext());
        navAdapter.populateListData();

        navAdapter.setOnClickListener(new NavAdapter.OnNavItemClick() {
            @Override
            public void onGroupClick(int pos, String name) {
                Log.d("NavFrag", name);
                ((MainActivity) getActivity()).selectDrawerPos(pos, name);
            }

            @Override
            public void onGroupLongClick(int pos, String name) {
                dropCustomEventsTable(name);
                navAdapter.populateListData();
                navAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCreateGroupClick(final int pos) {
                Log.d(LOG_TAG, "onCreateGroup");
                final InputDialog inputDialog = new InputDialog();

                inputDialog.setTitle(getString(R.string.create_event_group))
                        .setTextInputLayoutHint(getString(R.string.title))
                        .setNegativeText(getString(R.string.cancel))
                        .setPositiveText(getString(R.string.create));

                inputDialog.show(getActivity().getSupportFragmentManager(), "dialog");
                inputDialog.setStyle(DialogFragment.STYLE_NO_TITLE,
                        android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);

                inputDialog.setClickListener(new InputDialog.OnOptionClick() {
                    @Override
                    public void onCancel() {
                        inputDialog.dismiss();
                    }

                    @Override
                    public void onCreate(String groupName) {
                        Log.d(LOG_TAG, "onCreate: " + groupName);
                        createCustomEventGroupTable(groupName);
                        navAdapter.populateListData();
                        navAdapter.notifyDataSetChanged();
                        inputDialog.dismiss();
                        ((MainActivity) getActivity()).selectDrawerPos(pos, groupName);

                    }
                });
            }
        });
        recyclerView.setAdapter(navAdapter);

    }

    private void createCustomEventGroupTable(String name){
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + name + "(" +
                DbHelper.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbHelper.COLUMN_EVENT_NAME + " TEXT NOT NULL, " +
                DbHelper.COLUMN_BEGIN + " LONG , " +
                DbHelper.COLUMN_END + " LONG , " +
                DbHelper.COLUMN_SILENT_MODE + " INT, " +
                DbHelper.COLUMN_WIFI_SSID + " TEXT, " +
                DbHelper.COLUMN_SET_BRIGHTNESS + " INT, " +
                DbHelper.COLUMN_WIFI_ENABLED + " INT, " +
                DbHelper.COLUMN_DATA_ENABLED + " INT, " +
                DbHelper.COLUMN_TRIGGER + " TEXT NOT NULL, " +
                DbHelper.COLUMN_REPEATS + " BOOLEAN, " +
                DbHelper.COLUMN_IS_SET + " BOOLEAN " + ");";

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(CREATE_TABLE);
        if(db.isOpen()) db.close();
    }

    public void dropCustomEventsTable(String name){
        String DROP_TABLE = "DROP TABLE IF EXISTS " + name;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(DROP_TABLE);
        if(db.isOpen()) db.close();
    }

    public void renameCustomEventsTable(String oldName, String newName){
        String RENAME_TABLE = "ALTER TABLE " + oldName + " RENAME TO " + newName;
    }



}
