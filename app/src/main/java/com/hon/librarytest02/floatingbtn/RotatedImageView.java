package com.hon.librarytest02.floatingbtn;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.PAINT_FLAGS;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.hon.librarytest02.R;

/**
 * Created by shuaihua on 2021/8/18 5:22 下午
 * Email: shuaihua@staff.sina.com.cn
 */
public class RotatedImageView extends AppCompatImageView {

    private final Paint paint;
    private final Bitmap bitmap;

    public RotatedImageView(Context context) {
        this(context, null);
    }

    public RotatedImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotatedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test01);
        BitmapShader bitmapShader = new BitmapShader(
                bitmap,
                Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT
        );
        int max = Math.max(bitmap.getWidth(), bitmap.getHeight());
        float scale = max * 1.0f / Math.min(bitmap.getWidth(), bitmap.getHeight());
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
        setImageBitmap(bitmap);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawOval(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
    }
}
