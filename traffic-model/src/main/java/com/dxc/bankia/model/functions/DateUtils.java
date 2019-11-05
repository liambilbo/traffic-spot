package com.dxc.bankia.model.functions;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;

public class DateUtils {

    public static int getAge(Date startDate) {
        return getYears(startDate,new Date());
    }

    public static int getYears(Date startDate,Date endDate) {
        return Period.between(toLocalDate(startDate),toLocalDate(endDate))
                .getYears();
    }

    public static LocalDate toLocalDate(Date date){

        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

    }

    public static Date toDate(LocalDate date){

        return Date.from(date.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());

    }

    public static Date addYears(Date startDate,int years) {
        return toDate(toLocalDate(startDate).plusYears(years));
    }
    public static Date today(){return toDate(LocalDate.now());}
    public static String toString(Date date) {
        return toLocalDate(date).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
    }

}
