package com.freddieptf.shush.calendar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.freddieptf.shush.R;
import com.freddieptf.shush.calendar.data.EventsRepository;
import com.freddieptf.shush.calendar.data.CalendarChangeService;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EventsActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("calendar_events");
        if(fragment == null) fragment = new EventsFragment();

        EventsRepository repository = EventsRepository.getInstance();
        EventsPresenter presenter = new EventsPresenter((EventsFragment) fragment, repository);

        getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment, "calendar_events")
                    .commit();

        toolbar.setSubtitle("Calendar Events");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
            return true;
        }else if(id ==R.id.action_sync){
            Intent intent = new Intent(this, CalendarChangeService.class);
            startService(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
