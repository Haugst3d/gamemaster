package com.fortysevendeg.android.swipelistview;

import android.view.View;

/**
 * Class that saves pending dismiss data
 */
public class PendingDismissData implements Comparable<PendingDismissData> {
    public int position;
    public View view;

    public PendingDismissData(int position, View view) {
        this.position = position;
        this.view = view;
    }

    @Override
    public int compareTo(PendingDismissData other) {
        // Sort by descending position
        return other.position - position;
    }
}