package com.freddieptf.shush.calendar.ui;

import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freddieptf.shush.R;
import com.freddieptf.shush.calendar.Utils.DateUtils;
import com.freddieptf.shush.calendar.data.model.Event;
import com.freddieptf.shush.calendar.ui.base.BaseViewHolder;

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
    private List<Event> list;
    private ArrayMap<Integer, Integer> headerMap;
    private clickCallback clickCallback;
    private Random random;

    private int[] colors = {
            R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent, R.color.colorAccentLig
    };

    public static int VIEW_TYPE_HEADER = 3;
    public static int VIEW_TYPE_SECTION_HEADER = 2;
    public static int VIEW_TYPE_SECTION_ITEM = 1;

    public EventsAdapter(){
        random = new Random();
    }

    public void swapData(List<Event> list){
        this.list = list;
        generateHeaderMap(list);
        notifyDataSetChanged();
    }

    //// FIXME: 3/26/17 WYD BRO
    public void generateHeaderMap(List<Event> events) {
        headerMap = new ArrayMap<>();
        headerMap.put(0, VIEW_TYPE_SECTION_HEADER); // assumptions boi, FIXME: 3/26/17
        for(int i = 0; i < events.size()-1; i++){
            Event event = events.get(i);
            Event futureEvent = events.get((i+1));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(event.getStartTime());
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            calendar.setTimeInMillis(futureEvent.getStartTime());
            int tomorrow = calendar.get(Calendar.DAY_OF_WEEK);
            if(tomorrow > dayOfWeek){
                headerMap.put((i+1), VIEW_TYPE_SECTION_HEADER);
            }
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_HEADER)
            return new EventsViewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_header, parent, false));
        else if (viewType == VIEW_TYPE_SECTION_HEADER)
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
        return headerMap.containsKey(position) ? headerMap.get(position) : VIEW_TYPE_SECTION_ITEM;
    }

    public void setClickCallback(clickCallback clickCallback){
        this.clickCallback = clickCallback;
    }

    @Override
    public void onClick(View view) {
        int pos = (Integer) view.getTag();
        clickCallback.onClick(list.get(pos));
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
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), colors[random.nextInt(3)]));
        }

        @Override
        public void bind(Event event) {
            title.setText(event.getName());
            tvStartTime.setText(DateUtils.formatDate(event.getStartTime()));
            duration.setText(DateUtils.formatDate(event.getStartTime()) + " - " + DateUtils.formatDate(event.getEndTime()));
            tvEventDay.setText(DateUtils.getFormattedDate(event.getStartTime(), "EEEE"));
        }
    }

    public interface clickCallback {
        void onClick(Event event);
    }
}
