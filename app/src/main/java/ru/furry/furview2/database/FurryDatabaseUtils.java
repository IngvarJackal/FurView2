package ru.furry.furview2.database;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ru.furry.furview2.system.Utils;

public class FurryDatabaseUtils {

    private FurryDatabase database;

    public FurryDatabaseUtils(FurryDatabase database) {
        this.database = database;
    }

    public void addAlias(Utils.Tuple<String, String> alias) {
        ContentValues values = new ContentValues(2);
        values.put("a", alias.x);
        values.put("b", alias.y);
        database.getWritableDatabase().insert("aliases", null, values);
    }
    public void removeAlias(Utils.Tuple<String, String> alias) {
        database.getWritableDatabase().delete("aliases", "a = ? and b = ?", new String[]{alias.x, alias.y});
    }
    public List<Utils.Tuple<String, String>> getAliases() {
        List<Utils.Tuple<String, String>> aliases = new ArrayList<>();
        Cursor cursor = database.getWritableDatabase().query("aliases", new String[] {"a", "b"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            aliases.add(new Utils.Tuple<>(cursor.getString(cursor.getColumnIndex("a")),
                    cursor.getString(cursor.getColumnIndex("b"))));
        }
        return aliases;
    }


    public void addBlackTag(String blackTag) {
        ContentValues values = new ContentValues(1);
        values.put("tag", blackTag);
        database.getWritableDatabase().insert("blacklist", null, values);
    }
    public void removeBlackTag(String blackTag) {
        database.getWritableDatabase().delete("blacklist", "tag = ?", new String[]{blackTag});
    }
    public List<String> getBlacklist() {
        List<String> tags = new ArrayList<>();
        Cursor cursor = database.getWritableDatabase().query("blacklist", new String[] {"tag"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            tags.add(cursor.getString(cursor.getColumnIndex("tag")));
        }
        return tags;
    }




}
