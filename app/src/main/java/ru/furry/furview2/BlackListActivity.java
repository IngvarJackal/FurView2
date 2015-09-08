package ru.furry.furview2;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.furry.furview2.database.FurryDatabase;
import ru.furry.furview2.database.FurryDatabaseUtils;
import ru.furry.furview2.system.DefaultCreator;
import ru.furry.furview2.system.ExtendableWDef;
import ru.furry.furview2.system.Utils;


public class BlackListActivity extends ActionBarActivity { //is need ActionBar?

    private static final int LEN_OF_TAGS_ROW_PORT = 4;
    private static final int TAG_TEXT_LENGTH_PORT = 12;
    private static final int LEN_OF_TAGS_ROW_LAND = 8;
    private static final int TAG_TEXT_LENGTH_LAND = 12;

    int tag_text_lenght;
    int len_of_tags_row;

    EditText mAddingBlackTagField;
    Button mAddToBlackListButton;
    TableLayout mTagsTable;


    FurryDatabase database;
    FurryDatabaseUtils furryDatabaseUtils;
    View.OnLongClickListener delTagFromBlackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            tag_text_lenght = TAG_TEXT_LENGTH_PORT;
            len_of_tags_row = LEN_OF_TAGS_ROW_PORT;
        } else {
            tag_text_lenght = TAG_TEXT_LENGTH_LAND;
            len_of_tags_row = LEN_OF_TAGS_ROW_LAND;
        }

        super.onCreate(savedInstanceState);

        if (!InitialScreenActivity.isStarted) {
            Intent intent = new Intent("ru.furry.furview2.InitialScreenActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); // won't work on 10 API
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_black_list);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getSupportActionBar().hide();
            }

            mAddingBlackTagField = (EditText) findViewById(R.id.addingBlackTagField);
            mAddToBlackListButton = (Button) findViewById(R.id.addToBlackListButton);
            mTagsTable = (TableLayout) findViewById(R.id.tagsTableLayout);

            database = new FurryDatabase(getApplicationContext());
            furryDatabaseUtils = new FurryDatabaseUtils(database);

            mAddToBlackListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = mAddingBlackTagField.getText().toString();
                    if (!"".equals(tag) && !tag.contains(" ")) {
                        furryDatabaseUtils.addBlackTag(tag);
                        mAddingBlackTagField.setText("");
                        refreshTagTable();
                        //hide keyboard
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mAddingBlackTagField.getWindowToken(), 0);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.need_input_tag_blacklist), Toast.LENGTH_SHORT).show();
                    }
                    refreshTagTable();
                }
            });

            mAddingBlackTagField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    String tag = mAddingBlackTagField.getText().toString();
                    if (!"".equals(tag) && !tag.contains(" ")) {
                        furryDatabaseUtils.addBlackTag(tag);
                        refreshTagTable();
                        mAddingBlackTagField.setText("");
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.need_input_tag_blacklist), Toast.LENGTH_SHORT).show();
                    }
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mAddingBlackTagField.getWindowToken(), 0);
                    return true;
                }
            });

            //attempt select text when click on EditText
            /*
            mAddingBlackTagField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editText = (EditText) v;
                    editText.selectAll();
                }
            });
            */

            delTagFromBlackList = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    TextView textView = (TextView) v;
                    furryDatabaseUtils.removeBlackTag(textView.getText().toString());
                    refreshTagTable();
                    return true;
                }
            };

            refreshTagTable();
        }
    }

    class Labelled6Row {
        public List<TextView> items = new ArrayList<>();

        public Labelled6Row(TableLayout table, Context context) {
            LinearLayout linLay = new LinearLayout(context);
            linLay.setOrientation(LinearLayout.HORIZONTAL);
            linLay.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
            TableRow row = new TableRow(context);
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1));
            for (int i = 0; i < len_of_tags_row; i++) {
                TextView t = new TextView(context);
                t.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                t.setGravity(Gravity.CENTER);
                //t.setLines(1);    //set single line
                t.setTextColor(Color.BLACK);
                t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                int padding = (int) (1.5 * getResources().getDisplayMetrics().density + 0.5f);
                t.setPadding(padding, 0, padding, 0);
                items.add(t);
                linLay.addView(t);
            }
            row.addView(linLay);
            table.addView(row);
        }
    }

    class Labelled6RowCreator extends DefaultCreator<Labelled6Row> {
        @Override
        public Labelled6Row getDefaultValue(Object... params) {
            return new Labelled6Row((TableLayout) params[0], (Context) params[1]);
        }
    }

    private void refreshTagTable() {
        List<String> blacklist = furryDatabaseUtils.getBlacklist();

        mTagsTable.removeAllViewsInLayout();
        Log.d("fgsfds", "blacklist.size() = " + blacklist.size());

        if (blacklist.size() > 0) {
            mTagsTable.setVisibility(View.VISIBLE);
            ExtendableWDef<Labelled6Row> tagsLinesHandler = new ExtendableWDef<Labelled6Row>(new Labelled6RowCreator()) {
                @Override
                public void ensureCapacity(int index) {
                    if (entries.size() <= index) {
                        for (int i = 0; i < ((index - entries.size()) * 3 / 2 + 1); i++) {
                            entries.add(creator.getDefaultValue(mTagsTable, getApplicationContext()));
                        }
                    }
                }
            };

            for (int row = 0; row < Math.ceil(blacklist.size() * 1.0 / len_of_tags_row); row++) {
                for (int column = 0; (column < len_of_tags_row) && (row * len_of_tags_row + column < blacklist.size()); column++) {
                    tagsLinesHandler.get(row).items.get(column).setText(Utils.unescapeUnicode(blacklist.get(row * len_of_tags_row + column)), TextView.BufferType.NORMAL);
                    tagsLinesHandler.get(row).items.get(column).setOnLongClickListener(delTagFromBlackList);
                }
            }
        } else {
            mTagsTable.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_blacklist, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem_clear_blacklist = menu.findItem(R.id.action_blacklist_clear);
        if (furryDatabaseUtils.getBlacklist().size()>0){
            menuItem_clear_blacklist.setEnabled(true);
        }else{
            menuItem_clear_blacklist.setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_blacklist_clear: {
                List<String> blacklist = furryDatabaseUtils.getBlacklist();
                for (int i = 0; i < blacklist.size(); i++) {
                    furryDatabaseUtils.removeBlackTag(blacklist.get(i));
                }
                refreshTagTable();
                return true;
            }
            case R.id.action_blacklist_help:
                Intent intent = new Intent("ru.furry.furview2.HelpScreenActivity");
                intent.putExtra("source","BlackListActivity");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
