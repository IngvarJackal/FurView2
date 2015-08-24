package ru.furry.furview2.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ru.furry.furview2.R;

public class HelpScreen extends AppCompatActivity {

    Button closeButton;
    TextView helpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_screen);
        closeButton = (Button) findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        helpTextView = (TextView) findViewById(R.id.helpTextView);
        helpTextView.setText(Html.fromHtml(getApplicationContext().getResources().getString(getIntent().getIntExtra("helptextId", 0))));
        helpTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
