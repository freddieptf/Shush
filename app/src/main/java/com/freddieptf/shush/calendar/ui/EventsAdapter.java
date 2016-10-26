package com.freddieptf.shush.calendar.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freddieptf.shush.R;
import com.freddieptf.shush.calendar.data.model.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by fred on 12/9/15.
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewholder>
        implements View.OnClickListener {

    private static final String TAG = "EventsAdapter";
    private List<Event> list;
    private clickCallback clickCallback;

    public EventsAdapter(){}

    public void swapData(List<Event> list){
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
        holder.itemView.setTag(position);
        holder.title.setText(list.get(position).getName());
        holder.date.setText(getDateString(list.get(position).getStartTime(), list.get(position).getEndTime()));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setClickCallback(clickCallback clickCallback){
        this.clickCallback = clickCallback;
    }

    @Override
    public void onClick(View view) {
        int pos = (Integer) view.getTag();
        clickCallback.onClick(list.get(pos));
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

    private String getDateString(long startDateMs, long endDateMs){
        String simpleTime = "h:mm a";
        String daySimpleTime = "EEEE, h:mm a";
        String extended = "EEEE, MMM dd. hh:mm";
        String result = "";
        SimpleDateFormat format;

        Calendar futureDate = Calendar.getInstance(), cal = futureDate;
        futureDate.add(Calendar.DAY_OF_WEEK, 7);

        try {
            Date startdate = new Date(startDateMs);
            Date enddate = new Date(endDateMs);

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
            Log.d(TAG, e.getMessage());
            return "";
        }

    }

    public interface clickCallback {
        void onClick(Event event);
    }
}
