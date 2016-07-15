package com.freddieptf.shush.calNextras.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.freddieptf.shush.R;
import com.freddieptf.shush.calendar.data.DbHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by fred on 12/7/15.
 */
public class NavAdapter extends RecyclerView.Adapter<NavAdapter.NavVHolder> implements
        View.OnClickListener, View.OnLongClickListener {

    List<String> stringList;
    OnNavItemClick onNavItemClick;
    Context context;
    private static final String LOG_TAG = "NavAdapter";

    public NavAdapter(Context context){
        this.context = context;
    }

    public void populateListData(){
        stringList = new ArrayList<>();
        stringList.add("Calendar Events");
        listTables(stringList);
    }

    public String getName(int position){
        return stringList.get(position);
    }

    public void listTables(List<String> list){
        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table'", null);
        if(cursor.moveToPosition(2)){
            while (cursor.moveToNext()) {
                Log.d(LOG_TAG, cursor.getString(0));
                if(!cursor.getString(0).contains("sqlite_sequence")) list.add(cursor.getString(0));
            }
        }
        if(!cursor.isClosed()) cursor.close();
        if(db.isOpen()) db.close();
    }

    @Override
    public NavVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NavVHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nav, parent, false));
    }


    @Override
    public void onBindViewHolder(NavVHolder holder, int position) {
        holder.itemView.setOnClickListener(this);
        holder.itemView.setOnLongClickListener(this);
        holder.itemView.setTag(position);

        if(position == stringList.size()){
            holder.textView.setText(R.string.add_event_group);
            holder.icon.setImageResource(R.drawable.ic_add_black_24dp);
        } else if(position == 0) {
            holder.icon.setImageResource(R.drawable.ic_event_black_24dp);
            holder.textView.setText(stringList.get(position));
        } else {
            holder.textView.setText(stringList.get(position));
            holder.icon.setImageResource(R.drawable.ic_label_black_24dp);
        }

    }

    @Override
    public int getItemCount() {
        return stringList.size() + 1;
    }

    public void setOnClickListener(OnNavItemClick onNavItemClick){
        this.onNavItemClick = onNavItemClick;
    }

    @Override
    public void onClick(View view) {
        int pos = (int) view.getTag();
        if(pos == stringList.size()) onNavItemClick.onCreateGroupClick(pos);
        else {
            onNavItemClick.onGroupClick(pos, stringList.get(pos));
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int pos = (int) view.getTag();
        if(pos == stringList.size() || pos == 0){}
        else onNavItemClick.onGroupLongClick(pos, stringList.get(pos));
        return true;
    }

    class NavVHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.nav_text) TextView textView;
        @Bind(R.id.icon) ImageView icon;
        public NavVHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnNavItemClick {
        void onGroupClick(int pos, String name);
        void onGroupLongClick(int pos, String name);
        void onCreateGroupClick(int pos);
    }
}
