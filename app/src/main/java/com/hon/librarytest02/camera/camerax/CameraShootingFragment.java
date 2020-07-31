package com.hon.librarytest02.camera.camerax;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.hon.librarytest02.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraShootingFragment extends Fragment {

    private static final String TAG = "CameraShootingFragment";
    private static final int OPEN_ALBUM_ACTIVITY_REQUEST = 11;
    private static final String KEY_LENS_FACING = "LENS_FACING";

    private PreviewView mPreviewView;
    private ImageView mSearchingImage;
    private ProgressBar mLoadingProgress;
    private ImageButton mCameraCaptureButton;
    private ImageButton mSelectGalleryButton;
    private ImageButton mCameraSwitchButton;

    private List<ImageUploader> mImageUploaderList = new ArrayList<>();
    private CameraXManager mCameraXManager;

    private boolean isFromGallery = false;

    private ImageUploader.Callback mUploadCallback = new ImageUploader.Callback() {
        @Override
        public void onRequest() {
            disableAllButtons();
            mLoadingProgress.setVisibility(View.VISIBLE);
        }

        @Override
        public void onResult(@Nullable String url) {
            enableAllButtons();
            mLoadingProgress.setVisibility(View.GONE);
            if (TextUtils.isEmpty(url)) {
                Toast.makeText(getContext(), "Error when searching", Toast.LENGTH_LONG).show();
                return;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera_shooting_page, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initCamera(savedInstanceState);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mCameraXManager.startCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 10);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            int grantResult = grantResults[i];
            if (Manifest.permission.CAMERA.equals(permission)) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    mCameraXManager.startCamera();
                } else if (grantResult == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getContext(), "Sorry, no camera permission", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case OPEN_ALBUM_ACTIVITY_REQUEST:
                Uri result = (intent == null || resultCode != Activity.RESULT_OK) ? null : intent.getData();
                if (result != null) {
                    try {
                        Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromContentResolver(getContext(), result);
                        showSearchingImage(bitmap);
                        isFromGallery = true;
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Error when getting image from gallery", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (ImageUploader imageUploader : mImageUploaderList) {
            imageUploader.cancel(true);
        }
        mImageUploaderList.clear();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_LENS_FACING, mCameraXManager.getCurrentLensFacing());
    }

    private void initView(View view) {
        mPreviewView = view.findViewById(R.id.pv_camera);
        mSearchingImage = view.findViewById(R.id.iv_searching);
        mLoadingProgress = view.findViewById(R.id.pb_loading);

        mCameraCaptureButton = view.findViewById(R.id.ib_camera_capture);
        mCameraCaptureButton.setOnClickListener(v -> takePhoto());
        mSelectGalleryButton = view.findViewById(R.id.ib_select_gallery);
        mSelectGalleryButton.setOnClickListener(v -> chooseImage());
        mCameraSwitchButton = view.findViewById(R.id.ib_switch_camera);
        mCameraSwitchButton.setOnClickListener(v -> switchCamera());
    }

    private void initCamera(Bundle savedInstanceState) {
        mCameraXManager = new CameraXManager(getActivity(), mPreviewView, new CameraXManager.OnImageCapturedListener() {
            @Override
            public void onCaptureSuccess(Bitmap bitmap) {
                if (bitmap != null) {
                    showSearchingImage(bitmap);
                    isFromGallery = false;
                }
            }

            @Override
            public void onError(ImageCaptureException exception) {
                //do nothing
            }
        });
        if (savedInstanceState != null) {
            int currentFacing = savedInstanceState.getInt(KEY_LENS_FACING, CameraSelector.LENS_FACING_BACK);
            mCameraXManager.setCurrentLensFacing(currentFacing);
        }
    }

    private void switchCamera() {
        hideSearchingImage();
        mCameraXManager.switchCamera();
    }

    private void takePhoto() {
        if (hideSearchingImage()) {
            return;
        }
        mCameraXManager.takePhoto();
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, OPEN_ALBUM_ACTIVITY_REQUEST);
    }

    private boolean hideSearchingImage() {
        if (mSearchingImage.getVisibility() == View.VISIBLE) {
            if (isFromGallery) {
                // some devices'(such as OPPO R11) camera will close and preview will turn black in the case where users select photo from gallery,
                // then take photo. So, restart camera here.
                mCameraXManager.startCamera();
            }
            mPreviewView.setVisibility(View.VISIBLE);
            mSearchingImage.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    private void showSearchingImage(Bitmap bitmap) {
        mPreviewView.setVisibility(View.GONE);
        mSearchingImage.setImageBitmap(bitmap);
        mSearchingImage.setVisibility(View.VISIBLE);
    }

    private void disableAllButtons() {
        mCameraCaptureButton.setEnabled(false);
        mCameraSwitchButton.setEnabled(false);
        mSelectGalleryButton.setEnabled(false);
    }

    private void enableAllButtons() {
        mCameraCaptureButton.setEnabled(true);
        mCameraSwitchButton.setEnabled(true);
        mSelectGalleryButton.setEnabled(true);
    }

    public static CameraShootingFragment newInstance() {
        return new CameraShootingFragment();
    }
}
