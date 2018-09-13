package com.miracas.espresso.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miracas.R;
import com.miracas.espresso.fragments.home.DailyCollectionFragment;
import com.miracas.espresso.utils.CalendarHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.ViewHolder> {

    private DailyCollectionFragment fragment;
    private List<List<Date>> weeks;
    private Date actualStartDate;
    private HashMap<String, String> activeDates;

    WeekAdapter(DailyCollectionFragment fragment, List<List<Date>> weeks, Date actualStartDate,
                HashMap<String, String> activeDates) {
        super();
        this.fragment = fragment;
        this.weeks = weeks;
        this.actualStartDate = actualStartDate;
        this.activeDates = activeDates;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
        View view = inflater.inflate(R.layout.list_item_calendar_week, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder != null && getItemCount() > 0 && position != -1) {
            List<Date> week = weeks.get(position);
            LayoutInflater inflater = LayoutInflater.from(fragment.getContext());

            Date startDate = week.get(0);
            Date monday = CalendarHelper.getStart(startDate, Calendar.DAY_OF_WEEK);
            long diff = CalendarHelper.getDateDiff(monday, startDate, TimeUnit.DAYS);

            if (diff < 0)
                diff += 7;

            for (int i=0; i<diff; ++i) {
                @SuppressLint("InflateParams")
                View view = inflater.inflate(R.layout.list_item_date, holder.parent, false);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                );
                view.setLayoutParams(param);
                TextView dateView = view.findViewById(R.id.date);
                dateView.setText(" ");
                holder.parent.addView(view);
            }

            for (Date date : week) {
                @SuppressLint("InflateParams")
                View view = inflater.inflate(R.layout.list_item_date, holder.parent, false);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                );
                view.setLayoutParams(param);
                TextView dateView = view.findViewById(R.id.date);
                DateFormat dateFormat = new SimpleDateFormat("d", Locale.US);
                dateView.setText(dateFormat.format(date));

                DateFormat activeFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String activeDate = activeFormat.format(date);
                if (activeDates.containsKey(activeDate)) {
                    View dot = view.findViewById(R.id.dot);
                    dot.setVisibility(View.VISIBLE);
                }

                if (CalendarHelper.onSameDay(fragment.selectedDate, date)) {
                    if (CalendarHelper.onSameDay(date, new Date())) {
                        dateView.setTextColor(Color.parseColor("#cc0000"));
                        view.findViewById(R.id.underline).setVisibility(View.GONE);
                    } else {
                        view.findViewById(R.id.underline).setVisibility(View.VISIBLE);
                        dateView.setTextColor(Color.parseColor("#333333"));
                    }

                } else if (date.after(new Date()) || date.before(actualStartDate)) {
                    dateView.setTextColor(Color.parseColor("#eeeeee"));

                } else {
                    if (CalendarHelper.isSunday(date))
                        dateView.setTextColor(Color.parseColor("#eeeeee"));
                    view.setOnClickListener(view1 -> fragment.selectDateFromExpandedCalendar(date));
                }

                holder.parent.addView(view);
            }

            Date endDate = week.get(week.size()-1);
            Date sunday = CalendarHelper.getEnd(endDate, Calendar.DAY_OF_WEEK);
            diff = CalendarHelper.getDateDiff(endDate, sunday, TimeUnit.DAYS);

            for (int i=0; i<diff; ++i) {
                @SuppressLint("InflateParams")
                View view = inflater.inflate(R.layout.list_item_date, holder.parent, false);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                );
                view.setLayoutParams(param);
                TextView dateView = view.findViewById(R.id.date);
                dateView.setText(" ");
                holder.parent.addView(view);
            }

        }
    }

    @Override
    public int getItemCount() {
        return weeks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewGroup parent;

        public ViewHolder(View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
        }
    }

}
