package ru.furry.furview2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;


public class FullscreenActivity extends Activity {

    String url;
    WebView mwebViewFullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        url = getIntent().getExtras().getString("target_url");

        mwebViewFullscreen = (WebView)findViewById(R.id.webViewFullscreen);

        mwebViewFullscreen.loadUrl(url);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fullscreen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_text) + " action_settings", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_save:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_text) + " action_save", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_tags:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_text) + " action_tags", Toast.LENGTH_SHORT).show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }


    }
}
