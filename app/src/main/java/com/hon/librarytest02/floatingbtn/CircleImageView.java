package com.hon.librarytest02.floatingbtn;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import androidx.appcompat.widget.AppCompatImageView;


/**
 * 圆形ImageView
 *
 * @author zhangtao
 */
public class CircleImageView extends AppCompatImageView {
    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 2;

    private static final int DEFAULT_BORDER_WIDTH = 5;
    private static final int DEFAULT_BORDER_COLOR = Color.parseColor("#FE350E");

    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();

    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();

    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private float mDrawableRadius;
    private float mBorderRadius;

    private int mAlpha = 255;

    private float imageRotatedDegree = 0f;
    private float progressRotatedDegree = 0f;

    public CircleImageView(Context context) {
        super(context);

        init();
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        final TypedArray a = context.obtainStyledAttributes(attrs,
//                R.styleable.CircleImageView);
//        mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_civ_border_width, DEFAULT_BORDER_WIDTH);
//        mBorderColor = a.getColor(R.styleable.CircleImageView_civ_border_color, DEFAULT_BORDER_COLOR);
//        a.recycle();
//        init();
    }

    private void init() {
        super.setScaleType(SCALE_TYPE);
//        mReady = true;
//
//        if (mSetupPending) {
//            System.out.println("init -- setup");
//            setup();
//            mSetupPending = false;
//        }
    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format(
                    "ScaleType %s not supported.", scaleType));
        }
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (adjustViewBounds) {
            throw new IllegalArgumentException(
                    "adjustViewBounds not supported.");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, mDrawableRadius,
                mBitmapPaint);
        if (mBorderWidth != 0) {
//            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, mBorderRadius,
//                    mBorderPaint);
            canvas.drawArc(mBorderWidth / 2f, mBorderWidth / 2f, getWidth() - mBorderWidth / 2f,
                    getHeight() - mBorderWidth / 2f, 270f,
                    progressRotatedDegree, false, mBorderPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d("frankhon", "onSizeChanged: ");
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }

        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth != mBorderWidth) {
            mBorderWidth = borderWidth;
            setup();
            invalidate();
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        Log.d("frankhon", "setImageBitmap: ");
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        Log.d("frankhon", "setImageDrawable: ");
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setup();
    }

    @Override
    public void setImageResource(int resId) {
        Log.d("frankhon", "setImageResource: ");
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    @Override
    public void setImageURI(Uri uri) {
        Log.d("frankhon", "setImageURI: ");
        super.setImageURI(uri);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    @Override
    public void setImageAlpha(int alpha) {
        mAlpha = alpha;
        super.setImageAlpha(alpha);
    }

    public void startUpdateProgress(int duration) {
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(0f, 360f);
        progressAnimator.setDuration(duration * 1000L);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.addUpdateListener(animation -> {
            progressRotatedDegree = (float) animation.getAnimatedValue();
            invalidate();
        });

        ValueAnimator imageAnimator = ValueAnimator.ofFloat(0f, 360f);
        imageAnimator.setDuration(5 * 1000);
        imageAnimator.setInterpolator(new LinearInterpolator());
        imageAnimator.setRepeatCount(ValueAnimator.INFINITE);
        imageAnimator.setRepeatMode(ValueAnimator.RESTART);
        imageAnimator.addUpdateListener(animation -> {
            imageRotatedDegree = (float) animation.getAnimatedValue();
            setup();
            invalidate();
        });

        progressAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                imageAnimator.start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                imageAnimator.end();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        progressAnimator.start();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION,
                        COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void setup() {
        // 这个mBitmap比较妙，只有当有图片被设置以后才会开始执行下面的方法。
        if (mBitmap == null) {
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);
        mBitmapPaint.setAlpha(mAlpha);

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setAlpha(mAlpha);

        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        // 整个图像的显示区域：即全部的View大小区域。
        mBorderRect.set(0, 0, getWidth(), getHeight());
        // Border的半径为长宽中取小的那一边，android中drawCircle的半径是内圆的半径，不是外圆的半径。
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2,
                (mBorderRect.width() - mBorderWidth) / 2);

        // 图片显示的区域：即View的大小区域减去边界的大小。
        mDrawableRect.set(mBorderWidth, mBorderWidth, mBorderRect.width()
                - mBorderWidth, mBorderRect.height() - mBorderWidth);
        // 图片的半径大小取图片小边。
        mDrawableRadius = Math.min(mDrawableRect.height() / 2,
                mDrawableRect.width() / 2);

        updateShaderMatrix();
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width()
                * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth,
                (int) (dy + 0.5f) + mBorderWidth);
        mShaderMatrix.postRotate(imageRotatedDegree, getWidth() / 2f, getHeight() / 2f);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

}
