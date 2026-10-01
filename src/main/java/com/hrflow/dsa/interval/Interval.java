package com.hrflow.dsa.interval;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Generic Time Interval [start, end] inclusive.
 */
public class Interval<T> implements Comparable<Interval<T>> {
    private final LocalDate start;
    private final LocalDate end;
    private final T data;

    public Interval(LocalDate start, LocalDate end, T data) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        this.start = Objects.requireNonNull(start);
        this.end = Objects.requireNonNull(end);
        this.data = data;
    }

    public boolean overlaps(Interval<T> other) {
        return !this.start.isAfter(other.end) && !other.start.isAfter(this.end);
    }

    public LocalDate getStart() { return start; }
    public LocalDate getEnd() { return end; }
    public T getData() { return data; }

    @Override
    public int compareTo(Interval<T> o) {
        int cmp = this.start.compareTo(o.start);
        if (cmp != 0) return cmp;
        return this.end.compareTo(o.end);
    }
}
