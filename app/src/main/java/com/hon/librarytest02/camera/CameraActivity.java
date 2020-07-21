package com.hon.librarytest02.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.hon.librarytest02.R;
import com.hon.mylogger.MyLogger;

import java.util.Arrays;
import java.util.List;

import static android.graphics.ImageFormat.JPEG;

/**
 * Created by Frank Hon on 2020/7/20 11:52 PM.
 * E-mail: frank_hon@foxmail.com
 */
@TargetApi(value = Build.VERSION_CODES.LOLLIPOP)
public class CameraActivity extends AppCompatActivity {

    private CameraManager mCameraManager;
    private Handler mBackgroundHandler;
    private String[] mCameraIds = new String[2];

    private CameraDevice mFrontDevice;
    private TextureView mFrontTexture;
    private SurfaceTexture mFrontSurfaceTexture;
    private CameraCharacteristics mFrontCharacteristics;

    private ImageReader mImageReader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowFlag();
        setContentView(R.layout.activity_camera);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                initCamera();
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 10);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (permission.equals(Manifest.permission.CAMERA)) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    initCamera();
                } else {
                    Toast.makeText(this, "Sorry, no camera permission", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    private void setWindowFlag() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        window.setAttributes(params);
    }

    private void initView() {
        mFrontTexture = findViewById(R.id.tv_front);
    }

    private void initCamera() {
        initBackgroundHandler();
        mCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String[] cameraIdList = mCameraManager.getCameraIdList();
            for (String cameraId : cameraIdList) {
                CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null) {
                    if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                        mCameraIds[0] = cameraId;
                        mFrontCharacteristics = characteristics;
                    } else if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                        mCameraIds[1] = cameraId;
                    }
                }
            }
            openDevice();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void initBackgroundHandler() {
        HandlerThread handlerThread = new HandlerThread("camera");
        handlerThread.start();
        mBackgroundHandler = new Handler(handlerThread.getLooper());
    }

    @SuppressLint("MissingPermission")
    private void openDevice() {
        try {
            for (String cameraId : mCameraIds) {
                if (!"".equals(cameraId)) {
                    mCameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                        @Override
                        public void onOpened(@NonNull CameraDevice camera) {
                            String id = camera.getId();
                            if (id.equals(mCameraIds[0])) {
                                mFrontDevice = camera;
                            }
                            if (mFrontDevice != null) {
                                if (mFrontTexture.isAvailable()) {
                                    mFrontSurfaceTexture = mFrontTexture.getSurfaceTexture();
                                    createCaptureSession(mFrontDevice, mFrontSurfaceTexture);
                                } else {
                                    mFrontTexture.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                                        @Override
                                        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                                            mFrontSurfaceTexture = mFrontTexture.getSurfaceTexture();
                                            createCaptureSession(mFrontDevice, mFrontSurfaceTexture);
                                        }

                                        @Override
                                        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

                                        }

                                        @Override
                                        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                                            return false;
                                        }

                                        @Override
                                        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                                        }
                                    });
                                }

                            }
                        }

                        @Override
                        public void onDisconnected(@NonNull CameraDevice camera) {
                            camera.close();
                        }

                        @Override
                        public void onError(@NonNull CameraDevice camera, int error) {
                            camera.close();
                        }
                    }, mBackgroundHandler);
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // createCaptureSession
    private void createCaptureSession(CameraDevice cameraDevice, SurfaceTexture surfaceTexture) {
        try {
            cameraDevice.createCaptureSession(setOutputSize(mFrontCharacteristics, surfaceTexture),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            try {
                                CaptureRequest captureRequest = buildCaptureRequest(mFrontDevice, mFrontTexture.getSurfaceTexture());
                                if (captureRequest != null) {
                                    session.setRepeatingRequest(captureRequest, new CameraCaptureSession.CaptureCallback() {
                                        @Override
                                        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                                            super.onCaptureStarted(session, request, timestamp, frameNumber);
                                        }

                                        @Override
                                        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                                            super.onCaptureCompleted(session, request, result);
                                        }
                                    }, null);
                                }
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //config photo size and preview size
    private List<Surface> setOutputSize(CameraCharacteristics characteristics, SurfaceTexture texture) {
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Size[] supportSize = map.getOutputSizes(JPEG);
        Arrays.sort(supportSize, (o1, o2) -> o2.getWidth() * o2.getHeight() - o1.getWidth() * o1.getHeight());
        Size pictureSize = supportSize[0];
        // config surface
        Surface surface = new Surface(texture);
        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
        mImageReader = ImageReader.newInstance(pictureSize.getWidth(),
                pictureSize.getHeight(), JPEG, 1);
        mImageReader.setOnImageAvailableListener(reader -> {
            MyLogger.d("onImageAvailable");
        }, mBackgroundHandler);
        return Arrays.asList(surface, mImageReader.getSurface());
    }

    // buildCaptureRequest
    private CaptureRequest buildCaptureRequest(CameraDevice cameraDevice, SurfaceTexture texture) {
        try {
            CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(new Surface(texture));
            builder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
            builder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
            return builder.build();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
