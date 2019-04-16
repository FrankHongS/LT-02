package com.hon.librarytest02.text;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.hon.librarytest02.R;
import com.hon.mylogger.MyLogger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Frank_Hon on 3/15/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class MyEditTextGroup extends FrameLayout implements View.OnKeyListener {

    private EditText mFirstText;
    private EditText mSecondText;
    private EditText mThirdText;
    private EditText mForthText;

    private OnTextChangeListener mListener;

    public MyEditTextGroup(@NonNull Context context) {
        this(context,null);
    }

    public MyEditTextGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyEditTextGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view= LayoutInflater.from(context).inflate(R.layout.layout_my_edit_text,this,false);

        initView(view);

        addView(view);

    }

    private void initView(View view) {

        mFirstText=view.findViewById(R.id.et_first);
        mSecondText=view.findViewById(R.id.et_second);
        mThirdText=view.findViewById(R.id.et_third);
        mForthText=view.findViewById(R.id.et_forth);

        mFirstText.addTextChangedListener(new SimpleTextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>=1){
                    if(mListener!=null){
                        mListener.onTextChange(s);
                    }
                    changeFocus(mSecondText);
                }
            }
        });
        mSecondText.addTextChangedListener(new SimpleTextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>=1){
                    if(mListener!=null){
                        mListener.onTextChange(s);
                    }
                    changeFocus(mThirdText);
                }
            }
        });
        mThirdText.addTextChangedListener(new SimpleTextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>=1){
                    if(mListener!=null){
                        mListener.onTextChange(s);
                    }
                    changeFocus(mForthText);
                }
            }
        });

        mForthText.addTextChangedListener(new SimpleTextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>=1){
                    if(mListener!=null){
                        mListener.onTextChange(s);
                    }
                }
            }
        });

        mSecondText.setOnKeyListener(this);
        mThirdText.setOnKeyListener(this);
        mForthText.setOnKeyListener(this);

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_DEL&&event.getAction()==KeyEvent.ACTION_DOWN){

            switch (v.getId()){
                case R.id.et_second:
                    if(mSecondText.getText().length()==0){
                       changeFocus(mFirstText);
                        return true;
                    }
                    break;
                case R.id.et_third:
                    if(mThirdText.getText().length()==0){
                        changeFocus(mSecondText);
                        return true;
                    }
                    break;
                case R.id.et_forth:
                    if(mForthText.getText().length()==0){
                        changeFocus(mThirdText);
                        return true;
                    }
                    break;
                default:
                    break;
            }

        }

        return false;
    }

    private void changeFocus(EditText newFocus){
        newFocus.requestFocus();
        newFocus.findFocus();

        newFocus.setSelection(newFocus.getText().length());
    }

    public void setOnTextChangeListener(OnTextChangeListener listener) {
        this.mListener = listener;
    }

    public interface OnTextChangeListener{

        void onTextChange(CharSequence s);

    }
}
