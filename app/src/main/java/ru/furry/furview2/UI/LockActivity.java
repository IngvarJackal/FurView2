package ru.furry.furview2.UI;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import ru.furry.furview2.R;

public class LockActivity extends Activity {
    TextView mLocktext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        mLocktext = (TextView)findViewById(R.id.lockText);
        mLocktext.setText(getString(R.string.searching_proxy));
    }
}
