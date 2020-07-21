package com.hon.librarytest02.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.hon.librarytest02.R;
import com.hon.mylogger.MyLogger;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import static android.graphics.ImageFormat.JPEG;

/**
 * Created by Frank Hon on 2020/7/20 11:52 PM.
 * E-mail: frank_hon@foxmail.com
 */
@TargetApi(value = Build.VERSION_CODES.LOLLIPOP)
public class CameraActivity extends AppCompatActivity {

    private Button mCaptureButton;
    private ImageView mSearchingImage;
    private ImageButton mSwitchButton;

    private CameraManager mCameraManager;
    private Handler mBackgroundHandler;
    private String[] mCameraIds = new String[2];

    private TextureView mPreviewTexture;
    private Size mPreviewSize;
    private ImageReader mImageReader;

    private CameraDevice mFrontDevice;
    private CameraDevice mBackDevice;
    private CameraCharacteristics mFrontCharacteristics;
    private CameraCharacteristics mBackCharacteristics;
    private CameraCaptureSession mFrontCaptureSession;
    private CameraCaptureSession mBackCaptureSession;

    private DisplayOrientationDetector mDisplayOrientationDetector;
    private int mDisplayOrientation;

    // 0:Front, 1:Back
    private int mCurrentFacing = 0;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        close();
        if (mBackgroundHandler != null) {
            mBackgroundHandler.removeCallbacksAndMessages(null);
        }
    }

    private void close() {
        if (mCurrentFacing == 0) {
            if (mFrontDevice != null) {
                mFrontDevice.close();
            }
            if (mFrontCaptureSession != null) {
                mFrontCaptureSession.close();
            }
        } else {
            if (mBackDevice != null) {
                mBackDevice.close();
            }
            if (mBackCaptureSession != null) {
                mBackCaptureSession.close();
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
        mPreviewTexture = findViewById(R.id.tv_preview);
        mCaptureButton = findViewById(R.id.btn_capture);
        mSearchingImage = findViewById(R.id.iv_searching);
        mSwitchButton = findViewById(R.id.ib_switch_camera);

        mCaptureButton.setOnClickListener(v -> takePhoto());
        mSwitchButton.setOnClickListener(v -> switchCamera());
    }

    private void initCamera() {
        initBackgroundHandler();
        initOrientationDetector();
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
                        mBackCharacteristics = characteristics;
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

    private void initOrientationDetector() {
        mDisplayOrientationDetector = new DisplayOrientationDetector(this) {
            @Override
            public void onDisplayOrientationChanged(int displayOrientation) {
                mDisplayOrientation = displayOrientation;
            }
        };
        mDisplayOrientationDetector.enable(getWindowManager().getDefaultDisplay());
    }

    private void takePhoto() {
        try {
            CameraDevice cameraDevice = mCurrentFacing == 0 ? mFrontDevice : mBackDevice;
            CameraCharacteristics characteristics = mCurrentFacing == 0 ? mFrontCharacteristics : mBackCharacteristics;
            CameraCaptureSession captureSession = mCurrentFacing == 0 ? mFrontCaptureSession : mBackCaptureSession;
            CaptureRequest captureRequest = buildCaptureRequest(cameraDevice, mImageReader.getSurface(), characteristics);
            captureSession.capture(captureRequest, new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                    MyLogger.d("onCaptureStarted");
                }

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    MyLogger.d("onCaptureCompleted");
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void switchCamera() {
        close();
        if (mCurrentFacing == 0) {
            mCurrentFacing = 1;
        } else {
            mCurrentFacing = 0;
        }
        mPreviewTexture.setVisibility(View.VISIBLE);
        openDevice();
    }

    @SuppressLint("MissingPermission")
    private void openDevice() {
        try {
            String cameraId = mCameraIds[mCurrentFacing];
            if (!"".equals(cameraId)) {
                mCameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(@NonNull CameraDevice camera) {
                        if (mCurrentFacing == 0) {
                            mFrontDevice = camera;
                            createCaptureSessionWhenAvailable(mFrontDevice, mPreviewTexture, mFrontCharacteristics);
                        } else if (mCurrentFacing == 1) {
                            mBackDevice = camera;
                            createCaptureSessionWhenAvailable(mBackDevice, mPreviewTexture, mBackCharacteristics);
                        }
                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice camera) {
                        camera.close();
                    }

                    @Override
                    public void onError(@NonNull CameraDevice camera, int error) {
                        MyLogger.d("onError " + error);
                        camera.close();
                    }
                }, mBackgroundHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCaptureSessionWhenAvailable(CameraDevice cameraDevice, TextureView textureView, CameraCharacteristics characteristics) {
        if (cameraDevice != null) {
            if (textureView.isAvailable()) {
                createCaptureSession(cameraDevice, textureView.getSurfaceTexture(), characteristics);
            } else {
                textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                    @Override
                    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                        MyLogger.d(String.format("onSurfaceTextureAvailable: width=%d, height=%d", width, height));
                        mPreviewSize = new Size(width, height);
//                                            configureTransform(width, height);
                        createCaptureSession(cameraDevice, textureView.getSurfaceTexture(), characteristics);
                    }

                    @Override
                    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                        MyLogger.d(String.format("onSurfaceTextureSizeChanged: width=%d, height=%d", width, height));
                        surface.setDefaultBufferSize(width, height);
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

    // createCaptureSession
    private void createCaptureSession(CameraDevice cameraDevice, SurfaceTexture surfaceTexture, CameraCharacteristics characteristics) {
        try {
            //防止TextureView变形
            Size previewSize = chooseOptimalSize();
            mPreviewTexture.getSurfaceTexture().setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            cameraDevice.createCaptureSession(setOutputSize(characteristics, surfaceTexture),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            if (mCurrentFacing == 0) {
                                mFrontCaptureSession = session;
                            } else {
                                mBackCaptureSession = session;
                            }
                            try {
                                CaptureRequest previewRequest = buildPreviewRequest(cameraDevice, new Surface(surfaceTexture), characteristics);
                                if (previewRequest != null) {
                                    session.setRepeatingRequest(previewRequest, new CameraCaptureSession.CaptureCallback() {
                                        @Override
                                        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {

                                        }

                                        @Override
                                        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {

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
        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
        mImageReader = ImageReader.newInstance(pictureSize.getWidth(),
                pictureSize.getHeight(), JPEG, 1);
        mImageReader.setOnImageAvailableListener(reader -> {
            MyLogger.d("onImageAvailable");
            try (Image image = reader.acquireNextImage()) {
                Image.Plane[] planes = image.getPlanes();
                if (planes.length > 0) {
                    ByteBuffer buffer = planes[0].getBuffer();
                    byte[] data = new byte[buffer.remaining()];
                    buffer.get(data);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    mSearchingImage.post(() -> {
                        mSearchingImage.setVisibility(View.VISIBLE);
                        mPreviewTexture.setVisibility(View.GONE);
                        mSearchingImage.setImageBitmap(bitmap);
                    });
                }
            }
        }, mBackgroundHandler);
        // config surface
        Surface surface = new Surface(texture);
        return Arrays.asList(surface, mImageReader.getSurface());
    }

    // buildCaptureRequest
    private CaptureRequest buildPreviewRequest(CameraDevice cameraDevice, Surface surface, CameraCharacteristics characteristics) {
        try {
            CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(surface);

            builder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
            builder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
            builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            if (available != null && available) {
                builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            }

            return builder.build();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private CaptureRequest buildCaptureRequest(CameraDevice cameraDevice, Surface surface, CameraCharacteristics characteristics) {
        try {
            CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            builder.addTarget(surface);

            builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            if (available != null && available) {
                builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            }

            int sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            builder.set(CaptureRequest.JPEG_ORIENTATION, (sensorOrientation + mDisplayOrientation + 360) % 360);

            return builder.build();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * Chooses the optimal preview size.
     *
     * @return The picked size for camera preview.
     */
    private Size chooseOptimalSize() {
        int surfaceLonger, surfaceShorter;
        int surfaceWidth = mPreviewSize.getWidth();
        int surfaceHeight = mPreviewSize.getHeight();
        if (surfaceWidth < surfaceHeight) {
            surfaceLonger = surfaceHeight;
            surfaceShorter = surfaceWidth;
        } else {
            surfaceLonger = surfaceWidth;
            surfaceShorter = surfaceHeight;
        }
        return new Size(surfaceLonger, surfaceShorter);
    }
}
