package ru.furry.furview2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

import ru.furry.furview2.database.FurryDatabase;
import ru.furry.furview2.database.FurryDatabaseUtils;


public class SetAliasesFullscreenActivity extends AppCompatActivity {

    Button mCancelButton;
    Button mSkipButton;
    Button mAddLinkButton;
    EditText mTagSetAliasesFullscreen;
    EditText mAliasSetAliasesFullscreen;
    LinearLayout mAnotherAliasesLayout;

    FurryDatabase furryDatabase;
    FurryDatabaseUtils furryDatabaseUtils;

    ArrayList<String> tags;
    int currentAlyas = 1;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("tagsForSave",tags);
        outState.putInt("currentAlyas", currentAlyas);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        tags = savedInstanceState.getStringArrayList("tagsForSave");
        currentAlyas = savedInstanceState.getInt("currentAlyas",1);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_aliases_fullscreen);

        tags = getIntent().getStringArrayListExtra("selectedTags");

        setTitle(getResources().getString(R.string.title_activity_aliases) + " ("+ currentAlyas +"/" + tags.size() + ")");

        mCancelButton = (Button) findViewById(R.id.cancelButton);
        mSkipButton = (Button) findViewById(R.id.skipButton);
        mAddLinkButton = (Button) findViewById(R.id.addLinkButton);
        mTagSetAliasesFullscreen = (EditText) findViewById(R.id.tagSetAliasesFullscreen);
        mAliasSetAliasesFullscreen = (EditText) findViewById(R.id.aliasSetAliasesFullscreen);
        mAnotherAliasesLayout = (LinearLayout) findViewById(R.id.anotherAliasesLayout);

        furryDatabase = new FurryDatabase(getApplicationContext());
        furryDatabaseUtils = new FurryDatabaseUtils(furryDatabase);

        mTagSetAliasesFullscreen.setText(tags.get(currentAlyas).toString());

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
