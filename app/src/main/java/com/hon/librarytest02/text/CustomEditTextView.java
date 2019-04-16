package com.hon.librarytest02.text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.hon.librarytest02.R;
import com.hon.librarytest02.util.Util;

import androidx.appcompat.widget.AppCompatEditText;

//思路：
// 先绘制4个bg，
// 取键盘输入的监听
// 绘制文字（如果监听到字数大于4，截取钱4个）
// TODO: 2019/3/5 增加自定义属性,处理光标，处理onmeasure
public class CustomEditTextView extends AppCompatEditText {
    private int mBoxSize;
    private int mSpan;
    private Paint paint;
    private String result;
    private Paint paint1;
    private int bgColor;
    private int tvcolor;
    private TextChangedListenr listener;

    public void setListener(TextChangedListenr listener) {
        this.listener = listener;
    }

    public CustomEditTextView(Context context) {
        this(context,null);
    }

    public CustomEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                result = s.toString().trim();
                int length = result.length();
                if (length > 4) {
                    if (listener != null) {
                        listener.onError();
                    }
                }
                if (listener != null) {
                    listener.onTextChanged(result);
                }
            }
        });
        mBoxSize = getResources().getDimensionPixelSize(R.dimen.xijp_book_size_37dp);
        mSpan = getResources().getDimensionPixelSize(R.dimen.xijp_book_size_7dp);
        bgColor = getResources().getColor(R.color.xijp_book_color_ffb845);
        tvcolor = getResources().getColor(R.color.xijp_book_color_ffffff);
        initPaint();
    }

    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(bgColor);
        paint.setStyle(Paint.Style.FILL);

        paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint1.setColor(tvcolor);
        paint1.setFakeBoldText(true);
        paint1.setTextSize(getTextSize());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int heightSize=MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(mBoxSize*4+3*mSpan,heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawRects(canvas);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        if (TextUtils.isEmpty(result)) {
            return;
        }

        for (int i = 0; i < result.length(); i++) {
            String substring = result.substring(i, i + 1);
            Rect bounds = new Rect();
            paint1.getTextBounds(substring, 0, 1, bounds);
            Paint.FontMetricsInt fontMetrics = paint1.getFontMetricsInt();
            int baseline = (mBoxSize - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
            canvas.drawText(substring, (2 * i * (mBoxSize + mSpan) + mBoxSize) / 2 - bounds.width() / 2, baseline, paint1);
        }
    }

    private void drawRects(Canvas canvas) {
        for (int i = 0; i < 4; i++) {
            Rect rect = new Rect(i * (mBoxSize + mSpan), 0, i * (mBoxSize + mSpan) + mBoxSize, mBoxSize);
            canvas.drawRoundRect(new RectF(rect), 10f, 10f, paint);
        }
    }

    public interface TextChangedListenr {
        void onTextChanged(String msg);

        void onError();
    }
}

