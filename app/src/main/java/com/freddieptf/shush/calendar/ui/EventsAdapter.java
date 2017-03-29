package com.freddieptf.shush.calendar.ui;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freddieptf.shush.R;
import com.freddieptf.shush.calendar.Utils.DateUtils;
import com.freddieptf.shush.calendar.data.model.Event;
import com.freddieptf.shush.calendar.ui.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by fred on 12/9/15.
 */
public class EventsAdapter extends RecyclerView.Adapter<BaseViewHolder>
        implements View.OnClickListener {

    private static final String TAG = "EventsAdapter";
    private List<Event> list = new ArrayList<>();
    private ArrayList<Integer> headerMap = new ArrayList<>();
    private clickCallback clickCallback;
    private Random random;

    private int[] colors = {
            R.color.header1, R.color.header2, R.color.header3, R.color.header4, R.color.header5
    };

    public static int VIEW_TYPE_HEADER = 3;
    public static int VIEW_TYPE_SECTION_HEADER = 2;
    public static int VIEW_TYPE_SECTION_ITEM = 1;

    public EventsAdapter(){
        random = new Random();
    }

    public void swapData(List<Event> list){
        this.list = list;
        this.headerMap = generateHeaderMap(list);
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_HEADER)
            return new EventsSectionHeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_section_header, parent, false));
        else
            return new EventsViewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(position);
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return headerMap.indexOf(position) != -1 ? VIEW_TYPE_HEADER : VIEW_TYPE_SECTION_ITEM;
    }

    public void setClickCallback(clickCallback clickCallback){
        this.clickCallback = clickCallback;
    }

    @Override
    public void onClick(View view) {
        int pos = (Integer) view.getTag();
        clickCallback.onClick(list.get(pos));
    }

    private ArrayList<Integer> generateHeaderMap(List<Event> events){
        ArrayList<Integer> headerMap = new ArrayList<>();
        long prev;
        for(int i = 0; i < events.size(); i++){
            Event event = events.get(i);
            if(i == 0){
                headerMap.add(i);
            }else {
                Calendar nowTime = Calendar.getInstance();
                nowTime.setTimeInMillis(event.getStartTime());
                prev = events.get((i - 1)).getStartTime();
                Calendar prevTime = Calendar.getInstance();
                prevTime.setTimeInMillis(prev);
                if (nowTime.get(Calendar.DAY_OF_YEAR) > prevTime.get(Calendar.DAY_OF_YEAR)) {
                    headerMap.add(i);
                }
            }
        }
        return headerMap;
    }

    class EventsViewholder extends BaseViewHolder<Event>{
        @Bind(R.id.event_title) TextView title;
        @Bind(R.id.event_date) TextView date;
        @Bind(R.id.event_duration) TextView duration;
        public EventsViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        public void bind(Event event){
            title.setText(event.getName());
            date.setText(DateUtils.formatDate(event.getStartTime()));
            duration.setText(DateUtils.formatDate(event.getStartTime()) + " - " + DateUtils.formatDate(event.getEndTime()));
        }
    }

    class EventsSectionHeaderHolder extends BaseViewHolder<Event>{
        @Bind(R.id.event_title) TextView title;
        @Bind(R.id.event_start_time) TextView tvStartTime;
        @Bind(R.id.event_duration) TextView duration;
        @Bind(R.id.event_day) TextView tvEventDay;
        public EventsSectionHeaderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(Event event) {
            title.setText(event.getName());
            tvStartTime.setText(DateUtils.formatDate(event.getStartTime()));
            duration.setText(DateUtils.formatDate(event.getStartTime()) + " - " + DateUtils.formatDate(event.getEndTime()));
            tvEventDay.setText(DateUtils.getFormattedDate(event.getStartTime(), "EEEE"));
            if(event.getColor() == -1) {
                int rcolor = colors[random.nextInt(4)];
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), rcolor));
                event.setColor(rcolor);
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), event.getColor()));
            }
        }
    }

    public interface clickCallback {
        void onClick(Event event);
    }
}
