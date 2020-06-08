package com.frankhon.simplesearchview.generator;

import android.content.ContentValues;
import android.content.Context;

import com.frankhon.simplesearchview.db.SearchHistoryDao;
import com.frankhon.simplesearchview.db.entity.SearchItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank Hon on 2020-06-07 15:34.
 * E-mail: frank_hon@foxmail.com
 */
public class DefaultSearchSuggestionGenerator extends SearchSuggestionGenerator {

    private final SearchHistoryDao searchHistoryDao;

    public DefaultSearchSuggestionGenerator(Context context) {
        searchHistoryDao = SearchHistoryDao.getInstance(context, getMaxCount());
    }

    @Override
    public List<String> generateSearchSuggestion(String query) {
        return getList(searchHistoryDao.getItemsByQuery(query));
    }

    @Override
    public List<String> generateSearchSuggestionWhenEmpty() {
        return getList(searchHistoryDao.getAllItems());
    }

    private List<String> getList(List<SearchItem> searchItems) {
        List<String> list = new ArrayList<>();
        for (SearchItem item : searchItems) {
            list.add(item.getText());
        }
        return list;
    }
}
