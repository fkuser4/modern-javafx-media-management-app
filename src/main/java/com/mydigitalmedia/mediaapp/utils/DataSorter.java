package com.mydigitalmedia.mediaapp.utils;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class DataSorter implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-yyyy");
        YearMonth date1 = YearMonth.parse(o1, formatter);
        YearMonth date2 = YearMonth.parse(o2, formatter);
        return date1.compareTo(date2);
    }
}
