package com.miracas.espresso.adapters;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miracas.R;
import com.miracas.espresso.fragments.home.DailyCollectionFragment;
import com.miracas.espresso.utils.CalendarHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private static final long ONE_DAY = 24*60*60*1000;

    private DailyCollectionFragment fragment;
    public List<List<Date>> weeks;
    private Date selected;
    private HashMap<String, String> activeDates;

    public CalendarAdapter(DailyCollectionFragment fragment, HashMap<String, String> activeDates) {
        super();
        this.fragment = fragment;
        this.activeDates = activeDates;

        Date startDate = CalendarHelper.getPrevMonthActualStart(new Date());
        Date endDate = new Date();
        selected = startDate;

        weeks = new ArrayList<>();
        List<Date> week = new ArrayList<>();
        Date weekEnd = CalendarHelper.getEnd(startDate, Calendar.DAY_OF_WEEK);

        if (CalendarHelper.onSameDay(weekEnd, startDate) || weekEnd.before(startDate))
            weekEnd = CalendarHelper.addDays(weekEnd, 7);

        week.add(new Date(startDate.getTime()));

        while (endDate.after(startDate)) {
            long newTime = startDate.getTime() + ONE_DAY;
            startDate.setTime(newTime);

            if (startDate.after(weekEnd) && !CalendarHelper.onSameDay(weekEnd, startDate) && !week.isEmpty()) {
                weeks.add(week);
                week = new ArrayList<>();
                weekEnd = CalendarHelper.addDays(weekEnd, 7);
            }

            if (endDate.after(startDate)) {
                Date date = new Date(startDate.getTime());
                week.add(date);
                selected = date;
            }
        }

        if (!week.isEmpty())
            weeks.add(week);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
        View view = inflater.inflate(R.layout.list_item_calendar_week, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder != null) {
            holder.setIsRecyclable(false);

            final List<Date> week = weeks.get(position);
            LayoutInflater inflater = LayoutInflater.from(fragment.getContext());

            Date startDate = week.get(0);
            Date monday = CalendarHelper.getStart(startDate, Calendar.DAY_OF_WEEK);
            long diff = CalendarHelper.getDateDiff(monday, startDate, TimeUnit.DAYS);

            if (diff < 0)
                diff += 7;

            for (int i=0; i<diff; ++i) {
                View view = inflater.inflate(R.layout.list_item_calendar, holder.parent, false);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                );
                view.setLayoutParams(param);
                TextView dateView = view.findViewById(R.id.date);
                dateView.setText(" ");
                holder.parent.addView(view, param);
            }

            for (final Date date : week) {
                View view = inflater.inflate(R.layout.list_item_calendar, holder.parent, false);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                );
                view.setLayoutParams(param);
                TextView dateView = view.findViewById(R.id.date);
                TextView dayView = view.findViewById(R.id.day);
                DateFormat dateFormat = new SimpleDateFormat("d", Locale.US);
                DateFormat dayFormat = new SimpleDateFormat("E", Locale.US);
                dateView.setText(dateFormat.format(date));
                dayView.setText(dayFormat.format(date).toUpperCase());

                DateFormat activeFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String activeDate = activeFormat.format(date);
                if (activeDates.containsKey(activeDate)) {
                    View dot = view.findViewById(R.id.dot);
                    dot.setVisibility(View.VISIBLE);
                }

                if (CalendarHelper.onSameDay(selected, date)) {
                    if (CalendarHelper.onSameDay(date, new Date())) {
                        dateView.setTextColor(Color.parseColor("#cc0000"));
                        view.findViewById(R.id.underline).setVisibility(View.GONE);
                    } else {
                        view.findViewById(R.id.underline).setVisibility(View.VISIBLE);
                        dateView.setTextColor(Color.parseColor("#333333"));
                    }

                } else if (CalendarHelper.isSunday(date))
                    dateView.setTextColor(Color.parseColor("#eeeeee"));

                view.setOnClickListener(view1 -> selectDate(date));

                holder.parent.addView(view, param);
            }

            Date endDate = week.get(week.size()-1);
            Date sunday = CalendarHelper.getEnd(endDate, Calendar.DAY_OF_WEEK);
            diff = CalendarHelper.getDateDiff(endDate, sunday, TimeUnit.DAYS);

            for (int i=0; i<diff; ++i) {
                View view = inflater.inflate(R.layout.list_item_calendar, holder.parent, false);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                );
                view.setLayoutParams(param);
                TextView dateView = view.findViewById(R.id.date);
                dateView.setText(" ");
                holder.parent.addView(view, param);
            }
        }
    }

    private void select(Date date) {
        selected = date;
    }

    public void selectDate(Date date) {
        int position = 0;
        for (List<Date> week : weeks) {
            if ((!week.isEmpty() && date.after(week.get(0)) && CalendarHelper.addDays(week.get(0), 7).after(date)) ||
                    CalendarHelper.onSameDay(week.get(0), date)) {
                position = weeks.indexOf(week);
            }
        }
        select(date);
        notifyDataSetChanged();
        fragment.selectDate(date);
        fragment.scrollTo(position);
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
