package comingvarjackalfurview2.github.furview2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


public class MainActivity extends Activity implements View.OnClickListener{

    EditText mSearchField;
    ImageButton mSearchButton;
    WebView mWebView1, mWebView2, mWebView3, mWebView4;
    String url, url2;
    View.OnTouchListener mOnTouchListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchField = (EditText)findViewById(R.id.SearchField);
        mSearchButton = (ImageButton)findViewById(R.id.SearchButton);

        mSearchField.setOnClickListener(this);
        mSearchButton.setOnClickListener(this);

        url = "http://freeicons.net.ru/wp-content/uploads/2013/08/catpurr_white.gif";
        url2 = "http://freeicons.net.ru/wp-content/uploads/2013/08/walkingcat_white.gif";

        mWebView1 = (WebView) findViewById(R.id.webView1);
        mWebView2 = (WebView) findViewById(R.id.webView2);
        mWebView3 = (WebView) findViewById(R.id.webView3);
        mWebView4 = (WebView) findViewById(R.id.webView4);

        /**
        mWebView1.setOnClickListener(this);
        mWebView2.setOnClickListener(this);
        mWebView3.setOnClickListener(this);
        mWebView4.setOnClickListener(this);
        **/

        mWebView1.loadUrl(url);
        mWebView2.loadUrl(url2);

        mOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String str = getResources().getString(R.string.toast_text)+" webView1"+getResources().getString(R.string.toast_to_fullscreen);
                Toast.makeText(getApplicationContext(),str,Toast.LENGTH_LONG).show();
                Intent intent = new Intent("comingvarjackalfurview2.github.furview2.fullscreen");
                intent.putExtra("target_url", url);
                startActivity(intent);
                return false;
            }
        };

        mWebView1.setOnTouchListener(mOnTouchListener);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_text) + " action_settings", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.SearchButton:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_text) + " SearchButton", Toast.LENGTH_SHORT).show();
                break;
            case R.id.SearchField:
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_text)+" SearchField",Toast.LENGTH_SHORT).show();
                break;
            /**
            case R.id.webView1:
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_text)+" webView1",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("comingvarjackalfurview2.github.furview2.fullscreen");
                startActivity(intent);
                break;
            case R.id.webView2:
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_text)+" webView2",Toast.LENGTH_SHORT).show();
                break;
            case R.id.webView3:
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_text)+" webView3",Toast.LENGTH_SHORT).show();
                break;
            case R.id.webView4:
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_text)+" webView4",Toast.LENGTH_SHORT).show();
                break;
             **/
        }

    }

/**
    public void webView1Click(View view) {
        Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_text)+" webView1",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent("comingvarjackalfurview2.github.furview2.fullscreen");
        startActivity(intent);
    }
 **/
}
