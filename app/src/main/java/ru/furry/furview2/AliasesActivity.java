package ru.furry.furview2;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ScrollView;
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

public class AliasesActivity extends AppCompatActivity {

    private static final int LEN_OF_TAGS_ROW_PORT = 3;
    private static final int LEN_OF_TAGS_ROW_LAND = 5;

    int len_of_tags_row;

    Button mAddAliasButton;
    EditText mEditTextAddAliasTag;
    EditText mEditTextAddAliasAlias;
    TableLayout mAliasesTable;
    ScrollView msvAlias;


    FurryDatabase database;
    FurryDatabaseUtils furryDatabaseUtils;
    View.OnLongClickListener delAlias;


    int offset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            len_of_tags_row = LEN_OF_TAGS_ROW_PORT;
        } else {
            len_of_tags_row = LEN_OF_TAGS_ROW_LAND;
        }

        super.onCreate(savedInstanceState);

        if (!InitialScreenActivity.isStarted) {
            Intent intent = new Intent("ru.furry.furview2.InitialScreenActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); // won't work on 10 API
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_aliases);

            mEditTextAddAliasTag = (EditText) findViewById(R.id.editTextAddAliasTag);
            mEditTextAddAliasAlias = (EditText) findViewById(R.id.editTextAddAliasAlias);
            mAddAliasButton = (Button) findViewById(R.id.addAliasButton);
            mAliasesTable = (TableLayout) findViewById(R.id.aliasesTableLayout);
            msvAlias = (ScrollView) findViewById(R.id.svAlias);

            database = new FurryDatabase(getApplicationContext());
            furryDatabaseUtils = new FurryDatabaseUtils(database);

            mAddAliasButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = mEditTextAddAliasTag.getText().toString().trim();
                    String alias = mEditTextAddAliasAlias.getText().toString().trim();
                    Utils.Tuple<String, String> tuple = new Utils.Tuple<String, String>(tag, alias);
                    if (!"".equals(tag) && !tag.contains(" ") && alias.length() > 0 && !tag.equals(alias)) {
                        furryDatabaseUtils.addAlias(tuple);
                        //cleat editTextes
                        mEditTextAddAliasTag.setText("");
                        mEditTextAddAliasAlias.setText("");
                        refreshAliasTable();
                        //hide keyboard
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mEditTextAddAliasAlias.getWindowToken(), 0);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.need_input_alias), Toast.LENGTH_SHORT).show();
                    }
                    refreshAliasTable();
                }
            });

/*
            //for scrollView with autuadding aliases http://stackoverflow.com/questions/7609253/how-to-get-last-scroll-view-position-scrollview
            Log.d("fgsfds", "current scrollView = " + msvAlias.getScrollY());
            Log.d("fgsfds", "getMaxScrollAmount scrollView = " + msvAlias.getMaxScrollAmount());
*/
            mEditTextAddAliasTag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    mEditTextAddAliasAlias.requestFocus();
                    mEditTextAddAliasAlias.selectAll();
                    return true;
                }
            });

            mEditTextAddAliasAlias.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    String tag = mEditTextAddAliasTag.getText().toString().trim();
                    String alias = mEditTextAddAliasAlias.getText().toString().trim();
                    Utils.Tuple<String, String> tuple = new Utils.Tuple<String, String>(tag, alias);
                    if (!"".equals(tag) && !tag.contains(" ") && alias.length() > 0 && !tag.equals(alias)) {
                        furryDatabaseUtils.addAlias(tuple);
                        //cleat editTextes
                        mEditTextAddAliasTag.setText("");
                        mEditTextAddAliasAlias.setText("");
                        refreshAliasTable();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.need_input_alias), Toast.LENGTH_SHORT).show();
                    }
                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEditTextAddAliasAlias.getWindowToken(), 0);
                    mEditTextAddAliasAlias.clearFocus();
                    return true;
                }
            });

            delAlias = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    TextView textView = (TextView) v;
                    int pos = textView.getText().toString().indexOf(" ");
                    String tag = textView.getText().toString().substring(0, pos);
                    String alias = textView.getText().toString().substring(pos + 3);
                    Utils.Tuple<String, String> tuple = new Utils.Tuple<>(tag, alias);
//TODO working after restart...
                    furryDatabaseUtils.removeAlias(tuple);
                    refreshAliasTable();
                    return true;
                }
            };

            refreshAliasTable();
        }
    }

    class LabelledXRow {
        public List<TextView> items = new ArrayList<>();

        public LabelledXRow(TableLayout table, Context context) {
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

    class Labelled6RowCreator extends DefaultCreator<LabelledXRow> {
        @Override
        public LabelledXRow getDefaultValue(Object... params) {
            return new LabelledXRow((TableLayout) params[0], (Context) params[1]);
        }
    }

    //TODO need test for performance. Too long when we have a many aliases! (First time is 6187 aliases 12.09.2005)
    private void refreshAliasTable() {
        //int sizeAll = furryDatabaseUtils.getAliases().size();     //don't use. May be replace as "count * ..." in FurryDatabaseUtils

        offset = 6187; //
        List<Utils.Tuple<String, String>> aliasesList = furryDatabaseUtils.getPortionAliases(offset, 30); //users aliases
        int size = aliasesList.size();

        mAliasesTable.removeAllViewsInLayout(); //clear previous table with aliases
        //Log.d("fgsfds", "Aliases in DB = " + sizeAll);
        Log.d("fgsfds", "Portion of aliases = " + size);

        if (size > 0) {
            mAliasesTable.setVisibility(View.VISIBLE);
            ExtendableWDef<LabelledXRow> aliasLinesHandler = new ExtendableWDef<LabelledXRow>(new Labelled6RowCreator()) {
                @Override
                public void ensureCapacity(int index) {
                    if (entries.size() <= index) {
                        for (int i = 0; i < ((index - entries.size()) * 3 / 2 + 1); i++) {
                            entries.add(creator.getDefaultValue(mAliasesTable, getApplicationContext()));
                        }
                    }
                }
            };

            for (int row = 0; row < Math.ceil(size * 1.0 / len_of_tags_row); row++) {
                for (int column = 0; (column < len_of_tags_row) && (row * len_of_tags_row + column < size); column++) {
                    aliasLinesHandler.get(row).items.get(column).setText(
                            //furryDatabaseUtils.getAliases().get(row * len_of_tags_row + column).x +
                            aliasesList.get(row * len_of_tags_row + column).x +
                                    " = " +
                                    //furryDatabaseUtils.getAliases().get(row * len_of_tags_row + column).y);
                                    aliasesList.get(row * len_of_tags_row + column).y);
                    aliasLinesHandler.get(row).items.get(column).setOnLongClickListener(delAlias);
                }
            }
        } else {
            mAliasesTable.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_aliases, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem_clear_blacklist = menu.findItem(R.id.action_aliases_clear);
        if (furryDatabaseUtils.getAliases().size() > 0) {
            menuItem_clear_blacklist.setEnabled(true);
        } else {
            menuItem_clear_blacklist.setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_aliases_clear: {
                List<Utils.Tuple<String, String>> aliasesList = furryDatabaseUtils.getAliases();
                for (int i = 0; i < aliasesList.size(); i++) {
//TODO this is slow... need another method
                    furryDatabaseUtils.removeAlias(aliasesList.get(i));
                }
                refreshAliasTable();
                return true;
            }
            case R.id.action_aliases_help:
                Intent intent = new Intent("ru.furry.furview2.HelpScreenActivity");
                intent.putExtra("source", "AliasesActivity");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}