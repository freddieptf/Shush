package com.freddieptf.shush.calendar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.freddieptf.shush.BuildConfig;
import com.freddieptf.shush.R;
import com.freddieptf.shush.calendar.ui.frags.CalendarEventsFragment;
import com.freddieptf.shush.calNextras.ui.frags.CustomGroupFragment;
import com.freddieptf.shush.calNextras.ui.frags.NavFrag;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.drawer_layout) DrawerLayout drawer;
    @Bind(R.id.nav_view_container) FrameLayout navBarFrame;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @BindDimen(R.dimen.navBarMargin) int navBarMargin;
    @BindDimen(R.dimen.navDrawerWidth) int navBarMaxWidth;
    ActionBarDrawerToggle toggle;

    int currentPos = -1; String currentName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if(BuildConfig.isCalOnly) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_container, new NavFrag()).commit();
            toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawer.openDrawer(GravityCompat.START);
                }
            });

            if (savedInstanceState != null) {
                currentPos = savedInstanceState.containsKey("pos") ? savedInstanceState.getInt("pos") : -1;
                currentName = savedInstanceState.containsKey("name") ? savedInstanceState.getString("name") : "";
            }
        }

        setSupportActionBar(toolbar);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(currentPos != -1) inflateFrag(currentPos, currentName);
        else inflateFrag(0, "Calendar Events");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pos", currentPos);
        outState.putString("name", currentName);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void selectDrawerPos(final int position, final String name){
        drawer.closeDrawer(GravityCompat.START);
        drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                inflateFrag(position, name);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    final public static String TABlE_NAME_KEY = "tkey";
    private void inflateFrag(int position, String name) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(name);
        if(fragment == null) {
            switch (position) {
                case 0:
                    fragment = new CalendarEventsFragment();
                    break;
                default:
                    Bundle bundle = new Bundle();
                    bundle.putString(TABlE_NAME_KEY, name);
                    fragment = new CustomGroupFragment();
                    fragment.setArguments(bundle);
                    break;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment, name)
                    .commit();
        }

        toolbar.setSubtitle(name);
        currentPos = position;
        currentName = name;
    }

}
