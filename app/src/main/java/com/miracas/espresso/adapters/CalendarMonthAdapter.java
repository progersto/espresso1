package com.miracas.espresso.adapters;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.miracas.R;
import com.miracas.espresso.fragments.home.DailyCollectionFragment;
import com.miracas.espresso.layouts.WrapContentLinearLayoutManager;
import com.miracas.espresso.utils.CalendarHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class CalendarMonthAdapter extends PagerAdapter {

    private static final long ONE_DAY = 24*60*60*1000;

    private DailyCollectionFragment fragment;
    public List<List<List<Date>>> months;
    private Date actualStartDate;
    private HashMap<String, String> activeDates;

    public CalendarMonthAdapter(DailyCollectionFragment fragment, HashMap<String, String> activeDates) {
        super();
        this.fragment = fragment;
        this.activeDates = activeDates;

        Date startDate = CalendarHelper.getPrevMonthStart(new Date());
        actualStartDate = CalendarHelper.getPrevMonthActualStart(new Date());
        Date endDate = CalendarHelper.getEnd(new Date(), Calendar.DAY_OF_MONTH);
        months = new ArrayList<>();

        Log.d("ACTUAL START DATE", String.valueOf(actualStartDate));

        List<List<Date>> weeks = new ArrayList<>();
        List<Date> week = new ArrayList<>();

        Date monthEnd = CalendarHelper.getEnd(startDate, Calendar.DAY_OF_MONTH);
        Date weekEnd = CalendarHelper.getEnd(startDate, Calendar.DAY_OF_WEEK);

        while (endDate.after(startDate) || endDate.equals(startDate)) {
            if (startDate.after(monthEnd) && weeks.size() > 0) {
                weeks.add(week);
                week = new ArrayList<>();
                months.add(weeks);
                weeks = new ArrayList<>();
                monthEnd = CalendarHelper.getEnd(startDate, Calendar.DAY_OF_MONTH);
                if (CalendarHelper.isSunday(startDate))
                    weekEnd = CalendarHelper.addDays(weekEnd, 7);

            } else if (startDate.after(weekEnd) && week.size() > 0) {
                weeks.add(week);
                week = new ArrayList<>();
                weekEnd = CalendarHelper.addDays(weekEnd, 7);

            } else if (CalendarHelper.isSunday(startDate) &&
                    week.size() == 0 && weeks.size() == 0 && months.size() == 0) {
                weekEnd = CalendarHelper.getEnd(CalendarHelper.addDays(startDate, 1), Calendar.DAY_OF_WEEK);
            }

            Date date = new Date(startDate.getTime());
            week.add(date);

            long newTime = startDate.getTime() + ONE_DAY;
            startDate.setTime(newTime);
        }

        if (!week.isEmpty())
            weeks.add(week);
        if (!weeks.isEmpty())
            months.add(weeks);
    }

    @Override
    public int getCount() {
        return months.size();
    }

    @NonNull
    @Override
    public View instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
        View view = inflater.inflate(R.layout.list_item_calendar_month, container, false);

        ViewHolder holder = new ViewHolder(view);
        List<List<Date>> month = months.get(position);
        WeekAdapter adapter = new WeekAdapter(fragment, month, actualStartDate, activeDates);
        holder.week.setAdapter(adapter);
        holder.week.setLayoutManager(new WrapContentLinearLayoutManager(fragment.getContext()));

        container.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public int getMonthPosition(Date date) {
        int position = 0;
        for (List<List<Date>> month : months) {
            for (List<Date> week : month) {
                if (!week.isEmpty()) {
                    try {
                        Date date1 = week.get(0);
                        if (CalendarHelper.onSameMonth(date1, date)) {
                            position = months.indexOf(month);
                            break;
                        }
                    } catch (IndexOutOfBoundsException ignored) {}
                }
            }
        }
        return position;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    class ViewHolder {
        RecyclerView week;
        ViewHolder(View itemView) {
            week = itemView.findViewById(R.id.month);
        }
    }

}
