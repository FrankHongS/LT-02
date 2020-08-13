package com.hon.librarytest02.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;

import com.hon.mylogger.MyLogger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Frank_Hon on 7/21/2020.
 * E-mail: v-shhong@microsoft.com
 */
public final class RotateUtil {

    public static Bitmap rotate(Context context, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        File imageFile = new File(context.getFilesDir(), UUID.randomUUID().toString().replace("-", "") + ".tmp");
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            OutputStream outputStream = new FileOutputStream(imageFile);
            outputStream.write(baos.toByteArray());
            outputStream.close();
            int degree = getDegree(imageFile.getAbsolutePath());
            if (degree == 0) {
                return bitmap;
            }
            MyLogger.d("rotate: " + degree);
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (result != null) {
                return result;
            }
            return bitmap;
        } catch (IOException e) {
            return bitmap;
        }finally {
            imageFile.delete();
        }
    }

    private static int getDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            MyLogger.d(orientation+"");
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            //do nothing
        }

        return degree;
    }

}
