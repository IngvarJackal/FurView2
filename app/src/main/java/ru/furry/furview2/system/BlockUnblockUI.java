package ru.furry.furview2.system;

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.concurrent.atomic.AtomicInteger;

public class BlockUnblockUI {

    private RelativeLayout relativeLayout;
    private AtomicInteger counterTheads = new AtomicInteger(0);

    public BlockUnblockUI(RelativeLayout incomingRelativeLayout) {
        this.relativeLayout = incomingRelativeLayout;
        Log.d("fgsfds", "Init counterTheads =" + counterTheads.get());
    }

    public void blockUI() {
        for (int i = 0; i < relativeLayout.getChildCount(); i++) {
            relativeLayout.getChildAt(i).setEnabled(false);
        }
        counterTheads.set(counterTheads.incrementAndGet());
        Log.d("fgsfds", "Lock UI! counterTheads ="+counterTheads.get());
    }

    public void unblockUI() {
        counterTheads.set(counterTheads.decrementAndGet());
        if (counterTheads.get() < 1) {
            for (int i = 0; i < relativeLayout.getChildCount(); i++) {
                relativeLayout.getChildAt(i).setEnabled(true);
                Log.d("fgsfds", "id =" + relativeLayout.getChildAt(i).getId());
            }
            Log.d("fgsfds", "Unblock UI! counterTheads ="+counterTheads.get());
        }
    }
}
