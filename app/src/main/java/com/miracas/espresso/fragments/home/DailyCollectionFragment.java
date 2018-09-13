package com.miracas.espresso.fragments.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miracas.R;
import com.miracas.espresso.adapters.CalendarAdapter;
import com.miracas.espresso.adapters.CalendarMonthAdapter;
import com.miracas.espresso.adapters.CalendarPaneAdapter;
import com.miracas.espresso.client.ActiveDatesClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.Quote;
import com.miracas.espresso.fragments.BaseFragment;
import com.miracas.espresso.utils.CalendarHelper;
import com.miracas.espresso.viewpager.CalendarViewPager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class DailyCollectionFragment extends BaseFragment {

    private TextView currentDate;
    private View calendarMonthParent;
    private RecyclerView calendar;
    private ImageView arrow;
    private CalendarMonthAdapter calendarMonthAdapter;
    private CalendarAdapter carouselAdapter;
    private SlidingUpPanelLayout slidingPanel;
    private ViewPager panePager;
    private CalendarPaneAdapter paneAdapter;
    private HashMap<String, String> activeDates;

    public Date selectedDate = new Date();
    private boolean expanded = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        View view = inflater.inflate(R.layout.fragment_daily_catalog, container, false);
        onCreateToolbar(view, "Daily Collection", false);

        List<Quote> quotes = new ArrayList<>();
        activeDates = new HashMap<>();

        if (getArguments() != null) {
            Type type = new TypeToken<List<Quote>>() {}.getType();
            quotes.addAll(new Gson().fromJson(getArguments().getString("QUOTES"), type));
        }

        AppBarLayout appBarLayout = view.findViewById(R.id.appBarLayout);
        CalendarViewPager calendarMonth = view.findViewById(R.id.calendarMonth);

        slidingPanel = view.findViewById(R.id.slidingPanel);
        appBarLayout.setExpanded(false, false);
        currentDate = view.findViewById(R.id.currentDate);
        arrow = view.findViewById(R.id.arrow);

        calendar = view.findViewById(R.id.calendar);
        calendarMonthParent = view.findViewById(R.id.calendarMonthParent);

        slidingPanel.setFadeOnClickListener(view13 ->
                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED));

        slidingPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                calendar.setAlpha(1.0F-slideOffset);
                calendarMonthParent.setAlpha(slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState.equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
                    expanded = true;
                    showMonth(selectedDate);
                    arrow.setImageResource(R.drawable.angle_arrow_up);
                    calendarMonthAdapter.notifyDataSetChanged();
                    calendarMonth.setCurrentItem(calendarMonthAdapter.getMonthPosition(selectedDate));

                } else if (newState.equals(SlidingUpPanelLayout.PanelState.COLLAPSED)) {
                    showDate(selectedDate);
                    arrow.setImageResource(R.drawable.angle_arrow_down);
                    for (List<Date> week : carouselAdapter.weeks) {
                        try {
                            if (CalendarHelper.onSameWeek(week.get(0), selectedDate)
                                    && selectedDate.after(week.get(0))) {
                                calendar.scrollToPosition(carouselAdapter.weeks.indexOf(week));
                                break;
                            }
                        } catch (IndexOutOfBoundsException ignored) {}
                    }
                }

                if (newState.equals(SlidingUpPanelLayout.PanelState.EXPANDED) &&
                        previousState.equals(SlidingUpPanelLayout.PanelState.DRAGGING)) {
                    calendar.setVisibility(View.GONE);
                    expanded = true;

                } else if (newState.equals(SlidingUpPanelLayout.PanelState.DRAGGING) &&
                        previousState.equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
                    calendar.setVisibility(View.VISIBLE);

                }
            }
        });

        paneAdapter = new CalendarPaneAdapter(getChildFragmentManager(), quotes);
        panePager = view.findViewById(R.id.panePager);
        panePager.setAdapter(paneAdapter);
        panePager.setCurrentItem(paneAdapter.getCount()-1);
        panePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Date date = paneAdapter.dates.get(position);
                selectedDate = date;
                carouselAdapter.selectDate(date);
                selectDate();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        View back = view.findViewById(R.id.back);
        back.setOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });

        carouselAdapter = new CalendarAdapter(this, activeDates);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        calendar.setLayoutManager(manager);
        calendar.setAdapter(carouselAdapter);
        calendar.scrollToPosition(carouselAdapter.getItemCount()-1);

        calendar.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int position = manager.findFirstVisibleItemPosition();
                    Date date = carouselAdapter.weeks.get(position).get(0);
                    showTempDate(date);
                }
            }
        });

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(calendar);

        calendarMonthAdapter = new CalendarMonthAdapter(this, activeDates);
        calendarMonth.setAdapter(calendarMonthAdapter);
        calendarMonth.setCurrentItem(calendarMonthAdapter.getCount()-1);
        calendarMonth.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                try {
                    List<List<Date>> weeks = calendarMonthAdapter.months.get(position);
                        for (List<Date> week : weeks) {
                            if (!week.isEmpty()) {
                                Date date = week.get(0);
                                showMonth(date);
                                return;
                            }
                        }

                } catch (IndexOutOfBoundsException ignored) {}
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        arrow.setOnClickListener(view12 -> toggle());

        ActiveDatesClient client = new ActiveDatesClient(this);
        client.execute();

        return view;
    }

    private void toggle() {
        if (slidingPanel.getPanelState().equals(SlidingUpPanelLayout.PanelState.COLLAPSED)) {
            slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

        } else {
            slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showProgress(false);
        selectDate(selectedDate);
    }

    @Override
    public void onResume() {
        super.onResume();
        onCreateToolbar(getView() ,"Daily Collection", false);
    }

    public void selectDate(Date date) {
        selectedDate = date;
        int position = 0;
        for(Date d : paneAdapter.dates) {
            if (CalendarHelper.onSameDay(d, date)) {
                position = paneAdapter.dates.indexOf(d);
                break;
            }
        }
        panePager.setCurrentItem(position);
        selectDate();
    }

    private void showTempDate(Date date) {
        DateFormat dateFormat2 = new SimpleDateFormat("MMMM", Locale.US);
        DateFormat dateFormat3 = new SimpleDateFormat(" yyyy", Locale.US);
        String dateString = dateFormat2.format(date).toUpperCase() + dateFormat3.format(date);
        currentDate.setText(dateString);
    }

    public void selectDateFromExpandedCalendar(Date date) {
        selectedDate = date;
        slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        calendarMonthAdapter.notifyDataSetChanged();
        carouselAdapter.notifyDataSetChanged();
        carouselAdapter.selectDate(date);
        selectDate();
    }

    private void selectDate() {
        showDate(selectedDate);
    }

    public void showMonth(Date date) {
        showDate(date);
    }

    public void showDate(Date date) {

        if (!expanded)
            date = selectedDate;

        DateFormat dateFormat2 = new SimpleDateFormat("MMMM", Locale.US);
        DateFormat dateFormat3 = new SimpleDateFormat(" yyyy", Locale.US);

        String dateString = dateFormat2.format(date).toUpperCase() + dateFormat3.format(date);
        currentDate.setText(dateString);
    }

    public void scrollTo(int position) {
        calendar.scrollToPosition(position);
    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {
        if (clientCode == ClientCodes.ACTIVE_KEYWORDS && response != null) {
            ActiveDatesClient.Response datesResponse = (ActiveDatesClient.Response) response;
            activeDates.putAll(datesResponse.dates);
            calendarMonthAdapter.notifyDataSetChanged();
            carouselAdapter.notifyDataSetChanged();
        }
    }
}
