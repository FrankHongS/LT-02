package com.hon.librarytest02.camera.camerax;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Frank_Hon on 7/28/2020.
 * E-mail: v-shhong@microsoft.com
 */
final class BitmapUtil {
    public static Bitmap rotate(Bitmap bitmap, int degree) {
        if (bitmap == null) {
            return null;
        }
        if (degree == 0) {
            return bitmap;
        }

        Bitmap result = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // may cause OutOfMemoryError
            result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        if (result == null) {
            result = bitmap;
        }
        return result;
    }

    public static Bitmap decodeSampledBitmapFromByteArray(byte[] data) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static Bitmap decodeSampledBitmapFromContentResolver(Context context, Uri uri) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        decodeBitmap(context, uri, options);
        options.inSampleSize = calculateInSampleSize(options);
        options.inJustDecodeBounds = false;
        return decodeBitmap(context, uri, options);
    }

    public static Bitmap decodeBitmap(Context context, Uri uri, BitmapFactory.Options options) throws IOException {
        Bitmap bitmap = null;
        if (uri != null) {
            ContentResolver cr = context.getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return bitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options) {
        int height = options.outHeight;
        int width = options.outWidth;
        Log.d("BitmapUtil", "calculateInSampleSize: " + height + "," + width);
        int inSampleSize = 1;
        while (height > ImageUploader.MAX_DIMENSION || width > ImageUploader.MAX_DIMENSION) {
            height = height / 2;
            width = width / 2;
            inSampleSize *= 2;
        }
        return inSampleSize;
    }
}
