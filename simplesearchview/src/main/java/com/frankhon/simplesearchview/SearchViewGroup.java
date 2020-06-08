package com.frankhon.simplesearchview;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.frankhon.simplesearchview.db.SearchHistoryDao;
import com.frankhon.simplesearchview.generator.DefaultSearchSuggestionGenerator;
import com.frankhon.simplesearchview.generator.SearchSuggestionGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank Hon on 2020-06-07 00:23.
 * E-mail: frank_hon@foxmail.com
 */
public class SearchViewGroup extends FrameLayout {

    private SearchItemAdapter mSearchItemAdapter;
    private List<String> mSuggestions;
    private ListView mSuggestionView;
    private EditText mSearchText;
    private ImageButton mClearButton;
    private ImageButton mBackButton;

    private String mSearchHint;

    private SearchSuggestionGenerator mSuggestionGenerator;
    private SearchHistoryDao mSearchHistoryDao;
    private final SearchViewExecutors searchViewExecutors;

    private OnSearchListener mOnSearchListener;
    private OnBackClickListener mOnBackClickListener;

    private boolean isTextFromSuggestion = false;
    private State mState = State.INITIAL;

    public SearchViewGroup(@NonNull Context context) {
        this(context, null);
    }

    public SearchViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(getContext()).inflate(R.layout.layout_search_view, this, true);
        initAttrs(attrs);
        bindView();
        bindListeners();

        searchViewExecutors = new SearchViewExecutors();
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs,
                    R.styleable.SearchViewGroup);
            mSearchHint = ta.getString(R.styleable.SearchViewGroup_searchHint);
            ta.recycle();
        }
    }

    private void bindView() {
        mSearchText = findViewById(R.id.et_search);
        mClearButton = findViewById(R.id.ib_search_clear);
        mBackButton = findViewById(R.id.ib_search_back);
        mSuggestionView = findViewById(R.id.lv_suggestions);

        mSearchText.setHint(mSearchHint);
        mSuggestions = new ArrayList<>();
        mSearchItemAdapter = new SearchItemAdapter(getContext(), mSuggestions);
        mSuggestionView.setAdapter(mSearchItemAdapter);
    }

    private void bindListeners() {
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Log.d("SearchViewGroup", "onEditorAction: " + actionId);
                    onSearch(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
        //todo 不起作用，有bug
        mSearchText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && !mSuggestions.isEmpty()) {
                    Log.d("SearchViewGroup", "onKey: KEYCODE_BACK");
                    clearView();
                    return true;
                }
                return false;
            }
        });
        mSearchText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    displaySuggestions("");
                    mState = State.EDITING;
                } else {
                    hideSuggestions();
                }
            }
        });
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString();
                if (!isTextFromSuggestion) {
                    displaySuggestions(query);
                } else {
                    isTextFromSuggestion = false;
                }
                mClearButton.setVisibility(TextUtils.isEmpty(query) ? GONE : VISIBLE);
            }
        });
        mClearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchText.setText("");
                mSearchText.requestFocus();
            }
        });
        mBackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mState != State.INITIAL) {
                    clearView();
                } else {
                    if (mOnBackClickListener != null) {
                        mOnBackClickListener.onClick();
                    }
                }
            }
        });
        mSuggestionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String query = mSuggestions.get(position);
                onSearch(query);
                isTextFromSuggestion = true;
                mSearchText.setText(query);
            }
        });
    }

    private void displaySuggestions(final String query) {
        if (mSuggestionGenerator != null) {
            searchViewExecutors
                    .getDiskIO()
                    .execute(new Runnable() {
                        @Override
                        public void run() {
                            List<String> suggestions;
                            if (TextUtils.isEmpty(query)) {
                                suggestions = mSuggestionGenerator.generateSearchSuggestionWhenEmpty();
                            } else {
                                suggestions = mSuggestionGenerator.generateSearchSuggestion(query);
                            }
                            final List<String> suggestionsTemp = suggestions;
                            searchViewExecutors
                                    .getMainThread()
                                    .execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (suggestionsTemp != null && !suggestionsTemp.isEmpty()) {
                                                mSuggestions.clear();
                                                mSuggestions.addAll(suggestionsTemp);
                                                mSearchItemAdapter.notifyDataSetChanged();
                                            } else {
                                                hideSuggestions();
                                            }
                                        }
                                    });
                        }
                    });
        }
    }

    private void hideSuggestions() {
        if (!mSuggestions.isEmpty()) {
            mSearchItemAdapter.clear();
        }
    }

    private void onSearch(String query) {
        if (mOnSearchListener != null) {
            mOnSearchListener.onSearch(query);
        }
        clearView();
        saveQuery(query);
    }

    private void saveQuery(final String query) {
        if (mSearchHistoryDao != null) {
            searchViewExecutors
                    .getDiskIO()
                    .execute(new Runnable() {
                        @Override
                        public void run() {
                            mSearchHistoryDao.insert(query);
                        }
                    });
        }
    }

    private void clearView() {
        hideSuggestions();
        hideSoftKeyboard();
        mSearchText.clearFocus();
        mState = State.INITIAL;
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getApplicationWindowToken(), 0);
    }

    /*
        clear all tasks and close db
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        searchViewExecutors.release();
        if (mSearchHistoryDao != null) {
            mSearchHistoryDao.close();
        }
    }

    public void setSuggestionGenerator(SearchSuggestionGenerator suggestionGenerator) {
        if (suggestionGenerator instanceof DefaultSearchSuggestionGenerator) {
            mSearchHistoryDao = SearchHistoryDao.getInstance(getContext(), suggestionGenerator.getMaxCount());
            mSearchHistoryDao.init();
        }
        this.mSuggestionGenerator = suggestionGenerator;
    }

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.mOnSearchListener = onSearchListener;
    }

    public void setOnBackClickListener(OnBackClickListener onBackClickListener) {
        this.mOnBackClickListener = onBackClickListener;
    }

    public interface OnSearchListener {
        void onSearch(String query);
    }

    public interface OnBackClickListener {
        void onClick();
    }

    private enum State {
        INITIAL,
        EDITING
    }
}
