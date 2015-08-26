package ru.furry.furview2.system;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockUnblockUI {

    private AtomicInteger counterTheads = new AtomicInteger(0);
    private ArrayList<View> views = new ArrayList<View>();
    private ProgressBar progressBar;


    public BlockUnblockUI(RelativeLayout incomingRelativeLayout, ProgressBar progressBar) {
        init(incomingRelativeLayout, progressBar);
    }

    public BlockUnblockUI(RelativeLayout incomingRelativeLayout) {
        init(incomingRelativeLayout);
    }

    private void init(RelativeLayout incomingRelativeLayout) {
        //auto adding all found enabled views into ArrayList "views"
        if (views.size() == 0)
            for (int i = 0; i < incomingRelativeLayout.getChildCount(); i++) {
                views.addAll(getAllChildren(incomingRelativeLayout.getChildAt(i)));
            }

        Log.d("fgsfds", "Enabled elements on screen = " + views.size() + " Ready to block.");
    }

    private void init(RelativeLayout incomingRelativeLayout, ProgressBar progressBar) {
        this.progressBar = progressBar;
        init(incomingRelativeLayout);
    }

    private ArrayList<View> getAllChildren(View v) {

        ArrayList<View> viewArrayList = new ArrayList<View>();

        if (!(v instanceof ViewGroup)) {
            if (v.isEnabled()) {
                viewArrayList.add(v);
            }
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();
        ViewGroup vg = (ViewGroup) v;

        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));
            result.addAll(viewArrayList);
        }

        return result;
    }

    public void blockUI() {

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
        }

        for (int i = 0; i < views.size(); i++) {
            views.get(i).setEnabled(false);
        }
        counterTheads.set(counterTheads.incrementAndGet());
        Log.d("fgsfds", "Lock UI! counterTheads after locking =" + counterTheads.get());
    }

    public void unblockUI() {

        if (counterTheads.get() > 1) {
            counterTheads.set(counterTheads.decrementAndGet());
        }
        if (counterTheads.get() == 1) {
            counterTheads.set(counterTheads.decrementAndGet());
            for (int i = 0; i < views.size(); i++) {
                views.get(i).setEnabled(true);
            }
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            Log.d("fgsfds", "Unblock UI! counterTheads after unlocking =" + counterTheads.get());
        }
    }

    public void blockUIall() {
        for (int i = 0; i < views.size(); i++) {
            views.get(i).setEnabled(false);
        }
    }

    public void unblockUIall() {
            for (int i = 0; i < views.size(); i++) {
                views.get(i).setEnabled(true);
            }
    }

    public void addViewToBlock(View v) {
        //manual adding view into ArrayList "views"
        views.add(v);
    }

    public void delViewFromBlock(View v) {
        //manual remove view from ArrayList "views"
        views.remove(v);
    }
}