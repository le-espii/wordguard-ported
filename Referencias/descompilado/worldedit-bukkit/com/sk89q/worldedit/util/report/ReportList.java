/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.sk89q.worldedit.util.report;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.util.report.Report;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ReportList
implements Report,
List<Report> {
    private final String title;
    private final List<Report> reports = Lists.newArrayList();

    public ReportList(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public int size() {
        return this.reports.size();
    }

    @Override
    public boolean isEmpty() {
        return this.reports.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.reports.contains(o);
    }

    @Override
    public Iterator<Report> iterator() {
        return this.reports.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.reports.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.reports.toArray(a);
    }

    @Override
    public boolean add(Report report) {
        return this.reports.add(report);
    }

    @Override
    public boolean remove(Object o) {
        return this.reports.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.reports.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Report> c) {
        return this.reports.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Report> c) {
        return this.reports.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.reports.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.reports.retainAll(c);
    }

    @Override
    public void clear() {
        this.reports.clear();
    }

    @Override
    public boolean equals(Object o) {
        return this.reports.equals(o);
    }

    @Override
    public int hashCode() {
        return this.reports.hashCode();
    }

    @Override
    public Report get(int index) {
        return this.reports.get(index);
    }

    @Override
    public Report set(int index, Report element) {
        return this.reports.set(index, element);
    }

    @Override
    public void add(int index, Report element) {
        this.reports.add(index, element);
    }

    @Override
    public Report remove(int index) {
        return this.reports.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.reports.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.reports.lastIndexOf(o);
    }

    @Override
    public ListIterator<Report> listIterator() {
        return this.reports.listIterator();
    }

    @Override
    public ListIterator<Report> listIterator(int index) {
        return this.reports.listIterator(index);
    }

    @Override
    public List<Report> subList(int fromIndex, int toIndex) {
        return this.reports.subList(fromIndex, toIndex);
    }

    public String toString() {
        if (!this.reports.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (Report report : this.reports) {
                builder.append("================================\n").append(report.getTitle()).append("\n================================").append("\n\n").append(report.toString()).append("\n\n");
            }
            return builder.toString();
        }
        return "No reports.";
    }
}

