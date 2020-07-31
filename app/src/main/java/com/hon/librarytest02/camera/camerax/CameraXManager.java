package com.hon.librarytest02.camera.camerax;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Frank_Hon on 7/27/2020.
 * E-mail: v-shhong@microsoft.com
 */
final class CameraXManager {
    private static final String TAG = "CameraXManager";
    private ImageCapture mImageCapture;
    private ProcessCameraProvider mCameraProvider;
    private int mCurrentLensFacing = CameraSelector.LENS_FACING_BACK;
    private boolean isSearching = false;

    private final ExecutorService cameraExecutor;
    private final FragmentActivity fragmentActivity;
    private final PreviewView previewView;
    private final OnImageCapturedListener onImageCapturedListener;
    private final RotateSensor rotateSensor;

    public CameraXManager(FragmentActivity fragmentActivity, PreviewView previewView, OnImageCapturedListener onImageCapturedListener) {
        this.fragmentActivity = fragmentActivity;
        this.previewView = previewView;
        this.onImageCapturedListener = onImageCapturedListener;

        cameraExecutor = Executors.newSingleThreadExecutor();
        rotateSensor = new RotateSensor(fragmentActivity);
        previewView.setPreferredImplementationMode(PreviewView.ImplementationMode.TEXTURE_VIEW);

    }

    void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(fragmentActivity);
        cameraProviderFuture.addListener(() -> {
            try {
                mCameraProvider = cameraProviderFuture.get();
                bindCameraUseCases();
            } catch (Exception e) {
                //do nothing
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(fragmentActivity));
    }

    void takePhoto() {
        if (isSearching) {
            return;
        }
        if (mImageCapture != null) {
            isSearching = true;
            int rotateAngle = rotateSensor.getCurrentDegree(mCurrentLensFacing);
            mImageCapture.takePicture(cameraExecutor, new ImageCapture.OnImageCapturedCallback() {
                @Override
                public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                    ByteBuffer buffer = imageProxy.getPlanes()[0].getBuffer();
                    buffer.rewind();
                    byte[] data = new byte[buffer.remaining()];
                    buffer.get(data);
                    Bitmap tempBitmap = BitmapUtil.decodeSampledBitmapFromByteArray(data);
                    int tempRotateAngle = rotateAngle;
                    // some phones(such as samsung), the rear lens rotates 90 degrees counterclockwise,
                    // the front lens rotates 90 degrees clockwise
                    if (tempBitmap.getHeight() < tempBitmap.getWidth()) {
                        if (mCurrentLensFacing == CameraSelector.LENS_FACING_BACK) {
                            tempRotateAngle += 90;
                        } else {
                            tempRotateAngle -= 90;
                        }
                    }
                    // rotate photo by sensor orientation
                    Bitmap bitmap = BitmapUtil.rotate(tempBitmap, tempRotateAngle % 360);
                    // Important, otherwise unable to issue request if the ImageReader has no available image buffer left.
                    imageProxy.close();
                    fragmentActivity.runOnUiThread(() -> {
                        if (onImageCapturedListener != null) {
                            onImageCapturedListener.onCaptureSuccess(bitmap);
                        }
                    });
                    isSearching = false;
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    fragmentActivity.runOnUiThread(() -> {
                        if (onImageCapturedListener != null) {
                            onImageCapturedListener.onError(exception);
                        }
                    });
                    exception.printStackTrace();
                    isSearching = false;
                }
            });
        }
    }

    void switchCamera() {
        if (mCurrentLensFacing == CameraSelector.LENS_FACING_BACK) {
            mCurrentLensFacing = CameraSelector.LENS_FACING_FRONT;
        } else {
            mCurrentLensFacing = CameraSelector.LENS_FACING_BACK;
        }
        bindCameraUseCases();
    }

    private void bindCameraUseCases() {
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(mCurrentLensFacing).build();
//        int rotation = previewView.getDisplay().getRotation();
        Preview preview = new Preview.Builder()
//                .setTargetRotation(rotation)
                .build();
        mImageCapture = new ImageCapture.Builder()
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
//                .setTargetRotation(rotation)
                .build();
        // Unbind use cases before rebinding
        mCameraProvider.unbindAll();
        try {
            // Bind use cases to camera
            Camera camera = mCameraProvider.bindToLifecycle(fragmentActivity, cameraSelector, preview, mImageCapture);
            preview.setSurfaceProvider(previewView.createSurfaceProvider());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getCurrentLensFacing() {
        return mCurrentLensFacing;
    }

    public void setCurrentLensFacing(int mCurrentLensFacing) {
        this.mCurrentLensFacing = mCurrentLensFacing;
    }

    interface OnImageCapturedListener {
        void onCaptureSuccess(Bitmap bitmap);

        void onError(ImageCaptureException exception);
    }
}
