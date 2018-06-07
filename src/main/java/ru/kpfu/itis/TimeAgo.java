package ru.kpfu.itis;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeAgo {
    public static final List<Long> times = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1));
    private static final List<String> timesString = Arrays.asList("year", "month", "day", "hour", "min", "second");

    public static String toDuration(long duration) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < times.size(); i++) {
            Long current = times.get(i);
            long temp = duration / current;
            if (temp > 0) {
                res.append(temp)
                        .append(" ")
                        .append(timesString.get(i))
                        .append(temp > 1 ? "s" : "")
                        .append(" ago");
                break;
            }
        }
        if ("".equals(res.toString())) {
            return "Just now";
        } else {
            return res.toString();
        }
    }

    public static String leftDuration(long duration) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < times.size(); i++) {
            Long current = times.get(i);
            long temp = duration / current;
            if (temp > 0) {
                res.append(temp)
                        .append(" ")
                        .append(timesString.get(i))
                        .append(temp > 1 ? "s" : "");
                break;
            }
        }
        return res.toString();
    }

    public static String toDurationFromNow(long eventTime) {
        return toDuration(new Date().getTime() - eventTime);
    }
}
