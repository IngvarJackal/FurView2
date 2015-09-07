package ru.furry.furview2;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

    TextView mAddingBlackTagField;
    TextView mAddToBlackListButton;
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
            Intent intent = new Intent("ru.furry.furview2.BlackListActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); // won't work on 10 API
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_black_list);

            mAddingBlackTagField = (TextView) findViewById(R.id.addingBlackTagField);
            mAddToBlackListButton = (Button) findViewById(R.id.addToBlackListButton);
            mTagsTable = (TableLayout) findViewById(R.id.tagsTableLayout);

            database = new FurryDatabase(getApplicationContext());

            furryDatabaseUtils = new FurryDatabaseUtils(database);

            View.OnClickListener addtagToBlackList = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = mAddingBlackTagField.getText().toString();
                    furryDatabaseUtils.addBlackTag(tag);
                    //TODO Add tag to Black list
                    refreshTagTable();
                }
            };
            mAddToBlackListButton.setOnClickListener(addtagToBlackList);

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
                t.setLines(1);
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

    private void refreshTagTable(){
        List<String> blacklist;
        blacklist = furryDatabaseUtils.getBlacklist();

        mTagsTable.removeAllViewsInLayout();

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
    }

}
