package com.miracas.espresso.adapters;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.miracas.espresso.client.responses.Quote;
import com.miracas.espresso.fragments.home.CalendarKeywordFragment;
import com.miracas.espresso.utils.CalendarHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarPaneAdapter extends FragmentPagerAdapter {

    private static final long ONE_DAY = 24*60*60*1000;

    public List<Date> dates;
    private List<Quote> quotes;

    public CalendarPaneAdapter(FragmentManager fm, List<Quote> quotes) {
        super(fm);
        Date startDate = CalendarHelper.getPrevMonthActualStart(new Date());
        Date endDate = new Date();
        this.quotes = quotes;

        dates = new ArrayList<>();
        while (endDate.after(startDate)) {
            Date date = new Date(startDate.getTime());
            dates.add(date);
            long newTime = startDate.getTime() + ONE_DAY;
            startDate.setTime(newTime);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return CalendarKeywordFragment.newInstance(dates.get(position), quotes);
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

}
