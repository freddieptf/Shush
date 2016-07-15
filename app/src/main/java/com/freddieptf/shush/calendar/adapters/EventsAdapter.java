package com.freddieptf.shush.calendar.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freddieptf.shush.calendar.Log;
import com.freddieptf.shush.R;
import com.freddieptf.shush.calendar.Utils.ColorUtils;
import com.freddieptf.shush.calendar.model.EventModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by fred on 12/9/15.
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewholder>
        implements View.OnClickListener, View.OnLongClickListener {

    final String LOG_TAG = getClass().getSimpleName();

    List<EventModel> list;
    Context context;
    ColorUtils colorUtils;
    onEventClick onEventClick;
    onEventLongClick onEventLongClick;

    public EventsAdapter(Context context){
        list = new ArrayList<>();
        this.context = context;
        colorUtils = new ColorUtils(context);
    }

    public void swapData(List<EventModel> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public EventsViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventsViewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false));
    }

    @Override
    public void onBindViewHolder(EventsViewholder holder, int position) {
        holder.itemView.setOnClickListener(this);
        holder.itemView.setOnLongClickListener(this);
        holder.itemView.setTag(position);
        holder.title.setText(list.get(position).getTitle());
        holder.date.setText(getDateString(list.get(position).getStart(), list.get(position).getEnd()));
        Log.d(LOG_TAG, list.get(position).getTitle() + " " + list.get(position).getId());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void saveState(Bundle outState){

    }

    public void restoreState(Bundle restore){

    }

    public void setOnEventClick(onEventClick onEventClick){
        this.onEventClick = onEventClick;
    }

    public void setOnEventLongClick(onEventLongClick onEventLongClick){
        this.onEventLongClick = onEventLongClick;
    }

    @Override
    public void onClick(View view) {
        int pos = (Integer) view.getTag();
        onEventClick.onClick(list.get(pos));
    }

    @Override
    public boolean onLongClick(View view) {
        int pos = (Integer) view.getTag();
        if(onEventLongClick != null) onEventLongClick.onLongClick(list.get(pos));
        return true;
    }

    class EventsViewholder extends RecyclerView.ViewHolder{
        @Bind(R.id.event_title) TextView title;
        @Bind(R.id.event_date) TextView date;
        @Bind(R.id.event_duration) TextView duration;
        public EventsViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    String getDateString(String startDateMs, String endDateMs){
        String simpleTime = "h:mm a";
        String daySimpleTime = "EEEE, h:mm a";
        String extended = "EEEE, MMM dd. hh:mm";
        String result = "";
        SimpleDateFormat format;

        Calendar futureDate = Calendar.getInstance(), cal = futureDate;
        futureDate.add(Calendar.DAY_OF_WEEK, 7);

        try {
            Date startdate = new Date(Long.parseLong(startDateMs));
            Date enddate = new Date(Long.parseLong(endDateMs));

            if(startdate.before(futureDate.getTime())){
                cal.setTime(startdate);
                futureDate.setTime(enddate);
                if(cal.get(Calendar.DAY_OF_WEEK) == futureDate.get(Calendar.DAY_OF_WEEK)){
                    format = new SimpleDateFormat(daySimpleTime);
                    result = result.concat(format.format(startdate));

                    format = new SimpleDateFormat(simpleTime);
                    result = result.concat(" - " + format.format(enddate));

                }else{


                }



            }else {


            }

            return result;
        }catch (NumberFormatException | NullPointerException e){
            Log.d(LOG_TAG, e.getMessage());
            return "";
        }

    }

    public interface onEventClick{
        void onClick(EventModel event);
    }

    public interface onEventLongClick{
        void onLongClick(EventModel event);
    }
}
