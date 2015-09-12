package ru.furry.furview2;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.furry.furview2.database.FurryDatabase;
import ru.furry.furview2.database.FurryDatabaseUtils;
import ru.furry.furview2.system.Utils;


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
    int currentAlias = 0;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("tagsForSave", tags);
        outState.putInt("cAlyas", currentAlias);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        tags = savedInstanceState.getStringArrayList("tagsForSave");
        currentAlias = savedInstanceState.getInt("cAlyas", 0);
        mTagSetAliasesFullscreen.setText(tags.get(currentAlias).toString());
        if (currentAlias > 0) {
            int currentAliasplus = currentAlias + 1;
            setTitle(getResources().getString(R.string.title_activity_aliases) + " (" + currentAliasplus + "/" + tags.size() + ")");
        } else {
            setTitle(getResources().getString(R.string.title_activity_aliases) + " (" + 1 + "/" + tags.size() + ")");
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_aliases_fullscreen);

        tags = getIntent().getStringArrayListExtra("selectedTags");

        mCancelButton = (Button) findViewById(R.id.cancelButton);
        mSkipButton = (Button) findViewById(R.id.skipButton);
        mAddLinkButton = (Button) findViewById(R.id.addLinkButton);
        mTagSetAliasesFullscreen = (EditText) findViewById(R.id.tagSetAliasesFullscreen);
        mAliasSetAliasesFullscreen = (EditText) findViewById(R.id.aliasSetAliasesFullscreen);
        mAnotherAliasesLayout = (LinearLayout) findViewById(R.id.anotherAliasesLayout);

        furryDatabase = new FurryDatabase(getApplicationContext());
        furryDatabaseUtils = new FurryDatabaseUtils(furryDatabase);

        mTagSetAliasesFullscreen.setText(tags.get(currentAlias).toString());
        setTitle(getResources().getString(R.string.title_activity_aliases) + " (" + 1 + "/" + tags.size() + ")");

        mAddLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAliasSetAliasesFullscreen.getText().toString().length() > 0) {
                    Utils.Tuple<String, String> tuple = new Utils.Tuple<String, String>(mTagSetAliasesFullscreen.getText().toString(), mAliasSetAliasesFullscreen.getText().toString());
                    furryDatabaseUtils.addAlias(tuple);
                    mAliasSetAliasesFullscreen.clearFocus();
                    nextAlias();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.need_input_alias_fullscreen), Toast.LENGTH_SHORT).show();
                }
                //hide keyboard
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mAliasSetAliasesFullscreen.getWindowToken(), 0);
                mAliasSetAliasesFullscreen.setText("");
                mAliasSetAliasesFullscreen.clearFocus();
            }
        });

        mAliasSetAliasesFullscreen.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (mAliasSetAliasesFullscreen.getText().toString().length() > 0) {
                    Utils.Tuple<String, String> tuple = new Utils.Tuple<String, String>(mTagSetAliasesFullscreen.getText().toString(), mAliasSetAliasesFullscreen.getText().toString());
                    furryDatabaseUtils.addAlias(tuple);
                    mAliasSetAliasesFullscreen.clearFocus();
                    nextAlias();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.need_input_alias_fullscreen), Toast.LENGTH_SHORT).show();
                }
                //hide keyboard
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mAliasSetAliasesFullscreen.getWindowToken(), 0);
                mAliasSetAliasesFullscreen.setText("");
                mAliasSetAliasesFullscreen.clearFocus();
                return true;
            }
        });

        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAliasSetAliasesFullscreen.setText("");
                mAliasSetAliasesFullscreen.clearFocus();
                nextAlias();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hide keyboard
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mAliasSetAliasesFullscreen.getWindowToken(), 0);
                mAliasSetAliasesFullscreen.clearFocus();
                finish();
            }
        });
    }

    private void nextAlias() {
        if (currentAlias == tags.size() - 1) {
            finish();
        } else {
            currentAlias++;
            int currentAliasplus = currentAlias + 1;
            setTitle(getResources().getString(R.string.title_activity_aliases) + " (" + currentAliasplus + "/" + tags.size() + ")");
            mTagSetAliasesFullscreen.setText(tags.get(currentAlias).toString());
        }
    }
}
