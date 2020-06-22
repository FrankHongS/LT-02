package com.frankhon.simplesearchview.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.frankhon.simplesearchview.db.entity.SearchItem;

import java.util.ArrayList;
import java.util.List;

import static com.frankhon.simplesearchview.db.SearchHistoryDatabase.SEARCH_HISTORY_COLUMN_ID;
import static com.frankhon.simplesearchview.db.SearchHistoryDatabase.SEARCH_HISTORY_COLUMN_TEXT;
import static com.frankhon.simplesearchview.db.SearchHistoryDatabase.SEARCH_HISTORY_TABLE;

/**
 * Created by Frank Hon on 2020-06-07 15:54.
 * E-mail: frank_hon@foxmail.com
 */
public class SearchHistoryDao {

    private static volatile SearchHistoryDao INSTANCE;

    private static final String QUERY_SQL = "SELECT * FROM " + SEARCH_HISTORY_TABLE + " WHERE " + SEARCH_HISTORY_COLUMN_TEXT + " =?";

    private final SearchHistoryDatabase dbHelper;
    private SQLiteDatabase db;
    private int maxCount = 6;

    private boolean isDBClosed = false;

    private SearchHistoryDao(Context context) {
        this.dbHelper = new SearchHistoryDatabase(context.getApplicationContext());
        this.db = dbHelper.getWritableDatabase();
    }

    public static SearchHistoryDao getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SearchHistoryDao.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SearchHistoryDao(context);
                }
            }
        }
        return INSTANCE;
    }

    public void init() {
        if (isDBClosed) {
            db = dbHelper.getWritableDatabase();
        }
    }

    public void close() {
        if (!isDBClosed) {
            dbHelper.close();
            isDBClosed = true;
        }
    }

    public void setHistoryCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public void insert(String text) {
        if (text != null && !"".equals(text)) {
            SearchItem queryResult = query(text);
            if (queryResult != null) {
                deleteById(queryResult.getId());
            } else {
                int itemCount = getItemCount();
                if (itemCount >= maxCount) {
                    SearchItem firstItem = queryFirst();
                    if (firstItem != null) {
                        deleteSmallerThanAndEqualsId(firstItem.getId());
                    }
                }
            }
            ContentValues values = new ContentValues();
            values.put(SEARCH_HISTORY_COLUMN_TEXT, text);
            db.insert(SEARCH_HISTORY_TABLE, null, values);
        }
    }

    private SearchItem queryFirst() {
        Cursor cursor = db.rawQuery("SELECT * FROM " + SEARCH_HISTORY_TABLE,
                null);
        try {
            if (cursor.moveToFirst()) {
                return new SearchItem(cursor.getInt(0), cursor.getString(1));
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    private SearchItem query(String text) {
        if (text != null && !"".equals(text)) {
            Cursor cursor = db.rawQuery(QUERY_SQL, new String[]{text});
            try {
                if (cursor.moveToFirst()) {
                    return new SearchItem(cursor.getInt(0), cursor.getString(1));
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public List<SearchItem> getAllItems() {
        String sql = "SELECT * FROM " + SEARCH_HISTORY_TABLE + " ORDER BY " + SEARCH_HISTORY_COLUMN_ID + " DESC";
        Cursor cursor = db.rawQuery(sql, null);
        List<SearchItem> searchItemList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                SearchItem item = new SearchItem(cursor.getInt(0), cursor.getString(1));
                searchItemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return searchItemList;
    }

    public List<SearchItem> getItemsByQuery(String query) {
        String sql = "SELECT * FROM " + SEARCH_HISTORY_TABLE
                + " WHERE " + SEARCH_HISTORY_COLUMN_TEXT + " like '%" + query
                + "%' ORDER BY " + SEARCH_HISTORY_COLUMN_ID + " DESC";
        Cursor cursor = db.rawQuery(sql, null);
        List<SearchItem> searchItemList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                SearchItem item = new SearchItem(cursor.getInt(0), cursor.getString(1));
                searchItemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return searchItemList;
    }

    private int getItemCount() {
        String sql = "SELECT * FROM " + SearchHistoryDatabase.SEARCH_HISTORY_TABLE;
        Cursor cursor = db.rawQuery(sql, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    private void deleteById(int id) {
        db.delete(SEARCH_HISTORY_TABLE, SEARCH_HISTORY_COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    private void deleteSmallerThanAndEqualsId(int id) {
        db.delete(SEARCH_HISTORY_TABLE, SEARCH_HISTORY_COLUMN_ID + " <= ?", new String[]{String.valueOf(id)});
    }
}
