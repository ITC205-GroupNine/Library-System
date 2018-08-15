import java.util.Date;
import java.util.concurrent.TimeUnit;


class Calendar {

    private static Calendar selfCalendar;
    private static java.util.Calendar calendar;


    private Calendar() {
        calendar = java.util.Calendar.getInstance();
    }


    static Calendar getInstance() {
        if (selfCalendar == null) {
            selfCalendar = new Calendar();
        }
        return selfCalendar;
    }


    void incrementDate(int days) {
        calendar.add(java.util.Calendar.DATE, days);
    }


    synchronized void setDate(Date date) {
        try {
            calendar.setTime(date);
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
            calendar.set(java.util.Calendar.MINUTE, 0);
            calendar.set(java.util.Calendar.SECOND, 0);
            calendar.set(java.util.Calendar.MILLISECOND, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    synchronized Date date() {
        try {
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
            calendar.set(java.util.Calendar.MINUTE, 0);
            calendar.set(java.util.Calendar.SECOND, 0);
            calendar.set(java.util.Calendar.MILLISECOND, 0);
            return calendar.getTime();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    synchronized Date getDueDate(int loanPeriod) {
        Date now = date();
        calendar.add(java.util.Calendar.DATE, loanPeriod);
        Date dueDate = calendar.getTime();
        calendar.setTime(now);
        return dueDate;
    }


    synchronized long getDaysDifference(Date targetDate) {
        long diffMillis = date().getTime() - targetDate.getTime();
        return TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);
    }
}
