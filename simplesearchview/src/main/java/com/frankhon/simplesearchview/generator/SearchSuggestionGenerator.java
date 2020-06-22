package com.frankhon.simplesearchview.generator;

import java.util.List;

/**
 * Created by Frank Hon on 2020-06-07 13:24.
 * E-mail: frank_hon@foxmail.com
 */
public abstract class SearchSuggestionGenerator {

    public abstract List<String> generateSearchSuggestion(String query);

    public List<String> generateSearchSuggestionWhenEmpty() {
        return null;
    }

}
