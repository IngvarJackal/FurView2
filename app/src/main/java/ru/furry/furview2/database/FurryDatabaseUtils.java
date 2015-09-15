package ru.furry.furview2.database;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.furry.furview2.MainActivity;
import ru.furry.furview2.system.Utils;

public class FurryDatabaseUtils {

    private FurryDatabase database;
    private static List<Utils.Tuple<String, String>> aliases;
    private static List<String> bTags;

    public FurryDatabaseUtils(FurryDatabase database) {
        this.database = database;
    }

    public void addAlias(Utils.Tuple<String, String> alias) {
        ContentValues values = new ContentValues(2);
        values.put("a", alias.x);
        values.put("b", alias.y);
        database.getWritableDatabase().insert("aliases", null, values);
        if (this.aliases != null && !this.aliases.contains(alias))
            this.aliases.add(alias);
    }

    public void removeAlias(Utils.Tuple<String, String> alias) {
        database.getWritableDatabase().delete("aliases", "a = ? and b = ?", new String[]{alias.x, alias.y});
        if (this.aliases != null)
            this.aliases.remove(alias);
    }

    public List<Utils.Tuple<String, String>> getAliases() {
        if (this.aliases != null)
            return new ArrayList<>(this.aliases);
        List<Utils.Tuple<String, String>> aliases = new ArrayList<>();
        Cursor cursor = database.getWritableDatabase().query("aliases", new String[]{"a", "b"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            aliases.add(new Utils.Tuple<>(cursor.getString(cursor.getColumnIndex("a")),
                    cursor.getString(cursor.getColumnIndex("b"))));
        }
        this.aliases = new ArrayList<>(aliases);
        return aliases;
    }

    public List<Utils.Tuple<String, String>> getPortionAliases(int offset, int maxResult) {
        if (this.aliases != null)
            return new ArrayList<>(this.aliases);
        List<Utils.Tuple<String, String>> aliases = new ArrayList<>();
        Cursor cursor = database.getWritableDatabase().rawQuery("select * from aliases " + "limit ?,?",
                new String[]{String.valueOf(offset),String.valueOf(maxResult)});
        while (cursor.moveToNext()) {
            aliases.add(new Utils.Tuple<>(cursor.getString(cursor.getColumnIndex("a")),
                    cursor.getString(cursor.getColumnIndex("b"))));
        }
        this.aliases = new ArrayList<>(aliases);
        cursor.close();
        return aliases;
    }

    public int countElements() {
        int count=0;
        Cursor cursor = database.getWritableDatabase().rawQuery("select * from aliases",null);
        count=cursor.getCount();
        cursor.close();
        return count;
    }


    public void addBlackTag(String blackTag) {
        ContentValues values = new ContentValues(1);
        values.put("tag", blackTag);
        database.getWritableDatabase().insert("blacklist", null, values);
        if (this.bTags != null && !this.bTags.contains(blackTag))
            this.bTags.add(blackTag);
    }

    public void removeBlackTag(String blackTag) {
        database.getWritableDatabase().delete("blacklist", "tag = ?", new String[]{blackTag});
        if (this.bTags != null)
            this.bTags.remove(blackTag);
    }

    public List<String> getBlacklist() {
        if (this.bTags != null)
            return new ArrayList<>(this.bTags);
        List<String> tags = new ArrayList<>();
        Cursor cursor = database.getWritableDatabase().query("blacklist", new String[]{"tag"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            tags.add(cursor.getString(cursor.getColumnIndex("tag")));
        }
        this.bTags = new ArrayList<>(tags);
        return tags;
    }

    private Map<String, List<String>> aliasedBTags = new HashMap<>();

    public List<String> getAliasedBlackTag(String bTag) {
        if (aliasedBTags.containsKey(bTag)) {
            return new ArrayList<>(aliasedBTags.get(bTag));
        } else {
            ArrayList<String> tags = new ArrayList<>();
            Cursor cursor = database.getWritableDatabase().query("aliases", new String[]{"b"}, "a = ?", new String[] {bTag}, null, null, null);
            while (cursor.moveToNext()) {
                tags.add(cursor.getString(cursor.getColumnIndex("b")));
            }
            tags.add(bTag);
            aliasedBTags.put(bTag, tags);
            return new ArrayList<>(tags);
        }
    }

    public List<String> getAliasedBlacklist(List<String> blackList) {
        ArrayList<String> aliasedBlackList = new ArrayList<>();
        for (String bTag : blackList) {
            aliasedBlackList.addAll(getAliasedBlackTag(bTag));
        }
        return aliasedBlackList;
    }
}
