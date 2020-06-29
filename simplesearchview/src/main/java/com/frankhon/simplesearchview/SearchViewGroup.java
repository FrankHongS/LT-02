package com.frankhon.simplesearchview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
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
import androidx.recyclerview.widget.RecyclerView;

import com.frankhon.simplesearchview.db.SearchHistoryDao;
import com.frankhon.simplesearchview.db.entity.SearchItem;
import com.frankhon.simplesearchview.generator.SearchSuggestionGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank Hon on 2020-06-07 00:23.
 * E-mail: frank_hon@foxmail.com
 */
public class SearchViewGroup extends FrameLayout {

    private List<String> mSuggestions;
    private ListView mSuggestionView;
    private EditText mSearchText;
    private ImageButton mClearButton;
    private ImageButton mBackButton;
    // search history
    private View mSearchHistoryLayout;
    private RecyclerView mHistoryRecyclerView;
    private ImageButton mClearHistoryButton;

    private String mSearchHint;
    private boolean isSearchHistoryVisible = false;
    private SearchHistoryItemAdapter mSearchHistoryItemAdapter;

    private SearchItemAdapter mSearchItemAdapter;
    private SearchSuggestionGenerator mSuggestionGenerator;
    private SearchHistoryDao mSearchHistoryDao;
    private final SearchViewExecutors searchViewExecutors;

    private OnSearchListener mOnSearchListener;
    private OnBackClickListener mOnBackClickListener;

    private boolean isTextFromSuggestion = false;
    private State mState = State.INITIAL;
    private String mQuery;
    private boolean isInstanceStateRestored = false;

    public SearchViewGroup(@NonNull Context context) {
        this(context, null);
    }

    public SearchViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(getContext()).inflate(R.layout.layout_search_view, this, true);
        searchViewExecutors = new SearchViewExecutors();
        mSearchHistoryDao = SearchHistoryDao.getInstance(getContext());
        mSearchHistoryDao.init();

        initAttrs(attrs);
        bindView();
        bindListeners();

    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        isInstanceStateRestored = true;
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

        mSearchHistoryLayout = findViewById(R.id.layout_search_history);
        mHistoryRecyclerView = findViewById(R.id.rv_search_history);
        mClearHistoryButton = findViewById(R.id.ib_delete_history);

        mSearchText.setHint(mSearchHint);
        mSuggestions = new ArrayList<>();
        mSearchItemAdapter = new SearchItemAdapter(getContext(), mSuggestions);
        mSuggestionView.setAdapter(mSearchItemAdapter);
        mSearchHistoryLayout.setVisibility(isSearchHistoryVisible ? VISIBLE : GONE);
        mSearchHistoryItemAdapter = new SearchHistoryItemAdapter(searchViewExecutors.getDiskIO());
        mHistoryRecyclerView.setAdapter(mSearchHistoryItemAdapter);
        if (isSearchHistoryVisible) {
            updateSearchHistory();
        }
    }

    private void bindListeners() {
        //当输入完成时
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onSearch(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
        //当按系统返回键时
        //todo 不起作用，有bug
        mSearchText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && !mSuggestions.isEmpty()) {
                    clearView();
                    return true;
                }
                return false;
            }
        });
        //当搜索框文本改变时
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mQuery = s.toString();
                mClearButton.setVisibility(TextUtils.isEmpty(mQuery) ? GONE : VISIBLE);
                if (isInstanceStateRestored) {
                    isInstanceStateRestored = false;
                    return;
                }
                if (!isTextFromSuggestion) {
                    displaySuggestions(mQuery);
                } else {
                    isTextFromSuggestion = false;
                }
            }
        });
        //当搜索框焦点发生改变时
        //当view重建时，会先执行afterTextChanged，然后onFocusChange
        mSearchText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    displaySuggestions(mQuery);
                    mState = State.EDITING;
                }
            }
        });
        //当按清除按钮时
        mClearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchText.setText("");
                mSearchText.requestFocus();
            }
        });
        //当按左边返回箭头时
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
        mClearHistoryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchHistoryItemAdapter.submitList(new ArrayList<SearchItem>());
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
        if (isSearchHistoryVisible) {
            updateSearchHistory();
        }
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

    private void updateSearchHistory() {
        searchViewExecutors.getDiskIO()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        final List<SearchItem> searchItems = mSearchHistoryDao.getAllItems();
                        searchViewExecutors.getMainThread()
                                .execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        mSearchHistoryItemAdapter.submitList(searchItems);
                                    }
                                });
                    }
                });
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

    public void setHistoryCount(int maxCount) {
        mSearchHistoryDao.setHistoryCount(maxCount);
    }

    public void setSuggestionGenerator(SearchSuggestionGenerator suggestionGenerator) {
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

    //搜索框不同状态
    private enum State {
        INITIAL,
        EDITING
    }
}
