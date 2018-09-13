package com.miracas.espresso.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    // Used to convert String to date
    public static Date convertStringToDate(String strDate, String parseFormat) {
        try {
            return new SimpleDateFormat(parseFormat).parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Used to convert Date object to string
    public static String convertDateToString(Date objDate, String parseFormat) {
        try {
            return new SimpleDateFormat(parseFormat).format(objDate);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Used to convert Date string to string
    public static String convertDateStringToString(String strDate,
                                                   String currentFormat, String parseFormat) {
        try {
            return convertDateToString(
                    convertStringToDate(strDate, currentFormat), parseFormat);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Used to convert milliseconds to date
    public static String millisToDate(long millis, String format) {

        return new SimpleDateFormat(format).format(new Date(millis));
    }

    public static String DateStringToSeconds(String dateString, String format) {
        String timeInMilliseconds = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date mDate = sdf.parse(dateString);
            timeInMilliseconds = (mDate.getTime() / 1000) + "";

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timeInMilliseconds;
    }

    public static long converDatetomillis(String dateString, String dateFormate) {
        long timeInMilliseconds = 0;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormate);
        // sdf.setTimeZone(TimeZone.getTimeZone("MST"));
        try {
            Date mDate = sdf.parse(dateString);
            timeInMilliseconds = mDate.getTime();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    public static String getDateDiffString(Date dateOne) {
        long timeOne = dateOne.getTime();
        long timeTwo = new Date().getTime();
        long oneDay = 1000 * 60 * 60 * 24;
        long delta = (timeOne - timeTwo) / oneDay;

//        int year = (int) (delta / 365);
        int rest = (int) (delta % 365);
//        int month = rest / 30;
//        rest = rest % 30;
        int weeks = rest / 7;
//        int days = rest % 7;
//        if (delta > 0) {
//            return "dateTwo is " + delta + " days after dateOne";
//        } else {
//            delta *= -1;
//            return "dateTwo is " + delta + " days before dateOne";
//        }
        return weeks + " Weeks";
    }

}