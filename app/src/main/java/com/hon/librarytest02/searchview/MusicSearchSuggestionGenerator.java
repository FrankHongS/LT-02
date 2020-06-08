package com.hon.librarytest02.searchview;

import com.frankhon.simplesearchview.generator.SearchSuggestionGenerator;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Frank Hon on 2020-06-07 13:46.
 * E-mail: frank_hon@foxmail.com
 */
public class MusicSearchSuggestionGenerator extends SearchSuggestionGenerator {
    @Override
    public List<String> generateSearchSuggestion(String query) {
        return null;
    }

    @Override
    public List<String> generateSearchSuggestionWhenEmpty() {
        return Arrays.asList("Monica", "Ross", "Chandler");
    }
}
